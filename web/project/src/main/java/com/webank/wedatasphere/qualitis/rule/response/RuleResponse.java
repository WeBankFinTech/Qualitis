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
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author howeye
 */
public class RuleResponse {

    @JsonProperty("rule_id")
    private Long ruleId;
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("cn_name")
    private String ruleCnName;
    @JsonProperty("rule_detail")
    private String ruleDetail;
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
    @JsonProperty("table_type")
    private String tableType;
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("project_name")
    private String projectName;

    @JsonProperty("abort_on_failure")
    private Boolean abortOnFailure;
    @JsonProperty("delete_fail_check_result")
    private Boolean deleteFailCheckResult;

    @JsonProperty("add_rule_metric_names")
    private String addRuleMetricNames;

    public RuleResponse() {
    }

    public RuleResponse(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public RuleResponse(Rule rule) {
        this.ruleId = rule.getId();
        this.ruleName = rule.getName();
        this.ruleDetail = rule.getDetail();
        this.ruleCnName = rule.getCnName();
        this.ruleTemplateName = rule.getTemplate().getName();
        this.ruleTemplateId = rule.getTemplate().getId();
        this.ruleGroupId = rule.getRuleGroup().getId();
        this.ruleGroupName = rule.getRuleGroup().getRuleGroupName();
        this.projectId = rule.getProject().getId();
        this.projectName = rule.getProject().getName();
        this.ruleType = rule.getRuleType();
        if (ruleType.equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
            if (StringUtils.isEmpty(rule.getFromContent())){
                // Just combine 2-2 code for sql check.
                this.tableType = RuleTypeEnum.CUSTOM_RULE.getCode().toString();
            }
        }
        clusterName = new ArrayList<>();
        tableName = new ArrayList<>();
        this.abortOnFailure = rule.getAbortOnFailure();
        if (CollectionUtils.isNotEmpty(rule.getRuleDataSources())) {
            for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
                if (StringUtils.isBlank(ruleDataSource.getTableName())) {
                    continue;
                }
                clusterName.add(ruleDataSource.getClusterName());
                tableName.add(ruleDataSource.getDbName() + "." + ruleDataSource.getTableName());
            }
        }

        this.deleteFailCheckResult = rule.getDeleteFailCheckResult();
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

    public String getRuleCnName() {
        return ruleCnName;
    }

    public void setRuleCnName(String ruleCnName) {
        this.ruleCnName = ruleCnName;
    }

    public String getRuleDetail() {
        return ruleDetail;
    }

    public void setRuleDetail(String ruleDetail) {
        this.ruleDetail = ruleDetail;
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

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public Boolean getAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(Boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
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

    public Boolean getDeleteFailCheckResult() {
        return deleteFailCheckResult;
    }

    public void setDeleteFailCheckResult(Boolean deleteFailCheckResult) {
        this.deleteFailCheckResult = deleteFailCheckResult;
    }

    public String getAddRuleMetricNames() {
        return addRuleMetricNames;
    }

    public void setAddRuleMetricNames(String addRuleMetricNames) {
        this.addRuleMetricNames = addRuleMetricNames;
    }

    @Override
    public String toString() {
        return "RuleResponse{" +
            "ruleId=" + ruleId +
            ", ruleName='" + ruleName + '\'' +
            ", ruleTemplateName='" + ruleTemplateName + '\'' +
            ", ruleTemplateId=" + ruleTemplateId +
            ", ruleGroupId=" + ruleGroupId +
            ", ruleGroupName='" + ruleGroupName + '\'' +
            ", projectId=" + projectId +
            ", abortOnFailure=" + abortOnFailure +
            '}';
    }
}
