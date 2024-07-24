package com.webank.wedatasphere.qualitis.scheduled.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.request.ParameterChecker;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-14 9:48
 * @description
 */
public class ModifyScheduledTaskRequest {

    @JsonProperty(value = "scheduled_project_id", required = true)
    private Long scheduledProjectId;
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
        CommonChecker.checkListSize(this.getWorkflowList(), 30, "workflow_list");
        for (ScheduledWorkflowRequest workflowDetailRequest : this.getWorkflowList()) {
            ScheduledWorkflowRequest.checkRequest(workflowDetailRequest);
        }
    }

    public Long getScheduledProjectId() {
        return scheduledProjectId;
    }

    public void setScheduledProjectId(Long scheduledProjectId) {
        this.scheduledProjectId = scheduledProjectId;
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
