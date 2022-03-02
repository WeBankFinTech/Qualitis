package com.webank.wedatasphere.qualitis.response;

import java.util.List;

/**
 * @author allenzhou
 */
public class TaskStatusResponse {
    private Integer taskId;
    private Integer taskExpectedStatus;
    private List<TaskCheckRuleResponse> taskCheckRuleResponses;

    public TaskStatusResponse() {
    }

    public TaskStatusResponse(Integer taskId, Integer taskStatus) {
        this.taskId = taskId;
        this.taskExpectedStatus = taskStatus;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getTaskExpectedStatus() {
        return taskExpectedStatus;
    }

    public void setTaskExpectedStatus(Integer taskExpectedStatus) {
        this.taskExpectedStatus = taskExpectedStatus;
    }

    public List<TaskCheckRuleResponse> getTaskCheckRuleResponses() {
        return taskCheckRuleResponses;
    }

    public void setTaskCheckRuleResponses(List<TaskCheckRuleResponse> taskCheckRuleResponses) {
        this.taskCheckRuleResponses = taskCheckRuleResponses;
    }
}
