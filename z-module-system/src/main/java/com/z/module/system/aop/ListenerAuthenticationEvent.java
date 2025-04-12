package com.z.module.system.aop;

import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.z.module.system.domain.LoginLog;
import com.z.module.system.service.LoginLogService;
import com.z.module.system.service.OnLineUserService;
import com.z.module.system.service.consumer.LoginLogConsumer;
import com.z.module.system.web.vo.LoginVO;
import com.z.module.system.web.vo.OnLineUserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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

    private final LoginLogService loginLogService;

    private final RocketMQTemplate rocketMQTemplate;


    public ListenerAuthenticationEvent(OnLineUserService onLineUserService, HttpServletRequest request, LoginLogService loginLogService, RocketMQTemplate rocketMQTemplate) {
        this.onLineUserService = onLineUserService;
        this.request = request;
        this.loginLogService = loginLogService;
        this.rocketMQTemplate = rocketMQTemplate;
    }

//    @EventListener(classes = AuthenticationSuccessEvent.class)
    @EventListener
    public void onSuccess(AuthenticationSuccessEvent successEvent) {
        log.info("{} 认证成功", successEvent.getAuthentication().getName());

        //      获取客户端信息, 如果获取不到则调整在 com.example.web.rest.UserJWTController.authorize写入threadLocal这里获取
        final OnLineUserVO onLineUser = new OnLineUserVO();
        onLineUser.setIp(JakartaServletUtil.getClientIP(request));
        final UserAgent userAgentParse = UserAgentUtil.parse(request.getHeader("User-Agent"));
        onLineUser.setOs(userAgentParse.getOs().toString());

        onLineUser.setUserName(successEvent.getAuthentication().getName());
        onLineUser.setBrowser(userAgentParse.getBrowser().toString());
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final String format = dateTimeFormatter.format(
            LocalDateTime.ofInstant(Instant.ofEpochMilli(successEvent.getTimestamp()), ZoneId.of("Asia/Shanghai"))
        );
        onLineUser.setNowTime(format);
//        onLineUser.setTokenId(successEvent.getAuthentication().getCredentials().toString());
        onLineUserService.add(onLineUser);


        // 登录成功记录日志
        LoginVO loginVM = new LoginVO();
        loginVM.setUsername(successEvent.getAuthentication().getName());
        LoginLog loginLog = loginLogService.genLogInfo(loginVM, request);
        loginLog.setResult("SUCCESS");
        loginLog.setRemark("登录成功");
        Message<LoginLog> loginLogMsg = MessageBuilder.withPayload(loginLog).build();
        rocketMQTemplate.send(LoginLogConsumer.TOPIC, loginLogMsg);
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failureEvent) {
        // 登录成功记录日志
        LoginVO loginVM = new LoginVO();
        loginVM.setUsername(failureEvent.getAuthentication().getName());
        LoginLog loginLog = loginLogService.genLogInfo(loginVM, request);
        loginLog.setResult("FAILURE");
        loginLog.setRemark(failureEvent.getException().getMessage());
        Message<LoginLog> loginLogMsg = MessageBuilder.withPayload(loginLog).build();
        rocketMQTemplate.send(LoginLogConsumer.TOPIC, loginLogMsg);
        log.info("{} 认证失败，失败原因： {}", failureEvent.getAuthentication().getName(), failureEvent.getException().getMessage());
    }

    @EventListener
    public void onLogout(LogoutSuccessEvent logoutSuccessEvent) {
        log.info("{} 退出成功", logoutSuccessEvent.getAuthentication().getName());
        onLineUserService.delete(logoutSuccessEvent.getAuthentication().getName());
    }
}
