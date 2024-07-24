package com.webank.wedatasphere.qualitis.scheduled.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author v_gaojiedeng@webank.com
 */
public class DeleteScheduledRequest {

    @JsonProperty("scheduled_task_front_back_id")
    private Long scheduledTaskFrontBackId;
    @JsonProperty("release_user")
    private String releaseUser;

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

    public static void checkRequest(DeleteScheduledRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkObject(request.getScheduledTaskFrontBackId(), "scheduled_task_front_back_id");

    }

}
