package com.example.aop;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.example.domain.RequestLog;
import com.example.repository.RequestLogRepository;
import com.example.service.UrlMappingService;
import com.example.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Autowired
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
            final String clientIP = ServletUtil.getClientIP(request);
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
            requestLogging.setClientIP(clientIP);
            if(requestURI.startsWith("/api")
                    // 系统内部程序不在记录日志
                    && !requestURI.startsWith("/api/request/")
                    && !requestURI.startsWith("/api/login/")
                    && !requestURI.startsWith("/api/menus/")
                    && !requestURI.startsWith("/api/request/")
                    && !requestURI.startsWith("/api/roles/")
                    && !requestURI.startsWith("/api/params/")
                    && !requestURI.startsWith("/api/task/")
                    && !requestURI.startsWith("/api/tasks/")
                    && !requestURI.startsWith("/api/users/")
            ){
                String s = urlMappingService.getUrlMap().get(requestURI + "_" + method);
                if(StringUtils.isEmpty(s)){
                    // http://192.168.22.120:80/pay/payvoucher/pull/340000000/2022/001001
                    // 上述类型形式的请求还原为{mof_div_code}/{fiscal_year}/{agency_code}就可以匹配了
                    final Map<String, String> map = new HashMap<>();
                    map.put("0", "{agency_code}");
                    map.put("1", "{fiscal_year}");
                    map.put("2", "{mof_div_code}");
                    final String[] split = requestURI.split("/");
                    for (int i = split.length-1; i >= split.length-3; i--) {
                        split[i] = map.get(String.valueOf(split.length-1-i));
                    }
                    final String join = String.join("/", split);
                    s = urlMappingService.getUrlMap().get(join + "_" + method);
                    requestLogging.setRequestName(s);
                }else{
                    requestLogging.setRequestName(s);
                }
                final Map<String, Object> parameterMap = this.getParameterMap(request);
                requestLogging.setParams(JSONUtil.toJsonStr(parameterMap));
                requestLogging.setSuccess("是");
                requestLoggingRepository.save(requestLogging);
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
