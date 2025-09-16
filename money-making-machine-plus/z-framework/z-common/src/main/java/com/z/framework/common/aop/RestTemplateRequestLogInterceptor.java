package com.z.framework.common.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.RequestEntity;

/**
 * @Title: RestTemplateRequestLogInterceptor
 * @Package com/longtu/aop/RestTemplateRequestLogInterceptor.java
 * @Description: 记录RestTemplate请求日志信息
 * @author zhaozhiwei
 * @date 2022/12/6 下午4:45
 * @version V1.0
 */
public class RestTemplateRequestLogInterceptor implements MethodInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RestTemplateRequestLogInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        final Object argument = invocation.getArguments()[0];
        String url;
        if(argument instanceof RequestEntity){
            url = String.valueOf(((RequestEntity)argument).getUrl());
        }else{
            url = (String) invocation.getArguments()[0];
        }

        final long l = System.currentTimeMillis();
        Object result = invocation.proceed();
        logger.info("请求: {}, 耗时: {}", url, System.currentTimeMillis() - l);
        return result;
    }
}
