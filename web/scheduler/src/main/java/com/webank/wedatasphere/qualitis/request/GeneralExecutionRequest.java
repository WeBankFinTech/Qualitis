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
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import java.util.List;

/**
 * @author howeye
 */
public class GeneralExecutionRequest {
    @JsonProperty("project_id")
    private Long projectId;

    @JsonProperty("group_id")
    private Long groupId;

    @JsonProperty("rule_list")
    private List<Long> ruleList;

    @JsonProperty("execution_user")
    private String executionUser;

    @JsonProperty("execution_param")
    private String executionParam;

    @JsonProperty("create_user")
    private String createUser;

    @JsonProperty(value = "project_name")
    private String projectName;
    @JsonProperty(value = "rule_name_list")
    private List<String> ruleNameList;

    @JsonProperty("cross_table")
    private Boolean crossTable;
    private String database;
    private String cluster;
    private String table;

    @JsonProperty("node_name")
    private String nodeName;

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

    public GeneralExecutionRequest() {
        this.crossTable = false;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public List<Long> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<Long> ruleList) {
        this.ruleList = ruleList;
    }

    public String getExecutionUser() {
        return executionUser;
    }

    public void setExecutionUser(String executionUser) {
        this.executionUser = executionUser;
    }

    public String getExecutionParam() {
        return executionParam;
    }

    public void setExecutionParam(String executionParam) {
        this.executionParam = executionParam;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<String> getRuleNameList() {
        return ruleNameList;
    }

    public void setRuleNameList(List<String> ruleNameList) {
        this.ruleNameList = ruleNameList;
    }

    public Boolean getCrossTable() {
        return crossTable;
    }

    public void setCrossTable(Boolean crossTable) {
        this.crossTable = crossTable;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
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

    public static void checkRequest(GeneralExecutionRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        CommonChecker.checkString(request.getCreateUser(), "Create User");
        CommonChecker.checkString(request.getExecutionUser(), "Execution User");
    }

    @Override
    public String toString() {
        return "GeneralExecutionRequest{" +
            "projectId=" + projectId +
            ", groupId=" + groupId +
            ", ruleList=" + ruleList +
            ", executionUser='" + executionUser + '\'' +
            ", executionParam='" + executionParam + '\'' +
            ", createUser='" + createUser + '\'' +
            ", projectName='" + projectName + '\'' +
            ", ruleNameList=" + ruleNameList +
            ", crossTable=" + crossTable +
            ", database='" + database + '\'' +
            ", cluster='" + cluster + '\'' +
            ", table='" + table + '\'' +
            ", nodeName='" + nodeName + '\'' +
            ", clusterName='" + clusterName + '\'' +
            ", startupParamName='" + startupParamName + '\'' +
            ", setFlag='" + setFlag + '\'' +
            ", dyNamicPartition=" + dyNamicPartition +
            ", dyNamicPartitionPrefix='" + dyNamicPartitionPrefix + '\'' +
            ", async=" + async +
            '}';
    }
}
