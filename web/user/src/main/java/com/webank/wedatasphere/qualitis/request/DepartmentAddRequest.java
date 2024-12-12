package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author allenzhou
 */
public class DepartmentAddRequest {
    @JsonProperty("department_name")
    private String departmentName;

    public DepartmentAddRequest() {
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
