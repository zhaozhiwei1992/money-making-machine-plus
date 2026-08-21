# 计划：赚钱工具项目「模块化单体」规范改造·第一期（铺安全网 + 接单模板）

> 用户原始任务：通过项目测试新功能、以项目为基础接单加速开发（Java 为主），
> 结合单体架构设计（~/workspace/notes/编程/架构设计/单体架构/模块化单体最佳实践.org）谈改造方向，先规划、逐步处理。
> **硬性节奏**：每次改动要小、可 review；每步改完必须测试通过才进下一步；大工作先规划。
> 由 pi-plan 只读探索生成，未改动任何源码。执行用 /implement 本文件路径。

## 1. 背景与目标

**一句话目标**：给项目铺上「模块化单体」的边界安全网 + 一份可复制的接单模块模板，为后续存量改造和新单加速打底；**本期不做存量模块内部结构迁移**。

**验收标准（可验证）**：
- 干净模块（dynamic-ui / report / code-generator）可被 Modulith + ArchUnit 守卫，测试通过
- 根 pom 注释任一模块 → `mvn clean package` 仍过（可裁剪成立）
- 提供一份标准接单模块模板（分层目录 + package-info + verify 测试 + 最小 CRUD + README）
- 违规模块（system / ai / screen）本期**不修**，列入后续期待办清单
- 全程每次小改动 + 每步跑 `mvn` 测试通过 + 每步可独立提交/review

## 2. 现状（只读探索结果 + 模块健康度分类）

**仓库**：根 `/home/zhaozhiwei/workspace/money-making-machine-plus`（git），后端 Maven 工程 `money-making-machine-plus/`（下称 `<maven>`），前端 `z-ui-admin-vue3` 等。
技术栈：SpringBoot 3.3.3 / Java21 / JPA / liquibase / quartz / MapStruct / Lombok / SpringAI。

**Maven 骨架（已合格，不推翻）**：`z-dependencies`(BOM, parent-less) → `z-framework`(z-common + 13 starter) → 业务模块 → `z-public-server`(唯一 main `BootStrapServerApplication`)。liquibase 按模块分目录 ✓。

**模块内部结构**：扁平四层 `domain / repository / service / web`(+`config`/`aop`)，**无 api 契约、无 application/infrastructure 层**。

**只读依赖矩阵诊断（本次已实测）**——按下层是否反向依赖 web 分层：

| 模块 | 子包 | 下层→web 反向依赖 | 健康度 | 本期处置 |
|------|------|------------------|--------|---------|
| dynamic-ui (ui) | domain/web/config/repository | 0 | ✅ 干净 | 本期铺守卫 |
| report | domain/aop/util/service/web/enums/config/repository | 0 | ✅ 干净 | 本期铺守卫 |
| code-generator (generator) | service/web/config | 0（config 不反依赖） | ✅ 干净 | 本期铺守卫 |
| screen | domain/service/web/config/repository | service→web(5) | ❌ 违规 | 后续期修 |
| system | system/domain/aop/service/web/config/repository | service→web.vo(4)、aop→web.vo(2) | ❌ 违规 | 后续期修 |
| ai | framework/tools/domain/utils/service/web/enums/config/repository | service→web(27)、repository→web(1) | ❌ 最严重 | 后续期修 |

**为什么这样分类**：Modulith per-biz verify 只拦**环**（这些模块下层→web 均为单向，不构成环，能绿）；**ArchUnit 显式分层规则**（service/web 不得反向依赖 web）会立刻暴露 system/ai/screen 违规。故本期 ArchUnit 分层规则**只对干净模块生效**，违规模块留待各单独规划。

**复用资产**：root pom 已配 MapStruct 编译链；既有 `z-framework` starter 拆分模式；Modulith/ArchUnit 用法参考 `~/workspace/demo/modular-monolith-demo`；BOM 独立句法已正确。

## 3. 方案与取舍

### 目标两大诉求映射
- **测试新功能** → 干净模块加守卫后 = 安全试验场；新功能做成可裁剪模块
- **接单加速** → 接单模板 + 可裁剪 + 留 Feign 拆服务接口

### 备选方案

**方案 A：本期全量修 system/ai/screen（一次性把违规都改成 DDD）**
- 成本极高（120+145+23 文件搬迁）；风险极高；无法小步 review
- ⚠️ 不推荐：违背「每次改动小」硬性要求，且 AI 功能正在用

**方案 B：本期只铺守卫+模板，违规模块逐一后置（按用户选 (b)）⭐**
- 思路：dynamic-ui/report/code-generator 先加 guard（全绿）；system/ai/screen 只记入待办、本期不碰；ArchUnit 分层规则只扫干净模块；提供接单模板 + 可裁剪验证
- 成本：中等；维护成本低；风险低（几乎不动业务代码）
- 对外影响：功能零变化
- ⭐ 推荐：安全网先立起来，新单从第一天合规；违规模块在网内逐步处理，每个单独规划、单独提交、单独测试

**方案 C：只做模板不铺守卫**
- 成本最低，但无守卫，模板很快被带偏
- ⚠️ 不推荐

## 4. 实施步骤（第一期，每步独立可提交 + 改完即测）

> 每步结束都跑「第 5 步」对应验证，绿了才进下一步。每步改动都是小单元，可独立 review。

1. **BOM 加守卫依赖**：
   - 改 `z-dependencies/pom.xml`：加入 `spring-modulith-core`（compile，RUNTIME retention 惰性）、`archunit-junit5`（test scope）、`spring-modulith-starter-test`（test，可选）版本管理
   - 目的：让守卫依赖可用；scope 控制不污染运行时
   - 验证：`cd <maven> && mvn -pl z-module-dynamic-ui -am clean compile` 编译过
   - ⭐ 独立可提交

2. **给干净模块逐个加 package-info + per-biz verify（每模块一步）**：
   - 每个子步 = 一个模块：`<maven>/z-module-{dynamic-ui,report,code-generator}/src/main/java/com/z/module/<u|report|generator>/package-info.java`（`@ApplicationModule(allowedDependencies={"common","security","operate-log",...})`）+ `<X>ModuleVerifyTest.java`
   - 目的：建立每模块的 Modulith 模块声明与自检
   - 建议顺序：dynamic-ui → report → code-generator（逐个，不要合并）
   - 验证：`mvn -pl z-module-<x> -am test` 跑该模块 verify 绿
   - ⭐ 每个子步独立可提交

3. **z-public-server 加聚合 Modulith verify**：
   - 新建 `z-public-server/src/test/java/com/z/server/MonolithModulithVerifyTest.java`：`ApplicationModules.of("com.z").verify()`
   - 目的：跨模块 internal 泄漏、allowedDependencies 真实性一次性拦（通常只在 server 全模块齐时能验）
   - 依赖第 2 步
   - 验证：`mvn -pl z-public-server -am test`
   - ⭐ 独立可提交

4. **z-public-server 加 ArchUnit（先只开干净模块 + 技术债通用规则）**：
   - 新建 `z-public-server/src/test/java/com/z/server/ArchUnitLayeringTest.java`
   - 分层规则用 `@AnalyzeClasses(packages = {"com.z.module.ui","com.z.module.report","com.z.module.generator"})` **限定干净模块**，避免 system/ai/screen 一开就红
   - 技术债通用规则（禁 System.out / java.util.Date）可全类路径生效或同样限定，先保守限定干净模块
   - 目的：干净模块正式进入 DDD 分层约束
   - 验证：`mvn -pl z-public-server -am test`
   - ⭐ 独立可提交

5. **提供标准接单模块模板**：
   - 新增独立工程 `z-module-template/`（或以 `z-module-samples` 为基改造）：一份**非核心三层**标准模块（api/service/web + package-info + verify 测试 + 最小 CRUD + `README.md` 说明），包名占位 `com.z.module.template`
   - 目的：新单 `cp -r` 改名即得规范骨架（接单加速的核心产物）
   - 验证：`mvn -pl z-module-template -am clean package` 通过
   - ⭐ 独立可提交（可与 2~4 并行）

6. **可裁剪验证**：
   - 在根 pom 与 `z-public-server/pom.xml` 临时注释 `z-module-template`（或任一干净模块）+ 对应依赖，`mvn clean package` 应过；再验证 `mvn -pl z-public-server -am clean package` 只构建入口链
   - 目的：证明「注释即去模块」成立（接单裁剪的基础）
   - 依赖第 5 步；验证通过后恢复注释并提交
   - ⭐ 独立可提交

7. **建立后续期待办清单**（只写文档，不实施）：
   - 新建 `money-making-machine-plus/docs/architecture-roadmap.md` 或复用仓库根现有文档目录：记录 system / ai / screen 的分层违规详情与后续期建议（每模块一个后续 `/plan`）
   - 目的：把「未修的债」显式化，避免遗忘；为后续各单独规划提供入口
   - ⭐ 独立可提交

> 明确不在本期：改 system/ai/screen 内部结构、AI 瘦身、bpm/samples 清理、K8s 部署、-api 拆工程抽服务。均属后续期单独规划。

## 5. 验证

- 单步构建/测试（每步必跑）：
  - `cd <maven> && mvn -pl z-module-<x> -am clean test`（干净模块 verify）
  - `mvn -pl z-public-server -am clean test`（聚合 verify + ArchUnit）
  - `mvn -pl z-module-template -am clean package`（模板可构建）
- 全量回归（每 2~3 步或完成后跑）：`mvn clean package`
- 可裁剪：临时注释模块 + `mvn clean package`；`mvn -pl z-public-server -am clean package`
- 手动验收：`BootStrapServerApplication` 启动正常、admin/admin 登录、system 基础功能 + dynamic-ui 页面不被影响
- 回归重点：本期只加 package-info/测试/新模板工程，几乎不动既有业务代码，理论上零功能影响；重点确认编译与启动不破

## 6. 风险与回滚

- ⚠️ 风险：
  - 加 `spring-modulith-core` 编译依赖后，Modulith 可能报「子包被当作小模块的环」——但实测干净模块均单向无环，若个别报红即为真实违规被早发现，逐项修即可（本期只碰干净模块，可控）
  - ArchUnit 若误判（如 `web.vo` 归属），可能误报；先限定干净模块包扫描，再按报错微调规则
  - 模板工程若被误认为「又一个业务模块」被 mvn 全量构建，可能拖慢；故标记清楚为模板/示例
- 回滚：每步独立提交，`git revert <commit>` 单步回退；package-info/测试/模板均为增量，去掉即还原；步骤 4 若误伤改 `@AnalyzeClasses` 范围即可
- 🚫 禁止：本期改 system/ai/screen 源码、删 bpm/samples、动 AI 业务、做 DDD 存量迁移

## 7. 交接给 /implement 的提示

严格按「第 4 步」编号顺序逐条执行；**每完成一步**：跑对应验证 → 签名 `[DONE:n]` → 用户 review → 再进下一步，不跳步、不合批、不中途加码。违规模块的后续期只写入第 7 步文档，不实施。全部完成后建议对违规模块逐一新建独立 `/plan`。
