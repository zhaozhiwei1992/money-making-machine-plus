package com.z.module.report.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories({ "com.z.module.report.repository" })
@EntityScan({"com.z.module.report.domain"})
public class ReportDatabaseConfiguration {}
