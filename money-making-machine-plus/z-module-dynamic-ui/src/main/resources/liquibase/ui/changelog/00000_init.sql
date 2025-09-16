--liquibase formatted sql
--changeset zzw:00000

delete from sys_menu where id in (57, 58, 59);

INSERT INTO sys_menu (id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component) VALUES(57, 'admin', '2023-06-05 14:19:18', 'admin', '2023-06-05 14:19:51', '', 1, 'ep:management', NULL, '演示demo', 1, 0, NULL, '/demo', '#');
INSERT INTO sys_menu (id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component) VALUES(58, 'admin', '2023-06-05 14:20:33', 'admin', '2023-06-05 14:21:03', '', 1, '', NULL, '演示demo2', 23, 57, NULL, 'demo2', 'views/Components/UI/Demo2');
INSERT INTO sys_menu (id, created_by, created_date, last_modified_by, last_modified_date, config, enabled, icon_cls, keep_alive, name, order_num, parent_id, require_auth, url, component, template) VALUES(59, 'admin', '2023-06-05 14:22:07', 'admin', '2023-06-05 14:22:20', '', 1, '', NULL, '演示demo1', 22, 57, NULL, 'demo1', 'views/Components/UI/Demo1', 'TemplateDefault');

-- 编辑页面　demo配置
DELETE FROM ui_t_component WHERE id=1;
DELETE FROM ui_t_component WHERE id=2;
DELETE FROM ui_t_component WHERE id=3;
DELETE FROM ui_t_component WHERE id=4;

INSERT INTO ui_t_component (id, created_by, created_date, last_modified_by, last_modified_date, component, config, menu_id, order_num, name) VALUES(1, 'admin', NULL, NULL, NULL, 'toolbutton', NULL, 59, 1, 'toolbutton');
INSERT INTO ui_t_component (id, created_by, created_date, last_modified_by, last_modified_date, component, config, menu_id, order_num, name) VALUES(2, 'admin', NULL, NULL, NULL, 'queryform', NULL, 59, 2, 'queryform');
INSERT INTO ui_t_component (id, created_by, created_date, last_modified_by, last_modified_date, component, config, menu_id, order_num, name) VALUES(3, 'admin', NULL, NULL, NULL, 'tab', NULL, 59, 3, 'tab');
INSERT INTO ui_t_component (id, created_by, created_date, last_modified_by, last_modified_date, component, config, menu_id, order_num, name) VALUES(4, 'admin', NULL, NULL, NULL, 'table', NULL, 59, 4, 'table');

DELETE FROM ui_t_toolbutton WHERE id=1;
DELETE FROM ui_t_toolbutton WHERE id=2;

INSERT INTO ui_t_toolbutton (id, created_by, created_date, last_modified_by, last_modified_date, `action`, code, config, menu_id, name, order_num) VALUES(1, 'admin', NULL, NULL, NULL, 'add', 'add', NULL, 59, '新增', 1);
INSERT INTO ui_t_toolbutton (id, created_by, created_date, last_modified_by, last_modified_date, `action`, code, config, menu_id, name, order_num) VALUES(2, 'admin', NULL, NULL, NULL, 'del', 'del', NULL, 59, '删除', 2);

DELETE FROM ui_t_queryform WHERE id=1;
DELETE FROM ui_t_queryform WHERE id=2;
DELETE FROM ui_t_queryform WHERE id=3;
DELETE FROM ui_t_queryform WHERE id=5;

INSERT INTO ui_t_queryform (id, created_by, created_date, last_modified_by, last_modified_date, code, config, is_source, menu_id, name, order_num, placeholder, requirement, `type`) VALUES(1, 'admin', NULL, NULL, NULL, 'name', NULL, NULL, 59, '姓名', 1, NULL, NULL, 'Input');
INSERT INTO ui_t_queryform (id, created_by, created_date, last_modified_by, last_modified_date, code, config, is_source, menu_id, name, order_num, placeholder, requirement, `type`) VALUES(2, 'admin', NULL, NULL, NULL, 'login', NULL, NULL, 59, '登录名', 2, NULL, NULL, 'Input');
INSERT INTO ui_t_queryform (id, created_by, created_date, last_modified_by, last_modified_date, code, config, is_source, menu_id, name, order_num, placeholder, requirement, `type`) VALUES(3, 'admin', NULL, NULL, NULL, 'mofDivCode', NULL, NULL, 59, '区划', 3, NULL, NULL, 'Input');
INSERT INTO ui_t_queryform (id, created_by, created_date, last_modified_by, last_modified_date, code, config, is_source, menu_id, name, order_num, placeholder, requirement, `type`) VALUES(5, 'admin', NULL, NULL, NULL, 'createDate', NULL, NULL, 59, '创建日期', 4, NULL, NULL, 'DatePicker');

DELETE FROM ui_t_tab WHERE id=1;
DELETE FROM ui_t_tab WHERE id=2;
INSERT INTO ui_t_tab (id, created_by, created_date, last_modified_by, last_modified_date, code, config, menu_id, name, order_num) VALUES(1, 'admin', NULL, NULL, NULL, 'tab1', NULL, 59, '待审核', 1);
INSERT INTO ui_t_tab (id, created_by, created_date, last_modified_by, last_modified_date, code, config, menu_id, name, order_num) VALUES(2, 'admin', NULL, NULL, NULL, 'tab2', NULL, 59, '已审核', 2);

DELETE FROM ui_t_table WHERE id=1;
DELETE FROM ui_t_table WHERE id=2;
DELETE FROM ui_t_table WHERE id=3;
DELETE FROM ui_t_table WHERE id=4;
INSERT INTO ui_t_table (id, created_by, created_date, last_modified_by, last_modified_date, code, config, is_edit, is_source, menu_id, name, order_num, requirement, `type`) VALUES(1, 'admin', NULL, NULL, NULL, 'name', NULL, NULL, NULL, 59, '姓名', 2, NULL, '');
INSERT INTO ui_t_table (id, created_by, created_date, last_modified_by, last_modified_date, code, config, is_edit, is_source, menu_id, name, order_num, requirement, `type`) VALUES(2, 'admin', NULL, NULL, NULL, 'login', NULL, NULL, NULL, 59, '登录名', 3, NULL, NULL);
INSERT INTO ui_t_table (id, created_by, created_date, last_modified_by, last_modified_date, code, config, is_edit, is_source, menu_id, name, order_num, requirement, `type`) VALUES(3, 'admin', NULL, NULL, NULL, 'mofDivCode', NULL, NULL, NULL, 59, '区划', 4, NULL, NULL);
INSERT INTO ui_t_table (id, created_by, created_date, last_modified_by, last_modified_date, code, config, is_edit, is_source, menu_id, name, order_num, requirement, `type`) VALUES(4, 'admin', NULL, NULL, NULL, 'index', NULL, NULL, NULL, 59, '序号', 1, NULL, 'index');


-- 新增页面 demo配置

DELETE FROM ui_t_component WHERE id=5;
DELETE FROM ui_t_component WHERE id=6;

INSERT INTO ui_t_component (id, created_by, created_date, last_modified_by, last_modified_date, component, config, menu_id, order_num, name) VALUES(5, 'admin', NULL, NULL, NULL, 'editform', NULL, 58, 1, 'editform');
INSERT INTO ui_t_component (id, created_by, created_date, last_modified_by, last_modified_date, component, config, menu_id, order_num, name) VALUES(6, 'admin', NULL, NULL, NULL, 'toolbutton', NULL, 58, 2, 'toolbutton');

DELETE FROM ui_t_toolbutton WHERE id=3;

INSERT INTO ui_t_toolbutton (id, created_by, created_date, last_modified_by, last_modified_date, `action`, code, config, menu_id, name, order_num) VALUES(3, 'admin', NULL, NULL, NULL, 'save', 'save', NULL, 58, '保存', 1);

DELETE FROM ui_t_editform WHERE id in (7, 8, 9);

INSERT INTO ui_t_editform (id, created_by, created_date, last_modified_by, last_modified_date, code, config, is_source, menu_id, name, order_num, placeholder, requirement, `type`) VALUES(7, 'admin', NULL, NULL, NULL, 'name', NULL, NULL, 58, '姓名', 1, NULL, NULL, 'Input');
INSERT INTO ui_t_editform (id, created_by, created_date, last_modified_by, last_modified_date, code, config, is_source, menu_id, name, order_num, placeholder, requirement, `type`) VALUES(8, 'admin', NULL, NULL, NULL, 'login', NULL, NULL, 58, '登录名', 2, NULL, NULL, 'Input');
INSERT INTO ui_t_editform (id, created_by, created_date, last_modified_by, last_modified_date, code, config, is_source, menu_id, name, order_num, placeholder, requirement, `type`) VALUES(9, 'admin', NULL, NULL, NULL, 'createDate', NULL, NULL, 58, '创建日期', 4, NULL, NULL, 'DatePicker');

-- 可编辑列表测试 demo3
DELETE FROM ui_t_component WHERE id=7;
DELETE FROM ui_t_component WHERE id=8;

INSERT INTO ui_t_component (id, created_by, created_date, last_modified_by, last_modified_date, component, config, menu_id, order_num, name) VALUES(8, 'admin', NULL, NULL, NULL, 'toolbutton', NULL, 60, 1, 'toolbutton');
INSERT INTO ui_t_component (id, created_by, created_date, last_modified_by, last_modified_date, component, config, menu_id, order_num, name) VALUES(7, 'admin', NULL, NULL, NULL, 'edittable', NULL, 60, 2, 'edittable');

DELETE FROM ui_t_toolbutton WHERE menu_id=60;


DELETE FROM ui_t_table WHERE menu_id = '60';

INSERT INTO ui_t_table
(id, created_by, created_date, last_modified_by, last_modified_date, code, config, is_edit, is_source, menu_id, name, order_num, requirement, `type`)
VALUES(5, 'admin', NULL, NULL, NULL, 'name', NULL, 1, NULL, 60, '姓名', 2, NULL, 'Input');
INSERT INTO ui_t_table
(id, created_by, created_date, last_modified_by, last_modified_date, code, config, is_edit, is_source, menu_id, name, order_num, requirement, `type`)
VALUES(6, 'admin', NULL, NULL, NULL, 'login', NULL, 1, NULL, 60, '登录名', 3, NULL, 'Input');
INSERT INTO ui_t_table
(id, created_by, created_date, last_modified_by, last_modified_date, code, config, is_edit, is_source, menu_id, name, order_num, requirement, `type`)
VALUES(7, 'admin', NULL, NULL, NULL, 'mofDivCode', NULL, 1, NULL, 60, '区划', 4, NULL, 'Input');

