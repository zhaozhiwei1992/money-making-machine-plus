package com.z.module.ai.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories({ "com.z.module.ai.repository" })
@EntityScan({"com.z.module.ai.domain"})
public class AIDatabaseConfiguration {}
