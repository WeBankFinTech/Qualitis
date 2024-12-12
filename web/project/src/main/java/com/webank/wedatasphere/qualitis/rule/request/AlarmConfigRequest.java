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
import com.webank.wedatasphere.qualitis.rule.constant.CompareTypeEnum;

/**
 * @author howeye
 */
public class AlarmConfigRequest extends AbstractCommonAlarmConfigRequest{

    @JsonProperty("output_meta_id")
    private Long outputMetaId;

    private static final Integer MAX_THRESHOLD_VALUE = 10000;

    public AlarmConfigRequest() {
        // Default Constructor
    }

    public AlarmConfigRequest(Long outputMetaId, Integer checkTemplate, Integer compareType, Double threshold) {
        this.outputMetaId = outputMetaId;
        super.setCheckTemplate(checkTemplate);
        super.setCompareType(compareType);
        super.setThreshold(threshold);
    }

    public Long getOutputMetaId() {
        return outputMetaId;
    }

    public void setOutputMetaId(Long outputMetaId) {
        this.outputMetaId = outputMetaId;
    }

    public static Integer getMaxThresholdValue() {
        return MAX_THRESHOLD_VALUE;
    }

    public static void checkRequest(AlarmConfigRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkObject(request.getThreshold(), "threshold");
        CommonChecker.checkObject(request.getOutputMetaId(), "output_meta_id");
        CommonChecker.checkObject(request.getCheckTemplate(), "checkTemplate");

        // 检查checkTemplate是否是枚举中的值
        checkCheckTemplateEnum(request.getCheckTemplate());

        if (request.getCheckTemplate().equals(CheckTemplateEnum.FIXED_VALUE.getCode())) {
            CommonChecker.checkObject(request.getCompareType(), "compareType");
            // 检查compareType是否是枚举中的值
            checkCompareTypeEnum(request.getCompareType());
        } else {
            if (request.getThreshold() > MAX_THRESHOLD_VALUE) {
                throw new UnExpectedRequestException("The threshold of [" + CheckTemplateEnum.getCheckTemplateName(request.getCheckTemplate()) + "]" +
                        "can not larger than the max value of 10000");
            }
        }
    }

    private static void checkCheckTemplateEnum(Integer checkTemplate) throws UnExpectedRequestException {
        for (CheckTemplateEnum e : CheckTemplateEnum.values()) {
            if (checkTemplate.equals(e.getCode())) {
                return;
            }
        }
        throw new UnExpectedRequestException("value of check_template not support");
    }

    private static void checkCompareTypeEnum(Integer compareType) throws UnExpectedRequestException {
        for (CompareTypeEnum e : CompareTypeEnum.values()) {
            if (compareType.equals(e.getCode())) {
                return;
            }
        }
        throw new UnExpectedRequestException("value of compare_type not support");
    }



}