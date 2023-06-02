--liquibase formatted sql
--changeset zzw:00001

-- 一些系统必要的基础要素
delete from ele_union where id in ('1','2','3','4','5','6','7','8','9','10','11','12','13','14','15','16','17','18','19','20','21','22','23','24','25','26','27','28','29','30');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(1, NULL, '2023-05-27 19:46:03.312000000', NULL, '2023-05-27 19:46:03.312000000', 'bool', '是否', '1', '是', 1, 1, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(2, NULL, '2023-05-27 20:16:05.234000000', NULL, '2023-05-27 20:16:05.234000000', 'bool', '是否', '2', '否', 1, 1, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(3, NULL, '2023-05-27 20:17:00.812000000', NULL, '2023-05-27 20:17:00.812000000', 'sex', '性别', '1', '男', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(4, NULL, '2023-05-27 20:17:00.812000000', NULL, '2023-05-27 20:17:00.812000000', 'sex', '性别', '2', '女', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(5, NULL, '2023-05-27 20:49:14.040000000', NULL, '2023-05-27 20:49:14.040000000', 'bpm_model_category', '流程分类', 'leave', '请假流程', 1, 1, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(6, NULL, '2023-05-27 20:49:14.040000000', NULL, '2023-05-27 20:49:14.040000000', 'bpm_model_category', '流程分类', 'oa', 'oa流程', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(7, NULL, '2023-05-27 20:54:22.111000000', NULL, '2023-05-27 20:54:22.111000000', 'common_status', '通用状态', '1', '启用', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(8, NULL, '2023-05-27 20:54:22.111000000', NULL, '2023-05-27 20:54:22.111000000', 'common_status', '通用状态', '2', '停用', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(11, NULL, '2023-05-27 20:55:34.761000000', NULL, '2023-05-27 20:55:34.761000000', 'sys_listener_type', '监听类型', '1', '任务监听', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(12, NULL, '2023-05-27 20:55:34.761000000', NULL, '2023-05-27 20:55:34.761000000', 'sys_listener_type', '监听类型', '2', '执行监听', 0, 0, 0, '');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(13, NULL, '2023-05-27 20:57:29.862000000', NULL, '2023-05-27 20:57:29.862000000', 'sys_listener_value', '监听值分类', '1', 'JAVA类', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(14, NULL, '2023-05-27 20:57:55.335000000', NULL, '2023-05-27 20:57:55.335000000', 'sys_listener_value', '监听值分类', '2', '表达式', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(15, NULL, '2023-05-27 20:57:55.335000000', NULL, '2023-05-27 20:57:55.335000000', 'sys_listener_value', '监听值分类', '3', '大力表达式', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(16, NULL, '2023-05-28 12:51:25.252000000', NULL, '2023-05-28 12:51:25.252000000', 'bpm_process_instance_status', '流程实例状态', '1', '进行中', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(17, NULL, '2023-05-28 12:51:25.252000000', NULL, '2023-05-28 12:51:25.252000000', 'bpm_process_instance_status', '流程实例状态', '2', '已完成', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(18, NULL, '2023-05-28 12:52:07.316000000', NULL, '2023-05-28 12:52:07.316000000', 'bpm_process_instance_result', '流程实例结果', '1', '处理中', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(19, NULL, '2023-05-28 12:52:44.155000000', NULL, '2023-05-28 12:52:44.155000000', 'bpm_process_instance_result', '流程实例结果', '2', '通过', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(20, NULL, '2023-05-28 12:52:44.155000000', NULL, '2023-05-28 12:52:44.155000000', 'bpm_process_instance_result', '流程实例结果', '3', '不通过', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(21, NULL, '2023-05-28 12:52:44.155000000', NULL, '2023-05-28 12:52:44.155000000', 'bpm_process_instance_result', '流程实例结果', '4', '已取消', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(22, NULL, '2023-05-28 12:54:13.793000000', NULL, '2023-05-28 12:54:13.793000000', 'bpm_model_form_type', '流程的表单类型', '10', '流程表单', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(23, NULL, '2023-05-28 12:54:13.793000000', NULL, '2023-05-28 12:54:13.793000000', 'bpm_model_form_type', '流程的表单类型', '20', '业务表单', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(24, NULL, '2023-05-28 12:55:46.280000000', NULL, '2023-05-28 12:55:46.280000000', 'bpm_task_assign_rule_type', '任务分配规则的类型', '10', '角色', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(25, NULL, '2023-05-28 12:56:37.711000000', NULL, '2023-05-28 12:56:37.711000000', 'bpm_task_assign_rule_type', '任务分配规则的类型', '20', '用户', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(26, NULL, '2023-05-28 12:56:37.711000000', NULL, '2023-05-28 12:56:37.711000000', 'bpm_task_assign_rule_type', '任务分配规则的类型', '30', '用户组', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(27, NULL, '2023-05-28 12:56:37.711000000', NULL, '2023-05-28 12:56:37.711000000', 'bpm_task_assign_rule_type', '任务分配规则的类型', '40', '自定义脚本', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(28, NULL, '2023-05-28 12:56:37.711000000', NULL, '2023-05-28 12:56:37.711000000', 'bpm_task_assign_rule_type', '任务分配规则的类型', '50', '岗位', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(29, NULL, '2023-05-28 12:57:51.550000000', NULL, '2023-05-28 12:57:51.550000000', 'bpm_task_assign_script', '任务分配自定义脚本', '10', '流程发起人', 0, 0, 0, '0');
INSERT INTO ele_union
(id, created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id)
VALUES(30, NULL, '2023-05-28 12:58:11.389000000', NULL, '2023-05-28 12:58:11.389000000', 'bpm_task_assign_script', '任务分配自定义脚本', '20', '流程发起人一级领导', 0, 0, 0, '0');
