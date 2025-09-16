--liquibase formatted sql
--changeset zzw:00000

delete from sys_menu where id = '500';
INSERT INTO sys_menu (
  id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component, menu_type, permission_code
) VALUES
(500, 'system', NOW(), 'system', NOW(), NULL, TRUE, 'ant-design:robot-outlined', TRUE, 'AI',2, 0, TRUE, '/ai', '#', '0', 'ai:');

delete from sys_menu where id in ('501', '521', '541');
INSERT INTO sys_menu (
  id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component, menu_type, permission_code
) VALUES
(501, 'system', NOW(), 'system', NOW(), NULL, TRUE, '', TRUE, '应用广场', 1, 500, TRUE, 'ai_workbench', 'views/ai/Workbench/Index', '0', 'ai:workbench:view'),
(521, 'system', NOW(), 'system', NOW(), NULL, TRUE, '', TRUE, '体验中心', 2, 500, TRUE, 'ai_chat', 'views/ai/chat/Index', '0', 'ai:portal:view'),
(541, 'system', NOW(), 'system', NOW(), NULL, TRUE, '', TRUE, 'API Key管理', 1, 500, TRUE, 'ai_apikey', 'views/ai/ApiKey/Index', '1', 'ai:apikey:view'),

delete from sys_menu where id in ('542', '543', '544', '545', '546');
INSERT INTO sys_menu (
  id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component, menu_type, permission_code
) VALUES
(542, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '新增', 1, 541, TRUE, 'ai/engine/add', '#', '2', 'ai:engine:add'),
(543, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '编辑', 2, 541, TRUE, 'ai/engine/edit', '#', '2', 'ai:engine:edit'),
(544, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '详情', 3, 541, TRUE, 'ai/engine/detail', '#', '2', 'ai:engine:detail'),
(545, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '删除', 4, 541, TRUE, 'ai/engine/delete', '#', '2', 'ai:engine:delete'),
(546, 'system', NOW(), 'system', NOW(), NULL, TRUE, NULL, TRUE, '批量删除', 5, 541, TRUE, 'ai/engine/batch-delete', '#', '2', 'ai:engine:batchDelete');
