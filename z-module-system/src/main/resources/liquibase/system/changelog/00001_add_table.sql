drop table if exists sys_task_param;
CREATE TABLE if not exists `sys_task_param` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cron_expression` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `enable` bit(1) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `start_class` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  created_by varchar(50) NULL,
  created_date timestamp NULL,
  last_modified_by varchar(50) NULL,
  last_modified_date timestamp NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

drop table if exists sys_task_log;
CREATE TABLE if not exists `sys_task_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `detail` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `end_time` datetime(6) DEFAULT NULL,
  `start_time` datetime(6) DEFAULT NULL,
  `success` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `task_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `total_time` int DEFAULT NULL,
  `trace_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  created_by varchar(50) NULL,
  created_date timestamp NULL,
  last_modified_by varchar(50) NULL,
  last_modified_date timestamp NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

drop table if exists sys_upload;
CREATE TABLE `sys_upload` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_date` datetime(6) DEFAULT NULL,
  `last_modified_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `value` blob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;