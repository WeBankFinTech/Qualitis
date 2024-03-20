package com.webank.wedatasphere.qualitis.rule.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum YarnParameterEnum {

    /**
     * YARN 队列名
     * YARN 队列实例最大个数
     * YARN 队列 CPU 使用上限
     * YARN 队列内存使用上限
     */
    QUEUE_NAME("YARN 队列名", "wds.linkis.rm.yarnqueue", ""),
    MAXIMUM_QUEUE_INSTANCES("YARN 队列实例最大个数", "wds.linkis.rm.yarnqueue.instance.max", "30"),
    CPU_UPPER_LIMIT_USE("YARN 队列CPU使用上限", "wds.linkis.rm.yarnqueue.cores.max", "150"),
    INTERNAL_STORAGE_UPPER_LIMIT_USE("YARN 队列内存使用上限", "wds.linkis.rm.yarnqueue.memory.max", "300G");

    private String label;
    private String value;
    private String initial;

    YarnParameterEnum(String label, String value, String initial) {
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

    public static List<Map<String, Object>> getYarnParameterList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (YarnParameterEnum yarnParameterEnum : YarnParameterEnum.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("label", yarnParameterEnum.label);
            item.put("value", yarnParameterEnum.value);
            item.put("initial", yarnParameterEnum.initial);
            list.add(item);

        }
        return list;
    }
}
