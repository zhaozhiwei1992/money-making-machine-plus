--liquibase formatted sql
--changeset zzw:00001

delete from sys_menu where id in (41, 42, 43, 44);

INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component)
VALUES(42, NULL, '2022-07-28 01:45:57', NULL, '2022-07-28 01:45:57', NULL, 1, NULL, NULL, '积木报表', 5, 31, NULL, 'jmreport/list', 'views/report/JMReport/Index');

INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component)
VALUES(41, NULL, '2022-07-28 01:40:21', NULL, '2022-07-28 01:40:21', NULL, 1, 'ep:management', NULL, '报表查看', 15, 0, NULL, '/report/views', '#');

INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component)
VALUES(43, NULL, '2022-07-28 01:40:21', NULL, '2022-07-28 01:40:21', NULL, 1, 'ep:management', NULL, '接口请求报表', 15, 41, NULL, '1cd9d574d0c42f3915046dc61d9f33bd', 'views/report/JMReport/view');

INSERT INTO sys_menu
(id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component)
VALUES(44, NULL, '2022-07-28 01:40:21', NULL, '2022-07-28 01:40:21', NULL, 1, 'ep:management', NULL, '用户访问图表', 15, 41, NULL, '575163965000249344', 'views/report/JMReport/view');
