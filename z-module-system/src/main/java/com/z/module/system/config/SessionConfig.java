package com.z.module.system.config;

import org.springframework.boot.autoconfigure.session.DefaultCookieSerializerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.DefaultCookieSerializer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title: SessionConfig
 * @Package com/z/module/system/config/SessionConfig.java
 * @Description: 调整为https后, 登录时后端无法获取session中的captcha信息
 * 查看前台网络请求发现setcookie有误，提示 SameSite=Lax引起的Set-Cookie不成功问题
 * 处理: https://blog.csdn.net/new_baibai/article/details/129307540
 * 开发模式没有此问题
 * @author zhaozhiwei
 * @date 2024/7/4 下午3:25
 * @version V1.0
 */
@Configuration
@EnableSpringHttpSession
@Profile("prod")
public class SessionConfig {
    @Bean
    public SessionRepository sessionRepository() {
        return new MapSessionRepository(new ConcurrentHashMap<>());
    }

    @Bean
    DefaultCookieSerializerCustomizer cookieSerializerCustomizer() {
        return new DefaultCookieSerializerCustomizer() {
            @Override
            public void customize(DefaultCookieSerializer cookieSerializer) {
                cookieSerializer.setSameSite("None");
                // 此项必须，否则set-cookie会被chrome浏览器阻拦
                cookieSerializer.setUseSecureCookie(true);
            }
        };
    }
}