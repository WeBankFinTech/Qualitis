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

package com.webank.wedatasphere.qualitis.project.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;

import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author howeye
 */
public class HiveRuleDetail {

    @JsonProperty("rule_id")
    private Long ruleId;
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("rule_type")
    private Integer ruleType;
    private List<String> filter;
    @JsonProperty("rule_template_id")
    private Long ruleTemplateId;
    @JsonProperty("template_name")
    private String templateName;
    @JsonProperty("datasource")
    private List<HiveDataSourceDetail> hiveDataSource;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;
    @JsonProperty("rule_group_name")
    private String ruleGroupName;

    public HiveRuleDetail() {
    }

    public HiveRuleDetail(Rule rule) {
        this.ruleId = rule.getId();
        this.ruleName = rule.getName();
        this.ruleGroupId = rule.getRuleGroup().getId();
        this.ruleGroupName = rule.getRuleGroup().getRuleGroupName();
        if (rule.getRuleType().equals(RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode())) {
            this.filter = rule.getRuleDataSources().stream().map(RuleDataSource::getFilter).collect(Collectors.toList());
        } else if (rule.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
            this.filter = Collections.singletonList(rule.getWhereContent());
        } else if (rule.getRuleType().equals(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode())) {
          rule.getRuleVariables()
              .stream()
              .filter(item -> TemplateInputTypeEnum.CONDITION.getCode().equals(item.getTemplateMidTableInputMeta().getInputType()))
              .findAny()
              .ifPresent(variable -> this.filter = Collections.singletonList(variable.getValue()));
        } else if (rule.getRuleType().equals(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode())) {
            this.filter = rule.getRuleDataSources().stream().map(RuleDataSource::getFilter).collect(Collectors.toList());
        }
        this.ruleTemplateId = rule.getTemplate().getId();
        this.templateName = rule.getTemplate().getName();
        this.ruleType = rule.getRuleType();
        this.hiveDataSource = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(rule.getRuleDataSources())) {
            for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
                String tableName = ruleDataSource.getTableName();
                if (StringUtils.isEmpty(tableName)) {
                    continue;
                }
                // If type equals to data source
                hiveDataSource.add(new HiveDataSourceDetail(ruleDataSource.getClusterName(), ruleDataSource.getDbName(), tableName));
            }
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

    public List<String> getFilter() {
        return filter;
    }

    public void setFilter(List<String> filter) {
        this.filter = filter;
    }

    public Long getRuleTemplateId() {
        return ruleTemplateId;
    }

    public void setRuleTemplateId(Long ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public List<HiveDataSourceDetail> getHiveDataSource() {
        return hiveDataSource;
    }

    public void setHiveDataSource(List<HiveDataSourceDetail> hiveDataSource) {
        this.hiveDataSource = hiveDataSource;
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

    @Override
    public String toString() {
        return "HiveRuleDetail{" +
                "ruleId=" + ruleId +
                ", ruleName='" + ruleName + '\'' +
                ", ruleType=" + ruleType +
                ", filter=" + filter +
                ", ruleTemplateId=" + ruleTemplateId +
                ", templateName='" + templateName + '\'' +
                ", hiveDataSource=" + hiveDataSource +
                '}';
    }
}
