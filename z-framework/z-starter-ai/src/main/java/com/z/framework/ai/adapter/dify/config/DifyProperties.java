package com.z.framework.ai.adapter.dify.config;

import com.z.framework.ai.model.AppConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "z.module.ai.type", havingValue = "dify")
@ConfigurationProperties(prefix = "z.module.ai")
public class DifyProperties extends AppConfig {

}
