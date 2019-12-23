
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for auth_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_auth_role_permission`;
CREATE TABLE `qualitis_auth_role_permission` (
  `role_id` bigint(20) NOT NULL,
  `permission_id` bigint(20) NOT NULL,
  KEY `FK5mgu2qwy6vgke5w8ds63it2ni` (`permission_id`),
  KEY `FKmsro136xvh0q33x68slqluhdf` (`role_id`),
  CONSTRAINT `FK5mgu2qwy6vgke5w8ds63it2ni` FOREIGN KEY (`permission_id`) REFERENCES `qualitis_auth_permission` (`id`),
  CONSTRAINT `FKmsro136xvh0q33x68slqluhdf` FOREIGN KEY (`role_id`) REFERENCES `qualitis_auth_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for auth_user_permission
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_auth_user_permission`;
CREATE TABLE `qualitis_auth_user_permission` (
  `user_id` bigint(20) NOT NULL,
  `permission_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`permission_id`),
  KEY `FK8d46965lnl53mk5qqxvdky89u` (`permission_id`),
  CONSTRAINT `FK8d46965lnl53mk5qqxvdky89u` FOREIGN KEY (`permission_id`) REFERENCES `qualitis_auth_permission` (`id`),
  CONSTRAINT `FKbctborxhgbh1e1cw2eq2rej18` FOREIGN KEY (`user_id`) REFERENCES `qualitis_auth_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for auth_user_role
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_auth_user_role`;
CREATE TABLE `qualitis_auth_user_role` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FK2bte4mk8xumyi6mxhjpfqljhk` (`role_id`),
  CONSTRAINT `FK2bte4mk8xumyi6mxhjpfqljhk` FOREIGN KEY (`role_id`) REFERENCES `qualitis_auth_role` (`id`),
  CONSTRAINT `FKfxumv6vh4o8pewtsr7lsdn33y` FOREIGN KEY (`user_id`) REFERENCES `qualitis_auth_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_application
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_application`;
CREATE TABLE `qualitis_application` (
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
-- Table structure for qualitis_application_task
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_application_task`;
CREATE TABLE `qualitis_application_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `begin_time` varchar(25) DEFAULT NULL,
  `cluster_id` varchar(100) DEFAULT NULL,
  `end_time` varchar(25) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `submit_address` varchar(255) DEFAULT NULL,
  `task_remote_id` int(11) DEFAULT NULL,
  `application_id` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8vt8tfuq1jlqofdsl2bfx602d` (`application_id`),
  CONSTRAINT `FK8vt8tfuq1jlqofdsl2bfx602d` FOREIGN KEY (`application_id`) REFERENCES `qualitis_application` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_application_task_datasource
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_application_task_datasource`;
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
  PRIMARY KEY (`id`),
  KEY `FKeru6qjd5gwkkm1a58g290g18o` (`task_id`),
  CONSTRAINT `FKeru6qjd5gwkkm1a58g290g18o` FOREIGN KEY (`task_id`) REFERENCES `qualitis_application_task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_application_task_result
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_application_task_result`;
CREATE TABLE `qualitis_application_task_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `application_id` varchar(255) DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  `result_type` varchar(255) DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  `value` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_application_task_rule_alarm_config
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_application_task_rule_alarm_config`;
CREATE TABLE `qualitis_application_task_rule_alarm_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `check_template` int(11) DEFAULT NULL,
  `compare_type` int(11) DEFAULT NULL,
  `output_name` varchar(500) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `threshold` double DEFAULT NULL,
  `task_rule_simple_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrhyx3i15dja1ipm81v3biges` (`task_rule_simple_id`),
  CONSTRAINT `FKrhyx3i15dja1ipm81v3biges` FOREIGN KEY (`task_rule_simple_id`) REFERENCES `qualitis_application_task_rule_simple` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_application_task_rule_simple
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_application_task_rule_simple`;
CREATE TABLE `qualitis_application_task_rule_simple` (
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
  KEY `FKiciivreqw0dltknemgrqis9tv` (`parent_rule_simple_id`),
  KEY `FK8nr2cvnqp4pg0q2ftp26v0wnw` (`task_id`),
  CONSTRAINT `FK8nr2cvnqp4pg0q2ftp26v0wnw` FOREIGN KEY (`task_id`) REFERENCES `qualitis_application_task` (`id`),
  CONSTRAINT `FKiciivreqw0dltknemgrqis9tv` FOREIGN KEY (`parent_rule_simple_id`) REFERENCES `qualitis_application_task_rule_simple` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_auth_list
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_auth_list`;
CREATE TABLE `qualitis_auth_list` (
  `app_id` varchar(255) NOT NULL,
  `app_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_auth_permission
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_auth_permission`;
CREATE TABLE `qualitis_auth_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `method` varchar(6) DEFAULT NULL,
  `url` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_auth_proxy_user
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_auth_proxy_user`;
CREATE TABLE `qualitis_auth_proxy_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `proxy_user_name` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_auth_role
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_auth_role`;
CREATE TABLE `qualitis_auth_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_d6h6ies9p214yj1lmwkegdcdc` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_auth_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_auth_role_permission`;
CREATE TABLE `qualitis_auth_role_permission` (
  `id` varchar(32) NOT NULL,
  `permission_id` bigint(20) DEFAULT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKs9v745h3b0ekhibqipbj84scv` (`permission_id`),
  KEY `FKjricuk1yv825s34s0cy10x3ns` (`role_id`),
  CONSTRAINT `FKjricuk1yv825s34s0cy10x3ns` FOREIGN KEY (`role_id`) REFERENCES `qualitis_auth_role` (`id`),
  CONSTRAINT `FKs9v745h3b0ekhibqipbj84scv` FOREIGN KEY (`permission_id`) REFERENCES `qualitis_auth_permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_auth_user
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_auth_user`;
CREATE TABLE `qualitis_auth_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `chinese_name` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `username` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_jsqqcjes14hjorfqihq8i10wr` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_auth_user_permission
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_auth_user_permission`;
CREATE TABLE `qualitis_auth_user_permission` (
  `id` varchar(32) NOT NULL,
  `permission_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfh74vev3awmabhwonewr5oogp` (`permission_id`),
  KEY `FK6yvgd2emno63qw1ecnxl77ipa` (`user_id`),
  CONSTRAINT `FK6yvgd2emno63qw1ecnxl77ipa` FOREIGN KEY (`user_id`) REFERENCES `qualitis_auth_user` (`id`),
  CONSTRAINT `FKfh74vev3awmabhwonewr5oogp` FOREIGN KEY (`permission_id`) REFERENCES `qualitis_auth_permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_auth_user_proxy_user
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_auth_user_proxy_user`;
CREATE TABLE `qualitis_auth_user_proxy_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `proxy_user_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpmln0snv5mkc203umorgcjf05` (`proxy_user_id`),
  KEY `FKjrpgawp7y8srylpamisntf34y` (`user_id`),
  CONSTRAINT `FKjrpgawp7y8srylpamisntf34y` FOREIGN KEY (`user_id`) REFERENCES `qualitis_auth_user` (`id`),
  CONSTRAINT `FKpmln0snv5mkc203umorgcjf05` FOREIGN KEY (`proxy_user_id`) REFERENCES `qualitis_auth_proxy_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_auth_user_role
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_auth_user_role`;
CREATE TABLE `qualitis_auth_user_role` (
  `id` varchar(32) NOT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKta8a7krobg79tw41od6tdsex0` (`role_id`),
  KEY `FKeifs7mfg3qs5igw023vta8e7b` (`user_id`),
  CONSTRAINT `FKeifs7mfg3qs5igw023vta8e7b` FOREIGN KEY (`user_id`) REFERENCES `qualitis_auth_user` (`id`),
  CONSTRAINT `FKta8a7krobg79tw41od6tdsex0` FOREIGN KEY (`role_id`) REFERENCES `qualitis_auth_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_config_cluster_info
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_config_cluster_info`;
CREATE TABLE `qualitis_config_cluster_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cluster_name` varchar(100) DEFAULT NULL,
  `cluster_type` varchar(100) DEFAULT NULL,
  `hive_server2_address` varchar(100) DEFAULT NULL,
  `linkis_address` varchar(100) DEFAULT NULL,
  `linkis_token` varchar(500) DEFAULT NULL,
  `meta_store_address` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_config_system
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_config_system`;
CREATE TABLE `qualitis_config_system` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key_name` varchar(50) DEFAULT NULL,
  `value` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_665kcle6t77m5lbm48gohcyyg` (`key_name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_project
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_project`;
CREATE TABLE `qualitis_project` (
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
-- Table structure for qualitis_project_user
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_project_user`;
CREATE TABLE `qualitis_project_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `permission` int(11) DEFAULT NULL,
  `user_full_name` varchar(30) DEFAULT NULL,
  `user_name` varchar(20) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK383dxni31ohf4rl00v5l981ny` (`project_id`),
  CONSTRAINT `FK383dxni31ohf4rl00v5l981ny` FOREIGN KEY (`project_id`) REFERENCES `qualitis_project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_rule
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_rule`;
CREATE TABLE `qualitis_rule` (
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
  UNIQUE KEY `UK29l9s1h04gntnqv4eje2f93n4` (`project_id`,`name`),
  KEY `FKltabc4x1omja141lo9la6dg4k` (`parent_rule_id`),
  KEY `FK7hv5yh1en46cfwxkqdmixyrn1` (`rule_group_id`),
  KEY `FKf769w3wjl2ywbue7hft6aq8c4` (`template_id`),
  CONSTRAINT `FK7hv5yh1en46cfwxkqdmixyrn1` FOREIGN KEY (`rule_group_id`) REFERENCES `qualitis_rule_group` (`id`),
  CONSTRAINT `FK9tcl2mktybw44ue89mk47sejs` FOREIGN KEY (`project_id`) REFERENCES `qualitis_project` (`id`),
  CONSTRAINT `FKf769w3wjl2ywbue7hft6aq8c4` FOREIGN KEY (`template_id`) REFERENCES `qualitis_template` (`id`),
  CONSTRAINT `FKltabc4x1omja141lo9la6dg4k` FOREIGN KEY (`parent_rule_id`) REFERENCES `qualitis_rule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_rule_alarm_config
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_rule_alarm_config`;
CREATE TABLE `qualitis_rule_alarm_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `check_template` int(11) DEFAULT NULL,
  `compare_type` int(11) DEFAULT NULL,
  `threshold` double DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  `template_output_meta_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKh2hr5kere1f15udbtkk7cc97n` (`rule_id`),
  KEY `FKjq2m5wga1kmck2haw1o867un6` (`template_output_meta_id`),
  CONSTRAINT `FKh2hr5kere1f15udbtkk7cc97n` FOREIGN KEY (`rule_id`) REFERENCES `qualitis_rule` (`id`),
  CONSTRAINT `FKjq2m5wga1kmck2haw1o867un6` FOREIGN KEY (`template_output_meta_id`) REFERENCES `qualitis_template_output_meta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_rule_datasource
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_rule_datasource`;
CREATE TABLE `qualitis_rule_datasource` (
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
  KEY `FKcbr5lp3b6wuh669qglf3dnc6r` (`rule_id`),
  CONSTRAINT `FKcbr5lp3b6wuh669qglf3dnc6r` FOREIGN KEY (`rule_id`) REFERENCES `qualitis_rule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_rule_datasource_mapping
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_rule_datasource_mapping`;
CREATE TABLE `qualitis_rule_datasource_mapping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `left_column_names` varchar(2000) DEFAULT NULL,
  `left_statement` varchar(3000) DEFAULT NULL,
  `operation` int(11) DEFAULT NULL,
  `right_column_names` varchar(2000) DEFAULT NULL,
  `right_statement` varchar(3000) DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKnooevousm8ai6i1b82407cq4x` (`rule_id`),
  CONSTRAINT `FKnooevousm8ai6i1b82407cq4x` FOREIGN KEY (`rule_id`) REFERENCES `qualitis_rule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_rule_group
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_rule_group`;
CREATE TABLE `qualitis_rule_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) DEFAULT NULL,
  `rule_group_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_rule_variable
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_rule_variable`;
CREATE TABLE `qualitis_rule_variable` (
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
  KEY `FKgvkh60999kiv1hfc5qtr2b7rt` (`rule_id`),
  KEY `FK9cipdyq5a9xmwfdvybhcw2i8d` (`template_mid_table_input_meta_id`),
  KEY `FKkl4loc3y5qpb618cwglvhyd5h` (`template_statistics_input_meta_id`),
  CONSTRAINT `FK9cipdyq5a9xmwfdvybhcw2i8d` FOREIGN KEY (`template_mid_table_input_meta_id`) REFERENCES `qualitis_template_mid_table_input_meta` (`id`),
  CONSTRAINT `FKgvkh60999kiv1hfc5qtr2b7rt` FOREIGN KEY (`rule_id`) REFERENCES `qualitis_rule` (`id`),
  CONSTRAINT `FKkl4loc3y5qpb618cwglvhyd5h` FOREIGN KEY (`template_statistics_input_meta_id`) REFERENCES `qualitis_template_statistic_input_meta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_template
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_template`;
CREATE TABLE `qualitis_template` (
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
  KEY `FKpwhmy0wvpm0ycoifta3nh0fyc` (`parent_template_id`),
  CONSTRAINT `FKpwhmy0wvpm0ycoifta3nh0fyc` FOREIGN KEY (`parent_template_id`) REFERENCES `qualitis_template` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_template_mid_table_input_meta
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_template_mid_table_input_meta`;
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
  PRIMARY KEY (`id`),
  KEY `FK15rlx42bkg7syh6apwnsss18r` (`parent_id`),
  KEY `FK7antueilfq1itsq2cx29q3xlf` (`template_id`),
  CONSTRAINT `FK15rlx42bkg7syh6apwnsss18r` FOREIGN KEY (`parent_id`) REFERENCES `qualitis_template_mid_table_input_meta` (`id`),
  CONSTRAINT `FK7antueilfq1itsq2cx29q3xlf` FOREIGN KEY (`template_id`) REFERENCES `qualitis_template` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30005 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_template_output_meta
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_template_output_meta`;
CREATE TABLE `qualitis_template_output_meta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `field_name` varchar(50) DEFAULT NULL,
  `field_type` int(11) DEFAULT NULL,
  `output_name` varchar(150) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKia38171mjfi5ix7esd968c0s5` (`template_id`),
  CONSTRAINT `FKia38171mjfi5ix7esd968c0s5` FOREIGN KEY (`template_id`) REFERENCES `qualitis_template` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_template_regexp_expr
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_template_regexp_expr`;
CREATE TABLE `qualitis_template_regexp_expr` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key_name` varchar(255) DEFAULT NULL,
  `regexp_type` int(11) DEFAULT NULL,
  `regexp_value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_template_statistic_input_meta
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_template_statistic_input_meta`;
CREATE TABLE `qualitis_template_statistic_input_meta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `func_name` varchar(5) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `result_type` varchar(255) DEFAULT NULL,
  `value` varchar(50) DEFAULT NULL,
  `value_type` int(11) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKi1irb2fkjcu16pe7jdwsr7h11` (`template_id`),
  CONSTRAINT `FKi1irb2fkjcu16pe7jdwsr7h11` FOREIGN KEY (`template_id`) REFERENCES `qualitis_template` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for qualitis_template_user
-- ----------------------------
DROP TABLE IF EXISTS `qualitis_template_user`;
CREATE TABLE `qualitis_template_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKp4il6ga20u8v6yoyibplo971i` (`template_id`),
  CONSTRAINT `FKp4il6ga20u8v6yoyibplo971i` FOREIGN KEY (`template_id`) REFERENCES `qualitis_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------- 插入数据库预先数据 -------------------------
-- 管理员账户
insert into qualitis_auth_user(id, username, password, chinese_name, department) values(1, "admin", "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918", "管理员", "管理员");
-- 管理员角色
insert into qualitis_auth_role(id, name) values(1, "ADMIN");
insert into qualitis_auth_role(id, name) values(2, "PROJECTOR");
-- 管理员权限
insert into qualitis_auth_permission(id, url, method) values(1, "/qualitis/**", "GET"), (2, "/qualitis/**", "POST"), (3, "/qualitis/**", "DELETE"), (4, "/qualitis/**", "PUT");
insert into qualitis_auth_permission(id, url, method) values(5, "/qualitis/api/v1/projector/**", "GET"), (6, "/qualitis/api/v1/projector/**", "POST"), (7, "/qualitis/api/v1/projector/**", "DELETE"), (8, "/qualitis/api/v1/projector/**", "PUT");
insert into qualitis_auth_user_role(id, user_id, role_id) values("5932425efdfe49949587f51a54e0affa", 1, 1);
insert into qualitis_auth_role_permission(id, role_id, permission_id) values("5932425efdfe49949587f51a54e0affb", 1, 1), ("5932425efdfe49949587f51a54e0affc", 1, 2), ("5932425efdfe49949587f51a54e0affd", 1, 3), ("5932425efdfe49949587f51a54e0affe", 1, 4);
insert into qualitis_auth_role_permission(id, role_id, permission_id) values("5932425efdfe49949587f51a54e0afaa", 2, 5), ("5932425efdfe49949587f51a54e0afab", 2, 6), ("5932425efdfe49949587f51a54e0afac", 2, 7), ("5932425efdfe49949587f51a54e0afad", 2, 8);

-- 规则模版

-- 字段非空检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(1, "{&NULL_VERIFICATION}", 1, 1, 1, 1, 1, "select * from ${db}.${table} where (${filter}) and (${field} is null)", 1, 1, true,
	      "select count(*) from ${db}.${table} where (${filter}) and (${field} is null)");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 1, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 1, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 1, "field", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into qualitis_template_statistic_input_meta(template_id, name, func_name, value, value_type, result_type)
	values(1, "{&RECORD_NUMBER_OF_NULL}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type)
	values(1, 1, "{&RECORD_NUMBER_OF_NULL}", "count", 1);

-- 主键检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(2, "{&PRIMARY_KEY_VERIFICATION}", 1, 1, 1, -1, 1, "select * from ${db}.${table} where ${filter} and (${field_concat}) in (select ${field_concat} from ${db}.${table} where ${filter} group by ${field_concat} having count(*) > 1)", 1, 1, true,
	      "select count(*) from ${db}.${table} where ${filter} and (${field_concat}) in (select ${field_concat} from ${db}.${table} where ${filter} group by ${field_concat} having count(*) > 1)");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 2, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 2, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD_CONCAT}", 2, "field_concat", 6, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field_concat}");
insert into qualitis_template_statistic_input_meta(template_id, name, func_name, value, value_type, result_type)
	values(2, "{&PRIMARY_KEY_MULTIPLE_NUMBER}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type)
	values(2, 2, "{&PRIMARY_KEY_MULTIPLE_NUMBER}", "count", 1);

-- 表行数检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(3, "{&TABLE_RECORD_NUMBER_VERIFICATION}", 1, 1, 1, 0, 1, "select count(*) as myCount from ${db}.${table} where ${filter}", 1, 1, false,
	        "select count(*) from ${db}.${table} where ${filter}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 3, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 3, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_statistic_input_meta(template_id, name, func_name, value, value_type, result_type)
	values(3, "{&TABLE_RECORD_NUMBER}", "max", "myCount", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type)
	values(3, 3, "{&TABLE_RECORD_NUMBER}", "max", 1);

-- 平均值检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(4, "{&AVERAGE_VALUE_VERIFICATION}", 1, 1, 1, 1, 1, "select avg(${field}) as myAvg from ${db}.${table} where ${filter}", 1, 1, false,
	      "select avg(${field}) from ${db}.${table} where ${filter}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 4, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 4, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 4, "field", 4, 1, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(4, 4, "{&AVERAGE_VALUE}", "max", "myAvg", 1, "Long");
insert into qualitis_template_output_meta(template_id, output_name, field_name, field_type)
	values(4, "{&AVERAGE_VALUE}", "max", 1);

-- 总和检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(5, "{&SUM_VALUE_VERIFICATION}", 1, 1, 1, 1, 1, "select sum(${field}) as mySum from ${db}.${table} where ${filter}", 1, 1, false,
	      "select sum(${field}) from ${db}.${table} where ${filter}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 5, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 5, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 5, "field", 4, 1, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(5, 5, "{&SUM_VALUE}", "max", "mySum", 1, "Long");
insert into qualitis_template_output_meta(template_id, output_name, field_name, field_type)
	values(5, "{&SUM_VALUE}", "max", 1);

-- 最大值检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(6, "{&MAX_VALUE_VERIFICATION}", 1, 1, 1, 1, 1, "select max(${field}) as myMax from ${db}.${table} where ${filter}", 1, 1, false,
	"select max(${field}) from ${db}.${table} where ${filter}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 6, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 6, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 6, "field", 4, 1, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(6, 6, "{&MAX_VALUE}", "max", "myMax", 1, "Long");
insert into qualitis_template_output_meta(template_id, output_name, field_name, field_type)
	values(6, "{&MAX_VALUE}", "max", 1);

-- 最小值检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(7, "{&MIN_VALUE_VERIFICATION}", 1, 1, 1, 1, 1, "select min(${field}) as myMin from ${db}.${table} where ${filter}", 1, 1, false,
	"select min(${field}) from ${db}.${table} where ${filter}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 7, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 7, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 7, "field", 4, 1, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(7, 7, "{&MIN_VALUE}", "max", "myMin", 1, "Long");
insert into qualitis_template_output_meta(template_id, output_name, field_name, field_type)
	values(7, "{&MIN_VALUE}", "max", 1);

-- 正则表达式检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(8, "{&REGEXP_EXPRESSION_VERIFICATION}", 1, 1, 1, 1, 1, "select * from ${db}.${table} where (${filter}) and (${field} not regexp '${regexp}')", 1, 1, true,
	"select count(*) from ${db}.${table} where (${filter}) and (${field} not regexp '${regexp}')");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 8, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 8, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 8, "field", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&REGEXP_EXPRESSION}", 8, "regexp", 7, null, true, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${regexp},{&PLEASE_TYPE_IN_REGEXP_EXPRESSION}");
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(8, 8, "{&MISMATCH_RECORD_NUMBER}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, output_name, field_name, field_type)
	values(8, "{&MISMATCH_RECORD_NUMBER}", "count", 1);

-- 时间格式检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(9, "{&DATE_FORMAT_VERIFICATION}", 1, 1, 1, 1, 1, "select * from ${db}.${table} where (${filter}) and (${field} not regexp '${regexp}')", 1, 1, true,
	"select count(*) from ${db}.${table} where (${filter}) and (${field} not regexp '${regexp}')");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 9, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 9, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 9, "field", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATE_FORMAT}", 9, "regexp", 7, null, false, 1, "{&REPLACE_PLACEHOLDER_IN_SQL}${regexp},{&CHOOSE_APPROPRIATE}");
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(9, 9, "{&MISMATCH_DATE_FORMAT_RECORD_NUMBER}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, output_name, field_name, field_type)
	values(9, "{&MISMATCH_DATE_FORMAT_RECORD_NUMBER}", "count", 1);
insert into qualitis_template_regexp_expr(key_name, regexp_type, regexp_value) values("yyyyMMdd", 1, "^(?:(?!0000)[0-9]{4}(?:(?:0[1-9]|1[0-2])(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])(?:29|30)|(?:0[13578]|1[02])31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)0229)$");
insert into qualitis_template_regexp_expr(key_name, regexp_type, regexp_value) values("yyyy-MM-dd", 1, "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$");
insert into qualitis_template_regexp_expr(key_name, regexp_type, regexp_value) values("yyyyMMddHH", 1, "^(?:(?!0000)[0-9]{4}(?:(?:0[1-9]|1[0-2])(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])(?:29|30)|(?:0[13578]|1[02])31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)0229)([01][0-9]|2[0-3])$");

-- 数值格式检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(10, "{&NUMBER_FORMAT_VERIFICATION}", 1, 1, 1, 1, 1, "select * from ${db}.${table} where (${filter}) and (${field} not regexp '${regexp}')", 1, 1, true,
	"select count(*) from ${db}.${table} where (${filter}) and (${field} not regexp '${regexp}')");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 10, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 10, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 10, "field", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&NUMBER_FORMAT_REGEXP_EXPRESSION}", 10, "regexp", 7, null, false, 2, "{&REPLACE_PLACEHOLDER_IN_SQL}${regexp}");
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(10, 10, "{&RECORD_NUMBER_OF_MISMATCH_NUMBER_FORMAT}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, output_name, field_name, field_type)
	values(10, "{&RECORD_NUMBER_OF_MISMATCH_NUMBER_FORMAT}", "count", 1);
insert into qualitis_template_regexp_expr(regexp_type, regexp_value) values(2, "-?[0-9]+(\\\\.[0-9])?[0-9]*$");

-- 枚举值检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(11, "{&ENUM_VALUE_VERIFICATION}", 1, 1, 1, 1, 1, "select * from ${db}.${table} where (${filter}) and (${field} not in ( ${list} ) or ${field} is null)", 1, 1, true,
	"select count(*) from ${db}.${table} where (${filter}) and (${field} not in ( ${list} ) or ${field} is null)");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 11, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 11, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 11, "field", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&ENUM_VALUE}", 11, "list", 8, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${list},示例:'1,2,3,4'");
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(11, 11, "{&RECORD_NUMBER_OF_NOT_IN_ENUM_VALUE}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, output_name, field_name, field_type)
	values(11, "{&RECORD_NUMBER_OF_NOT_IN_ENUM_VALUE}", "count", 1);

-- 数值范围检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(12, "{&NUMBER_RANGE_VERIFICATION}", 1, 1, 1, 0, 1, "select * from ${db}.${table} where (${filter}) and (not (${filter2}))", 1, 1, true,
	"select count(*) from ${db}.${table} where (${filter}) and (not (${filter2}))");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 12, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 12, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&NUMBER_RANGE}", 12, "filter2", 1, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter2}，{&PLEASE_TYPE_IN_NUMBER_RANGE}");
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(12, 12, "{&RECORD_NUMBER_OF_NOT_NUMBER_RANGE}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, output_name, field_name, field_type)
	values(12, "{&RECORD_NUMBER_OF_NOT_NUMBER_RANGE}", "count", 1);

-- 身份证检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(13, "{&IDENTITY_VERIFICATION}", 1, 1, 1, 1, 1, "select * from ${db}.${table} where (${filter}) and (${field} not regexp '${regexp}')", 1, 1, true,
	"select count(*) from ${db}.${table} where (${filter}) and (${field} not regexp '${regexp}')");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 13, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 13, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 13, "field", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&IDENTITY_REGEXP_EXPRESSION}", 13, "regexp", 7, null, false, 3, "{&REPLACE_PLACEHOLDER_IN_SQL}${regexp}");
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(13, 13, "{&MISMATCH_IDENTITY_RECORD_NUMBER}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, output_name, field_name, field_type)
	values(13, "{&MISMATCH_IDENTITY_RECORD_NUMBER}", "count", 1);
insert into qualitis_template_regexp_expr(regexp_type, regexp_value) values(3, "^[1-9][0-9]{5}(18|19|20)[0-9]{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)[0-9]{3}[0-9Xx]$");

-- 逻辑类检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(14, "{&LOGIC_VERIFICATION}", 1, 1, 1, 0, 1, "select * from ${db}.${table} where (${filter}) and ( (${condition1}) and not (${condition2}) )", 1, 1, true,
	"select count(*) from ${db}.${table} where (${filter}) and ( (${condition1}) and not (${condition2}) )");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 14, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 14, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&PRE_CONDITION}", 14, "condition1", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${condition1},{&PLEASE_TYPE_IN_PRE_CONDITION}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&POST_CONDITION}", 14, "condition2", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${condition2}，{&PLEASE_TYPE_IN_POST_CONDITION}");
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(14, 14, "{&RECORD_NUMBER_OF_MISMATCH_LOGIC_VERIFICATION}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, output_name, field_name, field_type)
	values(14, "{&RECORD_NUMBER_OF_MISMATCH_LOGIC_VERIFICATION}", "count", 1);

-- 空字符串检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(15, "{&EMPTY_VERIFICATION}", 1, 1, 1, 1, 1, "select * from ${db}.${table} where (${filter}) and (trim(${field}) = '' )", 1, 1, true,
	"select count(*) from ${db}.${table} where (${filter}) and (trim(${field}) = '' )");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 15, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 15, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 15, "field", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(15, id, "{&NULL_AND_EMPTY_RECORD_NUMBER}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, output_name, field_name, field_type)
	values(15, "{&NULL_AND_EMPTY_RECORD_NUMBER}", "count", 1);

-- 空值或空字符串检测
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(16, "{&NULL_AND_EMPTY_VERIFICATION}", 1, 1, 1, 1, 1, "select * from ${db}.${table} where (${filter}) and (${field} is null or trim(${field}) = '' )", 1, 1, true,
	"select count(*) from ${db}.${table} where (${filter}) and (${field} is null or trim(${field}) = '' )");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&DATABASE}", 16, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TABLE}", 16, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FIELD}", 16, "field", 4, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field}");
insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(16, 16, "{&NULL_AND_EMPTY_RECORD_NUMBER}", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, output_name, field_name, field_type)
	values(16, "{&NULL_AND_EMPTY_RECORD_NUMBER}", "count", 1);

-- 跨表模版
-- 跨表准确性校验
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(17, "{&MULTI-TABLE_ACCURACY_VERIFICATION}", 1, 2, 2, 0, 1, "SELECT tmp1.* FROM (SELECT * FROM ${source_db}.${source_table} WHERE ${filter_left}) tmp1 LEFT JOIN (SELECT * FROM ${target_db}.${target_table} WHERE ${filter_right}) tmp2 ON ${mapping_argument} WHERE ( NOT (${source_column_is_null}) AND (${target_column_is_null}) )", 3, 1, true,
	"SELECT count(tmp1.*) FROM (SELECT * FROM ${source_db}.${source_table} WHERE ${filter_left}) tmp1 LEFT JOIN (SELECT * FROM ${target_db}.${target_table} WHERE ${filter_right}) tmp2 ON ${mapping_argument} WHERE ( NOT (${source_column_is_null}) AND (${target_column_is_null}) )");

insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&SOURCE_DATABASE}", 17, "source_db", 11, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&SOURCE_TABLE}", 17, "source_table", 12, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_table}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TARGET_DATABASE}", 17, "target_db", 13, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TARGET_TABLE}", 17, "target_table", 14, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_table}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
	values(10000, "{&JOIN_CONDITION}", 17, "mapping_argument", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${mapping_argument}", "(${left_statement} ${operation} ${right_statement})");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
	values(10001, "{&SOURCE_TABLE_COLUMN_IS_NULL}", 17, "source_column_is_null", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_column_is_null}", "${source_column} IS NULL");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
	values(10002, "{&TARGET_TABLE_COLUMN_IS_NULL}", 17, "target_column_is_null", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_column_is_null}", "${target_column} IS NULL");

insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_LEFT_EXPRESSION}", null, "left_statement", 15, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${left_statement}", 10000);
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_OPERATION}", null, "operation", 16, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${operation}", 10000);
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_RIGHT_EXPRESSION}", null, "right_statement", 17, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${right_statement}", 10000);
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_LEFT_FILED}", null, "source_column", 18, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_column}", 10001);
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_RIGHT_FILED}", null, "target_column", 19, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_column}", 10002);

insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(17, 17, "", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, output_name, field_name, field_type)
	values(17, "{&DIFFERENT_RECORD_BETWEEN_SOURCE_AND_TARGET_TABLE}", "count", 1);

-- 附属模版
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql, parent_template_id)
	values(18, "{&MULTI-TABLE_ACCURACY_VERIFICATION_CHILD_TEMPLATE}", 1, 2, 2, 0, 1, "SELECT tmp1.* FROM (SELECT * FROM ${source_db}.${source_table} WHERE ${filter_left}) tmp1 LEFT JOIN (SELECT * FROM ${target_db}.${target_table} WHERE ${filter_right}) tmp2 ON ${mapping_argument} WHERE ( NOT (${source_column_is_null}) AND (${target_column_is_null}) )", 3, 1, true,
	"SELECT count(tmp1.*) FROM (SELECT * FROM ${source_db}.${source_table} WHERE ${filter_left}) tmp1 LEFT JOIN (SELECT * FROM ${target_db}.${target_table} WHERE ${filter_right}) tmp2 ON ${mapping_argument} WHERE ( NOT (${source_column_is_null}) AND (${target_column_is_null}) )", 17);

insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&SOURCE_DATABASE}", 18, "source_db", 11, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&SOURCE_TABLE}", 18, "source_table", 12, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_table}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TARGET_DATABASE}", 18, "target_db", 13, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TARGET_TABLE}", 18, "target_table", 14, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_table}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
	values(20000, "{&JOIN_OPERATION}", 18, "mapping_argument", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${mapping_argument}", "(${left_statement} ${operation} ${right_statement})");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
	values(20001, "{&SOURCE_TABLE_COLUMN_IS_NULL}", 18, "source_column_is_null", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_column_is_null}", "${source_column} IS NULL");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
	values(20002, "{&TARGET_TABLE_COLUMN_IS_NULL}", 18, "target_column_is_null", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_column_is_null}", "${target_column} IS NULL");

insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_LEFT_EXPRESSION}", null, "left_statement", 15, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${left_statement}", 20000);
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_OPERATION}", null, "operation", 16, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${operation}", 20000);
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_RIGHT_EXPRESSION}", null, "right_statement", 17, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${right_statement}", 20000);
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_LEFT_FILED}", null, "source_column", 18, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_column}", 20001);
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_RIGHT_FILED}", null, "target_column", 19, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_column}", 20002);

insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(18, 18, "", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, output_name, field_name, field_type)
	values(18, "{&DIFFERENT_RECORD_BETWEEN_TARGET_AND_SOURCE_TABLE}", "count", 1);


-- 跨表通用校验
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, datasource_type, mid_table_action, template_type, action_type, save_mid_table, show_sql)
	values(19, "{&MULTI-TABLE_COMMON_VERIFICATION}", 1, 2, 2, 0, 1, "SELECT tmp1.* FROM (SELECT * FROM ${source_db}.${source_table} WHERE ${filter_left}) tmp1 LEFT JOIN (SELECT * FROM ${target_db}.${target_table} WHERE ${filter_right}) tmp2 ON ${mapping_argument} WHERE ${filter}", 3, 1, true,
	"SELECT count(tmp1.*) FROM (SELECT * FROM ${source_db}.${source_table} WHERE ${filter_left}) tmp1 LEFT JOIN (SELECT * FROM ${target_db}.${target_table} WHERE ${filter_right}) tmp2 ON ${mapping_argument} WHERE ${filter}");

insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&SOURCE_DATABASE}", 19, "source_db", 11, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&SOURCE_TABLE}", 19, "source_table", 12, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${source_table}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TARGET_DATABASE}", 19, "target_db", 13, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_db}");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&TARGET_TABLE}", 19, "target_table", 14, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${target_table}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, concat_template)
	values(30000, "{&JOIN_OPERATION}", 19, "mapping_argument", 10, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${mapping_argument}", "(${left_statement} ${operation} ${right_statement})");
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description)
	values("{&FILTER_IN_RESULT}", 19, "filter", 9, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${filter}");

insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_LEFT_EXPRESSION}", null, "left_statement", 15, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${left_statement}", 30000);
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_OPERATION}", null, "operation", 16, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${operation}", 30000);
insert into qualitis_template_mid_table_input_meta(name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description, parent_id)
	values("{&JOIN_RIGHT_EXPRESSION}", null, "right_statement", 17, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${right_statement}", 30000);

insert into qualitis_template_statistic_input_meta(template_id, id, name, func_name, value, value_type, result_type)
	values(19, 19, "", "count", "*", 1, "Long");
insert into qualitis_template_output_meta(template_id, output_name, field_name, field_type)
	values(19, "{&NOT_PASS_VERIFICATION_RECORD_NUMBER}", "count", 1);

insert into qualitis_config_system(id, key_name, `value`) values(1, "save_database_pattern", "${USERNAME}_ind");

insert into qualitis_auth_list(app_id, app_token) values("linkis_id", "a33693de51");