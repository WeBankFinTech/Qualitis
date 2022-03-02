package com.webank.wedatasphere.qualitis.metadata.constant;

/**
 * @author allenzhou
 */

public enum RuleConstraintEnum {
    /**
     * For spec case solution.
     * CUSTOM_DATABASE_PREFIS: for custom rule configuration in context service when has no database.
     */
    CUSTOM_DATABASE_PREFIS("linkis_cs_tmp_db"),
    DEFAULT_NODENAME("qualitis_node");

    private String value;
    RuleConstraintEnum(String str) {
        this.value = str;
    }

    public String getValue() {
        return value;
    }
}
