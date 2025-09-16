package com.z.framework.banner.core;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.ClassUtils;

import java.util.concurrent.TimeUnit;

/**
 * @Title: BannerApplicationRunner
 * @Package com/example/core/BannerApplicationRunner.java
 * @Description: 自定义项目启动输出
 * @author zhaozhiwei
 * @date 2023/4/16 下午9:42
 * @version V1.0
 */
@Slf4j
public class BannerApplicationRunner implements ApplicationRunner {

    @Value("${spring.application.name}")
    private String appName;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ThreadUtil.execute(() -> {
            // 延迟 1 秒，保证输出到结尾
            ThreadUtil.sleep(1, TimeUnit.SECONDS);
            log.info("\n----------------------------------------------------------\n\t" +
                            "项目启动成功！\n\t" +
                            "接口文档: \t{} \n\t" +
                            "开发文档: \t{} \n" +
                            "----------------------------------------------------------",
                    "https://github.com/zhaozhiwei1992/" + appName,
                    "https://github.com/zhaozhiwei1992/" + appName);

            // 工作流
            if (isNotPresent("com.z.module.framework.flowable.config.FlowableConfiguration")) {
//                System.out.println("[工作流模块 yudao-module-bpm - 已禁用][参考 https://doc.iocoder.cn/bpm/ 开启]");
            }
        });
    }

    private static boolean isNotPresent(String className) {
        return !ClassUtils.isPresent(className, ClassUtils.getDefaultClassLoader());
    }

}
