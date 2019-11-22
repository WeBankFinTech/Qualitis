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

package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

import java.util.List;

/**
 * @author howeye
 */
public class RuleListExecutionRequest {

    @JsonProperty("rule_list")
    private List<Long> ruleList;
    private String partition;
    @JsonProperty("execution_user")
    private String executionUser;
    @JsonProperty("create_user")
    private String createUser;

    public RuleListExecutionRequest() {
        // Default Constructor
    }

    public List<Long> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<Long> ruleList) {
        this.ruleList = ruleList;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getExecutionUser() {
        return executionUser;
    }

    public void setExecutionUser(String executionUser) {
        this.executionUser = executionUser;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public static void checkRequest(RuleListExecutionRequest request) throws UnExpectedRequestException {
        com.webank.wedatasphere.qualitis.project.request.CommonChecker.checkObject(request, "Request");
        if (null == request.getRuleList() || request.getRuleList().isEmpty()) {
            throw new UnExpectedRequestException("Rules can not be null or empty");
        }
        com.webank.wedatasphere.qualitis.project.request.CommonChecker.checkString(request.getExecutionUser(), "Execution_user");
        CommonChecker.checkString(request.getCreateUser(), "Create_user");
    }
}
