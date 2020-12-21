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
public class GeneralExecutionRequest {

    @JsonProperty("project_id")
    private Long projectId;
    private String partition;
    @JsonProperty("execution_user")
    private String executionUser;

    @JsonProperty("create_user")
    private String createUser;

    @JsonProperty("rule_list")
    private List<Long> ruleList;

    private String cluster;
    private String database;
    private String table;
    @JsonProperty("cross_table")
    private Boolean crossTable;

    @JsonProperty("group_id")
    private Long groupId;



    public GeneralExecutionRequest() {
        this.crossTable = false;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

    public List<Long> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<Long> ruleList) {
        this.ruleList = ruleList;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Boolean getCrossTable() {
        return crossTable;
    }

    public void setCrossTable(Boolean crossTable) {
        this.crossTable = crossTable;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public static void checkRequest(GeneralExecutionRequest request) throws UnExpectedRequestException {
        com.webank.wedatasphere.qualitis.project.request.CommonChecker.checkObject(request, "Request");
        com.webank.wedatasphere.qualitis.project.request.CommonChecker.checkString(request.getExecutionUser(), "Execution_user");
        CommonChecker.checkString(request.getCreateUser(), "Create_user");
    }

    @Override
    public String toString() {
        return "GeneralExecutionRequest{" +
                "projectId=" + projectId +
                ", partition='" + partition + '\'' +
                ", executionUser='" + executionUser + '\'' +
                ", ruleList=" + ruleList +
                ", cluster='" + cluster + '\'' +
                ", database='" + database + '\'' +
                ", table='" + table + '\'' +
                ", crossTable=" + crossTable +
                '}';
    }
}
