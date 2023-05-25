--liquibase formatted sql
--changeset zzw:00000

delete from sys_menu where id in (51, 52, 53, 54, 55);

INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component)
VALUES(51, NULL, '2022-07-28 01:40:21', NULL, '2022-07-28 01:40:21', NULL, 1, 'ep:management', NULL, '流程管理', 15, 0, NULL, '/bpm/manage', '#');

INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component)
VALUES(52, NULL, '2022-07-28 01:40:21', NULL, '2022-07-28 01:40:21', NULL, 1, 'ep:management', NULL, '流程定义', 15, 51, NULL, 'definition', 'views/bpm/Definition/Index');

INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component)
VALUES(53, NULL, '2022-07-28 01:40:21', NULL, '2022-07-28 01:40:21', NULL, 1, 'ep:management', NULL, '表单配置', 15, 51, NULL, 'form', 'views/bpm/Form/Index');

INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component)
VALUES(54, NULL, '2022-07-28 01:40:21', NULL, '2022-07-28 01:40:21', NULL, 1, 'ep:management', NULL, '流程表达式', 15, 51, NULL, 'task-assign-rule', 'views/bpm/TaskAssignRule/Index');

INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component)
VALUES(55, NULL, '2022-07-28 01:40:21', NULL, '2022-07-28 01:40:21', NULL, 1, 'ep:management', NULL, '流程监听', 15, 51, NULL, 'xx', 'views/report/JMReport/view');
