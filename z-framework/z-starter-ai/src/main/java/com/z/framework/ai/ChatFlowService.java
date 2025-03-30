package com.z.framework.ai;

import com.z.framework.ai.model.chat.ChatMessage;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Map;

/**
 * @Title: ChatFlowService
 * @Package com/z/framework/ai/ChatFlowService.java
 * @Description:
 * 对接工作流chat
 * @author zhaozhiwei
 * @date 2025/3/30 14:46
 * @version V1.0
 */
public interface ChatFlowService extends ChatService{

    /**
     * 发送对话消息（流式模式）
     * 注：Agent模式下不允许blocking
     *
     * @param message  消息
     * @throws IOException IO异常
     */
    Flux<Map<String, Object>> sendChatFlowMessageStream(ChatMessage message) throws IOException;

}
