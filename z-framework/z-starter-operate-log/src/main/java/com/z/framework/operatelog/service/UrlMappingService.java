package com.z.framework.operatelog.service;

import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import jakarta.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @Title: UrlMappingService
 * @Package com/longtu/service/UrlMappingService.java
 * @Description: 初始化mapping及接口中文描述信息
 * @author zhaozhiwei
 * @date 2022/7/20 上午10:22
 * @version V1.0
 */
@Service
public class UrlMappingService {

    private final WebApplicationContext applicationContext;

    public UrlMappingService(WebApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Getter
    private final List<Map<String, String>> urlMappingList = new ArrayList<>();

    /**
     * -- GETTER --
     *
     */
    @Getter
    private final Map<String, String> urlMap = new HashMap<>();


    @PostConstruct
    public void init() {

        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        // 获取url与类和方法的对应信息
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> mappingInfoHandlerMethodEntry : map.entrySet()) {
            Map<String, String> resultMap = new LinkedHashMap<>();

            RequestMappingInfo requestMappingInfo = mappingInfoHandlerMethodEntry.getKey();
            HandlerMethod handlerMethod = mappingInfoHandlerMethodEntry.getValue();

            Annotation[] annotations = handlerMethod.getMethod().getDeclaredAnnotations();
            // 处理具体的方法信息
            for (Annotation annotation : annotations) {
                if (annotation instanceof Operation) {
                    Operation methodDesc = (Operation) annotation;
                    String desc = methodDesc.description();
                    //接口描述
                    resultMap.put("desc",desc);
                }
            }

            final PathPatternsRequestCondition pathPatternsCondition = requestMappingInfo.getPathPatternsCondition();
            for (PathPattern pattern : pathPatternsCondition.getPatterns()) {
                //请求URL
                resultMap.put("url", pattern.getPatternString());
            }

            final RequestMethodsRequestCondition methods = requestMappingInfo.getMethodsCondition();
            final Optional<RequestMethod> first = methods.getMethods().stream().findFirst();
            // 保存url和描述的对照信息
            urlMap.put(resultMap.get("url") + "_" + (first.map(Enum::name).orElse("get")), resultMap.get("desc"));
            urlMappingList.add(resultMap);
        }
    }
}
