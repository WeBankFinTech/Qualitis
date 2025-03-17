package com.webank.wedatasphere.qualitis.rule.constant;

/**
 * @author v_minminghe@webank.com
 * @date 2024-11-18 14:24
 * @description 指标类别
 */
public enum MetricClassEnum {

    RULE_METRIC("rule"),
    IMS_METRIC("ims");

    private String code;

    MetricClassEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
