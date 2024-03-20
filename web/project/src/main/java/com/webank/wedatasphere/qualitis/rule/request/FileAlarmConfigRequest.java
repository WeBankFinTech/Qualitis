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
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;

/**
 * @author allenzhou
 */
public class FileAlarmConfigRequest extends AbstractCommonAlarmConfigRequest{
    @JsonProperty("file_output_unit")
    private Integer fileOutputUnit;

    @JsonProperty("output_meta_id")
    private Long outputMetaId;

    public FileAlarmConfigRequest() {
        // Default Constructor
    }

    public Integer getFileOutputUnit() {
        return fileOutputUnit;
    }

    public void setFileOutputUnit(Integer fileOutputUnit) {
        this.fileOutputUnit = fileOutputUnit;
    }

    public Long getOutputMetaId() {
        return outputMetaId;
    }

    public void setOutputMetaId(Long outputMetaId) {
        this.outputMetaId = outputMetaId;
    }

    public static void checkRequest(FileAlarmConfigRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        CommonChecker.checkCheckTemplate(request.getCheckTemplate());
        if (request.getCheckTemplate().equals(CheckTemplateEnum.FIXED_VALUE.getCode())) {
            CommonChecker.checkCompareType(request.getCompareType());
        }
        CommonChecker.checkObject(request.getThreshold(), "Threshold");
    }


}
