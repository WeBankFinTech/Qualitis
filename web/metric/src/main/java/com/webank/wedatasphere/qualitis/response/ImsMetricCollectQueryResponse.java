package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;


/**
 * @author v_wenxuanzhang
 */
public class ImsMetricCollectQueryResponse {
    @JsonProperty("metricId")
    private BigInteger metricId;
    @JsonProperty("dbName")
    private String dbName;
    @JsonProperty("tableName")
    private String tableName;
    @JsonProperty("columnName")
    private String columnName;
    @JsonProperty("calcuUnitName")
    private String calcuUnitName;
    @JsonProperty("enumType")
    private boolean enumType;
    @JsonProperty("enumValue")
    private String enumValue;
    @JsonProperty("proxyUser")
    private String proxyUser;

    public BigInteger getMetricId() {
        return metricId;
    }

    public void setMetricId(BigInteger metricId) {
        this.metricId = metricId;
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

    public String getCalcuUnitName() {
        return calcuUnitName;
    }

    public void setCalcuUnitName(String calcuUnitName) {
        this.calcuUnitName = calcuUnitName;
    }

    public boolean isEnumType() {
        return enumType;
    }

    public void setEnumType(boolean enumType) {
        this.enumType = enumType;
    }

    public String getEnumValue() {
        return enumValue;
    }

    public void setEnumValue(String enumValue) {
        this.enumValue = enumValue;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    @Override
    public String toString() {
        return "ImsMetricCollectQueryResponse{" +
                "metricId=" + metricId +
                ", dbName='" + dbName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", columnName='" + columnName + '\'' +
                ", calcuUnitName='" + calcuUnitName + '\'' +
                ", enumType=" + enumType +
                ", enumValue='" + enumValue + '\'' +
                ", proxyUser='" + proxyUser + '\'' +
                '}';
    }
}
