package com.z.module.ai.service;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class LocalChatClient {

    private RestTemplate restTemplate;

    public LocalChatClient(){
        restTemplate = new RestTemplate();
    }

    public String callWithMessage(String content) {

        String url = "http://localhost:8000/api/question?q=" + content;
        try {
            final ResponseEntity<String> exchange = restTemplate.exchange(
                    RequestEntity.get(new URI(url))
                            .build(), String.class);

            return exchange.getBody();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}