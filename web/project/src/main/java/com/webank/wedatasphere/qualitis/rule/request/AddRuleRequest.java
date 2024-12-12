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
public class AddRuleRequest extends AbstractCommonRequest {
    @JsonProperty("template_arguments")
    private List<TemplateArgumentRequest> templateArgumentRequests;

    @JsonProperty("alarm_variable")
    private List<AlarmConfigRequest> alarmVariable;

    public AddRuleRequest() {
        // Default Constructor
    }

    public List<TemplateArgumentRequest> getTemplateArgumentRequests() {
        return templateArgumentRequests;
    }

    public void setTemplateArgumentRequests(List<TemplateArgumentRequest> templateArgumentRequests) {
        this.templateArgumentRequests = templateArgumentRequests;
    }

    public List<AlarmConfigRequest> getAlarmVariable() {
        return alarmVariable;
    }

    public void setAlarmVariable(List<AlarmConfigRequest> alarmVariable) {
        this.alarmVariable = alarmVariable;
    }

    public static void checkRequest(AddRuleRequest request, boolean modifyOrNot) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkString(request.getRuleName(), "rule_name");
        CommonChecker.checkObject(request.getTemplateArgumentRequests(), "template_arguments");

        CommonChecker.checkObject(request.getAlarm(), "alarm");
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

        if (request.getWorkFlowName() == null) {
            request.setWorkFlowName("");
        }

        if (request.getWorkFlowVersion() == null) {
            request.setWorkFlowVersion("");
        }

        CommonChecker.checkStringLength(request.getRuleName(), 128, "rule name");
    }

    @Override
    public String toString() {
        String superString = super.toString();
        return "Super{" + superString + '}' +
            " AddRuleRequest{" +
            ", templateArgumentRequests=" + templateArgumentRequests +
            ", alarmVariable=" + alarmVariable +
            '}';
    }
}