package com.webank.wedatasphere.qualitis.scheduled.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflowTaskRelation;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-18 11:03
 * @description
 */
public class RuleGroupResponse {

    @JsonProperty("rule_group_id")
    private Long ruleGroupId;
    @JsonProperty("rule_group_name")
    private String ruleGroupName;

    public RuleGroupResponse() {
    }

    public RuleGroupResponse(ScheduledWorkflowTaskRelation workflowTaskRelation) {
        this.ruleGroupId = workflowTaskRelation.getRuleGroup().getId();
        this.ruleGroupName = workflowTaskRelation.getRuleGroup().getRuleGroupName();
    }

    public RuleGroupResponse(RuleGroup ruleGroup) {
        this.ruleGroupId = ruleGroup.getId();
        this.ruleGroupName = ruleGroup.getRuleGroupName();
    }

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
