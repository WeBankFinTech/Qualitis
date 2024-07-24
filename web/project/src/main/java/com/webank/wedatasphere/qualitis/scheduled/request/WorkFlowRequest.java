package com.webank.wedatasphere.qualitis.scheduled.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author v_gaojiedeng@webank.com
 */
public class WorkFlowRequest {

    @JsonProperty("release_user")
    private String releaseUser;

    @JsonProperty("project_name")
    private String projectName;

    @JsonProperty("cluster")
    private String cluster;

    public String getReleaseUser() {
        return releaseUser;
    }

    public void setReleaseUser(String releaseUser) {
        this.releaseUser = releaseUser;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public static void checkRequest(WorkFlowRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkString(request.getProjectName(),"project_name");
        CommonChecker.checkString(request.getCluster(),"cluster");

    }


}
