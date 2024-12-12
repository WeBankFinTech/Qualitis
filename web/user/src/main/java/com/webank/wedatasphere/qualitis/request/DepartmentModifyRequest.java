package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constant.DepartmentSourceTypeEnum;
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
    @JsonProperty("department_code")
    private String departmentCode;
    @JsonProperty("source_type")
    private Integer sourceType;

    public DepartmentModifyRequest() {
        // Default Constructor
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
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

    public static void checkRequest(DepartmentModifyRequest request) throws UnExpectedRequestException {
        if (request.getDepartmentId() == null || StringUtils.isBlank(request.getDepartmentId().toString())) {
            throw new UnExpectedRequestException("department id {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (StringUtils.isBlank(request.getDepartmentName())) {
            throw new UnExpectedRequestException("department name {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (StringUtils.isBlank(request.getDepartmentCode())) {
            throw new UnExpectedRequestException("department code {&CAN_NOT_BE_NULL_OR_EMPTY}");
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