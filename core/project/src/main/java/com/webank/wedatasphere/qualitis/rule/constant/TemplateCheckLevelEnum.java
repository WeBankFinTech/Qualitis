package com.webank.wedatasphere.qualitis.rule.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum TemplateCheckLevelEnum {

    /**
     * 表级,字段级
     */
    TABLE_LEVEL(1, "表级"),
    FIELD_LEVEL(2, "字段级"),
    ;

    private Integer code;
    private String message;

    TemplateCheckLevelEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static List<Map<String, Object>> getTemplateCheckLevelList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (TemplateCheckLevelEnum templateCheckLevelEnum : TemplateCheckLevelEnum.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("code", templateCheckLevelEnum.code);
            item.put("message", templateCheckLevelEnum.message);
            list.add(item);

        }
        return list;
    }

    public static String getMessage(Integer code) {
        for (TemplateCheckLevelEnum templateCheckLevelEnum : TemplateCheckLevelEnum.values()) {
            if (templateCheckLevelEnum.getCode().equals(code)) {
                return templateCheckLevelEnum.getMessage();
            }
        }
        return "";
    }


}
