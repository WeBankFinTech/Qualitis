package com.webank.wedatasphere.qualitis.scheduled.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.request.PageRequest;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-13 18:15
 * @description
 */
public class PublishScheduledTaskRequest extends PageRequest {

    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("schedule_system")
    private String scheduleSystem;
    @JsonProperty("scheduled_project_name")
    private String projectName;
    @JsonProperty("scheduled_workflow_name")
    private String workFlow;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("task_name")
    private String taskName;
    @JsonProperty("db_name")
    private String dbName;
    @JsonProperty("table_name")
    private String tableName;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getScheduleSystem() {
        return scheduleSystem;
    }

    public void setScheduleSystem(String scheduleSystem) {
        this.scheduleSystem = scheduleSystem;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getWorkFlow() {
        return workFlow;
    }

    public void setWorkFlow(String workFlow) {
        this.workFlow = workFlow;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
