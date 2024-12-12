package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.project.response.HiveRuleDetail;

import java.util.List;

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
    @JsonProperty("datasource_names")
    private List<String> datasourceNames;
    @JsonProperty("env_name")
    private String envName;
    @JsonProperty("rule")
    private HiveRuleDetail hiveRuleDetail;

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public List<String> getDatasourceNames() {
        return datasourceNames;
    }

    public void setDatasourceNames(List<String> datasourceNames) {
        this.datasourceNames = datasourceNames;
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

    public HiveRuleDetail getHiveRuleDetail() {
        return hiveRuleDetail;
    }

    public void setHiveRuleDetail(HiveRuleDetail hiveRuleDetail) {
        this.hiveRuleDetail = hiveRuleDetail;
    }
}
