package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import org.apache.commons.lang.StringUtils;

/**
 * @author allenzhou
 */
public class DepartmentModifyRequest {

    @JsonProperty("department_id")
    private Long departmentId;
    @JsonProperty("department_name")
    private String departmentName;

    public DepartmentModifyRequest() {
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

    public void checkRequest() throws UnExpectedRequestException {
        if (departmentId == null || StringUtils.isBlank(departmentId.toString())) {
            throw new UnExpectedRequestException("department id {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (StringUtils.isBlank(departmentName)) {
            throw new UnExpectedRequestException("department name {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }
}