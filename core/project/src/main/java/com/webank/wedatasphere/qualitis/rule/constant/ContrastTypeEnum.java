package com.webank.wedatasphere.qualitis.rule.constant;

import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum ContrastTypeEnum {

    /**
     * 单向 (分析右表与左表不一致的数据，left join)，双向（分析两边不一致的数据，full outer join）
     * Constrast Type
     */
    LEFT_JOIN(1, "分析右表与左表不一致的数据", "LEFT JOIN"),
    FULL_OUTER_JOIN(2, "分析两边不一致的数据", "FULL OUTER JOIN");

    private Integer code;
    private String message;
    private String joinType;

    ContrastTypeEnum(Integer code, String message, String joinType) {
        this.code = code;
        this.message = message;
        this.joinType = joinType;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getJoinType() {
        return joinType;
    }

    public static String getJoinType(Integer code) {
        if (code == null) {
            return ContrastTypeEnum.LEFT_JOIN.getJoinType();
        }
        for (ContrastTypeEnum contrastTypeEnum : ContrastTypeEnum.values()) {
            if (contrastTypeEnum.getCode().equals(code)) {
                return contrastTypeEnum.getJoinType();
            }
        }
        return ContrastTypeEnum.LEFT_JOIN.getJoinType();
    }

    public static Integer getCode(String joinType) {
        if (StringUtils.isEmpty(joinType)) {
            return ContrastTypeEnum.LEFT_JOIN.getCode();
        }

        for (ContrastTypeEnum contrastTypeEnum : ContrastTypeEnum.values()) {
            if (contrastTypeEnum.getJoinType().equals(joinType.toUpperCase())) {
                return contrastTypeEnum.getCode();
            }
        }

        return ContrastTypeEnum.LEFT_JOIN.getCode();
    }

    public static List<Map<String, Object>> getConstrastEnumList() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (ContrastTypeEnum contrastTypeEnum : ContrastTypeEnum.values()) {
            Map<String, Object> item = Maps.newHashMap();
            item.put("code", contrastTypeEnum.code);
            item.put("message", contrastTypeEnum.message);
            item.put("join_type", contrastTypeEnum.joinType);
            list.add(item);

        }
        return list;
    }


}
