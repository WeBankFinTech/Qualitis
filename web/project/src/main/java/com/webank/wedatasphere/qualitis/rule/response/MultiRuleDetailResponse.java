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
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceMapping;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinConfigRequest;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceMapping;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinConfigRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
public class MultiRuleDetailResponse {

    @JsonProperty("rule_id")
    private Long ruleId;
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("multi_source_rule_template_id")
    private Long multiSourceRuleTemplateId;
    @JsonProperty("rule_template_name")
    private String ruleTemplateName;
    private MultiDataSourceConfigRequest source;
    private MultiDataSourceConfigRequest target;
    private List<MultiDataSourceJoinConfigRequest> mappings;

    private String filter;

    @JsonProperty("alarm")
    private Boolean alarm;
    @JsonProperty("alarm_variable")
    private List<AlarmConfigResponse> alarmVariable;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;

    public MultiRuleDetailResponse() {
    }

    public MultiRuleDetailResponse(Rule rule) {
        this.ruleId = rule.getId();
        this.ruleName = rule.getName();
        this.clusterName = rule.getRuleDataSources().iterator().next().getClusterName();
        this.multiSourceRuleTemplateId = rule.getTemplate().getId();
        this.ruleTemplateName = rule.getRuleTemplateName();
        this.ruleGroupId = rule.getRuleGroup().getId();

        this.source = new MultiDataSourceConfigRequest(rule.getRuleDataSources(), 0);
        this.target = new MultiDataSourceConfigRequest(rule.getRuleDataSources(), 1);

        // 设置mapping
        this.mappings = new ArrayList<>();
        for (RuleDataSourceMapping ruleDataSourceMapping : rule.getRuleDataSourceMappings()) {
            this.mappings.add(new MultiDataSourceJoinConfigRequest(ruleDataSourceMapping));
        }

        // 设置filter
        List<RuleVariable> filterRuleVariable = rule.getRuleVariables().stream().filter(ruleVariable ->
                ruleVariable.getTemplateMidTableInputMeta().getInputType().equals(TemplateInputTypeEnum.CONDITION.getCode())).collect(Collectors.toList());
        if (filterRuleVariable != null && filterRuleVariable.size() != 0) {
            this.filter = filterRuleVariable.iterator().next().getValue();
        }

        this.alarm = rule.getAlarm();
        // 设置alarmVariable
        this.alarmVariable = new ArrayList<>();
        for (AlarmConfig alarmConfig : rule.getAlarmConfigs()) {
            this.alarmVariable.add(new AlarmConfigResponse(alarmConfig));
        }
        if (rule.getChildRule() != null) {
            for (AlarmConfig alarmConfig : rule.getChildRule().getAlarmConfigs()) {
                this.alarmVariable.add(new AlarmConfigResponse(alarmConfig));
            }
        }
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public Long getMultiSourceRuleTemplateId() {
        return multiSourceRuleTemplateId;
    }

    public void setMultiSourceRuleTemplateId(Long multiSourceRuleTemplateId) {
        this.multiSourceRuleTemplateId = multiSourceRuleTemplateId;
    }

    public MultiDataSourceConfigRequest getSource() {
        return source;
    }

    public void setSource(MultiDataSourceConfigRequest source) {
        this.source = source;
    }

    public MultiDataSourceConfigRequest getTarget() {
        return target;
    }

    public void setTarget(MultiDataSourceConfigRequest target) {
        this.target = target;
    }

    public List<MultiDataSourceJoinConfigRequest> getMappings() {
        return mappings;
    }

    public void setMappings(List<MultiDataSourceJoinConfigRequest> mappings) {
        this.mappings = mappings;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Boolean getAlarm() {
        return alarm;
    }

    public void setAlarm(Boolean alarm) {
        this.alarm = alarm;
    }

    public List<AlarmConfigResponse> getAlarmVariable() {
        return alarmVariable;
    }

    public void setAlarmVariable(List<AlarmConfigResponse> alarmVariable) {
        this.alarmVariable = alarmVariable;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleTemplateName() {
        return ruleTemplateName;
    }

    public void setRuleTemplateName(String ruleTemplateName) {
        this.ruleTemplateName = ruleTemplateName;
    }
}
