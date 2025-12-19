
package com.z.module.ai.service;

import com.z.module.ai.config.AIModuleConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class DifyChatClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DifyChatClient difyChatClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCallWithMessage() {
        String content = "test message";
        String expectedResponse = "test response";

        AIModuleConfiguration.DIFY_BASE_URL = "http://localhost";
        AIModuleConfiguration.DIFY_API_KEY = "test_key";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + AIModuleConfiguration.DIFY_API_KEY);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("inputs", Collections.emptyMap());
        requestBody.put("query", content);
        requestBody.put("response_mode", "blocking");
        requestBody.put("conversation_id", "");
        requestBody.put("user", "abc-123");
        requestBody.put("files", Collections.singletonList(
            Map.of(
                "type", "image",
                "transfer_method", "remote_url",
                "url", "https://cloud.dify.ai/logo/logo-site.png"
            )
        ));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(
            eq(AIModuleConfiguration.DIFY_BASE_URL + "/chat-messages"),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(String.class)
        )).thenReturn(responseEntity);

        String actualResponse = difyChatClient.callWithMessage(content);

        assertEquals(expectedResponse, actualResponse);
    }
}
