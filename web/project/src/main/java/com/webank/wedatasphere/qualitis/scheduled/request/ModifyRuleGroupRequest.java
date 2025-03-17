package com.webank.wedatasphere.qualitis.scheduled.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author v_gaojiedeng@webank.com
 */
public class ModifyRuleGroupRequest {

    @JsonProperty("schedule_system")
    private String scheduleSystem;
    @JsonProperty("cluster")
    private String cluster;
    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("work_flow")
    private String workFlow;
    @JsonProperty("task_name")
    private String taskName;
    @JsonProperty("scheduled_task_front_back_id")
    private Long scheduledTaskFrontBackId;
    @JsonProperty("release_user")
    private String releaseUser;

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

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Long getScheduledTaskFrontBackId() {
        return scheduledTaskFrontBackId;
    }

    public void setScheduledTaskFrontBackId(Long scheduledTaskFrontBackId) {
        this.scheduledTaskFrontBackId = scheduledTaskFrontBackId;
    }

    public String getReleaseUser() {
        return releaseUser;
    }

    public void setReleaseUser(String releaseUser) {
        this.releaseUser = releaseUser;
    }

    public static void checkRequest(ModifyRuleGroupRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkString(request.getScheduleSystem(), "schedule_system");
        CommonChecker.checkString(request.getCluster(), "cluster");
        CommonChecker.checkString(request.getProjectName(), "project_name");
        CommonChecker.checkString(request.getWorkFlow(), "work_flow");
        CommonChecker.checkString(request.getTaskName(), "task_name");
        CommonChecker.checkObject(request.getScheduledTaskFrontBackId(),"scheduled_task_front_back_id");
    }


}
