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
public class ModifyRuleRequest extends AbstractCommonRequest{

    @JsonProperty("template_arguments")
    private List<TemplateArgumentRequest> templateArgumentRequests;
    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("standard_value_version_id")
    private Long standardValueVersionId;
    private String fifter;
    @JsonProperty("alarm_variable")
    private List<AlarmConfigRequest> alarmVariable;


    public ModifyRuleRequest() {
        // Default Constructor
    }

    public List<TemplateArgumentRequest> getTemplateArgumentRequests() {
        return templateArgumentRequests;
    }

    public void setTemplateArgumentRequests(List<TemplateArgumentRequest> templateArgumentRequests) {
        this.templateArgumentRequests = templateArgumentRequests;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getStandardValueVersionId() {
        return standardValueVersionId;
    }

    public void setStandardValueVersionId(Long standardValueVersionId) {
        this.standardValueVersionId = standardValueVersionId;
    }

    public String getFifter() {
        return fifter;
    }

    public void setFifter(String fifter) {
        this.fifter = fifter;
    }

    public List<AlarmConfigRequest> getAlarmVariable() {
        return alarmVariable;
    }

    public void setAlarmVariable(List<AlarmConfigRequest> alarmVariable) {
        this.alarmVariable = alarmVariable;
    }

    public static void checkRequest(ModifyRuleRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkObject(request.getRuleId(), "rule_id");
        AddRuleRequest addRuleRequest = new AddRuleRequest();
        BeanUtils.copyProperties(request, addRuleRequest);
        AddRuleRequest.checkRequest(addRuleRequest, true);
    }

    @Override
    public String toString() {
        return "ModifyRuleRequest{" +
                "templateArgumentRequests=" + templateArgumentRequests +
                ", projectName='" + projectName + '\'' +
                ", standardValueVersionId=" + standardValueVersionId +
                ", fifter='" + fifter + '\'' +
                ", alarmVariable=" + alarmVariable +
                "} " + super.toString();
    }
}
