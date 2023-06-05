--liquibase formatted sql
--changeset zzw:00000

INSERT INTO sys_menu (id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component) VALUES(57, 'admin', '2023-06-05 14:19:18', 'admin', '2023-06-05 14:19:51', '', 1, 'ep:management', NULL, '演示demo', 1, 0, NULL, '/demo', '#');
INSERT INTO sys_menu (id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component) VALUES(58, 'admin', '2023-06-05 14:20:33', 'admin', '2023-06-05 14:21:03', '', 1, '', NULL, '演示demo2', 23, 57, NULL, 'demo2', 'views/Components/UI/Demo2');
INSERT INTO sys_menu (id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component) VALUES(59, 'admin', '2023-06-05 14:22:07', 'admin', '2023-06-05 14:22:20', '', 1, '', NULL, '演示demo1', 22, 57, NULL, 'demo1', 'views/Components/UI/Demo1');
