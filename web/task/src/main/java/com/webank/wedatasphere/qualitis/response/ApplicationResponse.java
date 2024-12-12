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
import com.webank.wedatasphere.qualitis.constant.ApplicationCommentEnum;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;

import java.util.*;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author howeye
 */
public class ApplicationResponse {
    @JsonProperty("application_id")
    private String applicationId;
    @JsonProperty("task")
    private List<TaskResponse> taskResponses;
    @JsonProperty("rule_size")
    private Integer ruleSize;
    @JsonProperty("start_time")
    private String startTime;
    @JsonProperty("end_time")
    private String endTime;
    private Integer status;
    @JsonProperty("finished_task_num")
    private Integer finishedTaskNum;
    @JsonProperty("failed_task_num")
    private Integer failedTaskNum;
    @JsonProperty("failed_check_task_num")
    private Integer failedCheckTaskNum;
    @JsonProperty("invoke_type")
    private Integer invokeType;
    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty("task_group_name")
    private String taskName;
    @JsonProperty("group_id")
    private Long ruleGroupId;
    @JsonProperty("exception_message")
    private String exceptionMessage;
    @JsonProperty("kill_option")
    private Boolean killOption;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("execute_user")
    private String executeUser;
    @JsonProperty("partition")
    private String partition;
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("startup_param")
    private String startupParam;
    @JsonProperty("execution_param")
    private String execParam;
    @JsonProperty("set_flag")
    private String setFlag;
    @JsonProperty("run_date")
    private String runDate;

    @JsonProperty("application_rule_datasource")
    private String ruleDatasource;

    @JsonProperty("comment")
    private String comment;

    public ApplicationResponse(Application application, List<Task> tasks, String localStr) {
        this.applicationId = application.getId();
        this.ruleSize = application.getRuleSize();
        this.startTime = application.getSubmitTime();
        this.endTime = application.getFinishTime();
        this.status = application.getStatus();
        this.finishedTaskNum = application.getFinishTaskNum();
        this.failedTaskNum = application.getFailTaskNum();
        this.failedCheckTaskNum = application.getNotPassTaskNum();
        this.invokeType = application.getInvokeType();
        this.taskResponses = new ArrayList<>();
        this.exceptionMessage = application.getExceptionMessage();
        if (CollectionUtils.isNotEmpty(tasks)) {
            for (Task task : tasks) {
                for (TaskRuleSimple taskRuleSimple : task.getTaskRuleSimples()) {
                    this.projectName = taskRuleSimple.getProjectName();
                    this.taskName = taskRuleSimple.getRuleGroupName();
                    break;
                }
                taskResponses.add(new TaskResponse(task));
            }
        } else {
            this.projectName = application.getProjectName();
            this.taskName = application.getRuleGroupName();
        }
        this.ruleDatasource = application.getRuleDatesource();


        this.ruleGroupId =  application.getRuleGroupId();
        this.createUser = application.getCreateUser();
        this.executeUser = application.getExecuteUser();
        this.partition = application.getPartition();
        this.startupParam = application.getStartupParam();
        this.execParam = application.getExecutionParam();
        this.runDate = application.getRunDate();
        this.setFlag = application.getSetFlag();
        this.clusterName = application.getClusterName();
        this.comment = ApplicationCommentEnum.getCommentName(application.getApplicationComment(), localStr);
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getExecuteUser() {
        return executeUser;
    }

    public void setExecuteUser(String executeUser) {
        this.executeUser = executeUser;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public Boolean getKillOption() {
        return killOption;
    }

    public void setKillOption(Boolean killOption) {
        this.killOption = killOption;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public Integer getFinishedTaskNum() {
        return finishedTaskNum;
    }

    public void setFinishedTaskNum(Integer finishedTaskNum) {
        this.finishedTaskNum = finishedTaskNum;
    }

    public Integer getFailedTaskNum() {
        return failedTaskNum;
    }

    public void setFailedTaskNum(Integer failedTaskNum) {
        this.failedTaskNum = failedTaskNum;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getRuleSize() {
        return ruleSize;
    }

    public void setRuleSize(Integer ruleSize) {
        this.ruleSize = ruleSize;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getInvokeType() {
        return invokeType;
    }

    public void setInvokeType(Integer invokeType) {
        this.invokeType = invokeType;
    }

    public List<TaskResponse> getTaskResponses() {
        return taskResponses;
    }

    public void setTaskResponses(List<TaskResponse> taskResponses) {
        this.taskResponses = taskResponses;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getFailedCheckTaskNum() {
        return failedCheckTaskNum;
    }

    public void setFailedCheckTaskNum(Integer failedCheckTaskNum) {
        this.failedCheckTaskNum = failedCheckTaskNum;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getStartupParam() {
        return startupParam;
    }

    public void setStartupParam(String startupParam) {
        this.startupParam = startupParam;
    }

    public String getExecParam() {
        return execParam;
    }

    public void setExecParam(String execParam) {
        this.execParam = execParam;
    }

    public String getSetFlag() {
        return setFlag;
    }

    public void setSetFlag(String setFlag) {
        this.setFlag = setFlag;
    }

    public String getRunDate() {
        return runDate;
    }

    public void setRunDate(String runDate) {
        this.runDate = runDate;
    }

    public String getRuleDatasource() {
        return ruleDatasource;
    }

    public void setRuleDatasource(String ruleDatasource) {
        this.ruleDatasource = ruleDatasource;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
