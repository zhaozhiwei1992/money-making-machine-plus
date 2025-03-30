package com.z.framework.ai.adapter.dify.service;

import com.alibaba.fastjson.JSON;
import com.z.framework.ai.AbstractAIService;
import com.z.framework.ai.model.AppConfig;
import com.z.framework.ai.model.chat.ChatMessage;
import com.z.framework.ai.model.chat.MessageListResponse;
import com.z.framework.ai.util.JsonUtils;
import io.github.imfangs.dify.client.enums.ResponseMode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Map;

//@Component
@ConditionalOnProperty(name = "z.module.ai.type", havingValue = "dify")
@Slf4j
@Data
public class DifyHttpServiceAdapter extends AbstractAIService {

    @Autowired
    private AppConfig appConfig;

    // 流式响应相关常量
    private static final String DONE_MARKER = "[DONE]";
    private static final String DATA_PREFIX = "data:";

    // API 路径常量
    // 对话型应用相关路径
    private static final String CHAT_MESSAGES_PATH = "/chat-messages";
    private static final String MESSAGES_PATH = "/messages";
    private static final String CONVERSATIONS_PATH = "/conversations";
    private static final String AUDIO_TO_TEXT_PATH = "/audio-to-text";
    private static final String TEXT_TO_AUDIO_PATH = "/text-to-audio";
    private static final String META_PATH = "/meta";
    private static final String STOP_PATH = "/stop";
    private static final String FEEDBACKS_PATH = "/feedbacks";
    private static final String SUGGESTED_QUESTIONS_PATH = "/suggested";
    private static final String NAME_PATH = "/name";

    // 文本生成型应用相关路径
    private static final String COMPLETION_MESSAGES_PATH = "/completion-messages";

    // 工作流应用相关路径
    private static final String WORKFLOWS_PATH = "/workflows";
    private static final String WORKFLOWS_RUN_PATH = "/workflows/run";
    private static final String WORKFLOWS_TASKS_PATH = "/workflows/tasks";
    private static final String WORKFLOWS_LOGS_PATH = "/workflows/logs";

    @Autowired
    private WebClient webClient;

    @Autowired
    private RestTemplate restTemplate;

    private Flux<Map<String, Object>> parseServerSentEvent(String data) {
        try {
            return Flux.just(JSON.parseObject(data, Map.class));
        } catch (Exception e) {
            return Flux.error(new RuntimeException("解析错误: " + data, e));
        }
    }

    @Override
    public Flux<Map<String, Object>> sendChatMessageStream(ChatMessage message) {
        message.setResponseMode(ResponseMode.STREAMING);
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("query", message.getQuery());
//        requestBody.put("response_mode", "streaming");
//        requestBody.put("conversation_id", message.getConversationId());
//        requestBody.put("user", "abc-123");
//        requestBody.put("inputs", new HashMap<>());
        String json = JsonUtils.toJson(message);
        String apiKey = message.getApiKey();
        String baseUrl = getAppConfig().getBaseUrl();
        return webClient.post().uri(baseUrl + CHAT_MESSAGES_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey).bodyValue(json).retrieve()
                .bodyToFlux(String.class).flatMap(this::parseServerSentEvent);

        // 测试输出
//        mapFlux.doOnSubscribe(sub -> System.out.println("Stream started"))
//                .doOnNext(event -> {
//                    System.out.println("--- Event Received ---");
//                    System.out.println("Event Type: " + event.get("event"));
//                    System.out.println("Answer: " + event.get("answer"));
//                    System.out.println("Metadata: " + event.get("metadata"));
//                })
//                .doOnComplete(() -> System.out.println("Stream ended successfully"))
//                .doOnError(e -> System.err.println("Error: " + e.getMessage()))
//                .subscribe();
//
//        // 5. 防止主线程退出（测试用）
//        try {
//            Thread.sleep(30_000); // 根据流持续时间调整
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public MessageListResponse getMessages(String conversationId, String user, String firstId, Integer limit, String apiKey) {
        // curl -X GET
        // 'http://10.10.115.9/v1/conversations?user=abc-123&last_id=&limit=20'\
        // --header 'Authorization: Bearer {api_key}'
        String baseUrl = getAppConfig().getBaseUrl();
        String url = baseUrl + CONVERSATIONS_PATH;
        // 这里参数需要按照认证提供用户给出
//        url += String.format("?user=%s&last_id=%s&limit=%s", user, firstId, limit);
        buildUrlWithParams(url, Map.of("user", user, "last_id", firstId, "limit", limit));
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + apiKey);

        final HttpEntity<List<Map<String, Object>>> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<MessageListResponse> exchange = restTemplate.getForEntity(url, MessageListResponse.class, httpEntity);
        return exchange.getBody();
    }
}
