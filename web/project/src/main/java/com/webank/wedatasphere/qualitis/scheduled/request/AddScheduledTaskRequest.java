package com.webank.wedatasphere.qualitis.scheduled.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.request.ParameterChecker;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-14 9:42
 * @description
 */
public class AddScheduledTaskRequest {

    @JsonProperty(value = "project_id", required = true)
    private Long projectId;
    @JsonProperty(value = "dispatching_system_type", required = true)
    private String dispatchingSystemType;
    @JsonProperty(value = "scheduled_project_name", required = true)
    private String scheduledProjectName;
    @JsonProperty(value = "cluster_name", required = true)
    private String clusterName;
    @JsonProperty(value = "publish_user", required = true)
    private String releaseUser;
    @JsonProperty(value = "workflow_list", required = true)
    private List<ScheduledWorkflowRequest> workflowList;

    public void checkRequest() throws UnExpectedRequestException {
        try {
            ParameterChecker.checkEmpty(this);
        } catch (IllegalAccessException e) {
            throw new UnExpectedRequestException("Server error!Failed to check request parameter.");
        }
        CommonChecker.checkListSize(this.getWorkflowList(), 31, "workflow_list");
        for (ScheduledWorkflowRequest workflowDetailRequest : this.getWorkflowList()) {
            ScheduledWorkflowRequest.checkRequest(workflowDetailRequest);
        }
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getDispatchingSystemType() {
        return dispatchingSystemType;
    }

    public void setDispatchingSystemType(String dispatchingSystemType) {
        this.dispatchingSystemType = dispatchingSystemType;
    }

    public String getScheduledProjectName() {
        return scheduledProjectName;
    }

    public void setScheduledProjectName(String scheduledProjectName) {
        this.scheduledProjectName = scheduledProjectName;
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

    public List<ScheduledWorkflowRequest> getWorkflowList() {
        return workflowList;
    }

    public void setWorkflowList(List<ScheduledWorkflowRequest> workflowList) {
        this.workflowList = workflowList;
    }
}
