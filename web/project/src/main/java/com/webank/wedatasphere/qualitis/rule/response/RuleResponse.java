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

package com.webank.wedatasphere.qualitis.rule.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
public class RuleResponse {

    @JsonProperty("rule_id")
    private Long ruleId;
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("cluster_name")
    private List<String> clusterName;
    @JsonProperty("table_name")
    private List<String> tableName;
    @JsonProperty("rule_template_name")
    private String ruleTemplateName;
    @JsonProperty("rule_template_id")
    private Long ruleTemplateId;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;
    @JsonProperty("rule_group_name")
    private String ruleGroupName;
    @JsonProperty("rule_type")
    private Integer ruleType;

    @JsonProperty("abort_on_failure")
    private Boolean abortOnFailure;

    @JsonProperty("project_id")
    private String newProjectId;

    public RuleResponse() {
    }

    public RuleResponse(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public RuleResponse(Rule rule) {
        this.ruleId = rule.getId();
        this.ruleName = rule.getName();
        this.ruleTemplateName = rule.getTemplate().getName();
        this.ruleTemplateId = rule.getTemplate().getId();
        this.ruleGroupId = rule.getRuleGroup().getId();
        this.ruleGroupName = rule.getRuleGroup().getRuleGroupName();
        this.ruleType = rule.getRuleType();
        clusterName = new ArrayList<>();
        tableName = new ArrayList<>();
        this.abortOnFailure = rule.getAbortOnFailure();
        for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
            clusterName.add(ruleDataSource.getClusterName());
            tableName.add(ruleDataSource.getDbName() + "." + ruleDataSource.getTableName());
        }
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

    public List<String> getClusterName() {
        return clusterName;
    }

    public void setClusterName(List<String> clusterName) {
        this.clusterName = clusterName;
    }

    public List<String> getTableName() {
        return tableName;
    }

    public void setTableName(List<String> tableName) {
        this.tableName = tableName;
    }

    public String getRuleTemplateName() {
        return ruleTemplateName;
    }

    public void setRuleTemplateName(String ruleTemplateName) {
        this.ruleTemplateName = ruleTemplateName;
    }

    public Long getRuleTemplateId() {
        return ruleTemplateId;
    }

    public void setRuleTemplateId(Long ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
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

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public Boolean getAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(Boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
    }

    public String getNewProjectId() {
        return newProjectId;
    }

    public void setNewProjectId(String newProjectId) {
        this.newProjectId = newProjectId;
    }
}
