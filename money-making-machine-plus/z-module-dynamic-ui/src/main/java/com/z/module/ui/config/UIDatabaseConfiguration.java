package com.z.module.ui.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories({ "com.z.module.ui.repository" })
@EntityScan({"com.z.module.ui.domain"})
public class UIDatabaseConfiguration {}
