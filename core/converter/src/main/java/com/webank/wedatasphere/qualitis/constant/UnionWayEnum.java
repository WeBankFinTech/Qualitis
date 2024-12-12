package com.webank.wedatasphere.qualitis.constant;

/**
 * @author v_minminghe@webank.com
 * @date 2024-01-26 10:07
 * @description
 */
public enum UnionWayEnum {

    /**
     * 两边数量一致的n个环境一一比对，产生n个结果
     */
    NO_COLLECT_CALCULATE(0, "无聚合计算"),
    /**
     * 两边数量一致的n个环境一一比对，聚合成1个结果
     */
    COLLECT_AFTER_CALCULATE(1, "先计算再聚合"),
    /**
     * 两边的n个环境各自聚合后，进行一次比对，产生1个结果
     */
    CALCULATE_AFTER_COLLECT(2, "先聚合再计算");

    private Integer code;
    private String message;

    UnionWayEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static UnionWayEnum fromCode(Integer code) {
        for (UnionWayEnum unionWayEnum: UnionWayEnum.values()) {
            if (unionWayEnum.code.equals(code)) {
                return unionWayEnum;
            }
        }
        return UnionWayEnum.NO_COLLECT_CALCULATE;
    }

}
