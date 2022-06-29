select @dss_appconn_qualitisId:=id from `dss_appconn` where `appconn_name` = 'qualitis';

delete from `dss_appconn_instance` where  `appconn_id`=@dss_appconn_qualitisId;
delete from `dss_appconn`  where `appconn_name`='qualitis';
INSERT INTO `dss_appconn` (
            `appconn_name`,
            `is_user_need_init`,
            `level`,
            `if_iframe`,
            `is_external`,
            `reference`,
            `class_name`,
            `appconn_class_path`,
            `resource`)
            VALUES (
                'qualitis',
                0,
                1,
                1,
                1,
                NULL,
                'com.webank.wedatasphere.dss.appconn.qualitis.QualitisAppConn',
                'DSS_INSTALL_HOME_VAL/dss-appconns/qualitis',
                '');


select @dss_appconn_qualitisId:=id from `dss_appconn` where `appconn_name` = 'qualitis';

INSERT INTO `dss_appconn_instance`(
            `appconn_id`,
            `label`,
            `url`,
            `enhance_json`,
            `homepage_uri`)
            VALUES (@dss_appconn_qualitisId,
            'DEV',
            'http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/',
            '',
            '#/dashboard');

select @dss_qualitis_appconnId:=id from `dss_appconn` WHERE `appconn_name` in ('qualitis');

select @qualitis_menuId:=id from `dss_workspace_menu` WHERE `name` in ('数据质量');
delete from `dss_workspace_menu_appconn` where `title_en`='Qualitis';

INSERT INTO `dss_workspace_menu_appconn` (
    `appconn_id`,
    `menu_id`,
    `title_en`,
    `title_cn`,
    `desc_en`,
    `desc_cn`,
    `labels_en`,
    `labels_cn`,
    `is_active`,
    `access_button_en`,
    `access_button_cn`,
    `manual_button_en`,
    `manual_button_cn`,
    `manual_button_url`,
    `icon`, `order`,
    `create_by`,
    `create_time`,
    `last_update_time`,
    `last_update_user`,
    `image`)
VALUES (@dss_qualitis_appconnId,
       @qualitis_menuId,
       'Qualitis',
       'Qualitis',
       'Qualitis is a financial and one-stop data quality management platform that provides data quality model definition, visualization and monitoring of data quality results',
       'Qualitis是一套金融级、一站式的数据质量管理平台，提供了数据质量模型定义，数据质量结果可视化可监控等功能，并用整套统一的流程来定义和检测数据集的质量并及时报告问题',
       'product, operations',
       '生产,运维',
       '1',
       'enter Qualitis',
       '进入Qualitis',
       'user manual',
       '用户手册',
       'http://127.0.0.1:8088/wiki/scriptis/manual/workspace_cn.html',
       'shujuzhiliang-logo',
       NULL,
       NULL,
       NULL,
       NULL,
       NULL,
       'shujuzhiliang-icon');

select @dss_qualitisId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.qualitis';

delete from `dss_workflow_node_to_ui` where `workflow_node_id`=@dss_qualitisId;
delete from `dss_workflow_node_to_group` where `node_id`=@dss_qualitisId;
delete from `dss_workflow_node` where `node_type`='linkis.appconn.qualitis';

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
            'icons/qualitis.icon',
            'linkis.appconn.qualitis',
            'qualitis',
            1,
            0,
            0,
            1,
            1,
            'qualitis');

select @dss_qualitis_nodeId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.qualitis';

INSERT INTO `dss_workflow_node_to_group` (`node_id`, `group_id`) VALUES (@dss_qualitis_nodeId, 3);
INSERT INTO `dss_workflow_node_to_ui` (workflow_node_id, ui_id) VALUES (@dss_qualitis_nodeId, 1),(@dss_qualitis_nodeId,3),(@dss_qualitis_nodeId,5),(@dss_qualitis_nodeId,6),(@dss_qualitis_nodeId,35),(@dss_qualitis_nodeId,36);
