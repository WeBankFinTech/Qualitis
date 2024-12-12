package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import java.util.Arrays;
import java.util.List;

/**
 * @author allenzhou
 */
public class AddRuleGroupRequest {

    @JsonProperty("project_id")
    private Long projectId;

    @JsonProperty("rule_group_name")
    private String ruleGroupName;

    private List<DataSourceRequest> datasource;

    @JsonProperty("cs_id")
    private String csId;

    public AddRuleGroupRequest() {
//        do something
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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
        CommonChecker.checkObject(projectId, "Project ID");
        CommonChecker.checkCollections(datasource, "Datasource");
    }

    @Override
    public String toString() {
        return "AddRuleGroupRequest{" +
            "projectId=" + projectId +
            ", ruleGroupName='" + ruleGroupName + '\'' +
            ", datasource='" + Arrays.toString(this.datasource.toArray()) + '\'' +
            '}';
    }
}
