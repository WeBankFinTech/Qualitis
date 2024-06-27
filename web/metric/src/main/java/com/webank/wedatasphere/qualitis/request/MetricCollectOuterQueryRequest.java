package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_minminghe@webank.com
 * @date 2024-04-28 11:07
 * @description
 */
public class MetricCollectOuterQueryRequest {

    @JsonProperty("cluster_name")
    private String clusterName;
    private String database;
    private String table;
    private String column;
    @JsonProperty("calcu_unit_name")
    private String calcuUnitName;

    public String getCalcuUnitName() {
        return calcuUnitName;
    }

    public void setCalcuUnitName(String calcuUnitName) {
        this.calcuUnitName = calcuUnitName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }
}
