package com.webank.wedatasphere.qualitis.constant;

/**
 * @author allenzhou@webank.com
 * @date 2021/9/8 11:18
 */
public enum TemplateFunctionNameEnum {

    /**
     * Template enum.
     */
    EXPECT_SQL_PASS(0L, "expectSqlPass", "", "CUSTOM_SQL"),
    EXPECT_FILE_SIZE_PASS(0L, "expectFileSizePass", "Directory Capacity",""),
    EXPECT_FILE_AMOUNT_COUNT(0L, "expectFileAmountCount", "",""),

    EXPECT_COLUMN_NOT_NULL(1L, "expectColumnNotNull", "Null Verification", "CNA"),
    EXPECT_COLUMNS_PRIMARY_NOT_REPEAT(2L, "expectColumnsPrimaryNotRepeat", "Primary Key Verification","CPK"),
    EXPECT_TABLE_ROWS(3L, "expectTablesRows", "Row Number Verification","TC"),
    EXPECT_COLUMNS_AVG(4L, "expectColumnAvg", "Average Value Verification","AVGCK"),
    EXPECT_COLUMNS_SUM(5L, "expectColumnSum", "Sum Value Verification","SUMCK"),
    EXPECT_COLUMNS_MAX(6L, "expectColumnMax", "Max Value Verification","MAXCK"),
    EXPECT_COLUMNS_MIN(7L, "expectColumnMin", "Min Value Verification","MINCK"),
    EXPECT_COLUMNS_MATCH_REGX(8L, "expectColumnMatchRegx", "Regexp Expression Verification","CFT"),
    EXPECT_COLUMNS_MATCH_DATE(9L, "expectColumnMatchDate", "Date Format Verification","CFT"),
    EXPECT_COLUMNS_MATCH_NUM(10L, "expectColumnMatchNum", "Number Format Verification","PR"),
    EXPECT_COLUMNS_IN_LIST(11L, "expectColumnInList", "Enum Value Verification","CVD"),
    EXPECT_COLUMNS_IN_RANGE(12L, "expectColumnInRange", "Number Range Verification","CLR"),
    EXPECT_COLUMNS_MATCH_ID_CARD(13L, "expectColumnMatchIDCard", "ID card NO Verification","CFT"),
    EXPECT_COLUMNS_LOGIC_CHECK(14L, "expectColumnLogicCheck", "Logic Verification","CUSTOM_SQL"),
    EXPECT_COLUMNS_NOT_EMPTY(15L, "expectColumnNotEmpty", "Empty String Verification", "CNA"),
    EXPECT_COLUMNS_NOT_NULL_NOT_EMPTY(16L, "expectColumnNotNullNotEmpty", "Null And Empty String Verification", "CNA"),
    EXPECT_SPECIFIED_COLUMN_CONSISTENT(17L, "expectSpecifiedColumnConsistent", "Field consistency check", "EC"),
    EXPECT_JOIN_SQL_PASS(19L, "expectJoinTableSqlPass", "", "SC"),
    EXPECT_TABLE_CONSISTENT(20L, "expectTableConsistent", "Row data consistency check", "EC"),
    EXPECT_LINES_NOT_REPEAT(2149L, "expectLinesNotRepeat", "Primary Line Verification", "CUSTOM_SQL"),
    EXPECT_COLUMNS_IN_LIST_NEW_VALUE(2853L, "expectColumnInListNewValueCheck", "Enumerate new value monitoring","CVD"),
    EXPECT_COLUMNS_IN_RANGE_NEW_VALUE(2860L, "expectColumnInRangeNewValueCheck", "Numerical range monitoring","CVD"),
    EXPECT_DATA_NOT_REPEAT(4000L, "expectDataNotRepeat", "Repeat data check", "CUSTOM_SQL"),
    EXPECT_CUSTOM_COLUMN_CONSISTENT_MULTI_CLUSTER(4200L, "expectCustomColumnConsistent", "Multi cluster custom field consistency check", "SC"),
    EXPECT_CUSTOM_COLUMN_CONSISTENT_SINGLE_CLUSTER(4201L, "expectCustomColumnConsistent", "Single cluster custom field consistency check", "SC"),
    EXPECT_TABLE_ROWS_CONSISTENT_MULTI_CLUSTER(4202L, "expectTableRowsConsistent", "Multi table rows consistensy", "CUSTOM_SQL"),
    EXPECT_TABLE_ROWS_CONSISTENT_SINGLE_CLUSTER(4203L, "expectTableRowsConsistent", "Single table rows consistensy", "CUSTOM_SQL"),
    EXPECT_TABLE_STRUCTURE_CONSISTENT_MULTI_CLUSTER(4215L, "expectTableStructureConsistent", "Multi cluster table structure consistency", "CUSTOM_SQL"),
    EXPECT_TABLE_STRUCTURE_CONSISTENT_SINGLE_CLUSTER(4216L, "expectTableStructureConsistent", "Single cluster table structure consistency", "CUSTOM_SQL");

    private Long code;
    private String name;
    private String enName;
    private String dgsmRuleType;

    TemplateFunctionNameEnum(Long code, String name, String enName, String dgsmRuleType) {
        this.code = code;
        this.name = name;
        this.enName = enName;
        this.dgsmRuleType = dgsmRuleType;
    }

    public Long getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getEnName() {
        return enName;
    }

    public String getDgsmRuleType() {
        return dgsmRuleType;
    }

    public static  String getDgsmRuleTypeByEnName(String enName) {
        for (TemplateFunctionNameEnum c : TemplateFunctionNameEnum.values()) {
            if (c.getEnName().equals(enName)) {
                return c.getDgsmRuleType();
            }
        }
        return "CUSTOM_SQL";
    }
}
