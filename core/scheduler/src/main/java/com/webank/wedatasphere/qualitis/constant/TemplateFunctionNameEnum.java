package com.webank.wedatasphere.qualitis.constant;

/**
 * @author allenzhou@webank.com
 * @date 2021/9/8 11:18
 */
public enum TemplateFunctionNameEnum {

    /**
     * Template enum.
     */
    EXPECT_SQL_PASS(0L, "expectSqlPass"),
    EXPECT_FILE_SIZE_PASS(0L, "expectFileSizePass"),
    EXPECT_FILE_AMOUNT_COUNT(0L, "expectFileAmountCount"),

    EXPECT_COLUMN_NOT_NULL(1L, "expectColumnNotNull"),
    EXPECT_COLUMNS_PRIMARY_NOT_REPEAT(2L, "expectColumnsPrimaryNotRepeat"),
    EXPECT_TABLE_ROWS(3L, "expectTablesRows"),
    EXPECT_COLUMNS_AVG(4L, "expectColumnAvg"),
    EXPECT_COLUMNS_SUM(5L, "expectColumnSum"),
    EXPECT_COLUMNS_MAX(6L, "expectColumnMax"),
    EXPECT_COLUMNS_MIN(7L, "expectColumnMin"),
    EXPECT_COLUMNS_MATCH_REGX(8L, "expectColumnMatchRegx"),
    EXPECT_COLUMNS_MATCH_DATE(9L, "expectColumnMatchDate"),
    EXPECT_COLUMNS_MATCH_NUM(10L, "expectColumnMatchNum"),
    EXPECT_COLUMNS_IN_LIST(11L, "expectColumnInList"),
    EXPECT_COLUMNS_IN_RANGE(12L, "expectColumnInRange"),
    EXPECT_COLUMNS_MATCH_ID_CARD(13L, "expectColumnMatchIDCard"),
    EXPECT_COLUMNS_LOGIC_CHECK(14L, "expectColumnLogicCheck"),
    EXPECT_COLUMNS_NOT_EMPTY(15L, "expectColumnNotEmpty"),
    EXPECT_COLUMNS_NOT_NULL_NOT_EMPTY(16L, "expectColumnNotNullNotEmpty"),
    EXPECT_SPECIFIED_COLUMN_CONSISTENT(17L, "expectSpecifiedColumnConsistent"),
    EXPECT_JOIN_SQL_PASS(19L, "expectJoinTableSqlPass"),
    EXPECT_TABLE_CONSISTENT(20L, "expectTableConsistent"),
    EXPECT_LINES_NOT_REPEAT(2149L, "expectLinesNotRepeat"),
    ;


    private Long code;
    private String name;

    TemplateFunctionNameEnum(Long code, String name) {
        this.code = code;
        this.name = name;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
