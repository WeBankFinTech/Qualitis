package com.webank.wedatasphere.qualitis.rule.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RuleEnableResponse {

    @JsonProperty("rule_list")
    private List<Long> ruleList;

    public List<Long> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<Long> ruleList) {
        this.ruleList = ruleList;
    }
}
