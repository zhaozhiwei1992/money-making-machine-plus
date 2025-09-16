package com.z.framework.security.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: CustomSecurityProperties
 * @Package com/z/framework/security/config/CustomSecurityProperties.java
 * @Description: 一些自定义配置信息
 * @date 2023/5/20 下午2:38
 */
@ConfigurationProperties(prefix = "z.security")
@Data
public class CustomSecurityProperties {

    private List<String> authWhiteList;

    private final Authentication authentication = new Authentication();

    private final RememberMe rememberMe = new RememberMe();

    @Data
    public static class RememberMe {
        private @NotNull String key;
    }

    @Data
    public static class Authentication {
        private final Jwt jwt = new Jwt();

        @Data
        public static class Jwt {
            private String secret;
            private String base64Secret;
            private int tokenValidityInSeconds;
            private int tokenValidityInSecondsForRememberMe;
        }
    }
}
