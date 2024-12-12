package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.request.PageRequest;

/**
 * @author v_minminghe@webank.com
 * @date 2022-05-27 15:58
 * @description
 */
public class RuleGroupPageRequest extends PageRequest {

    @JsonProperty("project_id")
    private Long projectId;

    public void checkRequest() throws UnExpectedRequestException {
        CommonChecker.checkObject(this.getProjectId(), "project_id");
        CommonChecker.checkObject(this.getPage(), "page");
        CommonChecker.checkObject(this.getSize(), "size");
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
