package com.webank.wedatasphere.qualitis.constant;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum PositionRoleEnum {
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

    PositionRoleEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static Optional<PositionRoleEnum> fromCode(String code) {
        for (PositionRoleEnum positionRoleEnum : PositionRoleEnum.values()) {
            if (positionRoleEnum.getCode().toLowerCase().equals(code.toLowerCase())) {
                return Optional.of(positionRoleEnum);
            }
        }
        return Optional.empty();
    }

    public static Optional<PositionRoleEnum> fromMessage(String name) {
        for (PositionRoleEnum positionRoleEnum : PositionRoleEnum.values()) {
            if (positionRoleEnum.getMessage().toLowerCase().equals(name.toLowerCase())) {
                return Optional.of(positionRoleEnum);
            }
        }
        return Optional.empty();
    }


    public static List<Map<String, Object>> getPositionRoleEnumList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (PositionRoleEnum positionRoleEnum : PositionRoleEnum.values()) {
            Map<String, Object> item = Maps.newHashMap();
            item.put("code", positionRoleEnum.code);
            item.put("message", positionRoleEnum.message);
            list.add(item);
        }
        return list;
    }
}
