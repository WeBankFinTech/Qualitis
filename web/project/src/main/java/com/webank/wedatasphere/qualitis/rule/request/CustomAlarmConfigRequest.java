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
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;

/**
 * @author howeye
 */
public class CustomAlarmConfigRequest {

    @JsonProperty("check_template")
    private Integer checkTemplate;
    @JsonProperty("compare_type")
    private Integer compareType;
    private Double threshold;

    public CustomAlarmConfigRequest() {
    }

    public Integer getCheckTemplate() {
        return checkTemplate;
    }

    public void setCheckTemplate(Integer checkTemplate) {
        this.checkTemplate = checkTemplate;
    }

    public Integer getCompareType() {
        return compareType;
    }

    public void setCompareType(Integer compareType) {
        this.compareType = compareType;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public static void checkRequest(CustomAlarmConfigRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        CommonChecker.checkCheckTemplate(request.getCheckTemplate());
        if (request.getCheckTemplate().equals(CheckTemplateEnum.FIXED_VALUE.getCode())) {
            CommonChecker.checkCompareType(request.getCompareType());
        }
        CommonChecker.checkObject(request.getThreshold(), "Threshold");
    }
}
