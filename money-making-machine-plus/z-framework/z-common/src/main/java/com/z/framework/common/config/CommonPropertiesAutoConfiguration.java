package com.z.framework.common.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


/**
 * Swagger 自动配置类，基于 OpenAPI + Springdoc 实现。
 *
 * 友情提示：
 * 1. Springdoc 文档地址：<a href="https://github.com/springdoc/springdoc-openapi">仓库</a>
 * 2. Swagger 规范，于 2015 更名为 OpenAPI 规范，本质是一个东西
 *
 * @author 芋道源码
 */
@AutoConfiguration
@EnableConfigurationProperties(CommonProperties.class)
public class CommonPropertiesAutoConfiguration {

}

