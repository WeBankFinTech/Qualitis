/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.query.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v_wblwyan
 * @date 2018-11-1
 */
public class RuleQueryRule {

  @JsonProperty("rule_id")
  private Long ruleId;

  @JsonProperty("rule_name")
  private String ruleName;

  @JsonProperty("rule_template")
  private String ruleTemplate;

  @JsonProperty("rule_template_id")
  private Long ruleTemplateId;

  @JsonProperty("rule_type")
  private Integer ruleType;

  @JsonProperty("rule_group_id")
  private Long ruleGroupId;

  @JsonProperty("rule_group_name")
  private String ruleGroupName;

  private List<RuleQueryCluster> clusters;

  @JsonIgnore
  private Map<String, RuleQueryCluster> dataMap = new HashMap<>();

  public RuleQueryRule() {
  }

  public RuleQueryRule(RuleQueryCluster data, Rule rule) {
    BeanUtils.copyProperties(data, this);
    this.setRuleId(rule.getId());
    this.setRuleName(rule.getName());
    this.setRuleTemplate(rule.getTemplate().getName());
    this.setRuleTemplateId(rule.getTemplate().getId());
    this.setRuleType(rule.getRuleType());
    this.ruleGroupId = rule.getRuleGroup().getId();
    this.ruleGroupName = rule.getRuleGroup().getRuleGroupName();
    clusters = new ArrayList<>();
    clusters.add(data);
    dataMap.put(data.getClusterName(), data);
  }

  public Long getRuleId() {
    return ruleId;
  }

  public void setRuleId(Long ruleId) {
    this.ruleId = ruleId;
  }

  public String getRuleName() {
    return ruleName;
  }

  public void setRuleName(String ruleName) {
    this.ruleName = ruleName;
  }

  public String getRuleTemplate() {
    return ruleTemplate;
  }

  public void setRuleTemplate(String ruleTemplate) {
    this.ruleTemplate = ruleTemplate;
  }

  public List<RuleQueryCluster> getClusters() {
    return clusters;
  }

  public void setClusters(List<RuleQueryCluster> clusters) {
    this.clusters = clusters;
  }

  public Map<String, RuleQueryCluster> getDataMap() {
    return dataMap;
  }

  public void setDataMap(Map<String, RuleQueryCluster> dataMap) {
    this.dataMap = dataMap;
  }

  public Long getRuleTemplateId() {
    return ruleTemplateId;
  }

  public void setRuleTemplateId(Long ruleTemplateId) {
    this.ruleTemplateId = ruleTemplateId;
  }

  public Integer getRuleType() {
    return ruleType;
  }

  public void setRuleType(Integer ruleType) {
    this.ruleType = ruleType;
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

  public void addRuleQueryCluster(RuleQueryCluster data) {
    clusters.add(data);
    dataMap.put(data.getClusterName(), data);
  }
}
