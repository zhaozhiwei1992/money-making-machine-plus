/**
 * 模板模块：新业务模块的规范骨架（cp -r 改名即得）。
 *
 * <p>分层：api（对外契约 DTO）→ service（业务逻辑）→ web/rest（REST 暴露），
 * domain/repository 为数据支撑。非核心 CRUD 模块无需 DDD 五层。
 *
 * <p>本声明启用 Spring Modulith 模块边界校验：模板只依赖基础件
 * （z-common），不引用任何其他业务模块（com.z.module.*），边界干净，
 * 无需 allowedDependencies。
 */
@org.springframework.modulith.ApplicationModule(displayName = "模板模块")
package com.z.module.template;