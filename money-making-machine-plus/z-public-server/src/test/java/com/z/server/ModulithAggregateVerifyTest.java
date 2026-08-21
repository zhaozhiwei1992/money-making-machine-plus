package com.z.server;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * 第 2 层守卫（聚合）：校验所有业务模块（com.z.module.*）之间的边界。
 * 只有服务器聚合模块把所有业务模块带上类路径，才能做全量模块间校验：
 *  - 模块间 artifact 依赖（如 dynamic-ui 依赖 system）必须显式 allowedDependencies 声明
 *  - 引用了不存在的模块 / 命名接口 → 抛 Violations
 * per-module verify（各业务模块内）只管内部结构，拦不了跨模块（本项目扁平模块主要在聚合层守卫）。
 */
class ModulithAggregateVerifyTest {

    @Test
    void verifyAllBusinessModules() {
        var modules = ApplicationModules.of("com.z.module");
        modules.forEach(m ->
            System.out.println("[module] " + m.getName() + " -> " + m.getBasePackage()));
        modules.verify(); // 有违规直接抛异常
    }
}
