package com.webank.wedatasphere.qualitis.rule.request;

/**
 * @author allenzhou
 */
public class RuleNodeRequest {
    private String ruleObject;
    private String templateObject;
    private String ruleGroupObject;
    private String ruleDataSourcesObject;
    private String ruleDataSourceMappingsObject;
    private String alarmConfigsObject;
    private String ruleVariablesObject;

    /**
     * Template.
     */
    private String templateTemplateStatisticsInputMetaObject;
    private String templateTemplateMidTableInputMetaObject;
    private String templateTemplateOutputMetaObject;

    /**
     * Project info.
     */
    private String projectId;
    private String projectName;
    private String userName;

    private String newProjectId;

    /**
     * Child rule.
     */
    private String childRuleObject;
    private String childTemplateObject;
    private String childRuleDataSourcesObject;
    private String childRuleDataSourceMappingsObject;
    private String childAlarmConfigsObject;
    private String childRuleVariablesObject;

    /**
     * Child template info.
     */
    private String childTemplateTemplateStatisticsInputMetaObject;
    private String childTemplateTemplateMidTableInputMetaObject;
    private String childTemplateTemplateOutputMetaObject;

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

    public String getRuleGroupObject() {
        return ruleGroupObject;
    }

    public void setRuleGroupObject(String ruleGroupObject) {
        this.ruleGroupObject = ruleGroupObject;
    }

    public String getRuleDataSourcesObject() {
        return ruleDataSourcesObject;
    }

    public void setRuleDataSourcesObject(String ruleDataSourcesObject) {
        this.ruleDataSourcesObject = ruleDataSourcesObject;
    }

    public String getRuleDataSourceMappingsObject() {
        return ruleDataSourceMappingsObject;
    }

    public void setRuleDataSourceMappingsObject(String ruleDataSourceMappingsObject) {
        this.ruleDataSourceMappingsObject = ruleDataSourceMappingsObject;
    }

    public String getAlarmConfigsObject() {
        return alarmConfigsObject;
    }

    public void setAlarmConfigsObject(String alarmConfigsObject) {
        this.alarmConfigsObject = alarmConfigsObject;
    }

    public String getRuleVariablesObject() {
        return ruleVariablesObject;
    }

    public void setRuleVariablesObject(String ruleVariablesObject) {
        this.ruleVariablesObject = ruleVariablesObject;
    }

    public String getChildRuleObject() {
        return childRuleObject;
    }

    public void setChildRuleObject(String childRuleObject) {
        this.childRuleObject = childRuleObject;
    }

    public String getTemplateTemplateStatisticsInputMetaObject() {
        return templateTemplateStatisticsInputMetaObject;
    }

    public void setTemplateTemplateStatisticsInputMetaObject(String templateTemplateStatisticsInputMetaObject) {
        this.templateTemplateStatisticsInputMetaObject = templateTemplateStatisticsInputMetaObject;
    }

    public String getTemplateTemplateMidTableInputMetaObject() {
        return templateTemplateMidTableInputMetaObject;
    }

    public void setTemplateTemplateMidTableInputMetaObject(String templateTemplateMidTableInputMetaObject) {
        this.templateTemplateMidTableInputMetaObject = templateTemplateMidTableInputMetaObject;
    }

    public String getTemplateTemplateOutputMetaObject() {
        return templateTemplateOutputMetaObject;
    }

    public void setTemplateTemplateOutputMetaObject(String templateTemplateOutputMetaObject) {
        this.templateTemplateOutputMetaObject = templateTemplateOutputMetaObject;
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

    public RuleNodeRequest() {}

    public String getChildTemplateObject() {
        return childTemplateObject;
    }

    public void setChildTemplateObject(String childTemplateObject) {
        this.childTemplateObject = childTemplateObject;
    }

    public String getChildRuleDataSourcesObject() {
        return childRuleDataSourcesObject;
    }

    public void setChildRuleDataSourcesObject(String childRuleDataSourcesObject) {
        this.childRuleDataSourcesObject = childRuleDataSourcesObject;
    }

    public String getChildRuleDataSourceMappingsObject() {
        return childRuleDataSourceMappingsObject;
    }

    public void setChildRuleDataSourceMappingsObject(String childRuleDataSourceMappingsObject) {
        this.childRuleDataSourceMappingsObject = childRuleDataSourceMappingsObject;
    }

    public String getChildAlarmConfigsObject() {
        return childAlarmConfigsObject;
    }

    public void setChildAlarmConfigsObject(String childAlarmConfigsObject) {
        this.childAlarmConfigsObject = childAlarmConfigsObject;
    }

    public String getChildRuleVariablesObject() {
        return childRuleVariablesObject;
    }

    public void setChildRuleVariablesObject(String childRuleVariablesObject) {
        this.childRuleVariablesObject = childRuleVariablesObject;
    }

    public String getChildTemplateTemplateStatisticsInputMetaObject() {
        return childTemplateTemplateStatisticsInputMetaObject;
    }

    public void setChildTemplateTemplateStatisticsInputMetaObject(String childTemplateTemplateStatisticsInputMetaObject) {
        this.childTemplateTemplateStatisticsInputMetaObject = childTemplateTemplateStatisticsInputMetaObject;
    }

    public String getChildTemplateTemplateMidTableInputMetaObject() {
        return childTemplateTemplateMidTableInputMetaObject;
    }

    public void setChildTemplateTemplateMidTableInputMetaObject(String childTemplateTemplateMidTableInputMetaObject) {
        this.childTemplateTemplateMidTableInputMetaObject = childTemplateTemplateMidTableInputMetaObject;
    }

    public String getChildTemplateTemplateOutputMetaObject() {
        return childTemplateTemplateOutputMetaObject;
    }

    public void setChildTemplateTemplateOutputMetaObject(String childTemplateTemplateOutputMetaObject) {
        this.childTemplateTemplateOutputMetaObject = childTemplateTemplateOutputMetaObject;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "RuleNodeRequest{" +
            "ruleObject='" + ruleObject + '\'' +
            ", templateObject='" + templateObject + '\'' +
            ", ruleGroupObject='" + ruleGroupObject + '\'' +
            ", ruleDataSourcesObject='" + ruleDataSourcesObject + '\'' +
            ", ruleDataSourceMappingsObject='" + ruleDataSourceMappingsObject + '\'' +
            ", alarmConfigsObject='" + alarmConfigsObject + '\'' +
            ", ruleVariablesObject='" + ruleVariablesObject + '\'' +
            ", templateTemplateStatisticsInputMetaObject='" + templateTemplateStatisticsInputMetaObject + '\'' +
            ", templateTemplateMidTableInputMetaObject='" + templateTemplateMidTableInputMetaObject + '\'' +
            ", templateTemplateOutputMetaObject='" + templateTemplateOutputMetaObject + '\'' +
            ", projectId='" + projectId + '\'' +
            ", projectName='" + projectName + '\'' +
            ", userName='" + userName + '\'' +
            ", newProjectId='" + newProjectId + '\'' +
            ", childRuleObject='" + childRuleObject + '\'' +
            ", childTemplateObject='" + childTemplateObject + '\'' +
            ", childRuleDataSourcesObject='" + childRuleDataSourcesObject + '\'' +
            ", childRuleDataSourceMappingsObject='" + childRuleDataSourceMappingsObject + '\'' +
            ", childAlarmConfigsObject='" + childAlarmConfigsObject + '\'' +
            ", childRuleVariablesObject='" + childRuleVariablesObject + '\'' +
            ", childTemplateTemplateStatisticsInputMetaObject='" + childTemplateTemplateStatisticsInputMetaObject + '\'' +
            ", childTemplateTemplateMidTableInputMetaObject='" + childTemplateTemplateMidTableInputMetaObject + '\'' +
            ", childTemplateTemplateOutputMetaObject='" + childTemplateTemplateOutputMetaObject + '\'' +
            '}';
    }
}
