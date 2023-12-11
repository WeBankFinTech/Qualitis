package com.webank.wedatasphere.qualitis.rule.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum SparkParameterEnum {

    /**
     * Spark Driver 内存使用上限
     * Spark Driver CPU Cores 使用上限
     * Spark Executor 并发数
     * Spark Executor 内存数
     */
    SPARK_INTERNAL_STORAGE_UPPER_LIMIT_USE("Spark Driver 内存使用上限", "wds.linkis.rm.client.memory.max", "20G"),
    CPU_CORES_UPPER_LIMIT_USE("Spark Driver CPU Cores 使用上限", "wds.linkis.rm.client.core.max", "10"),
    EXECUTOR_COMPLICATED("Spark Executor 并发数", "spark.executor.instances", "2"),
    Executor_INTERNAL_STORAGE("Spark Executor 内存数", "spark.executor.memory", "3g");

    private String label;
    private String value;
    private String initial;

    SparkParameterEnum(String label, String value, String initial) {
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

    public static List<Map<String, Object>> getSparkParameterList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (SparkParameterEnum sparkParameterEnum : SparkParameterEnum.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("label", sparkParameterEnum.label);
            item.put("value", sparkParameterEnum.value);
            item.put("initial", sparkParameterEnum.initial);
            list.add(item);

        }
        return list;
    }

}
