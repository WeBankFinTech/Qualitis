package com.webank.wedatasphere.qualitis.project.response;

import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledFrontBackRule;

/**
 * @author v_gaojiedeng@webank.com
 */
public class ScheduledFrontBackRuleResponse {

    private String scheduledTaskName;

    private String ruleGroupName;

    private Integer triggerType;

    public ScheduledFrontBackRuleResponse(){
//        Doing something
    }

    public ScheduledFrontBackRuleResponse(ScheduledFrontBackRule scheduledFrontBackRule) {
        this.scheduledTaskName = scheduledFrontBackRule.getScheduledTask().getTaskName();
        this.ruleGroupName = scheduledFrontBackRule.getRuleGroup().getRuleGroupName();
        this.triggerType = scheduledFrontBackRule.getTriggerType();
    }

    public String getScheduledTaskName() {
        return scheduledTaskName;
    }

    public void setScheduledTaskName(String scheduledTaskName) {
        this.scheduledTaskName = scheduledTaskName;
    }

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public void setRuleGroupName(String ruleGroupName) {
        this.ruleGroupName = ruleGroupName;
    }

    public Integer getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(Integer triggerType) {
        this.triggerType = triggerType;
    }
}
