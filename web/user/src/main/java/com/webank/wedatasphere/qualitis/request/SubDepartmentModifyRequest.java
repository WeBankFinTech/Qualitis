package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constant.DepartmentSourceTypeEnum;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import org.apache.commons.lang.StringUtils;

/**
 * @author allenzhou
 */
public class SubDepartmentModifyRequest {

    @JsonProperty(value = "sub_department_id", required = true)
    private Long subDepartmentId;
    @JsonProperty(value = "department_name", required = true)
    private String departmentName;
    @JsonProperty("department_code")
    private String departmentCode;
    @JsonProperty(value = "sub_department_name", required = true)
    private String subDepartmentName;
    @JsonProperty(value = "sub_department_code", required = true)
    private String subDepartmentCode;
    @JsonProperty(value = "source_type", required = true)
    private Integer sourceType;

    public SubDepartmentModifyRequest() {
        // Default Constructor
    }

    public Long getSubDepartmentId() {
        return subDepartmentId;
    }

    public void setSubDepartmentId(Long subDepartmentId) {
        this.subDepartmentId = subDepartmentId;
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

    public static void checkRequest(SubDepartmentModifyRequest request) throws UnExpectedRequestException {
        if (request.getSubDepartmentId() == null || StringUtils.isBlank(request.getSubDepartmentId().toString())) {
            throw new UnExpectedRequestException("department id {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (StringUtils.isBlank(request.getDepartmentName())) {
            throw new UnExpectedRequestException("department name {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (StringUtils.isBlank(request.getDepartmentCode())) {
            throw new UnExpectedRequestException("department code {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (StringUtils.isBlank(request.getSubDepartmentName())) {
            throw new UnExpectedRequestException("sub department name {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (StringUtils.isBlank(request.getSubDepartmentCode())) {
            throw new UnExpectedRequestException("sub department code {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (DepartmentSourceTypeEnum.fromCode(request.getSourceType()) == null) {
            throw new UnExpectedRequestException("source type {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }

        if (!validateInteger(request.getDepartmentCode())) {
            throw new UnExpectedRequestException("Error parameter: sub_department_code must be number.");
        }

    }
    private static boolean validateInteger(String param) {
        try {
            Integer.parseInt(param);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}