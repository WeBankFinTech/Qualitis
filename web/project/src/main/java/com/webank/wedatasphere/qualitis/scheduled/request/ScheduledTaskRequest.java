package com.webank.wedatasphere.qualitis.scheduled.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public class ScheduledTaskRequest {

    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("schedule_system")
    private String scheduleSystem;
    @JsonProperty("task_type")
    private Integer taskType;
    @JsonProperty("cluster")
    private String cluster;
    @JsonProperty("project_name")
    private String wtssProjectName;
    @JsonProperty("work_flow")
    private String workFlow;
    @JsonProperty("task_name")
    private String taskName;
    @JsonProperty("rule_group_id")
    private List<Long> ruleGroupId;
    @JsonProperty("db_name")
    private String dbName;
    @JsonProperty("table_name")
    private String tableName;
    @JsonProperty("approve_number")
    private String approveNumber;

    private int page;
    private int size;

    public String getApproveNumber() {
        return approveNumber;
    }

    public void setApproveNumber(String approveNumber) {
        this.approveNumber = approveNumber;
    }

    public String getScheduleSystem() {
        return scheduleSystem;
    }

    public void setScheduleSystem(String scheduleSystem) {
        this.scheduleSystem = scheduleSystem;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getWtssProjectName() {
        return wtssProjectName;
    }

    public void setWtssProjectName(String wtssProjectName) {
        this.wtssProjectName = wtssProjectName;
    }

    public String getWorkFlow() {
        return workFlow;
    }

    public void setWorkFlow(String workFlow) {
        this.workFlow = workFlow;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<Long> getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(List<Long> ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public ScheduledTaskRequest() {
        this.page = 0;
        this.size = 15;
    }

    public static void checkRequest(ScheduledTaskRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        long page = request.getPage();
        long size = request.getSize();
        if (page < 0) {
            throw new UnExpectedRequestException("page should >= 0, request: " + request);
        }
        if (size <= 0) {
            throw new UnExpectedRequestException("size should > 0, request: " + request);
        }
    }

}
