package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.entity.Department;

/**
 * @author allenzhou
 */
public class DepartmentResponse {
    @JsonProperty("department_id")
    private Long departmentId;
    @JsonProperty("department_name")
    private String departmentName;

    public DepartmentResponse() {
    }

    public DepartmentResponse(Department department) {
        this.departmentId = department.getId();
        this.departmentName = department.getName();
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
}
