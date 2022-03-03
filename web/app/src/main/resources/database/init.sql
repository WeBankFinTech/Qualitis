-- MySQL dump 10.13  Distrib 5.7.35, for Win64 (x86_64)
--
-- Host: 192.168.0.120    Database: qualitis
-- ------------------------------------------------------
-- Server version	5.7.33

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_alarm_info`
--

LOCK TABLES `qualitis_alarm_info` WRITE;
/*!40000 ALTER TABLE `qualitis_alarm_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_alarm_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qualitis_application`
--

DROP TABLE IF EXISTS `qualitis_application`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
  `execution_param` varchar(255) DEFAULT NULL,
  `fps_file_id` varchar(255) DEFAULT NULL,
  `fps_hash` varchar(255) DEFAULT NULL,
  `filter_partition` varchar(255) DEFAULT NULL,
  `run_date` varchar(255) DEFAULT NULL,
  `set_flag` varchar(255) DEFAULT NULL,
  `startup_param` varchar(255) DEFAULT NULL,
  `application_comment` int(11) DEFAULT NULL,
  `project_name` varchar(100) DEFAULT NULL,
  `rule_group_name` varchar(100) DEFAULT NULL,
  `rule_datasource` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_application`
--

LOCK TABLES `qualitis_application` WRITE;
/*!40000 ALTER TABLE `qualitis_application` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_application` ENABLE KEYS */;
UNLOCK TABLES;

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
  `task_exec_id` varchar(255) DEFAULT NULL,
  `task_proxy_user` varchar(255) DEFAULT NULL,
  `new_progress_time` bigint(20) DEFAULT NULL,
  `progress` double DEFAULT NULL,
  `task_comment` int(11) DEFAULT NULL,
  `running_time` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8vt8tfuq1jlqofdsl2bfx602d` (`application_id`),
  CONSTRAINT `FK8vt8tfuq1jlqofdsl2bfx602d` FOREIGN KEY (`application_id`) REFERENCES `qualitis_application` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_application_task`
--

LOCK TABLES `qualitis_application_task` WRITE;
/*!40000 ALTER TABLE `qualitis_application_task` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_application_task` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_application_task_datasource`
--

LOCK TABLES `qualitis_application_task_datasource` WRITE;
/*!40000 ALTER TABLE `qualitis_application_task_datasource` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_application_task_datasource` ENABLE KEYS */;
UNLOCK TABLES;

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
  `rule_metric_id` bigint(20) DEFAULT NULL,
  `save_result` bit(1) DEFAULT b'1',
  `run_date` bigint(20) DEFAULT NULL,
  `department_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_application_task_result`
--

LOCK TABLES `qualitis_application_task_result` WRITE;
/*!40000 ALTER TABLE `qualitis_application_task_result` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_application_task_result` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_application_task_rule_alarm_config`
--

LOCK TABLES `qualitis_application_task_rule_alarm_config` WRITE;
/*!40000 ALTER TABLE `qualitis_application_task_rule_alarm_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_application_task_rule_alarm_config` ENABLE KEYS */;
UNLOCK TABLES;

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
  `alert_level` int(11) DEFAULT NULL,
  `alert_receiver` varchar(255) DEFAULT NULL,
  `rule_group_name` varchar(255) DEFAULT NULL,
  `delete_fail_check_result` bit(1) DEFAULT b'1',
  `template_name` varchar(200) DEFAULT NULL,
  `rule_detail` varchar(340) DEFAULT NULL,
  `cn_name` varchar(128) DEFAULT NULL,
  `project_cn_name` varchar(170) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKiciivreqw0dltknemgrqis9tv` (`parent_rule_simple_id`),
  KEY `FK8nr2cvnqp4pg0q2ftp26v0wnw` (`task_id`),
  CONSTRAINT `FK8nr2cvnqp4pg0q2ftp26v0wnw` FOREIGN KEY (`task_id`) REFERENCES `qualitis_application_task` (`id`),
  CONSTRAINT `FKiciivreqw0dltknemgrqis9tv` FOREIGN KEY (`parent_rule_simple_id`) REFERENCES `qualitis_application_task_rule_simple` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_application_task_rule_simple`
--

LOCK TABLES `qualitis_application_task_rule_simple` WRITE;
/*!40000 ALTER TABLE `qualitis_application_task_rule_simple` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_application_task_rule_simple` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qualitis_auth_department`
--

DROP TABLE IF EXISTS `qualitis_auth_department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_auth_department` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_tem0temhxj86cdqpep31q1iaa` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_auth_department`
--

LOCK TABLES `qualitis_auth_department` WRITE;
/*!40000 ALTER TABLE `qualitis_auth_department` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_auth_department` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_auth_list`
--

LOCK TABLES `qualitis_auth_list` WRITE;
/*!40000 ALTER TABLE `qualitis_auth_list` DISABLE KEYS */;
INSERT INTO `qualitis_auth_list` VALUES ('linkis_id','a33693de51');
/*!40000 ALTER TABLE `qualitis_auth_list` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_auth_permission`
--

LOCK TABLES `qualitis_auth_permission` WRITE;
/*!40000 ALTER TABLE `qualitis_auth_permission` DISABLE KEYS */;
INSERT INTO `qualitis_auth_permission` VALUES (1,'GET','/qualitis/**'),(2,'POST','/qualitis/**'),(3,'DELETE','/qualitis/**'),(4,'PUT','/qualitis/**'),(5,'GET','/qualitis/api/v1/projector/**'),(6,'POST','/qualitis/api/v1/projector/**'),(7,'DELETE','/qualitis/api/v1/projector/**'),(8,'PUT','/qualitis/api/v1/projector/**');
/*!40000 ALTER TABLE `qualitis_auth_permission` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_auth_proxy_user`
--

LOCK TABLES `qualitis_auth_proxy_user` WRITE;
/*!40000 ALTER TABLE `qualitis_auth_proxy_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_auth_proxy_user` ENABLE KEYS */;
UNLOCK TABLES;

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
  UNIQUE KEY `UK_d6h6ies9p214yj1lmwkegdcdc` (`name`),
  KEY `FK3na1kucq8f675ajtj54onfi6` (`department_id`),
  CONSTRAINT `FK3na1kucq8f675ajtj54onfi6` FOREIGN KEY (`department_id`) REFERENCES `qualitis_auth_department` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_auth_role`
--

LOCK TABLES `qualitis_auth_role` WRITE;
/*!40000 ALTER TABLE `qualitis_auth_role` DISABLE KEYS */;
INSERT INTO `qualitis_auth_role` VALUES (1,'ADMIN',NULL),(2,'PROJECTOR',NULL);
/*!40000 ALTER TABLE `qualitis_auth_role` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_auth_role_permission`
--

LOCK TABLES `qualitis_auth_role_permission` WRITE;
/*!40000 ALTER TABLE `qualitis_auth_role_permission` DISABLE KEYS */;
INSERT INTO `qualitis_auth_role_permission` VALUES ('5932425efdfe49949587f51a54e0afaa',5,2),('5932425efdfe49949587f51a54e0afab',6,2),('5932425efdfe49949587f51a54e0afac',7,2),('5932425efdfe49949587f51a54e0afad',8,2),('5932425efdfe49949587f51a54e0affb',1,1),('5932425efdfe49949587f51a54e0affc',2,1),('5932425efdfe49949587f51a54e0affd',3,1),('5932425efdfe49949587f51a54e0affe',4,1);
/*!40000 ALTER TABLE `qualitis_auth_role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qualitis_auth_user`
--

DROP TABLE IF EXISTS `qualitis_auth_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_auth_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `chinese_name` varchar(255) DEFAULT NULL,
  `department` varchar(255) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `user_name` varchar(30) DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `lock_time` bigint(20) DEFAULT NULL,
  `login_error_time` bigint(20) DEFAULT NULL,
  `login_error_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_jsqqcjes14hjorfqihq8i10wr` (`user_name`),
  KEY `FKg2ayqqmqkqbbvvkehqj7fd6la` (`department_id`),
  CONSTRAINT `FKg2ayqqmqkqbbvvkehqj7fd6la` FOREIGN KEY (`department_id`) REFERENCES `qualitis_auth_department` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_auth_user`
--

LOCK TABLES `qualitis_auth_user` WRITE;
/*!40000 ALTER TABLE `qualitis_auth_user` DISABLE KEYS */;
INSERT INTO `qualitis_auth_user` VALUES (1,'管理员','管理员','8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918','admin',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `qualitis_auth_user` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_auth_user_permission`
--

LOCK TABLES `qualitis_auth_user_permission` WRITE;
/*!40000 ALTER TABLE `qualitis_auth_user_permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_auth_user_permission` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_auth_user_proxy_user`
--

LOCK TABLES `qualitis_auth_user_proxy_user` WRITE;
/*!40000 ALTER TABLE `qualitis_auth_user_proxy_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_auth_user_proxy_user` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_auth_user_role`
--

LOCK TABLES `qualitis_auth_user_role` WRITE;
/*!40000 ALTER TABLE `qualitis_auth_user_role` DISABLE KEYS */;
INSERT INTO `qualitis_auth_user_role` VALUES ('5932425efdfe49949587f51a54e0affa',1,1);
/*!40000 ALTER TABLE `qualitis_auth_user_role` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_bdp_client_history`
--

LOCK TABLES `qualitis_bdp_client_history` WRITE;
/*!40000 ALTER TABLE `qualitis_bdp_client_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_bdp_client_history` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_config_cluster_info`
--

LOCK TABLES `qualitis_config_cluster_info` WRITE;
/*!40000 ALTER TABLE `qualitis_config_cluster_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_config_cluster_info` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_config_system`
--

LOCK TABLES `qualitis_config_system` WRITE;
/*!40000 ALTER TABLE `qualitis_config_system` DISABLE KEYS */;
INSERT INTO `qualitis_config_system` VALUES (1,'save_database_pattern','${USERNAME}_ind');
/*!40000 ALTER TABLE `qualitis_config_system` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_project`
--

LOCK TABLES `qualitis_project` WRITE;
/*!40000 ALTER TABLE `qualitis_project` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_project` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_project_event`
--

LOCK TABLES `qualitis_project_event` WRITE;
/*!40000 ALTER TABLE `qualitis_project_event` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_project_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qualitis_project_label`
--

DROP TABLE IF EXISTS `qualitis_project_label`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_project_label` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `label_name` varchar(25) NOT NULL,
  `project_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_project_id_label_name` (`label_name`,`project_id`),
  KEY `FK_qualitis_project_label_project_id` (`project_id`),
  CONSTRAINT `FK_qualitis_project_label_project_id` FOREIGN KEY (`project_id`) REFERENCES `qualitis_project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_project_label`
--

LOCK TABLES `qualitis_project_label` WRITE;
/*!40000 ALTER TABLE `qualitis_project_label` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_project_label` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_project_user`
--

LOCK TABLES `qualitis_project_user` WRITE;
/*!40000 ALTER TABLE `qualitis_project_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_project_user` ENABLE KEYS */;
UNLOCK TABLES;

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
  `name` varchar(170) DEFAULT NULL,
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
  `cn_name` varchar(128) DEFAULT NULL,
  `detail` varchar(340) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK29l9s1h04gntnqv4eje2f93n4` (`project_id`,`name`),
  KEY `FKltabc4x1omja141lo9la6dg4k` (`parent_rule_id`),
  KEY `FK7hv5yh1en46cfwxkqdmixyrn1` (`rule_group_id`),
  KEY `FKf769w3wjl2ywbue7hft6aq8c4` (`template_id`),
  KEY `qualitis_rule_project_id_IDX` (`project_id`) USING BTREE,
  CONSTRAINT `FK7hv5yh1en46cfwxkqdmixyrn1` FOREIGN KEY (`rule_group_id`) REFERENCES `qualitis_rule_group` (`id`),
  CONSTRAINT `FK9tcl2mktybw44ue89mk47sejs` FOREIGN KEY (`project_id`) REFERENCES `qualitis_project` (`id`),
  CONSTRAINT `FKf769w3wjl2ywbue7hft6aq8c4` FOREIGN KEY (`template_id`) REFERENCES `qualitis_template` (`id`),
  CONSTRAINT `FKltabc4x1omja141lo9la6dg4k` FOREIGN KEY (`parent_rule_id`) REFERENCES `qualitis_rule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_rule`
--

LOCK TABLES `qualitis_rule` WRITE;
/*!40000 ALTER TABLE `qualitis_rule` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_rule` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_rule_alarm_config`
--

LOCK TABLES `qualitis_rule_alarm_config` WRITE;
/*!40000 ALTER TABLE `qualitis_rule_alarm_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_rule_alarm_config` ENABLE KEYS */;
UNLOCK TABLES;

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
  `file_delimiter` varchar(255) DEFAULT NULL,
  `file_header` bit(1) DEFAULT NULL,
  `file_id` varchar(256) DEFAULT NULL,
  `file_sheet_name` varchar(255) DEFAULT NULL,
  `file_table_desc` varchar(255) DEFAULT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `file_uuid` varchar(50) DEFAULT NULL,
  `proxy_user` varchar(255) DEFAULT NULL,
  `file_hash_values` varchar(200) DEFAULT NULL,
  `black_col_name` bit(1) DEFAULT b'1',
  `linkis_datasoure_id` bigint(20) DEFAULT NULL,
  `datasource_type` int(11) DEFAULT '1',
  `linkis_datasoure_version_id` bigint(20) DEFAULT NULL,
  `linkis_datasource_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcbr5lp3b6wuh669qglf3dnc6r` (`rule_id`),
  CONSTRAINT `FKcbr5lp3b6wuh669qglf3dnc6r` FOREIGN KEY (`rule_id`) REFERENCES `qualitis_rule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_rule_datasource`
--

LOCK TABLES `qualitis_rule_datasource` WRITE;
/*!40000 ALTER TABLE `qualitis_rule_datasource` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_rule_datasource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qualitis_rule_datasource_count`
--

DROP TABLE IF EXISTS `qualitis_rule_datasource_count`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_rule_datasource_count` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `datasource_count` int(11) DEFAULT NULL,
  `datasource_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKn2rgeii2gfg122j89kgo6qw79` (`user_id`,`datasource_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_rule_datasource_count`
--

LOCK TABLES `qualitis_rule_datasource_count` WRITE;
/*!40000 ALTER TABLE `qualitis_rule_datasource_count` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_rule_datasource_count` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_rule_datasource_mapping`
--

LOCK TABLES `qualitis_rule_datasource_mapping` WRITE;
/*!40000 ALTER TABLE `qualitis_rule_datasource_mapping` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_rule_datasource_mapping` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_rule_group`
--

LOCK TABLES `qualitis_rule_group` WRITE;
/*!40000 ALTER TABLE `qualitis_rule_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_rule_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qualitis_rule_metric`
--

DROP TABLE IF EXISTS `qualitis_rule_metric`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qualitis_rule_metric` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(48) NOT NULL COMMENT '0.14.0 Must be chinese.',
  `metric_desc` varchar(500) NOT NULL,
  `sub_system_id` int(11) NOT NULL,
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
  `sub_system_alias` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `product_id` varchar(255) DEFAULT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `buss_code` int(11) DEFAULT NULL,
  `buss_custom` varchar(100) DEFAULT NULL,
  `cn_name` varchar(512) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNI_RULE_METRIC_name` (`name`),
  UNIQUE KEY `UNI_RULE_METRIC_EN_CODE` (`en_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Created in version qualitis-0.12.0 to manage rule metric.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_rule_metric`
--

LOCK TABLES `qualitis_rule_metric` WRITE;
/*!40000 ALTER TABLE `qualitis_rule_metric` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_rule_metric` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_rule_metric_department_user`
--

LOCK TABLES `qualitis_rule_metric_department_user` WRITE;
/*!40000 ALTER TABLE `qualitis_rule_metric_department_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_rule_metric_department_user` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_rule_metric_type_config`
--

LOCK TABLES `qualitis_rule_metric_type_config` WRITE;
/*!40000 ALTER TABLE `qualitis_rule_metric_type_config` DISABLE KEYS */;
INSERT INTO `qualitis_rule_metric_type_config` VALUES (1,'一般指标','general-metric'),(2,'一般校验指标','general-check-metric'),(3,'一般业务指标','general-busi-check'),(4,'一般IT指标','IT-metric'),(5,'一级指标','1-level-metric'),(6,'二级指标','2-level-metric'),(7,'自定义分类','custom-metric');
/*!40000 ALTER TABLE `qualitis_rule_metric_type_config` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_rule_variable`
--

LOCK TABLES `qualitis_rule_variable` WRITE;
/*!40000 ALTER TABLE `qualitis_rule_variable` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_rule_variable` ENABLE KEYS */;
UNLOCK TABLES;

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
  `import_export_name` varchar(255) DEFAULT NULL,
  `template_level` int(11) DEFAULT NULL,
  `create_user_id` bigint(20) DEFAULT NULL,
  `modify_user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpwhmy0wvpm0ycoifta3nh0fyc` (`parent_template_id`),
  KEY `FKampr04xxfhfqky18levn4svhb` (`create_user_id`),
  KEY `FKd8bp8wlgc9rslq4w3o4ha6w3m` (`modify_user_id`),
  CONSTRAINT `FKampr04xxfhfqky18levn4svhb` FOREIGN KEY (`create_user_id`) REFERENCES `qualitis_auth_user` (`id`),
  CONSTRAINT `FKd8bp8wlgc9rslq4w3o4ha6w3m` FOREIGN KEY (`modify_user_id`) REFERENCES `qualitis_auth_user` (`id`),
  CONSTRAINT `FKpwhmy0wvpm0ycoifta3nh0fyc` FOREIGN KEY (`parent_template_id`) REFERENCES `qualitis_template` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2150 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_template`
--

LOCK TABLES `qualitis_template` WRITE;
/*!40000 ALTER TABLE `qualitis_template` DISABLE KEYS */;
INSERT INTO `qualitis_template` VALUES (1,1,1,1,1,'select * from ${db}.${table} where (${filter}) and (${field} is null)','{&NULL_VERIFICATION}',_binary '','select count(*) from ${db}.${table} where (${filter}) and (${field} is null)',1,1,NULL,NULL,1,NULL,NULL),(2,1,1,1,-1,'select * from ${db}.${table} where ${filter} and (${field_concat}) in (select ${field_concat} from ${db}.${table} where ${filter} group by ${field_concat} having count(*) > 1)','{&PRIMARY_KEY_VERIFICATION}',_binary '','select count(*) from ${db}.${table} where ${filter} and (${field_concat}) in (select ${field_concat} from ${db}.${table} where ${filter} group by ${field_concat} having count(*) > 1)',1,1,NULL,NULL,1,NULL,NULL),(3,1,1,1,0,'select count(*) as myCount from ${db}.${table} where ${filter}','{&TABLE_RECORD_NUMBER_VERIFICATION}',_binary '\0','select count(*) from ${db}.${table} where ${filter}',1,1,NULL,NULL,1,NULL,NULL),(4,1,1,1,1,'select avg(${field}) as myAvg from ${db}.${table} where ${filter}','{&AVERAGE_VALUE_VERIFICATION}',_binary '\0','select avg(${field}) from ${db}.${table} where ${filter}',1,1,NULL,NULL,1,NULL,NULL),(5,1,1,1,1,'select sum(${field}) as mySum from ${db}.${table} where ${filter}','{&SUM_VALUE_VERIFICATION}',_binary '\0','select sum(${field}) from ${db}.${table} where ${filter}',1,1,NULL,NULL,1,NULL,NULL),(6,1,1,1,1,'select max(${field}) as myMax from ${db}.${table} where ${filter}','{&MAX_VALUE_VERIFICATION}',_binary '\0','select max(${field}) from ${db}.${table} where ${filter}',1,1,NULL,NULL,1,NULL,NULL),(7,1,1,1,1,'select min(${field}) as myMin from ${db}.${table} where ${filter}','{&MIN_VALUE_VERIFICATION}',_binary '\0','select min(${field}) from ${db}.${table} where ${filter}',1,1,NULL,NULL,1,NULL,NULL),(8,1,1,1,1,'select * from ${db}.${table} where (${filter}) and (${field} not regexp \'${regexp}\')','{&REGEXP_EXPRESSION_VERIFICATION}',_binary '','select count(*) from ${db}.${table} where (${filter}) and (${field} not regexp \'${regexp}\')',1,1,NULL,NULL,1,NULL,NULL),(9,1,1,1,1,'select * from ${db}.${table} where (${filter}) and (${field} not regexp \'${regexp}\')','{&DATE_FORMAT_VERIFICATION}',_binary '','select count(*) from ${db}.${table} where (${filter}) and (${field} not regexp \'${regexp}\')',1,1,NULL,NULL,1,NULL,NULL),(10,1,1,1,1,'select * from ${db}.${table} where (${filter}) and (${field} not regexp \'${regexp}\')','{&NUMBER_FORMAT_VERIFICATION}',_binary '','select count(*) from ${db}.${table} where (${filter}) and (${field} not regexp \'${regexp}\')',1,1,NULL,NULL,1,NULL,NULL),(11,1,1,1,1,'select * from ${db}.${table} where (${filter}) and (${field} not in ( ${list} ) or ${field} is null)','{&ENUM_VALUE_VERIFICATION}',_binary '','select count(*) from ${db}.${table} where (${filter}) and (${field} not in ( ${list} ) or ${field} is null)',1,1,NULL,NULL,1,NULL,NULL),(12,1,1,1,0,'select * from ${db}.${table} where (${filter}) and (not (${filter2}))','{&NUMBER_RANGE_VERIFICATION}',_binary '','select count(*) from ${db}.${table} where (${filter}) and (not (${filter2}))',1,1,NULL,NULL,1,NULL,NULL),(13,1,1,1,1,'select * from ${db}.${table} where (${filter}) and (${field} not regexp \'${regexp}\')','{&IDENTITY_VERIFICATION}',_binary '','select count(*) from ${db}.${table} where (${filter}) and (${field} not regexp \'${regexp}\')',1,1,NULL,NULL,1,NULL,NULL),(14,1,1,1,0,'select * from ${db}.${table} where (${filter}) and ( (${condition1}) and not (${condition2}) )','{&LOGIC_VERIFICATION}',_binary '','select count(*) from ${db}.${table} where (${filter}) and ( (${condition1}) and not (${condition2}) )',1,1,NULL,NULL,1,NULL,NULL),(15,1,1,1,1,'select * from ${db}.${table} where (${filter}) and (trim(${field}) = \'\' )','{&EMPTY_VERIFICATION}',_binary '','select count(*) from ${db}.${table} where (${filter}) and (trim(${field}) = \'\' )',1,1,NULL,NULL,1,NULL,NULL),(16,1,1,1,1,'select * from ${db}.${table} where (${filter}) and (${field} is null or trim(${field}) = \'\' )','{&NULL_AND_EMPTY_VERIFICATION}',_binary '','select count(*) from ${db}.${table} where (${filter}) and (${field} is null or trim(${field}) = \'\' )',1,1,NULL,NULL,1,NULL,NULL),(17,1,1,2,0,'SELECT tmp1.* FROM (SELECT * FROM ${source_db}.${source_table} WHERE ${filter_left}) tmp1 LEFT JOIN (SELECT * FROM ${target_db}.${target_table} WHERE ${filter_right}) tmp2 ON ${mapping_argument} WHERE ( NOT (${source_column_is_null}) AND (${target_column_is_null}) )','{&MULTI-TABLE_ACCURACY_VERIFICATION}',_binary '','SELECT count(tmp1.*) FROM (SELECT * FROM ${source_db}.${source_table} WHERE ${filter_left}) tmp1 LEFT JOIN (SELECT * FROM ${target_db}.${target_table} WHERE ${filter_right}) tmp2 ON ${mapping_argument} WHERE ( NOT (${source_column_is_null}) AND (${target_column_is_null}) )',2,3,NULL,NULL,1,NULL,NULL),(18,1,1,2,0,'SELECT tmp1.* FROM (SELECT * FROM ${source_db}.${source_table} WHERE ${filter_left}) tmp1 LEFT JOIN (SELECT * FROM ${target_db}.${target_table} WHERE ${filter_right}) tmp2 ON ${mapping_argument} WHERE ( NOT (${source_column_is_null}) AND (${target_column_is_null}) )','{&MULTI-TABLE_ACCURACY_VERIFICATION_CHILD_TEMPLATE}',_binary '','SELECT count(tmp1.*) FROM (SELECT * FROM ${source_db}.${source_table} WHERE ${filter_left}) tmp1 LEFT JOIN (SELECT * FROM ${target_db}.${target_table} WHERE ${filter_right}) tmp2 ON ${mapping_argument} WHERE ( NOT (${source_column_is_null}) AND (${target_column_is_null}) )',2,3,17,NULL,1,NULL,NULL),(19,1,1,2,0,'SELECT tmp1.* FROM (SELECT * FROM ${source_db}.${source_table} WHERE ${filter_left}) tmp1 LEFT JOIN (SELECT * FROM ${target_db}.${target_table} WHERE ${filter_right}) tmp2 ON ${mapping_argument} WHERE ${filter}','{&MULTI-TABLE_COMMON_VERIFICATION}',_binary '','SELECT count(tmp1.*) FROM (SELECT * FROM ${source_db}.${source_table} WHERE ${filter_left}) tmp1 LEFT JOIN (SELECT * FROM ${target_db}.${target_table} WHERE ${filter_right}) tmp2 ON ${mapping_argument} WHERE ${filter}',2,3,NULL,NULL,1,NULL,NULL),(20,1,1,2,0,'SELECT tmp1.* FROM (SELECT ${SOURCE_GROUP_BY_COLUMNS}, count(1) as qualitis_mul_db_accuracy_num FROM ${source_db}.${source_table} WHERE ${filter_left} group by ${SOURCE_GROUP_BY_COLUMNS}) tmp1 LEFT JOIN (SELECT ${TARGET_GROUP_BY_COLUMNS}, count(1) as qualitis_mul_db_accuracy_num FROM ${target_db}.${target_table} WHERE ${filter_right} group by ${TARGET_GROUP_BY_COLUMNS}) tmp2 ON ${mapping_argument} WHERE ( NOT (${source_column_is_null}) AND (${target_column_is_null}) )','{&MULTI-DATABASE_ACCURACY_VERIFICATION}',_binary '','SELECT count(tmp1.*) FROM (SELECT ${SOURCE_GROUP_BY_COLUMNS}, count(1) as qualitis_mul_db_accuracy_num FROM ${source_db}.${source_table} WHERE ${filter_left} group by ${SOURCE_GROUP_BY_COLUMNS}) tmp1 LEFT JOIN (SELECT ${TARGET_GROUP_BY_COLUMNS}, count(1) as qualitis_mul_db_accuracy_num FROM ${target_db}.${target_table} WHERE ${filter_right} group by ${TARGET_GROUP_BY_COLUMNS}) tmp2 ON ${mapping_argument} WHERE ( NOT (${source_column_is_null}) AND (${target_column_is_null}) )',2,3,NULL,NULL,1,NULL,NULL),(2149,1,1,1,-1,'select ${field_replace_null_concat} from ${db}.${table} where ${filter}','{&PRIMARY_LINE_VERIFICATION}',_binary '\0','select max(md5_count) from (select md5(${field_replace_null_concat}) as md5, count(1) as md5_count from ${db}.${table} where ${filter}) tmp where ${filter} group by tmp.md5 having count(*) > 1',1,1,NULL,NULL,1,NULL,NULL);
/*!40000 ALTER TABLE `qualitis_template` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_template_datasource_type`
--

LOCK TABLES `qualitis_template_datasource_type` WRITE;
/*!40000 ALTER TABLE `qualitis_template_datasource_type` DISABLE KEYS */;
INSERT INTO `qualitis_template_datasource_type` VALUES (1,1,1),(2,1,2),(41,1,3),(3,2,1),(4,2,2),(42,2,3),(5,3,1),(6,3,2),(43,3,3),(7,4,1),(8,4,2),(44,4,3),(9,5,1),(10,5,2),(45,5,3),(11,6,1),(12,6,2),(46,6,3),(13,7,1),(14,7,2),(47,7,3),(15,8,1),(16,8,2),(48,8,3),(17,9,1),(18,9,2),(49,9,3),(19,10,1),(20,10,2),(50,10,3),(21,11,1),(22,11,2),(51,11,3),(23,12,1),(24,12,2),(52,12,3),(25,13,1),(26,13,2),(53,13,3),(27,14,1),(28,14,2),(54,14,3),(29,15,1),(30,15,2),(55,15,3),(31,16,1),(32,16,2),(56,16,3),(33,17,1),(34,17,2),(57,17,3),(35,18,1),(36,18,2),(58,18,3),(37,19,1),(38,19,2),(59,19,3),(39,20,1),(40,20,2),(60,20,3),(61,2149,1),(62,2149,2),(63,2149,3);
/*!40000 ALTER TABLE `qualitis_template_datasource_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qualitis_template_department`
--

DROP TABLE IF EXISTS `qualitis_template_department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_template_department`
--

LOCK TABLES `qualitis_template_department` WRITE;
/*!40000 ALTER TABLE `qualitis_template_department` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_template_department` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=31394 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_template_mid_table_input_meta`
--

LOCK TABLES `qualitis_template_mid_table_input_meta` WRITE;
/*!40000 ALTER TABLE `qualitis_template_mid_table_input_meta` DISABLE KEYS */;
INSERT INTO `qualitis_template_mid_table_input_meta` VALUES (10000,'(${left_statement} ${operation} ${right_statement})',NULL,10,'{&JOIN_CONDITION}','mapping_argument','{&REPLACE_PLACEHOLDER_IN_SQL}${mapping_argument}',NULL,_binary '\0',NULL,17),(10001,'${source_column} IS NULL',NULL,10,'{&SOURCE_TABLE_COLUMN_IS_NULL}','source_column_is_null','{&REPLACE_PLACEHOLDER_IN_SQL}${source_column_is_null}',NULL,_binary '\0',NULL,17),(10002,'${target_column} IS NULL',NULL,10,'{&TARGET_TABLE_COLUMN_IS_NULL}','target_column_is_null','{&REPLACE_PLACEHOLDER_IN_SQL}${target_column_is_null}',NULL,_binary '\0',NULL,17),(10010,'(${left_statement} ${operation} ${right_statement} OR (${left_statement} is null and ${right_statement} is null))',NULL,10,'{&JOIN_CONDITION}','mapping_argument','{&REPLACE_PLACEHOLDER_IN_SQL}${mapping_argument}',NULL,_binary '\0',NULL,20),(10011,'${source_column} IS NULL',NULL,10,'{&SOURCE_TABLE_COLUMN_IS_NULL}','source_column_is_null','{&REPLACE_PLACEHOLDER_IN_SQL}${source_column_is_null}',NULL,_binary '\0',NULL,20),(10012,'${target_column} IS NULL',NULL,10,'{&TARGET_TABLE_COLUMN_IS_NULL}','target_column_is_null','{&REPLACE_PLACEHOLDER_IN_SQL}${target_column_is_null}',NULL,_binary '\0',NULL,20),(20000,'(${left_statement} ${operation} ${right_statement})',NULL,10,'{&JOIN_OPERATION}','mapping_argument','{&REPLACE_PLACEHOLDER_IN_SQL}${mapping_argument}',NULL,_binary '\0',NULL,18),(20001,'${source_column} IS NULL',NULL,10,'{&SOURCE_TABLE_COLUMN_IS_NULL}','source_column_is_null','{&REPLACE_PLACEHOLDER_IN_SQL}${source_column_is_null}',NULL,_binary '\0',NULL,18),(20002,'${target_column} IS NULL',NULL,10,'{&TARGET_TABLE_COLUMN_IS_NULL}','target_column_is_null','{&REPLACE_PLACEHOLDER_IN_SQL}${target_column_is_null}',NULL,_binary '\0',NULL,18),(30000,'(${left_statement} ${operation} ${right_statement})',NULL,10,'{&JOIN_OPERATION}','mapping_argument','{&REPLACE_PLACEHOLDER_IN_SQL}${mapping_argument}',NULL,_binary '\0',NULL,19),(30005,NULL,NULL,5,'{&DATABASE}','db','{&REPLACE_PLACEHOLDER_IN_SQL}${db}',NULL,_binary '\0',NULL,1),(30006,NULL,NULL,3,'{&TABLE}','table','{&REPLACE_PLACEHOLDER_IN_SQL}${table}',NULL,_binary '\0',NULL,1),(30007,NULL,NULL,4,'{&FIELD}','field','{&REPLACE_PLACEHOLDER_IN_SQL}${field}',NULL,_binary '\0',NULL,1),(30008,NULL,NULL,5,'{&DATABASE}','db','{&REPLACE_PLACEHOLDER_IN_SQL}${db}',NULL,_binary '\0',NULL,2),(30009,NULL,NULL,3,'{&TABLE}','table','{&REPLACE_PLACEHOLDER_IN_SQL}${table}',NULL,_binary '\0',NULL,2),(30010,NULL,NULL,6,'{&FIELD_CONCAT}','field_concat','{&REPLACE_PLACEHOLDER_IN_SQL}${field_concat}',NULL,_binary '\0',NULL,2),(30011,NULL,NULL,5,'{&DATABASE}','db','{&REPLACE_PLACEHOLDER_IN_SQL}${db}',NULL,_binary '\0',NULL,3),(30012,NULL,NULL,3,'{&TABLE}','table','{&REPLACE_PLACEHOLDER_IN_SQL}${table}',NULL,_binary '\0',NULL,3),(30013,NULL,NULL,5,'{&DATABASE}','db','{&REPLACE_PLACEHOLDER_IN_SQL}${db}',NULL,_binary '\0',NULL,4),(30014,NULL,NULL,3,'{&TABLE}','table','{&REPLACE_PLACEHOLDER_IN_SQL}${table}',NULL,_binary '\0',NULL,4),(30015,NULL,1,4,'{&FIELD}','field','{&REPLACE_PLACEHOLDER_IN_SQL}${field}',NULL,_binary '\0',NULL,4),(30016,NULL,NULL,5,'{&DATABASE}','db','{&REPLACE_PLACEHOLDER_IN_SQL}${db}',NULL,_binary '\0',NULL,5),(30017,NULL,NULL,3,'{&TABLE}','table','{&REPLACE_PLACEHOLDER_IN_SQL}${table}',NULL,_binary '\0',NULL,5),(30018,NULL,1,4,'{&FIELD}','field','{&REPLACE_PLACEHOLDER_IN_SQL}${field}',NULL,_binary '\0',NULL,5),(30019,NULL,NULL,5,'{&DATABASE}','db','{&REPLACE_PLACEHOLDER_IN_SQL}${db}',NULL,_binary '\0',NULL,6),(30020,NULL,NULL,3,'{&TABLE}','table','{&REPLACE_PLACEHOLDER_IN_SQL}${table}',NULL,_binary '\0',NULL,6),(30021,NULL,1,4,'{&FIELD}','field','{&REPLACE_PLACEHOLDER_IN_SQL}${field}',NULL,_binary '\0',NULL,6),(30022,NULL,NULL,5,'{&DATABASE}','db','{&REPLACE_PLACEHOLDER_IN_SQL}${db}',NULL,_binary '\0',NULL,7),(30023,NULL,NULL,3,'{&TABLE}','table','{&REPLACE_PLACEHOLDER_IN_SQL}${table}',NULL,_binary '\0',NULL,7),(30024,NULL,1,4,'{&FIELD}','field','{&REPLACE_PLACEHOLDER_IN_SQL}${field}',NULL,_binary '\0',NULL,7),(30025,NULL,NULL,5,'{&DATABASE}','db','{&REPLACE_PLACEHOLDER_IN_SQL}${db}',NULL,_binary '\0',NULL,8),(30026,NULL,NULL,3,'{&TABLE}','table','{&REPLACE_PLACEHOLDER_IN_SQL}${table}',NULL,_binary '\0',NULL,8),(30027,NULL,NULL,4,'{&FIELD}','field','{&REPLACE_PLACEHOLDER_IN_SQL}${field}',NULL,_binary '\0',NULL,8),(30028,NULL,NULL,7,'{&REGEXP_EXPRESSION}','regexp','{&REPLACE_PLACEHOLDER_IN_SQL}${regexp},{&PLEASE_TYPE_IN_REGEXP_EXPRESSION}',NULL,_binary '',NULL,8),(30029,NULL,NULL,5,'{&DATABASE}','db','{&REPLACE_PLACEHOLDER_IN_SQL}${db}',NULL,_binary '\0',NULL,9),(30030,NULL,NULL,3,'{&TABLE}','table','{&REPLACE_PLACEHOLDER_IN_SQL}${table}',NULL,_binary '\0',NULL,9),(30031,NULL,NULL,4,'{&FIELD}','field','{&REPLACE_PLACEHOLDER_IN_SQL}${field}',NULL,_binary '\0',NULL,9),(30032,NULL,NULL,7,'{&DATE_FORMAT}','regexp','{&REPLACE_PLACEHOLDER_IN_SQL}${regexp},{&CHOOSE_APPROPRIATE}',1,_binary '\0',NULL,9),(30033,NULL,NULL,5,'{&DATABASE}','db','{&REPLACE_PLACEHOLDER_IN_SQL}${db}',NULL,_binary '\0',NULL,10),(30034,NULL,NULL,3,'{&TABLE}','table','{&REPLACE_PLACEHOLDER_IN_SQL}${table}',NULL,_binary '\0',NULL,10),(30035,NULL,NULL,4,'{&FIELD}','field','{&REPLACE_PLACEHOLDER_IN_SQL}${field}',NULL,_binary '\0',NULL,10),(30036,NULL,NULL,7,'{&NUMBER_FORMAT_REGEXP_EXPRESSION}','regexp','{&REPLACE_PLACEHOLDER_IN_SQL}${regexp}',2,_binary '\0',NULL,10),(30037,NULL,NULL,5,'{&DATABASE}','db','{&REPLACE_PLACEHOLDER_IN_SQL}${db}',NULL,_binary '\0',NULL,11),(30038,NULL,NULL,3,'{&TABLE}','table','{&REPLACE_PLACEHOLDER_IN_SQL}${table}',NULL,_binary '\0',NULL,11),(30039,NULL,NULL,4,'{&FIELD}','field','{&REPLACE_PLACEHOLDER_IN_SQL}${field}',NULL,_binary '\0',NULL,11),(30040,NULL,NULL,8,'{&ENUM_VALUE}','list','{&REPLACE_PLACEHOLDER_IN_SQL}${list},示例:\'1,2,3,4\'',NULL,_binary '\0',NULL,11),(30041,NULL,NULL,5,'{&DATABASE}','db','{&REPLACE_PLACEHOLDER_IN_SQL}${db}',NULL,_binary '\0',NULL,12),(30042,NULL,NULL,3,'{&TABLE}','table','{&REPLACE_PLACEHOLDER_IN_SQL}${table}',NULL,_binary '\0',NULL,12),(30043,NULL,NULL,1,'{&NUMBER_RANGE}','filter2','{&REPLACE_PLACEHOLDER_IN_SQL}${filter2}，{&PLEASE_TYPE_IN_NUMBER_RANGE}',NULL,_binary '\0',NULL,12),(30044,NULL,NULL,5,'{&DATABASE}','db','{&REPLACE_PLACEHOLDER_IN_SQL}${db}',NULL,_binary '\0',NULL,13),(30045,NULL,NULL,3,'{&TABLE}','table','{&REPLACE_PLACEHOLDER_IN_SQL}${table}',NULL,_binary '\0',NULL,13),(30046,NULL,NULL,4,'{&FIELD}','field','{&REPLACE_PLACEHOLDER_IN_SQL}${field}',NULL,_binary '\0',NULL,13),(30047,NULL,NULL,7,'{&IDENTITY_REGEXP_EXPRESSION}','regexp','{&REPLACE_PLACEHOLDER_IN_SQL}${regexp}',3,_binary '\0',NULL,13),(30048,NULL,NULL,5,'{&DATABASE}','db','{&REPLACE_PLACEHOLDER_IN_SQL}${db}',NULL,_binary '\0',NULL,14),(30049,NULL,NULL,3,'{&TABLE}','table','{&REPLACE_PLACEHOLDER_IN_SQL}${table}',NULL,_binary '\0',NULL,14),(30050,NULL,NULL,9,'{&PRE_CONDITION}','condition1','{&REPLACE_PLACEHOLDER_IN_SQL}${condition1},{&PLEASE_TYPE_IN_PRE_CONDITION}',NULL,_binary '\0',NULL,14),(30051,NULL,NULL,9,'{&POST_CONDITION}','condition2','{&REPLACE_PLACEHOLDER_IN_SQL}${condition2}，{&PLEASE_TYPE_IN_POST_CONDITION}',NULL,_binary '\0',NULL,14),(30052,NULL,NULL,5,'{&DATABASE}','db','{&REPLACE_PLACEHOLDER_IN_SQL}${db}',NULL,_binary '\0',NULL,15),(30053,NULL,NULL,3,'{&TABLE}','table','{&REPLACE_PLACEHOLDER_IN_SQL}${table}',NULL,_binary '\0',NULL,15),(30054,NULL,NULL,4,'{&FIELD}','field','{&REPLACE_PLACEHOLDER_IN_SQL}${field}',NULL,_binary '\0',NULL,15),(30055,NULL,NULL,5,'{&DATABASE}','db','{&REPLACE_PLACEHOLDER_IN_SQL}${db}',NULL,_binary '\0',NULL,16),(30056,NULL,NULL,3,'{&TABLE}','table','{&REPLACE_PLACEHOLDER_IN_SQL}${table}',NULL,_binary '\0',NULL,16),(30057,NULL,NULL,4,'{&FIELD}','field','{&REPLACE_PLACEHOLDER_IN_SQL}${field}',NULL,_binary '\0',NULL,16),(30058,NULL,NULL,11,'{&SOURCE_DATABASE}','source_db','{&REPLACE_PLACEHOLDER_IN_SQL}${source_db}',NULL,_binary '\0',NULL,17),(30059,NULL,NULL,12,'{&SOURCE_TABLE}','source_table','{&REPLACE_PLACEHOLDER_IN_SQL}${source_table}',NULL,_binary '\0',NULL,17),(30060,NULL,NULL,13,'{&TARGET_DATABASE}','target_db','{&REPLACE_PLACEHOLDER_IN_SQL}${target_db}',NULL,_binary '\0',NULL,17),(30061,NULL,NULL,14,'{&TARGET_TABLE}','target_table','{&REPLACE_PLACEHOLDER_IN_SQL}${target_table}',NULL,_binary '\0',NULL,17),(30062,NULL,NULL,15,'{&JOIN_LEFT_EXPRESSION}','left_statement','{&REPLACE_PLACEHOLDER_IN_SQL}${left_statement}',NULL,_binary '\0',10000,NULL),(30063,NULL,NULL,16,'{&JOIN_OPERATION}','operation','{&REPLACE_PLACEHOLDER_IN_SQL}${operation}',NULL,_binary '\0',10000,NULL),(30064,NULL,NULL,17,'{&JOIN_RIGHT_EXPRESSION}','right_statement','{&REPLACE_PLACEHOLDER_IN_SQL}${right_statement}',NULL,_binary '\0',10000,NULL),(30065,NULL,NULL,18,'{&JOIN_LEFT_FILED}','source_column','{&REPLACE_PLACEHOLDER_IN_SQL}${source_column}',NULL,_binary '\0',10001,NULL),(30066,NULL,NULL,19,'{&JOIN_RIGHT_FILED}','target_column','{&REPLACE_PLACEHOLDER_IN_SQL}${target_column}',NULL,_binary '\0',10002,NULL),(30067,NULL,NULL,11,'{&SOURCE_DATABASE}','source_db','{&REPLACE_PLACEHOLDER_IN_SQL}${source_db}',NULL,_binary '\0',NULL,18),(30068,NULL,NULL,12,'{&SOURCE_TABLE}','source_table','{&REPLACE_PLACEHOLDER_IN_SQL}${source_table}',NULL,_binary '\0',NULL,18),(30069,NULL,NULL,13,'{&TARGET_DATABASE}','target_db','{&REPLACE_PLACEHOLDER_IN_SQL}${target_db}',NULL,_binary '\0',NULL,18),(30070,NULL,NULL,14,'{&TARGET_TABLE}','target_table','{&REPLACE_PLACEHOLDER_IN_SQL}${target_table}',NULL,_binary '\0',NULL,18),(30071,NULL,NULL,15,'{&JOIN_LEFT_EXPRESSION}','left_statement','{&REPLACE_PLACEHOLDER_IN_SQL}${left_statement}',NULL,_binary '\0',20000,NULL),(30072,NULL,NULL,16,'{&JOIN_OPERATION}','operation','{&REPLACE_PLACEHOLDER_IN_SQL}${operation}',NULL,_binary '\0',20000,NULL),(30073,NULL,NULL,17,'{&JOIN_RIGHT_EXPRESSION}','right_statement','{&REPLACE_PLACEHOLDER_IN_SQL}${right_statement}',NULL,_binary '\0',20000,NULL),(30074,NULL,NULL,18,'{&JOIN_LEFT_FILED}','source_column','{&REPLACE_PLACEHOLDER_IN_SQL}${source_column}',NULL,_binary '\0',20001,NULL),(30075,NULL,NULL,19,'{&JOIN_RIGHT_FILED}','target_column','{&REPLACE_PLACEHOLDER_IN_SQL}${target_column}',NULL,_binary '\0',20002,NULL),(30076,NULL,NULL,11,'{&SOURCE_DATABASE}','source_db','{&REPLACE_PLACEHOLDER_IN_SQL}${source_db}',NULL,_binary '\0',NULL,19),(30077,NULL,NULL,12,'{&SOURCE_TABLE}','source_table','{&REPLACE_PLACEHOLDER_IN_SQL}${source_table}',NULL,_binary '\0',NULL,19),(30078,NULL,NULL,13,'{&TARGET_DATABASE}','target_db','{&REPLACE_PLACEHOLDER_IN_SQL}${target_db}',NULL,_binary '\0',NULL,19),(30079,NULL,NULL,14,'{&TARGET_TABLE}','target_table','{&REPLACE_PLACEHOLDER_IN_SQL}${target_table}',NULL,_binary '\0',NULL,19),(30080,NULL,NULL,9,'{&FILTER_IN_RESULT}','filter','{&REPLACE_PLACEHOLDER_IN_SQL}${filter}',NULL,_binary '\0',NULL,19),(30081,NULL,NULL,15,'{&JOIN_LEFT_EXPRESSION}','left_statement','{&REPLACE_PLACEHOLDER_IN_SQL}${left_statement}',NULL,_binary '\0',30000,NULL),(30082,NULL,NULL,16,'{&JOIN_OPERATION}','operation','{&REPLACE_PLACEHOLDER_IN_SQL}${operation}',NULL,_binary '\0',30000,NULL),(30083,NULL,NULL,17,'{&JOIN_RIGHT_EXPRESSION}','right_statement','{&REPLACE_PLACEHOLDER_IN_SQL}${right_statement}',NULL,_binary '\0',30000,NULL),(30910,NULL,NULL,11,'{&SOURCE_DATABASE}','source_db','{&REPLACE_PLACEHOLDER_IN_SQL}${source_db}',NULL,_binary '\0',NULL,20),(30911,NULL,NULL,12,'{&SOURCE_TABLE}','source_table','{&REPLACE_PLACEHOLDER_IN_SQL}${source_table}',NULL,_binary '\0',NULL,20),(30912,NULL,NULL,13,'{&TARGET_DATABASE}','target_db','{&REPLACE_PLACEHOLDER_IN_SQL}${target_db}',NULL,_binary '\0',NULL,20),(30913,NULL,NULL,14,'{&TARGET_TABLE}','target_table','{&REPLACE_PLACEHOLDER_IN_SQL}${target_table}',NULL,_binary '\0',NULL,20),(30914,NULL,NULL,22,'{&SOURCE_GROUP_BY_COLUMNS}','SOURCE_GROUP_BY_COLUMNS','{&REPLACE_PLACEHOLDER_IN_SQL}${SOURCE_GROUP_BY_COLUMNS}',NULL,_binary '\0',NULL,20),(30915,NULL,NULL,23,'{&TARGET_GROUP_BY_COLUMNS}','TARGET_GROUP_BY_COLUMNS','{&REPLACE_PLACEHOLDER_IN_SQL}${TARGET_GROUP_BY_COLUMNS}',NULL,_binary '\0',NULL,20),(30916,NULL,NULL,15,'{&JOIN_LEFT_EXPRESSION}','left_statement','{&REPLACE_PLACEHOLDER_IN_SQL}${left_statement}',NULL,_binary '\0',10010,NULL),(30917,NULL,NULL,16,'{&JOIN_OPERATION}','operation','{&REPLACE_PLACEHOLDER_IN_SQL}${operation}',NULL,_binary '\0',10010,NULL),(30918,NULL,NULL,17,'{&JOIN_RIGHT_EXPRESSION}','right_statement','{&REPLACE_PLACEHOLDER_IN_SQL}${right_statement}',NULL,_binary '\0',10010,NULL),(30919,NULL,NULL,18,'{&JOIN_LEFT_FILED}','source_column','{&REPLACE_PLACEHOLDER_IN_SQL}${source_column}',NULL,_binary '\0',10011,NULL),(30920,NULL,NULL,19,'{&JOIN_RIGHT_FILED}','target_column','{&REPLACE_PLACEHOLDER_IN_SQL}${target_column}',NULL,_binary '\0',10012,NULL),(31391,NULL,NULL,5,'{&DATABASE}','db','{&REPLACE_PLACEHOLDER_IN_SQL}${db}',NULL,_binary '\0',NULL,2149),(31392,NULL,NULL,3,'{&TABLE}','table','{&REPLACE_PLACEHOLDER_IN_SQL}${table}',NULL,_binary '\0',NULL,2149),(31393,NULL,NULL,24,'{&FIELD_REPLACE_NULL_CONCAT}','field_replace_null_concat','{&REPLACE_PLACEHOLDER_IN_SQL}${field_replace_null_concat}',NULL,_binary '\0',NULL,2149);
/*!40000 ALTER TABLE `qualitis_template_mid_table_input_meta` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=2106 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_template_output_meta`
--

LOCK TABLES `qualitis_template_output_meta` WRITE;
/*!40000 ALTER TABLE `qualitis_template_output_meta` DISABLE KEYS */;
INSERT INTO `qualitis_template_output_meta` VALUES (1,'count',1,'{&RECORD_NUMBER_OF_NULL}',1),(2,'count',1,'{&PRIMARY_KEY_MULTIPLE_NUMBER}',2),(3,'max',1,'{&TABLE_RECORD_NUMBER}',3),(4,'max',1,'{&AVERAGE_VALUE}',4),(5,'max',1,'{&SUM_VALUE}',5),(6,'max',1,'{&MAX_VALUE}',6),(7,'max',1,'{&MIN_VALUE}',7),(8,'count',1,'{&MISMATCH_RECORD_NUMBER}',8),(9,'count',1,'{&MISMATCH_DATE_FORMAT_RECORD_NUMBER}',9),(10,'count',1,'{&RECORD_NUMBER_OF_MISMATCH_NUMBER_FORMAT}',10),(11,'count',1,'{&RECORD_NUMBER_OF_NOT_IN_ENUM_VALUE}',11),(12,'count',1,'{&RECORD_NUMBER_OF_NOT_NUMBER_RANGE}',12),(13,'count',1,'{&MISMATCH_IDENTITY_RECORD_NUMBER}',13),(14,'count',1,'{&RECORD_NUMBER_OF_MISMATCH_LOGIC_VERIFICATION}',14),(15,'count',1,'{&NULL_AND_EMPTY_RECORD_NUMBER}',15),(16,'count',1,'{&NULL_AND_EMPTY_RECORD_NUMBER}',16),(17,'count',1,'{&DIFFERENT_RECORD_BETWEEN_SOURCE_AND_TARGET_TABLE}',17),(18,'count',1,'{&DIFFERENT_RECORD_BETWEEN_TARGET_AND_SOURCE_TABLE}',18),(19,'count',1,'{&NOT_PASS_VERIFICATION_RECORD_NUMBER}',19),(645,'count',1,'{&DIFFERENT_RECORD_BETWEEN_SOURCE_AND_TARGET_TABLE}',20),(2105,'max',1,'{&LINE_REPEAT_MAX_NUMBER}',2149);
/*!40000 ALTER TABLE `qualitis_template_output_meta` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_template_regexp_expr`
--

LOCK TABLES `qualitis_template_regexp_expr` WRITE;
/*!40000 ALTER TABLE `qualitis_template_regexp_expr` DISABLE KEYS */;
INSERT INTO `qualitis_template_regexp_expr` VALUES (6,'yyyyMMdd',1,'^(?:(?!0000)[0-9]{4}(?:(?:0[1-9]|1[0-2])(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])(?:29|30)|(?:0[13578]|1[02])31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)0229)$'),(7,'yyyy-MM-dd',1,'^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$'),(8,'yyyyMMddHH',1,'^(?:(?!0000)[0-9]{4}(?:(?:0[1-9]|1[0-2])(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])(?:29|30)|(?:0[13578]|1[02])31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)0229)([01][0-9]|2[0-3])$'),(9,NULL,2,'^[-+]?\\d+(\\.\\d+)?$'),(10,NULL,3,'^[1-9][0-9]{5}(18|19|20)[0-9]{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)[0-9]{3}[0-9Xx]$');
/*!40000 ALTER TABLE `qualitis_template_regexp_expr` ENABLE KEYS */;
UNLOCK TABLES;

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
) ENGINE=InnoDB AUTO_INCREMENT=2092 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_template_statistic_input_meta`
--

LOCK TABLES `qualitis_template_statistic_input_meta` WRITE;
/*!40000 ALTER TABLE `qualitis_template_statistic_input_meta` DISABLE KEYS */;
INSERT INTO `qualitis_template_statistic_input_meta` VALUES (4,'max','{&AVERAGE_VALUE}','Long','myAvg',1,4),(5,'max','{&SUM_VALUE}','Long','mySum',1,5),(6,'max','{&MAX_VALUE}','Long','myMax',1,6),(7,'max','{&MIN_VALUE}','Long','myMin',1,7),(8,'count','{&MISMATCH_RECORD_NUMBER}','Long','*',1,8),(9,'count','{&MISMATCH_DATE_FORMAT_RECORD_NUMBER}','Long','*',1,9),(10,'count','{&RECORD_NUMBER_OF_MISMATCH_NUMBER_FORMAT}','Long','*',1,10),(11,'count','{&RECORD_NUMBER_OF_NOT_IN_ENUM_VALUE}','Long','*',1,11),(12,'count','{&RECORD_NUMBER_OF_NOT_NUMBER_RANGE}','Long','*',1,12),(13,'count','{&MISMATCH_IDENTITY_RECORD_NUMBER}','Long','*',1,13),(14,'count','{&RECORD_NUMBER_OF_MISMATCH_LOGIC_VERIFICATION}','Long','*',1,14),(16,'count','{&NULL_AND_EMPTY_RECORD_NUMBER}','Long','*',1,16),(17,'count','','Long','*',1,17),(18,'count','','Long','*',1,18),(19,'count','','Long','*',1,19),(20,'count','{&RECORD_NUMBER_OF_NULL}','Long','*',1,1),(21,'count','{&PRIMARY_KEY_MULTIPLE_NUMBER}','Long','*',1,2),(22,'max','{&TABLE_RECORD_NUMBER}','Long','myCount',1,3),(23,'count','{&NULL_AND_EMPTY_RECORD_NUMBER}','Long','*',1,15),(25,'count','','Long','*',1,20),(2091,'max','{&LINE_REPEAT_MAX_NUMBER}','Long','md5_count',1,2149);
/*!40000 ALTER TABLE `qualitis_template_statistic_input_meta` ENABLE KEYS */;
UNLOCK TABLES;

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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qualitis_template_user`
--

LOCK TABLES `qualitis_template_user` WRITE;
/*!40000 ALTER TABLE `qualitis_template_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `qualitis_template_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'qualitis'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-03-03 23:00:41
