--liquibase formatted sql
--changeset zzw:00002

-- 基础信息维护菜单
delete from sys_menu where id = '56';
INSERT INTO sys_menu (id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component) VALUES(56, NULL, '2023-05-27 16:56:19', NULL, '2023-05-27 16:56:19', '', 1, '', NULL, '基础要素维护', 1, 2, NULL, 'ele-union', 'views/system/EleUnion/Index');
