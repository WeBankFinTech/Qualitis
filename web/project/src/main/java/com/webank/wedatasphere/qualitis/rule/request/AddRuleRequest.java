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
public class AddRuleRequest {
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("rule_template_id")
    private Long ruleTemplateId;
    @JsonProperty("alarm")
    private Boolean alarm;
    @JsonProperty("alarm_variable")
    private List<AlarmConfigRequest> alarmVariable;
    private List<DataSourceRequest> datasource;
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("template_arguments")
    private List<TemplateArgumentRequest> templateArgumentRequests;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;

    @JsonProperty("abort_on_failure")
    private Boolean abortOnFailure;

    public AddRuleRequest() {
        // Default Constructor
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

    public List<AlarmConfigRequest> getAlarmVariable() {
        return alarmVariable;
    }

    public void setAlarmVariable(List<AlarmConfigRequest> alarmVariable) {
        this.alarmVariable = alarmVariable;
    }

    public Boolean getAlarm() {
        return alarm;
    }

    public void setAlarm(Boolean alarm) {
        this.alarm = alarm;
    }

    public List<DataSourceRequest> getDatasource() {
        return datasource;
    }

    public void setDatasource(List<DataSourceRequest> datasource) {
        this.datasource = datasource;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Boolean getAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(Boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
    }

    public static void checkRequest(AddRuleRequest request, boolean modifyOrNot) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkString(request.getRuleName(), "rule_name");
        CommonChecker.checkObject(request.getTemplateArgumentRequests(), "template_arguments");

        CommonChecker.checkObject(request.getAlarm(), "alarm");
        CommonChecker.checkObject(request.getAbortOnFailure(), "abort_on_failure");
        if (request.getAlarm()) {
            CommonChecker.checkObject(request.getAlarmVariable(), "alarm_variable");
            if (request.getAlarmVariable().isEmpty()) {
                throw new UnExpectedRequestException("alarm_variable can not be empty");
            }
        }

        CommonChecker.checkObject(request.getDatasource(), "datasource");
        for (DataSourceRequest dataSourceRequest : request.getDatasource()) {
            CommonChecker.checkObject(dataSourceRequest.getFilter(), "filter");
        }

        if (!modifyOrNot) {
            CommonChecker.checkObject(request.getProjectId(), "project_id");
        }

        CommonChecker.checkStringLength(request.getRuleName(), 50, "rule name");
    }

    public List<TemplateArgumentRequest> getTemplateArgumentRequests() {
        return templateArgumentRequests;
    }

    public void setTemplateArgumentRequests(List<TemplateArgumentRequest> templateArgumentRequests) {
        this.templateArgumentRequests = templateArgumentRequests;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    @Override
    public String toString() {
        return "AddRuleRequest{" +
            "ruleName='" + ruleName + '\'' +
            ", ruleTemplateId=" + ruleTemplateId +
            ", alarm=" + alarm +
            ", alarmVariable=" + alarmVariable +
            ", datasource=" + datasource +
            ", projectId=" + projectId +
            ", templateArgumentRequests=" + templateArgumentRequests +
            ", ruleGroupId=" + ruleGroupId +
            '}';
    }
}