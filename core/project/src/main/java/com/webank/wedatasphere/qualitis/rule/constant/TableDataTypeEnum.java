package com.webank.wedatasphere.qualitis.rule.constant;

/**
 * @author v_minminghe@webank.com
 * @date 2022-09-14 10:21
 * @description
 */
public enum TableDataTypeEnum {
    /**
     * ruleMetric 指标管理
     * standardValue 标准值
     * ruleTemplate 规则模板
     * linkisUdf Linkis UDF
     * linkisDataSource Linkis数据源
     */
    RULE_METRIC("ruleMetric", "指标管理"),
    STANDARD_VALUE("standardValue", "标准值"),
    RULE_TEMPLATE("ruleTemplate", "规则模板"),
    LINKIS_UDF("linkisUdf", "Linkis UDF"),
    LINKIS_DATA_SOURCE("linkisDataSource", "Linkis数据源");

    private String code;
    private String desc;

    TableDataTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
