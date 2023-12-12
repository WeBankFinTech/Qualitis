package com.webank.wedatasphere.qualitis.rule.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum NoiseStrategyEnum {

    /**
     * 只告警不阻断、不告警不阻断
     */
    ALARM_ONLY_NO_BLOCKING(1, "只告警不阻断"),
    NO_ALARM_NO_BLOCKING(2, "不告警不阻断");

    private Integer code;
    private String message;

    NoiseStrategyEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static List<Map<String, Object>> getNoiseStrategyEnumList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (NoiseStrategyEnum noiseStrategyEnum : NoiseStrategyEnum.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("code", noiseStrategyEnum.code);
            item.put("message", noiseStrategyEnum.message);
            list.add(item);

        }
        return list;
    }

    public static String getNoiseStrategyMessage(Integer code) {
        for (NoiseStrategyEnum noiseStrategyEnum : NoiseStrategyEnum.values()) {
            if (noiseStrategyEnum.getCode().equals(code)) {
                return noiseStrategyEnum.getMessage();
            }
        }
        return null;
    }


}
