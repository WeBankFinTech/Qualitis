package com.webank.wedatasphere.qualitis.scheduled.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.scheduled.dao.ScheduledWorkflowTaskRelationDao;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledTask;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflow;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflowTaskRelation;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-18 9:32
 * @description
 */
public class ScheduledPublishTaskResponse {

    @JsonProperty("scheduled_task_id")
    private Long scheduledTaskId;
    @JsonProperty("scheduled_task_name")
    private String taskName;
    @JsonProperty("scheduled_workflow_name")
    private String workflowName;
    @JsonProperty("scheduled_project_id")
    private Long scheduledProjectId;
    @JsonProperty("scheduled_project_name")
    private String scheduledProjectName;
    @JsonProperty("workflow_business_name")
    private String workflowBusinessName;
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("project_type")
    private Integer projectType;
    @JsonProperty("table_group")
    private Boolean tableGroup;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;
    @JsonProperty("rule_group_name")
    private String ruleGroupName;
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("db_name")
    private String dbName = StringUtils.EMPTY;
    @JsonProperty("table_name")
    private String tableName = StringUtils.EMPTY;
    @JsonProperty("schedule_system")
    private String scheduleSystem;
    @JsonProperty("execute_interval")
    private String executeInterval;
    @JsonProperty("execute_date_in_interval")
    private Integer executeDateInInterval;
    @JsonProperty("execute_time_in_date")
    private String executeTimeInDate;
    @JsonProperty("release_status")
    private Integer releaseStatus;
    @JsonProperty("scheduled_type")
    private String scheduledType;

    public ScheduledPublishTaskResponse(ScheduledTask scheduledTask, ScheduledWorkflowTaskRelation scheduledWorkflowTaskRelation) {
        this.scheduledTaskId = scheduledTask.getId();
        this.taskName = scheduledTask.getTaskName();
        if (Objects.nonNull(scheduledWorkflowTaskRelation)) {
            RuleGroup ruleGroup = scheduledWorkflowTaskRelation.getRuleGroup();
            this.ruleGroupId = ruleGroup.getId();
            this.ruleGroupName = ruleGroup.getRuleGroupName();
            this.projectType = scheduledTask.getProject().getProjectType();
            this.projectId = scheduledTask.getProject().getId();
            Set<RuleDataSource> ruleDataSources = ruleGroup.getRuleDataSources();
            if (CollectionUtils.isNotEmpty(ruleDataSources)) {
                this.tableGroup = true;
                for (RuleDataSource ruleDataSource : ruleDataSources) {
                    if (StringUtils.isBlank(ruleDataSource.getTableName())) {
                        continue;
                    }
                    this.dbName = ruleDataSource.getDbName();
                    this.tableName = ruleDataSource.getTableName();
                }
            } else {
                this.tableGroup = false;
                RuleDao ruleDao = SpringContextHolder.getBean(RuleDao.class);
                List<Rule> rules = ruleDao.findByRuleGroup(ruleGroup);
                for (Rule rule: rules) {
                    Set<RuleDataSource> ruleDataSourceSet = rule.getRuleDataSources();
                    for (RuleDataSource ruleDataSource : ruleDataSourceSet) {
                        if (StringUtils.isBlank(ruleDataSource.getTableName())) {
                            continue;
                        }
                        this.dbName = ruleDataSource.getDbName();
                        this.tableName = ruleDataSource.getTableName();
                    }
                }
            }
            this.scheduledProjectId = scheduledWorkflowTaskRelation.getScheduledProject().getId();
            ScheduledWorkflow scheduledWorkflow = scheduledWorkflowTaskRelation.getScheduledWorkflow();
            if (Objects.nonNull(scheduledWorkflow)) {
                this.executeInterval = scheduledWorkflow.getExecuteInterval();
                this.executeDateInInterval = scheduledWorkflow.getExecuteDateInInterval();
                this.executeTimeInDate = scheduledWorkflow.getExecuteTimeInDate();
                this.scheduledType = scheduledWorkflow.getScheduledType();
                this.workflowBusinessName = scheduledWorkflow.getWorkflowBusinessName();
            }
        }
        this.scheduledTaskId = scheduledTask.getId();
        this.scheduledProjectName = scheduledTask.getProjectName();
        this.scheduleSystem = scheduledTask.getDispatchingSystemType();
        this.workflowName = scheduledTask.getWorkFlowName();
        this.clusterName = scheduledTask.getClusterName();
        this.releaseStatus = scheduledTask.getReleaseStatus();
    }

    public String getWorkflowBusinessName() {
        return workflowBusinessName;
    }

    public void setWorkflowBusinessName(String workflowBusinessName) {
        this.workflowBusinessName = workflowBusinessName;
    }

    public Integer getReleaseStatus() {
        return releaseStatus;
    }

    public void setReleaseStatus(Integer releaseStatus) {
        this.releaseStatus = releaseStatus;
    }

    public String getScheduledType() {
        return scheduledType;
    }

    public void setScheduledType(String scheduledType) {
        this.scheduledType = scheduledType;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getProjectType() {
        return projectType;
    }

    public void setProjectType(Integer projectType) {
        this.projectType = projectType;
    }

    public Boolean getTableGroup() {
        return tableGroup;
    }

    public void setTableGroup(Boolean tableGroup) {
        this.tableGroup = tableGroup;
    }

    public Long getScheduledTaskId() {
        return scheduledTaskId;
    }

    public void setScheduledTaskId(Long scheduledTaskId) {
        this.scheduledTaskId = scheduledTaskId;
    }

    public Long getScheduledProjectId() {
        return scheduledProjectId;
    }

    public void setScheduledProjectId(Long scheduledProjectId) {
        this.scheduledProjectId = scheduledProjectId;
    }

    public String getScheduledProjectName() {
        return scheduledProjectName;
    }

    public void setScheduledProjectName(String scheduledProjectName) {
        this.scheduledProjectName = scheduledProjectName;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public void setRuleGroupName(String ruleGroupName) {
        this.ruleGroupName = ruleGroupName;
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

    public String getScheduleSystem() {
        return scheduleSystem;
    }

    public void setScheduleSystem(String scheduleSystem) {
        this.scheduleSystem = scheduleSystem;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getExecuteInterval() {
        return executeInterval;
    }

    public void setExecuteInterval(String executeInterval) {
        this.executeInterval = executeInterval;
    }

    public Integer getExecuteDateInInterval() {
        return executeDateInInterval;
    }

    public void setExecuteDateInInterval(Integer executeDateInInterval) {
        this.executeDateInInterval = executeDateInInterval;
    }

    public String getExecuteTimeInDate() {
        return executeTimeInDate;
    }

    public void setExecuteTimeInDate(String executeTimeInDate) {
        this.executeTimeInDate = executeTimeInDate;
    }
}
