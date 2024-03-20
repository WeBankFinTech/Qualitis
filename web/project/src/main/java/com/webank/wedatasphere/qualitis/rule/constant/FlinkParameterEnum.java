package com.webank.wedatasphere.qualitis.rule.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum FlinkParameterEnum {

    /**
     * 预留，当前版本无参数设置
     */
    INIT_PARAMETER("", "", "");

    private String label;
    private String value;
    private String initial;


    FlinkParameterEnum(String label, String value, String initial) {
        this.label = label;
        this.value = value;
        this.initial = initial;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public String getInitial() {
        return initial;
    }

    public static List<Map<String, Object>> getFlinkParameterList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (FlinkParameterEnum flinkParameterEnum : FlinkParameterEnum.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("label", flinkParameterEnum.label);
            item.put("value", flinkParameterEnum.value);
            item.put("initial", flinkParameterEnum.initial);
            list.add(item);

        }
        return list;
    }


}
