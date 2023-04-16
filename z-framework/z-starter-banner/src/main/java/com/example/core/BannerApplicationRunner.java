package com.example.core;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
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
                    "https://doc.iocoder.cn/api-doc/",
                    "https://doc.iocoder.cn");

            // 数据报表
            if (isNotPresent("cn.iocoder.yudao.module.report.framework.security.config.SecurityConfiguration")) {
                System.out.println("[报表模块 yudao-module-report - 已禁用][参考 https://doc.iocoder.cn/report/ 开启]");
            }
            // 工作流
            if (isNotPresent("cn.iocoder.yudao.framework.flowable.config.YudaoFlowableConfiguration")) {
                System.out.println("[工作流模块 yudao-module-bpm - 已禁用][参考 https://doc.iocoder.cn/bpm/ 开启]");
            }
        });
    }

    private static boolean isNotPresent(String className) {
        return !ClassUtils.isPresent(className, ClassUtils.getDefaultClassLoader());
    }

}
