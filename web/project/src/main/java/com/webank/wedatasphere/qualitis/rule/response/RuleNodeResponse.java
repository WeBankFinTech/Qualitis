package com.webank.wedatasphere.qualitis.rule.response;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 实现 DSS appjoint 的 NodeSerivce 的导入导出接口，采用 json 序列化 rule 对象及其相关引用为字符串的方法，实现对象在服务间的传递
 * @author allenzhou
 */
public class RuleNodeResponse {
    @JsonProperty("rule_object")
    private String ruleObject;
    @JsonProperty("template_object")
    private String templateObject;
    @JsonProperty("rule_group_object")
    private String ruleGroupObject;
    @JsonProperty("rule_datasources_object")
    private String ruleDataSourcesObject;
    @JsonProperty("rule_datasources_mappings_object")
    private String ruleDataSourceMappingsObject;
    @JsonProperty("alarm_configs_object")
    private String alarmConfigsObject;
    @JsonProperty("rule_variables_object")
    private String ruleVariablesObject;

    /**
     * Template info.
     */
    @JsonProperty("template_templateStatisticsInputMeta_object")
    private String templateTemplateStatisticsInputMetaObject;
    @JsonProperty("template_templateMidTableInputMeta_object")
    private String templateTemplateMidTableInputMetaObject;
    @JsonProperty("template_templateOutputMeta_object")
    private String templateTemplateOutputMetaObject;

    /**
     * Project info.
     */
    @JsonProperty("project_id")
    private String projectId;
    @JsonProperty("project_name")
    private String projectName;

    /**
     * Child rule.
     */
    @JsonProperty("child_rule_object")
    private String childRuleObject;
    @JsonProperty("child_template_object")
    private String childTemplateObject;
    @JsonProperty("child_rule_datasources_object")
    private String childRuleDataSourcesObject;
    @JsonProperty("child_rule_datasources_mappings_object")
    private String childRuleDataSourceMappingsObject;
    @JsonProperty("child_alarm_configs_object")
    private String childAlarmConfigsObject;
    @JsonProperty("child_rule_variables_object")
    private String childRuleVariablesObject;

    /**
     * Child template info.
     */
    @JsonProperty("child_template_templateStatisticsInputMeta_object")
    private String childTemplateTemplateStatisticsInputMetaObject;
    @JsonProperty("child_template_templateMidTableInputMeta_object")
    private String childTemplateTemplateMidTableInputMetaObject;
    @JsonProperty("child_template_templateOutputMeta_object")
    private String childTemplateTemplateOutputMetaObject;

    public RuleNodeResponse() {}

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

    @Override
    public String toString() {
        return "RuleNodeResponse{" +
            "ruleObject='" + ruleObject + '\'' +
            ", templateObject='" + templateObject + '\'' +
            ", ruleGroupObject='" + ruleGroupObject + '\'' +
            ", ruleDataSourcesObject='" + ruleDataSourcesObject + '\'' +
            ", ruleDataSourceMappingsObject='" + ruleDataSourceMappingsObject + '\'' +
            ", alarmConfigsObject='" + alarmConfigsObject + '\'' +
            ", ruleVariablesObject='" + ruleVariablesObject + '\'' +
            ", childRuleObject='" + childRuleObject + '\'' +
            ", templateTemplateStatisticsInputMetaObject='" + templateTemplateStatisticsInputMetaObject + '\'' +
            ", templateTemplateMidTableInputMetaObject='" + templateTemplateMidTableInputMetaObject + '\'' +
            ", templateTemplateOutputMetaObject='" + templateTemplateOutputMetaObject + '\'' +
            ", projectId='" + projectId + '\'' +
            ", projectName='" + projectName + '\'' +
            '}';
    }
}
