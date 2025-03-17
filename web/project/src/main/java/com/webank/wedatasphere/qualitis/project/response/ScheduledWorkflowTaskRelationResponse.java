package com.webank.wedatasphere.qualitis.project.response;

import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflowTaskRelation;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-15 10:00
 * @description
 */
public class ScheduledWorkflowTaskRelationResponse {

    private String scheduledProjectName;

    private String scheduledWorkflowName;

    private String scheduledTaskName;

    private String ruleGroupName;

    public ScheduledWorkflowTaskRelationResponse() {
//        Doing something
    }

    public ScheduledWorkflowTaskRelationResponse(ScheduledWorkflowTaskRelation scheduledWorkflowTaskRelation) {
        this.scheduledProjectName = scheduledWorkflowTaskRelation.getScheduledProject().getName();
        this.scheduledWorkflowName = scheduledWorkflowTaskRelation.getScheduledWorkflow().getName();
        this.scheduledTaskName = scheduledWorkflowTaskRelation.getScheduledTask().getTaskName();
        RuleGroup ruleGroup = scheduledWorkflowTaskRelation.getRuleGroup();
        if (ruleGroup != null) {
            this.ruleGroupName = ruleGroup.getRuleGroupName();
        }
    }

    public String getScheduledProjectName() {
        return scheduledProjectName;
    }

    public void setScheduledProjectName(String scheduledProjectName) {
        this.scheduledProjectName = scheduledProjectName;
    }

    public String getScheduledWorkflowName() {
        return scheduledWorkflowName;
    }

    public void setScheduledWorkflowName(String scheduledWorkflowName) {
        this.scheduledWorkflowName = scheduledWorkflowName;
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
}
