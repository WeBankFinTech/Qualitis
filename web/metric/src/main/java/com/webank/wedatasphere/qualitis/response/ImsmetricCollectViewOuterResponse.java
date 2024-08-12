package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
//import com.webank.wedatasphere.qualitis.dto.ImsMetricCollectDto;

/**
 * @author v_minminghe@webank.com
 * @date 2024-04-28 11:47
 * @description
 */
public class ImsmetricCollectViewOuterResponse {

    @JsonProperty("metric_id")
    private Long metricId;
    @JsonProperty("cluster_name")
    private String clusterName;
    private String database;
    private String table;
    private String column;
    @JsonProperty("column_type")
    private String columnName;
    @JsonProperty("calcu_unit_name")
    private String calcuUnitName;
    @JsonProperty("proxy_user")
    private String proxyUser;
    @JsonProperty("identify_value")
    private String identifyValue;

//    public ImsmetricCollectViewOuterResponse (ImsMetricCollectDto imsMetricCollectDto) {
//        this.metricId = imsMetricCollectDto.getMetricId();
//        this.clusterName = imsMetricCollectDto.getClusterName();
//        this.database = imsMetricCollectDto.getDbName();
//        this.table = imsMetricCollectDto.getTableName();
//        this.column = imsMetricCollectDto.getColumnName();
//        this.calcuUnitName = imsMetricCollectDto.getCalcuUnitName();
//        this.columnName = imsMetricCollectDto.getColumnType();
//        this.proxyUser = imsMetricCollectDto.getProxyUser();
//        this.identifyValue = imsMetricCollectDto.getIdentifyValue();
//    }

    public String getIdentifyValue() {
        return identifyValue;
    }

    public void setIdentifyValue(String identifyValue) {
        this.identifyValue = identifyValue;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Long getMetricId() {
        return metricId;
    }

    public void setMetricId(Long metricId) {
        this.metricId = metricId;
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

    public String getCalcuUnitName() {
        return calcuUnitName;
    }

    public void setCalcuUnitName(String calcuUnitName) {
        this.calcuUnitName = calcuUnitName;
    }
}
