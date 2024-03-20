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
 * @author allenzhou
 */
public class AddFileRuleRequest extends AbstractCommonRequest {
    @JsonProperty("alarm_variable")
    private List<FileAlarmConfigRequest> fileAlarmVariable;

    public AddFileRuleRequest() {
        // Default Constructor
    }

    public List<FileAlarmConfigRequest> getFileAlarmVariable() {
        return fileAlarmVariable;
    }

    public void setFileAlarmVariable(List<FileAlarmConfigRequest> fileAlarmVariable) {
        this.fileAlarmVariable = fileAlarmVariable;
    }

    public static void checkRequest(AddFileRuleRequest request, boolean modifyOrNot) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkString(request.getRuleName(), "rule_name");

        CommonChecker.checkObject(request.getAlarm(), "alarm");
        if (request.getAlarm()) {
            CommonChecker.checkObject(request.getFileAlarmVariable(), "alarm_variable");
            if (request.getFileAlarmVariable().isEmpty()) {
                throw new UnExpectedRequestException("alarm_variable can not be empty");
            }
        }

        CommonChecker.checkObject(request.getDatasource(), "datasource");

        if (!modifyOrNot) {
            CommonChecker.checkObject(request.getProjectId(), "project_id");
        }

        CommonChecker.checkStringLength(request.getRuleName(), 128, "rule name");
    }

}