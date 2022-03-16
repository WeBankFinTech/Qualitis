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
import com.webank.wedatasphere.qualitis.bean.TaskSubmitResult;
import java.util.ArrayList;
import java.util.List;

/**
 * @author allenzhou
 */
public class ApplicationProjectResponse {
    @JsonProperty("last_submit_time")
    private Long lastSubmitTime;

    @JsonProperty("project_applications")
    private List<ApplicationTaskSimpleResponse> applicationTaskSimpleResponses;

    public ApplicationProjectResponse() {
        this.applicationTaskSimpleResponses = new ArrayList<>();
    }

    public List<ApplicationTaskSimpleResponse> getApplicationTaskSimpleResponses() {
        return applicationTaskSimpleResponses;
    }

    public void setApplicationTaskSimpleResponses(
        List<ApplicationTaskSimpleResponse> applicationTaskSimpleResponses) {
        this.applicationTaskSimpleResponses = applicationTaskSimpleResponses;
    }

    public Long getLastSubmitTime() {
        return lastSubmitTime;
    }

    public void setLastSubmitTime(Long lastSubmitTime) {
        this.lastSubmitTime = lastSubmitTime;
    }
}
