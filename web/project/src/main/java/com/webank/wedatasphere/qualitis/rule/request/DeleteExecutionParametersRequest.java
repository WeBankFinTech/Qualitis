package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author v_gaojiedeng
 */
public class DeleteExecutionParametersRequest {

    @JsonProperty("execution_parameters_id")
    private Long executionParametersId;
    @JsonProperty("project_id")
    private Long projectId;

    public DeleteExecutionParametersRequest() {
//        do something
    }

    public Long getExecutionParametersId() {
        return executionParametersId;
    }

    public void setExecutionParametersId(Long executionParametersId) {
        this.executionParametersId = executionParametersId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public static void checkRequest(DeleteExecutionParametersRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        CommonChecker.checkObject(request.getExecutionParametersId(), "execution_parameters_id");
        CommonChecker.checkObject(request.getProjectId(), "project_id");
    }
}
