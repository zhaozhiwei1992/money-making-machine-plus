package com.z.module.ai.web.rest.admin.chat;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.z.framework.common.util.object.BeanUtils;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.ai.domain.chat.AiChatConversationDO;
import com.z.module.ai.service.chat.AiChatConversationService;
import com.z.module.ai.service.chat.AiChatMessageService;
import com.z.module.ai.web.rest.admin.chat.vo.conversation.AiChatConversationCreateMyReqVO;
import com.z.module.ai.web.rest.admin.chat.vo.conversation.AiChatConversationPageReqVO;
import com.z.module.ai.web.rest.admin.chat.vo.conversation.AiChatConversationRespVO;
import com.z.module.ai.web.rest.admin.chat.vo.conversation.AiChatConversationUpdateMyReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.z.framework.common.util.collection.CollectionUtils.convertList;
import static com.z.framework.security.util.SecurityUtils.getUserId;

@Tag(name = "管理后台 - AI 聊天对话")
@RestController
@RequestMapping("/ai/chat/conversation")
@Validated
public class AiChatConversationController {

    @Resource
    private AiChatConversationService chatConversationService;
    @Resource
    private AiChatMessageService chatMessageService;

    @PostMapping("/create-my")
    @Operation(summary = "创建【我的】聊天对话")
    public Long createChatConversationMy(@RequestBody @Valid AiChatConversationCreateMyReqVO createReqVO) {
        return chatConversationService.createChatConversationMy(createReqVO, getUserId());
    }

    @PutMapping("/update-my")
    @Operation(summary = "更新【我的】聊天对话")
    public Boolean updateChatConversationMy(@RequestBody @Valid AiChatConversationUpdateMyReqVO updateReqVO) {
        chatConversationService.updateChatConversationMy(updateReqVO, getUserId());
        return true;
    }

    @GetMapping("/my-list")
    @Operation(summary = "获得【我的】聊天对话列表")
    public List<AiChatConversationRespVO> getChatConversationMyList() {
        List<AiChatConversationDO> list = chatConversationService.getChatConversationListByUserId(getUserId());
        return BeanUtils.toBean(list, AiChatConversationRespVO.class);
    }

    @GetMapping("/get-my")
    @Operation(summary = "获得【我的】聊天对话")
    @Parameter(name = "id", required = true, description = "对话编号", example = "1024")
    public AiChatConversationRespVO getChatConversationMy(@RequestParam("id") Long id) {
        AiChatConversationDO conversation = chatConversationService.getChatConversation(id);
        if (conversation != null && ObjUtil.notEqual(conversation.getUserId(), getUserId())) {
            conversation = null;
        }
        return BeanUtils.toBean(conversation, AiChatConversationRespVO.class);
    }

    @DeleteMapping("/delete-my")
    @Operation(summary = "删除聊天对话")
    @Parameter(name = "id", required = true, description = "对话编号", example = "1024")
    public Boolean deleteChatConversationMy(@RequestParam("id") Long id) {
        chatConversationService.deleteChatConversationMy(id, getUserId());
        return true;
    }

    @DeleteMapping("/delete-by-unpinned")
    @Operation(summary = "删除未置顶的聊天对话")
    public Boolean deleteChatConversationMyByUnpinned() {
        chatConversationService.deleteChatConversationMyByUnpinned(getUserId());
        return true;
    }

    // ========== 对话管理 ==========

    @GetMapping("/page")
    @Operation(summary = "获得对话分页", description = "用于【对话管理】菜单")
    public PageResult<AiChatConversationRespVO> getChatConversationPage(Pageable pageable, AiChatConversationDO pageReqVO) {
        PageResult<AiChatConversationDO> pageResult = chatConversationService.getChatConversationPage(pageable, pageReqVO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty();
        }
        // 拼接关联数据
        Map<Long, Integer> messageCountMap = chatMessageService.getChatMessageCountMap(
                convertList(pageResult.getList(), AiChatConversationDO::getId));
        return BeanUtils.toBean(pageResult, AiChatConversationRespVO.class,
                conversation -> conversation.setMessageCount(messageCountMap.getOrDefault(conversation.getId(), 0)));
    }

    @Operation(summary = "管理员删除对话")
    @DeleteMapping("/delete-by-admin")
    @Parameter(name = "id", required = true, description = "对话编号", example = "1024")
    public Boolean deleteChatConversationByAdmin(@RequestParam("id") Long id) {
        chatConversationService.deleteChatConversationByAdmin(id);
        return true;
    }

}
