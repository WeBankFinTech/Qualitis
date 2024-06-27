package com.webank.wedatasphere.qualitis.project.constant;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum ProjectStatusEnum {

    /**
     * Type of project status 可操作、不可操作状态
     */
    OPERABLE_STATUS(0, "可操作状态"),
    INOPERABLE_STATUS(1, "不可操作状态");

    private Integer code;
    private String message;

    ProjectStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
