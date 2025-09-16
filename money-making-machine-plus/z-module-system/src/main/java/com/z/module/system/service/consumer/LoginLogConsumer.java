package com.z.module.system.service.consumer;

import com.z.module.system.domain.LoginLog;
import com.z.module.system.repository.LoginLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @Title: null.java
 * @Package: com.z.module.system.service.consumer
 * @Description: TODO
 * @author: zhaozhiwei
 * @date: 2025/4/11 21:58
 * @version: V1.0
 */
@Component
@RocketMQMessageListener(topic = LoginLogConsumer.TOPIC, consumerGroup = LoginLogConsumer.TOPIC + "-group")
@Slf4j
public class LoginLogConsumer implements RocketMQListener<LoginLog> {

    public static final String TOPIC = "login-log";

    private final LoginLogRepository loginLogRepository;

    public LoginLogConsumer(LoginLogRepository loginLogRepository) {
        this.loginLogRepository = loginLogRepository;
    }

    @Override
    public void onMessage(LoginLog loginLog) {
        loginLogRepository.save(loginLog);
    }
}
