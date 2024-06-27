package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public class EnableOrDisableRule {

    @JsonProperty("operate_user")
    private String operateUser;

    /**
     * 项目维度
     */
    @JsonProperty("project_enable_list")
    private List<DifferentDimensionsRequest> projectEnableList;
    /**
     * 规则组维度
     */
    @JsonProperty("group_enable_list")
    private List<DifferentDimensionsRequest> groupEnableList;
    /**
     * 数据源维度
     */
    @JsonProperty("data_source_enable_list")
    private List<DifferentDimensionsRequest> datasourceEnableList;
    /**
     * 模板维度
     */
    @JsonProperty("template_enable_list")
    private List<DifferentDimensionsRequest> templateEnableList;
    /**
     * 名称列表
     */
    @JsonProperty("rule_name_enable_list")
    private List<DifferentDimensionsRequest> ruleNameEnableList;

    public List<DifferentDimensionsRequest> getProjectEnableList() {
        return projectEnableList;
    }

    public void setProjectEnableList(List<DifferentDimensionsRequest> projectEnableList) {
        this.projectEnableList = projectEnableList;
    }

    public List<DifferentDimensionsRequest> getGroupEnableList() {
        return groupEnableList;
    }

    public void setGroupEnableList(List<DifferentDimensionsRequest> groupEnableList) {
        this.groupEnableList = groupEnableList;
    }

    public List<DifferentDimensionsRequest> getDatasourceEnableList() {
        return datasourceEnableList;
    }

    public void setDatasourceEnableList(List<DifferentDimensionsRequest> datasourceEnableList) {
        this.datasourceEnableList = datasourceEnableList;
    }

    public List<DifferentDimensionsRequest> getTemplateEnableList() {
        return templateEnableList;
    }

    public void setTemplateEnableList(List<DifferentDimensionsRequest> templateEnableList) {
        this.templateEnableList = templateEnableList;
    }

    public List<DifferentDimensionsRequest> getRuleNameEnableList() {
        return ruleNameEnableList;
    }

    public void setRuleNameEnableList(List<DifferentDimensionsRequest> ruleNameEnableList) {
        this.ruleNameEnableList = ruleNameEnableList;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }
}
