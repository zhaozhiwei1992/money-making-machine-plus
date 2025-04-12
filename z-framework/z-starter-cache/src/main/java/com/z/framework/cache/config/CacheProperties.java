package com.z.framework.cache.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @Title: null.java
 * @Package: com.z.framework.cache.config
 * @Description: TODO
 * @author: zhaozhiwei
 * @date: 2025/4/12 15:42
 * @version: V1.0
 */
@Configuration
@ConfigurationProperties("z.cache-config")
@Data
public class CacheProperties {

    private Map<String, CacheSpec> specs;

    @Data
    public static class CacheSpec {
        private Integer expireTime;
        private Integer maxSize;
        private String remark;
    }
}
