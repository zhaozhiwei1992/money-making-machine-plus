--liquibase formatted sql
--changeset zzw:00001

-- ----------------------------
-- 后边写脚本
-- ----------------------------

delete from sys_menu where id in (31, 32);

INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component)
VALUES(31, NULL, '2022-07-28 01:40:21', NULL, '2022-07-28 01:40:21', NULL, 1, 'ep:management', NULL, '开发者工具', 15, 0, NULL, '/dev/tools', '#');

INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component)
VALUES(32, NULL, '2022-07-28 01:45:57', NULL, '2022-07-28 01:45:57', NULL, 1, NULL, NULL, '代码生成', 5, 31, NULL, 'code/generator', 'views/CodeGenerator/Index');

delete from sys_menu where id in (45, 46);

INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component)
VALUES(45, NULL, '2023-05-21 00:17:29', NULL, '2023-05-21 00:17:29', '', 1, '', NULL, '接口测试swagger', 1, 31, NULL, 'swagger', 'views/system/Swagger/Index');
INSERT INTO money_making_machine_plus.sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component)
VALUES(46, NULL, '2023-05-21 15:43:34', NULL, '2023-05-21 15:43:34', '', 1, '', NULL, '接口测试doc', 1, 31, NULL, 'knife4j-doc', 'views/system/Knife4jDoc/Index');

alter table sys_menu auto_increment=50;
