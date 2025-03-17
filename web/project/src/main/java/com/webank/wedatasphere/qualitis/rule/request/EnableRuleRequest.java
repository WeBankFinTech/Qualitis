package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

import java.util.List;

public class EnableRuleRequest {

    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("rule_enable")
    private Boolean ruleEnable;
    @JsonProperty("rule_ids")
    private List<Long> ruleIds;

    public static void checkRequest(EnableRuleRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request,"request");
        CommonChecker.checkObject(request.getProjectId(), "project_id");
        CommonChecker.checkObject(request.getRuleEnable(), "rule_enable");
        CommonChecker.checkListMinSize(request.getRuleIds(), 1, "rule_ids");
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Boolean getRuleEnable() {
        return ruleEnable;
    }

    public void setRuleEnable(Boolean ruleEnable) {
        this.ruleEnable = ruleEnable;
    }

    public List<Long> getRuleIds() {
        return ruleIds;
    }

    public void setRuleIds(List<Long> ruleIds) {
        this.ruleIds = ruleIds;
    }

    @Override
    public String toString() {
        return "EnableRuleRequest{" +
                "projectId=" + projectId +
                ", ruleEnable=" + ruleEnable +
                ", ruleIds=" + ruleIds +
                '}';
    }
}
