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

package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author howeye
 */
public class ModifyCustomRuleRequest {

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
    @JsonProperty("alarm")
    private Boolean alarm;
    @JsonProperty("alarm_variable")
    private List<CustomAlarmConfigRequest> alarmVariable;
    @JsonProperty("cluster_name")
    private String clusterName;

    @JsonProperty("abort_on_failure")
    private Boolean abortOnFailure;


    public ModifyCustomRuleRequest() {

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

    public List<CustomAlarmConfigRequest> getAlarmVariable() {
        return alarmVariable;
    }

    public void setAlarmVariable(List<CustomAlarmConfigRequest> alarmVariable) {
        this.alarmVariable = alarmVariable;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Boolean getAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(Boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
    }

    public static void checkRequest(ModifyCustomRuleRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        CommonChecker.checkObject(request.getRuleId(), "Rule id");

        AddCustomRuleRequest addCustomRuleRequest = new AddCustomRuleRequest();
        BeanUtils.copyProperties(request, addCustomRuleRequest);
        AddCustomRuleRequest.checkCustomRuleRequest(addCustomRuleRequest);
    }

}
