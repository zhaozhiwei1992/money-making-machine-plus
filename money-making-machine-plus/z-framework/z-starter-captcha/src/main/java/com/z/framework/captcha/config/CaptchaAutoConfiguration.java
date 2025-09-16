package com.z.framework.captcha.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author zhaozhiwei
 * @version V1.0
 * @Title: null.java
 * @Package com.longtu.config
 * @Description: TODO
 * @date 2022/8/31 上午9:38
 */
@AutoConfiguration
@ComponentScan(value = {"com.z.framework.captcha"})
public class CaptchaAutoConfiguration {

    @Bean
    public Producer producer(){
        final DefaultKaptcha defaultKaptcha = new DefaultKaptcha();

        // 读取配置文件
        Properties props = new Properties();

        try {
            props.load(CaptchaAutoConfiguration.class.getClassLoader()
                    .getResourceAsStream("captcha.properties"));
        }catch (Exception e) {
            e.printStackTrace();
        }

        defaultKaptcha.setConfig(new Config(props));
        return defaultKaptcha;
    }

}
