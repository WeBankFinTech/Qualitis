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

package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * @author howeye
 */
public class ApplicationListResultResponse {


    @JsonProperty("application_result")
    private Map<String, String> applicationResult;

    public ApplicationListResultResponse() {
    }

    public ApplicationListResultResponse(Map<String, String> applicationResult) {
        this.applicationResult = applicationResult;
    }

    public Map< String, String > getApplicationResult() {
        return applicationResult;
    }

    public void setApplicationResult(Map< String, String > applicationResult) {
        this.applicationResult = applicationResult;
    }
}
