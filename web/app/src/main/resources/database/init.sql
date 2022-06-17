
SET FOREIGN_KEY_CHECKS=0;

--
-- Table structure for table `qualitis_application`
--

DROP TABLE IF EXISTS `qualitis_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_application` (
  `id` varchar(40) NOT NULL,
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
  `application_comment` int(11) DEFAULT NULL,
  `project_name` varchar(100) DEFAULT NULL,
  `cluster_name` varchar(100) DEFAULT NULL,
  `startup_param` varchar(2000) DEFAULT NULL,
  `rule_group_name` varchar(100) DEFAULT NULL,
  `rule_datasource` varchar(3000) DEFAULT NULL,
  `execution_param` varchar(255) DEFAULT NULL,
  `set_flag` varchar(2000) DEFAULT NULL,
  `run_date` varchar(25) DEFAULT NULL,
  `filter_partition` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_application_task`
--

DROP TABLE IF EXISTS `qualitis_application_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  `task_comment` int(11) DEFAULT NULL,
  `running_time` bigint(20) DEFAULT NULL,
  `task_exec_id` varchar(255) DEFAULT NULL,
  `task_proxy_user` varchar(50) DEFAULT NULL,
  `new_progress_time` bigint(20) DEFAULT NULL,
  `progress` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8vt8tfuq1jlqofdsl2bfx602d` (`application_id`),
  CONSTRAINT `FK8vt8tfuq1jlqofdsl2bfx602d` FOREIGN KEY (`application_id`) REFERENCES `qualitis_application` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_application_task_datasource`
--

DROP TABLE IF EXISTS `qualitis_application_task_datasource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  PRIMARY KEY (`id`),
  KEY `FKeru6qjd5gwkkm1a58g290g18o` (`task_id`),
  CONSTRAINT `FKeru6qjd5gwkkm1a58g290g18o` FOREIGN KEY (`task_id`) REFERENCES `qualitis_application_task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_application_task_result`
--

DROP TABLE IF EXISTS `qualitis_application_task_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_application_task_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `application_id` varchar(255) DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  `result_type` varchar(255) DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  `value` varchar(128) DEFAULT NULL,
  `run_date` bigint(20) DEFAULT NULL,
  `rule_metric_id` bigint(20) DEFAULT NULL,
  `save_result` bit(1) DEFAULT b'1',
  `department_code` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_application_task_rule_alarm_config`
--

DROP TABLE IF EXISTS `qualitis_application_task_rule_alarm_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_application_task_rule_alarm_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `check_template` int(11) DEFAULT NULL,
  `compare_type` int(11) DEFAULT NULL,
  `output_name` varchar(500) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `threshold` double DEFAULT NULL,
  `task_rule_simple_id` bigint(20) DEFAULT NULL,
  `rule_metric_id` bigint(20) DEFAULT NULL,
  `upload_abnormal_value` bit(1) DEFAULT b'0',
  `upload_rule_metric_value` bit(1) DEFAULT b'0',
  `delete_fail_check_result` bit(1) DEFAULT b'0',
  `output_unit` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrhyx3i15dja1ipm81v3biges` (`task_rule_simple_id`),
  CONSTRAINT `FKrhyx3i15dja1ipm81v3biges` FOREIGN KEY (`task_rule_simple_id`) REFERENCES `qualitis_application_task_rule_simple` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_application_task_rule_simple`
--

DROP TABLE IF EXISTS `qualitis_application_task_rule_simple`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  `parent_rule_simple_id` bigint(20) DEFAULT NULL,
  `task_id` bigint(20) DEFAULT NULL,
  `template_name` varchar(200) DEFAULT NULL,
  `rule_detail` varchar(340) DEFAULT NULL,
  `delete_fail_check_result` bit(1) DEFAULT b'1',
  `cn_name` varchar(128) DEFAULT NULL,
  `project_cn_name` varchar(170) DEFAULT NULL,
  `rule_group_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKiciivreqw0dltknemgrqis9tv` (`parent_rule_simple_id`),
  KEY `FK8nr2cvnqp4pg0q2ftp26v0wnw` (`task_id`),
  CONSTRAINT `FK8nr2cvnqp4pg0q2ftp26v0wnw` FOREIGN KEY (`task_id`) REFERENCES `qualitis_application_task` (`id`),
  CONSTRAINT `FKiciivreqw0dltknemgrqis9tv` FOREIGN KEY (`parent_rule_simple_id`) REFERENCES `qualitis_application_task_rule_simple` (`id`)
) DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_auth_list`
--

DROP TABLE IF EXISTS `qualitis_auth_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_auth_list` (
  `app_id` varchar(255) NOT NULL,
  `app_token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`app_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_auth_permission`
--

DROP TABLE IF EXISTS `qualitis_auth_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_auth_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `method` varchar(6) DEFAULT NULL,
  `url` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_auth_proxy_user`
--

DROP TABLE IF EXISTS `qualitis_auth_proxy_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_auth_proxy_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `proxy_user_name` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_auth_role`
--

DROP TABLE IF EXISTS `qualitis_auth_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_auth_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_d6h6ies9p214yj1lmwkegdcdc` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_auth_role_permission`
--

DROP TABLE IF EXISTS `qualitis_auth_role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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


--
-- Table structure for table `qualitis_auth_user`
--

DROP TABLE IF EXISTS `qualitis_auth_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_auth_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `chinese_name` varchar(255) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `user_name` varchar(30) DEFAULT NULL,
  `lock_time` bigint(20) DEFAULT NULL,
  `login_error_count` int(11) DEFAULT NULL,
  `login_error_time` bigint(20) DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_jsqqcjes14hjorfqihq8i10wr` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_auth_department`
--

DROP TABLE IF EXISTS `qualitis_auth_department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_auth_department` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_department_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_auth_user_permission`
--

DROP TABLE IF EXISTS `qualitis_auth_user_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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


--
-- Table structure for table `qualitis_auth_user_proxy_user`
--

DROP TABLE IF EXISTS `qualitis_auth_user_proxy_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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


--
-- Table structure for table `qualitis_auth_user_role`
--

DROP TABLE IF EXISTS `qualitis_auth_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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


--
-- Table structure for table `qualitis_alarm_info`
--

DROP TABLE IF EXISTS `qualitis_alarm_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


--
-- Table structure for table `qualitis_config_cluster_info`
--

DROP TABLE IF EXISTS `qualitis_config_cluster_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_config_cluster_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cluster_name` varchar(100) DEFAULT NULL,
  `cluster_type` varchar(100) DEFAULT NULL,
  `hive_server2_address` varchar(100) DEFAULT NULL,
  `linkis_address` varchar(100) DEFAULT NULL,
  `linkis_token` varchar(500) DEFAULT NULL,
  `skip_data_size` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_config_system`
--

DROP TABLE IF EXISTS `qualitis_config_system`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_config_system` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key_name` varchar(50) DEFAULT NULL,
  `value` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_665kcle6t77m5lbm48gohcyyg` (`key_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_project`
--

DROP TABLE IF EXISTS `qualitis_project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_user` varchar(50) DEFAULT NULL,
  `create_user_full_name` varchar(50) DEFAULT NULL,
  `create_time` varchar(25) DEFAULT NULL,
  `description` varchar(1700) DEFAULT NULL,
  `name` varchar(170) DEFAULT NULL,
  `project_type` int(11) DEFAULT NULL,
  `user_department` varchar(50) DEFAULT NULL,
  `modify_user` varchar(50) DEFAULT NULL,
  `modify_time` varchar(25) DEFAULT NULL,
  `cn_name` varchar(170) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_project_event`
--

DROP TABLE IF EXISTS `qualitis_project_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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


--
-- Table structure for table `qualitis_project_label`
--

DROP TABLE IF EXISTS `qualitis_project_label`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_project_label` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `label_name` varchar(255) NOT NULL,
  `project_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_project_id_label_name` (`label_name`,`project_id`),
  KEY `FK_qualitis_project_label_project_id` (`project_id`),
  CONSTRAINT `FK_qualitis_project_label_project_id` FOREIGN KEY (`project_id`) REFERENCES `qualitis_project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_project_user`
--

DROP TABLE IF EXISTS `qualitis_project_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_project_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `permission` int(11) DEFAULT NULL,
  `user_full_name` varchar(30) DEFAULT NULL,
  `user_name` varchar(20) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK383dxni31ohf4rl00v5l981ny` (`project_id`),
  KEY `user_name` (`user_name`),
  CONSTRAINT `FK383dxni31ohf4rl00v5l981ny` FOREIGN KEY (`project_id`) REFERENCES `qualitis_project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_rule`
--

DROP TABLE IF EXISTS `qualitis_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  `parent_rule_id` bigint(20) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `rule_group_id` bigint(20) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  `cs_id` varchar(1000) DEFAULT NULL,
  `abort_on_failure` bit(1) DEFAULT b'0',
  `create_user` varchar(50) DEFAULT NULL,
  `create_time` varchar(25) DEFAULT NULL,
  `modify_time` varchar(25) DEFAULT NULL,
  `modify_user` varchar(50) DEFAULT NULL,
  `delete_fail_check_result` bit(1) DEFAULT b'1',
  `specify_static_startup_param` bit(1) DEFAULT b'0',
  `detail` varchar(340) DEFAULT NULL,
  `cn_name` varchar(128) DEFAULT NULL,
  `static_startup_param` varchar(2000) DEFAULT NULL,
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


--
-- Table structure for table `qualitis_rule_alarm_config`
--

DROP TABLE IF EXISTS `qualitis_rule_alarm_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_rule_alarm_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `check_template` int(11) DEFAULT NULL,
  `compare_type` int(11) DEFAULT NULL,
  `threshold` double DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  `template_output_meta_id` bigint(20) DEFAULT NULL,
  `rule_metric_id` bigint(20) DEFAULT NULL,
  `upload_abnormal_value` bit(1) DEFAULT b'0',
  `upload_rule_metric_value` bit(1) DEFAULT b'0',
  `delete_fail_check_result` bit(1) DEFAULT b'0',
  `file_output_name` int(11) DEFAULT NULL,
  `file_output_unit` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKh2hr5kere1f15udbtkk7cc97n` (`rule_id`),
  KEY `FKjq2m5wga1kmck2haw1o867un6` (`template_output_meta_id`),
  CONSTRAINT `FKh2hr5kere1f15udbtkk7cc97n` FOREIGN KEY (`rule_id`) REFERENCES `qualitis_rule` (`id`),
  CONSTRAINT `FKjq2m5wga1kmck2haw1o867un6` FOREIGN KEY (`template_output_meta_id`) REFERENCES `qualitis_template_output_meta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_rule_datasource`
--

DROP TABLE IF EXISTS `qualitis_rule_datasource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_rule_datasource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cluster_name` varchar(100) DEFAULT NULL,
  `col_name` mediumtext,
  `datasource_index` int(11) DEFAULT NULL,
  `db_name` varchar(100) DEFAULT NULL,
  `filter` varchar(3200) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `table_name` varchar(100) DEFAULT NULL,
  `rule_id` bigint(20) DEFAULT NULL,
  `proxy_user` varchar(20) DEFAULT NULL,
  `black_col_name` bit(1) DEFAULT b'1',
  `linkis_datasoure_id` bigint(20) DEFAULT NULL,
  `datasource_type` int(11) DEFAULT '1',
  `linkis_datasoure_version_id` bigint(20) DEFAULT NULL,
  `linkis_datasource_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcbr5lp3b6wuh669qglf3dnc6r` (`rule_id`),
  CONSTRAINT `FKcbr5lp3b6wuh669qglf3dnc6r` FOREIGN KEY (`rule_id`) REFERENCES `qualitis_rule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_rule_datasource_count`
--

DROP TABLE IF EXISTS `qualitis_rule_datasource_count`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_rule_datasource_count` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `datasource_name` varchar(200) NOT NULL,
  `datasource_count` int(11) NOT NULL DEFAULT '1',
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `qualitis_rule_datasouce_count_UN` (`user_id`,`datasource_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_rule_datasource_mapping`
--

DROP TABLE IF EXISTS `qualitis_rule_datasource_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  PRIMARY KEY (`id`),
  KEY `FKnooevousm8ai6i1b82407cq4x` (`rule_id`),
  CONSTRAINT `FKnooevousm8ai6i1b82407cq4x` FOREIGN KEY (`rule_id`) REFERENCES `qualitis_rule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_rule_variable`
--

DROP TABLE IF EXISTS `qualitis_rule_variable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_rule_variable` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cluster_name` varchar(50) DEFAULT NULL,
  `db_name` varchar(50) DEFAULT NULL,
  `input_action_step` int(11) DEFAULT NULL,
  `origin_value` varchar(500) DEFAULT NULL,
  `table_name` varchar(500) DEFAULT NULL,
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


--
-- Table structure for table `qualitis_rule_group`
--

DROP TABLE IF EXISTS `qualitis_rule_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_rule_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) DEFAULT NULL,
  `rule_group_name` varchar(100) DEFAULT NULL,
  `version` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_rule_metric`
--

DROP TABLE IF EXISTS `qualitis_rule_metric`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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


--
-- Table structure for table `qualitis_rule_metric_department_user`
--

DROP TABLE IF EXISTS `qualitis_rule_metric_department_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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


--
-- Table structure for table `qualitis_rule_metric_type_config`
--

DROP TABLE IF EXISTS `qualitis_rule_metric_type_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_rule_metric_type_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cn_name` varchar(50) DEFAULT NULL,
  `en_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_template`
--

DROP TABLE IF EXISTS `qualitis_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  `template_type` int(11) DEFAULT NULL,
  `parent_template_id` bigint(20) DEFAULT NULL,
  `create_user_id` bigint(20) DEFAULT NULL,
  `modify_user_id` bigint(20) DEFAULT NULL,
  `template_level` int(11) DEFAULT '1',
  `import_export_name` varchar(40) DEFAULT NULL,
  `datasource_type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpwhmy0wvpm0ycoifta3nh0fyc` (`parent_template_id`),
  CONSTRAINT `FKpwhmy0wvpm0ycoifta3nh0fyc` FOREIGN KEY (`parent_template_id`) REFERENCES `qualitis_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_template_mid_table_input_meta`
--

DROP TABLE IF EXISTS `qualitis_template_mid_table_input_meta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_template_output_meta`
--

DROP TABLE IF EXISTS `qualitis_template_output_meta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_template_output_meta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `field_name` varchar(50) DEFAULT NULL,
  `field_type` int(11) DEFAULT NULL,
  `output_name` varchar(150) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKia38171mjfi5ix7esd968c0s5` (`template_id`),
  CONSTRAINT `FKia38171mjfi5ix7esd968c0s5` FOREIGN KEY (`template_id`) REFERENCES `qualitis_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_template_regexp_expr`
--

DROP TABLE IF EXISTS `qualitis_template_regexp_expr`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_template_regexp_expr` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key_name` varchar(255) DEFAULT NULL,
  `regexp_type` int(11) DEFAULT NULL,
  `regexp_value` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_template_statistic_input_meta`
--

DROP TABLE IF EXISTS `qualitis_template_statistic_input_meta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_template_user`
--

DROP TABLE IF EXISTS `qualitis_template_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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


--
-- Table structure for table `qualitis_template_department`
--

DROP TABLE IF EXISTS `qualitis_template_department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_template_department` (
  `id` bigint(32) NOT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_department_template` (`department_id`,`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_template_datasource_type`
--

DROP TABLE IF EXISTS `qualitis_template_datasource_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_template_datasource_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `template_id` bigint(20) DEFAULT NULL,
  `data_source_type_id` int(5) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `template_id` (`template_id`,`data_source_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `qualitis_bdp_client_history`
--

DROP TABLE IF EXISTS `qualitis_bdp_client_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_bdp_client_history` (
  `rule_id` bigint(20) NOT NULL,
  `template_function` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`rule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- -------------------------- 插入数据库预先数据 -------------------------
-- 管理员账户
insert into qualitis_auth_user(id, user_name, password, chinese_name, department) values(1, "admin", "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918", "管理员", "管理员");
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
insert into qualitis_template_regexp_expr(regexp_type, regexp_value) values(2, "^[-+]?\\d+(\\.\\d+)?$");

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


-- 行数据重复校验
insert into qualitis_template(id, name, cluster_num, db_num, table_num, field_num, mid_table_action, template_type, action_type, save_mid_table, show_sql) values (2149, "{&PRIMARY_LINE_VERIFICATION}", 1, 1, 1, -1, "select ${field_replace_null_concat} from ${db}.${table} where ${filter}", 1, 1, false, "select max(md5_count) from (select md5(${field_replace_null_concat}) as md5, count(1) as md5_count from ${db}.${table} where ${filter}) tmp where ${filter} group by tmp.md5 having count(*) > 1");

insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description) values (31391, "{&DATABASE}", 2149, "db", 5, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${db}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description) values (31392, "{&TABLE}", 2149, "table", 3, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${table}");
insert into qualitis_template_mid_table_input_meta(id, name, template_id, placeholder, input_type, field_type, replace_by_request, regexp_type, placeholder_description) values (31393, "{&FIELD_REPLACE_NULL_CONCAT}", 2149, "field_replace_null_concat", 24, null, false, null, "{&REPLACE_PLACEHOLDER_IN_SQL}${field_replace_null_concat}");

insert into qualitis_template_statistic_input_meta(id, template_id, name, func_name, value, value_type, result_type) values(2091, 2149, "{&LINE_REPEAT_MAX_NUMBER}", "max", "md5_count", 1, "Long");
insert into qualitis_template_output_meta(template_id, id, output_name, field_name, field_type) values(2149, 2105, "{&LINE_REPEAT_MAX_NUMBER}", "max", 1);

update qualitis_template set template_level = 1;


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

insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("一般指标", "general-metric");
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("一般校验指标", "general-check-metric");
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("一般业务指标", "general-busi-check");
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("一般IT指标", "IT-metric");
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("一级指标", "1-level-metric");
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("二级指标", "2-level-metric");
insert into `qualitis_rule_metric_type_config` (`cn_name`, `en_name`) values("自定义分类", "custom-metric");

insert into qualitis_config_system(id, key_name, `value`) values(1, "save_database_pattern", "${USERNAME}_ind");

insert into qualitis_auth_list(app_id, app_token) values("linkis_id", "a33693de51");



