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
 * @author allenzhou
 */
public class FilterAdvanceRequest {
    @JsonProperty("application_id")
    private String applicationId;
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("selected_status")
    private Integer status;
    @JsonProperty("comment_type")
    private Integer commentType;
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("database_name")
    private String databaseName;
    @JsonProperty("table_name")
    private String tableName;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;

    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("end_time")
    private String endTime;
    @JsonProperty("execute_user")
    private String executeUser;

    @JsonProperty("stop_able_status")
    private List<Integer> stopAbleStatus;
    @JsonProperty("start_finish_time")
    private String startFinishTime;
    @JsonProperty("end_finish_time")
    private String endFinishTime;

    private Integer page;
    private Integer size;

    public FilterAdvanceRequest() {
        // Do nothing.
    }

    public static void checkRequest(FilterAdvanceRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Advance filter request.");
        CommonChecker.checkObject(request.getPage(), "page");
        CommonChecker.checkObject(request.getSize(), "size");
    }

    public String getStartFinishTime() {
        return startFinishTime;
    }

    public void setStartFinishTime(String startFinishTime) {
        this.startFinishTime = startFinishTime;
    }

    public String getEndFinishTime() {
        return endFinishTime;
    }

    public void setEndFinishTime(String endFinishTime) {
        this.endFinishTime = endFinishTime;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCommentType() {
        return commentType;
    }

    public void setCommentType(Integer commentType) {
        this.commentType = commentType;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getExecuteUser() {
        return executeUser;
    }

    public void setExecuteUser(String executeUser) {
        this.executeUser = executeUser;
    }

    public List<Integer> getStopAbleStatus() {
        return stopAbleStatus;
    }

    public void setStopAbleStatus(List<Integer> stopAbleStatus) {
        this.stopAbleStatus = stopAbleStatus;
    }
}
