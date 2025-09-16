package com.z.framework.common.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.z.framework.common.web.rest.vm.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @Title: GlobalResponseHandler
 * @Package com/z/framework/common/aop/GlobalResponseHandler.java
 * @Description: 定义统一响应处理，全部返回ResponseData，自定义业务编码
 * @author zhaozhiwei
 * @date 2025/7/23 22:46
 * @version V1.0
 */
//@ControllerAdvice
@Slf4j
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        log.debug("Return type: {}, Converter type: {}", returnType.getParameterType(), converterType);
        // 排除某些不需要处理的类型，如swagger等
//        return !returnType.getParameterType().isAssignableFrom(ResponseData.class);

        return true; // 拦截所有返回值
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        // 自动封装为 ResponseData
        if (body instanceof ResponseData) {
            return body; // 已经是封装对象则跳过
        }

        // 处理String类型返回值, 或者将将jackson转换器放到第一个,在string之前
//        if (body instanceof String) {
//            try {
//                return objectMapper.writeValueAsString(ResponseData.ok(body));
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException("String转换异常", e);
//            }
//        }

        return ResponseData.ok(body);
    }
}