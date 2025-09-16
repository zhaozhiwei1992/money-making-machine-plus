package com.z.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 项目的启动类
 *
 * 每个扩展模块可能出现类重复的情况, 所以要建立不同的包
 */
// 忽略 IDEA 无法识别 ${z.app.base-package}
@SuppressWarnings("SpringComponentScan")
@SpringBootApplication(scanBasePackages = {"${z.app.base-package}.server", "${z.app.base-package}.module"})
// 统一增加事务管理及 jpa的审计拦截
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class BootStrapServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootStrapServerApplication.class, args);
    }

}
