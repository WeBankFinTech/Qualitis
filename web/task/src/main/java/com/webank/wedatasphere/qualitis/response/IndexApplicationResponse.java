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

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author v_wblwyan
 * @date 2018-11-14
 */
public class IndexApplicationResponse {

  @JsonProperty("application_id")
  private String applicationId;
  @JsonProperty("project_name")
  private String projectName;
  @JsonProperty("task_group_name")
  private String taskName;
  @JsonProperty("submit_time")
  private String submitTime;
  private Integer status;
  @JsonProperty("finish_task_num")
  private Integer finishTaskNum;
  @JsonProperty("fail_task_num")
  private Integer failTaskNum;
  @JsonProperty("fail_check_task_num")
  private Integer notPassTaskNum;
  @JsonProperty("total_task_num")
  private Integer totalTaskNum;
  private List<TaskResponse> task;
  @JsonProperty("exception_message")
  private String exceptionMessage;

  public IndexApplicationResponse() {
  }

  public IndexApplicationResponse(Application application, List<Task> tasks) {
    BeanUtils.copyProperties(application, this);
    this.applicationId = application.getId();
    this.task = new ArrayList<>();
    Set<String> projectGroupNames = new HashSet<>();
    for (Task tmpTask : tasks) {
      for (TaskRuleSimple taskRuleSimple : tmpTask.getTaskRuleSimples()) {
        this.projectName = taskRuleSimple.getProjectName();
        this.taskName = taskRuleSimple.getRuleGroupName();
        break;
      }
      this.task.add(new TaskResponse(tmpTask));
    }
  }

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
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

  public String getSubmitTime() {
    return submitTime;
  }

  public void setSubmitTime(String submitTime) {
    this.submitTime = submitTime;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Integer getFinishTaskNum() {
    return finishTaskNum;
  }

  public void setFinishTaskNum(Integer finishTaskNum) {
    this.finishTaskNum = finishTaskNum;
  }

  public Integer getFailTaskNum() {
    return failTaskNum;
  }

  public void setFailTaskNum(Integer failTaskNum) {
    this.failTaskNum = failTaskNum;
  }

  public Integer getNotPassTaskNum() {
    return notPassTaskNum;
  }

  public void setNotPassTaskNum(Integer notPassTaskNum) {
    this.notPassTaskNum = notPassTaskNum;
  }

  public Integer getTotalTaskNum() {
    return totalTaskNum;
  }

  public void setTotalTaskNum(Integer totalTaskNum) {
    this.totalTaskNum = totalTaskNum;
  }

  public List<TaskResponse> getTask() {
    return task;
  }

  public void setTask(List<TaskResponse> task) {
    this.task = task;
  }

  public String getExceptionMessage() {
    return exceptionMessage;
  }

  public void setExceptionMessage(String exceptionMessage) {
    this.exceptionMessage = exceptionMessage;
  }
}
