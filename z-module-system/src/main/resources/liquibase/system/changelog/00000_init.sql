--liquibase formatted sql
--changeset zzw:00000

-- 判断表不存在先创建
-- 用户表
CREATE TABLE if not exists t_user (
	id bigint NOT NULL,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	activated bool NOT NULL,
	appid varchar(20) NULL,
	image_url varchar(256) NULL,
	login varchar(50) NOT NULL,
	mof_div_code varchar(9) NULL,
	"name" varchar(50) NULL,
	password_hash varchar(60) NOT NULL
);

CREATE TABLE if not exists t_authority (
	id bigint NOT NULL,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	code varchar(50) NOT NULL,
	"name" varchar(50) NOT NULL
);

CREATE TABLE if not exists t_user_authority (
	user_id int8 NOT NULL,
	authority_code varchar(50) NOT NULL
);

CREATE TABLE if not exists sys_menu (
	id bigint NOT NULL,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	config varchar(255) NULL,
	enabled bool NULL,
	icon_cls varchar(255) NULL,
	keep_alive bool NULL,
	"name" varchar(255) NULL,
	order_num int4 NULL,
	parent_id int8 NULL,
	require_auth bool NULL,
	url varchar(255) NULL
);


CREATE TABLE if not exists sys_param (
	id bigint NOT NULL,
	created_by varchar(50) NULL,
	created_date timestamp NULL,
	last_modified_by varchar(50) NULL,
	last_modified_date timestamp NULL,
	code varchar(255) NULL,
	"enable" bool NULL,
	"name" varchar(255) NULL,
	remark varchar(255) NULL,
	value varchar(255) NULL
);

-- ----------------------------
-- 用户角色初始化
-- ----------------------------
delete from t_user;
INSERT INTO t_user
(id, created_by, created_date, last_modified_by, last_modified_date, activated, password2, image_url, login, "name", password_hash, appid, mof_div_code)
VALUES(3, 'admin', '2022-07-14 11:27:02.515', NULL, '2022-08-18 15:42:42.015', false, NULL, 'user', '普通用户', '$2a$10$aQ9NOy/S2.UiNAmxOwNcjueSrRbwnYnUBbED.LE/DePeAeddPGCA.', 'system', '330000000');
INSERT INTO t_user
(id, created_by, created_date, last_modified_by, last_modified_date, activated, password2, image_url, login, "name", password_hash, appid, mof_div_code)
VALUES(1, 'admin', '2022-07-14 11:25:56.594', NULL, '2022-07-27 09:05:07.221', false, NULL, 'admin', '系统管理员', '$2a$10$Uaq/uIj3D5VZ4Y5.I7MTB.pMXka6FKuCNy4A.ZnnRk9GshwYxBQZG', 'system', '330000000');

-- 初始化序列
select setval('t_user_id_seq', 4);


delete from t_authority;
INSERT INTO t_authority
(id, created_by, created_date, last_modified_by, last_modified_date, code, "name")
VALUES(1, 'system', NULL, 'system', NULL, 'ROLE_ADMIN', '管理员');
INSERT INTO t_authority
(id, created_by, created_date, last_modified_by, last_modified_date, code, "name")
VALUES(2, 'system', NULL, 'system', NULL, 'ROLE_USER', '经办');

select setval('t_authority_id_seq', 3);

delete from t_user_authority;
INSERT INTO t_user_authority
(user_id, authority_code)
VALUES(1, 'ROLE_ADMIN');
INSERT INTO t_user_authority
(user_id, authority_code)
VALUES(3, 'ROLE_USER');


-- ----------------------------
-- 菜单初始化
-- ----------------------------
delete from sys_menu;
INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, "name", order_num, parent_id, require_auth, url)
VALUES(16, NULL, '2022-07-28 01:54:02.767', NULL, '2022-07-28 01:54:02.767', NULL, true, NULL, NULL, '登录日志', 16, 15, NULL, '/log/login');
INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, "name", order_num, parent_id, require_auth, url)
VALUES(17, NULL, '2022-07-28 01:54:24.449', NULL, '2022-07-28 01:54:24.449', NULL, true, NULL, NULL, '接口请求日志', 17, 15, NULL, '/log/request');
INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, "name", order_num, parent_id, require_auth, url)
VALUES(18, NULL, '2022-07-28 01:54:50.347', NULL, '2022-07-28 01:54:50.347', NULL, true, NULL, NULL, '定时任务日志', 18, 15, NULL, '/log/task');
INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, "name", order_num, parent_id, require_auth, url)
VALUES(3, NULL, '2022-07-28 01:45:19.705', NULL, '2022-07-28 01:45:19.705', NULL, true, NULL, NULL, '菜单管理', 3, 2, NULL, '/menu/list');
INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, "name", order_num, parent_id, require_auth, url)
VALUES(4, NULL, '2022-07-28 01:45:40.293', NULL, '2022-07-28 01:45:40.293', NULL, true, NULL, NULL, '角色管理', 4, 2, NULL, '/role/list');
INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, "name", order_num, parent_id, require_auth, url)
VALUES(5, NULL, '2022-07-28 01:45:56.510', NULL, '2022-07-28 01:45:56.510', NULL, true, NULL, NULL, '用户管理', 5, 2, NULL, '/user/list');
INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, "name", order_num, parent_id, require_auth, url)
VALUES(7, NULL, '2022-07-28 01:46:28.005', NULL, '2022-07-28 01:46:28.005', NULL, true, NULL, NULL, '定时任务管理', 7, 2, NULL, '/task/list');
INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, "name", order_num, parent_id, require_auth, url)
VALUES(15, NULL, '2022-07-28 01:53:00.920', NULL, '2022-07-28 01:53:00.920', NULL, true, 'fa-layui-icon-transfer', NULL, '日志查看', 8, 0, NULL, '#');
INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, "name", order_num, parent_id, require_auth, url)
VALUES(8, NULL, '2022-07-28 01:46:52.525', NULL, '2022-07-28 01:46:52.525', NULL, true, 'fa-layui-icon-rss', NULL, '报表查询', 2, 0, NULL, '#');
INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, "name", order_num, parent_id, require_auth, url)
VALUES(2, NULL, '2022-07-28 01:40:21.057', NULL, '2022-07-28 01:40:21.057', NULL, true, 'fa-layui-icon-at', NULL, '系统管理', 15, 0, NULL, '#');
INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, "name", order_num, parent_id, require_auth, url)
VALUES(1, NULL, '2022-10-09 20:20:21.593', NULL, '2022-10-09 20:20:21.593', NULL, true, NULL, NULL, '系统参数配置', 1, 2, NULL, '/params/list');

select setval('sys_menu_id_seq', 20);

