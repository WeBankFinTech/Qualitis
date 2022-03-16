package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author allenzhou@webank.com
 * @date 2021/2/24 16:30
 */
public class RuleMetricRulesRequest {
  @JsonProperty("rule_metric_id")
  private Long ruleMetricId;
  @JsonProperty("page")
  private Integer page;
  @JsonProperty("size")
  private Integer size;

  public RuleMetricRulesRequest() {
  }

  public Long getRuleMetricId() {
    return ruleMetricId;
  }

  public void setRuleMetricId(Long ruleMetricId) {
    this.ruleMetricId = ruleMetricId;
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
