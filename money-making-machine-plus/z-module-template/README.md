# z-module-template（新业务模块标准骨架）

> ⚠️ 本模块是**模板/示例**，不是业务模块：只有 `api / service / web.rest / domain / repository`
> 五类文件 + package-info + verify 测试 + 单测 + 本 README，不存在真实业务。
> 它参与 Maven 全量构建与聚合 verify，用于演示"新模块天生过守卫"；
> 新接单时拷贝改名即得规范骨架。

## 用法（cp -r 改名三步）

```bash
cd <仓库根>/money-making-machine-plus
cp -r z-module-template z-module-<你的业务名>          # 1. 拷贝目录
# 2. 全局替换（源码 + pom + README）：
#    com.z.module.template      -> com.z.module.<你的业务名>
#    TemplateItem / TemplateDTO -> <你的实体名> / <你的实体DTO名>
#    tpl_t_item                 -> <你的表名>（表前缀 tpl_ 可改成业务缩写）
#    /template                  -> /<你的REST前缀>
# 3. 注册：
#    - 根 pom <modules> 追加 <module>z-module-<你的业务名></module>
#    - z-public-server/pom.xml 视需要添加依赖（不需要就不加）
```

改完跑：`mvn -o -pl z-module-<你的业务名> -am clean package`

## 结构约定（非核心 CRUD 模块）

| 包 | 职责 | 说明 |
|----|------|------|
| `api` | 对外契约层（DTO） | 只承载传输字段，不依赖 JPA 实体；跨模块被调用时配合 `@NamedInterface` 暴露 |
| `service` | 业务逻辑 | 构造器注入；DTO↔实体转换收敛于此，实体不泄漏到 REST 边界 |
| `web.rest` | REST 暴露 | 构造器注入 service；`@Operation` 中文描述；分页返回 `{list, total}`；删除收 idList |
| `domain` | JPA 实体 | 继承 `AbstractAuditingEntity`（自带审计字段）；表名 `tpl_` 前缀 |
| `repository` | Spring Data 仓库 | 派生查询 + `deleteAllByIdIn` |

- **分层方向**：web → service → repository/domain，api 是唯一对外通道。
- **不用 DDD 五层**：非核心 CRUD 模块 api/service/web 三层即可，跑全套 DDD 是过度设计。
- **模块声明**：根包 `package-info.java` 标 `@ApplicationModule`，启用 Modulith 边界校验
  （模块间依赖、环、字段注入）。只依赖基础件 z-common 时无需 `allowedDependencies`。

## 自带守卫

- **per-biz verify**：`TemplateModuleVerifyTest` —— 本模块内部结构自检，秒级完成。
- **聚合 verify**：z-public-server 的
  [`ModulithAggregateVerifyTest`](../z-public-server/src/test/java/com/z/server/ModulithAggregateVerifyTest.java)
  会把本模块当整体纳入校验（前提：已加入 z-public-server 依赖）。
- **ArchUnit 分层规则**：新增业务模块后，把包名加进
  [`ArchUnitLayeringTest`](../z-public-server/src/test/java/com/z/server/ArchUnitLayeringTest.java)
  的 `@AnalyzeClasses`（现扫 ui/report/generator 三个干净模块）。

## 验证命令

```bash
# 本模块专项（compile + verify + 单测）
mvn -o -pl z-module-template -am clean package
# 全量回归
mvn -o clean package
```

## 最小 CRUD 速览

REST 前缀 `/template`，实体 `TemplateItem`（模板条目）：

- `POST /template/items` 新建（body 不带 id）
- `PUT /template/items/{id}` 更新
- `GET /template/items` 分页查询（Pageable）
- `GET /template/items/{id}` 单查
- `DELETE /template/items` 批量删除（body: idList）

单测 `TemplateItemServiceTest` 演示"service 层纯 JUnit + Mockito 即可覆盖，无需 Spring 容器"。