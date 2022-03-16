package com.webank.wedatasphere.qualitis.response;

import java.util.List;

/**
 * @author allenzhou
 */
public class TaskCheckRuleResponse {
    private Long ruleId;
    private Double expectedResult;
    private List<TaskAlarmConfigResponse> taskAlarmConfigResponses;

    public TaskCheckRuleResponse() {
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public List<TaskAlarmConfigResponse> getTaskAlarmConfigResponses() {
        return taskAlarmConfigResponses;
    }

    public void setTaskAlarmConfigResponses(List<TaskAlarmConfigResponse> taskAlarmConfigResponses) {
        this.taskAlarmConfigResponses = taskAlarmConfigResponses;
    }

    public Double getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(Double expectedResult) {
        this.expectedResult = expectedResult;
    }
}
