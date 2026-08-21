/**
 * 报表模块。
 * 依赖基础件（z-common、z-starter-security、liquibase、jsqlparser、mysql），
 * 不引用任何其他业务模块（com.z.module.*），边界干净，无需 allowedDependencies。
 * 本声明启用 Spring Modulith 的模块边界校验（per-module verify）。
 */
@org.springframework.modulith.ApplicationModule(displayName = "报表模块")
package com.z.module.report;
