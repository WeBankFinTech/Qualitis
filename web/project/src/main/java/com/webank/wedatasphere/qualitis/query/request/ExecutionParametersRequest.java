package com.webank.wedatasphere.qualitis.query.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author v_gaojiedeng
 */
public class ExecutionParametersRequest {

    private String name;
    @JsonProperty("project_id")
    private Long projectId;

    private int page;
    private int size;

    public ExecutionParametersRequest() {
        this.page = 0;
        this.size = 15;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public static void checkRequest(ExecutionParametersRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        CommonChecker.checkObject(request.getProjectId(), "project_id");
    }

}
