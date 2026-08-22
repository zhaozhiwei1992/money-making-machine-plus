# 计划：二期依赖治理（BOM 死配置清理 + 高风险依赖升级）

> 来源：`00_项目概览/06_技术选型评审与地基加固方案.org` 二期"依赖治理"路线；AI 依赖瘦身（08）之后的延续。
> 目标：清理 `z-dependencies/pom.xml`（BOM，601 行）中的死配置，并升级真正在用的过旧/高风险依赖（fastjson、JWT、liquibase、hibernate-validator）。
> 方法：只读排查已全部完成（模块 pom 引用面 + `mvn dependency:tree` 实际解析交叉验证）；以下清单中"死配置"均以**最终解析树中不存在**为准，未做该验证的一律归入 ⚠️ 观察项，不擅自删除。

## 产出

- [ ] 批 1：BOM 删死配置（含 08 残留 dashscope 声明）
- [ ] 批 2：删 BOM 中 hibernate-validator/hibernate 老声明，hibernate-validator 回落到 Boot BOM 管理的 8.0.1.Final
- [ ] 批 3：fastjson 1.2.83 → fastjson2（13 处 import / 6 文件包名迁移）
- [ ] 批 4：jjwt 0.10.5 → 0.12.6（3 文件破坏性 API 迁移 + 测试更新）
- [ ] 批 5：liquibase 4.6.1 → Boot 管理的 4.27.0（对齐）
- [ ] 每批 `mvn clean package` 全量验证通过

## 被治理项清单（已实测）

| 类别 | artifact（BOM 当前版本） | 实测证据 | 处置 |
|---|---|---|---|
| 😵 死配置 | org.hibernate:hibernate-core 5.6.4.Final | 实际解析 `org.hibernate.orm:hibernate-core:6.5.2.Final`（Boot BOM），老 groupId `org.hibernate` 无任何引用 | 删 BOM 声明 + `hibernate.version` property |
| 😵 死配置 | org.hibernate:hibernate-jpamodelgen 5.6.4 | 0 模块 pom 引用，解析树不存在 | 删除 |
| 😵 死配置 | commons-net 3.8.0 / guice 5.1.0 / easyexcel 3.2.1 / jsoup 1.15.4 / tika-core 2.7.0 / podam 7.2.11 / jedis-mock 1.0.7 / spring-boot-admin-starter-* 2.7.10 / wx-java-mp 4.3.0 / justauth 1.4.0 / captcha-plus 1.0.2 / ip2region 2.7.0 / xercesImpl 2.12.2 | 0 模块 pom 引用，解析树不存在 | 删除整段声明 |
| 😵 死配置 | com.alibaba:dashscope-sdk-java | 08 瘦身已删 z-starter-ai 引用，BOM 声明残留（475-478 行） | 删除 |
| 😵 死配置 | druid 2 段注释块 + `druid.version` | 声明全被注释，无生效实体 | 删注释块；property 若无引用一并删 |
| 🚧 版本错配 | org.hibernate.validator:hibernate-validator 6.2.5.Final | ⚠️ **并非死配置**：spring-boot-starter-validation 3.3.3 传递引入，被 BOM 强制锁 6.2.5（解析树证实 6.2.5:compile，属 jakarta 3 时代旧版）；Boot BOM 管理 8.0.1.Final | **删 BOM 声明**，让 Boot 接管 → 8.0.1（批 2） |
| 🚧 版本错配 | javax.validation:validation-api 2.0.1 | 出现在解析树，来源待核实（疑似 hibernate-validator 6.2.5 传递） | 随批 2 删 BOM 声明后复查解析树，仍残留则定位排除 |
| 🚧 过旧 | com.alibaba:fastjson 1.2.83 | 6 文件 13 处 import（z-starter-ai×1、z-starter-operate-log×1、z-module-ai×2、z-module-bpm×5 类）；1.2.x 已停更，autoType 安全补丁止步 | 迁移 fastjson2（批 3） |
| 🚧 过旧 | io.jsonwebtoken jjwt-api/impl/jackson 0.10.5 | z-starter-security 3 文件真实使用（JwtUtil/TokenProviderService/JWTAuthenticationFilter + JwtUtilTest）；0.10.x 多年未更，多 CVE | 升级 0.12.6 + API 迁移（批 4） |
| 🚧 过旧 | org.liquibase:liquibase-core 4.6.1 | 5~6 个模块 pom 显式引用；Boot BOM 管理 4.27.0 | 对齐 4.27.0（批 5） |
| ⚠️ 观察项 | io.netty:netty-all 4.1.90.Final | 0 模块 pom 显式引用，但解析树中存在（传递引入，BOM 锁定 4.1.90；Boot BOM 管 4.1.112.Final） | 纳入批 1 删除声明，验证树回落到 4.1.112；若回落异常则恢复声明并升 4.1.112 |
| ⚠️ 观察项 | org.apache.rocketmq:rocketmq-spring-boot-starter 2.3.1 + rocketmq-client 5.2.0 | 解析树中 starter 2.3.1 与 client 5.2.0 混搭（client 5.2.0 被 BOM 强制，超 starter 支持面） | 本期不动，标注：后续对齐 starter 自身 client 版本 |
| ⚠️ 观察项 | org.codehaus.jettison:jettison 1.5.4 | 0 模块引用，但为 f52c129 提交的**安全锁定锚点**（防止某传递依赖拉回 old 1.1）；删除可能让传递回落旧版 | 保留不动，标注原因 |
| ✅ 保留 | hutool-all×5、guava×2、velocity-engine-core×1（code-generator）、screw-core×1、jimureport×2、kaptcha×2、jsqlparser×6、flowable×1 | 均有模块 pom 显式引用且解析树存在 | 不动 |
| ✅ 保留 | commons-io 2.5 | 1 模块 pom 显式引用（z-starter-ai） | 本期不升（2.5 有 CVE-2024-47554，但非用户点名的三类；后续批次处理） |

## 实施方案（5 批，每批可独立提交、独立验证）

### 批 1：删死配置（最低风险）
1. `z-dependencies/pom.xml` 删除：dashscope-sdk-java 段、hibernate-core + hibernate-jpamodelgen 声明及 `hibernate.version` property、commons-net / guice / easyexcel / jsoup / tika-core / podam / jedis-mock / spring-boot-admin-* / wx-java-mp / justauth / captcha-plus / ip2region / xercesImpl 各段、druid 注释块（`druid.version` property 若无引用一并删）、netty-all 段。
2. 验证：`mvn clean package`（全量）+ `mvn dependency:tree` 确认上述 artifact 不再出现（netty-all 回落到 4.1.112.Final）；若 netty-all 回落失败恢复声明改 4.1.112。
3. 风险：极低；回滚 = 恢复被删段。

### 批 2：修 hibernate-validator 版本错配
1. `z-dependencies/pom.xml` 删除 `org.hibernate.validator:hibernate-validator:6.2.5.Final` 与 `javax.validation:validation-api:2.0.1` 声明（含相关 property）。
2. 验证：`mvn dependency:tree` 确认 hibernate-validator 回落 **8.0.1.Final**、jakarta.validation-api 3.0.2；`mvn clean package` 通过；若源码中用到 6.2.5 特有 API（概率低，@Valid/@Validated/自定义注解均兼容）则逐处调整。
3. 风险：低；验证树确认无 6.2.5 残留即可。

### 批 3：fastjson → fastjson2（13 处 import / 6 文件）
1. BOM：`com.alibaba:fastjson` 段替换为 `com.alibaba.fastjson2:fastjson2`（版本取 Nexus/npm 镜像最新 2.0.x，实施时查仓库；候选 2.0.53+）。
2. 代码：以下文件 `import com.alibaba.fastjson` → `com.alibaba.fastjson2`（逐个审 API 兼容）：
   - `z-framework/z-starter-ai/.../adapter/dify/service/DifyHttpServiceAdapter.java`
   - `z-framework/z-starter-operate-log/.../aop/RequestLoggingInterceptor.java`
   - `z-module-ai/.../service/workflow/AiWorkflowService.java`、`z-module-ai/.../service/SearchService.java`
   - `z-module-bpm/z-module-bpm-biz/.../domain/convert/JpaListStringJsonConverter.java`、`JpaMapJsonConverter.java`、`JpaSetLongJsonConverter.java`、`service/definition/BpmFormServiceImpl.java`、`BpmTaskAssignRuleServiceImpl.java`、`BpmModelServiceImpl.java`、`web/mapper/definition/BpmModelConvert.java`
3. 兼容要点：JSON/JSONObject/JSONArray/JSON.parseObject/JSON.toJSONString 基本同构；`@JSONField` 若被使用须改 `com.alibaba.fastjson2.annotation.JSONField`；toJSONString 序列化细节差异以相关单测兜底。
4. 验证：`mvn clean package`（含测试，重点 z-module-ai/bpm 相关测试）+ grep 确认无 `com.alibaba.fastjson`（非 fastjson2）import 残留。
5. 风险：中；fastjson2 序列化行为有细微差异，若单测暴露再逐处适配。

### 批 4：jjwt 0.10.5 → 0.12.6（破坏性 API 迁移）
1. BOM：`jjwt.version` 0.10.5 → 0.12.6（jjwt-api/impl/jackson 三件套同一版本）。
2. 代码迁移（0.10 → 0.12 破坏性变化大）：
   - `JwtUtil.java`、`TokenProviderService.java`：`Jwts.parser().setSigningKey(key)` → `Jwts.parser().verifyWith((SecretKey) key).build()`；`parseClaimsJws()` → `parseSignedClaims()`；`signWith(key, SignatureAlgorithm.HS256)` → `signWith(key, Jwts.SIG.HS256)`（0.12 移除 `SignatureAlgorithm` 枚举）。
   - `JWTAuthenticationFilter.java`：仅 import 异常类（ExpiredJwtException/MalformedJwtException/UnsupportedJwtException），正常无改。
   - `JwtUtilTest.java`：配套断言迁移（结构不变）。
3. 验证：`mvn clean package`（z-starter-security JwtUtilTest 通过）+ 全量编译。
4. 风险：中（API 破坏面集中在 2 文件，测试覆盖 generate/validate 双向，可兜底）。

### 批 5：liquibase 4.6.1 → 4.27.0（对齐 Boot BOM）
1. BOM：`liquibase.version` 4.6.1 → 4.27.0（Boot 3.3.3 BOM 管理值，4.6→4.27 跨 8 个 minor）。
2. 验证：`mvn clean package`；若项目带 liquibase changelog，启动应用验证 changelog 能正常 apply。⚠️ 4.20+ checksum 计算方式变化，**已存在的 databasechangelog 记录可能校验失败**（先例见 06 笔记：校验不过时从 databasechangelog 删记录重启 = 开发库重放；是否允许由用户定，不自动执行）。
3. 风险：**最高**——4.6 与 4.27 的 changelog 解析/checksum/SQL 生成行为差异大；供应商对象、扩展语法需实测。若启动失败且属 changelog 语法兼容问题，逐 changeSet 修或回滚此项，其余 4 批不受影响。
4. 回滚：单独恢复 `liquibase.version`。

## 验证方式（每批通用）

1. 构建：`cd /home/zhaozhiwei/workspace/money-making-machine-plus/money-making-machine-plus && mvn clean package`（含测试，无 ERROR/FAIL）。
2. 解析树：`mvn dependency:tree` 核对本批目标 artifact 的新版本/消失。
3. 残留扫描：grep 确认已删类无 import/引用残留。
4. 批 2/5 额外：启动 `z-public-server` 验证 hibernate-validator/liquibase 运行时正常（liquibase 至少 dry-run changelog）。

## 交接

- 每批验证绿后提示用户自行 `git add -A && git commit`（禁止代 commit）。
- 待用户确认的遗留（与本次无关）：application.yml openai 段 litellm 地址 `api.gptsapi.net` 占位。