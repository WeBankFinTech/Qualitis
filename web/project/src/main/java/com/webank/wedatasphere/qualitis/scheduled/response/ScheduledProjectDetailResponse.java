package com.webank.wedatasphere.qualitis.scheduled.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledProject;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-14 9:39
 * @description
 */
public class ScheduledProjectDetailResponse {

    @JsonProperty("scheduled_project_id")
    private Long projectId;
    @JsonProperty("scheduled_project_name")
    private String projectName;
    @JsonProperty("dispatching_system_type")
    private String dispatchingSystemType;
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("publish_user")
    private String releaseUser;
    @JsonProperty("workflow_list")
    private List<ScheduledWorkflowDetailResponse> workflowList;

    public ScheduledProjectDetailResponse(ScheduledProject scheduledProject) {
        this.projectId = scheduledProject.getId();
        this.projectName = scheduledProject.getName();
        this.dispatchingSystemType = scheduledProject.getDispatchingSystemType();
        this.clusterName = scheduledProject.getClusterName();
        this.releaseUser = scheduledProject.getReleaseUser();
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDispatchingSystemType() {
        return dispatchingSystemType;
    }

    public void setDispatchingSystemType(String dispatchingSystemType) {
        this.dispatchingSystemType = dispatchingSystemType;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getReleaseUser() {
        return releaseUser;
    }

    public void setReleaseUser(String releaseUser) {
        this.releaseUser = releaseUser;
    }

    public List<ScheduledWorkflowDetailResponse> getWorkflowList() {
        return workflowList;
    }

    public void setWorkflowList(List<ScheduledWorkflowDetailResponse> workflowList) {
        this.workflowList = workflowList;
    }
}
