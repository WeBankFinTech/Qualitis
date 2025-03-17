package com.webank.wedatasphere.qualitis.scheduled.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author v_gaojiedeng@webank.com
 */
public class PublishUserRequest {

    @JsonProperty("cluster")
    private String cluster;

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public static void checkRequest(PublishUserRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkString(request.getCluster(), "cluster");

    }
}
