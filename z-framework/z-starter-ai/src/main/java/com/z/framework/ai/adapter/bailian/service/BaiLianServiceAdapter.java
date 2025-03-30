package com.z.framework.ai.adapter.bailian.service;

import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.z.framework.ai.AbstractAIService;
import com.z.framework.ai.model.AppConfig;
import com.z.framework.ai.model.chat.ChatMessage;
import com.z.framework.ai.model.chat.MessageListResponse;
import io.reactivex.Flowable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.*;

@Component
@ConditionalOnProperty(name = "z.module.ai.type", havingValue = "bailian")
@Slf4j
@Data
public class BaiLianServiceAdapter extends AbstractAIService {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private WebClient webClient;

    @Autowired
    private RestTemplate restTemplate;

    private Map<String, Object> convertToStandardFormat(ApplicationResult result) {
        Map<String, Object> response = new LinkedHashMap<>();

        // 基础字段
        response.put("event", "message");
        response.put("answer", result.getOutput().getText());
        response.put("timestamp", System.currentTimeMillis());

        // 元数据
        Map<String, Object> meta = new HashMap<>();
        meta.put("token_usage", result.getUsage());
        response.put("metadata", meta);

        return response;
    }

    private Flux<Map<String, Object>> errorToMap(Throwable e) {
        return Flux.just(Map.of(
                "error_type", e.getClass().getSimpleName(),
                "error_message", e.getMessage(),
                "is_end", true
        ));
    }

    @Override
    public Flux<Map<String, Object>> sendChatMessageStream(ChatMessage message) {
        return Flux.create((FluxSink<Map<String, Object>> emitter) -> {
            try {
                // 构建SDK参数
                ApplicationParam param = ApplicationParam.builder()
                        .apiKey(message.getApiKey())
                        .appId(message.getAppId())
                        .prompt(message.getQuery())
                        .incrementalOutput(true)
                        .build();

                // 获取SDK流
                Flowable<ApplicationResult> sdkStream = new Application().streamCall(param);

                // 转换RxJava流到Reactor，并结构化处理
                sdkStream.subscribe(
                        result -> {
                            try {
                                Map<String, Object> structuredData = convertToStandardFormat(result);
                                emitter.next(structuredData);
                            } catch (Exception e) {
                                emitter.error(new RuntimeException("数据转换失败", e));
                            }
                        },
                        error -> emitter.error(new RuntimeException(error)),
                        emitter::complete
                );

            } catch (NoApiKeyException | InputRequiredException e) {
                emitter.error(new RuntimeException("认证参数异常", e));
            } catch (Exception e) {
                emitter.error(new RuntimeException("系统错误", e));
            }
        })
        .subscribeOn(Schedulers.boundedElastic()) // 线程隔离
        .timeout(Duration.ofSeconds(30))         // 全局超时
        .onErrorResume(this::errorToMap);  // 错误转Map
    }

    @Override
    public MessageListResponse getMessages(String conversationId, String user, String firstId, Integer limit, String apiKey) {
        // curl -X GET
        // 'http://10.10.115.9/v1/conversations?user=abc-123&last_id=&limit=20'\
        // --header 'Authorization: Bearer {api_key}'
        String baseUrl = getAppConfig().getBaseUrl();
        String url = baseUrl;
//                + CONVERSATIONS_PATH;
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
