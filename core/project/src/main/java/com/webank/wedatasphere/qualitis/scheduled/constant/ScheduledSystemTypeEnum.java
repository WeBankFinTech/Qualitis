package com.webank.wedatasphere.qualitis.scheduled.constant;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum ScheduledSystemTypeEnum {

    /**
     * Scheduled Type
     */
    WTSS(1, "WTSS"),
    ;

    private Integer code;
    private String message;

    ScheduledSystemTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static List<Map<String, Object>> getScheduledEnumList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (ScheduledSystemTypeEnum scheduledSystemTypeEnum : ScheduledSystemTypeEnum.values()) {
            Map<String, Object> item = Maps.newHashMap();
            item.put("code", scheduledSystemTypeEnum.code);
            item.put("message", scheduledSystemTypeEnum.message);
            list.add(item);

        }
        return list;
    }


}
