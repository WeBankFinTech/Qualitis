/*
  Using update from release-0.6.0 to release-0.7.0
*/
DROP TABLE IF EXISTS `qualitis_project_label`;
CREATE TABLE `qualitis_project_label` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `label_name` varchar(25) not null,
  `project_id` bigint(20) not null,
  PRIMARY KEY (`id`),
  CONSTRAINT `UK_project_id_label_name` UNIQUE KEY  (`label_name`, `project_id`),
  CONSTRAINT `FK_qualitis_project_label_project_id` FOREIGN KEY (`project_id`) REFERENCES `qualitis_project` (`id`)
 }