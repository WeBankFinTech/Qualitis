
CREATE TABLE `qualitis_linkis_datasource_env` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `linkis_data_source_id` bigint(20) NOT NULL,
  `env_id` bigint(20) NOT NULL,
  `env_name` varchar(100) DEFAULT NULL,
  `dcn_num` varchar(100) DEFAULT NULL,
  `logic_area` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uni_env_id` (`env_id`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `qualitis_subscribe_operate_report` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `receiver` varchar(255) DEFAULT NULL,
  `execution_frequency` int(11) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `create_time` varchar(25) DEFAULT NULL,
  `modify_user` varchar(50) DEFAULT NULL,
  `modify_time` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='订阅运营报表';

ALTER TABLE qualitis_application ADD COLUMN collect_ids text;
ALTER TABLE qualitis_application ADD COLUMN env_names varchar(1000);
ALTER TABLE qualitis_application ADD COLUMN run_today varchar(25);
ALTER TABLE qualitis_application MODIFY COLUMN sub_system_id varchar(25);

ALTER TABLE qualitis_application_task_datasource ADD COLUMN filter mediumtext;
ALTER TABLE qualitis_application_task_datasource MODIFY COLUMN sub_system_id varchar(25);

ALTER TABLE qualitis_application_task_result ADD COLUMN task_id bigint(20);

ALTER TABLE qualitis_application_task_rule_simple ADD COLUMN template_en_name varchar(300);

ALTER TABLE qualitis_auth_permission ADD COLUMN cn_name varchar(180);
ALTER TABLE qualitis_auth_permission ADD COLUMN en_name varchar(64);

ALTER TABLE qualitis_execution_parameters ADD COLUMN union_way int(4);

ALTER TABLE qualitis_linkis_datasource ADD COLUMN dcn_range_type varchar(10);


ALTER TABLE qualitis_project ADD COLUMN run_status int(11);
ALTER TABLE qualitis_project MODIFY COLUMN sub_system_id varchar(25);

ALTER TABLE qualitis_rule ADD COLUMN union_way int(4);

ALTER TABLE qualitis_rule_datasource ADD COLUMN collect_sql varchar(5000);
ALTER TABLE qualitis_rule_datasource ADD COLUMN dcn_range_type varchar(10);
ALTER TABLE qualitis_rule_datasource MODIFY COLUMN sub_system_id varchar(25);

ALTER TABLE qualitis_rule_metric MODIFY COLUMN sub_system_id varchar(25);

ALTER TABLE qualitis_rule_udf ADD COLUMN rule_datasource_id bigint(20);

ALTER TABLE qualitis_service ADD COLUMN collect_status int(10);

ALTER TABLE qualitis_template ADD COLUMN calcu_unit_id bigint(20);


CREATE TABLE `qualitis_subscribe_operate_report_associated_projects` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) DEFAULT NULL,
  `operate_report_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKSubscribeOpeateReport` (`project_id`),
  KEY `FKAssociatedProjectsId` (`operate_report_id`),
  CONSTRAINT `FKAssociatedProjectsId` FOREIGN KEY (`operate_report_id`) REFERENCES `qualitis_subscribe_operate_report` (`id`),
  CONSTRAINT `FKSubscribeOpeateReport` FOREIGN KEY (`project_id`) REFERENCES `qualitis_project` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='订阅运营报表关联项目表';

CREATE TABLE `qualitis_subscription_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `project_id` bigint(20) DEFAULT NULL COMMENT '项目id',
  `execution_frequency` int(11) DEFAULT NULL COMMENT '执行频率',
  `configured_rules_table_num` bigint(20) DEFAULT NULL COMMENT '已配置规则的表数量',
  `configured_rules_num` bigint(20) DEFAULT NULL COMMENT '已配置规则数量',
  `configured_rules_kpi_table_num` bigint(20) DEFAULT NULL COMMENT '已配置规则的KPI表数量',
  `configured_rules_kpi_num` bigint(20) DEFAULT NULL COMMENT 'KPI表已配置规则数量',
  `scheduling_rules` bigint(20) DEFAULT NULL COMMENT '在调度监控的规则数',
  `pass_rules` bigint(20) DEFAULT NULL COMMENT '通过校验规则数',
  `no_pass_rules` bigint(20) DEFAULT NULL COMMENT '未通过校验规则数 ',
  `fail_rules` bigint(20) DEFAULT NULL COMMENT '失败规则数 ',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='运营推送记录表';