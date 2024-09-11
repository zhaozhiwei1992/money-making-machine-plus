package com.z.module.screen.config;

import com.z.framework.common.config.SwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * screen 模块 swagger配置
 *
 * @author 芋道源码
 */
@Configuration(proxyBeanMethods = false)
public class ScreenSwaggerConfiguration {

    /**
     * screen 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi screenGroupedOpenApi() {
        return SwaggerAutoConfiguration.buildGroupedOpenApi("goview");
    }

}
