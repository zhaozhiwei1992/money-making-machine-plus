package com.z.module.ai.config;

import com.z.framework.common.config.SwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AISwaggerConfiguration {

    /**
     * system 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi aiGroupedOpenApi() {
        // No operations defined in spec!
        return SwaggerAutoConfiguration.buildGroupedOpenApi("ai");
    }

}
