package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2024-05-13 15:38
 * @description
 */
public class ModifyCalcuTemplateRequest {

    @JsonProperty(value = "template_id", required = true)
    private Long ruleTemplateId;
    @JsonProperty(value = "cn_name")
    private String cnName;
    private String description;
    @JsonProperty(value = "udf_id")
    private Long udfId;
    @JsonProperty(value = "sql_action", required = true)
    private String sqlAction;
    @JsonProperty(value = "dev_department_name")
    private String devDepartmentName;
    @JsonProperty(value = "ops_department_name")
    private String opsDepartmentName;
    @JsonProperty(value = "dev_department_id")
    private Long devDepartmentId;
    @JsonProperty(value = "ops_department_id")
    private Long opsDepartmentId;
    @JsonProperty(value = "visibility_department_list")
    private List<DepartmentSubInfoRequest> visibilityDepartmentList;

    public Long getRuleTemplateId() {
        return ruleTemplateId;
    }

    public void setRuleTemplateId(Long ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUdfId() {
        return udfId;
    }

    public void setUdfId(Long udfId) {
        this.udfId = udfId;
    }

    public String getSqlAction() {
        return sqlAction;
    }

    public void setSqlAction(String sqlAction) {
        this.sqlAction = sqlAction;
    }

    public String getDevDepartmentName() {
        return devDepartmentName;
    }

    public void setDevDepartmentName(String devDepartmentName) {
        this.devDepartmentName = devDepartmentName;
    }

    public String getOpsDepartmentName() {
        return opsDepartmentName;
    }

    public void setOpsDepartmentName(String opsDepartmentName) {
        this.opsDepartmentName = opsDepartmentName;
    }

    public Long getDevDepartmentId() {
        return devDepartmentId;
    }

    public void setDevDepartmentId(Long devDepartmentId) {
        this.devDepartmentId = devDepartmentId;
    }

    public Long getOpsDepartmentId() {
        return opsDepartmentId;
    }

    public void setOpsDepartmentId(Long opsDepartmentId) {
        this.opsDepartmentId = opsDepartmentId;
    }

    public List<DepartmentSubInfoRequest> getVisibilityDepartmentList() {
        return visibilityDepartmentList;
    }

    public void setVisibilityDepartmentList(List<DepartmentSubInfoRequest> visibilityDepartmentList) {
        this.visibilityDepartmentList = visibilityDepartmentList;
    }
}
