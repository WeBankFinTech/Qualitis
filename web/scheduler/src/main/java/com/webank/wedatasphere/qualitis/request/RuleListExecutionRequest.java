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

import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
public class RuleListExecutionRequest {
    @JsonProperty("rule_list")
    private List<Long> ruleList;
    @JsonProperty("execution_param")
    private String executionParam;
    @JsonProperty("execution_user")
    private String executionUser;
    @JsonProperty("create_user")
    private String createUser;

    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("startup_param_name")
    private String startupParamName;
    @JsonProperty("set_flag")
    private String setFlag;
    @JsonProperty("dynamic_partition_bool")
    private boolean dyNamicPartition;
    @JsonProperty("dynamic_partition_prefix")
    private String dyNamicPartitionPrefix;
    @JsonProperty("bool_async")
    private boolean async;
    public RuleListExecutionRequest() {
        // Default Constructor
    }

    public RuleListExecutionRequest(Long ruleId,  String createUser, String executionUser) {
        ruleList = new ArrayList<>(1);
        ruleList.add(ruleId);
        this.createUser = createUser;
        this.executionUser = executionUser;
    }

    public RuleListExecutionRequest(RuleResponse ruleResponse, String createUser, String executionUser) {
        ruleList = new ArrayList<>(1);
        ruleList.add(ruleResponse.getRuleId());
        this.createUser = createUser;
        this.executionUser = executionUser;
    }

    public List<Long> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<Long> ruleList) {
        this.ruleList = ruleList;
    }

    public String getExecutionParam() {
        return executionParam;
    }

    public void setExecutionParam(String executionParam) {
        this.executionParam = executionParam;
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

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getStartupParamName() {
        return startupParamName;
    }

    public void setStartupParamName(String startupParamName) {
        this.startupParamName = startupParamName;
    }

    public String getSetFlag() {
        return setFlag;
    }

    public void setSetFlag(String setFlag) {
        this.setFlag = setFlag;
    }

    public boolean getDyNamicPartition() {
        return dyNamicPartition;
    }

    public void setDyNamicPartition(boolean dyNamicPartition) {
        this.dyNamicPartition = dyNamicPartition;
    }

    public String getDyNamicPartitionPrefix() {
        return dyNamicPartitionPrefix;
    }

    public void setDyNamicPartitionPrefix(String dyNamicPartitionPrefix) {
        this.dyNamicPartitionPrefix = dyNamicPartitionPrefix;
    }

    public boolean getAsync() {
        return async;
    }

    public void setAsync(boolean async) {
        this.async = async;
    }

    public static void checkRequest(RuleListExecutionRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        if (null == request.getRuleList() || request.getRuleList().isEmpty()) {
            throw new UnExpectedRequestException("Rules can not be null or empty");
        }
        CommonChecker.checkString(request.getExecutionUser(), "Execution_user");
        CommonChecker.checkString(request.getCreateUser(), "Create_user");
    }
}
