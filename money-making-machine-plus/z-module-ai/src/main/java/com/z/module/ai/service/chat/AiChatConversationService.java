package com.z.module.ai.service.chat;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.z.module.ai.domain.chat.AiChatConversationDO;
import com.z.module.ai.domain.model.AiChatRoleDO;
import com.z.module.ai.domain.model.AiModelDO;
import com.z.module.ai.enums.model.AiModelTypeEnum;
import com.z.module.ai.service.model.AiChatRoleService;
import com.z.module.ai.service.model.AiModelService;
import com.z.module.ai.service.knowledge.AiKnowledgeService;
import com.z.module.ai.web.rest.admin.chat.vo.conversation.AiChatConversationCreateMyReqVO;
import com.z.module.ai.web.rest.admin.chat.vo.conversation.AiChatConversationUpdateMyReqVO;
import com.z.module.ai.web.rest.admin.chat.vo.conversation.AiChatConversationPageReqVO;
import com.z.module.ai.repository.AiChatConversationRepository;
import com.z.framework.common.util.collection.CollectionUtils;
import com.z.framework.common.util.object.BeanUtils;
import com.z.framework.common.web.rest.vm.PageResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.z.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.z.module.ai.enums.ErrorCodeConstants.CHAT_CONVERSATION_MODEL_ERROR;
import static com.z.module.ai.enums.ErrorCodeConstants.CHAT_CONVERSATION_NOT_EXISTS;

/**
 * AI 聊天对话 Service 实现类
 *
 * @author fansili
 */
@Service
@Validated
@Slf4j
public class AiChatConversationService {

    @Resource
    private AiChatConversationRepository chatConversationRepository;

    @Resource
    private AiModelService modalService;
    @Resource
    private AiChatRoleService chatRoleService;
    @Resource
    private AiKnowledgeService knowledgeService;

    public Long createChatConversationMy(AiChatConversationCreateMyReqVO createReqVO, Long userId) {
        // 1.1 获得 AiChatRoleDO 聊天角色
        AiChatRoleDO role = createReqVO.getRoleId() != null ? chatRoleService.validateChatRole(createReqVO.getRoleId()) : null;
        // 1.2 获得 AiModelDO 聊天模型
        AiModelDO model = role != null && role.getModelId() != null ? modalService.validateModel(role.getModelId())
                : modalService.getRequiredDefaultModel(AiModelTypeEnum.CHAT.getType());
        Assert.notNull(model, "必须找到默认模型");
        validateChatModel(model);

        // 1.3 校验知识库
        if (Objects.nonNull(createReqVO.getKnowledgeId())) {
            knowledgeService.validateKnowledgeExists(createReqVO.getKnowledgeId());
        }

        // 2. 创建 AiChatConversationDO 聊天对话
        AiChatConversationDO conversation = new AiChatConversationDO().setUserId(userId).setPinned(false)
                .setModelId(model.getId()).setModel(model.getModel())
                .setTemperature(model.getTemperature()).setMaxTokens(model.getMaxTokens()).setMaxContexts(model.getMaxContexts());
        if (role != null) {
            conversation.setTitle(role.getName()).setRoleId(role.getId()).setSystemMessage(role.getSystemMessage());
        } else {
            conversation.setTitle(AiChatConversationDO.TITLE_DEFAULT);
        }
        chatConversationRepository.save(conversation);
        return conversation.getId();
    }

    public void updateChatConversationMy(AiChatConversationUpdateMyReqVO updateReqVO, Long userId) {
        // 1.1 校验对话是否存在
        AiChatConversationDO conversation = validateChatConversationExists(updateReqVO.getId());
        if (ObjUtil.notEqual(conversation.getUserId(), userId)) {
            throw exception(CHAT_CONVERSATION_NOT_EXISTS);
        }
        // 1.2 校验模型是否存在（修改模型的情况）
        AiModelDO model = null;
        if (updateReqVO.getModelId() != null) {
            model = modalService.validateModel(updateReqVO.getModelId());
        }

        // 1.3 校验知识库是否存在
        if (updateReqVO.getKnowledgeId() != null) {
            knowledgeService.validateKnowledgeExists(updateReqVO.getKnowledgeId());
        }

        // 2. 更新对话信息
        AiChatConversationDO updateObj = BeanUtils.toBean(updateReqVO, AiChatConversationDO.class);
        if (Boolean.TRUE.equals(updateReqVO.getPinned())) {
            updateObj.setPinnedTime(LocalDateTime.now());
        }
        if (model != null) {
            updateObj.setModel(model.getModel());
        }
        chatConversationRepository.save(updateObj);
    }

    public List<AiChatConversationDO> getChatConversationListByUserId(Long userId) {
        return chatConversationRepository.findByUserId(userId);
    }

    public AiChatConversationDO getChatConversation(Long id) {
        return chatConversationRepository.findById(id).orElse(null);
    }

    public void deleteChatConversationMy(Long id, Long userId) {
        // 1. 校验对话是否存在
        AiChatConversationDO conversation = validateChatConversationExists(id);
        if (conversation == null || ObjUtil.notEqual(conversation.getUserId(), userId)) {
            throw exception(CHAT_CONVERSATION_NOT_EXISTS);
        }
        // 2. 执行删除
        chatConversationRepository.deleteById(id);
    }

    public void deleteChatConversationByAdmin(Long id) {
        // 1. 校验对话是否存在
        AiChatConversationDO conversation = validateChatConversationExists(id);
        if (conversation == null) {
            throw exception(CHAT_CONVERSATION_NOT_EXISTS);
        }
        // 2. 执行删除
        chatConversationRepository.deleteById(id);
    }

    private void validateChatModel(AiModelDO model) {
        if (ObjectUtil.isAllNotEmpty(model.getTemperature(), model.getMaxTokens(), model.getMaxContexts())) {
            return;
        }
        Assert.equals(model.getType(), AiModelTypeEnum.CHAT.getType(), "模型类型不正确：" + model);
        throw exception(CHAT_CONVERSATION_MODEL_ERROR);
    }

    public AiChatConversationDO validateChatConversationExists(Long id) {
        AiChatConversationDO conversation = chatConversationRepository.findById(id).orElse(null);
        if (conversation == null) {
            throw exception(CHAT_CONVERSATION_NOT_EXISTS);
        }
        return conversation;
    }

    public void deleteChatConversationMyByUnpinned(Long userId) {
        List<AiChatConversationDO> list = chatConversationRepository.findByUserIdAndPinned(userId, false);
        if (CollUtil.isEmpty(list)) {
            return;
        }
        chatConversationRepository.deleteAllById(CollectionUtils.convertList(list, AiChatConversationDO::getId));
    }

    public PageResult<AiChatConversationDO> getChatConversationPage(Pageable pageable, AiChatConversationDO query) {
        log.debug("REST request to get all AiChatConversation for page {}", pageable);

        // 根据id降序
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<AiChatConversationDO> page;
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true)
                .withIgnorePaths("id", "createdDate", "lastModifiedDate");

        Example<AiChatConversationDO> ex = Example.of(query, matcher);
        page = chatConversationRepository.findAll(ex, pageable);

        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

}
