-- NEW TABLE


DROP TABLE IF EXISTS `qualitis_auth_department`;

CREATE TABLE `qualitis_auth_department` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_department_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `qualitis_project_event`;

CREATE TABLE `qualitis_project_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` varchar(5000) NOT NULL,
  `time` varchar(25) DEFAULT NULL,
  `project_id` bigint(20) NOT NULL,
  `event_type` int(11) DEFAULT NULL,
  `field` varchar(50) DEFAULT NULL,
  `before_modify` varchar(200) DEFAULT NULL,
  `after_modify` varchar(200) DEFAULT NULL,
  `modify_user` varchar(50) DEFAULT NULL,
  `execute_user` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3yw1cm38kqld5s9xc1l9qdf04` (`project_id`),
  CONSTRAINT `FK3yw1cm38kqld5s9xc1l9qdf04` FOREIGN KEY (`project_id`) REFERENCES `qualitis_project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `qualitis_rule_datasource_count`;

CREATE TABLE `qualitis_rule_datasource_count` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `datasource_name` mediumtext NOT NULL,
  `datasource_count` int(11) NOT NULL DEFAULT '1',
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `qualitis_rule_datasouce_count_UN` (`user_id`,`datasource_name`(25))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `qualitis_rule_metric`;

CREATE TABLE `qualitis_rule_metric` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(512) NOT NULL,
  `cn_name` varchar(512) NOT NULL,
  `metric_desc` varchar(100) NOT NULL,
  `metric_level` int(5) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `create_time` varchar(25) DEFAULT NULL,
  `modify_time` varchar(25) DEFAULT NULL,
  `modify_user` varchar(50) DEFAULT NULL,
  `sub_system_name` varchar(100) DEFAULT NULL,
  `full_cn_name` varchar(100) DEFAULT NULL,
  `type` int(11) DEFAULT '0',
  `available` bit(1) DEFAULT b'0',
  `frequency` int(11) DEFAULT NULL,
  `en_code` varchar(50) NOT NULL DEFAULT '',  
  `dev_department_name` varchar(30) DEFAULT NULL,
  `ops_department_name` varchar(30) DEFAULT NULL,  
  `department_name` varchar(255) DEFAULT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `buss_code` int(5) DEFAULT NULL,
  `buss_custom` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNI_RULE_METRIC_EN_CODE` (`en_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `qualitis_rule_metric_department_user`;

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


DROP TABLE IF EXISTS `qualitis_rule_metric_type_config`;

CREATE TABLE `qualitis_rule_metric_type_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cn_name` varchar(50) DEFAULT NULL,
  `en_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `qualitis_template_datasource_type`;

CREATE TABLE `qualitis_template_datasource_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `template_id` bigint(20) DEFAULT NULL,
  `data_source_type_id` int(5) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `template_id` (`template_id`,`data_source_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `qualitis_template_department`;

CREATE TABLE `qualitis_template_department` (
  `id` bigint(32) NOT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_department_template` (`department_id`,`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- MODIFY TABLE


-- ----------------------------
-- for qualitis_auth_user
-- ----------------------------
ALTER TABLE qualitis_auth_user CHANGE username user_name varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_auth_user ADD lock_time bigint(20) NULL;
ALTER TABLE qualitis_auth_user ADD login_error_count int(11) NULL;
ALTER TABLE qualitis_auth_user ADD login_error_time bigint(20) NULL;
ALTER TABLE qualitis_auth_user ADD department_id bigint(20) NULL;
-- ----------------------------
-- for qualitis_auth_role
-- ----------------------------
ALTER TABLE qualitis_auth_role ADD department_id bigint(20) NULL;
-- ----------------------------
-- for qualitis_application
-- ----------------------------
ALTER TABLE qualitis_application MODIFY COLUMN exception_message TEXT NULL;
ALTER TABLE qualitis_application ADD project_id bigint(20) NULL;
ALTER TABLE qualitis_application ADD rule_group_id bigint(20) NULL;
ALTER TABLE qualitis_application ADD application_comment int(11) NULL;
ALTER TABLE qualitis_application ADD project_name varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application ADD cluster_name varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application ADD startup_param varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application ADD rule_group_name varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application ADD rule_datasource varchar(3000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application ADD execution_param varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application ADD set_flag varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application ADD run_date varchar(25) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application ADD filter_partition varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
-- ----------------------------
-- for qualitis_application_task
-- ----------------------------
ALTER TABLE qualitis_application_task MODIFY COLUMN task_remote_id BIGINT(20) NULL;
ALTER TABLE qualitis_application_task ADD task_comment int(11) NULL;
ALTER TABLE qualitis_application_task ADD running_time bigint(20) NULL;
ALTER TABLE qualitis_application_task ADD task_exec_id varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application_task ADD task_proxy_user varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application_task ADD new_progress_time bigint(20) NULL;
ALTER TABLE qualitis_application_task ADD progress double NULL;
-- ----------------------------
-- for qualitis_application_task_datasource
-- ----------------------------
ALTER TABLE qualitis_application_task_datasource ADD project_id bigint(20) NULL;
ALTER TABLE qualitis_application_task_datasource ADD datasource_type int(11) DEFAULT 1 NULL;
-- ----------------------------
-- for qualitis_application_task_result
-- ----------------------------
ALTER TABLE qualitis_application_task_result MODIFY COLUMN value VARCHAR(128) NULL;
ALTER TABLE qualitis_application_task_result ADD run_date bigint(20) NULL;
ALTER TABLE qualitis_application_task_result ADD rule_metric_id bigint(20) NULL;
ALTER TABLE qualitis_application_task_result ADD save_result bit(1) DEFAULT b'1' NULL;
ALTER TABLE qualitis_application_task_result ADD department_code varchar(25) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
-- ----------------------------
-- for qualitis_application_task_rule_alarm_config
-- ----------------------------
ALTER TABLE qualitis_application_task_rule_alarm_config ADD rule_metric_id bigint(20) NULL;
ALTER TABLE qualitis_application_task_rule_alarm_config ADD upload_abnormal_value bit(1) DEFAULT b'0' NULL;
ALTER TABLE qualitis_application_task_rule_alarm_config ADD upload_rule_metric_value bit(1) DEFAULT b'0' NULL;
ALTER TABLE qualitis_application_task_rule_alarm_config ADD delete_fail_check_result bit(1) DEFAULT b'0' NULL;
ALTER TABLE qualitis_application_task_rule_alarm_config ADD output_unit varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
-- ----------------------------
-- for qualitis_application_task_rule_simple
-- ----------------------------
ALTER TABLE qualitis_application_task_rule_simple MODIFY COLUMN mid_table_name varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application_task_rule_simple ADD template_name varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application_task_rule_simple ADD rule_detail varchar(340) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application_task_rule_simple ADD delete_fail_check_result bit(1) DEFAULT b'1' NULL;
ALTER TABLE qualitis_application_task_rule_simple ADD cn_name varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application_task_rule_simple ADD project_cn_name varchar(170) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application_task_rule_simple ADD rule_group_name varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
-- ----------------------------
-- for qualitis_config_cluster_info
-- ----------------------------
ALTER TABLE qualitis_config_cluster_info DROP COLUMN hive_server2_address;
ALTER TABLE qualitis_config_cluster_info DROP COLUMN meta_store_address;
ALTER TABLE qualitis_config_cluster_info ADD skip_data_size varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
-- ----------------------------
-- for qualitis_project
-- ----------------------------
ALTER TABLE qualitis_project ADD cn_name varchar(170) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
-- ----------------------------
-- for qualitis_rule
-- ----------------------------
ALTER TABLE qualitis_rule ADD cs_id varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_rule ADD create_user varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_rule ADD create_time varchar(25) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_rule ADD modify_time varchar(25) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_rule ADD modify_user varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_rule ADD delete_fail_check_result bit(1) DEFAULT b'1' NULL;
ALTER TABLE qualitis_rule ADD specify_static_startup_param bit(1) DEFAULT b'0' NULL;
ALTER TABLE qualitis_rule ADD detail varchar(340) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_rule ADD cn_name varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_rule ADD static_startup_param varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
-- ----------------------------
-- for qualitis_rule_alarm_config
-- ----------------------------
ALTER TABLE qualitis_rule_alarm_config ADD rule_metric_id bigint(20) NULL;
ALTER TABLE qualitis_rule_alarm_config ADD upload_abnormal_value bit(1) DEFAULT b'0' NULL;
ALTER TABLE qualitis_rule_alarm_config ADD upload_rule_metric_value bit(1) DEFAULT b'0' NULL;
ALTER TABLE qualitis_rule_alarm_config ADD delete_fail_check_result bit(1) DEFAULT b'0' NULL;
ALTER TABLE qualitis_rule_alarm_config ADD file_output_name int(11) NULL;
ALTER TABLE qualitis_rule_alarm_config ADD file_output_unit int(11) NULL;
-- ----------------------------
-- for qualitis_rule_datasource
-- ----------------------------
ALTER TABLE qualitis_rule_datasource MODIFY COLUMN col_name MEDIUMTEXT NULL;
ALTER TABLE qualitis_rule_datasource ADD proxy_user varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_rule_datasource ADD black_col_name bit(1) DEFAULT b'1' NULL;
ALTER TABLE qualitis_rule_datasource ADD linkis_datasoure_id bigint(20) NULL;
ALTER TABLE qualitis_rule_datasource ADD datasource_type int(11) DEFAULT 1 NULL;
ALTER TABLE qualitis_rule_datasource ADD linkis_datasoure_version_id bigint(20) NULL;
ALTER TABLE qualitis_rule_datasource ADD linkis_datasource_name varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
-- ----------------------------
-- for qualitis_rule_datasource_mapping
-- ----------------------------
ALTER TABLE qualitis_rule_datasource_mapping ADD left_column_types varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_rule_datasource_mapping ADD right_column_types varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
-- ----------------------------
-- for qualitis_rule_group
-- ----------------------------
ALTER TABLE qualitis_rule_group ADD version varchar(25) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
-- ----------------------------
-- for qualitis_rule_variable
-- ----------------------------
ALTER TABLE qualitis_rule_variable MODIFY COLUMN table_name varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_rule_variable MODIFY COLUMN value MEDIUMTEXT NULL;
-- ----------------------------
-- for qualitis_template
-- ----------------------------
ALTER TABLE qualitis_template ADD create_user_id bigint(20) NULL;
ALTER TABLE qualitis_template ADD modify_user_id bigint(20) NULL;
ALTER TABLE qualitis_template ADD template_level int(11) DEFAULT 1 NULL;
ALTER TABLE qualitis_template ADD import_export_name varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;


-- INSERT TABLE


-- ----------------------------
-- for qualitis_rule_metric_type_config
-- ----------------------------
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("一般指标", "general-metric");
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("一般校验指标", "general-check-metric");
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("一般业务指标", "general-busi-check");
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("一般IT指标", "IT-metric");
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("一级指标", "1-level-metric");
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("二级指标", "2-level-metric");
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("自定义分类", "custom-metric");
-- ----------------------------
-- for new template
-- ----------------------------
-- 行数据重复校验
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql) values (2149, "{&PRIMARY_LINE_VERIFICATION}", 1, 1, 1, -1, "select ${field_replace_null_concat} from ${db}.${table} where ${filter}", 1, 1, false, "select max(md5_count) from (select md5(${field_replace_null_concat}) as md5, count(1) as md5_count from ${db}.${table} where ${filter}) tmp where ${filter} group by tmp.md5 having count(*) > 1");

insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description) values (31391, "{&DATABASE}", 2149, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description) values (31392, "{&TABLE}", 2149, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description) values (31393, "{&FIELD_REPLACE_NULL_CONCAT}", 2149, "field_replace_null_concat", 24, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field_replace_null_concat}");

insert into qualitis_template_statistic_input_meta(id, template_id, name, func_name, value, value_type, result_type) values(2091, 2149, "{&LINE_REPEAT_MAX_NUMBER}", "max", "md5_count", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type) values(2149, 2105, "{&LINE_REPEAT_MAX_NUMBER}", "max", 1);
-- 跨库全量校验
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(20, "{&MULTI-DATABASE_ACCURACY_VERIFICATION}", 1, 2, 2, 0, 1, "SELECT tmp1.* FROM (SELECT ${SOURCE_GROUP_BY_COLUMNS}, count(1) as qualitis_mul_db_accuracy_num FROM ${source_db}.${source_table} WHERE ${filter_left} group by ${SOURCE_GROUP_BY_COLUMNS}) tmp1 LEFT JOIN (SELECT ${TARGET_GROUP_BY_COLUMNS}, count(1) as qualitis_mul_db_accuracy_num FROM ${target_db}.${target_table} WHERE ${filter_right} group by ${TARGET_GROUP_BY_COLUMNS}) tmp2 ON ${mapping_argument} WHERE ( NOT (${source_column_is_null}) AND (${target_column_is_null}) )", 3, 1, true,
	"SELECT count(tmp1.*) FROM (SELECT ${SOURCE_GROUP_BY_COLUMNS}, count(1) as qualitis_mul_db_accuracy_num FROM ${source_db}.${source_table} WHERE ${filter_left} group by ${SOURCE_GROUP_BY_COLUMNS}) tmp1 LEFT JOIN (SELECT ${TARGET_GROUP_BY_COLUMNS}, count(1) as qualitis_mul_db_accuracy_num FROM ${target_db}.${target_table} WHERE ${filter_right} group by ${TARGET_GROUP_BY_COLUMNS}) tmp2 ON ${mapping_argument} WHERE ( NOT (${source_column_is_null}) AND (${target_column_is_null}) )");

insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&SOURCE_DATABASE}", 20, "source_db", 11, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&SOURCE_TABLE}", 20, "source_table", 12, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_table}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TARGET_DATABASE}", 20, "target_db", 13, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TARGET_TABLE}", 20, "target_table", 14, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_table}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
	values(40000, "{&JOIN_CONDITION}", 20, "mapping_argument", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${mapping_argument}", "(${left_statement} ${operation} ${right_statement} OR (${left_statement} is null and ${right_statement} is null))");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
	values(40011, "{&SOURCE_TABLE_COLUMN_IS_NULL}", 20, "source_column_is_null", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_column_is_null}", "${source_column} IS NULL");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
	values(40012, "{&TARGET_TABLE_COLUMN_IS_NULL}", 20, "target_column_is_null", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_column_is_null}", "${target_column} IS NULL");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&SOURCE_GROUP_BY_COLUMNS}", 20, "SOURCE_GROUP_BY_COLUMNS", 22, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${SOURCE_GROUP_BY_COLUMNS}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TARGET_GROUP_BY_COLUMNS}", 20, "TARGET_GROUP_BY_COLUMNS", 23, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${TARGET_GROUP_BY_COLUMNS}");

insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_LEFT_EXPRESSION}", null, "left_statement", 15, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${left_statement}", 40000);
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_OPERATION}", null, "operation", 16, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${operation}", 40000);
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_RIGHT_EXPRESSION}", null, "right_statement", 17, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${right_statement}", 40000);
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_LEFT_FILED}", null, "source_column", 18, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_column}", 40011);
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_RIGHT_FILED}", null, "target_column", 19, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_column}", 40012);

insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(20, 25, "", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type)
	values(20, 645, "{&DIFFERENT_RECORD_BETWEEN_SOURCE_AND_TARGET_TABLE}", "count", 1);
-- 模板数据源类型支持
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(1,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(1,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(2,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(2,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(3,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(3,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(4,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(4,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(5,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(5,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(6,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(6,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(7,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(7,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(8,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(8,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(9,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(9,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(10,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(10,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(11,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(11,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(12,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(12,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(13,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(13,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(14,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(14,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(15,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(15,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(16,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(16,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(17,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(17,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(18,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(18,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(19,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(19,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(20,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(20,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(2149,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(2149,2);

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
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(18,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(19,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(20,3);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(2149,3);

