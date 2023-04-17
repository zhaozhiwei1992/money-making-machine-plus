package com.example.config;

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
public class LiquibaseConfiguration {

    @Bean
    public SpringLiquibase liquibase(
            LiquibaseProperties liquibaseProperties,
            DataSource dataSource) {

        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:liquibase/master.xml");
        liquibase.setDataSource(dataSource);
        liquibase.setContexts("development,test,production");
        liquibase.setShouldRun(liquibaseProperties.isEnabled());
        log.info("Configuring Liquibase Success");
        return liquibase;
    }
}
