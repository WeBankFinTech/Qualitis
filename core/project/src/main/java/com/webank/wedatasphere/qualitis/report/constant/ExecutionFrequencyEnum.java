package com.webank.wedatasphere.qualitis.report.constant;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum ExecutionFrequencyEnum {
    /**
     * DAILY,WEEKLY
     */
    DAILY(1, "每天","0 0 * * *"),
    WEEKLY(2, "每周","0 0 * * 0");

    private Integer code;
    private String message;
    private String cronExpressions;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getCronExpressions() {
        return cronExpressions;
    }

    ExecutionFrequencyEnum(Integer code, String message, String cronExpressions) {
        this.code = code;
        this.message = message;
        this.cronExpressions = cronExpressions;
    }

    public static String getExecutionFrequencyName(Integer code) {
        for (ExecutionFrequencyEnum c : ExecutionFrequencyEnum.values()) {
            if (c.getCode().equals(code)) {
                return c.getMessage();
            }
        }
        return null;
    }

    public static List<Map<String, Object>> getExecutionFrequencyEnumList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (ExecutionFrequencyEnum executionFrequencyEnum : ExecutionFrequencyEnum.values()) {
            Map<String, Object> item = Maps.newHashMap();
            item.put("code", executionFrequencyEnum.code);
            item.put("message", executionFrequencyEnum.message);
            list.add(item);

        }
        return list;
    }


}
