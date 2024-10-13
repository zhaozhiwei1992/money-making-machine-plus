--liquibase formatted sql
--changeset zzw:00001

delete from sys_menu where id = 92;
INSERT INTO sys_menu (
  id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component, menu_type, permission_code
) VALUES
(92, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '缓存监控',3, 4, TRUE, 'cache', 'views/framework/monitor/CacheListIndex', '1', 'monitor:cache:view')
