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
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;

import java.util.*;

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
    private List<String> projects;
    @JsonProperty("exception_message")
    private String exceptionMessage;

    public ApplicationResponse(Application application, List<Task> tasks) {
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
        Set<String> projectSet = new HashSet<>();
        this.projects = new ArrayList<>();
        for (Task task : tasks) {
            for (TaskRuleSimple taskRuleSimple : task.getTaskRuleSimples()) {
                projectSet.add(taskRuleSimple.getProjectName());
            }

            taskResponses.add(new TaskResponse(task));
        }
        projects.addAll(projectSet);
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

    public List<String> getProjects() {
        return projects;
    }

    public void setProjects(List<String> projects) {
        this.projects = projects;
    }

    public Integer getFailedCheckTaskNum() {
        return failedCheckTaskNum;
    }

    public void setFailedCheckTaskNum(Integer failedCheckTaskNum) {
        this.failedCheckTaskNum = failedCheckTaskNum;
    }
}
