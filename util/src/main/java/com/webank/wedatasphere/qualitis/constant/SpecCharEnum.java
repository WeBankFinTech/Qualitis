package com.webank.wedatasphere.qualitis.constant;

/**
 * @author allenzhou
 */

public enum SpecCharEnum {
    /**
     * For spec char in columns.
     */
    RIGHT_BIG_BRACKET("}"),
    PERIOD_NO_ESCAPE("."),
    LEFT_BIG_BRACKET("{"),
    LEFT_BRACKET("["),
    LEFT_SMALL_BRACKET("("),
    RIGHT_SMALL_BRACKET(")"),
    RIGHT_BRACKET("]"),
    VERTICAL_BAR("\\|"),
    BOTTOM_BAR("_"),
    PERIOD("\\."),
    DIVIDER(";"),
    MINUS("-"),
    COMMA(","),
    COLON(":"),
    EMPTY(" "),
    LINE("\n"),
    STAR("*"),
    PERCENT("%"),
    SLASH("/"),
    EQUAL("="),
    DOLLAR("$");

    private String value;

    SpecCharEnum(String s) {
        this.value = s;
    }

    public String getValue() {
        return value;
    }

}
