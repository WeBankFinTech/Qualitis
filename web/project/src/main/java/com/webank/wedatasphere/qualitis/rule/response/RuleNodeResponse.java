package com.webank.wedatasphere.qualitis.rule.response;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 实现 Dss Appconn 的 NodeSerivce 的导入导出接口，采用 json 序列化 rule 对象及其相关引用为字符串的方法，实现对象在服务间的传递
 * @author allenzhou
 */
public class RuleNodeResponse {
    @JsonProperty("rule_object")
    private String ruleObject;
    @JsonProperty("template_data_visibility_object")
    private String templateDataVisibilityObject;
    @JsonProperty("template_object")
    private String templateObject;
    @JsonProperty("rule_group_object")
    private String ruleGroupObject;

    /**
     * Project info.
     */
    @JsonProperty("project_id")
    private String projectId;
    @JsonProperty("project_name")
    private String projectName;

    /**
     * Rule execution param object.
     */
    @JsonProperty("execution_param")
    private String executionParamObject;
    /**
     * Check alert object.
     */
    @JsonProperty("check_alert_rule")
    private String checkAlertRule;

    public RuleNodeResponse() {
        // Default do nothing.
    }

    public String getRuleObject() {
        return ruleObject;
    }

    public void setRuleObject(String ruleObject) {
        this.ruleObject = ruleObject;
    }


    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
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
        return "RuleNodeResponse{" +
            "ruleObject='" + ruleObject + '\'' +
            ", templateObject='" + templateObject + '\'' +
            ", ruleGroupObject='" + ruleGroupObject + '\'' +
            ", projectId='" + projectId + '\'' +
            ", projectName='" + projectName + '\'' +
            ", checkAlertRule='" + checkAlertRule + '\'' +
            '}';
    }
}
