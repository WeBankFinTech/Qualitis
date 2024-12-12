package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2022/6/6 20:20
 */
public class QueryGroupRulesRequest {
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;

    @JsonProperty("template_id")
    private Long templateId;

    @JsonProperty("columns")
    private List<String> columns;
    @JsonProperty("cn_name")
    private String cnName;
    @JsonProperty("name")
    private String name;
    @JsonProperty("rule_type")
    private Integer ruleType;

    private int page;
    private int size;

    public QueryGroupRulesRequest() {
        this.page = 0;
        this.size = 10;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void checkRequest() throws UnExpectedRequestException {
        CommonChecker.checkObject(projectId, "Project ID");
        CommonChecker.checkObject(ruleGroupId, "Rule group ID");
    }
}
