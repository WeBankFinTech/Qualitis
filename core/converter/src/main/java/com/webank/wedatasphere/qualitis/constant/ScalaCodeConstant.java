package com.webank.wedatasphere.qualitis.constant;

import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import java.text.MessageFormat;

/**
 * @author v_wenxuanzhang
 */
public class ScalaCodeConstant {

    public static final String BOTTOM_BAR = "_";
    public static final String VAL_STRING = "val %s = \"%s\"";
    public static final String VAL_NUMBER = "val %s = %s";
    public static final String VAR_STRING = "var %s = \"%s\"";
    public static final String VAR_NUMBER = "var %s = %s";
    public static final String NUMBER = "%s = %s";
    public static final String STRING = "%s = \"%s\"";
    public static final String ERROR_MSG = "{0} = \"database_name=%s,table_name=%s,attr_name=%s,rowkey_name=%s,rowvalueenum_name=%s,calc_type=%s\".format({1},{2},{3},{4},{5},{6})";
    public static final String EXCEPTION_COLLECT = "case e: %s => %s = (%s + \",%s:\" + e.getMessage) +: %s";

    public static final String QUERY_IDENTIFY_COMMON_SQL = "select metric_id from qualitis_imsmetric_identify where datasource_type = %s and database_name = '%s' " +
            "and table_name = '%s' and attr_name = '\" + %s + \"' ";
    public static final String QUERY_IDENTIFY_ENUM_SQL_1 = "select metric_id from qualitis_imsmetric_identify where datasource_type = %s and database_name = '%s' " +
            "and table_name = '%s' and attr_name = '\" + %s + \"' and groupbyattr_names = '\" + %s + \"' ";
    public static final String QUERY_IDENTIFY_BY_ENUM_SQL_2 = "  and rowvalueenum_name = '%s' and calc_type = %s ";
    public static final String QUERY_IDENTIFY_BY_TOTAL_SQL = " and groupbyattr_names = '%s' and rowkey_name = '%s' and calc_type = %s ";
    public static final String QUERY_IDENTIFY_BY_ORIGIN_SQL = " and rowkey_name = '%s' ";
    public static final String QUERY_IDENTIFY_BY_TABLE_SQL = " and calc_type = %s ";

    public static final String INSERT_IDENTIFY_TABLE_SQL = "insert into qualitis_imsmetric_identify(metric_type,datasource_type,database_name,table_name,attr_name," +
            "create_time,update_time,datasource_user,ageing,partition_attrs,calc_type) values " +
            "(%s,%s,'%s','%s','\" + %s + \"','\" + %s + \"','\" + %s + \"','%s','\" + %s + \"','\" + %s + \"'";
    public static final String INSERT_IDENTIFY_BY_ENUM_SQL = "insert into qualitis_imsmetric_identify(metric_type,datasource_type,database_name,table_name,attr_name,groupbyattr_names," +
            "create_time,update_time,datasource_user,ageing,partition_attrs,calc_type,rowvalueenum_name) values " +
            "(%s,%s,'%s','%s','\" + %s + \"','\"+ %s + \"','\" + %s + \"','\" + %s + \"','%s','\" + %s + \"','\" + %s + \"'";
    public static final String INSERT_IDENTIFY_BY_ORIGIN_SQL = "insert into qualitis_imsmetric_identify(metric_type,datasource_type,database_name,table_name,attr_name,calc_type," +
            "create_time,update_time,datasource_user,ageing,partition_attrs,rowkey_name) values " +
            "(%s,%s,'%s','%s','\" + %s + \"',7,'\" + %s + \"','\" + %s + \"','%s','\" + %s + \"','\" + %s + \"'";

    public static final String INSERT_DATA_COMMON_SQL = "INSERT INTO qualitis_imsmetric_data (metric_id, metric_value, create_time,update_time,data_time,data_date,datasource_user,datasource_type) VALUES %s " +
            "ON DUPLICATE KEY UPDATE metric_value = values(metric_value) , update_time = values(update_time)";
    public static final String INSERT_DATA_SQL_1 = "'\" + %s + \"', '\" + %s + \"', '\" + %s + \"'";
    public static final String INSERT_DATA_SQL_2 = " '%s' ,  %s \" + \") ";
    public static final String NOT_SUPPORTED_METRIC_TYPE = "不支持的指标采集方式";


    public static String stateValString(String fieldName, String fieldValue) {
        return format(VAL_STRING, fieldName, fieldValue);
    }

    public static String stateValNumber(String fieldName, Object fieldValue) {
        return format(VAL_NUMBER, fieldName, fieldValue);
    }

    public static String stateVarString(String fieldName, String fieldValue) {
        return format(VAR_STRING, fieldName, fieldValue);
    }

    public static String stateVarNumber(String fieldName, Object fieldValue) {
        return format(VAR_NUMBER, fieldName, fieldValue);
    }

    public static String stateString(String fieldName, Object fieldValue) {
        return format(STRING, fieldName, fieldValue);
    }

    public static String stateNumber(String fieldName, Object fieldValue) {
        return format(NUMBER, fieldName, fieldValue);
    }

    public static String jointVariableName(String prefix, String suffixes) {
        return prefix + BOTTOM_BAR + suffixes;
    }

    public static String errorMsg(String fieldName, String databaseName, String tableName, String attrName, String rowkeyName, String rowvalueenumName, String calcType) {
        return MessageFormat.format(ERROR_MSG, fieldName, databaseName, tableName, attrName, rowkeyName, rowvalueenumName, calcType);
    }

    public static String getQueryIdentifySql(int metricTypeInt, RuleDataSource dataSource, String attrName) {
        if (MetricTypeEnum.TABLE_STATISTICS.getCode() == metricTypeInt) {
            return format(QUERY_IDENTIFY_COMMON_SQL, dataSource.getDatasourceType(), dataSource.getDbName(),
                    dataSource.getTableName(), attrName) + QUERY_IDENTIFY_BY_TABLE_SQL;

        } else if (MetricTypeEnum.ENUM_STATISTICS.getCode() == metricTypeInt) {
            return format(QUERY_IDENTIFY_ENUM_SQL_1, dataSource.getDatasourceType(), dataSource.getDbName(),
                    dataSource.getTableName(), attrName, attrName) + QUERY_IDENTIFY_BY_ENUM_SQL_2;

        } else if (MetricTypeEnum.ORIGIN_STATISTICS.getCode() == metricTypeInt) {
            return format(QUERY_IDENTIFY_COMMON_SQL, dataSource.getDatasourceType(), dataSource.getDbName(),
                    dataSource.getTableName(), attrName) + QUERY_IDENTIFY_BY_ORIGIN_SQL;

        } else if (MetricTypeEnum.COLLECT_STATISTICS.getCode() == metricTypeInt) {
            return format(QUERY_IDENTIFY_COMMON_SQL + QUERY_IDENTIFY_BY_TOTAL_SQL, dataSource.getDatasourceType(),
                    dataSource.getDbName(), dataSource.getTableName(), attrName);
        } else {
            throw new RuntimeException(NOT_SUPPORTED_METRIC_TYPE);
        }
    }

    public static String getInsertIdentifySql(int metricTypeInt, RuleDataSource dataSource, String attrName,
                                              String nowTime, String ageing, String partitionAttrs) {

        if (MetricTypeEnum.TABLE_STATISTICS.getCode() == metricTypeInt) {
            return format(INSERT_IDENTIFY_TABLE_SQL, metricTypeInt, dataSource.getDatasourceType(), dataSource.getDbName(),
                    dataSource.getTableName(), attrName, nowTime, nowTime, dataSource.getProxyUser(), ageing, partitionAttrs) + ",%s)";

        } else if (MetricTypeEnum.ENUM_STATISTICS.getCode() == metricTypeInt) {
            return format(INSERT_IDENTIFY_BY_ENUM_SQL, metricTypeInt, dataSource.getDatasourceType(),
                    dataSource.getDbName(), dataSource.getTableName(), attrName, attrName, nowTime, nowTime, dataSource.getProxyUser(), ageing, partitionAttrs) + ",%s,'%s')";

        } else if (MetricTypeEnum.ORIGIN_STATISTICS.getCode() == metricTypeInt) {
            return format(INSERT_IDENTIFY_BY_ORIGIN_SQL, metricTypeInt, dataSource.getDatasourceType(),
                    dataSource.getDbName(), dataSource.getTableName(), attrName, nowTime, nowTime, dataSource.getProxyUser(), ageing, partitionAttrs) + ",'%s')";

        } else if (MetricTypeEnum.COLLECT_STATISTICS.getCode() == metricTypeInt) {
            return format(QUERY_IDENTIFY_COMMON_SQL + QUERY_IDENTIFY_BY_TOTAL_SQL, dataSource.getDatasourceType(),
                    dataSource.getDbName(), dataSource.getTableName(), attrName);
        } else {
            throw new RuntimeException(NOT_SUPPORTED_METRIC_TYPE);
        }
    }

    public static String getInsertDataSql(int metricTypeInt, RuleDataSource dataSource, String nowTime) {

        if (MetricTypeEnum.TABLE_STATISTICS.getCode() == metricTypeInt) {
            return "(%s, %s, " + format(INSERT_DATA_SQL_1, nowTime, nowTime, nowTime) + ", %s, " + format(INSERT_DATA_SQL_2, dataSource.getProxyUser(), dataSource.getDatasourceType());
        } else if (MetricTypeEnum.ENUM_STATISTICS.getCode() == metricTypeInt) {
            return "(%s, %s, " + format(INSERT_DATA_SQL_1, nowTime, nowTime, nowTime) + ", %s, " + format(INSERT_DATA_SQL_2, dataSource.getProxyUser(), dataSource.getDatasourceType());

        } else if (MetricTypeEnum.ORIGIN_STATISTICS.getCode() == metricTypeInt) {
            return "(%s, %s, " + format(INSERT_DATA_SQL_1, nowTime, nowTime, nowTime) + ", %s, " + format(INSERT_DATA_SQL_2, dataSource.getProxyUser(), dataSource.getDatasourceType());
        } else if (MetricTypeEnum.COLLECT_STATISTICS.getCode() == metricTypeInt) {
            return "(%s, %s, " + format(INSERT_DATA_SQL_1, nowTime, nowTime, nowTime) + ", %s, " + format(INSERT_DATA_SQL_2, dataSource.getProxyUser(), dataSource.getDatasourceType());
        } else {
            throw new RuntimeException(NOT_SUPPORTED_METRIC_TYPE);
        }
    }

    public static String exceptionCollect(String exception, String errorList, String remark, String errorMsg) {
        return format(EXCEPTION_COLLECT, exception, errorList, errorMsg, remark, errorList);
    }


    public static String format(String template, Object... param) {
        return String.format(template, param);
    }

}
