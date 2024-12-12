package com.webank.wedatasphere.qualitis.rule.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum DataSelectMethodEnum {
    /**
     * 按天选择
     * 按交易日选择
     * 按节假日选择
     */
    ACCORDING_DAYS(1, "按天选择"),
    ACCORDING_SECURITIES_WORK_DAYS(2, "按银行交易日选择"),
    ACCORDING_BANK_WORK_DAYS(3, "按证券交易日选择"),
    ACCORDING_HOLIDAYS(4, "按节假日选择"),
    ACCORDING_WEEKS(5, "按周六日选择");

    private Integer code;
    private String message;

    DataSelectMethodEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static List<Map<String, Object>> getDataSelectMethodList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (DataSelectMethodEnum dataSelectMethodEnum : DataSelectMethodEnum.values()) {
            Map<String, Object> item = new HashMap<>();
            item.put("code", dataSelectMethodEnum.code);
            item.put("message", dataSelectMethodEnum.message);
            list.add(item);

        }
        return list;
    }


}
