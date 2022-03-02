package com.webank.wedatasphere.qualitis.constant;

/**
 * @author allenzhou
 */

public enum SpecCharEnum {
    /**
     * For spec char in columns.
     */
    LEFT_BRACKET("["),
    RIGHT_BRACKET("]"),
    VERTICAL_BAR("\\|"),
    BOTTOM_BAR("_"),
    DIVIDER(";"),
    PERIOD("\\."),
    EQUAL("="),
    MINUS("-"),
    COMMA(","),
    COLON(":"),
    EMPTY(" "),
    STAR("*");

    private String value;

    SpecCharEnum(String s) {
        this.value = s;
    }

    public String getValue() {
        return value;
    }

}
