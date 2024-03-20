package com.webank.wedatasphere.qualitis.rule.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum StaticArgumentsTypeEnum {

    /**
     *  内置参数，自定义参数
     */
    BUILT_ARGUMENTS(1, "内置变量"),
    CUSTOM_ARGUMENTS(2, "自定义变量"),
    ;

    private Integer code;
    private String message;

    StaticArgumentsTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static List<Map<String, Object>> getStaticArgumentsTypeList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (StaticArgumentsTypeEnum staticArgumentsTypeEnum : StaticArgumentsTypeEnum.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("code", staticArgumentsTypeEnum.code);
            item.put("message", staticArgumentsTypeEnum.message);
            list.add(item);

        }
        return list;
    }


}
