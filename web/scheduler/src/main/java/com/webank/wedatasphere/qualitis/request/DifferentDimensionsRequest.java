package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleRequest;

/**
 * @author v_gaojiedeng@webank.com
 */
public class DifferentDimensionsRequest {

    @JsonProperty("project_id")
    private Long projectId;

    @JsonProperty("project_name")
    private String projectName;

    @JsonProperty("rule_group_id")
    private Long ruleGroupId;

    @JsonProperty("rule_group_name")
    private String ruleGroupName;

    @JsonProperty("template_en_name")
    private String templateEnName;

    @JsonProperty("template_id")
    private String templateId;

    @JsonProperty("rule_name")
    private String ruleName;

    @JsonProperty("db_and_table")
    private String dbAndTable;

    @JsonProperty("rule_enable")
    private Boolean ruleEnable;

    @JsonProperty("create_user")
    private String createUser;

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public void setRuleGroupName(String ruleGroupName) {
        this.ruleGroupName = ruleGroupName;
    }

    public String getTemplateEnName() {
        return templateEnName;
    }

    public void setTemplateEnName(String templateEnName) {
        this.templateEnName = templateEnName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getDbAndTable() {
        return dbAndTable;
    }

    public void setDbAndTable(String dbAndTable) {
        this.dbAndTable = dbAndTable;
    }

    public void setRuleEnable(Boolean ruleEnable) {
        this.ruleEnable = ruleEnable;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Boolean getRuleEnable() {
        return ruleEnable;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public static void checkRequest(DifferentDimensionsRequest request, String type) throws UnExpectedRequestException {
        CommonChecker.checkString(request.getProjectName(), "project_name");
        CommonChecker.checkString(request.getCreateUser(), "create_user");
        CommonChecker.checkObject(request.getRuleEnable(), "rule_enable");
        if ("ruleGroup".equals(type)) {
            CommonChecker.checkString(request.getRuleGroupName(), "rule_group_name");
        } else if ("dataSource".equals(type)) {
            CommonChecker.checkString(request.getDbAndTable(), "db_and_table");
        } else if ("template".equals(type)) {
            CommonChecker.checkString(request.getTemplateEnName(), "template_en_name");
        } else if ("ruleName".equals(type)) {
            CommonChecker.checkString(request.getRuleName(), "rule_name");
        }

    }


}
