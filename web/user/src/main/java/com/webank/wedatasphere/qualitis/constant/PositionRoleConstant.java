package com.webank.wedatasphere.qualitis.constant;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum PositionRoleConstant {
    /**
     * 中文名：开发，英文名：Dev
     * 中文名：测试，英文名：Test
     * 中文名：运维，英文名：Ops
     */
    DEVELOPMENT_POSITION("Dev", "开发"),
    TEST_POSITION("Test", "测试"),
    OPS_POSITION("Ops", "运维"),
    ;

    private String code;
    private String message;

    PositionRoleConstant(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static Boolean matchData(String enName) {
        for (PositionRoleConstant positionRoleConstant : PositionRoleConstant.values()) {
            if (positionRoleConstant.getCode().equals(enName)) {
                return true;
            }
        }
        return false;
    }


    public static List<Map<String, Object>> getPositionRoleEnumList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (PositionRoleConstant positionRoleConstant : PositionRoleConstant.values()) {
            Map<String, Object> item = Maps.newHashMap();
            item.put("code", positionRoleConstant.code);
            item.put("message", positionRoleConstant.message);
            list.add(item);
        }
        return list;
    }
}
