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
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;

import java.util.*;

/**
 * @author howeye
 */
public class RuleDetailResponse {

    @JsonProperty("rule_id")
    private Long ruleId;
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("rule_template_id")
    private Long ruleTemplateId;
    @JsonProperty("rule_template_name")
    private String ruleTemplateName;
    @JsonProperty("template_variable")
    private List<RuleVariableResponse> templateVariable;
    @JsonProperty("alarm")
    private Boolean alarm;
    @JsonProperty("alarm_variable")
    private List<AlarmConfigResponse> alarmVariable;
    private List<DataSourceResponse> datasource;

    @JsonProperty("rule_group_id")
    private Long ruleGroupId;

    public RuleDetailResponse() {
    }

    public RuleDetailResponse(Rule rule) {
        this.ruleId = rule.getId();
        this.ruleName = rule.getName();
        this.ruleTemplateId = rule.getTemplate().getId();
        this.ruleTemplateName = rule.getRuleTemplateName();
        this.ruleGroupId = rule.getRuleGroup().getId();
        // Set TemplateVariable
        this.templateVariable = new ArrayList<>();
        for (RuleVariable ruleVariable : rule.getRuleVariables()) {
            this.templateVariable.add(new RuleVariableResponse(ruleVariable));
        }
        this.alarm = rule.getAlarm();
        // Set alarmVariable
        this.alarmVariable = new ArrayList<>();
        for (AlarmConfig alarmConfig : rule.getAlarmConfigs()) {
            this.alarmVariable.add(new AlarmConfigResponse(alarmConfig));
        }
        // Set datasource
        this.datasource = new ArrayList<>();
        addDataSource(rule);
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

    public Long getRuleTemplateId() {
        return ruleTemplateId;
    }

    public void setRuleTemplateId(Long ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
    }


    public Boolean getAlarm() {
        return alarm;
    }

    public void setAlarm(Boolean alarm) {
        this.alarm = alarm;
    }

    public String getRuleTemplateName() {
        return ruleTemplateName;
    }

    public void setRuleTemplateName(String ruleTemplateName) {
        this.ruleTemplateName = ruleTemplateName;
    }

    public void setTemplateVariable(List<RuleVariableResponse> templateVariable) {
        this.templateVariable = templateVariable;
    }

    public void setAlarmVariable(List<AlarmConfigResponse> alarmVariable) {
        this.alarmVariable = alarmVariable;
    }

    public List<DataSourceResponse> getDatasource() {
        return datasource;
    }

    public void setDatasource(List<DataSourceResponse> datasource) {
        this.datasource = datasource;
    }

    public List<RuleVariableResponse> getTemplateVariable() {
        return templateVariable;
    }

    public List<AlarmConfigResponse> getAlarmVariable() {
        return alarmVariable;
    }

    private void addDataSource(Rule rule) {
        for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
            datasource.add(new DataSourceResponse(ruleDataSource));
        }
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }
}
