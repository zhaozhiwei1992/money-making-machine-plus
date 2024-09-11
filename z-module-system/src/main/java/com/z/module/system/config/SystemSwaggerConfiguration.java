package com.z.module.system.config;

import com.z.framework.common.config.SwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class SystemSwaggerConfiguration {

    /**
     * system 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi systemGroupedOpenApi() {
        // 因为实际在system模块下的resource都没家 system路径即, 没有/api/system形式, 所以分组下
        // No operations defined in spec!
        return SwaggerAutoConfiguration.buildGroupedOpenApi("system");
    }

}
