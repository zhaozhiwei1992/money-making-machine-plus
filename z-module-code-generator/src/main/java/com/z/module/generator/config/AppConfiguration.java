package com.z.module.generator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title: null.java
 * @Package: com.longtu.web.config
 * @Description: 应用配置
 * @author: zhaozhiwei
 * @date: 2023/3/16 下午2:05
 * @version: V1.0
 */

@ConfigurationProperties(
        prefix = "z.module"
)
@Configuration
public class AppConfiguration {

    // 代码生成相关配置
    private final Generator generator = new Generator();

    public Generator getGenerator() {
        return generator;
    }

    public static class Generator{

        private String module;

        private String basePackage;

        private String author;

        private String email;

        private String tablePrefix;

        // 数据库字段映射
        private Map<String, Object> mapping = new HashMap<>();

        public String getBasePackage() {
            return basePackage;
        }

        public void setBasePackage(String basePackage) {
            this.basePackage = basePackage;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getTablePrefix() {
            return tablePrefix;
        }

        public void setTablePrefix(String tablePrefix) {
            this.tablePrefix = tablePrefix;
        }

        public Map<String, Object> getMapping() {
            return mapping;
        }

        public void setMapping(Map<String, Object> mapping) {
            this.mapping = mapping;
        }

        public String getModule() {
            return module;
        }

        public void setModule(String module) {
            this.module = module;
        }
    }
}
