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
import com.webank.wedatasphere.qualitis.project.entity.Project;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v_wblwyan
 * @date 2018-11-1
 */
public class RuleQueryProject {

  @JsonProperty("project_id")
  private Long projectId;

  @JsonProperty("project_name")
  private String projectName;

  @JsonProperty("rule_size")
  private int ruleSize;

  @JsonProperty("create_user_full_name")
  private String createUserFullName;

  @JsonIgnore
  private Map<Long, RuleQueryRule> dataMap = new HashMap<>();

  private List<RuleQueryRule> rules;

  public RuleQueryProject() {
  }

  public RuleQueryProject(RuleQueryRule data, Project project) {
    this.createUserFullName = project.getCreateUser();
    this.projectId = project.getId();
    this.projectName = project.getName();
    rules = new ArrayList<>();
    rules.add(data);
    dataMap.put(data.getRuleId(), data);
  }

  public Long getProjectId() {
    return projectId;
  }

  public void setProjectId(Long projectId) {
    this.projectId = projectId;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public int getRuleSize() {
    if (rules == null || rules.isEmpty()) {
      return 0;
    }
    return rules.size();
  }

  public void setRuleSize(int ruleSize) {
    this.ruleSize = ruleSize;
  }

  public String getCreateUserFullName() {
    return createUserFullName;
  }

  public void setCreateUserFullName(String createUserFullName) {
    this.createUserFullName = createUserFullName;
  }

  public List<RuleQueryRule> getRules() {
    return rules;
  }

  public void setRules(List<RuleQueryRule> rules) {
    this.rules = rules;
  }

  public Map<Long, RuleQueryRule> getDataMap() {
    return dataMap;
  }

  public void setDataMap(Map<Long, RuleQueryRule> dataMap) {
    this.dataMap = dataMap;
  }

  public void addRuleQueryRule(RuleQueryRule ruleSrc) {
    rules.add(ruleSrc);
    dataMap.put(ruleSrc.getRuleId(), ruleSrc);
  }
}
