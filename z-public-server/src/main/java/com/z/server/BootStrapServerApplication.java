package com.z.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 *
 * 每个扩展模块可能出现类重复的情况, 所以要建立不同的包
 */
// 忽略 IDEA 无法识别 ${z.app.base-package}
@SuppressWarnings("SpringComponentScan")
@SpringBootApplication(scanBasePackages = {"${z.app.base-package}.server", "${z.app.base-package}.module"})
public class BootStrapServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BootStrapServerApplication.class, args);
    }

}
