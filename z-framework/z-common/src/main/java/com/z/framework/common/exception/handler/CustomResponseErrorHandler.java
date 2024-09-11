package com.z.framework.common.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.util.Arrays;

/**
 * @Title: MyErrorHandle
 * @Package: com/longtu/exception/MyErrorHandle.java
 * @Description: org.springframework.web.client.DefaultResponseErrorHandler#handleError
 * (org.springframework.http.client.ClientHttpResponse, org.springframework.http.HttpStatus)
 * 上述默认实现对于返回是500 或者 400 的请求，直接抛出异常, restTemplate看到的结果一脸蒙蔽, 这里直接覆盖掉这个逻辑
 * @author: zhaozhiwei
 * @date: 2022/11/27 下午11:54
 * @version: V1.0
 */
public class CustomResponseErrorHandler extends DefaultResponseErrorHandler {

    @Override
    protected void handleError(ClientHttpResponse response, HttpStatusCode statusCode) throws IOException {
        // 覆盖默认的实现, 防止500的异常请求日志信息丢失
        if (!Arrays.asList(HttpStatus.INTERNAL_SERVER_ERROR
                , HttpStatus.BAD_REQUEST).contains(statusCode)) {
            super.handleError(response, statusCode);
        }
    }
}