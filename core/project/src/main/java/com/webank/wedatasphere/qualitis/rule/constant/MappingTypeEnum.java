package com.webank.wedatasphere.qualitis.rule.constant;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum MappingTypeEnum {

    /**
     * Mapping type, including Mapping using CONNECT and  MATCHING FIELDS
     */
    CONNECT_FIELDS(1, "连接字段"),
    MATCHING_FIELDS(2, "对比字段")
    ;

    private Integer code;
    private String message;

    MappingTypeEnum(Integer code, String message) {
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
