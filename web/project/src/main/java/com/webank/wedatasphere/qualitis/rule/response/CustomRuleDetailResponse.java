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
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;

import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
public class CustomRuleDetailResponse {

    @JsonProperty("rule_id")
    private Long ruleId;
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("output_name")
    private String outputName;
    @JsonProperty("save_mid_table")
    private Boolean saveMidTable;
    @JsonProperty("function_type")
    private Integer functionType;
    @JsonProperty("function_content")
    private String functionContent;
    @JsonProperty("from_content")
    private String fromContent;
    @JsonProperty("where_content")
    private String whereContent;
    private Boolean alarm;
    @JsonProperty("alarm_variable")
    private List<AlarmConfigResponse> alarmVariable;
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;
    @JsonProperty("abort_on_failure")
    private Boolean abortOnFailure;

    public CustomRuleDetailResponse(Rule customRule) {
        this.ruleId = customRule.getId();
        this.ruleName = customRule.getName();
        this.outputName = customRule.getOutputName();
        this.saveMidTable = customRule.getTemplate().getSaveMidTable();
        this.functionType = customRule.getFunctionType();
        this.functionContent = customRule.getFunctionContent();
        this.fromContent = customRule.getFromContent();
        this.whereContent = customRule.getWhereContent();
        this.alarm = customRule.getAlarm();
        this.alarmVariable = new ArrayList<>();
        this.ruleGroupId = customRule.getRuleGroup().getId();
        this.abortOnFailure = customRule.getAbortOnFailure();
        for (AlarmConfig alarmConfig : customRule.getAlarmConfigs()) {
            this.alarmVariable.add(new AlarmConfigResponse(alarmConfig));
        }
        this.clusterName = customRule.getRuleDataSources().iterator().next().getClusterName();
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

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public Boolean getSaveMidTable() {
        return saveMidTable;
    }

    public void setSaveMidTable(Boolean saveMidTable) {
        this.saveMidTable = saveMidTable;
    }

    public Integer getFunctionType() {
        return functionType;
    }

    public void setFunctionType(Integer functionType) {
        this.functionType = functionType;
    }

    public String getFunctionContent() {
        return functionContent;
    }

    public void setFunctionContent(String functionContent) {
        this.functionContent = functionContent;
    }

    public String getFromContent() {
        return fromContent;
    }

    public void setFromContent(String fromContent) {
        this.fromContent = fromContent;
    }

    public String getWhereContent() {
        return whereContent;
    }

    public void setWhereContent(String whereContent) {
        this.whereContent = whereContent;
    }

    public Boolean getAlarm() {
        return alarm;
    }

    public void setAlarm(Boolean alarm) {
        this.alarm = alarm;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public List<AlarmConfigResponse> getAlarmVariable() {
        return alarmVariable;
    }

    public void setAlarmVariable(List<AlarmConfigResponse> alarmVariable) {
        this.alarmVariable = alarmVariable;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public Boolean getAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(Boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
    }

    @Override
    public String toString() {
        return "CustomRuleDetailResponse{" +
                "ruleName='" + ruleName + '\'' +
                ", outputName='" + outputName + '\'' +
                ", saveMidTable=" + saveMidTable +
                ", functionType=" + functionType +
                ", functionContent='" + functionContent + '\'' +
                ", fromContent='" + fromContent + '\'' +
                ", whereContent='" + whereContent + '\'' +
                ", alarm=" + alarm +
                ", alarmVariable=" + alarmVariable +
                ", clusterName='" + clusterName + '\'' +
                '}';
    }
}
