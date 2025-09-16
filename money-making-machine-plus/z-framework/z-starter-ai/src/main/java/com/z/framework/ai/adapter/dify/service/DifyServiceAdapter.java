package com.z.framework.ai.adapter.dify.service;

import com.z.framework.ai.AbstractAIService;
import com.z.framework.ai.model.AppConfig;
import com.z.framework.ai.model.chat.ChatMessage;
import com.z.framework.ai.model.chat.MessageListResponse;
import com.z.framework.ai.util.JsonUtils;
import io.github.imfangs.dify.client.DifyChatClient;
import io.github.imfangs.dify.client.DifyChatflowClient;
import io.github.imfangs.dify.client.DifyClientFactory;
import io.github.imfangs.dify.client.callback.ChatStreamCallback;
import io.github.imfangs.dify.client.callback.ChatflowStreamCallback;
import io.github.imfangs.dify.client.event.ErrorEvent;
import io.github.imfangs.dify.client.event.MessageEndEvent;
import io.github.imfangs.dify.client.event.MessageEvent;
import io.github.imfangs.dify.client.event.NodeFinishedEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "z.module.ai.type", havingValue = "dify")
@Slf4j
@Data
public class DifyServiceAdapter extends AbstractAIService {

    @Autowired
    private AppConfig appConfig;

    @Override
    public Flux<Map<String, Object>> sendChatFlowMessageStream(ChatMessage msg) {
        // 创建聊天消息
        io.github.imfangs.dify.client.model.chat.ChatMessage message = io.github.imfangs.dify.client.model.chat.ChatMessage.builder()
                .query(msg.getQuery())
                .user(msg.getUser())
                .responseMode(io.github.imfangs.dify.client.enums.ResponseMode.STREAMING)
                .build();
        return Flux.create((FluxSink<Map<String, Object>> emitter) -> {
                    // 创建客户端实例
                    DifyChatflowClient chatClient = DifyClientFactory.createChatWorkflowClient(getAppConfig().getBaseUrl(), msg.getApiKey());

                    // 注册回调
                    ChatflowStreamCallback callback = new ChatflowStreamCallback() {
                        @Override
                        public void onMessage(MessageEvent event) {
                            log.debug("收到消息片段: {}", event);
                            emitter.next(JsonUtils.jsonToMap(JsonUtils.toJson(event)));
                        }

                        @Override
                        public void onNodeFinished(NodeFinishedEvent event) {
                            log.debug("收到node结束片段: {}", event);
                            emitter.next(JsonUtils.jsonToMap(JsonUtils.toJson(event)));
                        }

                        @Override
                        public void onMessageEnd(MessageEndEvent event) {
                            log.debug("消息结束: {}", event);
                            emitter.next(JsonUtils.jsonToMap(JsonUtils.toJson(event)));
                            emitter.complete();
                        }

                        @Override
                        public void onError(ErrorEvent event) {
                            log.error("错误: {}", event.getMessage());
                            emitter.next(JsonUtils.jsonToMap(JsonUtils.toJson(event)));
                            emitter.complete();
                        }

                        @Override
                        public void onException(Throwable throwable) {
                            log.error("异常: ", throwable);
                            emitter.error(throwable);
                        }
                    };

                    // 发起流式请求
                    try {
                        chatClient.sendChatMessageStream(message, callback);
                    } catch (IOException e) {
                        emitter.error(new RuntimeException("连接失败", e));
                    }

                    // 取消订阅时清理资源
                    emitter.onCancel(() -> closeClient(chatClient));
                    emitter.onDispose(() -> closeClient(chatClient));
                })
                .subscribeOn(Schedulers.boundedElastic()) // 指定异步线程
                .timeout(Duration.ofSeconds(30))         // 超时控制
                .onErrorResume(this::handleError);
    }

    @Override
    public Flux<Map<String, Object>> sendChatMessageStream(ChatMessage msg) {
        // 创建聊天消息
        io.github.imfangs.dify.client.model.chat.ChatMessage message = io.github.imfangs.dify.client.model.chat.ChatMessage.builder()
                .query(msg.getQuery())
                .user(msg.getUser())
                .responseMode(io.github.imfangs.dify.client.enums.ResponseMode.STREAMING)
                .build();
        return Flux.create((FluxSink<Map<String, Object>> emitter) -> {
                    // 创建客户端实例
                    DifyChatClient chatClient = DifyClientFactory.createChatClient(getAppConfig().getBaseUrl(), msg.getApiKey());

                    // 注册回调
                    ChatStreamCallback callback = new ChatStreamCallback() {
                        @Override
                        public void onMessage(MessageEvent event) {
                            log.debug("收到消息片段: {}", event);
                            emitter.next(JsonUtils.jsonToMap(JsonUtils.toJson(event)));
                        }

                        @Override
                        public void onMessageEnd(MessageEndEvent event) {
                            System.out.println("消息结束，完整消息ID: " + event);
                            emitter.next(JsonUtils.jsonToMap(JsonUtils.toJson(event)));
                            emitter.complete();
                        }

                        @Override
                        public void onError(ErrorEvent event) {
                            System.err.println("错误: " + event.getMessage());
                            emitter.next(JsonUtils.jsonToMap(JsonUtils.toJson(event)));
                            emitter.complete();
                        }

                        @Override
                        public void onException(Throwable throwable) {
                            System.err.println("异常: " + throwable.getMessage());
                            emitter.error(throwable);
                        }
                    };

                    // 发起流式请求
                    try {
                        chatClient.sendChatMessageStream(message, callback);
                    } catch (IOException e) {
                        emitter.error(new RuntimeException("连接失败", e));
                    }

                    // 取消订阅时清理资源
                    emitter.onCancel(() -> closeClient(chatClient));
                    emitter.onDispose(() -> closeClient(chatClient));
                })
                .subscribeOn(Schedulers.boundedElastic()) // 指定异步线程
                .timeout(Duration.ofSeconds(30))         // 超时控制
                .onErrorResume(this::handleError);
    }

    // 资源清理方法
    private void closeClient(DifyChatClient client) {
        try {
            if (client != null) {
                client.close();
            }
        } catch (Exception e) {
            log.error("关闭客户端失败", e);
        }
    }

    // 错误统一处理
    private Flux<Map<String, Object>> handleError(Throwable throwable) {
        return Flux.just(Map.of(
                "type", "system_error",
                "message", throwable.getMessage(),
                "is_end", true
        ));
    }

    @Override
    public MessageListResponse getMessages(String conversationId, String user, String firstId, Integer limit, String apiKey) {

        DifyChatClient chatClient = DifyClientFactory.createChatClient(getAppConfig().getBaseUrl(), apiKey);
        try {
            io.github.imfangs.dify.client.model.chat.MessageListResponse messages = chatClient.getMessages(conversationId, user, firstId, limit);
            MessageListResponse messageListResponse = new MessageListResponse();
            BeanUtils.copyProperties(messages, messageListResponse);
            return messageListResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
