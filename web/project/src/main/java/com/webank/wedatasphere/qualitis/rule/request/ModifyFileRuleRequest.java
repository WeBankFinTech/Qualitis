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
 * @author allenzhou
 */
public class ModifyFileRuleRequest extends AbstractCommonRequest{

    @JsonProperty("project_name")
    private String projectName;
    private String fifter;
    @JsonProperty("alarm_variable")
    private List<FileAlarmConfigRequest> fileAlarmVariable;


    public ModifyFileRuleRequest() {
        // Default Constructor
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getFifter() {
        return fifter;
    }

    public void setFifter(String fifter) {
        this.fifter = fifter;
    }

    public List<FileAlarmConfigRequest> getFileAlarmVariable() {
        return fileAlarmVariable;
    }

    public void setFileAlarmVariable(List<FileAlarmConfigRequest> fileAlarmVariable) {
        this.fileAlarmVariable = fileAlarmVariable;
    }



    public static void checkRequest(ModifyFileRuleRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkObject(request.getRuleId(), "rule_id");
        AddFileRuleRequest addFileRuleRequest = new AddFileRuleRequest();
        BeanUtils.copyProperties(request, addFileRuleRequest);
        AddFileRuleRequest.checkRequest(addFileRuleRequest, true);
    }
}
