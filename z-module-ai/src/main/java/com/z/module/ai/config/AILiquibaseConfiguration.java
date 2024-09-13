package com.z.module.ai.config;

import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(LiquibaseProperties.class)
@ConditionalOnProperty(prefix = "spring.liquibase", name = "enabled", havingValue = "true")
@Slf4j
public class AILiquibaseConfiguration {

    // 其它模块需要使用liquibase, 可以使用该配置, 调整模块名称即MODULE_NAME可
    private static final String MODULE_NAME = "ai";

    @Bean
    public SpringLiquibase liquibaseModuleAI(
            LiquibaseProperties liquibaseProperties,
            DataSource dataSource) {

        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:liquibase/" + MODULE_NAME + "/master.xml");
        liquibase.setDataSource(dataSource);
        liquibase.setContexts("development,test,production");
        liquibase.setShouldRun(liquibaseProperties.isEnabled());
        // 覆盖Liquibase changelog表名
        liquibase.setDatabaseChangeLogTable( MODULE_NAME + "_changelog");
        liquibase.setDatabaseChangeLogLockTable(MODULE_NAME + "_changelog_lock");
        log.info("Configuring Liquibase Success");
        return liquibase;
    }
}
