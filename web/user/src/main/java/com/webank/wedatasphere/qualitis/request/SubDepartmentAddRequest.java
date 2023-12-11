package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constant.DepartmentSourceTypeEnum;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import org.apache.commons.lang.StringUtils;

/**
 * @author allenzhou
 */
public class SubDepartmentAddRequest {
    @JsonProperty("department_name")
    private String departmentName;
    @JsonProperty("department_code")
    private String departmentCode;
    @JsonProperty("sub_department_name")
    private String subDepartmentName;
    @JsonProperty("sub_department_code")
    private String subDepartmentCode;
    @JsonProperty("source_type")
    private Integer sourceType;

    public SubDepartmentAddRequest() {
        // Default Constructor
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

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
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

    public static void checkRequest(SubDepartmentAddRequest request) throws UnExpectedRequestException {
        if (null == request) {
            throw new UnExpectedRequestException("request body {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (StringUtils.isBlank(request.getDepartmentName())) {
            throw new UnExpectedRequestException("department_name {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (StringUtils.isBlank(request.getDepartmentCode())) {
            throw new UnExpectedRequestException("department_code {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (StringUtils.isBlank(request.getSubDepartmentName())) {
            throw new UnExpectedRequestException("sub department name {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (StringUtils.isBlank(request.getSubDepartmentCode())) {
            throw new UnExpectedRequestException("sub department code {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (DepartmentSourceTypeEnum.fromCode(request.getSourceType()) == null) {
            throw new UnExpectedRequestException("source_type {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }


}
