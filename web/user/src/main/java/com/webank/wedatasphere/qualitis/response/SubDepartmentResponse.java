package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.entity.Department;

/**
 * @author v_minminghe@webank.com
 * @date 2023-06-14 10:27
 * @description
 */
public class SubDepartmentResponse extends BaseResponse {
    @JsonProperty("sub_department_id")
    private Long departmentId;
    @JsonProperty("department_name")
    private String departmentName;
    @JsonProperty("department_code")
    private String departmentCode;
    @JsonProperty("sub_department_name")
    private String subDepartmentName;
    @JsonProperty("sub_department_code")
    private String subDepartmentCode;
    @JsonProperty("tenant_user_name")
    private String tenantUserName;
    @JsonProperty("source_type")
    private Integer sourceType;

    public SubDepartmentResponse() {
    }

    public SubDepartmentResponse(Department department) {
        this.departmentId = department.getId();
        this.subDepartmentName = department.getName();
        this.subDepartmentCode = department.getDepartmentCode();
        if (department.getTenantUser() != null) {
            this.tenantUserName = department.getTenantUser().getTenantName();
        }
        this.sourceType = department.getSourceType();
        this.createTime = department.getCreateTime();
        this.createUser = department.getCreateUser();
        this.modifyUser = department.getModifyUser();
        this.modifyTime = department.getModifyTime();
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public String getTenantUserName() {
        return tenantUserName;
    }

    public void setTenantUserName(String tenantUserName) {
        this.tenantUserName = tenantUserName;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getSubDepartmentName() {
        return subDepartmentName;
    }

    public void setSubDepartmentName(String subDepartmentName) {
        this.subDepartmentName = subDepartmentName;
    }

    public String getSubDepartmentCode() {
        return subDepartmentCode;
    }

    public void setSubDepartmentCode(String subDepartmentCode) {
        this.subDepartmentCode = subDepartmentCode;
    }
}
