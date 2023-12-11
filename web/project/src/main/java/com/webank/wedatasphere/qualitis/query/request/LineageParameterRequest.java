package com.webank.wedatasphere.qualitis.query.request;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import org.apache.commons.lang3.StringUtils;

/**
 * @author v_minminghe@webank.com
 * @date 2022-06-28 15:13
 * @description
 */
public class LineageParameterRequest {

    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("db_name")
    private String dbName;
    @JsonProperty("table_name")
    private String tableName;
    @JsonProperty("column_name")
    private String columnName;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void checkRequest() throws UnExpectedRequestException {
        CommonChecker.checkString(this.clusterName, "cluster_name");
        CommonChecker.checkString(this.dbName, "db_name");
        CommonChecker.checkString(this.tableName, "table_name");
    }
}
