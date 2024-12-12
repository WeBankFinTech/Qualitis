package com.webank.wedatasphere.qualitis.rule.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum TemplateCheckTypeEnum {

    /**
     * 固定值
     */
    Fixed_value(1, "固定值"),
    ;

    private Integer code;
    private String message;

    TemplateCheckTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static List<Map<String, Object>> getTemplateCheckTypeList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (TemplateCheckTypeEnum templateCheckTypeEnum : TemplateCheckTypeEnum.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("code", templateCheckTypeEnum.code);
            item.put("message", templateCheckTypeEnum.message);
            list.add(item);

        }
        return list;
    }

    public static String getMessage(Integer code) {
        for (TemplateCheckTypeEnum templateCheckTypeEnum : TemplateCheckTypeEnum.values()) {
            if (templateCheckTypeEnum.getCode().equals(code)) {
                return templateCheckTypeEnum.getMessage();
            }
        }
        return "";
    }


}
