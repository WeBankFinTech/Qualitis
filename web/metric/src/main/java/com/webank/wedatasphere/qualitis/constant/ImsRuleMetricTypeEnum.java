package com.webank.wedatasphere.qualitis.constant;


/**
 * @author v_wenxuanzhang
 */
public enum ImsRuleMetricTypeEnum {
    /**
     指标类型，1表示表汇总统计；2表示枚举占比统计；3表示聚合统计;4表示原值统计
     */
    TABLE_COLLECT(1, "表汇总统计"),
    ENUM_COLLECT(2,"枚举占比统计"),
    TOTAL_COLLECT(3,"聚合统计"),
    ORIGIN_COLLECT(4,"原值统计");

    private int code;
    private String describe;

    ImsRuleMetricTypeEnum(int code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    public int getCode() {
        return code;
    }
    public String getDescribe() {
        return describe;
    }

    public static ImsRuleMetricTypeEnum match(int code) {
        ImsRuleMetricTypeEnum[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            ImsRuleMetricTypeEnum item = var1[var3];
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }
}
