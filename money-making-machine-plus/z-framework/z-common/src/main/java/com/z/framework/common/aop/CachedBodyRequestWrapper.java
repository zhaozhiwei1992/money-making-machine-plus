package com.z.framework.common.aop;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.Executors;

/**
 * @Title: CachedBodyRequestWrapper
 * @Package com/z/framework/operatelog/aop/CachedBodyRequestWrapper.java
 * @Description: 同时支持同步请求和异步请求包装
 * @author zhaozhiwei
 * @date 2025/4/15 00:18
 * @version V1.0
 */
public class CachedBodyRequestWrapper extends HttpServletRequestWrapper {
    private final byte[] cachedBody;

    public CachedBodyRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        // 强制读取输入流（即使为空）
        this.cachedBody = StreamUtils.copyToByteArray(request.getInputStream());
    }

    @Override
    public ServletInputStream getInputStream() {
        // 返回基于缓存的流
        ByteArrayInputStream buffer = new ByteArrayInputStream(cachedBody);

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                // 当 buffer 无剩余字节时标记为结束
                return buffer.available() == 0;
            }

            @Override
            public boolean isReady() {
                // 同步请求始终返回 true，异步需结合 ReadListener
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // 同步场景无需实现，但需满足接口要求
                if (!isAsyncSupported()) {
                    throw new IllegalStateException("Async not supported");
                }

                // 异步处理时立即触发数据可用
                Executors.newSingleThreadExecutor().submit(() -> {
                    try {
                        readListener.onDataAvailable();
                        while (buffer.read() != -1) {
                            // 消费数据
                        }
                        readListener.onAllDataRead();
                    } catch (IOException e) {
                        readListener.onError(e);
                    }
                });
            }

            @Override
            public int read() {
                return buffer.read();
            }
        };
    }

    public String getBody() {
        // 空值保护
        if (cachedBody == null || cachedBody.length == 0) {
            return "";
        }
        // 动态获取编码（优先使用请求头字符集）
        String charset = Optional.ofNullable(this.getCharacterEncoding())
                .orElse(StandardCharsets.UTF_8.name());
        return new String(cachedBody, Charset.forName(charset));
    }
}