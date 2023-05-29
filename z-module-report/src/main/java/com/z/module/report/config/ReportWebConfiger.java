package com.z.module.report.config;

import com.z.module.report.aop.TokenIntercepter;
import com.z.module.report.aop.TransactionalInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class ReportWebConfiger implements WebMvcConfigurer {

    @Autowired
    private TokenIntercepter tokenIntercepter;

    @Autowired
    private TransactionalInterceptor transactionalInterceptor;

    /**
     *  // 多个拦截器组成一个拦截器链
     *         // addPathPatterns 用于添加拦截规则
     *         // excludePathPatterns 用户排除拦截
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 进入预览, 设计, 列表页面时候进行token认证, 有的跳转没有token, 不能直接/jmreport/**
        final List<String> uiPathList = Arrays.asList("/jmreport/view/**", "/jmreport/index/**", "/jmreport/list");
        registry.addInterceptor(tokenIntercepter).addPathPatterns(uiPathList);
        // 需要带入事务的url, 省事点就全部, 命名不规范全配置太坑
        registry.addInterceptor(transactionalInterceptor).addPathPatterns("/jmreport/**").excludePathPatterns(uiPathList);
    }

}
