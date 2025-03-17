
ALTER TABLE qualitis_scheduled_workflow ADD workflow_business_name varchar(120) NULL COMMENT '应用信息模板name';

ALTER TABLE qualitis_scheduled_task MODIFY COLUMN wtss_task_name varchar(300) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;

ALTER TABLE qualitis_rule_datasource MODIFY COLUMN `db_name` varchar(200) DEFAULT '';

ALTER TABLE qualitis_rule_datasource MODIFY COLUMN `table_name` varchar(200) DEFAULT '';

ALTER TABLE qualitis_rule_variable MODIFY COLUMN `db_name` varchar(200) DEFAULT NULL;

ALTER TABLE qualitis_application_error_code_type ADD wtss_error_expression varchar(128) NULL COMMENT 'WTSS侧返回的异常信息匹配表达式';

ALTER TABLE qualitis_application_task ADD collect_ids text CHARACTER SET utf8 COLLATE utf8_general_ci NULL;

ALTER TABLE qualitis_application_task ADD data_size varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;

ALTER TABLE qualitis_application_task ADD retry int(11) NULL;

ALTER TABLE qualitis_application_task_rule_simple MODIFY COLUMN reg_rule_code varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;

ALTER TABLE qualitis_rule MODIFY COLUMN reg_rule_code varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL;

ALTER TABLE qualitis_service ADD collect_status int(10) NULL;

CREATE TABLE `qualitis_metric_ext` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `metric_id` bigint(20) NOT NULL COMMENT '指标id/采集配置id',
  `metric_class` varchar(10) NOT NULL COMMENT '指标类别（ims-ims指标，rule-规则指标）',
  `calculation_mode` varchar(10) DEFAULT NULL COMMENT 'offline-离线，realtime-实时',
  `monitoring_capabilities` varchar(100) DEFAULT NULL COMMENT '完整性、准确性、一致性、及时性、可靠性和可用性',
  `metric_definition` varchar(200) DEFAULT NULL COMMENT '指标口径（完整性的指标口径可以是记录的缺失率，准确性的指标口径可以是数据与已知预期值的偏差）',
  `business_domain` varchar(200) DEFAULT NULL COMMENT '业务域',
  `business_strategy` varchar(200) DEFAULT NULL COMMENT '业务策略',
  `business_system` varchar(200) DEFAULT NULL COMMENT '业务系统',
  `business_model` varchar(200) DEFAULT NULL COMMENT '业务模型',
  `imsmetric_desc` varchar(200) DEFAULT NULL COMMENT '统计指标描述',
  `create_user` varchar(50) DEFAULT NULL COMMENT '创建用户',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_user` varchar(50) DEFAULT NULL COMMENT '修改用户',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_metric_id_and_class` (`metric_id`,`metric_class`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标信息拓展表'

CREATE TABLE `qualitis_subscribe_operate_report_associated_projects` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) DEFAULT NULL,
  `operate_report_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKSubscribeOpeateReport` (`project_id`),
  KEY `FKAssociatedProjectsId` (`operate_report_id`),
  CONSTRAINT `FKSubscribeOpeateReport` FOREIGN KEY (`project_id`) REFERENCES `qualitis_project` (`id`),
  CONSTRAINT `FKAssociatedProjectsId` FOREIGN KEY (`operate_report_id`) REFERENCES `qualitis_subscribe_operate_report` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='订阅运营报表关联项目表';

CREATE TABLE `qualitis_subscribe_operate_report` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `receiver` varchar(255) DEFAULT NULL,
  `execution_frequency` int(11) DEFAULT NULL,
  `create_user` varchar(50) DEFAULT NULL,
  `create_time` varchar(25) DEFAULT NULL,
  `modify_user` varchar(50) DEFAULT NULL,
  `modify_time` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订阅运营报表';

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='运营推送记录表';

CREATE TABLE `qualitis_rule_datasource_env` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `rule_data_source_id` bigint(20) DEFAULT NULL,
  `linkis_env_name` varchar(100) DEFAULT NULL,
  `linkis_env_id` bigint(20) DEFAULT NULL,
  `db_and_table` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_qualitis_rule_datasource` (`rule_data_source_id`),
  CONSTRAINT `FK_qualitis_rule_datasource` FOREIGN KEY (`rule_data_source_id`) REFERENCES `qualitis_rule_datasource` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='linkis多环境数据源';