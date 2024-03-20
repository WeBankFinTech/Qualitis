package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

import java.util.List;

/**
 * @author allenzhou
 */
public class ModifyRuleGroupRequest {

    @JsonProperty("project_id")
    private Long projectId;

    @JsonProperty("rule_group_id")
    private Long ruleGroupId;

    @JsonProperty("rule_group_name")
    private String ruleGroupName;

    private List<DataSourceRequest> datasource;

    @JsonProperty("cs_id")
    private String csId;


    public ModifyRuleGroupRequest() {
//        do something
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

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public void setRuleGroupName(String ruleGroupName) {
        this.ruleGroupName = ruleGroupName;
    }

    public List<DataSourceRequest> getDatasource() {
        return datasource;
    }

    public void setDatasource(List<DataSourceRequest> datasource) {
        this.datasource = datasource;
    }

    public String getCsId() {
        return csId;
    }

    public void setCsId(String csId) {
        this.csId = csId;
    }

    public void checkRequest() throws UnExpectedRequestException {
        CommonChecker.checkString(ruleGroupId.toString(), "Rule group ID");
        CommonChecker.checkString(ruleGroupName, "Rule group name");
        CommonChecker.checkMatcher("[a-zA-Z0-9\\u4e00-\\u9fa5_]+", ruleGroupName);
    }
}
