package com.webank.wedatasphere.qualitis.constants;

/**
 * @author v_minminghe@webank.com
 * @date 2024-10-23 16:21
 * @description
 */
public class ThreadPoolConstant {

    /**
     * 失败任务重跑
     */
    public static final String TASK_RERUN = "Task-Rerun-ThreadPool";

    /**
     * dgsm上报
     */
    public static final String DGSM = "Dgsm-ThreadPool";

    /**
     * 系统内部执行规则
     */
    public static final String RULE_EXECUTION = "RuleExecution-ThreadPool";

    /**
     * 外部接口执行规则
     */
    public static final String OUTER_RULE_EXECUTION = "Outer-RuleExecution-ThreadPool";

    /**
     * DSS节点导入导出
     */
    public static final String DSS_RULE_NODE = "DssRuleNode-ThreadPool";

    /**
     * 基于模板批量创建规则
     */
    public static final String RULE_UPDATE = "RuleUpdate-ThreadPool";

    /**
     * bash节点创建规则
     */
    public static final String RULE_BASH_UPDATE = "RuleBashUpdate-ThreadPool";

    /**
     * 异步更新DMS元数据信息
     */
    public static final String SYNC_METADATA = "SyncMetadata-ThreadPool";

    /**
     * 并行批量同步DMS元数据信息并更新至规则数据源表
     */
    public static final String DMS_METADATA_RULE_DATASOURCE_UPDATER = "DmsMetadataOnRuleDataSourceUpdater-ThreadPool";

    /**
     * 并行批量新增或删除项目的用户权限
     */
    public static final String OPERATE_PROJECT_IN_BATCH = "OperateProjectInBatch-ThreadPool";

}
