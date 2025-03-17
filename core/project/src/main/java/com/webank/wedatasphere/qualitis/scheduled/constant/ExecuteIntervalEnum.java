package com.webank.wedatasphere.qualitis.scheduled.constant;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-13 18:08
 * @description
 */
public enum ExecuteIntervalEnum {
    /**
     * Execute Interval
     */
    HOUR("hour", "每小时"), DAY("day", "每天"), WEEK("week", "每周"), MONTH("month", "每月");

    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    ExecuteIntervalEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static ExecuteIntervalEnum fromCode(String code) {
        for (ExecuteIntervalEnum executeIntervalEnum : ExecuteIntervalEnum.values()) {
            if (executeIntervalEnum.getCode().equals(code.toLowerCase())) {
                return executeIntervalEnum;
            }
        }
        return null;
    }

}
