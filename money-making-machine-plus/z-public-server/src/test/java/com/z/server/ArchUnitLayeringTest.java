package com.z.server;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * 第 3 层守卫：分层 + 技术债规则，只放在聚合工程（z-public-server）测试里。
 * 模块间依赖边界归 Modulith（第 2 层），这里【不重复】模块规则。
 * 经验值：规则控制在 5~10 条，避免写成维护负担。
 *
 * 范围只限定【干净模块】：ui / report / generator —— 它们已被诊断无逆向依赖。
 * system / ai / screen 有 service→web 逆向依赖，等各自单独改造后再纳入本测试。
 *
 * 与 demo 的 DDD 版规则差异（本项目是扁平四层，非 DDD 五层）：
 *  - web 层允许直接碰 domain/repository（扁平结构，无 api/application 层）
 *  - 核心方向约束是"下层不得反依赖 web"（system/ai/screen 的违规类型）
 *  - 无 api 契约包，去掉 demo 的 api/infrastructure 规则
 */
@AnalyzeClasses(packages = {
        "com.z.module.ui",
        "com.z.module.report",
        "com.z.module.generator",
        "com.z.module.template"
})
class ArchUnitLayeringTest {

    // ==================== 分层规则（扁平四层方向） ====================

    /** 1. REST 入口（*Resource）只能待在 web 包 */
    @ArchTest
    static final ArchRule resource_in_web =
        classes().that().haveSimpleNameEndingWith("Resource")
                 .should().resideInAPackage("com.z.module..web..");

    /** 2. 下层（service/util/config/aop/enums）不得反向依赖 web 出口 */
    @ArchTest
    static final ArchRule lower_not_touch_web =
        noClasses().that().resideInAnyPackage("..service..", "..util..", "..config..", "..aop..", "..enums..")
                   .should().dependOnClassesThat().resideInAPackage("com.z.module..web..");

    /** 3. domain 是纯实体：不得依赖 web / service / util / aop */
    @ArchTest
    static final ArchRule domain_is_pure =
        noClasses().that().resideInAPackage("..domain..")
                   .should().dependOnClassesThat()
                   .resideInAnyPackage("com.z.module..web..", "com.z.module..service..", "com.z.module..util..", "com.z.module..aop..", "com.z.module..enums..");

    /** 4. repository 只依赖 domain：不反向依赖 web / service */
    @ArchTest
    static final ArchRule repository_stays_low =
        noClasses().that().resideInAPackage("..repository..")
                   .should().dependOnClassesThat()
                   .resideInAnyPackage("com.z.module..web..", "com.z.module..service..", "com.z.module..config..", "com.z.module..util..");

    // ==================== 技术债规则 ====================

    /** 5. 禁止 System.out（用日志框架 lombok @Slf4j） */
    @ArchTest
    static final ArchRule no_system_out =
        noClasses().should().callMethod(System.class, "out");

    /** 6. 禁止直接 new Date()（用 java.time） */
    @ArchTest
    static final ArchRule use_java_time =
        noClasses().should().callConstructor(java.util.Date.class);
}