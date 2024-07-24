package com.webank.wedatasphere.qualitis.scheduled.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public class ModifyScheduledRelationProjectRequest {

    @JsonProperty("scheduled_task_id")
    private Long scheduledTaskId;
    @JsonProperty("project_id")
    private Long projectId;
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
    @JsonProperty("front_rule_group")
    private List<Long> frontRuleGroup;
    @JsonProperty("back_rule_group")
    private List<Long> backRuleGroup;
    @JsonProperty("release_user")
    private String releaseUser;

    public Long getScheduledTaskId() {
        return scheduledTaskId;
    }

    public void setScheduledTaskId(Long scheduledTaskId) {
        this.scheduledTaskId = scheduledTaskId;
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

    public List<Long> getFrontRuleGroup() {
        return frontRuleGroup;
    }

    public void setFrontRuleGroup(List<Long> frontRuleGroup) {
        this.frontRuleGroup = frontRuleGroup;
    }

    public List<Long> getBackRuleGroup() {
        return backRuleGroup;
    }

    public void setBackRuleGroup(List<Long> backRuleGroup) {
        this.backRuleGroup = backRuleGroup;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getReleaseUser() {
        return releaseUser;
    }

    public void setReleaseUser(String releaseUser) {
        this.releaseUser = releaseUser;
    }

    public void checkRequest() throws UnExpectedRequestException {
        CommonChecker.checkObject(this.scheduledTaskId, "scheduled_task_id");
        CommonChecker.checkObject(this.getProjectId(),"project_id");
        CommonChecker.checkString(this.getScheduleSystem(), "schedule_system");
        CommonChecker.checkString(this.getCluster(), "cluster");
        CommonChecker.checkString(this.getProjectName(), "project_name");
    }
}
