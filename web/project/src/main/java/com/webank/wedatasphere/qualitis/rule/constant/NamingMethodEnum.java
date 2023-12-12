package com.webank.wedatasphere.qualitis.rule.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum NamingMethodEnum {
    /**
     * 1 规范命名
     * 2 自定义命名
     */
    NORMATIVE_NAMING(1, "规范命名"),
    CUSTOM_NAMING(2, "自定义命名");

    private Integer code;
    private String message;

    NamingMethodEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static List<Map<String, Object>> getNamingMethodList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (NamingMethodEnum namingMethodEnum : NamingMethodEnum.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("code", namingMethodEnum.code);
            item.put("message", namingMethodEnum.message);
            list.add(item);

        }
        return list;
    }


}
