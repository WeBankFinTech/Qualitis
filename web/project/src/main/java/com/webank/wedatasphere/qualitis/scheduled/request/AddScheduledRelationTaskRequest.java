package com.webank.wedatasphere.qualitis.scheduled.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2023-04-17 11:41
 * @description
 */
public class AddScheduledRelationTaskRequest {

    @JsonProperty("work_flow")
    private String workFlow;
    @JsonProperty("task_name")
    private String taskName;
    @JsonProperty("front_rule_group")
    private List<Long> frontRuleGroup;
    @JsonProperty("back_rule_group")
    private List<Long> backRuleGroup;


    public String getWorkFlow() {
        return workFlow;
    }

    public void setWorkFlow(String workFlow) {
        this.workFlow = workFlow;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<Long> getFrontRuleGroup() {
        return frontRuleGroup;
    }

    public void setFrontRuleGroup(List<Long> frontRuleGroup) {
        this.frontRuleGroup = frontRuleGroup;
    }

    public List<Long> getBackRuleGroup() {
        return backRuleGroup;
    }

    public void setBackRuleGroup(List<Long> backRuleGroup) {
        this.backRuleGroup = backRuleGroup;
    }

    public void checkRequest() throws UnExpectedRequestException {
        CommonChecker.checkString(this.getWorkFlow(), "work_flow");
        CommonChecker.checkString(this.getTaskName(), "task_name");
    }
}
