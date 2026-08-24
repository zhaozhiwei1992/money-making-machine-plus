# 计划：旧版本依赖清理（commons-io / guava / redisson CVE 修复升级）

> 由 pi-plan 只读探索生成，未改动任何源码。执行用 /implement <本文件路径>。

任务来源：用户对话（"先进行旧版本清理"），二期依赖治理收尾后确认的 3 个低风险 CVE 相关升级项。

## 1. 背景与目标

一期/二期依赖治理已闭环（fastjson2/jjwt/liquibase 等）。本次清理遗留的 3 个**旧版本 + CVE 相关**依赖：

| 依赖 | 当前 | 目标 | 原因 |
|---|---|---|---|
| commons-io | 2.5（2016） | **2.16.1** | CVE-2021-29425（2.7 修复）、CVE-2024-47554（2.14 修复，SnappyInputStream 解压炸弹） |
| guava | 31.1-jre | **33.6.0-jre** | CVE-2023-2976（32.0 修复，FileBackedOutputStream 临时文件权限） |
| redisson | 3.21.1 | **3.52.0** | 3.x 最新稳定（2025-09），对齐 Boot 3.3 生态，含两年 bug/安全修复 |

验收标准：
- `z-dependencies/pom.xml` 三处版本生效，依赖树最终仅见新版（无 2.5 / 31.1-jre / 3.21.1 残留）
- 全量构建 `mvn clean package -T 1C` BUILD SUCCESS
- 受影响模块单测通过（z-common、z-starter-cache、z-module-code-generator）
- 应用启动 smoke：Redis 缓存链（redisson 8.27 需实际验证 spring-data 兼容，见风险节）

## 2. 现状（只读探索结果）

Maven 根：`money-making-machine-plus/`。版本管理集中在 `money-making-machine-plus/z-dependencies/pom.xml`（BOM，被父 pom import）。

**commons-io**
- BOM 段：`z-dependencies/pom.xml:47` 属性 `${commons.io.version}=2.5`、`:222-225` dependencyManagement 段
- 显式引用：`z-dependencies/../z-module-code-generator/pom.xml:24-25`（唯一模块引用）
- 代码面：仅 `org.apache.commons.io.IOUtils`（GeneratorService.java、GeneratorResource.java 两个文件，IOUtils API 2.5→2.16 无破坏）
- 无 CVE 风险来源传导除此外无其他显式引用

**guava**
- BOM 段：`z-dependencies/pom.xml:45` 属性 `${guava.version}=31.1-jre`、`:209-211` dependencyManagement 段
- 显式引用：`z-framework/z-common/pom.xml:88-89`（唯一显式引用，但传递链多处被 BOM 统一锁到 31.1-jre，含 grpc-core/rocketmq 链）
- 代码面（8 文件）：`com.google.common.collect` 的 ImmutableMap / Lists / Maps / Multimap（z-common 3 文件 + z-module-ai 2 文件 + z-module-screen 1 文件 + z-module-system 2 文件）。31→33 无这些 API 的破坏性变更
- ⚠️ 风险点：grpc-core 1.53.0 声明 guava 31.1-jre 为传递依赖，BOM 锁 31.1；升 33.6 后需构建 + 运行时验证（通常 guava 向前兼容良好）

**redisson**
- BOM 段：`z-dependencies/pom.xml:429-432` 硬编码 `<version>3.21.1</version>`（无属性）
- 显式引用：`z-framework/z-starter-cache/pom.xml:37-38`（redisson-spring-boot-starter）
- 代码面（2 文件）：`MultiCacheAutoConfiguration.java`（RedissonClient / Config / SingleServerConfig / JsonJacksonCodec / RedissonSpringCacheManager / CacheConfig）+ `CacheService.java`（RedissonCache）。这些 API 在 3.x 线中稳定
- 配置面：`z-public-server/src/main/resources/application.yml:247-248` 仅注释掉的 redisson 配置
- ⚠️ 风险点：redisson-spring-boot-starter 默认传递 redisson-spring-data-3X 模块，版本需与 Boot 3.3.3 的 spring-data-redis 3.3.x 对齐（Redisson 3.x 线本身支持 Boot 3.x，4.x 才对应 Boot 4）

版本可用性（本地 m2 / 内网 Nexus）：commons-io 2.16.1 **本地已缓存**；guava 33.6.0-jre Nexus 有 metadata（本地仅 33.0.0-jre 有 jar）；redisson 3.52.0 Nexus 有。

## 3. 方案与取舍

**⚖️ 方案 A：三步走逐项升级（推荐）**
- commons-io → guava → redisson 分 3 个独立 commit，每次构建 + 测试 + 树复核
- 成本：3 轮验证循环；维护：零额外；风险：最低（单依赖变更问题定位明确）；对外影响：无 API 变化
- 适用于全部三个依赖（均为纯版本提升，无代码改动）

**⚖️ 方案 B：一次 BOM 改完一起构建**
- 三处版本一次改完，一次全量构建
- 成本：低；但问题无法定位到具体依赖，违背"分批可回滚"纪律；不推荐

**⭐ 推荐方案：方案 A（三步走）**——与二期依赖治理批处理纪律一致，每步独立 commit 可回滚，任一依赖带起兼容问题（尤其 guava→grpc 链、redisson→spring-data 链）可精准回溯。

版本选择说明：
- commons-io 取 **2.16.1**（本地已缓存+覆盖全部已知 CVE；不追 2.21.0 最新，减少 Nexus 拉取失败面，如需更激进实施时可再评估）
- guava 取 **33.6.0-jre**（CVE-2023-2976 在 32.0 修复，33.6.0-jre 为 Nexus 可见最新稳定；CVE 修复 + 两年 bug 修复）
- redisson 取 **3.52.0**（3.x 线最新，2025-09；Boot 3.x 兼容线内。实施时验证其传递的 redisson-spring-data-3X 与 Boot 3.3 的 spring-data-redis 兼容，不兼容则回落到 3.40.x 附近 Boot 3.3 同期版本）

## 4. 实施步骤

1. **commons-io 2.5 → 2.16.1**：改 `money-making-machine-plus/z-dependencies/pom.xml:47` `${commons.io.version}` 为 2.16.1。可独立提交。
2. 验证步骤 1：`mvn -o clean package -T 1C`（z-public-server 汇总模块）+ `mvn -o dependency:tree -pl z-public-server -am | grep commons-io` 确认仅有 2.16.1；跑 z-module-code-generator 的 GeneratorService 相关测试（当前无单测则依赖编译主链）。⚠️ 若 offline 拉不到 2.16.1（本地已缓存，理论不会），联网一次 -U。
3. **guava 31.1-jre → 33.6.0-jre**：改 `z-dependencies/pom.xml:45` `${guava.version}` 为 33.6.0-jre。需先 `mvn dependency:get -Dartifact=com.google.guava:guava:33.6.0-jre -U` 拉包（本地无 jar）。可独立提交。
4. 验证步骤 3：全量 `mvn clean package -T 1C` + 树复核 `grep guava` 无 31.1-jre 残留 + 运行 z-common 相关单测（CollectionUtils/MapUtils 所在模块测试）。
5. **redisson 3.21.1 → 3.52.0**：改 `z-dependencies/pom.xml:431` 硬编码版本为 3.52.0（可顺手转为属性 `${redisson.version}` 保持 BOM 风格）。需先 `mvn dependency:get -Dartifact=org.redisson:redisson-spring-boot-starter:3.52.0 -U`。可独立提交。
6. 验证步骤 5：全量构建 + 树复核 `grep -i redisson` 仅 3.52.0 相关 + 核对传递的 redisson-spring-data 版本与 spring-data-redis(3.3.x) 不冲突（冲突时见风险与回滚）。
7. **收尾**：无需代码改动（三依赖均纯版本升级，API 无变化）；预期零 diff 于 Java 源码。若验证中发现编译错误（应不会），按错误处理。

依赖顺序：1→3→5 各自独立，无前置依赖；每步必须在进入下一步前通过验证。

## 5. 验证

- 构建：在 Maven 根 `money-making-machine-plus/` 执行 `mvn clean package -T 1C`（项目当前验证方式，勿自选其他命令）
- 依赖树复核：`mvn -o dependency:tree -pl z-public-server -am -DoutputFile=/tmp/tree.txt` 后 grep 三个老版本号，确认 0 残留；确认新版本生效
- 单测：受影响模块（z-common、z-starter-cache、z-module-code-generator）在 package 阶段自动运行；重点看 MultiCacheAutoConfiguration 相关启动测试（若存在）
- 手动验收标准回读：三依赖版本号生效 + 构建全绿
- 回归范围：全量 27 模块构建 + 依赖树核对，等价二期各批验证方式

## 6. 风险与回滚

- ⚠️ **guava 33 与 grpc-core 1.53 传递链兼容**：BOM 全局锁 33.6，grpc 声明 31.1 被覆盖。构建/单测可发现编译级问题；运行时兼容风险低（guava 向前兼容）。回滚：单步 `git revert` 该 commit。
- ⚠️ **redisson 3.52.0 spring-data 版本对齐**：starter 传递 redisson-spring-data-3X，若默认引用最新 spring-data（如 3.5+）与 Boot 3.3.3 的 spring-data-redis 3.3.x 冲突，Maven 就近原则会选较高版，可能 NoSuchMethod 运行时错。处置：树复核时确认生效 spring-data-redis 版本；冲突则回落 redisson 3.40.x（Boot 3.3 同期）并重验。
- ⚠️ **Nexus 拉取失败**：guava 33.6.0-jre / redisson 3.52.0 本地未缓存，首拉可能 HTTP 500（二期 liquibase 时遇过）。处置：`mvn dependency:get -U` 重试；最终回退 guava 33.0.0-jre（本地已缓存）。
- 🚫 禁止：不升级到 guava 34+/redisson 4.x（超出本项目 Boot 3.3.3 生态）；不改动任何 Java 代码（无必要）。
- 回滚基线：每步完成后由**用户自行 commit**（禁止代 commit），与二期纪律一致。

## 7. 交接给 /implement 的提示

实施时严格按"第 4 步"顺序逐步提交，每步完成跑"第 5 步"的对应验证；不要合并步骤、不要顺手升级其他依赖；验证失败先修再进下一步。