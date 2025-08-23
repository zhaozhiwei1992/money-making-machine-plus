package com.z.framework.operatelog.aop;

import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.z.framework.common.aop.CachedBodyRequestWrapper;
import com.z.framework.operatelog.domain.RequestLog;
import com.z.framework.operatelog.service.RequestLogService;
import com.z.framework.operatelog.service.UrlMappingService;
import com.z.framework.security.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import java.util.*;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: TraceIdInterceptor
 * @Package com/example/web/rest/intercepter/TraceIdInterceptor.java
 * @Description: 每次请求产生traceId方便后续追踪
 * @date 2022/5/31
 */

@Slf4j
public class RequestLoggingInterceptor implements AsyncHandlerInterceptor {

    private static final ThreadLocal<String> traceIdThreadLocal = new ThreadLocal<>();

    private static final ThreadLocal<RequestLog> requestLogThreadLocal = new ThreadLocal<>();

    // 敏感字段集合
    private static final List<String> SENSITIVE_KEYS = Arrays.asList("password", "token");

    public static String getCurrentTraceId() {
        return traceIdThreadLocal.get();
    }

    // 这里不能注入, 会有循环依赖, 使用时getBean即可
    private UrlMappingService urlMappingService;

    @Autowired
    private RequestLogService requestLogService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        try {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
            if (log.isDebugEnabled()) {
                Arrays.stream(methodParameters).forEach(methodParameter -> log.debug("methodParameter: {}", methodParameter.getParameterName()));
                log.debug("handler method : {} \n", handler);
            }

            // 记录客户端ip
            final String clientIP = JakartaServletUtil.getClientIP(request);
            // 请求的地址
            final String requestURI = request.getRequestURI();
            // 生成traceId
            final String traceId = UUID.randomUUID().toString();
            // *.log中使用
            MDC.put("traceId", traceId);
            traceIdThreadLocal.set(traceId);
            //            当前用户
            final String loginName = SecurityUtils.getCurrentLoginName();

            final String method = request.getMethod();
            RequestLog requestLogging = new RequestLog();
            requestLogging.setTraceId(traceId);
            requestLogging.setLoginName(loginName);
            requestLogging.setRequestURI(requestURI);
            requestLogging.setRequestMethod(method);
            requestLogging.setClientIp(clientIP);
            //           异步线程记录到数据库

            // 循环依赖, 这里主动获取bean: urlMappingService
            if (Objects.isNull(urlMappingService)) {
                urlMappingService = SpringUtil.getBean(UrlMappingService.class);
            }

            if (requestURI.startsWith("/api")) {
                String s = urlMappingService.getUrlMap().get(requestURI + "_" + method);
                requestLogging.setRequestName(s);
                requestLogging.setParams(parseParameters(request));
                requestLogging.setSuccess("是");
            }
            requestLogThreadLocal.set(requestLogging);
            return true;
        } catch (Exception e) {
            log.error("Request logging preHandle error", e);
            cleanupThreadLocal();
            throw new RuntimeException(e);
        }
    }

    private String parseParameters(HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>();

        // 处理URL参数
        request.getParameterMap().forEach((key, values) -> {
            if (values.length > 1) {
                params.put(key, values);
            } else {
                params.put(key, values[0]);
            }
        });

        // 敏感信息脱敏
        params.replaceAll((k, v) -> SENSITIVE_KEYS.contains(k) ? "******" : v);

        // 处理JSON body（需要ContentCachingRequestWrapper）
        if (request instanceof CachedBodyRequestWrapper) {
            String body = ((CachedBodyRequestWrapper) request).getBody();
            try {
                JSONObject json = JSON.parseObject(body);
                if(!Objects.isNull(json)){
                    SENSITIVE_KEYS.forEach(key -> {
                        if (json.containsKey(key)) {
                            json.put(key, "******");
                        }
                    });
                }
            }catch (Exception e){
                log.error("parseParameters error", e);
            }
            params.put("requestBody", body);
        }

        return JSONUtil.toJsonStr(params);
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response,
                                               Object handler) {
        // 异步请求时立即清理资源
        cleanupThreadLocal();
        MDC.remove("traceId");
        // 确保缓存完成
        ((CachedBodyRequestWrapper) request).getBody();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        final String requestURI = request.getRequestURI();
        RequestLog requestLog = requestLogThreadLocal.get();
        if ((!Objects.isNull(ex) || response.getStatus() == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) && requestURI.startsWith("/api") && !requestURI.startsWith("/api/request/")) {
            // 如果有异常， 标记接口请求失败
            requestLog.setSuccess("否");
        }
        requestLogService.asyncSaveRequestLog(requestLog);
        //  清理threadLocal
        cleanupThreadLocal();
    }

    private void cleanupThreadLocal() {
        traceIdThreadLocal.remove();
        requestLogThreadLocal.remove();
    }
}
