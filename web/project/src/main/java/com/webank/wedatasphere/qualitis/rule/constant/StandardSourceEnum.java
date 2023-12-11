package com.webank.wedatasphere.qualitis.rule.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum StandardSourceEnum {
    /**
     * 标准值来源枚举
     */
    CUSTOM_SOURCE(1, "自定义"),
    DATA_SHAPIS(2, "DataShapis"),
    ;

    private Integer code;
    private String message;

    StandardSourceEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static List<Map<String, Object>> getStandardSourceList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (StandardSourceEnum standardSourceEnum : StandardSourceEnum.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("code", standardSourceEnum.code);
            item.put("message", standardSourceEnum.message);
            list.add(item);

        }
        return list;
    }

    public static String getStandardSourceByCode(Integer code) {
        for (StandardSourceEnum e : StandardSourceEnum.values()) {
            if (code.equals(e.getCode())) {
                return e.getMessage();
            }
        }
        return null;
    }


}
