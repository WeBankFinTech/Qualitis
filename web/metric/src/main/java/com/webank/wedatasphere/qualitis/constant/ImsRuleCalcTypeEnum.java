package com.webank.wedatasphere.qualitis.constant;


/**
 * @author v_wenxuanzhang
 */
public enum ImsRuleCalcTypeEnum {
    /**
     * 指标计算类型, 1表示SUM，2表示AVG，3表示MAX，4表示MIN，5表示COUNT，6表示STDDEV，7表示ORIGIN，8表示COUNT_PERCENT
     */
    SUM(1, "SUM"),
    AVG(2, "AVG"),
    MAX(3, "MAX"),
    MIN(4, "MIN"),
    COUNT(5, "COUNT"),
    STDDEV(6, "STDDEV"),
    ORIGIN(7, "ORIGIN"),
    COUNT_PERCENT(8, "COUNT_PERCENT"),
    NULL_NUM(9, "NULL_NUM"),
    NULL_RATE(10, "NULL_RATE"),
    ENUM_NUM(11, "ENUM_NUM"),
    ENUM_RATE(12, "ENUM_RATE");
    private int code;
    private String describe;

    ImsRuleCalcTypeEnum(int code, String describe) {
        this.code = code;
        this.describe = describe;
    }

    public int getCode() {
        return code;
    }

    public String getDescribe() {
        return describe;
    }

    public static ImsRuleCalcTypeEnum match(int code) {
        ImsRuleCalcTypeEnum[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            ImsRuleCalcTypeEnum item = var1[var3];
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }
}
