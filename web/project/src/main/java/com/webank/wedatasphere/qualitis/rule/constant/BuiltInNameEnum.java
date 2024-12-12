package com.webank.wedatasphere.qualitis.rule.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum BuiltInNameEnum {
    /**
     * fps_id
     * fps_hash
     * run_date
     * partition
     */
    FPS_ID("fps_id"),
    FPS_HASH("fps_hash"),
    RUN_DATE("run_date"),
    RUN_TODAY("run_today"),
    PARTITION("partition"),
    ENV_NAME("env_names");

    private String value;

    public String getValue() {
        return value;
    }

    BuiltInNameEnum(String value) {
        this.value = value;
    }

    public static List<String> getBuiltInNameList() {
        List<String> list = new ArrayList<String>();
        for (BuiltInNameEnum builtInNameEnum : BuiltInNameEnum.values()) {
            list.add(builtInNameEnum.value);
        }
        return list;
    }
}
