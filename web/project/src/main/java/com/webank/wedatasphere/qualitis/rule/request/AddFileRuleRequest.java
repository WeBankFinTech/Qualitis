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
import javax.persistence.Column;

/**
 * @author allenzhou
 */
public class AddFileRuleRequest extends AbstractAddRequest {
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("cn_name")
    private String ruleCnName;
    @JsonProperty("rule_detail")
    private String ruleDetail;
    @JsonProperty("alarm")
    private Boolean alarm;
    @JsonProperty("alarm_variable")
    private List<FileAlarmConfigRequest> alarmVariable;
    private DataSourceRequest datasource;
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;
    @JsonProperty("cs_id")
    private String csId;
    @JsonProperty("abort_on_failure")
    private Boolean abortOnFailure;

    private List<String> ruleMetricNamesForBdpClient;

    public AddFileRuleRequest() {
        // Default Constructor
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

    public List<FileAlarmConfigRequest> getAlarmVariable() {
        return alarmVariable;
    }

    public void setAlarmVariable(List<FileAlarmConfigRequest> alarmVariable) {
        this.alarmVariable = alarmVariable;
    }

    public Boolean getAlarm() {
        return alarm;
    }

    public void setAlarm(Boolean alarm) {
        this.alarm = alarm;
    }

    public DataSourceRequest getDatasource() {
        return datasource;
    }

    public void setDatasource(DataSourceRequest datasource) {
        this.datasource = datasource;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getCsId() { return csId; }

    public void setCsId(String csId) { this.csId = csId; }

    public Boolean getAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(Boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public List<String> getRuleMetricNamesForBdpClient() {
        return ruleMetricNamesForBdpClient;
    }

    public void setRuleMetricNamesForBdpClient(List<String> ruleMetricNamesForBdpClient) {
        this.ruleMetricNamesForBdpClient = ruleMetricNamesForBdpClient;
    }

    public static void checkRequest(AddFileRuleRequest request, boolean modifyOrNot) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkString(request.getRuleName(), "rule_name");

        CommonChecker.checkObject(request.getAlarm(), "alarm");
        CommonChecker.checkObject(request.getAbortOnFailure(), "abort_on_failure");
        if (request.getAlarm()) {
            CommonChecker.checkObject(request.getAlarmVariable(), "alarm_variable");
            if (request.getAlarmVariable().isEmpty()) {
                throw new UnExpectedRequestException("alarm_variable can not be empty");
            }
        }

        CommonChecker.checkObject(request.getDatasource(), "datasource");

        if (!modifyOrNot) {
            CommonChecker.checkObject(request.getProjectId(), "project_id");
        }

        CommonChecker.checkStringLength(request.getRuleName(), 128, "rule name");
    }

    @Override
    public String toString() {
        return "AddRuleRequest{" +
            "ruleName='" + ruleName + '\'' +
            ", alarm=" + alarm +
            ", alarmVariable=" + alarmVariable +
            ", datasource=" + datasource +
            ", projectId=" + projectId +
            ", ruleGroupId=" + ruleGroupId +
            ", csId='" + csId + '\'' +
            '}';
    }
}