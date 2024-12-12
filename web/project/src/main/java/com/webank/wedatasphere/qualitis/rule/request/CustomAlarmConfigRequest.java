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

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;

/**
 * @author howeye
 */
public class CustomAlarmConfigRequest extends AbstractCommonAlarmConfigRequest {

    public CustomAlarmConfigRequest() {
//        do something
    }

    public static void checkRequest(CustomAlarmConfigRequest request) throws UnExpectedRequestException {

        CommonChecker.checkObject(request, "Request");
        CommonChecker.checkCheckTemplate(request.getCheckTemplate());
        if (request.getCheckTemplate().equals(CheckTemplateEnum.FIXED_VALUE.getCode())) {
            CommonChecker.checkCompareType(request.getCompareType());
        }
        CommonChecker.checkObject(request.getThreshold(), "Threshold");
        //CommonChecker.checkObject(request.getRuleMetricEnCode(), "Rule Metric");
    }


}
