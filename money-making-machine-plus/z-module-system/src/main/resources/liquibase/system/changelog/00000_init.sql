--liquibase formatted sql
--changeset zzw:00000

-- 判断表不存在先创建
-- 用户表
CREATE TABLE if not exists sys_user (
	id bigint primary key auto_increment,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	activated bool NOT NULL,
	image_url varchar(256) NULL,
	login varchar(50) NOT NULL,
	name varchar(50) NULL,
	password_hash varchar(60) NOT NULL
);

CREATE TABLE if not exists sys_authority (
	id bigint primary key auto_increment,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	code varchar(50) NOT NULL,
	name varchar(50) NOT NULL
);

CREATE TABLE if not exists sys_user_authority (
	id bigint primary key auto_increment,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	user_id int8 NOT NULL,
	role_id int8 NOT NULL
);

CREATE TABLE if not exists sys_menu (
	id bigint primary key auto_increment,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	config varchar(255) NULL,
	enabled bool NULL,
	icon_cls varchar(255) NULL,
	keep_alive bool NULL,
	name varchar(255) NOT NULL,
	order_num int4 NULL,
	parent_id int8 NOT NULL,
	require_auth bool NULL,
	url varchar(255) NOT NULL,
	component varchar(255) NOT NULL,
    menu_type  int(1)                default 1       comment '菜单类型（1菜单 2按钮）',
    permission_code  varchar(100)     default null     comment '权限标识'
);


CREATE TABLE if not exists sys_param (
	id bigint primary key auto_increment,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	code varchar(255) NULL,
	enable bool NULL,
	name varchar(255) NULL,
	remark varchar(255) NULL,
	value varchar(255) NULL
);

-- 权限
CREATE TABLE if not exists sys_permissions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  code varchar(64)     not null                    comment '权限编码',
  name VARCHAR(255) NOT NULL UNIQUE,
  type int(1) NOT NULL                            comment '权限类型,1:数据权限',
  created_by varchar(50) NULL,
  created_date timestamp NULL,
  last_modified_by varchar(50) NULL,
  last_modified_date timestamp NULL,
  description VARCHAR(255)
);

delete from sys_permissions;
INSERT INTO sys_permissions (
  code, name, type, created_by, created_date, last_modified_by, last_modified_date, description
) VALUES
('ALL_DATA', '全部数据权限', 1, 'system', NOW(), 'system', NOW(), '访问所有数据的权限'),
('CUSTOM_DATA', '自定义数据权限', 1, 'system', NOW(), 'system', NOW(), '根据自定义规则访问数据的权限'),
('DEPT_DATA', '本部门数据权限', 1, 'system', NOW(), 'system', NOW(), '只能访问本部门数据的权限'),
('DEPT_SUB_DATA', '本部门及以下数据权限', 1, 'system', NOW(), 'system', NOW(), '可以访问本部门及下属部门数据的权限');

-- 岗位
create table if not exists sys_positions
(
  id INT AUTO_INCREMENT PRIMARY KEY                        comment '岗位ID',
  code     varchar(64)     not null                        comment '岗位编码',
  name     varchar(50)     not null                        comment '岗位名称',
  order_num     int(4)          not null                   comment '显示顺序',
  activated bool NOT NULL                                  comment '状态（1正常 0停用）',
  created_by varchar(50) NULL,
  created_date timestamp NULL,
  last_modified_by varchar(50) NULL,
  last_modified_date timestamp NULL,
  remark        varchar(500)    default null               comment '备注'
) engine=innodb comment = '岗位信息表';

-- 初始化岗位数据
delete from sys_positions;
INSERT INTO sys_positions (
  code, name, order_num, activated,
  created_by, created_date, last_modified_by, last_modified_date,
  remark
) VALUES
('CEO', '总裁', 1, true,'system', NOW(), 'system', NOW(),'公司最高执行官'),
('VP', '副总裁', 2, true,'system', NOW(), 'system', NOW(),'公司高级管理职位'),
('CEO', 'CEO', 3, true,'system', NOW(), 'system', NOW(),'负责公司的整体运营和管理'),
('PM', '项目经理', 4, true,'system', NOW(), 'system', NOW(),'负责项目的规划、执行和监控'),
('DM', '开发经理', 5, true,'system', NOW(), 'system', NOW(),'负责开发团队的管理工作'),
('DEV', '开发工程师', 6, true,'system', NOW(), 'system', NOW(),'负责软件产品的设计和开发工作'),
('TEST', '测试工程师', 7, true,'system', NOW(), 'system', NOW(),'负责软件产品的质量保证和测试工作');

-- 部门
create table if not exists sys_departments (
  id INT AUTO_INCREMENT PRIMARY KEY comment '部门id',
  parent_id         int             default 0                  comment '父部门id',
  name              varchar(50)     default ''                 comment '部门名称',
  order_num         int(4)          default 0                  comment '显示顺序',
  leader            varchar(20)     default null               comment '负责人',
  phone             varchar(11)     default null               comment '联系电话',
  email             varchar(50)     default null               comment '邮箱',
  activated bool NOT NULL           default true               comment '部门状态（1正常 0停用）',
  created_by varchar(50) NULL,
  created_date timestamp NULL,
  last_modified_by varchar(50) NULL,
  last_modified_date timestamp NULL
) comment = '部门表';

-- ----------------------------
-- 初始化-部门表数据
-- ----------------------------
delete from sys_departments;
INSERT INTO sys_departments (
  id, parent_id, name, order_num, leader, phone, email, activated,
  created_by, created_date, last_modified_by, last_modified_date
) VALUES
(1, 0, 'xx科技', 1, 'CEO', '13800000000', 'ceo@xx.com', true, 'system', NOW(), 'system', NOW()),
(2, 1, '北京总部', 1, '总部经理', '13700000001', 'headquarters@xx.com', true, 'system', NOW(), 'system', NOW()),
(3, 2, '研发部', 1, '研发经理', '13700000010', 'r&d@xx.com', true, 'system', NOW(), 'system', NOW()),
(4, 2, '市场部', 2, '市场经理', '13700000011', 'marketing@xx.com', true, 'system', NOW(), 'system', NOW()),
(5, 2, '测试部', 3, '测试经理', '13700000012', 'testing@xx.com', true, 'system', NOW(), 'system', NOW()),
(6, 2, '产品部', 4, '产品经理', '13700000013', 'product@xx.com', true, 'system', NOW(), 'system', NOW()),
(7, 2, '运维部门', 5, '运维经理', '13700000014', 'ops@xx.com', true, 'system', NOW(), 'system', NOW()),
(8, 1, '山西分公司', 2, '分公司经理', '13700000015', 'shanxi@xx.com', true, 'system', NOW(), 'system', NOW()),
(9, 1, '浙江分公司', 3, '分公司经理', '13700000016', 'zhejiang@xx.com', true, 'system', NOW(), 'system', NOW()),
(10, 8, '销售部门', 1, '销售经理', '13700000017', 'sales@xx.com', true, 'system', NOW(), 'system', NOW()),
(11, 8, '财务部', 2, '财务经理', '13700000018', 'finance@xx.com', true, 'system', NOW(), 'system', NOW()),
(12, 8, '人力资源部', 3, '人事经理', '13700000019', 'hr@xx.com', true, 'system', NOW(), 'system', NOW()),
(13, 8, '项目管理部', 4, '项目经理', '13700000020', 'project@xx.com', true, 'system', NOW(), 'system', NOW());

alter table sys_departments auto_increment=15;

-- ----------------------------
-- 用户角色初始化
-- ----------------------------
delete from sys_user;
INSERT INTO sys_user
(id, created_by, created_date, last_modified_by, last_modified_date, activated, image_url, login, name, password_hash)
VALUES
(1, 'system', '2022-07-14 11:25:56.594', 'system', '2022-07-27 09:05:07.221', true, NULL, 'admin', '系统管理员', '$2a$10$Uaq/uIj3D5VZ4Y5.I7MTB.pMXka6FKuCNy4A.ZnnRk9GshwYxBQZG'),
(2, 'system', '2022-07-14 11:27:02.515', 'system', '2022-08-18 15:42:42.015', true, NULL, 'user', '普通用户', '$2a$10$aQ9NOy/S2.UiNAmxOwNcjueSrRbwnYnUBbED.LE/DePeAeddPGCA.');

-- 初始化序列
alter table sys_user auto_increment=4;

delete from sys_authority;
-- 初始化角色数据
INSERT INTO sys_authority
(created_by, created_date, last_modified_by, last_modified_date, code, name)
VALUES
('system', NOW(), 'system', NOW(), 'ROLE_ADMIN', '管理员'),
('system', NOW(), 'system', NOW(), 'ROLE_SYSTEM_OPS', '系统运维人员'),
('system', NOW(), 'system', NOW(), 'ROLE_DEVELOPER', '开发人员'),
('system', NOW(), 'system', NOW(), 'ROLE_TESTER', '测试人员'),
('system', NOW(), 'system', NOW(), 'ROLE_GUEST', '访客'),
('system', NOW(), 'system', NOW(), 'ROLE_SALES', '销售');

delete from sys_user_authority;
INSERT INTO sys_user_authority
(user_id, role_id)
VALUES(1, 1);
INSERT INTO sys_user_authority
(user_id, role_id)
VALUES(2, 5);

-- ----------------------------
-- 菜单初始化
-- ----------------------------
delete from sys_menu;

INSERT INTO sys_menu (
  id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component, menu_type, permission_code
) VALUES
-- 一级菜单
(1, 'system', NOW(), 'system', NOW(), NULL, TRUE, 'ant-design:home-filled', TRUE, '首页',1, 0, TRUE, '/dashboard', '#', '0', 'dashboard:'),
(2, 'system', NOW(), 'system', NOW(), NULL, TRUE, 'ant-design:setting-filled', TRUE, '系统管理',2, 0, TRUE, '/system', '#', '0', 'system:'),
(3, 'system', NOW(), 'system', NOW(), NULL, TRUE, 'ant-design:code-outlined', TRUE, '开发者工具',3, 0, TRUE, '/tool', '#', '0', 'tool:'),
(4, 'system', NOW(), 'system', NOW(), NULL, TRUE, 'ant-design:dashboard-filled', TRUE, '系统监控',4, 0, TRUE, '/monitor', '#', '0', 'monitor:'),
(5, 'system', NOW(), 'system', NOW(), NULL, TRUE, 'ant-design:file-text-filled', TRUE, '动态报表',5, 0, TRUE, '/report', '#', '0', 'log:'),
(6, 'system', NOW(), 'system', NOW(), NULL, TRUE, 'ant-design:file-text-filled', TRUE, '日志查看',6, 0, TRUE, '/log', '#', '0', 'log:'),
(7, 'system', NOW(), 'system', NOW(), NULL, TRUE, 'ant-design:ellipsis', TRUE, '流程管理', 7, 0, TRUE, '/bpm', '#', '0', 'bpm:'),
(8, 'system', NOW(), 'system', NOW(), NULL, TRUE, 'ant-design:eye-outlined', TRUE, 'demo演示',8, 0, TRUE, '/demo', '#', '0', 'demo:'),
-- 二级菜单
(10, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '分析页', 1, 1, TRUE, 'analysis', 'views/Dashboard/Analysis', '1', 'dashboard:analysis:view'),
(11, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '工作台', 2, 1, TRUE, 'workplace', 'views/Dashboard/Workplace', '1', 'dashboard:workplace:view'),
(15, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '用户管理', 1, 2, TRUE, 'user', 'views/system/User/Index', '1', 'system:user:view'),
(16, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '角色管理', 2, 2, TRUE, 'role', 'views/system/Role/Index', '1', 'system:role:view'),
(17, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '菜单管理', 3, 2, TRUE, 'menu', 'views/system/Menu/Index', '1', 'system:menu:view'),
(18, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '岗位管理', 4, 2, TRUE, 'post', 'views/system/Post/Index', '1', 'system:post:view'),
(19, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '部门管理', 5, 2, TRUE, 'dept', 'views/system/Dept/Index', '1', 'system:dept:view'),
(20, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '系统参数配置', 6, 2, TRUE, 'params', 'views/system/Param/Index', '1', 'system:params:view'),
(21, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '基础要素维护', 7, 2, TRUE, 'ele-union', 'views/system/EleUnion/Index', '1', 'system:ele-union:view'),
(22, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '定时任务管理', 8, 2, TRUE, 'task', 'views/framework/job/Index', '1', 'system:task:view'),
(23, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '通知公告', 9, 2, TRUE, 'notice', 'views/system/Notice/Index', '1', 'system:notice:view'),
(50, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '登录日志', 1, 6, TRUE, 'login', 'views/system/LoginLog/Index', '1', 'log:login:view'),
(51, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '接口请求日志', 2, 6, TRUE, 'request', 'views/system/RequestLog/Index', '1', 'log:request:view'),
(52, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '定时任务日志', 3, 6, TRUE, 'task', 'views/framework/job/TaskLog/Index', '1', 'log:task:view'),
(70, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '代码生成',1, 3, TRUE, 'code/generator', 'views/CodeGenerator/Index', '1', 'tool:code:generator:view'),
(71, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '接口测试swagger',2, 3, TRUE, 'swagger', 'views/system/Swagger/Index', '1', 'tool:swagger:view'),
(72, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '接口测试doc',3, 3, TRUE, 'knife4j-doc', 'views/system/Knife4jDoc/Index', '1', 'tool:knife4j-doc:view'),
(90, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '在线用户',1, 4, TRUE, 'online/users', 'views/system/OnLine/Index', '1', 'monitor:online:user:view'),
(91, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '服务监控',2, 4, TRUE, 'server', 'views/framework/monitor/ServerInfoIndex', '1', 'monitor:server:view'),
(92, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '缓存监控',3, 4, TRUE, 'cache', 'views/framework/monitor/CacheListIndex', '1', 'monitor:cache:view'),
(63, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '积木报表配置', 1, 5, TRUE, 'jimu', 'views/report/JMReport/view', '1', 'report:jimu:view'),
(64, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'xx报表', 1, 5, TRUE, 'jimu', 'views/report/JMReport/view', '1', 'report:jimu:view'),
(80, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExamplePage', 1, 8, TRUE, 'example', 'views/Example/Page/ExamplePage', '1', 'demo:example:view'),
(81, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleAdd', 2, 8, TRUE, 'example/add', 'views/Example/Page/ExampleAdd', '1', 'demo:example:add'),
(82, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleDetail', 3, 8, TRUE, 'example/detail', 'views/Example/Page/ExampleDetail', '1', 'demo:example:detail'),
(83, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleEdit', 4, 8, TRUE, 'example/edit', 'views/Example/Page/ExampleEdit', '1', 'demo:example:edit'),
(84, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleDialog', 5, 8, TRUE, 'dialog', 'views/Example/Dialog/ExampleDialog', '1', 'demo:dialog:view'),
(85 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleDefaultForm', 6, 8, TRUE, 'form/default', 'views/Components/Form/DefaultForm', '1', 'demo:form:default:view'),
(86 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleRefForm', 7, 8, TRUE, 'form/ref', 'views/Components/Form/RefForm', '1', 'demo:form:ref:view'),
(87 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleUseForm', 8, 8, TRUE, 'form/use', 'views/Components/Form/UseFormDemo', '1', 'demo:form:use:view'),
(88 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleDefaultTable', 9, 8, TRUE, 'table/default', 'views/Components/Table/DefaultTable', '1', 'demo:table:default:view'),
(89 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleRefTable', 10, 8, TRUE, 'table/ref', 'views/Components/Table/RefTable', '1', 'demo:table:ref:view'),
(118 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleUseTable', 11, 8, TRUE, 'table/use', 'views/Components/Table/UseTableDemo', '1', 'demo:table:use:view'),
(119 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleDefaultEditTable', 12, 8, TRUE, 'edit/table/default', 'views/Components/EditTable/DefaultEditTable', '1', 'demo:edit:table:default:view'),
(120 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleEditor', 13, 8, TRUE, 'editor', 'views/Components/Editor/Editor', '1', 'demo:editor:view'),
(93 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleDemo1', 14, 8, TRUE, 'demo1', 'views/Components/UI/Demo1', '1', 'demo:demo1:view'),
(94 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleDemo2', 15, 8, TRUE, 'demo2', 'views/Components/UI/Demo2', '1', 'demo:demo2:view'),
(95 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleDemo3', 16, 8, TRUE, 'demo3', 'views/Components/UI/Demo3', '1', 'demo:demo3:view'),
(96 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleCountTo', 17, 8, TRUE, 'count/to', 'views/Components/CountTo', '1', 'demo:count:to:view'),
(97 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleDescriptions', 18, 8, TRUE, 'descriptions', 'views/Components/Descriptions', '1', 'demo:descriptions:view'),
(98 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleDialog2', 19, 8, TRUE, 'dialog2', 'views/Components/Dialog', '1', 'demo:dialog2:view'),
(99 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleEchart', 20, 8, TRUE, 'echart', 'views/Components/Echart', '1', 'demo:echart:view'),
(100 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleHighlight', 21, 8, TRUE, 'highlight', 'views/Components/Highlight', '1', 'demo:highlight:view'),
(101 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleIcon', 22, 8, TRUE, 'icon', 'views/Components/Icon', '1', 'demo:icon:view'),
(102 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleImageViewer', 23, 8, TRUE, 'image/viewer', 'views/Components/ImageViewer', '1', 'demo:image:viewer:view'),
(103 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleInfotip', 24, 8, TRUE, 'infotip', 'views/Components/Infotip', '1', 'demo:infotip:view'),
(104 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleInputPassword', 25, 8, TRUE, 'input/password', 'views/Components/InputPassword', '1', 'demo:input:password:view'),
(105 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleQrcode', 26, 8, TRUE, 'qrcode', 'views/Components/Qrcode', '1', 'demo:qrcode:view'),
(106 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleSearch', 27, 8, TRUE, 'search', 'views/Components/Search', '1', 'demo:search:view'),
(107 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleSticky', 28, 8, TRUE, 'sticky', 'views/Components/Sticky', '1', 'demo:sticky:view'),
(108 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'Example403', 29, 8, TRUE, '403', 'views/Error/403', '1', 'demo:403:view'),
(109 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'Example404', 30, 8, TRUE, '404', 'views/Error/404', '1', 'demo:404:view'),
(110 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'Example500', 31, 8, TRUE, '500', 'views/Error/500', '1', 'demo:500:view'),
(111 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleGuide', 32, 8, TRUE, 'guide', 'views/Guide/Guide', '1', 'demo:guide:view'),
(112 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleUseCrudSchemas', 33, 8, TRUE, 'use-crud-schemas', 'views/hooks/useCrudSchemas', '1', 'demo:use-crud-schemas:view'),
(113 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleUseWatermark', 34, 8, TRUE, 'use-watermark', 'views/hooks/useWatermark', '1', 'demo:use-watermark:view'),
(115 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleMenu2', 36, 8, TRUE, 'menu2', 'views/Level/Menu2', '1', 'demo:menu2:view'),
(116 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleMenu12', 37, 8, TRUE, 'menu12', 'views/Level/Menu12', '1', 'demo:menu12:view'),
(117 , 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, 'ExampleMenu111', 38, 8, TRUE, 'menu111', 'views/Level/Menu111', '1', 'demo:menu111:view');


-- 用户管理按钮
INSERT INTO sys_menu (
  id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component, menu_type, permission_code
) VALUES
(201, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '新增', 1, 15, TRUE, 'system/user/add', '#', '2', 'system:user:add'),
(202, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '编辑', 2, 15, TRUE, 'system/user/edit', '#', '2', 'system:user:edit'),
(203, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '详情', 3, 15, TRUE, 'system/user/detail', '#', '2', 'system:user:detail'),
(204, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '删除', 4, 15, TRUE, 'system/user/delete', '#', '2', 'system:user:delete'),
(205, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '批量删除', 5, 15, TRUE, 'system/user/batch-delete', '#', '2', 'system:user:batchDelete');

-- 角色管理按钮
INSERT INTO sys_menu (
  id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component, menu_type, permission_code
) VALUES
(251, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '新增', 1, 16, TRUE, 'system/role/add', '#', '2', 'system:role:add'),
(252, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '编辑', 2, 16, TRUE, 'system/role/edit', '#', '2', 'system:role:edit'),
(253, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '详情', 3, 16, TRUE, 'system/role/detail', '#', '2', 'system:role:detail'),
(254, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '删除', 4, 16, TRUE, 'system/role/delete', '#', '2', 'system:role:delete'),
(255, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '批量删除', 5, 16, TRUE, 'system/role/batch-delete', '#', '2', 'system:role:batchDelete');

-- 菜单管理按钮
INSERT INTO sys_menu (
  id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component, menu_type, permission_code
) VALUES
(301, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '新增', 1, 17, TRUE, 'system/menu/add', '#', '2', 'system:menu:add'),
(302, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '编辑', 2, 17, TRUE, 'system/menu/edit', '#', '2', 'system:menu:edit'),
(303, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '详情', 3, 17, TRUE, 'system/menu/detail', '#', '2', 'system:menu:detail'),
(304, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '删除', 4, 17, TRUE, 'system/menu/delete', '#', '2', 'system:menu:delete'),
(305, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '批量删除', 5, 17, TRUE, 'system/menu/batch-delete', '#', '2', 'system:menu:batchDelete');

-- 岗位管理按钮
INSERT INTO sys_menu (
  id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component, menu_type, permission_code
) VALUES
(351, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '新增', 1, 18, TRUE, 'system/post/add', '#', '2', 'system:post:add'),
(352, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '编辑', 2, 18, TRUE, 'system/post/edit', '#', '2', 'system:post:edit'),
(353, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '详情', 3, 18, TRUE, 'system/post/detail', '#', '2', 'system:post:detail'),
(354, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '删除', 4, 18, TRUE, 'system/post/delete', '#', '2', 'system:post:delete'),
(355, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '批量删除', 5, 18, TRUE, 'system/post/batch-delete', '#', '2', 'system:post:batchDelete');

-- 部门管理按钮
INSERT INTO sys_menu (
  id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component, menu_type, permission_code
) VALUES
(401, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '新增', 1, 19, TRUE, 'system/dept/add', '#', '2', 'system:dept:add'),
(402, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '编辑', 2, 19, TRUE, 'system/dept/edit', '#', '2', 'system:dept:edit'),
(403, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '详情', 3, 19, TRUE, 'system/dept/detail', '#', '2', 'system:dept:detail'),
(404, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '删除', 4, 19, TRUE, 'system/dept/delete', '#', '2', 'system:dept:delete'),
(405, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '批量删除', 5, 19, TRUE, 'system/dept/batch-delete', '#', '2', 'system:dept:batchDelete');

-- 系统参数配置按钮
INSERT INTO sys_menu (
  id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component, menu_type, permission_code
) VALUES
(451, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '新增', 1, 20, TRUE, 'system/params/add', '#', '2', 'system:params:add'),
(452, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '编辑', 2, 20, TRUE, 'system/params/edit', '#', '2', 'system:params:edit'),
(453, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '详情', 3, 20, TRUE, 'system/params/detail', '#', '2', 'system:params:detail'),
(454, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '删除', 4, 20, TRUE, 'system/params/delete', '#', '2', 'system:params:delete'),
(455, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '批量删除', 5, 20, TRUE, 'system/params/batch-delete', '#', '2', 'system:params:batchDelete');

CREATE TABLE IF NOT EXISTS sys_ele_union (
  id bigint primary key auto_increment,
  created_by varchar(50) NULL,
  created_date timestamp NULL,
  last_modified_by varchar(50) NULL,
  last_modified_date timestamp NULL,
  ele_cat_code VARCHAR(64) NOT NULL,
  ele_cat_name VARCHAR(255) NOT NULL,
  ele_code VARCHAR(64) NOT NULL,
  ele_name VARCHAR(255) NOT NULL,
  is_enabled TINYINT(1) NOT NULL DEFAULT 0,
  is_leaf TINYINT(1) NOT NULL DEFAULT 0,
  level_no INT NOT NULL DEFAULT 0,
  parent_id VARCHAR(50) NOT NULL DEFAULT '0'
) ENGINE=InnoDB COMMENT='基础要素表';

INSERT INTO sys_ele_union
(created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id) VALUES
('system', NOW(), 'system', NOW(), 'notice_rec_type', '公告接收类型', '1', '所有人', 1, 1, 0, '0'),
('system', NOW(), 'system', NOW(), 'notice_rec_type', '公告接收类型', '2', '按用户', 1, 1, 0, '0'),
('system', NOW(), 'system', NOW(), 'notice_rec_type', '公告接收类型', '3', '按角色', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'notice_rec_type', '公告接收类型', '4', '按单位', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'notice_type', '公告类型', '1', '通知公告', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'notice_type', '公告类型', '2', '规章制度', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'notice_type', '公告类型', '3', '政策文件', 0, 0, 0, '0');