package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
//import com.webank.wedatasphere.qualitis.entity.CalcuUnit;
//import com.webank.wedatasphere.qualitis.request.DepartmentSubInfoRequest;
//import com.webank.wedatasphere.qualitis.rule.entity.Template;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2024-05-13 16:18
 * @description
 */
public class MetricTemplateDetailResponse {

    @JsonProperty(value = "en_name", required = true)
    private String enName;
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
    private List<DepartmentSubInfoResponse> visibilityDepartmentList;

//    public MetricTemplateDetailResponse(Template template, CalcuUnit calcuUnit) {
//        this.enName = template.getEnName();
//        this.cnName = template.getName();
//        this.description = template.getDescription();
//        this.udfId = calcuUnit.getUdfId();
//        this.sqlAction = calcuUnit.getSqlAction();
//        this.devDepartmentName = template.getDevDepartmentName();
//        this.devDepartmentId = template.getDevDepartmentId();
//        this.opsDepartmentName = template.getOpsDepartmentName();
//        this.opsDepartmentId = template.getOpsDepartmentId();
//
//    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
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

    public List<DepartmentSubInfoResponse> getVisibilityDepartmentList() {
        return visibilityDepartmentList;
    }

    public void setVisibilityDepartmentList(List<DepartmentSubInfoResponse> visibilityDepartmentList) {
        this.visibilityDepartmentList = visibilityDepartmentList;
    }
}
