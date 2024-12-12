package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author allenzhou@webank.com
 * @date 2021/2/24 16:30
 */
public class RuleMetricValuesRequest {
  @JsonProperty("rule_metric_id")
  private Long ruleMetricId;
  @JsonProperty("start_time")
  private String startTime;
  @JsonProperty("end_time")
  private String endTime;
  @JsonProperty("env_name")
  private String envName;
  @JsonProperty("page")
  private Integer page;
  @JsonProperty("size")
  private Integer size;

  public RuleMetricValuesRequest() {
    // Default Constructor
  }

  public Long getRuleMetricId() {
    return ruleMetricId;
  }

  public void setRuleMetricId(Long ruleMetricId) {
    this.ruleMetricId = ruleMetricId;
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

  public String getEnvName() {
    return envName;
  }

  public void setEnvName(String envName) {
    this.envName = envName;
  }

  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }
}
