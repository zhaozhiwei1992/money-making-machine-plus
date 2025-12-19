
package com.z.module.ai.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class LocalChatClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private LocalChatClient localChatClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCallWithMessage() throws URISyntaxException {
        String content = "test question";
        String expectedResponse = "test answer";
        String url = "http://localhost:8000/api/question?q=" + content;

        ResponseEntity<String> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);

        when(restTemplate.exchange(
            eq(RequestEntity.get(new URI(url)).build()),
            eq(String.class)
        )).thenReturn(responseEntity);

        String actualResponse = localChatClient.callWithMessage(content);

        assertEquals(expectedResponse, actualResponse);
    }
}
