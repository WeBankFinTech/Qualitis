package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.entity.Department;

/**
 * @author allenzhou
 */
public class DepartmentResponse extends BaseResponse {
    @JsonProperty("department_id")
    private Long departmentId;
    @JsonProperty("department_name")
    private String departmentName;
    @JsonProperty("department_code")
    private String departmentCode;
    @JsonProperty("tenant_user_name")
    private String tenantUserName;
    @JsonProperty("source_type")
    private Integer sourceType;
    private String disable;
    public DepartmentResponse(){
//        Do nothing
    }
    public DepartmentResponse(Department department) {
        this.departmentId = department.getId();
        this.departmentName = department.getName();
        this.departmentCode = department.getDepartmentCode();
        if (department.getTenantUser() != null) {
            this.tenantUserName = department.getTenantUser().getTenantName();
        }
        this.sourceType = department.getSourceType();
        this.createTime = department.getCreateTime();
        this.createUser = department.getCreateUser();
        this.modifyUser = department.getModifyUser();
        this.modifyTime = department.getModifyTime();
    }

    public String getDisable() {
        return disable;
    }

    public void setDisable(String disable) {
        this.disable = disable;
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

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }
}
