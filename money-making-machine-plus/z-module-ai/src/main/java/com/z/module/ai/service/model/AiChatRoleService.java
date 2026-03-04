package com.z.module.ai.service.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.z.framework.common.enums.CommonStatusEnum;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.framework.common.util.collection.CollectionUtils;
import com.z.framework.common.util.object.BeanUtils;
import com.z.module.ai.domain.model.AiChatRoleDO;
import com.z.module.ai.repository.AiChatRoleRepository;
import com.z.module.ai.service.knowledge.AiKnowledgeService;
import com.z.module.ai.web.rest.admin.model.vo.chatRole.AiChatRoleSaveMyReqVO;
import com.z.module.ai.web.rest.admin.model.vo.chatRole.AiChatRoleSaveReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.z.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.z.module.ai.enums.ErrorCodeConstants.CHAT_ROLE_DISABLE;
import static com.z.module.ai.enums.ErrorCodeConstants.CHAT_ROLE_NOT_EXISTS;

/**
 * AI 聊天角色 Service 实现类
 *
 * @author fansili
 */
@Service
@Slf4j
public class AiChatRoleService {

    @Resource
    private AiChatRoleRepository chatRoleRepository;

    @Resource
    private AiKnowledgeService knowledgeService;
    @Resource
    private AiToolService toolService;

    public Long createChatRole(AiChatRoleSaveReqVO createReqVO) {
        // 校验文档
        validateDocuments(createReqVO.getKnowledgeIds());
        // 校验工具
        validateTools(createReqVO.getToolIds());

        // 保存角色
        AiChatRoleDO chatRole = BeanUtils.toBean(createReqVO, AiChatRoleDO.class);
        chatRoleRepository.save(chatRole);
        return chatRole.getId();
    }

    public Long createChatRoleMy(AiChatRoleSaveMyReqVO createReqVO, Long userId) {
        // 校验文档
        validateDocuments(createReqVO.getKnowledgeIds());
        // 校验工具
        validateTools(createReqVO.getToolIds());

        // 保存角色
        AiChatRoleDO chatRole = BeanUtils.toBean(createReqVO, AiChatRoleDO.class).setUserId(userId)
                .setStatus(CommonStatusEnum.ENABLE.getStatus()).setPublicStatus(false);
        chatRoleRepository.save(chatRole);
        return chatRole.getId();
    }

    public void updateChatRole(AiChatRoleSaveReqVO updateReqVO) {
        // 校验存在
        validateChatRoleExists(updateReqVO.getId());
        // 校验文档
        validateDocuments(updateReqVO.getKnowledgeIds());
        // 校验工具
        validateTools(updateReqVO.getToolIds());

        // 更新角色
        AiChatRoleDO updateObj = BeanUtils.toBean(updateReqVO, AiChatRoleDO.class);
        chatRoleRepository.save(updateObj);
    }

    public void updateChatRoleMy(AiChatRoleSaveMyReqVO updateReqVO, Long userId) {
        // 校验存在
        AiChatRoleDO chatRole = validateChatRoleExists(updateReqVO.getId());
        if (ObjectUtil.notEqual(chatRole.getUserId(), userId)) {
            throw exception(CHAT_ROLE_NOT_EXISTS);
        }
        // 校验文档
        validateDocuments(updateReqVO.getKnowledgeIds());
        // 校验工具
        validateTools(updateReqVO.getToolIds());

        // 更新
        AiChatRoleDO updateObj = BeanUtils.toBean(updateReqVO, AiChatRoleDO.class);
        chatRoleRepository.save(updateObj);
    }

    /**
     * 校验知识库是否存在
     *
     * @param knowledgeIds 知识库编号列表
     */
    private void validateDocuments(List<Long> knowledgeIds) {
        if (CollUtil.isEmpty(knowledgeIds)) {
            return;
        }
        // 校验文档是否存在
        knowledgeIds.forEach(knowledgeService::validateKnowledgeExists);
    }

    /**
     * 校验工具是否存在
     *
     * @param toolIds 工具编号列表
     */
    private void validateTools(List<Long> toolIds) {
        if (CollUtil.isEmpty(toolIds)) {
            return;
        }
        // 遍历校验每个工具是否存在
        toolIds.forEach(toolService::validateToolExists);
    }

    public void deleteChatRole(Long id) {
        // 校验存在
        validateChatRoleExists(id);
        // 删除
        chatRoleRepository.deleteById(id);
    }

    public void deleteChatRoleMy(Long id, Long userId) {
        // 校验存在
        AiChatRoleDO chatRole = validateChatRoleExists(id);
        if (ObjectUtil.notEqual(chatRole.getUserId(), userId)) {
            throw exception(CHAT_ROLE_NOT_EXISTS);
        }
        // 删除
        chatRoleRepository.deleteById(id);
    }

    private AiChatRoleDO validateChatRoleExists(Long id) {
        AiChatRoleDO chatRole = chatRoleRepository.findById(id).orElse(null);
        if (chatRole == null) {
            throw exception(CHAT_ROLE_NOT_EXISTS);
        }
        return chatRole;
    }

    public AiChatRoleDO getChatRole(Long id) {
        return chatRoleRepository.findById(id).orElse(null);
    }

    public List<AiChatRoleDO> getChatRoleList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return chatRoleRepository.findAllById(ids);
    }

    public AiChatRoleDO validateChatRole(Long id) {
        AiChatRoleDO chatRole = validateChatRoleExists(id);
        if (CommonStatusEnum.isDisable(chatRole.getStatus())) {
            throw exception(CHAT_ROLE_DISABLE, chatRole.getName());
        }
        return chatRole;
    }

    public PageResult<AiChatRoleDO> getChatRolePage(Pageable pageable, AiChatRoleDO query) {
        log.debug("REST request to get all AiChatRole for page {}", pageable);

        // 根据id降序
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<AiChatRoleDO> page;
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true)
                .withIgnorePaths("id", "createdDate", "lastModifiedDate");

        Example<AiChatRoleDO> ex = Example.of(query, matcher);
        page = chatRoleRepository.findAll(ex, pageable);

        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    public PageResult<AiChatRoleDO> getChatRoleMyPage(Pageable pageable, AiChatRoleDO query, Long userId) {
        log.debug("REST request to get all AiChatRole for user {} page {}", userId, pageable);

        // 设置userId过滤条件
        query.setUserId(userId);

        // 根据id降序
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<AiChatRoleDO> page;
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true)
                .withIgnorePaths("id", "createdDate", "lastModifiedDate");

        Example<AiChatRoleDO> ex = Example.of(query, matcher);
        page = chatRoleRepository.findAll(ex, pageable);

        return new PageResult<>(page.getContent(), page.getTotalElements());
    }

    public List<String> getChatRoleCategoryList() {
        // TODO: Need to implement selectListGroupByCategory in Repository
        List<AiChatRoleDO> list = chatRoleRepository.selectListGroupByCategory(CommonStatusEnum.ENABLE.getStatus());
        return CollectionUtils.convertList(list, AiChatRoleDO::getCategory,
                role -> role != null && StrUtil.isNotBlank(role.getCategory()));
    }

    public List<AiChatRoleDO> getChatRoleListByName(String name) {
        // TODO: Need to implement selectListByName in Repository
        return chatRoleRepository.findAllByName(name);
    }

}
