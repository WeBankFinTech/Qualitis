package com.webank.wedatasphere.qualitis.rule.constant;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum StandardVauleEnum {
    /**
     * level
     */
    DEFAULT_TEMPLATE(1, "内置模版"),
    DEPARTMENT_TEMPLATE(2, "部门模版"),
    PERSONAL_TEMPLATE(3, "个人模版");

    private Integer code;
    private String message;

    StandardVauleEnum(Integer code, String message) {
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
