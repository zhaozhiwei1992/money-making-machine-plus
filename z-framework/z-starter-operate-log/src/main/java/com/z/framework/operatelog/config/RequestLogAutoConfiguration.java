package com.z.framework.operatelog.config;

import com.z.framework.operatelog.aop.RequestLoggingInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@AutoConfiguration
public class RequestLogAutoConfiguration implements WebMvcConfigurer {

    @Bean
    public RequestLoggingInterceptor requestLoggingInterceptor() {
        return new RequestLoggingInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 请求监控
        registry.addInterceptor(requestLoggingInterceptor());
    }
}
