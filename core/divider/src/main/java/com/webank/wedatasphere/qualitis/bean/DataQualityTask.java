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

package com.webank.wedatasphere.qualitis.bean;

import com.webank.wedatasphere.qualitis.constant.TaskTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateActionTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.exception.ArgumentException;
import com.webank.wedatasphere.qualitis.constant.TaskTypeEnum;
import com.webank.wedatasphere.qualitis.exception.ArgumentException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
public class DataQualityTask {
    private String applicationId;
    private Integer taskType;
    private List<RuleTaskDetail> ruleTaskDetails;
    private String createTime;
    private String partition;
    private Long taskId;
    private String user;
    private String startupParam;

    public DataQualityTask() {
    }

    public DataQualityTask(String applicationId, String createTime, String partition, List<RuleTaskDetail> ruleTaskDetails) throws ArgumentException {
        List<Rule> rules = ruleTaskDetails.stream().map(RuleTaskDetail::getRule).collect(Collectors.toList());
        List<Integer> actionTypeList = rules.stream().map(rule -> rule.getTemplate().getActionType()).distinct().collect(Collectors.toList());
        if (actionTypeList.isEmpty()) {
            throw new ArgumentException("Error! Action type can not be null");
        }
        if (actionTypeList.size() != 1) {
            taskType = TaskTypeEnum.MIX_TASK.getCode();
        }
        Integer actionType = actionTypeList.get(0);
        if (actionType.equals(TemplateActionTypeEnum.SQL.getCode())) {
            taskType = TaskTypeEnum.SQL_TASK.getCode();
        } else if (actionType.equals(TemplateActionTypeEnum.JAVA.getCode())) {
            taskType = TaskTypeEnum.JAVA_TASK.getCode();
        } else if (actionType.equals(TemplateActionTypeEnum.PYTHON.getCode())) {
            taskType = TaskTypeEnum.PYTHON_TASK.getCode();
        } else if (actionType.equals(TemplateActionTypeEnum.SCALA.getCode())) {
            taskType = TaskTypeEnum.SPARK_TASK.getCode();
        } else {
            throw new ArgumentException("Error! Action type: [" + actionType + "] is not supported");
        }

        this.applicationId = applicationId;
        this.createTime = createTime;
        this.partition = partition;
        this.ruleTaskDetails = ruleTaskDetails;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public List<RuleTaskDetail> getRuleTaskDetails() {
        return ruleTaskDetails;
    }

    public void setRuleTaskDetails(List<RuleTaskDetail> ruleTaskDetails) {
        this.ruleTaskDetails = ruleTaskDetails;
    }

    public String getStartupParam() {
        return startupParam;
    }

    public void setStartupParam(String startupParam) {
        this.startupParam = startupParam;
    }

    @Override
    public String toString() {
        return "DataQualityTask{" +
            "applicationId='" + applicationId + '\'' +
            ", taskType=" + taskType +
            ", ruleTaskDetails=" + ruleTaskDetails +
            ", createTime='" + createTime + '\'' +
            ", partition='" + partition + '\'' +
            ", taskId=" + taskId +
            ", user='" + user + '\'' +
            '}';
    }
}
