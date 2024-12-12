package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.entity.RuleMetric;
import com.webank.wedatasphere.qualitis.entity.RuleMetricTypeConfig;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author allenzhou@webank.com
 * @date 2021/2/24 16:30
 */
public class RuleMetricConditionResponse {

  @JsonProperty("sub_system_name_condition")
  private Set<String> subSystemNameCondition;

  @JsonProperty("rule_metric_type")
  private Set<RuleMetricTypeConfig> ruleMetricTypeConfigs;

  @JsonProperty("en_code")
  private Set<String> enCode;

  public Set<String> getSubSystemNameCondition() {
    return subSystemNameCondition;
  }

  public void setSubSystemNameCondition(Set<String> subSystemNameCondition) {
    this.subSystemNameCondition = subSystemNameCondition;
  }

  public Set<RuleMetricTypeConfig> getRuleMetricType() {
    return ruleMetricTypeConfigs;
  }

  public void setRuleMetricType(Set<RuleMetricTypeConfig> ruleMetricTypeConfigs) {
    this.ruleMetricTypeConfigs = ruleMetricTypeConfigs;
  }

  public Set<String> getEnCode() {
    return enCode;
  }

  public void setEnCode(Set<String> enCode) {
    this.enCode = enCode;
  }
}
