package com.webank.wedatasphere.qualitis.project.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_gaojiedeng@webank.com
 */
public class ProjectTypeRequest {
    @JsonProperty("project_type")
    private Integer ProjectType;

    public Integer getProjectType() {
        return ProjectType;
    }

    public void setProjectType(Integer projectType) {
        ProjectType = projectType;
    }
}
