/*
  Using update from release-0.8.1 to release-0.9.0
*/
-- ----------------------------
-- For qualitis_project_label
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_project_label`;
CREATE TABLE `qualitis_project_label` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `label_name` varchar(25) not null,
  `project_id` bigint(20) not null,
  PRIMARY KEY (`id`),
  CONSTRAINT `UK_project_id_label_name` UNIQUE KEY  (`label_name`, `project_id`),
  CONSTRAINT `FK_qualitis_project_label_project_id` FOREIGN KEY (`project_id`) REFERENCES `qualitis_project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- ----------------------------
-- Add column for qualitis_application_task
-- ----------------------------
ALTER TABLE qualitis_application_task ADD task_comment int(11) NULL;
ALTER TABLE qualitis_application_task ADD running_time bigint(20) NULL;
ALTER TABLE qualitis_application_task ADD task_exec_id varchar(255) NULL;
ALTER TABLE qualitis_application_task ADD task_proxy_user varchar(50) NULL;
ALTER TABLE qualitis_application_task MODIFY COLUMN task_remote_id bigint(20) NULL;
ALTER TABLE qualitis_application_task ADD new_progress_time BIGINT NULL;
ALTER TABLE qualitis_application_task ADD progress double NULL;
-- ----------------------------
-- For qualitis_auth_department
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_auth_department`;
CREATE TABLE `qualitis_auth_department` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_department_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- ----------------------------
-- Add column for qualitis_auth_role
-- ----------------------------
ALTER TABLE qualitis_auth_role ADD COLUMN department_id bigint(20) DEFAULT NULL;
ALTER TABLE qualitis_auth_role MODIFY COLUMN name varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
-- ----------------------------
-- Modify column for qualitis_auth_user
-- ----------------------------
ALTER TABLE qualitis_auth_user ADD COLUMN department_id bigint(20) DEFAULT NULL,
ALTER TABLE qualitis_auth_user DROP COLUMN department;
-- ----------------------------
-- For qualitis_rule_datasource_count
-- ----------------------------
DROP TABLE IF EXISTS qualitis_rule_datasource_count;
CREATE TABLE qualitis_rule_datasource_count (
	id bigint(20) auto_increment NOT NULL,
	datasource_name varchar(500) NOT NULL,
	datasource_count int(11) DEFAULT 1 NOT NULL,
	user_id bigint(20) NOT NULL,
	CONSTRAINT qualitis_rule_datasouce_count_PK PRIMARY KEY (id),
	CONSTRAINT qualitis_rule_datasouce_count_UN UNIQUE KEY (user_id,datasource_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;
-- ----------------------------
-- Add column for qualitis_template
-- ----------------------------
ALTER TABLE qualitis_template ADD column create_user_id bigint(20) DEFAULT NULL;
ALTER TABLE qualitis_template ADD column modify_user_id bigint(20) DEFAULT NULL;
ALTER TABLE qualitis_template ADD column template_level int(11) NOT NULL DEFAULT 1;
ALTER TABLE qualitis_template ADD import_export_name varchar(40) DEFAULT NULL;
-- ----------------------------
-- For qualitis_template_department
-- ----------------------------
DROP TABLE IF EXISTS qualitis_template_department;
CREATE TABLE `qualitis_template_department` (
  `id` bigint(32) NOT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `UK_department_template` UNIQUE KEY  (`department_id`, `template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
-- ----------------------------
-- For qualitis_template_user
-- ----------------------------
DROP TABLE IF EXISTS qualitis_template_user;
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
-- ----------------------------
-- For qualitis_rule
-- ----------------------------
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
-- For qualitis_rule_variable
-- ----------------------------
ALTER TABLE qualitis_rule_variable MODIFY COLUMN value mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_rule_variable MODIFY COLUMN table_name varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_rule_variable MODIFY COLUMN origin_value varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
-- ----------------------------
-- For qualitis_rule_datasource
-- ----------------------------
ALTER TABLE qualitis_rule_datasource ADD proxy_user varchar(20) NULL;
ALTER TABLE qualitis_rule_datasource ADD black_col_name bit(1) DEFAULT b'1' NULL;
ALTER TABLE qualitis_rule_datasource MODIFY COLUMN col_name mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_rule_datasource ADD linkis_datasoure_id BIGINT(20) NULL;
ALTER TABLE qualitis_rule_datasource ADD datasource_type int(11) DEFAULT 1 NULL;
ALTER TABLE qualitis_rule_datasource ADD linkis_datasoure_version_id bigint(20) NULL;
ALTER TABLE qualitis_rule_datasource ADD linkis_datasource_name varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
-- ----------------------------
-- For qualitis_rule_datasource_mapping
-- ----------------------------
ALTER TABLE qualitis_rule_datasource_mapping ADD left_column_types varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_rule_datasource_mapping ADD right_column_types varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
-- ----------------------------
-- For qualitis_application
-- ----------------------------
ALTER TABLE qualitis_application ADD project_id bigint(20) NULL;
ALTER TABLE qualitis_application ADD rule_group_id bigint(20) NULL;
ALTER TABLE qualitis_application ADD application_comment int(11) NULL;
ALTER TABLE qualitis_application MODIFY COLUMN exception_message TEXT NULL;
ALTER TABLE qualitis_application ADD project_name varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application ADD cluster_name varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application ADD startup_param varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application ADD rule_group_name varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application ADD filter_partition varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application ADD rule_datasource varchar(3000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application ADD execution_param varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application ADD set_flag varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application ADD run_date varchar(25) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
-- ----------------------------
-- For qualitis_project_user
-- ----------------------------
ALTER table qualitis_project_user ADD INDEX (user_name);
-- ----------------------------
-- For qualitis_template_mid_table_input_meta: template_id=17, 18
-- ----------------------------
-- UPDATE TABLE qualitis_template_mid_table_input_meta SET concat_template = "(${left_statement} ${operation} ${right_statement} OR ({left_statement} is null and ${right_statement} is null))" WHERE id = 10000 or id = 20000;
-- ----------------------------
-- For qualitis_rule_metric
-- ----------------------------
CREATE TABLE `qualitis_rule_metric` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(512) NOT NULL,
  `metric_desc` varchar(100) NOT NULL,
  `sub_system_id` int(20) DEFAULT NULL,
  `dev_department_name` varchar(30) DEFAULT NULL,
  `metric_level` int(5) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `create_time` varchar(25) DEFAULT NULL,
  `modify_time` varchar(25) DEFAULT NULL,
  `modify_user` varchar(50) DEFAULT NULL,
  `sub_system_name` varchar(100) DEFAULT NULL,
  `full_cn_name` varchar(100) DEFAULT NULL,
  `type` int(11) DEFAULT '0' COMMENT '0.14.0 The classification of rule metric is usually based on a more detailed division of subsystem info.',
  `en_code` varchar(50) NOT NULL DEFAULT '' COMMENT '0.14.0 En code meaning.',
  `available` bit(1) DEFAULT b'0' COMMENT '0.14.0 Availability in use.',
  `ops_department_name` varchar(30) DEFAULT NULL COMMENT '0.14.0 Department was split into two departments whick are dev and ops.',
  `frequency` int(11) DEFAULT NULL COMMENT '0.14.0',
  `department_code` varchar(25) DEFAULT NULL,
  `department_name` varchar(255) DEFAULT NULL,
  `product_id` varchar(255) DEFAULT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `buss_code` int(5) DEFAULT NULL,
  `buss_custom` varchar(100) DEFAULT NULL,
  `cn_name` varchar(512) NOT NULL,
  `sub_system_alias` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNI_RULE_METRIC_EN_CODE` (`en_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Created in version qualitis-0.12.0 to manage rule metric.';
-- ----------------------------
-- For qualitis_rule_metric_department_user
-- ----------------------------
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
-- ----------------------------
-- For qualitis_application_task_result
-- ----------------------------
ALTER TABLE qualitis_application_task_result ADD run_date BIGINT NULL;
ALTER TABLE qualitis_application_task_result ADD rule_metric_id bigint(20) NULL;
ALTER TABLE qualitis_application_task_result MODIFY COLUMN value VARCHAR(128) NULL;
ALTER TABLE qualitis_application_task_result ADD save_result bit(1) DEFAULT b'1' NULL;
ALTER TABLE qualitis_application_task_result ADD department_code varchar(25) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
-- ----------------------------
-- For qualitis_application_task_rule_simple
-- ----------------------------
ALTER TABLE qualitis_application_task_rule_simple MODIFY COLUMN mid_table_name varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application_task_rule_simple ADD template_name varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application_task_rule_simple ADD rule_detail varchar(340) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application_task_rule_simple ADD delete_fail_check_result bit(1) DEFAULT b'1' NULL;
ALTER TABLE qualitis_application_task_rule_simple ADD cn_name varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application_task_rule_simple ADD project_cn_name varchar(170) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
ALTER TABLE qualitis_application_task_rule_simple ADD rule_group_name varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
-- ----------------------------
-- For qualitis_project_event
-- ----------------------------
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
-- ----------------------------
-- For qualitis_rule_alarm_config
-- ----------------------------
ALTER TABLE qualitis_rule_alarm_config ADD rule_metric_id bigint(20) NULL;
ALTER TABLE qualitis_rule_alarm_config ADD file_output_name int(11) NULL;
ALTER TABLE qualitis_rule_alarm_config ADD file_output_unit int(11) NULL;
ALTER TABLE qualitis_rule_alarm_config ADD upload_abnormal_value bit(1) DEFAULT b'0' NULL;
ALTER TABLE qualitis_rule_alarm_config ADD upload_rule_metric_value bit(1) DEFAULT b'0' NULL;
ALTER TABLE qualitis_rule_alarm_config ADD delete_fail_check_result bit(1) DEFAULT b'0' NULL;
-- ----------------------------
-- For qualitis_application_task_datasource
-- ----------------------------
ALTER TABLE qualitis_application_task_datasource ADD project_id bigint(20) NULL;
-- ----------------------------
-- For qualitis_application_task_rule_alarm_config
-- ----------------------------
ALTER TABLE qualitis_application_task_rule_alarm_config ADD rule_metric_id bigint(20) NULL;
ALTER TABLE qualitis_application_task_rule_alarm_config ADD upload_abnormal_value bit(1) DEFAULT b'0' NULL;
ALTER TABLE qualitis_application_task_rule_alarm_config ADD upload_rule_metric_value bit(1) DEFAULT b'0' NULL;
ALTER TABLE qualitis_application_task_rule_alarm_config ADD delete_fail_check_result bit(1) DEFAULT b'0' NULL;
ALTER TABLE qualitis_application_task_rule_alarm_config ADD output_unit varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
-- ----------------------------
-- For qualitis_config_cluster_info
-- ----------------------------
ALTER TABLE qualitis_config_cluster_info CHANGE meta_store_address skip_data_size varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
--
-- Create table `qualitis_template_datasource_type`
--
DROP TABLE if EXISTS `qualitis_template_datasource_type`;
CREATE TABLE `qualitis_template_datasource_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `template_id` bigint(20) DEFAULT NULL,
  `data_source_type_id` int(5) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`template_id`,`data_source_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
--
-- For table `qualitis_application_task_datasource`
--
ALTER TABLE qualitis_application_task_datasource ADD datasource_type int(11) DEFAULT 1 NULL;
--
-- For table `qualitis_rule_metric_type_config`
--
DROP TABLE if EXISTS `qualitis_rule_metric_type_config`;
CREATE TABLE `qualitis_rule_metric_type_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cn_name` varchar(50) DEFAULT NULL,
  `en_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("一般指标", "general-metric");
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("一般校验指标", "general-check-metric");
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("一般业务指标", "general-busi-check");
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("一般IT指标", "IT-metric");
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("一级指标", "1-level-metric");
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("二级指标", "2-level-metric");
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("自定义分类", "custom-metric");
--
-- For table `qualitis_project`
--
alter table qualitis_project add column modify_time varchar(25);
alter table qualitis_project add column modify_user varchar(50);
ALTER TABLE qualitis_project ADD cn_name varchar(170) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
--
-- For table `qualitis_template_regexp_expr`
--
ALTER TABLE qualitis_template_regexp_expr MODIFY COLUMN regexp_value varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;
--
-- For table `qualitis_template`
--
ALTER TABLE qualitis_template DROP COLUMN datasource_type;
-- 创建新模板start
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql, template_level) values (2149, "{&PRIMARY_LINE_VERIFICATION}", 1, 1, 1, -1, "select ${field_replace_null_concat} from ${db}.${table} where ${filter}", 1, 1, false, "select max(md5_count) from (select md5(${field_replace_null_concat}) as md5, count(1) as md5_count from ${db}.${table} where ${filter}) tmp where ${filter} group by tmp.md5 having count(*) > 1", 1);

insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description) values (31391, "{&DATABASE}", 2149, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description) values (31392, "{&TABLE}", 2149, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description) values (31393, "{&FIELD_REPLACE_NULL_CONCAT}", 2149, "field_replace_null_concat", 24, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field_replace_null_concat}");

insert into qualitis_template_statistic_input_meta(id, template_id, name, func_name, value, value_type, result_type) values(2091, 2149, "{&LINE_REPEAT_MAX_NUMBER}", "max", "md5_count", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type) values(2149, 2105, "{&LINE_REPEAT_MAX_NUMBER}", "max", 1);
-- 创建新模板end
--
-- For table `qualitis_template_datasource_type`
--
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(2149,1);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(2149,2);
insert into `qualitis_template_datasource_type` (`template_id`, `data_source_type_id`) values(2149,3);
--
-- For qualitis_rule_group
--
ALTER TABLE qualitis_rule_group ADD version varchar(25) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;

