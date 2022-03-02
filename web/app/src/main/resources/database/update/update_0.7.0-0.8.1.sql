/*
  Using update from release-0.7.0 to release-0.8.1
*/

-- ----------------------------
-- For qualitis_rule
-- ----------------------------
ALTER TABLE qualitis_rule ADD cs_id varchar(1024) DEFAULT NULL;
ALTER TABLE qualitis_rule ADD abort_on_failure bit(1) DEFAULT 0;

-- ----------------------------
-- For qualitis_application_task
-- ----------------------------
ALTER TABLE qualitis_application_task abort_on_failure bit(1) DEFAULT 0;

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
 }