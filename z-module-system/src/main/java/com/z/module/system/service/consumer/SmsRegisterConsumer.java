package com.z.module.system.service.consumer;

import com.z.module.system.web.vo.UserVO;
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
@RocketMQMessageListener(topic = SmsRegisterConsumer.TOPIC, consumerGroup = SmsRegisterConsumer.TOPIC + "-group")
@Slf4j
public class SmsRegisterConsumer implements RocketMQListener<UserVO> {

    public static final String TOPIC = "user-register-sms";

    @Override
    public void onMessage(UserVO userVO) {
        log.info("SmsRegisterConsumer 收到消息: {}", userVO);
    }
}
