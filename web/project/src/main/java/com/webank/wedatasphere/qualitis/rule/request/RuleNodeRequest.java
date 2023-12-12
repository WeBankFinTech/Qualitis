package com.webank.wedatasphere.qualitis.rule.request;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author allenzhou
 */
public class RuleNodeRequest {
    private String ruleObject;
    private String templateObject;
    private String templateDataVisibilityObject;
    private String ruleGroupObject;
    private String userName;

    /**
     * Project info.
     */
    private String projectId;
    private String projectName;
    private String newProjectId;

    /**
     * Execution params.
     */
    private String executionParamObject;
    /**
     * Check alert object.
     */
    @JsonProperty("check_alert_rule")
    private String checkAlertRule;

    public String getRuleObject() {
        return ruleObject;
    }

    public void setRuleObject(String ruleObject) {
        this.ruleObject = ruleObject;
    }

    public String getTemplateObject() {
        return templateObject;
    }

    public void setTemplateObject(String templateObject) {
        this.templateObject = templateObject;
    }

    public String getTemplateDataVisibilityObject() {
        return templateDataVisibilityObject;
    }

    public void setTemplateDataVisibilityObject(String templateDataVisibilityObject) {
        this.templateDataVisibilityObject = templateDataVisibilityObject;
    }

    public String getRuleGroupObject() {
        return ruleGroupObject;
    }

    public void setRuleGroupObject(String ruleGroupObject) {
        this.ruleGroupObject = ruleGroupObject;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getNewProjectId() {
        return newProjectId;
    }

    public void setNewProjectId(String newProjectId) {
        this.newProjectId = newProjectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public RuleNodeRequest() {
        // Default Constructor
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getExecutionParamObject() {
        return executionParamObject;
    }

    public void setExecutionParamObject(String executionParamObject) {
        this.executionParamObject = executionParamObject;
    }

    public String getCheckAlertRule() {
        return checkAlertRule;
    }

    public void setCheckAlertRule(String checkAlertRule) {
        this.checkAlertRule = checkAlertRule;
    }

    @Override
    public String toString() {
        return "RuleNodeRequest{" +
            "ruleObject='" + ruleObject + '\'' +
            ", templateObject='" + templateObject + '\'' +
            ", ruleGroupObject='" + ruleGroupObject + '\'' +
            ", projectId='" + projectId + '\'' +
            ", projectName='" + projectName + '\'' +
            ", userName='" + userName + '\'' +
            ", newProjectId='" + newProjectId + '\'' +
            '}';
    }
}
