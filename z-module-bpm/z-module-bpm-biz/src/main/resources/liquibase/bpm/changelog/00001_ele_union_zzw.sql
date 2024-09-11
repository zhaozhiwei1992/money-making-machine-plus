--liquibase formatted sql
--changeset zzw:00001

CREATE TABLE IF NOT EXISTS sys_ele_union (
  id bigint primary key auto_increment,
  created_by varchar(50) NULL,
  created_date timestamp NULL,
  last_modified_by varchar(50) NULL,
  last_modified_date timestamp NULL,
  ele_cat_code VARCHAR(64) NOT NULL,
  ele_cat_name VARCHAR(255) NOT NULL,
  ele_code VARCHAR(64) NOT NULL,
  ele_name VARCHAR(255) NOT NULL,
  is_enabled TINYINT(1) NOT NULL DEFAULT 0,
  is_leaf TINYINT(1) NOT NULL DEFAULT 0,
  level_no INT NOT NULL DEFAULT 0,
  parent_id VARCHAR(50) NOT NULL DEFAULT '0'
) ENGINE=InnoDB COMMENT='基础要素表';

INSERT INTO sys_ele_union
(created_by, created_date, last_modified_by, last_modified_date, ele_cat_code, ele_cat_name, ele_code, ele_name, is_enabled, is_leaf, level_no, parent_id) VALUES
('system', NOW(), 'system', NOW(), 'bool', '是否', '1', '是', 1, 1, 0, '0'),
('system', NOW(), 'system', NOW(), 'bool', '是否', '2', '否', 1, 1, 0, '0'),
('system', NOW(), 'system', NOW(), 'sex', '性别', '1', '男', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'sex', '性别', '2', '女', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'bpm_model_category', '流程分类', 'leave', '请假流程', 1, 1, 0, '0'),
('system', NOW(), 'system', NOW(), 'bpm_model_category', '流程分类', 'oa', 'oa流程', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'common_status', '通用状态', '1', '启用', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'common_status', '通用状态', '2', '停用', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'sys_listener_type', '监听类型', '1', '任务监听', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'sys_listener_type', '监听类型', '2', '执行监听', 0, 0, 0, ''),
('system', NOW(), 'system', NOW(), 'sys_listener_value', '监听值分类', '1', 'JAVA类', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'sys_listener_value', '监听值分类', '2', '表达式', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'sys_listener_value', '监听值分类', '3', '大力表达式', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'bpm_process_instance_status', '流程实例状态', '1', '进行中', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'bpm_process_instance_status', '流程实例状态', '2', '已完成', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'bpm_process_instance_result', '流程实例结果', '1', '处理中', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'bpm_process_instance_result', '流程实例结果', '2', '通过', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'bpm_process_instance_result', '流程实例结果', '3', '不通过', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'bpm_process_instance_result', '流程实例结果', '4', '已取消', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'bpm_model_form_type', '流程的表单类型', '10', '流程表单', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'bpm_model_form_type', '流程的表单类型', '20', '业务表单', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'bpm_task_assign_rule_type', '任务分配规则的类型', '10', '角色', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'bpm_task_assign_rule_type', '任务分配规则的类型', '20', '用户', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'bpm_task_assign_rule_type', '任务分配规则的类型', '30', '用户组', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'bpm_task_assign_rule_type', '任务分配规则的类型', '40', '自定义脚本', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'bpm_task_assign_rule_type', '任务分配规则的类型', '50', '岗位', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'bpm_task_assign_script', '任务分配自定义脚本', '10', '流程发起人', 0, 0, 0, '0'),
('system', NOW(), 'system', NOW(), 'bpm_task_assign_script', '任务分配自定义脚本', '20', '流程发起人一级领导', 0, 0, 0, '0');