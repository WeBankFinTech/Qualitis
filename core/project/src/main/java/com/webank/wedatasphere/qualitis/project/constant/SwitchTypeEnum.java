package com.webank.wedatasphere.qualitis.project.constant;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum SwitchTypeEnum {

    /**
     * Switch Type
     */
    AUTO_MATIC(true, "自动"),
    HAND_MOVEMENT(false, "手动"),
    ;

    private Boolean code;
    private String message;

    SwitchTypeEnum(Boolean code, String message) {
        this.code = code;
        this.message = message;
    }

    public Boolean getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static List<Map<String, Object>> getSwitchTypeEnumList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (SwitchTypeEnum switchTypeEnum : SwitchTypeEnum.values()) {
            Map<String, Object> item = Maps.newHashMap();
            item.put("code", switchTypeEnum.code);
            item.put("message", switchTypeEnum.message);
            list.add(item);

        }
        return list;
    }



}
