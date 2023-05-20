package com.z.module.report.config;

import com.z.module.report.aop.TokenIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ReportWebConfiger implements WebMvcConfigurer {

    @Autowired
    private TokenIntercepter tokenIntercepter;

    /**
     *  // 多个拦截器组成一个拦截器链
     *         // addPathPatterns 用于添加拦截规则
     *         // excludePathPatterns 用户排除拦截
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 进入预览, 设计, 列表页面时候进行token认证, 有的跳转没有token, 不能直接/jmreport/**
        registry.addInterceptor(tokenIntercepter).addPathPatterns("/jmreport/view/**", "/jmreport/index/**", "/jmreport/list");
    }

}
