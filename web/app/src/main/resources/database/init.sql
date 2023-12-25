-- ddl start

-- qualitis_abnormal_data_record_info definition

CREATE TABLE `qualitis_abnormal_data_record_info` (
  `rule_id` bigint(20) NOT NULL,
  `rule_name` varchar(600) NOT NULL,
  `rule_detail` varchar(500) NOT NULL,
  `datasource` varchar(20) NOT NULL,
  `db_name` varchar(100) NOT NULL,
  `table_name` varchar(100) NOT NULL,
  `dept` varchar(255) NOT NULL,
  `sub_system_id` int(20) NOT NULL,
  `execute_num` int(10) NOT NULL,
  `event_num` int(10) NOT NULL,
  `record_date` varchar(25) NOT NULL,
  `record_time` varchar(25) NOT NULL,
  PRIMARY KEY (`rule_id`,`db_name`,`table_name`,`record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_alarm_info definition

CREATE TABLE `qualitis_alarm_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `alarm_level` varchar(1) COLLATE utf8_bin DEFAULT NULL,
  `alarm_reason` text COLLATE utf8_bin,
  `alarm_time` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `alarm_type` int(11) DEFAULT NULL,
  `application_id` varchar(40) COLLATE utf8_bin DEFAULT NULL,
  `begin_time` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `end_time` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `task_id` int(11) DEFAULT NULL,
  `username` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `project_name` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- qualitis_alert_config definition

CREATE TABLE `qualitis_alert_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `topic` varchar(500) NOT NULL,
  `info_receiver` varchar(500) NOT NULL,
  `major_receiver` varchar(500) DEFAULT NULL,
  `alert_table` varchar(500) NOT NULL,
  `filter` varchar(1000) DEFAULT NULL,
  `alert_col` varchar(50) NOT NULL,
  `major_alert_col` varchar(50) DEFAULT NULL,
  `content_cols` varchar(1000) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `create_time` varchar(25) DEFAULT NULL,
  `modify_user` varchar(50) DEFAULT NULL,
  `modify_time` varchar(25) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `rule_group_id` bigint(20) DEFAULT NULL,
  `work_flow_version` varchar(180) DEFAULT NULL,
  `work_flow_name` varchar(180) DEFAULT NULL,
  `node_name` varchar(180) DEFAULT NULL,
  `work_flow_space` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_alert_whitelist definition

CREATE TABLE `qualitis_alert_whitelist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `item` varchar(500) NOT NULL,
  `type` int(5) NOT NULL,
  `authorized_user` varchar(500) NOT NULL,
  `create_time` varchar(25) DEFAULT NULL,
  `modify_time` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_application definition

CREATE TABLE `qualitis_application` (
  `id` varchar(80) NOT NULL,
  `abnormal_task_num` int(11) DEFAULT NULL,
  `create_user` varchar(150) DEFAULT NULL,
  `exception_message` text,
  `execute_user` varchar(150) DEFAULT NULL,
  `fail_task_num` int(11) DEFAULT NULL,
  `finish_task_num` int(11) DEFAULT NULL,
  `finish_time` varchar(25) DEFAULT NULL,
  `invoke_type` int(11) DEFAULT NULL,
  `not_pass_task_num` int(11) DEFAULT NULL,
  `rule_size` int(11) DEFAULT NULL,
  `saved_db` varchar(100) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `submit_time` varchar(25) DEFAULT NULL,
  `total_task_num` int(11) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `rule_group_id` bigint(20) DEFAULT NULL,
  `cluster_name` varchar(255) DEFAULT NULL,
  `execution_param` varchar(1000) DEFAULT NULL,
  `fps_file_id` varchar(255) DEFAULT NULL,
  `fps_hash` varchar(255) DEFAULT NULL,
  `filter_partition` varchar(255) DEFAULT NULL,
  `run_date` varchar(255) DEFAULT NULL,
  `set_flag` varchar(255) DEFAULT NULL,
  `startup_param` varchar(255) DEFAULT NULL,
  `application_comment` int(11) DEFAULT NULL,
  `project_name` varchar(100) DEFAULT NULL,
  `rule_group_name` varchar(100) DEFAULT NULL,
  `rule_datasource` varchar(3000) DEFAULT '',
  `job_id` varchar(50) DEFAULT NULL,
  `ip` varchar(20) NOT NULL,
  `split_by` varchar(20) DEFAULT NULL,
  `rule_ids` text,
  `node_name` varchar(50) DEFAULT NULL,
  `execution_param_json` varchar(1000) DEFAULT NULL,
  `tenant_user_name` varchar(20) DEFAULT NULL,
  `sub_system_id` bigint(20) DEFAULT NULL,
  `rule_names` text,
  `engine_reuse` bit(1) DEFAULT b'1',
  PRIMARY KEY (`id`),
  KEY `index_status_for_check` (`status`),
  KEY `idx_qualitis_application_jobid_submit_time` (`job_id`,`submit_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_application_comment definition

CREATE TABLE `qualitis_application_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `code` int(11) DEFAULT NULL COMMENT '编码',
  `zh_message` varchar(1000) DEFAULT NULL COMMENT '中文信息',
  `en_message` varchar(1000) DEFAULT NULL COMMENT '英文信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_application_error_code_type definition

CREATE TABLE `qualitis_application_error_code_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `linkis_error_code` varchar(128) DEFAULT NULL COMMENT '异常code',
  `application_comment` int(11) DEFAULT NULL COMMENT 'Application comment',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='linkis失败任务错误码映射';


-- qualitis_application_task_result definition

CREATE TABLE `qualitis_application_task_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `application_id` varchar(255) DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  `result_type` varchar(255) DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  `value` varchar(128) DEFAULT NULL,
  `rule_metric_id` bigint(20) DEFAULT NULL,
  `save_result` bit(1) DEFAULT b'1',
  `run_date` bigint(20) DEFAULT NULL,
  `department_code` varchar(255) DEFAULT NULL,
  `version` varchar(180) DEFAULT NULL,
  `env_name` varchar(255) DEFAULT '',
  `compare_value` varchar(255) DEFAULT NULL,
  `denoising_value` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `application_id_rule_id` (`application_id`,`rule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_auth_data_visibility definition

CREATE TABLE `qualitis_auth_data_visibility` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `table_data_id` bigint(20) DEFAULT NULL COMMENT '表主键ID',
  `table_data_type` varchar(20) DEFAULT NULL COMMENT '表数据类型',
  `department_sub_id` bigint(20) DEFAULT NULL COMMENT '科室ID',
  `department_sub_name` varchar(30) DEFAULT NULL COMMENT '科室名称',
  PRIMARY KEY (`id`),
  KEY `idx_table_data_id_type` (`table_data_id`,`table_data_type`) USING BTREE,
  KEY `idx_department_sub_id` (`department_sub_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据可见范围表';


-- qualitis_auth_department definition

CREATE TABLE `qualitis_auth_department` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) COLLATE utf8_bin DEFAULT NULL,
  `department_code` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `tenant_user_id` bigint(20) DEFAULT NULL,
  `source_type` int(4) DEFAULT NULL COMMENT '',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '',
  `create_user` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '创建用户',
  `create_time` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '创建时间',
  `modify_user` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '修改用户',
  `modify_time` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_tem0temhxj86cdqpep31q1iaa` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- qualitis_auth_list definition

CREATE TABLE `qualitis_auth_list` (
  `app_id` varchar(255) NOT NULL,
  `app_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_auth_permission definition

CREATE TABLE `qualitis_auth_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `method` varchar(6) DEFAULT NULL,
  `url` varchar(100) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建用户',
  `create_time` varchar(50) DEFAULT NULL COMMENT '创建时间',
  `modify_user` varchar(50) DEFAULT NULL COMMENT '修改用户',
  `modify_time` varchar(50) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_auth_tenant definition

CREATE TABLE `qualitis_auth_tenant` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tenant_name` varchar(20) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建用户',
  `create_time` varchar(50) DEFAULT NULL COMMENT '创建时间',
  `modify_user` varchar(50) DEFAULT NULL COMMENT '修改用户',
  `modify_time` varchar(50) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_bdp_client_history definition

CREATE TABLE `qualitis_bdp_client_history` (
  `rule_id` bigint(20) NOT NULL,
  `template_function` varchar(255) DEFAULT NULL,
  `datasource` varchar(500) DEFAULT NULL,
  `project_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_config_cluster_info definition

CREATE TABLE `qualitis_config_cluster_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cluster_name` varchar(100) DEFAULT NULL,
  `cluster_type` varchar(100) DEFAULT NULL,
  `hive_server2_address` varchar(100) DEFAULT NULL,
  `linkis_address` varchar(100) DEFAULT NULL,
  `linkis_token` varchar(500) DEFAULT NULL,
  `skip_data_size` varchar(100) DEFAULT NULL,
  `hive_urn` varchar(25) DEFAULT NULL,
  `wtss_json` mediumtext,
  `jobserver_json` mediumtext,
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建用户',
  `create_time` varchar(50) DEFAULT NULL COMMENT '创建时间',
  `modify_user` varchar(50) DEFAULT NULL COMMENT '修改用户',
  `modify_time` varchar(50) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_config_system definition

CREATE TABLE `qualitis_config_system` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key_name` varchar(50) DEFAULT NULL,
  `value` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_665kcle6t77m5lbm48gohcyyg` (`key_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_diff_variable definition

CREATE TABLE `qualitis_diff_variable` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `value` varchar(1000) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- qualitis_gateway_job_info definition

CREATE TABLE `qualitis_gateway_job_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `job_id` varchar(50) NOT NULL,
  `application_num` int(5) DEFAULT '0',
  `application_query_timeout` int(5) NOT NULL DEFAULT '5000',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_linkis_datasource definition

CREATE TABLE `qualitis_linkis_datasource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `linkis_data_source_id` bigint(20) DEFAULT NULL COMMENT 'linkis侧的数据源ID',
  `linkis_data_source_name` varchar(30) DEFAULT NULL COMMENT 'linkis侧的数据源名称',
  `data_source_type_id` bigint(20) DEFAULT NULL COMMENT 'linkis侧的数据源类型ID',
  `dev_department_id` bigint(20) DEFAULT NULL COMMENT '开发科室ID',
  `ops_department_id` bigint(20) DEFAULT NULL COMMENT '运维科室ID',
  `dev_department_name` varchar(30) DEFAULT NULL COMMENT '开发科室',
  `ops_department_name` varchar(30) DEFAULT NULL COMMENT '运维科室',
  `create_user` varchar(50) DEFAULT NULL,
  `create_time` varchar(25) DEFAULT NULL,
  `modify_user` varchar(50) DEFAULT NULL,
  `modify_time` varchar(25) DEFAULT NULL,
  `dcn_sequence` varchar(200) DEFAULT NULL COMMENT 'dcn序列，前端树形节点唯一值',
  `envs` varchar(256) DEFAULT NULL COMMENT '关联的环境名称，英文逗号分隔',
  `sub_system` varchar(30) DEFAULT NULL COMMENT '关联子系统',
  `labels` varchar(256) DEFAULT NULL COMMENT '标签，英文逗号分隔',
  `datasource_desc` varchar(256) DEFAULT NULL COMMENT '数据源描述',
  `input_type` int(4) DEFAULT NULL COMMENT '录入方式（1-手动，2-自动）',
  `verify_type` int(4) DEFAULT NULL COMMENT '认证方式（1-共享，2-非共享）',
  `version_id` bigint(5) DEFAULT NULL COMMENT '数据源版本号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='linkis数据源映射表';


-- qualitis_linkis_udf definition

CREATE TABLE `qualitis_linkis_udf` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `linkis_udf_id` bigint(20) DEFAULT NULL COMMENT 'linkis侧的UDF ID',
  `name` varchar(100) DEFAULT NULL COMMENT 'linkis侧的UDF名称',
  `cn_name` varchar(100) DEFAULT NULL COMMENT 'qualitis侧的UDF中文名称',
  `enter` varchar(100) DEFAULT NULL COMMENT 'qualitis侧的UDF输入类型',
  `return_type` varchar(100) DEFAULT NULL COMMENT 'qualitis侧的UDF输出类型',
  `register_name` varchar(200) DEFAULT NULL COMMENT 'qualitis侧的UDF注册格式',
  `impl_type_code` int(5) DEFAULT NULL COMMENT 'qualitis侧的UDF实现方式',
  `dev_department_id` bigint(20) DEFAULT NULL COMMENT '开发科室ID',
  `ops_department_id` bigint(20) DEFAULT NULL COMMENT '运维科室ID',
  `dev_department_name` varchar(30) DEFAULT NULL COMMENT '开发科室',
  `ops_department_name` varchar(30) DEFAULT NULL COMMENT '运维科室',
  `create_user` varchar(50) DEFAULT NULL,
  `create_time` varchar(25) DEFAULT NULL,
  `modify_user` varchar(50) DEFAULT NULL,
  `modify_time` varchar(25) DEFAULT NULL,
  `upload_path` varchar(1000) DEFAULT NULL COMMENT 'qualitis侧的UDF上传路径',
  `directory` varchar(200) DEFAULT NULL COMMENT 'qualitis侧的分类筛选条件',
  `status` bit(1) DEFAULT b'0',
  `udf_desc` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='linkis udf映射表';


-- qualitis_name_1 definition

CREATE TABLE `qualitis_name_1` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cn_name` varchar(50) DEFAULT NULL,
  `en_name` varchar(50) DEFAULT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `total_name` varchar(100) DEFAULT NULL,
  `type` varchar(100) DEFAULT NULL,
  `frequency` int(5) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_name_2 definition

CREATE TABLE `qualitis_name_2` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cn_name` varchar(50) DEFAULT NULL,
  `en_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_name_3 definition

CREATE TABLE `qualitis_name_3` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cn_name` varchar(50) DEFAULT NULL,
  `en_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_noise_elimination_management definition

CREATE TABLE `qualitis_noise_elimination_management` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date_selection_method` int(11) DEFAULT NULL,
  `business_date` mediumtext,
  `template_id` bigint(20) DEFAULT NULL,
  `noise_norm_ratio` mediumtext,
  `eliminate_strategy` int(20) DEFAULT NULL,
  `execution_parameters_id` bigint(20) DEFAULT NULL,
  `available` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_project definition

CREATE TABLE `qualitis_project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_user` varchar(50) DEFAULT NULL,
  `create_user_full_name` varchar(50) DEFAULT NULL,
  `create_time` varchar(25) DEFAULT NULL,
  `description` varchar(1700) DEFAULT NULL,
  `name` varchar(170) DEFAULT NULL,
  `project_type` int(11) DEFAULT NULL,
  `department` varchar(50) DEFAULT NULL,
  `modify_user` varchar(50) DEFAULT NULL,
  `modify_time` varchar(25) DEFAULT NULL,
  `cn_name` varchar(170) DEFAULT NULL,
  `sub_system_id` bigint(20) DEFAULT NULL,
  `sub_system_name` varchar(25) DEFAULT NULL,
  `git_type` int(5) DEFAULT NULL,
  `git_repo` varchar(500) DEFAULT NULL,
  `git_branch` varchar(100) DEFAULT NULL,
  `git_root_dir` varchar(500) DEFAULT NULL,
  `modify_user_full_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_project_event definition

CREATE TABLE `qualitis_project_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` mediumtext COLLATE utf8_bin,
  `time` varchar(25) COLLATE utf8_bin DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `operate_type` int(4) DEFAULT NULL COMMENT '操作类型',
  `operate_user` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '操作用户',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- qualitis_rule_datasource_count definition

CREATE TABLE `qualitis_rule_datasource_count` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `datasource_count` int(11) DEFAULT NULL,
  `datasource_name` mediumtext COLLATE utf8_bin NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- qualitis_rule_group definition

CREATE TABLE `qualitis_rule_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) DEFAULT NULL,
  `rule_group_name` varchar(300) DEFAULT NULL,
  `version` varchar(25) DEFAULT NULL,
  `node_name` varchar(1024) DEFAULT NULL,
  `type` int(5) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_rule_lock definition

CREATE TABLE `qualitis_rule_lock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `lock_key` varchar(30) NOT NULL COMMENT '锁key，由数据id和描述信息构成',
  `status` int(4) DEFAULT NULL COMMENT '0-空闲，1-已持有',
  `timestamp` bigint(11) DEFAULT NULL COMMENT '时间戳',
  `holder` varchar(30) DEFAULT NULL COMMENT '持有者',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_lock_key` (`lock_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='页面规则编辑锁表，保证同一个规则或相关数据，同时只允许一个用户编辑';


-- qualitis_rule_metric definition

CREATE TABLE `qualitis_rule_metric` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(512) NOT NULL,
  `metric_desc` varchar(500) DEFAULT NULL,
  `sub_system_id` int(20) DEFAULT NULL,
  `department_name` varchar(30) DEFAULT NULL,
  `metric_level` int(5) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `create_time` varchar(25) DEFAULT NULL,
  `modify_time` varchar(25) DEFAULT NULL,
  `modify_user` varchar(50) DEFAULT NULL,
  `sub_system_name` varchar(100) DEFAULT NULL,
  `full_cn_name` varchar(100) DEFAULT NULL,
  `available` bit(1) DEFAULT NULL,
  `department_code` varchar(255) DEFAULT NULL,
  `dev_department_name` varchar(255) DEFAULT NULL,
  `en_code` varchar(255) DEFAULT NULL,
  `frequency` int(11) DEFAULT NULL,
  `ops_department_name` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT '0' COMMENT '0.14.0 The classification of rule metric is usually based on a more detailed division of subsystem info.',
  `product_id` varchar(255) DEFAULT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `buss_code` int(5) DEFAULT NULL,
  `buss_custom` varchar(100) DEFAULT NULL,
  `cn_name` varchar(512) NOT NULL,
  `multi_env` bit(1) DEFAULT b'0' COMMENT '0.21.0 dcn in use.',
  `dev_department_id` bigint(20) DEFAULT NULL COMMENT '开发科室ID',
  `ops_department_id` bigint(20) DEFAULT NULL COMMENT '运维科室ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNI_RULE_METRIC_EN_CODE` (`en_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Created in version qualitis-0.12.0 to manage rule metric.';


-- qualitis_rule_metric_type_config definition

CREATE TABLE `qualitis_rule_metric_type_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cn_name` varchar(50) DEFAULT NULL,
  `en_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_scheduled_operate_history definition

CREATE TABLE `qualitis_scheduled_operate_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dispatching_system_type` varchar(100) DEFAULT NULL COMMENT '调度系统',
  `cluster_name` varchar(100) DEFAULT NULL COMMENT '集群',
  `wtss_project_name` varchar(100) DEFAULT NULL COMMENT '调度项目',
  `wtss_work_flow_name` varchar(100) DEFAULT NULL COMMENT '调度工作流',
  `wtss_task_name` varchar(100) DEFAULT NULL COMMENT '调度任务',
  `approve_number` varchar(30) DEFAULT NULL COMMENT '变更单号',
  `task_type` int(4) DEFAULT NULL COMMENT '任务类型（1-关联调度任务，2-发布调度任务）',
  `operate_type` int(4) DEFAULT NULL COMMENT '操作类型（1-发布，2-删除）',
  `progress_status` int(4) DEFAULT NULL COMMENT '进度状态（1-提交中，2-已结束，3-出现异常）',
  `error_message` varchar(1000) DEFAULT NULL COMMENT '错误原因',
  `create_user` varchar(30) DEFAULT NULL COMMENT '发布人',
  `create_time` varchar(30) DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- qualitis_scheduled_project_history definition

CREATE TABLE `qualitis_scheduled_project_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `scheduled_project_id` bigint(20) DEFAULT NULL,
  `approve_number` varchar(100) DEFAULT NULL COMMENT '审批单号',
  `release_user` varchar(30) DEFAULT NULL,
  `create_time` varchar(30) DEFAULT NULL,
  `create_user` varchar(30) DEFAULT NULL,
  `scheduled_project_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `qualitis_scheduled_project_history_FK` (`scheduled_project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='调度项目变更记录表';


-- qualitis_service definition

CREATE TABLE `qualitis_service` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ip` varchar(20) NOT NULL,
  `updating_application_num` bigint(10) DEFAULT NULL,
  `status` int(10) NOT NULL DEFAULT '1',
  `tenant_user_id` bigint(20) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建用户',
  `create_time` varchar(50) DEFAULT NULL COMMENT '创建时间',
  `modify_user` varchar(50) DEFAULT NULL COMMENT '修改用户',
  `modify_time` varchar(50) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNI_IP` (`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_standard_value definition

CREATE TABLE `qualitis_standard_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_standard_value_action_version definition

CREATE TABLE `qualitis_standard_value_action_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `action_range` varchar(255) NOT NULL,
  `standard_value_version_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_standard_value_department_version definition

CREATE TABLE `qualitis_standard_value_department_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deptment_id` bigint(20) NOT NULL,
  `standard_value_version_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_standard_value_label_version definition

CREATE TABLE `qualitis_standard_value_label_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `label_name` varchar(255) NOT NULL,
  `standard_value_version_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_standard_value_user_version definition

CREATE TABLE `qualitis_standard_value_user_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `standard_value_version_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_task_new_value definition

CREATE TABLE `qualitis_task_new_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rule_id` bigint(20) DEFAULT NULL,
  `status` bigint(20) DEFAULT NULL,
  `result_value` mediumtext,
  `create_user` varchar(50) CHARACTER SET latin1 DEFAULT NULL,
  `create_time` varchar(25) CHARACTER SET latin1 DEFAULT NULL,
  `modify_user` varchar(50) CHARACTER SET latin1 DEFAULT NULL,
  `modify_time` varchar(25) CHARACTER SET latin1 DEFAULT NULL,
  `rule_version` varchar(180) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `qualitis_task_new_value_rule_id_IDX` (`rule_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='新值表';


-- qualitis_template_datasource_type definition

CREATE TABLE `qualitis_template_datasource_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `template_id` bigint(20) DEFAULT NULL,
  `data_source_type_id` int(5) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `template_id` (`template_id`,`data_source_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_template_default_input_meta definition

CREATE TABLE `qualitis_template_default_input_meta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` int(11) DEFAULT NULL,
  `placeholder` varchar(100) DEFAULT NULL,
  `placeholder_desc` varchar(150) DEFAULT NULL,
  `cn_name` varchar(200) DEFAULT NULL,
  `en_name` varchar(200) DEFAULT NULL,
  `cn_desc` varchar(200) DEFAULT NULL,
  `en_desc` varchar(200) DEFAULT NULL,
  `support_fields` bit(1) DEFAULT NULL,
  `support_standard` bit(1) DEFAULT NULL,
  `support_new_value` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_template_naming_conventions definition

CREATE TABLE `qualitis_template_naming_conventions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `major_categories` mediumtext,
  `kind` mediumtext,
  `create_user` varchar(50) DEFAULT NULL,
  `create_time` varchar(25) DEFAULT NULL,
  `modify_user` varchar(50) DEFAULT NULL,
  `modify_time` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_template_regexp_expr definition

CREATE TABLE `qualitis_template_regexp_expr` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key_name` varchar(255) DEFAULT NULL,
  `regexp_type` int(11) DEFAULT NULL,
  `regexp_value` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_upload_record definition

CREATE TABLE `qualitis_upload_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ims_rule_count` int(10) DEFAULT NULL,
  `upload_status` bit(1) NOT NULL,
  `upload_date` date NOT NULL,
  `upload_time` varchar(25) NOT NULL,
  `upload_err_msg` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_application_task definition

CREATE TABLE `qualitis_application_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `begin_time` varchar(25) DEFAULT NULL,
  `cluster_id` varchar(100) DEFAULT NULL,
  `end_time` varchar(25) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `submit_address` varchar(255) DEFAULT NULL,
  `task_remote_id` bigint(20) DEFAULT NULL,
  `application_id` varchar(40) DEFAULT NULL,
  `abort_on_failure` bit(1) DEFAULT b'0',
  `task_exec_id` varchar(255) DEFAULT NULL,
  `task_proxy_user` varchar(255) DEFAULT NULL,
  `new_progress_time` bigint(20) DEFAULT NULL,
  `progress` double DEFAULT NULL,
  `task_comment` int(11) DEFAULT NULL,
  `running_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8vt8tfuq1jlqofdsl2bfx602d` (`application_id`),
  KEY `task_remote_id_cluster_id` (`task_remote_id`,`cluster_id`),
  CONSTRAINT `FK8vt8tfuq1jlqofdsl2bfx602d` FOREIGN KEY (`application_id`) REFERENCES `qualitis_application` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_application_task_datasource definition

CREATE TABLE `qualitis_application_task_datasource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cluster_name` varchar(100) DEFAULT NULL,
  `col_name` varchar(1000) DEFAULT NULL,
  `create_user` varchar(150) DEFAULT NULL,
  `database_name` varchar(100) DEFAULT NULL,
  `datasource_index` int(11) DEFAULT NULL,
  `execute_user` varchar(150) DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  `table_name` varchar(100) DEFAULT NULL,
  `task_id` bigint(20) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `datasource_type` int(11) DEFAULT '1',
  `sub_system_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKeru6qjd5gwkkm1a58g290g18o` (`task_id`),
  KEY `idx_cluster_name` (`cluster_name`) USING BTREE,
  CONSTRAINT `FKeru6qjd5gwkkm1a58g290g18o` FOREIGN KEY (`task_id`) REFERENCES `qualitis_application_task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_application_task_result_status definition

CREATE TABLE `qualitis_application_task_result_status` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `application_id` varchar(40) DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  `task_result_id` bigint(20) DEFAULT NULL,
  `task_rule_alarm_config_id` bigint(20) DEFAULT NULL,
  `status` int(2) DEFAULT NULL COMMENT '1-通过校验，2-未通过校验，3-未校验',
  PRIMARY KEY (`id`),
  KEY `qualitis_application_task_result_status_FK` (`task_result_id`),
  CONSTRAINT `qualitis_application_task_result_status_FK` FOREIGN KEY (`task_result_id`) REFERENCES `qualitis_application_task_result` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_application_task_rule_simple definition

CREATE TABLE `qualitis_application_task_rule_simple` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `application_id` varchar(40) DEFAULT NULL,
  `execute_user` varchar(20) DEFAULT NULL,
  `mid_table_name` varchar(300) DEFAULT NULL,
  `project_creator` varchar(50) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `project_name` varchar(170) DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  `rule_name` varchar(200) DEFAULT NULL,
  `rule_type` int(11) DEFAULT NULL,
  `submit_time` varchar(20) DEFAULT NULL,
  `task_id` bigint(20) DEFAULT NULL,
  `rule_group_name` varchar(255) DEFAULT NULL,
  `delete_fail_check_result` bit(1) DEFAULT b'1',
  `rule_detail` varchar(340) DEFAULT NULL,
  `template_name` varchar(200) DEFAULT NULL,
  `cn_name` varchar(128) DEFAULT NULL,
  `project_cn_name` varchar(128) DEFAULT NULL,
  `alert_level` int(11) DEFAULT NULL,
  `alert_receiver` varchar(100) DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `FK8nr2cvnqp4pg0q2ftp26v0wnw` (`task_id`),
  CONSTRAINT `FK8nr2cvnqp4pg0q2ftp26v0wnw` FOREIGN KEY (`task_id`) REFERENCES `qualitis_application_task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_auth_proxy_user definition

CREATE TABLE `qualitis_auth_proxy_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `proxy_user_name` varchar(20) DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `bdp_client_token` varchar(2000) DEFAULT NULL,
  `user_config_json` mediumtext COMMENT '用户配置信息',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建用户',
  `create_time` varchar(50) DEFAULT NULL COMMENT '创建时间',
  `modify_user` varchar(50) DEFAULT NULL COMMENT '修改用户',
  `modify_time` varchar(50) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `proxy_user_name_UK` (`proxy_user_name`),
  KEY `qualitis_auth_proxy_user_FK` (`department_id`),
  CONSTRAINT `qualitis_auth_proxy_user_FK` FOREIGN KEY (`department_id`) REFERENCES `qualitis_auth_department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_auth_proxy_user_department definition

CREATE TABLE `qualitis_auth_proxy_user_department` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `proxy_user_id` bigint(20) DEFAULT NULL,
  `department` varchar(50) DEFAULT NULL,
  `sub_department_code` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpmln0snv5mkc203umzzgcjf05` (`proxy_user_id`),
  CONSTRAINT `FKpmln0snv5mkc203umzzgcjf05` FOREIGN KEY (`proxy_user_id`) REFERENCES `qualitis_auth_proxy_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_auth_role definition

CREATE TABLE `qualitis_auth_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `role_type` int(11) DEFAULT NULL,
  `zn_name` varchar(50) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `create_time` varchar(25) DEFAULT NULL,
  `modify_user` varchar(50) DEFAULT NULL,
  `modify_time` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_d6h6ies9p214yj1lmwkegdcdc` (`name`),
  KEY `FK3na1kucq8f675ajtj54onfi6` (`department_id`),
  CONSTRAINT `FK3na1kucq8f675ajtj54onfi6` FOREIGN KEY (`department_id`) REFERENCES `qualitis_auth_department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_auth_role_permission definition

CREATE TABLE `qualitis_auth_role_permission` (
  `id` varchar(32) NOT NULL,
  `permission_id` bigint(20) DEFAULT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建用户',
  `create_time` varchar(50) DEFAULT NULL COMMENT '创建时间',
  `modify_user` varchar(50) DEFAULT NULL COMMENT '修改用户',
  `modify_time` varchar(50) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `FKs9v745h3b0ekhibqipbj84scv` (`permission_id`),
  KEY `FKjricuk1yv825s34s0cy10x3ns` (`role_id`),
  CONSTRAINT `FKjricuk1yv825s34s0cy10x3ns` FOREIGN KEY (`role_id`) REFERENCES `qualitis_auth_role` (`id`),
  CONSTRAINT `FKs9v745h3b0ekhibqipbj84scv` FOREIGN KEY (`permission_id`) REFERENCES `qualitis_auth_permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_auth_user definition

CREATE TABLE `qualitis_auth_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `chinese_name` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `username` varchar(30) DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `user_config_json` mediumtext COMMENT '用户配置',
  `sub_department_code` bigint(20) DEFAULT NULL,
  `bdp_client_token` varchar(2000) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建用户',
  `create_time` varchar(50) DEFAULT NULL COMMENT '创建时间',
  `modify_user` varchar(50) DEFAULT NULL COMMENT '修改用户',
  `modify_time` varchar(50) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_jsqqcjes14hjorfqihq8i10wr` (`username`),
  KEY `FKg2ayqqmqkqbbvvkehqj7fd6la` (`department_id`),
  CONSTRAINT `FKg2ayqqmqkqbbvvkehqj7fd6la` FOREIGN KEY (`department_id`) REFERENCES `qualitis_auth_department` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_auth_user_permission definition

CREATE TABLE `qualitis_auth_user_permission` (
  `id` varchar(32) NOT NULL,
  `permission_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建用户',
  `create_time` varchar(50) DEFAULT NULL COMMENT '创建时间',
  `modify_user` varchar(50) DEFAULT NULL COMMENT '修改用户',
  `modify_time` varchar(50) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `FKfh74vev3awmabhwonewr5oogp` (`permission_id`),
  KEY `FK6yvgd2emno63qw1ecnxl77ipa` (`user_id`),
  CONSTRAINT `FK6yvgd2emno63qw1ecnxl77ipa` FOREIGN KEY (`user_id`) REFERENCES `qualitis_auth_user` (`id`),
  CONSTRAINT `FKfh74vev3awmabhwonewr5oogp` FOREIGN KEY (`permission_id`) REFERENCES `qualitis_auth_permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_auth_user_proxy_user definition

CREATE TABLE `qualitis_auth_user_proxy_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `proxy_user_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_proxy_user_UK` (`proxy_user_id`,`user_id`),
  KEY `FKpmln0snv5mkc203umorgcjf05` (`proxy_user_id`),
  KEY `FKjrpgawp7y8srylpamisntf34y` (`user_id`),
  CONSTRAINT `FKjrpgawp7y8srylpamisntf34y` FOREIGN KEY (`user_id`) REFERENCES `qualitis_auth_user` (`id`),
  CONSTRAINT `FKpmln0snv5mkc203umorgcjf05` FOREIGN KEY (`proxy_user_id`) REFERENCES `qualitis_auth_proxy_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_auth_user_role definition

CREATE TABLE `qualitis_auth_user_role` (
  `id` varchar(32) NOT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `create_time` varchar(25) DEFAULT NULL,
  `modify_user` varchar(50) DEFAULT NULL,
  `modify_time` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_role_UK` (`role_id`,`user_id`),
  KEY `FKta8a7krobg79tw41od6tdsex0` (`role_id`),
  KEY `FKeifs7mfg3qs5igw023vta8e7b` (`user_id`),
  CONSTRAINT `FKeifs7mfg3qs5igw023vta8e7b` FOREIGN KEY (`user_id`) REFERENCES `qualitis_auth_user` (`id`),
  CONSTRAINT `FKta8a7krobg79tw41od6tdsex0` FOREIGN KEY (`role_id`) REFERENCES `qualitis_auth_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_auth_user_tenant_user definition

CREATE TABLE `qualitis_auth_user_tenant_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tenant_user_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `FKWITHUSER` (`user_id`),
  KEY `FKWITHTENANTUSER` (`tenant_user_id`),
  CONSTRAINT `FKWITHTENANTUSER` FOREIGN KEY (`tenant_user_id`) REFERENCES `qualitis_auth_tenant` (`id`),
  CONSTRAINT `FKWITHUSER` FOREIGN KEY (`user_id`) REFERENCES `qualitis_auth_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_execution_parameters definition

CREATE TABLE `qualitis_execution_parameters` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `specify_static_startup_param` bit(1) DEFAULT b'0',
  `static_startup_param` varchar(2000) DEFAULT NULL,
  `abort_on_failure` bit(1) DEFAULT b'0',
  `alert` bit(1) DEFAULT b'0',
  `alert_level` int(11) DEFAULT NULL,
  `alert_receiver` varchar(100) DEFAULT '',
  `project_id` bigint(20) DEFAULT NULL,
  `cluster` varchar(100) DEFAULT NULL,
  `abnormal_database` varchar(100) DEFAULT NULL,
  `abnormal_proxy_user` varchar(50) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `create_time` varchar(25) DEFAULT NULL,
  `modify_user` varchar(50) DEFAULT NULL,
  `modify_time` varchar(25) DEFAULT NULL,
  `filter` varchar(5000) DEFAULT NULL,
  `delete_fail_check_result` bit(1) DEFAULT b'0',
  `upload_rule_metric_value` bit(1) DEFAULT b'0',
  `upload_abnormal_value` bit(1) DEFAULT b'0',
  `union_all` bit(1) DEFAULT b'0',
  `source_table_filter` varchar(5000) DEFAULT NULL,
  `target_table_filter` varchar(5000) DEFAULT NULL,
  `abnormal_data_storage` bit(1) DEFAULT b'0',
  `specify_filter` bit(1) DEFAULT b'0',
  `execution_param` varchar(2000) DEFAULT NULL,
  `whether_noise` bit(1) DEFAULT b'0',
  `execution_variable` bit(1) DEFAULT b'0',
  `advanced_execution` bit(1) DEFAULT b'0',
  `dynamic_partitioning` bit(1) DEFAULT b'0',
  `concurrency_granularity` mediumtext,
  `engine_reuse` bit(1) DEFAULT b'0',
  `top_partition` varchar(2000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `qualitis_execution_parameters_name_IDX` (`name`,`project_id`) USING BTREE,
  KEY `qualitis_execution_parameters_FK` (`project_id`),
  CONSTRAINT `qualitis_execution_parameters_FK` FOREIGN KEY (`project_id`) REFERENCES `qualitis_project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='执行参数模板';


-- qualitis_execution_variable definition

CREATE TABLE `qualitis_execution_variable` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `variable_type` int(11) DEFAULT NULL,
  `variable_name` varchar(64) DEFAULT NULL,
  `variable_value` varchar(2000) DEFAULT NULL,
  `execution_parameters_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `qualitis_execution_variable_execution_parameters_id_IDX` (`execution_parameters_id`) USING BTREE,
  CONSTRAINT `qualitis_execution_variable_FK` FOREIGN KEY (`execution_parameters_id`) REFERENCES `qualitis_execution_parameters` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_linkis_udf_enable_cluster definition

CREATE TABLE `qualitis_linkis_udf_enable_cluster` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `linkis_udf_id` bigint(20) DEFAULT NULL COMMENT 'qualitis侧的UDF关联表ID',
  `enable_cluster` varchar(200) DEFAULT NULL COMMENT 'qualitis侧的UDF关联集群',
  `linkis_id` bigint(20) DEFAULT NULL,
  `linkis_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_linkis_udf_for_cluster` (`linkis_udf_id`),
  CONSTRAINT `FK_linkis_udf_for_cluster` FOREIGN KEY (`linkis_udf_id`) REFERENCES `qualitis_linkis_udf` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='linkis udf映射表关联支持引擎';


-- qualitis_linkis_udf_enable_engine definition

CREATE TABLE `qualitis_linkis_udf_enable_engine` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `linkis_udf_id` bigint(20) DEFAULT NULL COMMENT 'qualitis侧的UDF关联表ID',
  `enable_engine_code` int(5) DEFAULT NULL COMMENT 'qualitis侧的UDF关联引擎',
  PRIMARY KEY (`id`),
  KEY `FK_linkis_udf_for_engine` (`linkis_udf_id`),
  CONSTRAINT `FK_linkis_udf_for_engine` FOREIGN KEY (`linkis_udf_id`) REFERENCES `qualitis_linkis_udf` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='linkis udf映射表关联支持引擎';


-- qualitis_project_label definition

CREATE TABLE `qualitis_project_label` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `label_name` varchar(25) NOT NULL,
  `project_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_project_id_label_name` (`label_name`,`project_id`),
  KEY `FK_qualitis_project_label_project_id` (`project_id`),
  CONSTRAINT `FK_qualitis_project_label_project_id` FOREIGN KEY (`project_id`) REFERENCES `qualitis_project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_project_user definition

CREATE TABLE `qualitis_project_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `permission` int(11) DEFAULT NULL,
  `user_full_name` varchar(30) DEFAULT NULL,
  `user_name` varchar(20) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `automatic_switch` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `FK383dxni31ohf4rl00v5l981ny` (`project_id`),
  KEY `idx_user_name` (`user_name`) USING BTREE,
  CONSTRAINT `FK383dxni31ohf4rl00v5l981ny` FOREIGN KEY (`project_id`) REFERENCES `qualitis_project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_rule_metric_department_user definition

CREATE TABLE `qualitis_rule_metric_department_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `department_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `rule_metric_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbkshkfb278fdedgf79eyp30ly` (`department_id`),
  KEY `FKff1ogtbxi8rv47g850kk5qkc8` (`rule_metric_id`),
  KEY `FKhuctqjbn90jrpjgkjqnla1hs7` (`user_id`),
  CONSTRAINT `FKbkshkfb278fdedgf79eyp30ly` FOREIGN KEY (`department_id`) REFERENCES `qualitis_auth_department` (`id`),
  CONSTRAINT `FKff1ogtbxi8rv47g850kk5qkc8` FOREIGN KEY (`rule_metric_id`) REFERENCES `qualitis_rule_metric` (`id`),
  CONSTRAINT `FKhuctqjbn90jrpjgkjqnla1hs7` FOREIGN KEY (`user_id`) REFERENCES `qualitis_auth_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Created in version qualitis-0.12.0 to manage rule metric authority.';


-- qualitis_scheduled_project definition

CREATE TABLE `qualitis_scheduled_project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dispatching_system_type` varchar(25) DEFAULT NULL,
  `cluster_name` varchar(100) DEFAULT NULL COMMENT '集群名称',
  `name` varchar(100) DEFAULT NULL COMMENT '项目名称',
  `release_user` varchar(30) DEFAULT NULL,
  `job_rsa` varchar(100) DEFAULT NULL COMMENT '上传项目到WTSS的密码',
  `create_time` varchar(30) DEFAULT NULL,
  `create_user` varchar(30) DEFAULT NULL,
  `modify_time` varchar(30) DEFAULT NULL,
  `modify_user` varchar(30) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL COMMENT '本地项目ID',
  PRIMARY KEY (`id`),
  KEY `qualitis_scheduled_project_FK` (`project_id`),
  CONSTRAINT `qualitis_scheduled_project_FK` FOREIGN KEY (`project_id`) REFERENCES `qualitis_project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='调度项目表';


-- qualitis_scheduled_task definition

CREATE TABLE `qualitis_scheduled_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cluster_name` varchar(300) DEFAULT NULL,
  `dispatching_system_type` varchar(256) DEFAULT NULL,
  `wtss_project_name` varchar(100) DEFAULT NULL,
  `wtss_work_flow_name` varchar(100) DEFAULT NULL,
  `wtss_task_name` varchar(100) DEFAULT NULL,
  `task_type` int(4) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `create_time` varchar(25) DEFAULT NULL,
  `modify_user` varchar(50) DEFAULT NULL,
  `modify_time` varchar(25) DEFAULT NULL,
  `release_user` varchar(50) DEFAULT NULL,
  `approve_number` varchar(30) DEFAULT NULL COMMENT '变更单号',
  `release_status` int(4) DEFAULT '0' COMMENT '发布状态（0-未发布，1-已发布）',
  PRIMARY KEY (`id`),
  KEY `qualitis_scheduled_task_project_id_IDX` (`project_id`) USING BTREE,
  CONSTRAINT `qualitis_scheduled_task_FK` FOREIGN KEY (`project_id`) REFERENCES `qualitis_project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='调度任务表';


-- qualitis_scheduled_workflow definition

CREATE TABLE `qualitis_scheduled_workflow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `scheduled_project_id` bigint(20) NOT NULL COMMENT '调度项目ID',
  `name` varchar(100) DEFAULT NULL COMMENT '工作流名称',
  `proxy_user` varchar(30) DEFAULT NULL COMMENT '代理用户',
  `execute_interval` varchar(10) DEFAULT NULL COMMENT '执行频率',
  `execute_date_in_interval` int(4) DEFAULT NULL COMMENT '执行日期',
  `execute_time_in_date` varchar(30) DEFAULT NULL COMMENT '执行时间',
  `create_time` varchar(30) DEFAULT NULL,
  `create_user` varchar(30) DEFAULT NULL,
  `modify_time` varchar(30) DEFAULT NULL,
  `modify_user` varchar(30) DEFAULT NULL,
  `schedule_id` bigint(20) DEFAULT NULL COMMENT '第三方调度系统ID',
  `scheduled_type` varchar(10) DEFAULT NULL COMMENT 'interval-定时调度，signal-信号调度',
  `scheduled_signal_json` varchar(250) DEFAULT NULL COMMENT '调度信号',
  PRIMARY KEY (`id`),
  KEY `qualitis_scheduled_workflow_FK` (`scheduled_project_id`),
  CONSTRAINT `qualitis_scheduled_workflow_FK` FOREIGN KEY (`scheduled_project_id`) REFERENCES `qualitis_scheduled_project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='调度工作流表';


-- qualitis_scheduled_workflow_task_relation definition

CREATE TABLE `qualitis_scheduled_workflow_task_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `scheduled_task_id` bigint(20) DEFAULT NULL,
  `scheduled_project_id` bigint(20) DEFAULT NULL,
  `scheduled_workflow_id` bigint(20) DEFAULT NULL,
  `rule_group_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `qualitis_scheduled_workflow_task_relation_FK` (`scheduled_task_id`),
  KEY `qualitis_scheduled_workflow_task_relation_FK_1` (`scheduled_workflow_id`),
  KEY `qualitis_scheduled_workflow_task_relation_FK_2` (`rule_group_id`),
  CONSTRAINT `qualitis_scheduled_workflow_task_relation_FK` FOREIGN KEY (`scheduled_task_id`) REFERENCES `qualitis_scheduled_task` (`id`),
  CONSTRAINT `qualitis_scheduled_workflow_task_relation_FK_1` FOREIGN KEY (`scheduled_workflow_id`) REFERENCES `qualitis_scheduled_workflow` (`id`),
  CONSTRAINT `qualitis_scheduled_workflow_task_relation_FK_2` FOREIGN KEY (`rule_group_id`) REFERENCES `qualitis_rule_group` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='调度工作流-任务关联表';


-- qualitis_standard_value_version definition

CREATE TABLE `qualitis_standard_value_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cn_name` varchar(128) DEFAULT NULL,
  `en_name` varchar(128) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `content` mediumtext,
  `source` mediumtext,
  `approve_system` varchar(255) DEFAULT NULL,
  `approve_number` varchar(255) DEFAULT NULL,
  `standard_value_type` int(11) DEFAULT NULL,
  `standard_value_id` bigint(20) DEFAULT NULL,
  `is_available` bit(1) DEFAULT b'1',
  `create_user` varchar(50) DEFAULT NULL,
  `create_time` varchar(25) DEFAULT NULL,
  `modify_user` varchar(50) DEFAULT NULL,
  `modify_time` varchar(25) DEFAULT NULL,
  `dev_department_id` bigint(20) DEFAULT NULL COMMENT '开发科室ID',
  `ops_department_id` bigint(20) DEFAULT NULL COMMENT '运维科室ID',
  `dev_department_name` varchar(30) DEFAULT NULL COMMENT '开发科室',
  `ops_department_name` varchar(30) DEFAULT NULL COMMENT '运维科室',
  `std_sub_code` varchar(128) DEFAULT NULL COMMENT '数据标准类别英文名称',
  `std_sub_name` varchar(128) DEFAULT NULL COMMENT '数据标准类别中文名称',
  `std_big_category_code` varchar(128) DEFAULT NULL COMMENT '标准大类英文名称',
  `std_big_category_name` varchar(128) DEFAULT NULL COMMENT '数据标准类别中文名称',
  `small_category_code` varchar(128) DEFAULT NULL COMMENT '标准小类英文名称',
  `small_category_name` varchar(128) DEFAULT NULL COMMENT '标准小类中文名称',
  `std_cn_name` varchar(128) DEFAULT NULL COMMENT '数据标准中文名称',
  `code` varchar(128) DEFAULT NULL COMMENT '数据标准代码--代码英文名',
  `code_name` varchar(128) DEFAULT NULL COMMENT '数据标准代码中文名称--代码中文名',
  `std_small_category_urn` varchar(128) DEFAULT NULL COMMENT '数据标准小类urn',
  `source_value` int(11) DEFAULT NULL COMMENT '标准值来源',
  PRIMARY KEY (`id`),
  KEY `value_version_FK` (`standard_value_id`),
  CONSTRAINT `value_version_FK` FOREIGN KEY (`standard_value_id`) REFERENCES `qualitis_standard_value` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='标准值表';


-- qualitis_static_execution_parameters definition

CREATE TABLE `qualitis_static_execution_parameters` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parameter_type` int(11) DEFAULT NULL,
  `parameter_name` varchar(64) DEFAULT NULL,
  `parameter_value` varchar(2000) DEFAULT NULL,
  `execution_parameters_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `qualitis_static_execution_parameters_FK` (`execution_parameters_id`),
  CONSTRAINT `qualitis_static_execution_parameters_FK` FOREIGN KEY (`execution_parameters_id`) REFERENCES `qualitis_execution_parameters` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_template definition

CREATE TABLE `qualitis_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `action_type` int(11) DEFAULT NULL,
  `cluster_num` int(11) DEFAULT NULL,
  `db_num` int(11) DEFAULT NULL,
  `field_num` int(11) DEFAULT NULL,
  `mid_table_action` varchar(5000) DEFAULT NULL,
  `name` varchar(180) DEFAULT NULL,
  `save_mid_table` bit(1) DEFAULT NULL,
  `show_sql` varchar(5000) DEFAULT NULL,
  `table_num` int(11) DEFAULT NULL,
  `import_export_name` varchar(255) DEFAULT NULL,
  `template_level` int(11) DEFAULT NULL,
  `create_user_id` bigint(20) DEFAULT NULL,
  `modify_user_id` bigint(20) DEFAULT NULL,
  `template_type` int(11) DEFAULT NULL,
  `dev_department_id` bigint(20) DEFAULT NULL COMMENT '开发科室ID',
  `ops_department_id` bigint(20) DEFAULT NULL COMMENT '运维科室ID',
  `dev_department_name` varchar(30) DEFAULT NULL COMMENT '开发科室',
  `ops_department_name` varchar(30) DEFAULT NULL COMMENT '运维科室',
  `en_name` varchar(64) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `verification_level` int(11) DEFAULT NULL,
  `verification_type` int(11) DEFAULT NULL,
  `exception_database` bit(1) DEFAULT b'0',
  `filter_fields` bit(1) DEFAULT b'0',
  `whether_using_functions` bit(1) DEFAULT b'0',
  `statistical_function_id` bigint(20) DEFAULT NULL,
  `create_time` varchar(25) DEFAULT NULL,
  `modify_time` varchar(25) DEFAULT NULL,
  `verification_cn_name` varchar(64) DEFAULT NULL,
  `verification_en_name` varchar(200) DEFAULT NULL,
  `naming_method` int(11) DEFAULT NULL,
  `check_template` int(11) DEFAULT NULL,
  `major_type` varchar(128) DEFAULT NULL,
  `whether_solidification` bit(1) DEFAULT b'0',
  `template_number` varchar(128) DEFAULT NULL,
  `custom_zh_code` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKampr04xxfhfqky18levn4svhb` (`create_user_id`),
  KEY `FKd8bp8wlgc9rslq4w3o4ha6w3m` (`modify_user_id`),
  KEY `qualitis_template_template_type_IDX` (`template_type`) USING BTREE,
  CONSTRAINT `FKampr04xxfhfqky18levn4svhb` FOREIGN KEY (`create_user_id`) REFERENCES `qualitis_auth_user` (`id`),
  CONSTRAINT `FKd8bp8wlgc9rslq4w3o4ha6w3m` FOREIGN KEY (`modify_user_id`) REFERENCES `qualitis_auth_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_template_count_function definition

CREATE TABLE `qualitis_template_count_function` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `qualitis_template_count_function_UN` (`name`,`template_id`),
  KEY `qualitis_template_count_function_FK` (`template_id`),
  CONSTRAINT `qualitis_template_count_function_FK` FOREIGN KEY (`template_id`) REFERENCES `qualitis_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_template_department definition

CREATE TABLE `qualitis_template_department` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `department_id` bigint(20) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3ijoa6qb0b5ckeo9hvob9yqr2` (`department_id`),
  KEY `FKn96g1wp7cwc4v4f7e4e2229qc` (`template_id`),
  CONSTRAINT `FK3ijoa6qb0b5ckeo9hvob9yqr2` FOREIGN KEY (`department_id`) REFERENCES `qualitis_auth_department` (`id`),
  CONSTRAINT `FKn96g1wp7cwc4v4f7e4e2229qc` FOREIGN KEY (`template_id`) REFERENCES `qualitis_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- qualitis_template_mid_table_input_meta definition

CREATE TABLE `qualitis_template_mid_table_input_meta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `concat_template` varchar(3000) DEFAULT NULL,
  `field_type` int(11) DEFAULT NULL,
  `input_type` int(11) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `placeholder` varchar(30) DEFAULT NULL,
  `placeholder_description` varchar(300) DEFAULT NULL,
  `regexp_type` int(11) DEFAULT NULL,
  `replace_by_request` bit(1) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  `en_name` varchar(64) DEFAULT NULL,
  `cn_description` varchar(256) DEFAULT NULL,
  `en_description` varchar(256) DEFAULT NULL,
  `field_multiple_choice` bit(1) DEFAULT b'0',
  `whether_standard_value` bit(1) DEFAULT b'0',
  `whether_new_value` bit(1) DEFAULT b'0',
  `cn_name` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK15rlx42bkg7syh6apwnsss18r` (`parent_id`),
  KEY `FK7antueilfq1itsq2cx29q3xlf` (`template_id`),
  CONSTRAINT `FK15rlx42bkg7syh6apwnsss18r` FOREIGN KEY (`parent_id`) REFERENCES `qualitis_template_mid_table_input_meta` (`id`),
  CONSTRAINT `FK7antueilfq1itsq2cx29q3xlf` FOREIGN KEY (`template_id`) REFERENCES `qualitis_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_template_output_meta definition

CREATE TABLE `qualitis_template_output_meta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `field_name` varchar(50) DEFAULT NULL,
  `field_type` int(11) DEFAULT NULL,
  `output_name` varchar(150) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  `output_en_name` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKia38171mjfi5ix7esd968c0s5` (`template_id`),
  CONSTRAINT `FKia38171mjfi5ix7esd968c0s5` FOREIGN KEY (`template_id`) REFERENCES `qualitis_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_template_statistic_input_meta definition

CREATE TABLE `qualitis_template_statistic_input_meta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `func_name` varchar(5) DEFAULT NULL,
  `name` varchar(200) DEFAULT NULL,
  `result_type` varchar(255) DEFAULT NULL,
  `value` varchar(50) DEFAULT NULL,
  `value_type` int(11) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  `pure_name` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKi1irb2fkjcu16pe7jdwsr7h11` (`template_id`),
  CONSTRAINT `FKi1irb2fkjcu16pe7jdwsr7h11` FOREIGN KEY (`template_id`) REFERENCES `qualitis_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_template_udf definition

CREATE TABLE `qualitis_template_udf` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `qualitis_template_udf_UN` (`name`,`template_id`),
  KEY `qualitis_template_udf_FK` (`template_id`),
  CONSTRAINT `qualitis_template_udf_FK` FOREIGN KEY (`template_id`) REFERENCES `qualitis_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_template_user definition

CREATE TABLE `qualitis_template_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKp4il6ga20u8v6yoyibplo971i` (`template_id`),
  KEY `FKajutcl4gouu76xj4kngnb1mv2` (`user_id`),
  CONSTRAINT `FKajutcl4gouu76xj4kngnb1mv2` FOREIGN KEY (`user_id`) REFERENCES `qualitis_auth_user` (`id`),
  CONSTRAINT `FKp4il6ga20u8v6yoyibplo971i` FOREIGN KEY (`template_id`) REFERENCES `qualitis_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_user_file_id definition

CREATE TABLE `qualitis_user_file_id` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_id` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `hash_values` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKb3ofhh574g7br6ahd8301tic4` (`user_id`),
  CONSTRAINT `FKb3ofhh574g7br6ahd8301tic4` FOREIGN KEY (`user_id`) REFERENCES `qualitis_auth_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- qualitis_alarm_arguments_execution_parameters definition

CREATE TABLE `qualitis_alarm_arguments_execution_parameters` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `alarm_event` int(11) DEFAULT NULL,
  `alarm_level` int(11) DEFAULT NULL,
  `alarm_receiver` varchar(2000) DEFAULT NULL,
  `execution_parameters_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `qualitis_alarm_arguments_execution_parameters_FK` (`execution_parameters_id`),
  CONSTRAINT `qualitis_alarm_arguments_execution_parameters_FK` FOREIGN KEY (`execution_parameters_id`) REFERENCES `qualitis_execution_parameters` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_application_task_rule_alarm_config definition

CREATE TABLE `qualitis_application_task_rule_alarm_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `check_template` int(11) DEFAULT NULL,
  `compare_type` int(11) DEFAULT NULL,
  `output_name` varchar(500) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `threshold` double DEFAULT NULL,
  `task_rule_simple_id` bigint(20) DEFAULT NULL,
  `output_unit` varchar(500) DEFAULT NULL,
  `upload_abnormal_value` bit(1) DEFAULT NULL,
  `upload_rule_metric_value` bit(1) DEFAULT NULL,
  `rule_metric_id` bigint(20) DEFAULT NULL,
  `delete_fail_check_result` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `FKrhyx3i15dja1ipm81v3biges` (`task_rule_simple_id`),
  KEY `FK397t89fbiuklmrgmq5qb67ykn` (`rule_metric_id`),
  CONSTRAINT `FK397t89fbiuklmrgmq5qb67ykn` FOREIGN KEY (`rule_metric_id`) REFERENCES `qualitis_rule_metric` (`id`),
  CONSTRAINT `FKrhyx3i15dja1ipm81v3biges` FOREIGN KEY (`task_rule_simple_id`) REFERENCES `qualitis_application_task_rule_simple` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_rule definition

CREATE TABLE `qualitis_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `alarm` bit(1) DEFAULT NULL,
  `from_content` varchar(3010) DEFAULT NULL,
  `function_content` varchar(3010) DEFAULT NULL,
  `function_type` int(11) DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL,
  `output_name` varchar(170) DEFAULT NULL,
  `rule_template_name` varchar(180) DEFAULT NULL,
  `rule_type` int(11) DEFAULT NULL,
  `where_content` varchar(3010) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `rule_group_id` bigint(20) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  `cs_id` varchar(1000) DEFAULT NULL,
  `abort_on_failure` bit(1) DEFAULT b'0',
  `alert` bit(1) DEFAULT NULL,
  `alert_level` int(11) DEFAULT NULL,
  `alert_receiver` varchar(255) DEFAULT NULL,
  `create_time` varchar(25) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `modify_time` varchar(25) DEFAULT NULL,
  `modify_user` varchar(50) DEFAULT NULL,
  `rule_metric_id` bigint(20) DEFAULT NULL,
  `rule_metric_name` varchar(48) DEFAULT NULL,
  `delete_fail_check_result` bit(1) DEFAULT b'1',
  `specify_static_startup_param` bit(1) DEFAULT NULL,
  `static_startup_param` varchar(255) DEFAULT NULL,
  `detail` varchar(1024) DEFAULT NULL,
  `cn_name` varchar(128) DEFAULT NULL,
  `bash_content` varchar(1000) DEFAULT NULL,
  `execution_parameters_name` varchar(128) DEFAULT NULL,
  `abnormal_database` varchar(100) DEFAULT NULL,
  `cluster` varchar(100) DEFAULT NULL,
  `abnormal_proxy_user` varchar(50) DEFAULT NULL,
  `standard_value_version_id` bigint(20) DEFAULT NULL,
  `standard_value_version_en_name` varchar(128) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `work_flow_name` varchar(180) DEFAULT NULL,
  `work_flow_version` varchar(180) DEFAULT NULL,
  `rule_no` int(20) DEFAULT NULL,
  `enable` bit(1) DEFAULT b'1',
  `union_all` bit(1) DEFAULT b'0',
  `contrast_type` int(11) DEFAULT NULL,
  `work_flow_space` varchar(500) DEFAULT NULL,
  `node_name` varchar(180) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7hv5yh1en46cfwxkqdmixyrn1` (`rule_group_id`),
  KEY `FKf769w3wjl2ywbue7hft6aq8c4` (`template_id`),
  KEY `qualitis_rule_project_id_IDX` (`project_id`) USING BTREE,
  CONSTRAINT `FK7hv5yh1en46cfwxkqdmixyrn1` FOREIGN KEY (`rule_group_id`) REFERENCES `qualitis_rule_group` (`id`),
  CONSTRAINT `FKf769w3wjl2ywbue7hft6aq8c4` FOREIGN KEY (`template_id`) REFERENCES `qualitis_template` (`id`),
  CONSTRAINT `qualitis_rule_FK` FOREIGN KEY (`project_id`) REFERENCES `qualitis_project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_rule_alarm_config definition

CREATE TABLE `qualitis_rule_alarm_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `check_template` int(11) DEFAULT NULL,
  `compare_type` int(11) DEFAULT NULL,
  `threshold` double DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  `template_output_meta_id` bigint(20) DEFAULT NULL,
  `file_output_name` int(11) DEFAULT NULL,
  `file_output_unit` int(11) DEFAULT NULL,
  `upload_abnormal_value` bit(1) DEFAULT NULL,
  `upload_rule_metric_value` bit(1) DEFAULT NULL,
  `rule_metric_id` bigint(20) DEFAULT NULL,
  `delete_fail_check_result` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `FKh2hr5kere1f15udbtkk7cc97n` (`rule_id`),
  KEY `FKjq2m5wga1kmck2haw1o867un6` (`template_output_meta_id`),
  KEY `FKe17j5upcwxk0y5o2x3cfhv1rx` (`rule_metric_id`),
  CONSTRAINT `FKe17j5upcwxk0y5o2x3cfhv1rx` FOREIGN KEY (`rule_metric_id`) REFERENCES `qualitis_rule_metric` (`id`),
  CONSTRAINT `FKh2hr5kere1f15udbtkk7cc97n` FOREIGN KEY (`rule_id`) REFERENCES `qualitis_rule` (`id`),
  CONSTRAINT `FKjq2m5wga1kmck2haw1o867un6` FOREIGN KEY (`template_output_meta_id`) REFERENCES `qualitis_template_output_meta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_rule_datasource definition

CREATE TABLE `qualitis_rule_datasource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cluster_name` varchar(100) DEFAULT '',
  `col_name` mediumtext,
  `datasource_index` int(11) DEFAULT NULL,
  `db_name` varchar(100) DEFAULT NULL,
  `filter` varchar(3200) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `table_name` varchar(100) DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  `file_delimiter` varchar(255) DEFAULT NULL,
  `file_header` bit(1) DEFAULT NULL,
  `file_id` varchar(256) DEFAULT NULL,
  `file_sheet_name` varchar(255) DEFAULT NULL,
  `file_table_desc` varchar(255) DEFAULT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `file_uuid` varchar(50) DEFAULT NULL,
  `proxy_user` varchar(255) DEFAULT NULL,
  `file_hash_values` varchar(200) DEFAULT NULL,
  `linkis_datasource_id` bigint(20) DEFAULT NULL COMMENT 'linkis-datasource primary key',
  `linkis_datasource_version_id` bigint(20) DEFAULT NULL COMMENT 'linkis-datasource vsersion ID',
  `linkis_datasource_name` varchar(100) DEFAULT NULL,
  `datasource_type` int(11) DEFAULT '1',
  `black_col_name` bit(1) DEFAULT b'0',
  `rule_group_id` bigint(20) DEFAULT NULL,
  `sub_system_id` bigint(20) DEFAULT NULL,
  `department_name` varchar(100) DEFAULT NULL,
  `sub_system_name` varchar(25) DEFAULT NULL,
  `department_code` varchar(255) DEFAULT NULL,
  `dev_department_name` varchar(100) DEFAULT NULL,
  `tag_code` varchar(100) DEFAULT NULL,
  `tag_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcbr5lp3b6wuh669qglf3dnc6r` (`rule_id`),
  KEY `idx_cluster_db_table` (`cluster_name`,`db_name`,`table_name`),
  CONSTRAINT `FKcbr5lp3b6wuh669qglf3dnc6r` FOREIGN KEY (`rule_id`) REFERENCES `qualitis_rule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_rule_datasource_env definition

CREATE TABLE `qualitis_rule_datasource_env` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rule_data_source_id` bigint(20) DEFAULT NULL,
  `linkis_env_name` varchar(100) DEFAULT NULL,
  `linkis_env_id` bigint(20) DEFAULT NULL,
  `db_and_table` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_qualitis_rule_datasource` (`rule_data_source_id`),
  CONSTRAINT `FK_qualitis_rule_datasource` FOREIGN KEY (`rule_data_source_id`) REFERENCES `qualitis_rule_datasource` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='linkis多环境数据源';


-- qualitis_rule_datasource_mapping definition

CREATE TABLE `qualitis_rule_datasource_mapping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `left_column_names` varchar(2000) DEFAULT NULL,
  `left_statement` varchar(3000) DEFAULT NULL,
  `operation` int(11) DEFAULT NULL,
  `right_column_names` varchar(2000) DEFAULT NULL,
  `right_statement` varchar(3000) DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  `left_column_types` varchar(2000) DEFAULT NULL,
  `right_column_types` varchar(2000) DEFAULT NULL,
  `mapping_type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKnooevousm8ai6i1b82407cq4x` (`rule_id`),
  CONSTRAINT `FKnooevousm8ai6i1b82407cq4x` FOREIGN KEY (`rule_id`) REFERENCES `qualitis_rule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_rule_udf definition

CREATE TABLE `qualitis_rule_udf` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `udf_name` varchar(64) DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `qualitis_rule_udf_FK` (`rule_id`),
  CONSTRAINT `qualitis_rule_udf_FK` FOREIGN KEY (`rule_id`) REFERENCES `qualitis_rule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_rule_variable definition

CREATE TABLE `qualitis_rule_variable` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cluster_name` varchar(50) DEFAULT NULL,
  `db_name` varchar(50) DEFAULT NULL,
  `input_action_step` int(11) DEFAULT NULL,
  `origin_value` mediumtext,
  `table_name` varchar(200) DEFAULT NULL,
  `value` mediumtext,
  `rule_id` bigint(20) DEFAULT NULL,
  `template_mid_table_input_meta_id` bigint(20) DEFAULT NULL,
  `template_statistics_input_meta_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgvkh60999kiv1hfc5qtr2b7rt` (`rule_id`),
  KEY `FK9cipdyq5a9xmwfdvybhcw2i8d` (`template_mid_table_input_meta_id`),
  KEY `FKkl4loc3y5qpb618cwglvhyd5h` (`template_statistics_input_meta_id`),
  CONSTRAINT `FK9cipdyq5a9xmwfdvybhcw2i8d` FOREIGN KEY (`template_mid_table_input_meta_id`) REFERENCES `qualitis_template_mid_table_input_meta` (`id`),
  CONSTRAINT `FKgvkh60999kiv1hfc5qtr2b7rt` FOREIGN KEY (`rule_id`) REFERENCES `qualitis_rule` (`id`),
  CONSTRAINT `FKkl4loc3y5qpb618cwglvhyd5h` FOREIGN KEY (`template_statistics_input_meta_id`) REFERENCES `qualitis_template_statistic_input_meta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- qualitis_scheduled_front_back_rule definition

CREATE TABLE `qualitis_scheduled_front_back_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rule_group_id` bigint(20) DEFAULT NULL,
  `trigger_type` int(4) DEFAULT NULL,
  `scheduled_task_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `qualitis_scheduled_front_back_rule_scheduled_task_id_IDX` (`scheduled_task_id`) USING BTREE,
  CONSTRAINT `qualitis_scheduled_front_back_rule_FK` FOREIGN KEY (`scheduled_task_id`) REFERENCES `qualitis_scheduled_task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='调度前后置规则表';


-- qualitis_scheduled_signal definition

CREATE TABLE `qualitis_scheduled_signal` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `scheduled_project_id` bigint(20) DEFAULT NULL COMMENT '调度项目ID',
  `scheduled_workflow_id` bigint(20) DEFAULT NULL COMMENT '工作流ID',
  `rule_group_ids` varchar(254) DEFAULT NULL COMMENT '规则组id列表，英文逗号分隔',
  `type` int(4) DEFAULT NULL COMMENT '信号类型',
  `name` varchar(30) DEFAULT NULL COMMENT '信号名称',
  `content_json` varchar(512) DEFAULT NULL COMMENT '信号配置，json格式',
  PRIMARY KEY (`id`),
  KEY `qualitis_scheduled_signal_FK` (`scheduled_workflow_id`),
  KEY `qualitis_scheduled_signal_FK_1` (`scheduled_project_id`),
  CONSTRAINT `qualitis_scheduled_signal_FK` FOREIGN KEY (`scheduled_workflow_id`) REFERENCES `qualitis_scheduled_workflow` (`id`),
  CONSTRAINT `qualitis_scheduled_signal_FK_1` FOREIGN KEY (`scheduled_project_id`) REFERENCES `qualitis_scheduled_project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
-- ddl end

-- dml start
-- -------------------------- 插入数据库预先数据 -------------------------

-- 角色
insert into qualitis_auth_role(id, role_type, name, zn_name) values(1, 1, "ADMIN", "系统管理");
insert into qualitis_auth_role(id, role_type, name, zn_name) values(2, 1, "PROJECTOR", "普通用户");
insert into qualitis_auth_role(id, role_type, name, zn_name) values(3, 2, "Dev", "开发");
insert into qualitis_auth_role(id, role_type, name, zn_name) values(4, 2, "Ops", "运维");
insert into qualitis_auth_role(id, role_type, name, zn_name) values(5, 2, "Test", "测试");


-- 管理员账户
insert into qualitis_auth_user(id, username, password, chinese_name, department) values(1, "admin", "93956d0840b837284103670473a3dd2fca8c46c40c1c4313252d4a192df19474", "管理员", "管理员");

-- 管理员角色
insert into qualitis_auth_user_role(id, user_id, role_id) values("5932425efdfe49949587f51a54e8xffb", 1, 1);
insert into qualitis_auth_user_role(id, user_id, role_id) values("5932425efdfe49949587f51a54e9zffb", 1, 2);

-- 权限
insert into qualitis_auth_permission(id, url, method) values(1, "/qualitis/**", "GET"), (2, "/qualitis/**", "POST"), (3, "/qualitis/**", "DELETE"), (4, "/qualitis/**", "PUT");
insert into qualitis_auth_permission(id, url, method) values(5, "/qualitis/api/v1/projector/**", "GET"), (6, "/qualitis/api/v1/projector/**", "POST"), (7, "/qualitis/api/v1/projector/**", "DELETE"), (8, "/qualitis/api/v1/projector/**", "PUT");

-- 角色权限
insert into qualitis_auth_role_permission(id, role_id, permission_id) values("5932425efdfe49949587f51a54e0affb", 1, 1), ("5932425efdfe49949587f51a54e0affc", 1, 2), ("5932425efdfe49949587f51a54e0affd", 1, 3), ("5932425efdfe49949587f51a54e0affe", 1, 4);
insert into qualitis_auth_role_permission(id, role_id, permission_id) values("5932425efdfe49949587f51a54e0afaa", 2, 5), ("5932425efdfe49949587f51a54e0afab", 2, 6), ("5932425efdfe49949587f51a54e0afac", 2, 7), ("5932425efdfe49949587f51a54e0afad", 2, 8);


-- 规则模版

-- 单表模板

-- 空值检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql,en_name) values(1, "空值检测", 1, 1, 1, 1, "select * from ${database}.${table} where (${filter}) and (${fields} is null)", 1, 1, true, "select count(*) from ${database}.${table} where (${filter}) and (${fields} is null)","Null Verification");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(30005, "{&DATABASE}", 1, "database", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${database}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(30006, "{&TABLE}", 1, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(30007, "{&FIELD}", 1, "fields", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${fields}", 0, 0, 0);
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type) values(1, 20, "{&RECORD_NUMBER_OF_NULL}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type) values(1, 1, "{&RECORD_NUMBER_OF_NULL}", "count", 1);

-- 主键检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql,en_name) values(2, "主键检测", 1, 1, 1, -1, "select * from ${database}.${table} where ${filter} and (${fields}) in (select ${fields} from ${database}.${table} where ${filter} group by ${fields} having count(*) > 1)", 1, 1, true, "select count(*) from ${database}.${table} where ${filter} and (${fields}) in (select ${fields} from ${database}.${table} where ${filter} group by ${fields} having count(*) > 1)","Primary Key Verification");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(30008, "{&DATABASE}", 2, "database", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${database}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(30009, "{&TABLE}", 2, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(30010, "{&FIELD}", 2, "fields", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${fields}", 1, 0, 0);
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type) values(2, 21, "{&PRIMARY_KEY_MULTIPLE_NUMBER}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type) values(2, 2, "{&PRIMARY_KEY_MULTIPLE_NUMBER}", "count", 1);

-- 表行数检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql,en_name)
	values(3, "表行数检测", 1, 1, 1, 0, "select count(*) as myCount from ${database}.${table} where ${filter}", 1, 1, false,
	        "select count(*) from ${database}.${table} where ${filter}","Row Number Verification");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30011,"{&DATABASE}", 3, "database", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${database}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30012,"{&TABLE}", 3, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}", 0, 0, 0);
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(3,22, "{&TABLE_RECORD_NUMBER}", "max", "myCount", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type)
	values(3, 3, "{&TABLE_RECORD_NUMBER}", "max", 1);

-- 平均值检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql,en_name)
	values(4, "平均值检测", 1, 1, 1, 1, "select avg(${fields}) as myAvg from ${database}.${table} where ${filter}", 1, 1, false,
	      "select avg(${fields}) from ${database}.${table} where ${filter}","Average Value Verification");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30013,"{&DATABASE}", 4, "database", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${database}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30014,"{&TABLE}", 4, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30015,"{&FIELD}", 4, "fields", 4, 1, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${fields}", 0, 0, 0);
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(4, 4, "{&AVERAGE_VALUE}", "max", "myAvg", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type)
	values(4,20, "{&AVERAGE_VALUE}", "max", 1);

-- 总和检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql,en_name)
	values(5, "总和检测", 1, 1, 1, 1, "select sum(${fields}) as mySum from ${database}.${table} where ${filter}", 1, 1, false,
	      "select sum(${fields}) from ${database}.${table} where ${filter}","Sum Value Verification");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30016,"{&DATABASE}", 5, "database", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${database}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30017,"{&TABLE}", 5, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30018,"{&FIELD}", 5, "fields", 4, 1, 1, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${fields}", 0, 0, 0);
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(5, 5, "{&SUM_VALUE}", "max", "mySum", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type)
	values(5, 21, "{&SUM_VALUE}", "max", 1);

-- 最大值检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql,en_name)
	values(6, "最大值检测", 1, 1, 1, 1, "select max(${fields}) as myMax from ${database}.${table} where ${filter}", 1, 1, false,
	"select max(${fields}) from ${database}.${table} where ${filter}","Max Value Verification");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30019,"{&DATABASE}", 6, "database", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${database}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30020,"{&TABLE}", 6, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30021,"{&FIELD}", 6, "fields", 4, 1, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${fields}", 0, 0, 0);
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(6, 6, "{&MAX_VALUE}", "max", "myMax", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type)
	values(6,22, "{&MAX_VALUE}", "max", 1);

-- 最小值检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql,en_name)
	values(7, "最小值检测", 1, 1, 1, 1, "select min(${fields}) as myMin from ${database}.${table} where ${filter}", 1, 1, false,
	"select min(${fields}) from ${database}.${table} where ${filter}","Min Value Verification");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30022,"{&DATABASE}", 7, "database", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${database}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30023,"{&TABLE}", 7, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30024,"{&FIELD}", 7, "fields", 4, 1, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${fields}", 0, 0, 0);
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(7, 7, "{&MIN_VALUE}", "max", "myMin", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type)
	values(7,23, "{&MIN_VALUE}", "max", 1);

-- 正则表达式检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql,en_name)
	values(8, "正则表达式检测", 1, 1, 1, 1, "select * from ${database}.${table} where (${filter}) and (${fields} not regexp '${regexp}')", 1, 1, true,
	"select count(*) from ${database}.${table} where (${filter}) and (${fields} not regexp '${regexp}')","Regexp Expression Verification");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30025,"{&DATABASE}", 8, "database", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${database}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30026,"{&TABLE}", 8, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30027,"{&FIELD}", 8, "fields", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${fields}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30028,"{&REGEXP_EXPRESSION}", 8, "regexp", 7, null, true, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${regexp},{&PLEASE_TYPE_IN_REGEXP_EXPRESSION}", 0, 0, 0);
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(8, 8, "{&MISMATCH_RECORD_NUMBER}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type)
	values(8,24, "{&MISMATCH_RECORD_NUMBER}", "count", 1);

-- 时间格式检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql,en_name)
	values(9, "日期格式检测", 1, 1, 1, 1, "select * from ${database}.${table} where (${filter}) and (${fields} not regexp '${regexp}')", 1, 1, true,
	"select count(*) from ${database}.${table} where (${filter}) and (${fields} not regexp '${regexp}')","Date Format Verification");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30029,"{&DATABASE}", 9, "database", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${database}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30030,"{&TABLE}", 9, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30031,"{&FIELD}", 9, "fields", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${fields}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30032,"{&DATE_FORMAT}", 9, "regexp", 7, null, false, 1, "{&REPLACE_PLACEHOLDER_IN_SQL}${regexp},{&CHOOSE_APPROPRIATE}", 0, 0, 0);
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(9, 9, "{&MISMATCH_DATE_FORMAT_RECORD_NUMBER}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type)
	values(9,25, "{&MISMATCH_DATE_FORMAT_RECORD_NUMBER}", "count", 1);

-- 时间格式表达式
INSERT INTO qualitis_template_regexp_expr (key_name, regexp_type, regexp_value) VALUES('yyyyMMdd', 1, '(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)');
INSERT INTO qualitis_template_regexp_expr (key_name, regexp_type, regexp_value) VALUES('yyyy-MM-dd', 1, '(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[13579][26])00))-02-29)');
INSERT INTO qualitis_template_regexp_expr (key_name, regexp_type, regexp_value) VALUES('yyyyMMddHH', 1, '(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})(((0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)(0[1-9]|[12][0-9]|30))|(02(0[1-9]|[1][0-9]|2[0-8])))[0-5]{1}[0-9]{1})|(((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))0229)[0-5]{1}[0-9]{1})');

-- 数值格式检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql,en_name)
	values(10, "数值格式检测", 1, 1, 1, 1, "select * from ${database}.${table} where (${filter}) and (${fields} not regexp '${regexp}')", 1, 1, true,
	"select count(*) from ${database}.${table} where (${filter}) and (${fields} not regexp '${regexp}')","Number Format Verification");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30033,"{&DATABASE}", 10, "database", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${database}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30034,"{&TABLE}", 10, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30035,"{&FIELD}", 10, "fields", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${fields}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30036,"{&NUMBER_FORMAT_REGEXP_EXPRESSION}", 10, "regexp", 7, null, false, 2, "{&REPLACE_PLACEHOLDER_IN_SQL}${regexp}", 0, 0, 0);
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(10, 10, "{&RECORD_NUMBER_OF_MISMATCH_NUMBER_FORMAT}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type)
	values(10,26, "{&RECORD_NUMBER_OF_MISMATCH_NUMBER_FORMAT}", "count", 1);

insert into qualitis_template_regexp_expr(regexp_type, regexp_value) values(2, "^[-+]?\\d+(\\.\\d+)?$");

-- 枚举值检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql,en_name)
	values(11, "枚举值检测", 1, 1, 1, 1, "select * from ${database}.${table} where (${filter}) and (concat_ws(',',${fields}) not in ( ${enumerated_list} ) or (${fields}) is null)", 1, 1, true,
	"select count(*) from ${database}.${table} where (${filter}) and (concat_ws(',',${fields}) not in ( ${enumerated_list} ) or (${fields}) is null)","Enum Value Verification");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30037,"{&DATABASE}", 11, "database", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${database}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30038,"{&TABLE}", 11, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30039,"{&FIELD}", 11, "fields", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${fields}", 1, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30040,"{&ENUM_VALUE}", 11, "enumerated_list", 8, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${list},示例:'1,2,3,4'", 0, 0, 1);
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(11, 11, "{&RECORD_NUMBER_OF_NOT_IN_ENUM_VALUE}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type)
	values(11,27, "{&RECORD_NUMBER_OF_NOT_IN_ENUM_VALUE}", "count", 1);

-- 数值范围检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql,en_name)
	values(12, "数值范围检测", 1, 1, 1, 0, "select * from ${database}.${table} where (${filter}) and (not (${numerical_range}))", 1, 1, true,
	"select count(*) from ${database}.${table} where (${filter}) and (not (${numerical_range}))","Number Range Verification");
insert into qualitis_template_mid_table_input_meta(id,name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30041,"{&DATABASE}", 12, "database", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${database}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id,name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30042,"{&TABLE}", 12, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id,name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30043,"{&NUMBER_RANGE}", 12, "numerical_range", 1, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${numerical_range}", 0, 0, 0);
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(12, 12, "{&RECORD_NUMBER_OF_NOT_NUMBER_RANGE}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type)
	values(12,28, "{&RECORD_NUMBER_OF_NOT_NUMBER_RANGE}", "count", 1);

-- 身份证检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql,en_name)
	values(13, "身份证校验", 1, 1, 1, 1, "select * from ${database}.${table} where (${filter}) and (${fields} not regexp '${regexp}')", 1, 1, true,
	"select count(*) from ${database}.${table} where (${filter}) and (${fields} not regexp '${regexp}')","ID card NO. Verification");
insert into qualitis_template_mid_table_input_meta(id,name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30044,"{&DATABASE}", 13, "database", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${database}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id,name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30045,"{&TABLE}", 13, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id,name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30046,"{&FIELD}", 13, "fields", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${fields}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id,name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30047,"{&IDENTITY_REGEXP_EXPRESSION}", 13, "regexp", 7, null, false, 3, "{&REPLACE_PLACEHOLDER_IN_SQL}${regexp}", 0, 0, 0);
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(13, 13, "{&MISMATCH_IDENTITY_RECORD_NUMBER}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type)
	values(13,29, "{&MISMATCH_IDENTITY_RECORD_NUMBER}", "count", 1);
insert into qualitis_template_regexp_expr(regexp_type, regexp_value) values(3, "^[1-9][0-9]{5}(18|19|20)[0-9]{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)[0-9]{3}[0-9Xx]$");

-- 逻辑类检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql,en_name)
	values(14, "逻辑类校验", 1, 1, 1, 0, "select * from ${database}.${table} where (${filter}) and ( (${condition1}) and not (${condition2}) )", 1, 1, true,
	"select count(*) from ${database}.${table} where (${filter}) and ( (${condition1}) and not (${condition2}) )","Logic Verification");
insert into qualitis_template_mid_table_input_meta(id,name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30048,"{&DATABASE}", 14, "database", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${database}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id,name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30049,"{&TABLE}", 14, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id,name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30050,"{&PRE_CONDITION}", 14, "condition1", 20, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${condition1},{&PLEASE_TYPE_IN_PRE_CONDITION}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id,name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30051,"{&POST_CONDITION}", 14, "condition2", 21, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${condition2}，{&PLEASE_TYPE_IN_POST_CONDITION}", 0, 0, 0);
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(14, 14, "{&RECORD_NUMBER_OF_MISMATCH_LOGIC_VERIFICATION}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type)
	values(14,30, "{&RECORD_NUMBER_OF_MISMATCH_LOGIC_VERIFICATION}", "count", 1);

-- 空字符串检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql,en_name)
	values(15, "空字符串检测", 1, 1, 1, 1, "select * from ${database}.${table} where (${filter}) and (trim(${fields}) = '' )", 1, 1, true,
	"select count(*) from ${database}.${table} where (${filter}) and (trim(${fields}) = '' )","Empty String Verification");
insert into qualitis_template_mid_table_input_meta(id,name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30052,"{&DATABASE}", 15, "database", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${database}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id,name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30053,"{&TABLE}", 15, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id,name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30054,"{&FIELD}", 15, "fields", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${fields}", 0, 0, 0);
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(15, 23, "{&NULL_AND_EMPTY_RECORD_NUMBER}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type)
	values(15,31, "{&NULL_AND_EMPTY_RECORD_NUMBER}", "count", 1);

-- 空值或空字符串检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql,en_name)
	values(16, "空值或空字符串检测", 1, 1, 1, 1, "select * from ${database}.${table} where (${filter}) and (${fields} is null or trim(${fields}) = '' )", 1, 1, true,
	"select count(*) from ${database}.${table} where (${filter}) and (${fields} is null or trim(${fields}) = '' )","Null And Empty String Verification");
insert into qualitis_template_mid_table_input_meta(id,name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30055,"{&DATABASE}", 16, "database", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${database}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id,name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30056,"{&TABLE}", 16, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id,name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value)
	values(30057,"{&FIELD}", 16, "fields", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${fields}", 0, 0, 0);
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(16, 16, "{&NULL_AND_EMPTY_RECORD_NUMBER}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type)
	values(16,32, "{&NULL_AND_EMPTY_RECORD_NUMBER}", "count", 1);

-- 行数据重复检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql, template_level,en_name) values (2149, "行数据重复检测", 1, 1, 1, -1, "select * from ${database}.${table} where ${filter}", 1, 1, false, "select md5, count(1) as md5_count from (select md5(*) as md5 from ${database}.${table} where ${filter}) tmp where ${filter} group by md5 having count(*) > 1", 1,"Primary Line Verification");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values (31391, "{&DATABASE}", 2149, "database", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${database}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values (31392, "{&TABLE}", 2149, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}", 0, 0, 0);
insert into qualitis_template_statistic_input_meta(id, template_id, name, func_name, value, value_type, result_type) values(2091, 2149, "{&LINE_REPEAT_MAX_NUMBER}", "max", "md5_count", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type) values(2149, 2105, "{&LINE_REPEAT_MAX_NUMBER}", "max", 1);

-- 枚举新值监控
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql,en_name)	values(2853, "枚举新值监控", 1, 1, 1, -1, "select * from ${database}.${table} where (${filter}) and (concat_ws(',',${fields}) not in (${standard_value}) and (${fields}) is not null)", 1, 1, true,	"select count(*) from ${database}.${table} where (${filter}) and (concat_ws(',',${fields}) not in (${standard_value}) and (${fields}) is not null)","Enum Value Verification");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(31534, "{&STANDARD_VALUE_EXPRESSION}", 2853, "standard_value", 39, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${standard_value_expression}", 0, 0, 1);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(31535, "{&DATABASE}", 2853, "database", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${database}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(31536, "{&TABLE}", 2853, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(31537, "{&FIELD}", 2853, "fields", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${fields}", 1, 0, 0);
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type) values(2853, 2668, "{&RECORD_NUMBER_OF_NOT_IN_ENUM_VALUE}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type) values(2853, 2682, "{&RECORD_NUMBER_OF_NOT_IN_ENUM_VALUE}", "count", 1);

-- 数值范围新值监控
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql,en_name) values(2860, "数值范围新值监控", 1, 1, 1, 0, "select (${intermediate_expression}) as custom_column from ${database}.${table} where (${filter}) and !(${intermediate_expression} >=${minimum} and ${intermediate_expression} <=${maximum})", 1, 1, true, "select count((${intermediate_expression}) as custom_column) from ${database}.${table} where (${filter}) and !(${intermediate_expression} >=${minimum} and ${intermediate_expression} <=${maximum})","Number Range Verification");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(31612, "{&DATABASE}", 2860, "database", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${database}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(31613, "{&TABLE}", 2860, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(31614, "{&INTERMEDIATE_EXPRESSION}", 2860, "intermediate_expression", 37, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${intermediate_expression}", 0, 0, 1);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(31615, "{&MAXIMUM}", 2860, "maximum", 36, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${maximum}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(31616, "{&MINIMUM}", 2860, "minimum", 38, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${minimum}", 0, 0, 0);
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type) values(2860, 2684, "{&RECORD_NUMBER_OF_NOT_NUMBER_RANGE}", "count", "custom_column", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type) values(2860, 2698, "{&RECORD_NUMBER_OF_NOT_NUMBER_RANGE}", "count", 1);

-- 重复数据检测
insert into qualitis_template(id, name, en_name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql, template_level) values (4000,"重复数据检测", "Repeat data check", 1, 1, 1, -1, "select ${fields} from ${database}.${table} where ${filter}", 1, 1, false, "select max(md5_count) from (select md5(${fields}) as md5, count(1) as md5_count from ${database}.${table} where ${filter}) tmp where ${filter} group by tmp.md5 having count(*) > 1", 1);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description) values (4001, "{&DATABASE}", 4000, "database", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${database}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description) values (4002, "{&TABLE}", 4000, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice) values (4003, "{&FIELDS}", 4000, "fields", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${fields}", 1);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)  values(4004, "{&CONDITION}", 4000, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}");
insert into qualitis_template_statistic_input_meta(id, template_id, name, func_name, value, value_type, result_type) values(3501, 4000, "重复数据数量", "max", "md5_count", 1, "Long");
insert into qualitis_template_output_meta(id, template_id, output_name, field_name, field_type) values(4001, 4000, "重复数据数量", "max", 1);


-- 跨表模版

-- 字段一致性校验
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql,en_name)
	values(17, "字段一致性校验", 1, 2, 2, 0, "SELECT CASE WHEN ${compare_express} THEN 1 ELSE 0 END AS compare_result FROM ${left_database}.${left_table} tmp1 ${contrast_type} ${right_database}.${right_table} tmp2 ON ${join_express} WHERE (${result_filter})", 3, 1, true,
	"SELECT CASE WHEN ${compare_express} THEN 1 ELSE 0 END AS compare_result FROM ${left_database}.${left_table} tmp1 ${contrast_type} ${right_database}.${right_table} tmp2 ON ${join_express} WHERE (${result_filter})","Field consistency check");

insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values(30058,"{&SOURCE_DATABASE}", 17, "left_database", 11, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${left_database}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values(30059,"{&SOURCE_TABLE}", 17, "left_table", 12, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${left_table}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values(30060,"{&TARGET_DATABASE}", 17, "right_database", 13, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${right_database}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values(30061,"{&TARGET_TABLE}", 17, "right_table", 14, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${right_table}");

insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
 values(10000, "{&JOIN_CONDITION}", 17, "join_express", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${join_express}", "(${left_statement} ${operation} ${right_statement})");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
 values(10020, "{&COMPARE_CONDITION}", 17, "compare_express", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${compare_express}", "(${left_statement} ${operation} ${right_statement})");

insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values(30062,"{&JOIN_LEFT_EXPRESSION}", null, "left_statement", 15, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${left_statement}", 10000);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values(30063,"{&JOIN_OPERATION}", null, "operation", 16, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${operation}", 10000);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
 values(30064,"{&JOIN_RIGHT_EXPRESSION}", null, "right_statement", 17, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${right_statement}", 10000);

insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
 values(31638, "{&JOIN_LEFT_EXPRESSION}", null, "left_statement", 15, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${left_statement}", 10020);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
 values(31639, "{&JOIN_OPERATION}", null, "operation", 16, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${operation}", 10020);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
 values(31640, "{&JOIN_RIGHT_EXPRESSION}", null, "right_statement", 17, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${right_statement}", 10020);

insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values(31641, "{&CONTRAST_TYPE}", 17, "contrast_type", 25, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${contrast_type}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
 values(31462, "{&FILTER_IN_RESULT}", 17, "result_filter", 34, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${result_filter}");

insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(17, 17, "", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type)
	values(17,33, "{&DIFFERENT_RECORD_BETWEEN_SOURCE_AND_TARGET_TABLE}", "count", 1);


-- 行数据一致性校验
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql, template_level,en_name)
	values(20, "行数据一致性校验", 1, 2, 2, 0, "SELECT tmp1.* FROM (SELECT SOURCE_GROUP_BY_COLUMNS, count(1) as qualitis_mul_db_accuracy_num FROM ${left_database}.${left_table} group by SOURCE_GROUP_BY_COLUMNS) tmp1 ${contrast_type} (SELECT TARGET_GROUP_BY_COLUMNS, count(1) as qualitis_mul_db_accuracy_num FROM ${right_database}.${right_table} group by TARGET_GROUP_BY_COLUMNS) tmp2 ON SOURCE_GROUP_BY_COLUMNS = TARGET_GROUP_BY_COLUMNS WHERE ( NOT (source_column_is_null) AND (target_column_is_null) )", 3, 1, true,
	"SELECT count(tmp1.*) FROM (SELECT SOURCE_GROUP_BY_COLUMNS, count(1) as qualitis_mul_db_accuracy_num FROM ${left_database}.${left_table} group by SOURCE_GROUP_BY_COLUMNS) tmp1 ${contrast_type} (SELECT TARGET_GROUP_BY_COLUMNS, count(1) as qualitis_mul_db_accuracy_num FROM ${right_database}.${right_table} group by TARGET_GROUP_BY_COLUMNS) tmp2 ON SOURCE_GROUP_BY_COLUMNS = TARGET_GROUP_BY_COLUMNS WHERE ( NOT (source_column_is_null) AND (target_column_is_null) )", 1,"Row data consistency check");

insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values(30910, "{&SOURCE_DATABASE}", 20, "left_database", 11, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${left_database}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values(30911, "{&SOURCE_TABLE}", 20, "left_table", 12, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${left_table}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values(30912, "{&TARGET_DATABASE}", 20, "right_database", 13, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${right_database}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values(30913, "{&TARGET_TABLE}", 20, "right_table", 14, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${right_table}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values(30914, "{&SOURCE_GROUP_BY_COLUMNS}", 20, "SOURCE_GROUP_BY_COLUMNS", 22, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${SOURCE_GROUP_BY_COLUMNS}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values(30915, "{&TARGET_GROUP_BY_COLUMNS}", 20, "TARGET_GROUP_BY_COLUMNS", 23, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${TARGET_GROUP_BY_COLUMNS}");

insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
 values(10011, "{&SOURCE_TABLE_COLUMN_IS_NULL}", 20, "source_column_is_null", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_column_is_null}", "${source_column} IS NULL");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
 values(10012, "{&TARGET_TABLE_COLUMN_IS_NULL}", 20, "target_column_is_null", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_column_is_null}", "${target_column} IS NULL");

insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values(30919,"{&JOIN_LEFT_FILED}", null, "source_column", 18, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_column}", 10011);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values(30920,"{&JOIN_RIGHT_FILED}", null, "target_column", 19, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_column}", 10012);

insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
 values(30921, "{&CONTRAST_TYPE}", 20, "contrast_type", 25, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${contrast_type}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
 values(31922, "{&FILTER_IN_RESULT}", 20, "result_filter", 34, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${result_filter}");

insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(20, 25, "{&MULTI-DATABASE_ACCURACY_VERIFICATION}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type)
	values(20, 645, "{&DIFFERENT_RECORD_BETWEEN_SOURCE_AND_TARGET_TABLE}", "count", 1);


-- 文件模板

-- 目录容量
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql, template_level,en_name) values (3500, "目录容量", 1, 1, 1, -1, "", 4, 1, false, "", 1,"Directory Capacity");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description) values (32100, "{&DATABASE}", 3500, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description) values (32101, "{&TABLE}", 3500, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)  values(32102,"{&CONDITION}", 3500, "condition", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${condition}");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type) values(3500, 3000, "目录容量", "", 1);

-- 目录文件数
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql, template_level,en_name) values (3501, "目录文件数", 1, 1, 1, -1, "", 4, 1, false, "", 1,"Number Catalog Files");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description) values (32103, "{&DATABASE}", 3501, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description) values (32104, "{&TABLE}", 3501, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)  values(32105,"{&CONDITION}", 3501, "condition", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${condition}");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type) values(3501, 3001, "目录文件数", "", 1);

-- 分区数
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql, template_level,en_name) values (3502, "分区数", 1, 1, 1, -1, "", 4, 1, false, "", 1,"Number Partitions");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description) values (32106, "{&DATABASE}", 3502, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description) values (32107, "{&TABLE}", 3502, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)  values(32108,"{&CONDITION}", 3502, "condition", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${condition}");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type) values(3502, 3002, "分区数", "", 1);

update qualitis_template set template_level = 1;


-- 追加过滤条件占位符
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34000, "{&CONDITION}", 1, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34001,"{&CONDITION}", 2, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34002,"{&CONDITION}", 3, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34003,"{&CONDITION}", 4, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34004,"{&CONDITION}", 5, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34005,"{&CONDITION}", 6, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34006,"{&CONDITION}", 7, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34007,"{&CONDITION}", 8, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34008,"{&CONDITION}", 9, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34009,"{&CONDITION}", 10, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34010,"{&CONDITION}", 11, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34011,"{&CONDITION}", 12, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34012,"{&CONDITION}", 13, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34013,"{&CONDITION}", 14, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34014,"{&CONDITION}", 15, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34015,"{&CONDITION}", 16, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34018,"{&CONDITION}", 3500, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34019,"{&CONDITION}", 3501, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34020,"{&CONDITION}", 3502, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34021,"{&CONDITION}", 2149, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34022,"{&CONDITION}", 2853, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, field_multiple_choice, whether_standard_value, whether_new_value) values(34023,"{&CONDITION}", 2860, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", 0, 0, 0);


-- 模板支持数据类型初始化: hive, mysql, tdsql, fps
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(1,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(2,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(3,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(4,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(5,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(6,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(7,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(8,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(9,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(10,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(11,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(12,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(13,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(14,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(15,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(16,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(17,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(20,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(1,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(2,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(3,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(4,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(5,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(6,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(7,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(8,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(9,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(10,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(11,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(12,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(13,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(14,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(15,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(16,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(17,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(20,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(1,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(2,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(3,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(4,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(5,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(6,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(7,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(8,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(9,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(10,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(11,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(12,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(13,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(14,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(15,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(16,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(17,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(20,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(1,5);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(2,5);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(3,5);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(4,5);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(5,5);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(6,5);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(7,5);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(8,5);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(9,5);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(10,5);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(11,5);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(12,5);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(13,5);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(14,5);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(15,5);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(16,5);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(17,5);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(20,5);

insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(2149,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(2149,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(2149,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(2149,5);

insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values (2853, 1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values (2853, 2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values (2853, 3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values (2853, 5);

insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values (2860, 1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values (2860, 2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values (2860, 3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values (2860, 5);

insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values (3500, 1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values (3500, 2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values (3500, 3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values (3500, 5);

insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values (3501, 1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values (3501, 2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values (3501, 3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values (3501, 5);

insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values (3502, 1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values (3502, 2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values (3502, 3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values (3502, 5);

insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(4000,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(4000,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(4000,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(4000,5);

insert into qualitis_auth_data_visibility(table_data_id ,table_data_type ,department_sub_id ,department_sub_name ) SELECT id, 'ruleTemplate', 0, '全行' from qualitis_template where template_level = 1;

-- 指标类型
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("IT数据监控指标", "it-metrics");
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("业务数据监控指标", "product-metrics");
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("自定义监控指标", "custom-metrics");


insert into qualitis_config_system(id, key_name, `value`) values(1, "save_database_pattern", "${USERNAME}_ind");

insert into qualitis_auth_list(app_id, app_token) values("linkis_id", "***REMOVED***");


-- 插入默认占位符
insert into qualitis_template_default_input_meta(id,type, placeholder, placeholder_desc, cn_name, en_name, cn_desc, en_desc, support_fields, support_standard, support_new_value) values(17,5, "database", "{&REPLACE_PLACEHOLDER_IN_SQL}${database}", "数据库", "database", "数据库元信息", "Database meta info", false, false,false);
insert into qualitis_template_default_input_meta(id,type, placeholder, placeholder_desc, cn_name, en_name, cn_desc, en_desc, support_fields, support_standard, support_new_value) values(18,3, "table", "{&REPLACE_PLACEHOLDER_IN_SQL}${table}", "数据表", "table", "数据表元信息", "Table meta info", false, false,false);
insert into qualitis_template_default_input_meta(id,type, placeholder, placeholder_desc, cn_name, en_name, cn_desc, en_desc, support_fields, support_standard, support_new_value) values(19,4, "fields", "{&REPLACE_PLACEHOLDER_IN_SQL}${fields}", "字段", "fields", "字段元信息", "fields info", false, false,false);
insert into qualitis_template_default_input_meta(id,type, placeholder, placeholder_desc, cn_name, en_name, cn_desc, en_desc, support_fields, support_standard, support_new_value) values(20,9, "filter", "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}", "基础过滤条件", "filter", "基础过滤条件元信息", "filter info", false, false,false);
insert into qualitis_template_default_input_meta(id,type, placeholder, placeholder_desc, cn_name, en_name, cn_desc, en_desc, support_fields, support_standard, support_new_value) values(21,8, "enumerated_list", "{&REPLACE_PLACEHOLDER_IN_SQL}${enumerated_list}", "枚举值", "enumerated_list range", "枚举值元信息", "enumerated list info", false, false,false);
insert into qualitis_template_default_input_meta(id,type, placeholder, placeholder_desc, cn_name, en_name, cn_desc, en_desc, support_fields, support_standard, support_new_value) values(22,28, "numerical_range", "{&REPLACE_PLACEHOLDER_IN_SQL}${numerical_range}", "数值范围", "numerical_range", "数值范围元信息", "numerical range info", false, false,false);
insert into qualitis_template_default_input_meta(id,type, placeholder, placeholder_desc, cn_name, en_name, cn_desc, en_desc, support_fields, support_standard, support_new_value) values(23,29, "express", "{&REPLACE_PLACEHOLDER_IN_SQL}${express}", "表达式", "express", "表达式元信息", "express", false, false,false);
insert into qualitis_template_default_input_meta(id,type, placeholder, placeholder_desc, cn_name, en_name, cn_desc, en_desc, support_fields, support_standard, support_new_value) values(24,11, "left_database", "{&REPLACE_PLACEHOLDER_IN_SQL}${left_database}", "源数据库", "left_database", "源数据库元信息", "left database info", false, false,false);
insert into qualitis_template_default_input_meta(id,type, placeholder, placeholder_desc, cn_name, en_name, cn_desc, en_desc, support_fields, support_standard, support_new_value) values(25,12, "left_table", "{&REPLACE_PLACEHOLDER_IN_SQL}${left_table}", "源数据表", "left_table", "源数据表元信息", "left table info", false, false,false);
insert into qualitis_template_default_input_meta(id,type, placeholder, placeholder_desc, cn_name, en_name, cn_desc, en_desc, support_fields, support_standard, support_new_value) values(26,13, "right_database", "{&REPLACE_PLACEHOLDER_IN_SQL}${right_database}", "目标数据库", "right_database", "目标数据库元信息", "right database info", false, false,false);
insert into qualitis_template_default_input_meta(id,type, placeholder, placeholder_desc, cn_name, en_name, cn_desc, en_desc, support_fields, support_standard, support_new_value) values(27,14, "right_table", "{&REPLACE_PLACEHOLDER_IN_SQL}${right_table}", "目标数据表", "right_table", "目标数据表元信息", "right table info", false, false,false);
insert into qualitis_template_default_input_meta(id,type, placeholder, placeholder_desc, cn_name, en_name, cn_desc, en_desc, support_fields, support_standard, support_new_value) values(28,30, "left_filter", "{&REPLACE_PLACEHOLDER_IN_SQL}${left_filter}", "源基础过滤条件", "left_filter", "源基础过滤条件元信息", "left filter info", false, false,false);
insert into qualitis_template_default_input_meta(id,type, placeholder, placeholder_desc, cn_name, en_name, cn_desc, en_desc, support_fields, support_standard, support_new_value) values(29,31, "right_filter", "{&REPLACE_PLACEHOLDER_IN_SQL}${right_filter}", "目标基础过滤条件", "right_filter", "目标基础过滤条件元信息", "right filter info", false, false,false);
insert into qualitis_template_default_input_meta(id,type, placeholder, placeholder_desc, cn_name, en_name, cn_desc, en_desc, support_fields, support_standard, support_new_value) values(30,32, "join_express", "{&REPLACE_PLACEHOLDER_IN_SQL}${join_express}", "连接字段设置", "join_express", "连接字段设置元信息", "join express info", false, false,false);
insert into qualitis_template_default_input_meta(id,type, placeholder, placeholder_desc, cn_name, en_name, cn_desc, en_desc, support_fields, support_standard, support_new_value) values(31,33, "compare_express", "{&REPLACE_PLACEHOLDER_IN_SQL}${compare_express}", "比对字段设置", "compare_express", "比对字段设置元信息", "compare express info", false, false,false);
insert into qualitis_template_default_input_meta(id,type, placeholder, placeholder_desc, cn_name, en_name, cn_desc, en_desc, support_fields, support_standard, support_new_value) values(32,34, "result_filter", "{&REPLACE_PLACEHOLDER_IN_SQL}${result_filter}", "比对结果过滤条件", "result_filter", "比对结果过滤条件元信息", "result filter info", false, false,false);
insert into qualitis_template_default_input_meta(id, type, placeholder, placeholder_desc, cn_name, en_name, cn_desc, en_desc, support_fields, support_standard, support_new_value) values(33, 39, "standard_value", "{&REPLACE_PLACEHOLDER_IN_SQL}${standard_value}", "标准值", "standard_value_expression", "标准值元信息", "standard value expression meta info", false, false,false);

INSERT INTO qualitis_template_naming_conventions (id,major_categories,kind,create_user,create_time,modify_user,modify_time) VALUES (1,'{"en_name":"basics","zh_name":"IT基础","abbreviation":"A"}','[{"en_name":"Fluctuate","zh_name":"波动","abbreviation":"F"},{"en_name":"New","zh_name":"新值","abbreviation":"N"},{"en_name":"Balance","zh_name":"对账","abbreviation":"B"}]',NULL,NULL,NULL,NULL);
INSERT INTO qualitis_template_naming_conventions (id,major_categories,kind,create_user,create_time,modify_user,modify_time) VALUES (2,'{"en_name":"general_knowledge","zh_name":"IT通识","abbreviation":"B"}','[{"en_name":"Common","zh_name":"常规","abbreviation":"C"},{"en_name":"Anti","zh_name":"反洗钱","abbreviation":"A"},{"en_name":"Regulate","zh_name":"监管报送","abbreviation":"R"}]',NULL,NULL,NULL,NULL);

-- dml end