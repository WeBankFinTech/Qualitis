package com.webank.wedatasphere.qualitis.scheduled.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_gaojiedeng@webank.com
 */
public class ScheduledReleaseRequest {

    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("schedule_system")
    private String scheduleSystem;
    @JsonProperty("task_type")
    private Integer taskType;
    @JsonProperty("cluster")
    private String cluster;
    @JsonProperty("scheduled_project_name")
    private String wtssProjectName;
    @JsonProperty("scheduled_work_flow")
    private String workFlow;
    @JsonProperty("scheduled_task_name")
    private String taskName;
    @JsonProperty("approve_number")
    private String approveNumber;
    @JsonProperty("workflow_business_id")
    private Long workflowBusinessId;

    public Long getWorkflowBusinessId() {
        return workflowBusinessId;
    }

    public void setWorkflowBusinessId(Long workflowBusinessId) {
        this.workflowBusinessId = workflowBusinessId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getScheduleSystem() {
        return scheduleSystem;
    }

    public void setScheduleSystem(String scheduleSystem) {
        this.scheduleSystem = scheduleSystem;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
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

    public String getApproveNumber() {
        return approveNumber;
    }

    public void setApproveNumber(String approveNumber) {
        this.approveNumber = approveNumber;
    }


}
