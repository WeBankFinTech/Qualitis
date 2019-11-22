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
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

import java.util.List;

/**
 * @author howeye
 */
public class DownloadRuleRequest {

    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("rule_ids")
    private List<Long> ruleIds;

    public DownloadRuleRequest() {
    }

    public DownloadRuleRequest(Long projectId, List<Long> ruleIds) {
        this.projectId = projectId;
        this.ruleIds = ruleIds;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<Long> getRuleIds() {
        return ruleIds;
    }

    public void setRuleIds(List<Long> ruleIds) {
        this.ruleIds = ruleIds;
    }

    public static void checkRequest(DownloadRuleRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        Boolean hasNoRule = (request.getRuleIds() == null || request.getRuleIds().isEmpty());
        if (request.getProjectId() == null && hasNoRule) {
            throw new UnExpectedRequestException("Project id and rule id can be be null at the same time");
        }
    }
}
