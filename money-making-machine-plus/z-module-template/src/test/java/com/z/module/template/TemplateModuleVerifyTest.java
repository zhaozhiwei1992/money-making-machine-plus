package com.z.module.template;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * 第 2 层守卫（per-biz）：模板模块内部结构自检。
 * <p>
 * 校验：模块边界（不引用其他业务模块）、依赖方向、环依赖。
 * 新模块拷贝改名后保留本测试，即自带模块自检能力。
 */
class TemplateModuleVerifyTest {

    @Test
    void verifyModuleStructure() {
        ApplicationModules.of("com.z.module.template").verify();
    }
}