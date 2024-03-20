package com.webank.wedatasphere.qualitis.rule.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum StandardApproveEnum {
    /**
     * 数据地图, ITSM， 其他
     */
    DATA_MAP(1, "数据地图"),
    ITSM(2, "ITSM"),
    OTHER(3, "其他"),
    ;

    private Integer code;
    private String message;

    StandardApproveEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static List<Map<String, Object>> getStandardEnumList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (StandardApproveEnum standardApproveEnum : StandardApproveEnum.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("code", standardApproveEnum.code);
            item.put("message", standardApproveEnum.message);
            list.add(item);

        }
        return list;
    }

}
