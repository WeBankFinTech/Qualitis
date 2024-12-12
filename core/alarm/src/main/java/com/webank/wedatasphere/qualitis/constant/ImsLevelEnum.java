package com.webank.wedatasphere.qualitis.constant;

/**
 * @author allenzhou
 */
public enum ImsLevelEnum {
    /**
     * ims告警级别
     * 1CRITICAL 2MAJOR 3MINOR 4WARNING 5INFO
     */
    CRITICAL("1"),
    MAJOR("2"),
    MINOR("3"),
    WARNING("4"),
    INFO("5");

    private String code;

    ImsLevelEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
