package com.webank.wedatasphere.qualitis.checkalert.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_gaojiedeng@webank.com
 */
public class QueryWorkFlowRequest {

    @JsonProperty("project_id")
    private Long projectId;

    private Integer type;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
