/*
  Using update from release-0.5.0 to release-0.6.0
*/

ALTER TABLE application_task_result RENAME TO qualitis_application_task_result;
ALTER TABLE project RENAME TO qualitis_project;
ALTER TABLE project_user RENAME TO qualitis_project_user;
ALTER TABLE rule_alarm_config RENAME TO qualitis_rule_alarm_config;
ALTER TABLE rule RENAME TO qualitis_rule;
ALTER TABLE rule_datasource RENAME TO qualitis_rule_datasource;
ALTER TABLE rule_datasource_mapping RENAME TO qualitis_rule_datasource_mapping;
ALTER TABLE rule_group RENAME TO qualitis_rule_group;
ALTER TABLE rule_variable RENAME TO qualitis_rule_variable;
ALTER TABLE template RENAME TO qualitis_template;
ALTER TABLE template_mid_table_input_meta RENAME TO qualitis_template_mid_table_input_meta;
ALTER TABLE template_output_meta RENAME TO qualitis_template_output_meta;
ALTER TABLE template_regexp_expr RENAME TO qualitis_template_regexp_expr;
ALTER TABLE template_statistic_input_meta RENAME TO qualitis_template_statistic_input_meta;
ALTER TABLE template_user RENAME TO qualitis_template_user;
ALTER TABLE config_cluster_info RENAME TO qualitis_config_cluster_info;
ALTER TABLE application RENAME TO qualitis_application;
ALTER TABLE application_task RENAME TO qualitis_application_task;
ALTER TABLE application_task_datasource RENAME TO qualitis_application_task_datasource;
ALTER TABLE application_task_rule_alarm_config RENAME TO qualitis_application_task_rule_alarm_config;
ALTER TABLE application_task_rule_simple RENAME TO qualitis_application_task_rule_simple;
ALTER TABLE auth_permission RENAME TO qualitis_auth_permission;
ALTER TABLE auth_proxy_user RENAME TO qualitis_auth_proxy_user;
ALTER TABLE auth_role RENAME TO qualitis_auth_role;
ALTER TABLE auth_role_permission RENAME TO qualitis_auth_role_permission;
ALTER TABLE auth_user RENAME TO qualitis_auth_user;
ALTER TABLE auth_user_proxy_user RENAME TO qualitis_auth_user_proxy_user;
ALTER TABLE auth_user_role RENAME TO qualitis_auth_user_role;
ALTER TABLE auth_user_permission RENAME TO qualitis_auth_user_permission;
ALTER TABLE auth_list RENAME TO qualitis_auth_list;

SET FOREIGN_KEY_CHECKS=0;
DROP TABLE IF EXISTS `auth_meta_data`;
DROP TABLE IF EXISTS `meta_data_cluster`;
DROP TABLE IF EXISTS `meta_data_column`;
DROP TABLE IF EXISTS `meta_data_db`;
DROP TABLE IF EXISTS `meta_data_table`;