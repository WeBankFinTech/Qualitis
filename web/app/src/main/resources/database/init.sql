
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for application
-- ----------------------------
DROP TABLE IF EXISTS `application`;
CREATE TABLE `application` (
  `id` varchar(40) NOT NULL,
  `abnormal_task_num` int(11) DEFAULT NULL,
  `create_user` varchar(150) DEFAULT NULL,
  `exception_message` varchar(10000) DEFAULT NULL,
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for application_task
-- ----------------------------
DROP TABLE IF EXISTS `application_task`;
CREATE TABLE `application_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `begin_time` varchar(25) DEFAULT NULL,
  `cluster_id` varchar(100) DEFAULT NULL,
  `end_time` varchar(25) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `submit_address` varchar(255) DEFAULT NULL,
  `task_remote_id` int(11) DEFAULT NULL,
  `application_id` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1mqrf3lwti2x4n2b4tbvrp3vh` (`application_id`),
  CONSTRAINT `FK1mqrf3lwti2x4n2b4tbvrp3vh` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for application_task_datasource
-- ----------------------------
DROP TABLE IF EXISTS `application_task_datasource`;
CREATE TABLE `application_task_datasource` (
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
  PRIMARY KEY (`id`),
  KEY `FKwmp4c4apbq8ce65yn4rmiqto` (`task_id`),
  CONSTRAINT `FKwmp4c4apbq8ce65yn4rmiqto` FOREIGN KEY (`task_id`) REFERENCES `application_task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for application_task_result
-- ----------------------------
DROP TABLE IF EXISTS `application_task_result`;
CREATE TABLE `application_task_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `application_id` varchar(255) DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  `result_type` varchar(255) DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  `value` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for application_task_rule_alarm_config
-- ----------------------------
DROP TABLE IF EXISTS `application_task_rule_alarm_config`;
CREATE TABLE `application_task_rule_alarm_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `check_template` int(11) DEFAULT NULL,
  `compare_type` int(11) DEFAULT NULL,
  `output_name` varchar(500) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `threshold` double DEFAULT NULL,
  `task_rule_simple_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8pl4mc764sm6tigbltaxwy5ma` (`task_rule_simple_id`),
  CONSTRAINT `FK8pl4mc764sm6tigbltaxwy5ma` FOREIGN KEY (`task_rule_simple_id`) REFERENCES `application_task_rule_simple` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for application_task_rule_simple
-- ----------------------------
DROP TABLE IF EXISTS `application_task_rule_simple`;
CREATE TABLE `application_task_rule_simple` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `application_id` varchar(40) DEFAULT NULL,
  `execute_user` varchar(20) DEFAULT NULL,
  `mid_table_name` varchar(200) DEFAULT NULL,
  `project_creator` varchar(50) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `project_name` varchar(170) DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  `rule_name` varchar(200) DEFAULT NULL,
  `rule_type` int(11) DEFAULT NULL,
  `submit_time` varchar(20) DEFAULT NULL,
  `parent_rule_simple_id` bigint(20) DEFAULT NULL,
  `task_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9c0ew72yjb06o649fe06lom9` (`parent_rule_simple_id`),
  KEY `FK3cai6eubxbin3doev0qnsg591` (`task_id`),
  CONSTRAINT `FK3cai6eubxbin3doev0qnsg591` FOREIGN KEY (`task_id`) REFERENCES `application_task` (`id`),
  CONSTRAINT `FK9c0ew72yjb06o649fe06lom9` FOREIGN KEY (`parent_rule_simple_id`) REFERENCES `application_task_rule_simple` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for auth_list
-- ----------------------------
DROP TABLE IF EXISTS `auth_list`;
CREATE TABLE `auth_list` (
  `app_id` varchar(255) NOT NULL,
  `app_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for auth_permission
-- ----------------------------
DROP TABLE IF EXISTS `auth_permission`;
CREATE TABLE `auth_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `method` varchar(6) DEFAULT NULL,
  `url` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for auth_proxy_user
-- ----------------------------
DROP TABLE IF EXISTS `auth_proxy_user`;
CREATE TABLE `auth_proxy_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `proxy_user_name` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for auth_role
-- ----------------------------
DROP TABLE IF EXISTS `auth_role`;
CREATE TABLE `auth_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_lc1sij60969nsgl5cy8bfgbsm` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for auth_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `auth_role_permission`;
CREATE TABLE `auth_role_permission` (
  `id` varchar(32) NOT NULL,
  `permission_id` bigint(20) DEFAULT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKj7ra5lry2xr46sggtvhn9xa6g` (`permission_id`),
  KEY `FKjby3ropowpvay5qjs1ja7lni4` (`role_id`),
  CONSTRAINT `FKj7ra5lry2xr46sggtvhn9xa6g` FOREIGN KEY (`permission_id`) REFERENCES `auth_permission` (`id`),
  CONSTRAINT `FKjby3ropowpvay5qjs1ja7lni4` FOREIGN KEY (`role_id`) REFERENCES `auth_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for auth_user
-- ----------------------------
DROP TABLE IF EXISTS `auth_user`;
CREATE TABLE `auth_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `chinese_name` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `username` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_t1iph3dfc25ukwcl9xemtnojn` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for auth_user_permission
-- ----------------------------
DROP TABLE IF EXISTS `auth_user_permission`;
CREATE TABLE `auth_user_permission` (
  `id` varchar(32) NOT NULL,
  `permission_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`permission_id`),
  KEY `FKishs22pergukrq4pb3f6fuq07` (`permission_id`),
  CONSTRAINT `FKbretuphgkspacoe757g328q5m` FOREIGN KEY (`user_id`) REFERENCES `auth_user` (`id`),
  CONSTRAINT `FKishs22pergukrq4pb3f6fuq07` FOREIGN KEY (`permission_id`) REFERENCES `auth_permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for auth_user_proxy_user
-- ----------------------------
DROP TABLE IF EXISTS `auth_user_proxy_user`;
CREATE TABLE `auth_user_proxy_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `proxy_user_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK36mb4nt5p397hoyks58towu0v` (`proxy_user_id`),
  KEY `FKlppwrpvg1lf8y8a5j58y69kfm` (`user_id`),
  CONSTRAINT `FK36mb4nt5p397hoyks58towu0v` FOREIGN KEY (`proxy_user_id`) REFERENCES `auth_proxy_user` (`id`),
  CONSTRAINT `FKlppwrpvg1lf8y8a5j58y69kfm` FOREIGN KEY (`user_id`) REFERENCES `auth_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for auth_user_role
-- ----------------------------
DROP TABLE IF EXISTS `auth_user_role`;
CREATE TABLE `auth_user_role` (
  `id` varchar(32) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FK3eldmba9luu9l0apl0791x8vd` (`role_id`),
  CONSTRAINT `FK3eldmba9luu9l0apl0791x8vd` FOREIGN KEY (`role_id`) REFERENCES `auth_role` (`id`),
  CONSTRAINT `FKebutsbqm58ehnlffb299ng0ap` FOREIGN KEY (`user_id`) REFERENCES `auth_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for config_cluster_info
-- ----------------------------
DROP TABLE IF EXISTS `config_cluster_info`;
CREATE TABLE `config_cluster_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cluster_env` varchar(255) DEFAULT NULL,
  `cluster_name` varchar(100) DEFAULT NULL,
  `cluster_type` varchar(100) DEFAULT NULL,
  `hive_database_address` varchar(255) DEFAULT NULL,
  `hive_database_password` varchar(255) DEFAULT NULL,
  `hive_database_username` varchar(255) DEFAULT NULL,
  `hive_server2_address` varchar(100) DEFAULT NULL,
  `linkis_address` varchar(100) DEFAULT NULL,
  `linkis_token` varchar(500) DEFAULT NULL,
  `meta_store_address` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for project
-- ----------------------------
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_user` varchar(50) DEFAULT NULL,
  `create_user_full_name` varchar(50) DEFAULT NULL,
  `description` varchar(1700) DEFAULT NULL,
  `name` varchar(170) DEFAULT NULL,
  `project_type` int(11) DEFAULT NULL,
  `user_department` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for project_user
-- ----------------------------
DROP TABLE IF EXISTS `project_user`;
CREATE TABLE `project_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `permission` int(11) DEFAULT NULL,
  `user_full_name` varchar(30) DEFAULT NULL,
  `user_name` varchar(20) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4ug72llnm0n7yafwntgdswl3y` (`project_id`),
  CONSTRAINT `FK4ug72llnm0n7yafwntgdswl3y` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for rule
-- ----------------------------
DROP TABLE IF EXISTS `rule`;
CREATE TABLE `rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `alarm` bit(1) DEFAULT NULL,
  `from_content` varchar(3010) DEFAULT NULL,
  `function_content` varchar(3010) DEFAULT NULL,
  `function_type` int(11) DEFAULT NULL,
  `name` varchar(170) DEFAULT NULL,
  `output_name` varchar(170) DEFAULT NULL,
  `rule_template_name` varchar(180) DEFAULT NULL,
  `rule_type` int(11) DEFAULT NULL,
  `where_content` varchar(3010) DEFAULT NULL,
  `parent_rule_id` bigint(20) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `rule_group_id` bigint(20) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKtjrt75tttfux1esrhmfsuwtax` (`project_id`,`name`),
  KEY `FKi62skj2blskq1ynrnu2f37e4p` (`parent_rule_id`),
  KEY `FKbskuw5qpbvi7dcuc3aqt8t556` (`rule_group_id`),
  KEY `FKmx113051ieint865l7rs8g5ao` (`template_id`),
  CONSTRAINT `FKbskuw5qpbvi7dcuc3aqt8t556` FOREIGN KEY (`rule_group_id`) REFERENCES `rule_group` (`id`),
  CONSTRAINT `FKi62skj2blskq1ynrnu2f37e4p` FOREIGN KEY (`parent_rule_id`) REFERENCES `rule` (`id`),
  CONSTRAINT `FKmx113051ieint865l7rs8g5ao` FOREIGN KEY (`template_id`) REFERENCES `template` (`id`),
  CONSTRAINT `FKrwqvmphnc0n6hqdsadd7fvv8k` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for rule_alarm_config
-- ----------------------------
DROP TABLE IF EXISTS `rule_alarm_config`;
CREATE TABLE `rule_alarm_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `check_template` int(11) DEFAULT NULL,
  `compare_type` int(11) DEFAULT NULL,
  `threshold` double DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  `template_output_meta_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrq5hh7g0iihdm06yhkoqfuw1o` (`rule_id`),
  KEY `FKr4xt75g7t8qbq2p0py080cmow` (`template_output_meta_id`),
  CONSTRAINT `FKr4xt75g7t8qbq2p0py080cmow` FOREIGN KEY (`template_output_meta_id`) REFERENCES `template_output_meta` (`id`),
  CONSTRAINT `FKrq5hh7g0iihdm06yhkoqfuw1o` FOREIGN KEY (`rule_id`) REFERENCES `rule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for rule_datasource
-- ----------------------------
DROP TABLE IF EXISTS `rule_datasource`;
CREATE TABLE `rule_datasource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cluster_name` varchar(100) DEFAULT NULL,
  `col_name` varchar(500) DEFAULT NULL,
  `datasource_index` int(11) DEFAULT NULL,
  `db_name` varchar(100) DEFAULT NULL,
  `filter` varchar(3200) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `table_name` varchar(100) DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKogwvuymonrxmhyuti8ms6l9im` (`rule_id`),
  CONSTRAINT `FKogwvuymonrxmhyuti8ms6l9im` FOREIGN KEY (`rule_id`) REFERENCES `rule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for rule_datasource_mapping
-- ----------------------------
DROP TABLE IF EXISTS `rule_datasource_mapping`;
CREATE TABLE `rule_datasource_mapping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `left_column_names` varchar(2000) DEFAULT NULL,
  `left_statement` varchar(3000) DEFAULT NULL,
  `operation` int(11) DEFAULT NULL,
  `right_column_names` varchar(2000) DEFAULT NULL,
  `right_statement` varchar(3000) DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK95o4udqdcgwn1xarcolti8f6f` (`rule_id`),
  CONSTRAINT `FK95o4udqdcgwn1xarcolti8f6f` FOREIGN KEY (`rule_id`) REFERENCES `rule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for rule_group
-- ----------------------------
DROP TABLE IF EXISTS `rule_group`;
CREATE TABLE `rule_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) DEFAULT NULL,
  `rule_group_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for rule_variable
-- ----------------------------
DROP TABLE IF EXISTS `rule_variable`;
CREATE TABLE `rule_variable` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cluster_name` varchar(50) DEFAULT NULL,
  `db_name` varchar(50) DEFAULT NULL,
  `input_action_step` int(11) DEFAULT NULL,
  `origin_value` varchar(100) DEFAULT NULL,
  `table_name` varchar(50) DEFAULT NULL,
  `value` varchar(500) DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  `template_mid_table_input_meta_id` bigint(20) DEFAULT NULL,
  `template_statistics_input_meta_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKchynaoqvpq9bifa8nrjfvrcp1` (`rule_id`),
  KEY `FKc5ij308cuxst7bjmf94wrsnp7` (`template_mid_table_input_meta_id`),
  KEY `FKitdps70kr9qy0ikdklfybs34s` (`template_statistics_input_meta_id`),
  CONSTRAINT `FKc5ij308cuxst7bjmf94wrsnp7` FOREIGN KEY (`template_mid_table_input_meta_id`) REFERENCES `template_mid_table_input_meta` (`id`),
  CONSTRAINT `FKchynaoqvpq9bifa8nrjfvrcp1` FOREIGN KEY (`rule_id`) REFERENCES `rule` (`id`),
  CONSTRAINT `FKitdps70kr9qy0ikdklfybs34s` FOREIGN KEY (`template_statistics_input_meta_id`) REFERENCES `template_statistic_input_meta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for template
-- ----------------------------
DROP TABLE IF EXISTS `template`;
CREATE TABLE `template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `action_type` int(11) DEFAULT NULL,
  `cluster_num` int(11) DEFAULT NULL,
  `datasource_type` int(11) DEFAULT NULL,
  `db_num` int(11) DEFAULT NULL,
  `field_num` int(11) DEFAULT NULL,
  `mid_table_action` varchar(5000) DEFAULT NULL,
  `name` varchar(180) DEFAULT NULL,
  `save_mid_table` bit(1) DEFAULT NULL,
  `show_sql` varchar(5000) DEFAULT NULL,
  `table_num` int(11) DEFAULT NULL,
  `template_type` int(11) DEFAULT NULL,
  `parent_template_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKkl9utpdhsk0yt39o8ocv859aj` (`parent_template_id`),
  CONSTRAINT `FKkl9utpdhsk0yt39o8ocv859aj` FOREIGN KEY (`parent_template_id`) REFERENCES `template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for template_mid_table_input_meta
-- ----------------------------
DROP TABLE IF EXISTS `template_mid_table_input_meta`;
CREATE TABLE `template_mid_table_input_meta` (
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
  PRIMARY KEY (`id`),
  KEY `FKgv7116tkrbukouauu5m90jfry` (`parent_id`),
  KEY `FK8h1p66kt3864rg15o28phl7yc` (`template_id`),
  CONSTRAINT `FK8h1p66kt3864rg15o28phl7yc` FOREIGN KEY (`template_id`) REFERENCES `template` (`id`),
  CONSTRAINT `FKgv7116tkrbukouauu5m90jfry` FOREIGN KEY (`parent_id`) REFERENCES `template_mid_table_input_meta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for template_output_meta
-- ----------------------------
DROP TABLE IF EXISTS `template_output_meta`;
CREATE TABLE `template_output_meta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `field_name` varchar(50) DEFAULT NULL,
  `field_type` int(11) DEFAULT NULL,
  `output_name` varchar(150) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2ywuaixo07hn3ihs5waovetsf` (`template_id`),
  CONSTRAINT `FK2ywuaixo07hn3ihs5waovetsf` FOREIGN KEY (`template_id`) REFERENCES `template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for template_regexp_expr
-- ----------------------------
DROP TABLE IF EXISTS `template_regexp_expr`;
CREATE TABLE `template_regexp_expr` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key_name` varchar(255) DEFAULT NULL,
  `regexp_type` int(11) DEFAULT NULL,
  `regexp_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for template_statistic_input_meta
-- ----------------------------
DROP TABLE IF EXISTS `template_statistic_input_meta`;
CREATE TABLE `template_statistic_input_meta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `func_name` varchar(5) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `result_type` varchar(255) DEFAULT NULL,
  `value` varchar(50) DEFAULT NULL,
  `value_type` int(11) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq8eiiq2dbs0w7gmybsjc71oop` (`template_id`),
  CONSTRAINT `FKq8eiiq2dbs0w7gmybsjc71oop` FOREIGN KEY (`template_id`) REFERENCES `template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for template_user
-- ----------------------------
DROP TABLE IF EXISTS `template_user`;
CREATE TABLE `template_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmn68y8w3p2pehwsgd58km71rv` (`template_id`),
  CONSTRAINT `FKmn68y8w3p2pehwsgd58km71rv` FOREIGN KEY (`template_id`) REFERENCES `template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------- 插入数据库预先数据 -------------------------
-- 管理员账户
insert into auth_user(id, username, password, chinese_name, department) values(1, "admin", "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918", "管理员", "管理员");
-- 管理员角色
insert into auth_role(id, name) values(1, "ADMIN");
insert into auth_role(id, name) values(2, "PROJECTOR");
-- 管理员权限
insert into auth_permission(id, url, method) values(1, "/qualitis/**", "GET"), (2, "/qualitis/**", "POST"), (3, "/qualitis/**", "DELETE"), (4, "/qualitis/**", "PUT");
insert into auth_permission(id, url, method) values(5, "/qualitis/api/v1/projector/**", "GET"), (6, "/qualitis/api/v1/projector/**", "POST"), (7, "/qualitis/api/v1/projector/**", "DELETE"), (8, "/qualitis/api/v1/projector/**", "PUT");
insert into auth_user_role(id, user_id, role_id) values("5932425efdfe49949587f51a54e0affa", 1, 1);
insert into auth_role_permission(id, role_id, permission_id) values("5932425efdfe49949587f51a54e0affb", 1, 1), ("5932425efdfe49949587f51a54e0affc", 1, 2), ("5932425efdfe49949587f51a54e0affd", 1, 3), ("5932425efdfe49949587f51a54e0affe", 1, 4);
insert into auth_role_permission(id, role_id, permission_id) values("5932425efdfe49949587f51a54e0afaa", 2, 5), ("5932425efdfe49949587f51a54e0afab", 2, 6), ("5932425efdfe49949587f51a54e0afac", 2, 7), ("5932425efdfe49949587f51a54e0afad", 2, 8);

-- 规则模版

-- 字段非空检测
insert into template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(1, "{&NULL_VERIFICATION}", 1, 1, 1, 1, 1, "select * from ${db}.${table} where (${filter}) and (${field} is null)", 1, 1, true,
	      "select count(*) from ${db}.${table} where (${filter}) and (${field} is null)");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 1, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 1, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 1, "field", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into template_statistic_input_meta(template_id, name, func_name, value, value_type, result_type)
	values(1, "{&RECORD_NUMBER_OF_NULL}", "count", "*", 1, "Long");
insert into template_output_meta(template_id, id, output_name, field_name, field_type)
	values(1, 1, "{&RECORD_NUMBER_OF_NULL}", "count", 1);

-- 主键检测
insert into template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(2, "{&PRIMARY_KEY_VERIFICATION}", 1, 1, 1, -1, 1, "select * from ${db}.${table} where ${filter} and (${field_concat}) in (select ${field_concat} from ${db}.${table} where ${filter} group by ${field_concat} having count(*) > 1)", 1, 1, true,
	      "select count(*) from ${db}.${table} where ${filter} and (${field_concat}) in (select ${field_concat} from ${db}.${table} where ${filter} group by ${field_concat} having count(*) > 1)");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 2, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 2, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD_CONCAT}", 2, "field_concat", 6, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field_concat}");
insert into template_statistic_input_meta(template_id, name, func_name, value, value_type, result_type)
	values(2, "{&PRIMARY_KEY_MULTIPLE_NUMBER}", "count", "*", 1, "Long");
insert into template_output_meta(template_id, id, output_name, field_name, field_type)
	values(2, 2, "{&PRIMARY_KEY_MULTIPLE_NUMBER}", "count", 1);

-- 表行数检测
insert into template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(3, "{&TABLE_RECORD_NUMBER_VERIFICATION}", 1, 1, 1, 0, 1, "select count(*) as myCount from ${db}.${table} where ${filter}", 1, 1, false,
	        "select count(*) from ${db}.${table} where ${filter}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 3, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 3, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into template_statistic_input_meta(template_id, name, func_name, value, value_type, result_type)
	values(3, "{&TABLE_RECORD_NUMBER}", "max", "myCount", 1, "Long");
insert into template_output_meta(template_id, id, output_name, field_name, field_type)
	values(3, 3, "{&TABLE_RECORD_NUMBER}", "max", 1);

-- 平均值检测
insert into template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(4, "{&AVERAGE_VALUE_VERIFICATION}", 1, 1, 1, 1, 1, "select avg(${field}) as myAvg from ${db}.${table} where ${filter}", 1, 1, false,
	      "select avg(${field}) from ${db}.${table} where ${filter}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 4, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 4, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 4, "field", 4, 1, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(4, 4, "{&AVERAGE_VALUE}", "max", "myAvg", 1, "Long");
insert into template_output_meta(template_id, output_name, field_name, field_type)
	values(4, "{&AVERAGE_VALUE}", "max", 1);

-- 总和检测
insert into template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(5, "{&SUM_VALUE_VERIFICATION}", 1, 1, 1, 1, 1, "select sum(${field}) as mySum from ${db}.${table} where ${filter}", 1, 1, false,
	      "select sum(${field}) from ${db}.${table} where ${filter}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 5, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 5, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 5, "field", 4, 1, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(5, 5, "{&SUM_VALUE}", "max", "mySum", 1, "Long");
insert into template_output_meta(template_id, output_name, field_name, field_type)
	values(5, "{&SUM_VALUE}", "max", 1);

-- 最大值检测
insert into template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(6, "{&MAX_VALUE_VERIFICATION}", 1, 1, 1, 1, 1, "select max(${field}) as myMax from ${db}.${table} where ${filter}", 1, 1, false,
	"select max(${field}) from ${db}.${table} where ${filter}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 6, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 6, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 6, "field", 4, 1, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(6, 6, "{&MAX_VALUE}", "max", "myMax", 1, "Long");
insert into template_output_meta(template_id, output_name, field_name, field_type)
	values(6, "{&MAX_VALUE}", "max", 1);

-- 最小值检测
insert into template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(7, "{&MIN_VALUE_VERIFICATION}", 1, 1, 1, 1, 1, "select min(${field}) as myMin from ${db}.${table} where ${filter}", 1, 1, false,
	"select min(${field}) from ${db}.${table} where ${filter}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 7, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 7, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 7, "field", 4, 1, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(7, 7, "{&MIN_VALUE}", "max", "myMin", 1, "Long");
insert into template_output_meta(template_id, output_name, field_name, field_type)
	values(7, "{&MIN_VALUE}", "max", 1);

-- 正则表达式检测
insert into template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(8, "{&REGEXP_EXPRESSION_VERIFICATION}", 1, 1, 1, 1, 1, "select * from ${db}.${table} where (${filter}) and (${field} not regexp '${regexp}')", 1, 1, true,
	"select count(*) from ${db}.${table} where (${filter}) and (${field} not regexp '${regexp}')");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 8, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 8, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 8, "field", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&REGEXP_EXPRESSION}", 8, "regexp", 7, null, true, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${regexp},{&PLEASE_TYPE_IN_REGEXP_EXPRESSION}");
insert into template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(8, 8, "{&MISMATCH_RECORD_NUMBER}", "count", "*", 1, "Long");
insert into template_output_meta(template_id, output_name, field_name, field_type)
	values(8, "{&MISMATCH_RECORD_NUMBER}", "count", 1);

-- 时间格式检测
insert into template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(9, "{&DATE_FORMAT_VERIFICATION}", 1, 1, 1, 1, 1, "select * from ${db}.${table} where (${filter}) and (${field} not regexp '${regexp}')", 1, 1, true,
	"select count(*) from ${db}.${table} where (${filter}) and (${field} not regexp '${regexp}')");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 9, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 9, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 9, "field", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATE_FORMAT}", 9, "regexp", 7, null, false, 1, "{&REPLACE_PLACEHOLDER_IN_SQL}${regexp},{&CHOOSE_APPROPRIATE}");
insert into template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(9, 9, "{&MISMATCH_DATE_FORMAT_RECORD_NUMBER}", "count", "*", 1, "Long");
insert into template_output_meta(template_id, output_name, field_name, field_type)
	values(9, "{&MISMATCH_DATE_FORMAT_RECORD_NUMBER}", "count", 1);
insert into template_regexp_expr(key_name, regexp_type, regexp_value) values("yyyyMMdd", 1, "^(?:(?!0000)[0-9]{4}(?:(?:0[1-9]|1[0-2])(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])(?:29|30)|(?:0[13578]|1[02])31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)0229)$");
insert into template_regexp_expr(key_name, regexp_type, regexp_value) values("yyyy-MM-dd", 1, "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$");
insert into template_regexp_expr(key_name, regexp_type, regexp_value) values("yyyyMMddHH", 1, "^(?:(?!0000)[0-9]{4}(?:(?:0[1-9]|1[0-2])(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])(?:29|30)|(?:0[13578]|1[02])31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)0229)([01][0-9]|2[0-3])$");

-- 数值格式检测
insert into template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(10, "{&NUMBER_FORMAT_VERIFICATION}", 1, 1, 1, 1, 1, "select * from ${db}.${table} where (${filter}) and (${field} not regexp '${regexp}')", 1, 1, true,
	"select count(*) from ${db}.${table} where (${filter}) and (${field} not regexp '${regexp}')");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 10, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 10, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 10, "field", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&NUMBER_FORMAT_REGEXP_EXPRESSION}", 10, "regexp", 7, null, false, 2, "{&REPLACE_PLACEHOLDER_IN_SQL}${regexp}");
insert into template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(10, 10, "{&RECORD_NUMBER_OF_MISMATCH_NUMBER_FORMAT}", "count", "*", 1, "Long");
insert into template_output_meta(template_id, output_name, field_name, field_type)
	values(10, "{&RECORD_NUMBER_OF_MISMATCH_NUMBER_FORMAT}", "count", 1);
insert into template_regexp_expr(regexp_type, regexp_value) values(2, "-?[0-9]+(\\\\.[0-9])?[0-9]*$");

-- 枚举值检测
insert into template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(11, "{&ENUM_VALUE_VERIFICATION}", 1, 1, 1, 1, 1, "select * from ${db}.${table} where (${filter}) and (${field} not in ( ${list} ) or ${field} is null)", 1, 1, true,
	"select count(*) from ${db}.${table} where (${filter}) and (${field} not in ( ${list} ) or ${field} is null)");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 11, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 11, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 11, "field", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&ENUM_VALUE}", 11, "list", 8, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${list},示例:'1,2,3,4'");
insert into template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(11, 11, "{&RECORD_NUMBER_OF_NOT_IN_ENUM_VALUE}", "count", "*", 1, "Long");
insert into template_output_meta(template_id, output_name, field_name, field_type)
	values(11, "{&RECORD_NUMBER_OF_NOT_IN_ENUM_VALUE}", "count", 1);

-- 数值范围检测
insert into template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(12, "{&NUMBER_RANGE_VERIFICATION}", 1, 1, 1, 0, 1, "select * from ${db}.${table} where (${filter}) and (not (${filter2}))", 1, 1, true,
	"select count(*) from ${db}.${table} where (${filter}) and (not (${filter2}))");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 12, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 12, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&NUMBER_RANGE}", 12, "filter2", 1, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter2}，{&PLEASE_TYPE_IN_NUMBER_RANGE}");
insert into template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(12, 12, "{&RECORD_NUMBER_OF_NOT_NUMBER_RANGE}", "count", "*", 1, "Long");
insert into template_output_meta(template_id, output_name, field_name, field_type)
	values(12, "{&RECORD_NUMBER_OF_NOT_NUMBER_RANGE}", "count", 1);

-- 身份证检测
insert into template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(13, "{&IDENTITY_VERIFICATION}", 1, 1, 1, 1, 1, "select * from ${db}.${table} where (${filter}) and (${field} not regexp '${regexp}')", 1, 1, true,
	"select count(*) from ${db}.${table} where (${filter}) and (${field} not regexp '${regexp}')");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 13, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 13, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 13, "field", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&IDENTITY_REGEXP_EXPRESSION}", 13, "regexp", 7, null, false, 3, "{&REPLACE_PLACEHOLDER_IN_SQL}${regexp}");
insert into template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(13, 13, "{&MISMATCH_IDENTITY_RECORD_NUMBER}", "count", "*", 1, "Long");
insert into template_output_meta(template_id, output_name, field_name, field_type)
	values(13, "{&MISMATCH_IDENTITY_RECORD_NUMBER}", "count", 1);
insert into template_regexp_expr(regexp_type, regexp_value) values(3, "^[1-9][0-9]{5}(18|19|20)[0-9]{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)[0-9]{3}[0-9Xx]$");

-- 逻辑类检测
insert into template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(14, "{&LOGIC_VERIFICATION}", 1, 1, 1, 0, 1, "select * from ${db}.${table} where (${filter}) and ( (${condition1}) and not (${condition2}) )", 1, 1, true,
	"select count(*) from ${db}.${table} where (${filter}) and ( (${condition1}) and not (${condition2}) )");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 14, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 14, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&PRE_CONDITION}", 14, "condition1", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${condition1},{&PLEASE_TYPE_IN_PRE_CONDITION}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&POST_CONDITION}", 14, "condition2", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${condition2}，{&PLEASE_TYPE_IN_POST_CONDITION}");
insert into template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(14, 14, "{&RECORD_NUMBER_OF_MISMATCH_LOGIC_VERIFICATION}", "count", "*", 1, "Long");
insert into template_output_meta(template_id, output_name, field_name, field_type)
	values(14, "{&RECORD_NUMBER_OF_MISMATCH_LOGIC_VERIFICATION}", "count", 1);

-- 空字符串检测
insert into template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(15, "{&EMPTY_VERIFICATION}", 1, 1, 1, 1, 1, "select * from ${db}.${table} where (${filter}) and (trim(${field}) = '' )", 1, 1, true,
	"select count(*) from ${db}.${table} where (${filter}) and (trim(${field}) = '' )");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 15, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 15, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 15, "field", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(15, id, "{&NULL_AND_EMPTY_RECORD_NUMBER}", "count", "*", 1, "Long");
insert into template_output_meta(template_id, output_name, field_name, field_type)
	values(15, "{&NULL_AND_EMPTY_RECORD_NUMBER}", "count", 1);

-- 空值或空字符串检测
insert into template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(16, "{&NULL_AND_EMPTY_VERIFICATION}", 1, 1, 1, 1, 1, "select * from ${db}.${table} where (${filter}) and (${field} is null or trim(${field}) = '' )", 1, 1, true,
	"select count(*) from ${db}.${table} where (${filter}) and (${field} is null or trim(${field}) = '' )");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 16, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 16, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 16, "field", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(16, 16, "{&NULL_AND_EMPTY_RECORD_NUMBER}", "count", "*", 1, "Long");
insert into template_output_meta(template_id, output_name, field_name, field_type)
	values(16, "{&NULL_AND_EMPTY_RECORD_NUMBER}", "count", 1);

-- 跨表模版
-- 跨表准确性校验
insert into template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(17, "{&MULTI-TABLE_ACCURACY_VERIFICATION}", 1, 2, 2, 0, 1, "SELECT tmp1.* FROM (SELECT * FROM ${source_db}.${source_table} WHERE ${filter_left}) tmp1 LEFT JOIN (SELECT * FROM ${target_db}.${target_table} WHERE ${filter_right}) tmp2 ON ${mapping_argument} WHERE ( NOT (${source_column_is_null}) AND (${target_column_is_null}) )", 3, 1, true,
	"SELECT count(tmp1.*) FROM (SELECT * FROM ${source_db}.${source_table} WHERE ${filter_left}) tmp1 LEFT JOIN (SELECT * FROM ${target_db}.${target_table} WHERE ${filter_right}) tmp2 ON ${mapping_argument} WHERE ( NOT (${source_column_is_null}) AND (${target_column_is_null}) )");

insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&SOURCE_DATABASE}", 17, "source_db", 11, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&SOURCE_TABLE}", 17, "source_table", 12, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_table}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TARGET_DATABASE}", 17, "target_db", 13, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TARGET_TABLE}", 17, "target_table", 14, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_table}");
insert into template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
	values(10000, "{&JOIN_CONDITION}", 17, "mapping_argument", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${mapping_argument}", "(${left_statement} ${operation} ${right_statement})");
insert into template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
	values(10001, "{&SOURCE_TABLE_COLUMN_IS_NULL}", 17, "source_column_is_null", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_column_is_null}", "${source_column} IS NULL");
insert into template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
	values(10002, "{&TARGET_TABLE_COLUMN_IS_NULL}", 17, "target_column_is_null", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_column_is_null}", "${target_column} IS NULL");

insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_LEFT_EXPRESSION}", null, "left_statement", 15, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${left_statement}", 10000);
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_OPERATION}", null, "operation", 16, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${operation}", 10000);
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_RIGHT_EXPRESSION}", null, "right_statement", 17, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${right_statement}", 10000);
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_LEFT_FILED}", null, "source_column", 18, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_column}", 10001);
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_RIGHT_FILED}", null, "target_column", 19, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_column}", 10002);

insert into template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(17, 17, "", "count", "*", 1, "Long");
insert into template_output_meta(template_id, output_name, field_name, field_type)
	values(17, "{&DIFFERENT_RECORD_BETWEEN_SOURCE_AND_TARGET_TABLE}", "count", 1);

-- 附属模版
insert into template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql, parent_template_id)
	values(18, "{&MULTI-TABLE_ACCURACY_VERIFICATION_CHILD_TEMPLATE}", 1, 2, 2, 0, 1, "SELECT tmp1.* FROM (SELECT * FROM ${source_db}.${source_table} WHERE ${filter_left}) tmp1 LEFT JOIN (SELECT * FROM ${target_db}.${target_table} WHERE ${filter_right}) tmp2 ON ${mapping_argument} WHERE ( NOT (${source_column_is_null}) AND (${target_column_is_null}) )", 3, 1, true,
	"SELECT count(tmp1.*) FROM (SELECT * FROM ${source_db}.${source_table} WHERE ${filter_left}) tmp1 LEFT JOIN (SELECT * FROM ${target_db}.${target_table} WHERE ${filter_right}) tmp2 ON ${mapping_argument} WHERE ( NOT (${source_column_is_null}) AND (${target_column_is_null}) )", 17);

insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&SOURCE_DATABASE}", 18, "source_db", 11, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&SOURCE_TABLE}", 18, "source_table", 12, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_table}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TARGET_DATABASE}", 18, "target_db", 13, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TARGET_TABLE}", 18, "target_table", 14, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_table}");
insert into template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
	values(20000, "{&JOIN_OPERATION}", 18, "mapping_argument", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${mapping_argument}", "(${left_statement} ${operation} ${right_statement})");
insert into template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
	values(20001, "{&SOURCE_TABLE_COLUMN_IS_NULL}", 18, "source_column_is_null", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_column_is_null}", "${source_column} IS NULL");
insert into template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
	values(20002, "{&TARGET_TABLE_COLUMN_IS_NULL}", 18, "target_column_is_null", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_column_is_null}", "${target_column} IS NULL");

insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_LEFT_EXPRESSION}", null, "left_statement", 15, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${left_statement}", 20000);
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_OPERATION}", null, "operation", 16, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${operation}", 20000);
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_RIGHT_EXPRESSION}", null, "right_statement", 17, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${right_statement}", 20000);
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_LEFT_FILED}", null, "source_column", 18, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_column}", 20001);
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_RIGHT_FILED}", null, "target_column", 19, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_column}", 20002);

insert into template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(18, 18, "", "count", "*", 1, "Long");
insert into template_output_meta(template_id, output_name, field_name, field_type)
	values(18, "{&DIFFERENT_RECORD_BETWEEN_TARGET_AND_SOURCE_TABLE}", "count", 1);


-- 跨表通用校验
insert into template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(19, "{&MULTI-TABLE_COMMON_VERIFICATION}", 1, 2, 2, 0, 1, "SELECT tmp1.* FROM (SELECT * FROM ${source_db}.${source_table} WHERE ${filter_left}) tmp1 LEFT JOIN (SELECT * FROM ${target_db}.${target_table} WHERE ${filter_right}) tmp2 ON ${mapping_argument} WHERE ${filter}", 3, 1, true,
	"SELECT count(tmp1.*) FROM (SELECT * FROM ${source_db}.${source_table} WHERE ${filter_left}) tmp1 LEFT JOIN (SELECT * FROM ${target_db}.${target_table} WHERE ${filter_right}) tmp2 ON ${mapping_argument} WHERE ${filter}");

insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&SOURCE_DATABASE}", 19, "source_db", 11, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&SOURCE_TABLE}", 19, "source_table", 12, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_table}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TARGET_DATABASE}", 19, "target_db", 13, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_db}");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TARGET_TABLE}", 19, "target_table", 14, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_table}");
insert into template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
	values(30000, "{&JOIN_OPERATION}", 19, "mapping_argument", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${mapping_argument}", "(${left_statement} ${operation} ${right_statement})");
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FILTER_IN_RESULT}", 19, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}");

insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_LEFT_EXPRESSION}", null, "left_statement", 15, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${left_statement}", 30000);
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_OPERATION}", null, "operation", 16, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${operation}", 30000);
insert into template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_RIGHT_EXPRESSION}", null, "right_statement", 17, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${right_statement}", 30000);

insert into template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(19, 19, "", "count", "*", 1, "Long");
insert into template_output_meta(template_id, output_name, field_name, field_type)
	values(19, "{&NOT_PASS_VERIFICATION_RECORD_NUMBER}", "count", 1);

insert into auth_list(app_id, app_token) values("linkis_id", "a33693de51");