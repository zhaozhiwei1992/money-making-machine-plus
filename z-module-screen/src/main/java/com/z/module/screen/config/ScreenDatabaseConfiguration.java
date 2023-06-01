package com.z.module.screen.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories({ "com.z.module.screen.repository" })
@EntityScan({"com.z.module.screen.domain"})
public class ScreenDatabaseConfiguration {}
