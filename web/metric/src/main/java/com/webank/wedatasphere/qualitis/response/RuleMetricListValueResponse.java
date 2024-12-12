package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2021/5/7 10:30
 */
public class RuleMetricListValueResponse {
    @JsonProperty("rule_metric_id")
    private Long ruleMetricId;
    @JsonProperty("rule_metric_values")
    private List<RuleMetricValueResponse> ruleMetricValues;
    @JsonProperty("env_names")
    private List<String> envNames;

    public List<String> getEnvNames() {
        return envNames;
    }

    public void setEnvNames(List<String> envNames) {
        this.envNames = envNames;
    }

    public RuleMetricListValueResponse() {
        this.ruleMetricValues = new ArrayList<>();
    }

    public Long getRuleMetricId() {
        return ruleMetricId;
    }

    public void setRuleMetricId(Long ruleMetricId) {
        this.ruleMetricId = ruleMetricId;
    }

    public List<RuleMetricValueResponse> getRuleMetricValues() {
        return ruleMetricValues;
    }

    public void setRuleMetricValues(List<RuleMetricValueResponse> ruleMetricValues) {
        this.ruleMetricValues = ruleMetricValues;
    }
}
