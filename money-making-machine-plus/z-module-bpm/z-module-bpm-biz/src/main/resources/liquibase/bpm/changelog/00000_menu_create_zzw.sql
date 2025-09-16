--liquibase formatted sql
--changeset zzw:00000

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

delete from sys_menu where id in (7, 60, 61, 62);

INSERT INTO sys_menu (
  id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component, menu_type, permission_code
) VALUES
(60, 'system', NOW(), 'system', NOW(), NULL, TRUE, 'ep:management', TRUE, '流程定义', 1, 7, TRUE, 'model', 'views/bpm/Model/Index', '1', 'bpm:definition:view'),
(61, 'system', NOW(), 'system', NOW(), NULL, TRUE, 'ep:management', TRUE, '表单配置', 2, 7, TRUE, 'form', 'views/bpm/Form/Index', '1', 'bpm:form:view'),
(62, 'system', NOW(), 'system', NOW(), NULL, TRUE, 'ep:management', TRUE, '流程表达式', 3, 7, TRUE, 'task-assign-rule', 'views/bpm/TaskAssignRule/Index', '1', 'bpm:task-assign-rule:view');
