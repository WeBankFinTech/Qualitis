package com.webank.wedatasphere.qualitis.rule.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum StatisticalFunctionEnum {

    /**
     *  max count avg min sum
     *
     */
    MOST_VALUE("max", "最大值"),
    NUMERICAL_VALUE("count", "数值"),
    AVERAGE_VALUE("avg", "平均值"),
    LEAST_VALUE("min", "最小值"),
    ACCUMULATION_VALUE("sum", "累加值"),
    ;

    private String code;
    private String message;

    StatisticalFunctionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static List<Map<String, Object>> getStatisticalFunctionList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (StatisticalFunctionEnum statisticalFunctionEnum : StatisticalFunctionEnum.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("code", statisticalFunctionEnum.code);
            item.put("message", statisticalFunctionEnum.message);
            list.add(item);

        }
        return list;
    }

}
