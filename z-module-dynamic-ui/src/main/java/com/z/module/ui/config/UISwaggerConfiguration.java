package com.z.module.ui.config;

import com.z.framework.common.config.SwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * UI 模块 swagger配置
 *
 * @author zhaozhiwei
 */
@Configuration(proxyBeanMethods = false)
public class UISwaggerConfiguration {

    /**
     * screen 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi uiGroupedOpenApi() {
        return SwaggerAutoConfiguration.buildGroupedOpenApi("ui");
    }

}
