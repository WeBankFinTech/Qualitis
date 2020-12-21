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

import java.util.List;

/**
 * @author howeye
 */
public class AddCustomRuleRequest {

    @JsonProperty("project_id")
    private Long projectId;
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

    @JsonProperty("rule_group_id")
    private Long ruleGroupId;

    @JsonProperty("abort_on_failure")
    private Boolean abortOnFailure;


    public AddCustomRuleRequest() {

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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
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

    public static void checkRequest(AddCustomRuleRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        CommonChecker.checkObject(request.getProjectId(), "Project id");
        AddCustomRuleRequest.checkCustomRuleRequest(request);
    }

    public static void checkCustomRuleRequest(AddCustomRuleRequest request) throws UnExpectedRequestException {
        CommonChecker.checkString(request.getRuleName(), "Rule name");
        CommonChecker.checkString(request.getOutputName(), "Output name");
        CommonChecker.checkObject(request.getSaveMidTable(), "Save mid table");
        CommonChecker.checkFunctionTypeEnum(request.getFunctionType());
        CommonChecker.checkString(request.getFunctionContent(), "Function content");
        CommonChecker.checkString(request.getFromContent(), "From content");
        CommonChecker.checkString(request.getWhereContent(), "Where content");
        CommonChecker.checkString(request.getClusterName(), "Cluster name");
        CommonChecker.checkObject(request.getAlarm(), "alarm");
        CommonChecker.checkObject(request.getAbortOnFailure(), "abort_on_failure");
        if (request.getAlarm()) {
            CommonChecker.checkObject(request.getAlarmVariable(), "alarm_variable");
            if (request.getAlarmVariable().size() == 0) {
                throw new UnExpectedRequestException("alarm_variable can not be empty");
            }

            for (CustomAlarmConfigRequest customAlarmConfigRequest : request.getAlarmVariable()) {
                CustomAlarmConfigRequest.checkRequest(customAlarmConfigRequest);
            }
        }

        CommonChecker.checkStringLength(request.getRuleName(), 50, "Rule name");
        CommonChecker.checkStringLength(request.getOutputName(), 50, "Output name");
        CommonChecker.checkStringLength(request.getFromContent(), 1000, "From content");
        CommonChecker.checkStringLength(request.getWhereContent(), 1000, "Where content");
        CommonChecker.checkStringLength(request.getFunctionContent(), 1000, "Function content");
    }
}
