package com.webank.wedatasphere.qualitis.rule.constant;

/**
 * @author v_minminghe@webank.com
 * @date 2023-01-13 15:38
 * @description
 */
public enum RuleLockRangeEnum {
    /**
     * 1.rule 2.table_group_datasource 3.table_group_rules
     */
    RULE("rule"),
    TABLE_GROUP_DATASOURCE("table_group_datasource"),
    TABLE_GROUP_RULES("table_group_rules");

    private String value;

    public String getValue() {
        return value;
    }

    RuleLockRangeEnum(String value) {
        this.value = value;
    }

    public static RuleLockRangeEnum fromValue(String lockRange) {
        for (RuleLockRangeEnum ruleLockRangeEnum: RuleLockRangeEnum.values()) {
            if (ruleLockRangeEnum.value.equals(lockRange)) {
                return ruleLockRangeEnum;
            }
        }
        return null;
    }
}
