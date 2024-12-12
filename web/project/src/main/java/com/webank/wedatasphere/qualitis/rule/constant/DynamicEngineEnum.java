package com.webank.wedatasphere.qualitis.rule.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum DynamicEngineEnum {
    /**
     * 动态引擎枚举
     */
    YARN_ARGUMENTS(1, "YARN 参数"),
    SPARK_ARGUMENTS(2, "Spark 参数"),
    FLINK_ARGUMENTS(3, "Flink 参数"),
    CUSTOM_ARGUMENTS(4, "自定义参数"),
    ;

    private Integer code;
    private String message;

    DynamicEngineEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static List<Map<String, Object>> getDynamicEngineList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (DynamicEngineEnum dynamicEngineEnum : DynamicEngineEnum.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("code", dynamicEngineEnum.code);
            item.put("message", dynamicEngineEnum.message);
            list.add(item);

        }
        return list;
    }

}
