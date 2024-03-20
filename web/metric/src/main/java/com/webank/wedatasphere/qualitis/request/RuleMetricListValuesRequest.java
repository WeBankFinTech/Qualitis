package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2021/2/24 16:30
 */
public class RuleMetricListValuesRequest {
  @JsonProperty("rule_metric_id_list")
  private List<Long> ruleMetricIdList;
  @JsonProperty("start_time")
  private String startTime;
  @JsonProperty("end_time")
  private String endTime;

  public RuleMetricListValuesRequest() {
    // Default Constructor
  }

  public List<Long> getRuleMetricIdList() {
    return ruleMetricIdList;
  }

  public void setRuleMetricIdList(List<Long> ruleMetricIdList) {
    this.ruleMetricIdList = ruleMetricIdList;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }
}
