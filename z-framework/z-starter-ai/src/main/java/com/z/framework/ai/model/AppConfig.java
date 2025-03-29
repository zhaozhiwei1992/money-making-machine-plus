package com.z.framework.ai.model;

import lombok.Data;

/**
 * 不同平台配置
 */
@Data
public class AppConfig {
    /**
     * API基础URL
     */
    private String baseUrl;

    /**
     * 连接超时时间（毫秒）
     */
    private int connectTimeout = 5000;

    /**
     * 读取超时时间（毫秒）
     */
    private int readTimeout = 60000;

    /**
     * 写入超时时间（毫秒）
     */
    private int writeTimeout = 30000;
}
