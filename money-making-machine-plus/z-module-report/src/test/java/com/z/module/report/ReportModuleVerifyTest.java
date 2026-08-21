package com.z.module.report;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * 第 2 层守卫（per-biz）：报表模块内部结构自检。
 * 只扫描 com.z.module.report 包，秒级完成，失败信息贴近本模块。
 * 校验：模块边界（不引用其他业务模块）、依赖方向、环依赖。
 */
class ReportModuleVerifyTest {

    @Test
    void verifyModuleStructure() {
        ApplicationModules.of("com.z.module.report").verify();
    }
}
