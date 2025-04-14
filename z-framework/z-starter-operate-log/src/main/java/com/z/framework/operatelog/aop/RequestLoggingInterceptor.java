package com.z.framework.operatelog.aop;

import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.z.framework.operatelog.domain.RequestLog;
import com.z.framework.operatelog.repository.RequestLogRepository;
import com.z.framework.operatelog.service.UrlMappingService;
import com.z.framework.security.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.*;

/**
 * @Title: TraceIdInterceptor
 * @Package com/example/web/rest/intercepter/TraceIdInterceptor.java
 * @Description: 每次请求产生traceId方便后续追踪
 * @author zhaozhiwei
 * @date 2022/5/31
 * @version V1.0
 */

@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {
    private static final ThreadLocal<String> traceIdThreadLocal = new ThreadLocal<>();

    // 敏感字段集合
    private static final List<String> SENSITIVE_KEYS = Arrays.asList("password", "token");

    public static String getCurrentTraceId() {
        return traceIdThreadLocal.get();
    }

    @Autowired
    private RequestLogRepository requestLoggingRepository;

    // 这里不能注入, 会有循环依赖, 使用时getBean即可
    private UrlMappingService urlMappingService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
            for (MethodParameter methodParameter : methodParameters) {
                log.info(methodParameter.getParameterName());
            }
            log.info("handler method : {} \n", handler);

            // 包装请求以缓存body内容
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

            // 记录客户端ip
            final String clientIP = JakartaServletUtil.getClientIP(wrappedRequest);
            // 请求的地址
            final String requestURI = wrappedRequest.getRequestURI();
            // 生成traceId
            final String traceId = UUID.randomUUID().toString();
            // *.log中使用
            MDC.put("traceId", traceId);
            traceIdThreadLocal.set(traceId);
            //            当前用户
            final String loginName = SecurityUtils.getCurrentLoginName();

            //           异步线程记录到数据库
            final RequestLog requestLogging = new RequestLog();
            requestLogging.setTraceId(traceId);
            requestLogging.setLoginName(loginName);
            requestLogging.setRequestURI(requestURI);
            final String method = wrappedRequest.getMethod();
            requestLogging.setRequestMethod(method);
            requestLogging.setClientIp(clientIP);

            // 循环依赖, 这里主动获取bean: urlMappingService
            if(Objects.isNull(urlMappingService)){
                urlMappingService = SpringUtil.getBean(UrlMappingService.class);
            }

            if(requestURI.startsWith("/api")){
                String s = urlMappingService.getUrlMap().get(requestURI + "_" + method);
                requestLogging.setRequestName(s);
                requestLogging.setParams(parseParameters(wrappedRequest));
                requestLogging.setSuccess("是");
                requestLoggingRepository.save(requestLogging);
            }
        }

        return true;
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

        // 处理JSON body（需要ContentCachingRequestWrapper）
        if (request instanceof ContentCachingRequestWrapper) {
            byte[] content = ((ContentCachingRequestWrapper) request).getContentAsByteArray();
            if (content.length > 0) {
                // 实际项目需根据Content-Type解析
                String body = new String(content);
                params.put("requestBody", body);
            }
        }

        // 敏感信息脱敏
        params.replaceAll((k, v) -> SENSITIVE_KEYS.contains(k) ? "******" : v);

        return JSONUtil.toJsonStr(params);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        final String requestURI = request.getRequestURI();
        if(!Objects.isNull(ex) && requestURI.startsWith("/api") && !requestURI.startsWith("/api/request/")){
            // 如果有异常， 标记接口请求失败
            final String traceId = traceIdThreadLocal.get();
            final Optional<RequestLog> result = requestLoggingRepository.findOneByTraceId(traceId);
            if(result.isPresent()){
                RequestLog requestLogging = result.get();
                requestLogging.setSuccess("否");
                requestLoggingRepository.save(requestLogging);
            }
        }
        //  清理threadLocal
        traceIdThreadLocal.remove();
    }
}
