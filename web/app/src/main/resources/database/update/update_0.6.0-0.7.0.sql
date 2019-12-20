/*
  Using update from release-0.6.0 to release-0.7.0
*/

DROP TABLE IF EXISTS `qualitis_config_system`;
CREATE TABLE `qualitis_config_system` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key_name` varchar(50) DEFAULT NULL,
  `value` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_665kcle6t77m5lbm48gohcyyg` (`key_name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

insert into qualitis_config_system(id, key_name, `value`) values(1, "save_database_pattern", "${USERNAME}_ind");