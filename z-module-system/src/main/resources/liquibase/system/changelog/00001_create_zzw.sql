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
