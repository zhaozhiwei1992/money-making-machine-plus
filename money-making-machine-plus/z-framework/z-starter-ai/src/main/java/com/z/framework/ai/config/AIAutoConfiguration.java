package com.z.framework.ai.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: null.java
 * @Package com.longtu.config
 * @Description: TODO
 * @date 2022/8/31 上午9:38
 */
@AutoConfiguration
@ComponentScan(value = {"com.z.framework.ai"})
public class AIAutoConfiguration {

    @Bean
    public WebClient difyWebClient() {
        return WebClient.builder().defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .codecs(configurer ->
                configurer.defaultCodecs().maxInMemorySize(256 * 1024) // 禁用缓冲
        ).build();
    }
}
