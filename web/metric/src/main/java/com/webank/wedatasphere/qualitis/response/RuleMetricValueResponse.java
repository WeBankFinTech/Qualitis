package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author allenzhou@webank.com
 * @date 2021/5/7 10:30
 */
public class RuleMetricValueResponse {
    @JsonProperty("generate_time")
    private String generateTime;
    @JsonProperty("rule_metric_value")
    private String ruleMetricValue;
    @JsonProperty("related_rule_name")
    private String relatedRuleName;

    public RuleMetricValueResponse() {
    }

    public String getGenerateTime() {
        return generateTime;
    }

    public void setGenerateTime(String generateTime) {
        this.generateTime = generateTime;
    }

    public String getRuleMetricValue() {
        return ruleMetricValue;
    }

    public void setRuleMetricValue(String ruleMetricValue) {
        this.ruleMetricValue = ruleMetricValue;
    }

    public String getRelatedRuleName() {
        return relatedRuleName;
    }

    public void setRelatedRuleName(String relatedRuleName) {
        this.relatedRuleName = relatedRuleName;
    }
}
