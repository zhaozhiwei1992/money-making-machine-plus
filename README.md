# 项目描述

一个基础性单体项目, 提供一些常用的功能, 主要是为了减少后续开发项目一些基础性工作(cv操作), 面向开发人员。

前端使用vue-element-plus-admin, 后端springboot2.7.6, 使用jpa进行持久化。

项目参考了jhipster的优秀代码设计, ruoyi项目的目录接口, 分模块开发。

# 体验地址

http://43.143.194.245:8091/index.html

用户/密码: admin/admin

# 项目特点

代码注释友好, 实现简单, 便于阅读, 推荐二次开发

前后端分离，使用 token 认证

支持菜单及按钮权限控制, 动态显示

前端采用 Vue3.x, 数据绑定代码简单，提高开发效率

支持角色+菜单的数据权限控制, 按照规则配置好后，程序自动控制

使用 liquibase 进行数据库版本控制, 空库只需创建数据库然后启动服务即可

使用 quartz 定时任务，可动态完成任务的添加、修改、删除、暂停、恢复及日志查看等功能

使用 swagger 查看或测试后端接口

使用 jpa 做数据库操作, 理论上支持大部分关系数据库

# 功能模块

## 基础数据

- [ ] 部门管理

- [x] 用户管理

- [x] 角色管理

- [x] 基础数据维护

## 菜单管理

- [x] 菜单管理

- [ ] 动态表单

- [ ] 采集表

## 系统管理

- [ ] 功能权限

- [x] 数据权限

- [x] 流程管理

- [x] 缓存管理

- [x] 定时任务

- [x] 系统参数

## 审计查询

## 系统监控

- [x] 在线人员监控

- [x] 日志管理

- [ ] 资源监控

- [ ] 服务状态

- [ ] 缓存状态

## 首页显示

- [ ] 待办事项

- [ ] 通知公告

## 国际化

- [x] 国际化支持

## 开发者工具

- [x] 代码生成

- [x] 大屏显示

- [x] 报表制作


# 安装要求

1. java 8+
2. springboot 2.6.3.RELEASE
3. mysql 5.7.5+
5. npm 6.14.4+
6. node 16.13.1+

# 安装步骤

1. git clone 当前项目到你喜欢的目录
2. 用你喜欢的 ide 引入该项目,并加载好依赖
3. 创建好数据库 database, 默认 money_making_mache_plus
5. 启动项目: 后端运行com.z.server.BootStrapServerApplication, 前端进入z-ui-admin-vue3目录, 执行pnpm run dev(先构建)
6. 访问http://localhost:4000, 登录查看我们的成果, 用户/密码:admin/admin

# 部署

# 版本控制

该项目使用 git 进行版本管理。您可以在 tags 参看当前可用版本。

# 参考

https://github.com/YunaiV/ruoyi-vue-pro.git

https://github.com/kailong321200875/vue-element-plus-admin.git

# 版权说明
