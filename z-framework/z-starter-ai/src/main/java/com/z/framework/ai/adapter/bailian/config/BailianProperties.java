package com.z.framework.ai.adapter.bailian.config;

import com.z.framework.ai.model.AppConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "z.module.ai.type", havingValue = "bailian")
@ConfigurationProperties(prefix = "z.module.ai")
public class BailianProperties extends AppConfig {

}
