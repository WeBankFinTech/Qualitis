package com.webank.wedatasphere.qualitis.project.response;

import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledSignal;
import org.springframework.beans.BeanUtils;

/**
 * @author v_minminghe@webank.com
 * @date 2023-04-21 16:49
 * @description
 */
public class ScheduledSignalResponse {

    private String scheduledProjectName;

    private String scheduledWorkflowName;

    private String ruleGroupIds;

    private Integer type;

    private String name;

    private String contentJson;

    public ScheduledSignalResponse() {
//        Doing something
    }

    public ScheduledSignalResponse(ScheduledSignal scheduledSignal) {
        BeanUtils.copyProperties(scheduledSignal, this);
        this.scheduledProjectName = scheduledSignal.getScheduledProject().getName();
        this.scheduledWorkflowName = scheduledSignal.getScheduledWorkflow().getName();
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

    public String getRuleGroupIds() {
        return ruleGroupIds;
    }

    public void setRuleGroupIds(String ruleGroupIds) {
        this.ruleGroupIds = ruleGroupIds;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentJson() {
        return contentJson;
    }

    public void setContentJson(String contentJson) {
        this.contentJson = contentJson;
    }
}
