package com.z.framework.operatelog.aop;

import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.z.framework.operatelog.domain.RequestLog;
import com.z.framework.operatelog.repository.RequestLogRepository;
import com.z.framework.operatelog.service.UrlMappingService;
import com.z.framework.security.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

            //           异步线程记录到数据库
            final RequestLog requestLogging = new RequestLog();
            requestLogging.setTraceId(traceId);
            requestLogging.setLoginName(loginName);
            requestLogging.setRequestURI(requestURI);
            final String method = request.getMethod();
            requestLogging.setRequestMethod(method);
            requestLogging.setClientIp(clientIP);

            // 循环依赖, 这里主动获取bean: urlMappingService
            if(Objects.isNull(urlMappingService)){
                urlMappingService = SpringUtil.getBean(UrlMappingService.class);
            }

            if(requestURI.startsWith("/api")){
                String s = urlMappingService.getUrlMap().get(requestURI + "_" + method);
                requestLogging.setRequestName(s);
                final Map<String, Object> parameterMap = this.getParameterMap(request);
                final String jsonStr = JSONUtil.toJsonStr(parameterMap);
                if(jsonStr.length() < 1000){
                    requestLogging.setParams(jsonStr);
                    requestLogging.setSuccess("是");
                    requestLoggingRepository.save(requestLogging);
                }
            }
        }

        return true;
    }

    public Map<String, Object> getParameterMap(HttpServletRequest request) {
        Map<String, Object> param = new HashMap<>();
        try {
            Enumeration<String> em = request.getParameterNames();
            while (em.hasMoreElements()) {
                String key = em.nextElement();
                param.put(key, request.getParameter(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return param;
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
