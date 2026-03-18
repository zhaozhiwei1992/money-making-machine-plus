package com.z.module.ai.web.rest.admin.chat;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.framework.common.web.rest.vm.R;
import com.z.module.ai.domain.chat.AiChatConversationDO;
import com.z.module.ai.domain.chat.AiChatMessageDO;
import com.z.module.ai.service.chat.AiChatConversationService;
import com.z.module.ai.service.chat.AiChatMessageService;
import com.z.module.ai.service.knowledge.AiKnowledgeDocumentService;
import com.z.module.ai.service.knowledge.AiKnowledgeSegmentService;
import com.z.module.ai.service.model.AiChatRoleService;
import com.z.module.ai.web.rest.admin.chat.vo.message.AiChatMessageRespVO;
import com.z.module.ai.web.rest.admin.chat.vo.message.AiChatMessageSendReqVO;
import com.z.module.ai.web.rest.admin.chat.vo.message.AiChatMessageSendRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;

import static com.z.framework.security.util.SecurityUtils.getUserId;

@Tag(name = "管理后台 - 聊天消息")
@RestController
@RequestMapping("/ai/chat/message")
@Slf4j
public class AiChatMessageController {

    @Resource
    private AiChatMessageService chatMessageService;
    @Resource
    private AiChatConversationService chatConversationService;
    @Resource
    private AiChatRoleService chatRoleService;
    @Resource
    private AiKnowledgeSegmentService knowledgeSegmentService;
    @Resource
    private AiKnowledgeDocumentService knowledgeDocumentService;

    @Operation(summary = "发送消息（段式）", description = "一次性返回，响应较慢")
    @PostMapping("/send")
    public AiChatMessageSendRespVO sendMessage(@Valid @RequestBody AiChatMessageSendReqVO sendReqVO) {
        return chatMessageService.sendMessage(sendReqVO, getUserId());
    }

    @Operation(summary = "发送消息（流式）", description = "流式返回，响应较快")
    @PostMapping(value = "/send-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<R<AiChatMessageSendRespVO>> sendChatMessageStream(@Valid @RequestBody AiChatMessageSendReqVO sendReqVO) {
        return chatMessageService.sendChatMessageStream(sendReqVO, getUserId());
    }

    @Operation(summary = "获得指定对话的消息列表")
    @GetMapping("/list-by-conversation-id")
    @Parameter(name = "conversationId", required = true, description = "对话编号", example = "1024")
    public List<AiChatMessageRespVO> getChatMessageListByConversationId(
            @RequestParam("conversationId") Long conversationId) {
        AiChatConversationDO conversation = chatConversationService.getChatConversation(conversationId);
        if (conversation == null || ObjUtil.notEqual(conversation.getUserId(), getUserId())) {
            return Collections.emptyList();
        }
        // 1. 获取消息列表
        List<AiChatMessageDO> messageList = chatMessageService.getChatMessageListByConversationId(conversationId);
        if (CollUtil.isEmpty(messageList)) {
            return Collections.emptyList();
        }

        // 2. 拼接数据，主要是知识库段落信息
//        Map<Long, AiKnowledgeSegmentDO> segmentMap = knowledgeSegmentService.getKnowledgeSegmentMap(convertListByFlatMap(messageList,
//                message -> CollUtil.isEmpty(message.getSegmentIds()) ? null : message.getSegmentIds().stream()));
//        Map<Long, AiKnowledgeDocumentDO> documentMap = knowledgeDocumentService.getKnowledgeDocumentMap(
//                convertList(segmentMap.values(), AiKnowledgeSegmentDO::getDocumentId));
//        List<AiChatMessageRespVO> messageVOList = BeanUtils.toBean(messageList, AiChatMessageRespVO.class);
//        for (int i = 0; i < messageList.size(); i++) {
//            AiChatMessageDO message = messageList.get(i);
//            if (CollUtil.isEmpty(message.getSegmentIds())) {
//                continue;
//            }
//            // 设置知识库段落信息
//            messageVOList.get(i).setSegments(convertList(message.getSegmentIds(), segmentId -> {
//                AiKnowledgeSegmentDO segment = segmentMap.get(segmentId);
//                if (segment == null) {
//                    return null;
//                }
//                AiKnowledgeDocumentDO document = documentMap.get(segment.getDocumentId());
//                if (document == null) {
//                    return null;
//                }
//                return new AiChatMessageRespVO.KnowledgeSegment().setId(segment.getId()).setContent(segment.getContent())
//                        .setDocumentId(segment.getDocumentId()).setDocumentName(document.getName());
//            }));
//        }
//        return messageVOList;
        return null;
    }

    @Operation(summary = "删除消息")
    @DeleteMapping("/delete")
    @Parameter(name = "id", required = true, description = "消息编号", example = "1024")
    public Boolean deleteChatMessage(@RequestParam("id") Long id) {
        chatMessageService.deleteChatMessage(id, getUserId());
        return true;
    }

    @Operation(summary = "删除指定对话的消息")
    @DeleteMapping("/delete-by-conversation-id")
    @Parameter(name = "conversationId", required = true, description = "对话编号", example = "1024")
    public Boolean deleteChatMessageByConversationId(@RequestParam("conversationId") Long conversationId) {
        chatMessageService.deleteChatMessageByConversationId(conversationId, getUserId());
        return true;
    }

    // ========== 对话管理 ==========

    @GetMapping("/page")
    @Operation(summary = "获得消息分页", description = "用于【对话管理】菜单")
    public PageResult<AiChatMessageRespVO> getChatMessagePage(Pageable pageable, AiChatMessageDO aiChatMessageDO) {
        PageResult<AiChatMessageDO> pageResult = chatMessageService.getChatMessagePage(pageable, aiChatMessageDO);
        if (CollUtil.isEmpty(pageResult.getList())) {
            return PageResult.empty();
        }
        // 拼接数据
//        Map<Long, AiChatRoleDO> roleMap = chatRoleService.getChatRoleMap(
//                convertSet(pageResult.getList(), AiChatMessageDO::getRoleId));
//        return BeanUtils.toBean(pageResult, AiChatMessageRespVO.class,
//                respVO -> MapUtils.findAndThen(roleMap, respVO.getRoleId(),
//                        role -> respVO.setRoleName(role.getName())));
        return null;
    }

    @Operation(summary = "管理员删除消息")
    @DeleteMapping("/delete-by-admin")
    @Parameter(name = "id", required = true, description = "消息编号", example = "1024")
    public Boolean deleteChatMessageByAdmin(@RequestParam("id") Long id) {
        chatMessageService.deleteChatMessageByAdmin(id);
        return true;
    }

}
