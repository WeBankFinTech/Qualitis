package com.webank.wedatasphere.qualitis.response;

/**
 * @author allenzhou
 */
public class TaskAlarmConfigResponse {
    private Long taskAlarmConfigId;
    private String ruleName;
    private Integer expectedCheckStatus;

    public TaskAlarmConfigResponse() {
        // Default Constructor
    }

    public TaskAlarmConfigResponse(String ruleName) {
        this.ruleName = ruleName;
    }

    public Integer getExpectedCheckStatus() {
        return expectedCheckStatus;
    }

    public void setExpectedCheckStatus(Integer expectedCheckStatus) {
        this.expectedCheckStatus = expectedCheckStatus;
    }

    public Long getTaskAlarmConfigId() {
        return taskAlarmConfigId;
    }

    public void setTaskAlarmConfigId(Long taskAlarmConfigId) {
        this.taskAlarmConfigId = taskAlarmConfigId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

}
