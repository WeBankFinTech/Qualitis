package com.webank.wedatasphere.qualitis.rule.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public class CopyRuleWithDatasourceResponse {
    @JsonProperty("project_id")
    private Long projectId;

    @JsonProperty("rule_group_id")
    private Long ruleGroupId;

    @JsonProperty("rule_ids")
    private List<Long> ruleIds;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public List<Long> getRuleIds() {
        return ruleIds;
    }

    public void setRuleIds(List<Long> ruleIds) {
        this.ruleIds = ruleIds;
    }

    public CopyRuleWithDatasourceResponse() {
    }

    public CopyRuleWithDatasourceResponse(Long projectId, Long ruleGroupId, List<Long> ruleIds) {
        this.projectId = projectId;
        this.ruleGroupId = ruleGroupId;
        this.ruleIds = ruleIds;
    }
}
