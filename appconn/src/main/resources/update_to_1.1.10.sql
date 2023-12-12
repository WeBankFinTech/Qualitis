delete from `dss_workflow_node_to_ui` where `workflow_node_id`=(select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.qualitis.checkalert');
delete from `dss_workflow_node_to_group` where `node_id`=(select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.qualitis.checkalert');
delete from `dss_workflow_node` where `node_type`='linkis.appconn.qualitis.checkalert';

INSERT INTO `dss_workflow_node` (
            `icon_path`,
            `node_type`,
            `appconn_name`,
            `submit_to_scheduler`,
            `enable_copy`,
            `should_creation_before_node`,
            `support_jump`,
            `jump_type`,
            `name`)
            VALUES (
            'icons/checkalert.icon',
            'linkis.appconn.qualitis.checkalert',
            'qualitis',
            1,
            0,
            0,
            1,
            1,
            'checkalert');



INSERT INTO `dss_workflow_node_to_group` (`node_id`, `group_id`) VALUES ((select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.qualitis.checkalert'), 3);
INSERT INTO `dss_workflow_node_to_ui` (workflow_node_id, ui_id) VALUES ((select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.qualitis.checkalert'), 1), ((select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.qualitis.checkalert'),3), ((select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.qualitis.checkalert'),5), ((select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.qualitis.checkalert'),6), ((select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.qualitis.checkalert'),35), ((select id from `dss_workflow_node` where `node_type` = 'linkis.appconn.qualitis.checkalert'),36);
