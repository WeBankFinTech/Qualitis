package com.webank.wedatasphere.qualitis.scheduled.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author v_gaojiedeng@webank.com
 */
public class ScheduledFormRequest {

    @JsonProperty("release_user")
    private String releaseUser;

    @JsonProperty("project")
    private String project;

    @JsonProperty("flow")
    private String flow;

    @JsonProperty("cluster")
    private String cluster;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getFlow() {
        return flow;
    }

    public void setFlow(String flow) {
        this.flow = flow;
    }

    public String getReleaseUser() {
        return releaseUser;
    }

    public void setReleaseUser(String releaseUser) {
        this.releaseUser = releaseUser;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public static void checkRequest(ScheduledFormRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkString(request.getProject(), "project");
        CommonChecker.checkString(request.getFlow(), "flow");
        CommonChecker.checkString(request.getCluster(), "cluster");
    }


}
