package com.webank.wedatasphere.qualitis.scheduled.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-15 16:31
 * @description
 */
public class RuleGroupRequest {

    @JsonProperty("rule_group_id")
    private Long ruleGroupId;
    @JsonProperty("rule_group_name")
    private String ruleGroupName;

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public void setRuleGroupName(String ruleGroupName) {
        this.ruleGroupName = ruleGroupName;
    }
}
