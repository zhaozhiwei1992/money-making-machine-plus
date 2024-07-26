package com.z.module.system.aop;

import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.z.module.system.service.OnLineUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: null.java
 * @Package com.example.listener
 * @Description: 监听认证成功失败事件
 * {@see org.springframework.context.ApplicationListener}
 * 登录成功或者退出维护系统用户信息
 * @date 2022/5/25 下午2:24
 */
@Component
public class ListenerAuthenticationEvent {

    private static final Logger log = LoggerFactory.getLogger(ListenerAuthenticationEvent.class);

    private OnLineUserService onLineUserService;

    private HttpServletRequest request;

    public ListenerAuthenticationEvent(OnLineUserService onLineUserService, HttpServletRequest request) {
        this.onLineUserService = onLineUserService;
        this.request = request;
    }

    @EventListener(classes = AuthenticationSuccessEvent.class)
    public void onSuccess(AuthenticationSuccessEvent successEvent) {
        log.info("{} 认证成功", successEvent.getAuthentication().getName());

        //      获取客户端信息, 如果获取不到则调整在 com.example.web.rest.UserJWTController.authorize写入threadLocal这里获取
        final HashMap<String, Object> onLineUser = new HashMap<>();
        onLineUser.put("ip", ServletUtil.getClientIP(request));
        final UserAgent userAgentParse = UserAgentUtil.parse(request.getHeader("User-Agent"));
        onLineUser.put("os", userAgentParse.getOs().toString());

        onLineUser.put("userName", successEvent.getAuthentication().getName());
        onLineUser.put("browser", userAgentParse.getBrowser().toString());
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final String format = dateTimeFormatter.format(
            LocalDateTime.ofInstant(Instant.ofEpochMilli(successEvent.getTimestamp()), ZoneId.of("Asia/Shanghai"))
        );
        onLineUser.put("nowTime", format);
        onLineUserService.add(onLineUser);
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failureEvent) {
        log.info("{} 认证失败，失败原因： {}", failureEvent.getAuthentication().getName(), failureEvent.getException().getMessage());
    }

    @EventListener(classes = LogoutSuccessEvent.class)
    public void onLogout(LogoutSuccessEvent logoutSuccessEvent) {
        log.info("{} 退出成功", logoutSuccessEvent.getAuthentication().getName());
        onLineUserService.delete(logoutSuccessEvent.getAuthentication().getName());
    }
}
