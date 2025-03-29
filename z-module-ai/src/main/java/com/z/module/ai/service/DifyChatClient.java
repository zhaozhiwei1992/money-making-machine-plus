package com.z.module.ai.service;

import com.z.module.ai.config.AIModuleConfiguration;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @Title: DifyChatClient
 * @Package com/z/module/ai/service/DifyChatClient.java
 * @Description:  通过dify中api跟模型交互, 个人知识管家
 * @author zhaozhiwei
 * @date 2025/2/10 17:42
 * @version V1.0
 */
public class DifyChatClient {

    private final RestTemplate restTemplate;

    public DifyChatClient() {
        this.restTemplate = new RestTemplate();
    }

    public String callWithMessage(String content) {
        String url = AIModuleConfiguration.DIFY_BASE_URL + "/chat-messages";
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + AIModuleConfiguration.DIFY_API_KEY);

        // 使用 Map 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("inputs", Collections.emptyMap()); // 空的 inputs
        requestBody.put("query", content);
        requestBody.put("response_mode", "blocking");
        requestBody.put("conversation_id", "");
        requestBody.put("user", "abc-123");

        // 构建 files 数组
        List<Map<String, String>> files = new ArrayList<>();
        Map<String, String> file = new HashMap<>();
        file.put("type", "image");
        file.put("transfer_method", "remote_url");
        file.put("url", "https://cloud.dify.ai/logo/logo-site.png");
        files.add(file);

        requestBody.put("files", files);

        // 创建 HttpEntity
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // 发送 POST 请求
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        // 输出响应
        System.out.println("Response Status: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());
        return response.getBody();

    }
}
