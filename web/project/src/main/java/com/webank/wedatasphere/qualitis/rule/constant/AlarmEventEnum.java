package com.webank.wedatasphere.qualitis.rule.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum AlarmEventEnum {

    /**
     *  a.校验成功 b.校验失败 ->(初始化、校验不通过、阻断) c.执行完成 (校验通过或校验不通过)   阻断-> 校验不通过
     */
    CHECK_SUCCESS(1, "校验成功"),
    CHECK_FAILURE(2, "校验失败"),
    EXECUTION_COMPLETED(3, "执行完成"),
    ;

    private Integer code;
    private String message;

    AlarmEventEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static List<Map<String, Object>> getAlarmEventEnumList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (AlarmEventEnum alarmEventEnum : AlarmEventEnum.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("code", alarmEventEnum.code);
            item.put("message", alarmEventEnum.message);
            list.add(item);

        }
        return list;
    }


}
