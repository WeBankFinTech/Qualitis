package com.webank.wedatasphere.qualitis.constant;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum RoleTypeEnum {

    /**
     * Role Type
     */
    SYSTEM_ROLE(1, "系统角色"),
    POSITION_ROLE(2, "职位角色"),
    ;

    private Integer code;
    private String message;

    RoleTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static List<Map<String, Object>> getRoleTypeEnumList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (RoleTypeEnum roleTypeEnum : RoleTypeEnum.values()) {
            Map<String, Object> item = Maps.newHashMap();
            item.put("code", roleTypeEnum.code);
            item.put("message", roleTypeEnum.message);
            list.add(item);

        }
        return list;
    }


}
