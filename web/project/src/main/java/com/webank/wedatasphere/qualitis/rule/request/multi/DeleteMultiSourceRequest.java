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

package com.webank.wedatasphere.qualitis.rule.request.multi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author howeye
 */
public class DeleteMultiSourceRequest {

    @JsonProperty("rule_id")
    private Long multiRuleId;

    public DeleteMultiSourceRequest() {
    }

    public DeleteMultiSourceRequest(Long multiRuleId) {
        this.multiRuleId = multiRuleId;
    }

    public Long getMultiRuleId() {
        return multiRuleId;
    }

    public void setMultiRuleId(Long multiRuleId) {
        this.multiRuleId = multiRuleId;
    }

    public static void checkRequest(DeleteMultiSourceRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        CommonChecker.checkObject(request.getMultiRuleId(), "Multi rule id");
    }
}
