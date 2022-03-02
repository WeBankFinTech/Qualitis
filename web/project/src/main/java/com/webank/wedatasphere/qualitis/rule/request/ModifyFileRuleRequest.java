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
import org.springframework.beans.BeanUtils;

/**
 * @author allenzhou
 */
public class ModifyFileRuleRequest {

    @JsonProperty("rule_id")
    private Long ruleId;
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("rule_detail")
    private String ruleDetail;
    @JsonProperty("cn_name")
    private String ruleCnName;
    @JsonProperty("alarm")
    private Boolean alarm;
    @JsonProperty("alert")
    private Boolean alert;
    @JsonProperty("alert_level")
    private Integer alertLevel;
    @JsonProperty("alert_receiver")
    private String alertReceiver;
    @JsonProperty("alarm_variable")
    private List<FileAlarmConfigRequest> alarmVariable;
    private DataSourceRequest datasource;
    @JsonProperty("cs_id")
    private String csId;
    @JsonProperty("project_id")
    private long projectId;
    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("abort_on_failure")
    private Boolean abortOnFailure;

    public ModifyFileRuleRequest() {
        // Default Constructor
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

    public Boolean getAlarm() {
        return alarm;
    }

    public void setAlarm(Boolean alarm) {
        this.alarm = alarm;
    }

    public Boolean getAlert() {
        return alert;
    }

    public void setAlert(Boolean alert) {
        this.alert = alert;
    }

    public Integer getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(Integer alertLevel) {
        this.alertLevel = alertLevel;
    }

    public List<FileAlarmConfigRequest> getAlarmVariable() {
        return alarmVariable;
    }

    public void setAlarmVariable(List<FileAlarmConfigRequest> alarmVariable) {
        this.alarmVariable = alarmVariable;
    }

    public DataSourceRequest getDatasource() {
        return datasource;
    }

    public void setDatasource(DataSourceRequest datasource) {
        this.datasource = datasource;
    }

    public String getCsId() { return csId; }

    public void setCsId(String csId) { this.csId = csId; }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Boolean getAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(Boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
    }

    public String getAlertReceiver() {
        return alertReceiver;
    }

    public void setAlertReceiver(String alertReceiver) {
        this.alertReceiver = alertReceiver;
    }

    public static void checkRequest(ModifyFileRuleRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkObject(request.getRuleId(), "rule_id");
        AddFileRuleRequest addFileRuleRequest = new AddFileRuleRequest();
        BeanUtils.copyProperties(request, addFileRuleRequest);
        AddFileRuleRequest.checkRequest(addFileRuleRequest, true);
    }
}
