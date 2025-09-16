package com.z.framework.common.aop;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class CachingRequestBodyFilter extends OncePerRequestFilter {

    private static final List<String> WRAPPED_METHODS = Arrays.asList("POST", "PUT", "PATCH");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws IOException, ServletException {
        // 仅包装指定方法的请求
        if (shouldWrap(request)) {
            chain.doFilter(new CachedBodyRequestWrapper(request), response);
        } else {
            chain.doFilter(request, response);
        }
    }

    private boolean shouldWrap(HttpServletRequest request) {
        String method = request.getMethod().toUpperCase();
        String contentType = request.getContentType();

        // 条件1: 仅处理需要Body的方法
        boolean isWrappedMethod = WRAPPED_METHODS.contains(method);

        // 条件2: 排除文件上传
        boolean isMultipart = contentType != null
                && contentType.startsWith("multipart/");

        return isWrappedMethod && !isMultipart;
    }

    @Bean
    public FilterRegistrationBean<CachingRequestBodyFilter> cachingRequestBodyFilterRegister() {
        FilterRegistrationBean<CachingRequestBodyFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CachingRequestBodyFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1); // 确保最高优先级
        registration.addUrlPatterns("/*");
        return registration;
    }

}