package com.webank.wedatasphere.qualitis.rule.timer;

import com.webank.wedatasphere.qualitis.entity.ImsMetricCollect;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;

/**
 * @author v_minminghe@webank.com
 * @date 2023-03-01 10:09
 * @description
 */
public class MetadataSyncTask {

    private Long dataId;
    private Integer datasourceType;
    private String clusterType;
    private String clusterName;
    private String dbName;
    private String tableName;
    private String username;
    private String colName;
    private String cacheKey;

    public MetadataSyncTask(RuleDataSource ruleDataSource, String username) {
        this.dataId = ruleDataSource.getId();
        this.clusterName = ruleDataSource.getClusterName();
        this.datasourceType = ruleDataSource.getDatasourceType();
        this.dbName = ruleDataSource.getDbName();
        this.tableName = ruleDataSource.getTableName();
        this.username = username;
        StringBuilder cacheKeyBuilder = new StringBuilder();
        cacheKeyBuilder.append(ruleDataSource.getDatasourceType());
        cacheKeyBuilder.append(ruleDataSource.getClusterName());
        cacheKeyBuilder.append(ruleDataSource.getDbName());
        cacheKeyBuilder.append(ruleDataSource.getTableName());
        this.cacheKey = cacheKeyBuilder.toString();
    }

    public MetadataSyncTask(ImsMetricCollect imsMetricCollect, String username) {
        this.datasourceType = imsMetricCollect.getDatasourceType();
        this.clusterName = imsMetricCollect.getClusterName();
        this.dbName = imsMetricCollect.getDbName();
        this.tableName = imsMetricCollect.getTableName();
        this.colName = imsMetricCollect.getColumnName();
        this.username = username;
        this.cacheKey = String.valueOf(imsMetricCollect.getId());
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public Long getDataId() {
        return dataId;
    }

    public void setDataId(Long dataId) {
        this.dataId = dataId;
    }

    public Integer getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(Integer datasourceType) {
        this.datasourceType = datasourceType;
    }

    public String getClusterType() {
        return clusterType;
    }

    public void setClusterType(String clusterType) {
        this.clusterType = clusterType;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "MetadataSyncTask{" +
                "dataId=" + dataId +
                ", datasourceType=" + datasourceType +
                ", clusterName='" + clusterType + '\'' +
                ", dbName='" + dbName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", username='" + username + '\'' +
                ", colName='" + colName + '\'' +
                ", cacheKey='" + cacheKey + '\'' +
                '}';
    }
}
