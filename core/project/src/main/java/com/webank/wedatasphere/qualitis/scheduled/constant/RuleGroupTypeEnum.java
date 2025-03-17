package com.webank.wedatasphere.qualitis.scheduled.constant;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum RuleGroupTypeEnum {
    /**
     * Rule Group Type
     */
    FRONT_RULE_GROUP(1, "FRONT_RULE_GROUP"),
    BACK_RULE_GROUP(2, "BACK_RULE_GROUP");

    private Integer code;
    private String message;

    RuleGroupTypeEnum(Integer code, String message) {
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
