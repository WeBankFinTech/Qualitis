package com.webank.wedatasphere.qualitis.rule.timer;

import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;

/**
 * @author v_minminghe@webank.com
 * @date 2023-03-01 10:09
 * @description
 */
public class MetadataOnRuleDataSourceTask {

    private Long ruleDataSourceId;
    private Integer datasourceType;
    private String clusterName;
    private String dbName;
    private String tableName;
    private String username;

    public MetadataOnRuleDataSourceTask(RuleDataSource ruleDataSource, String username) {
        this.ruleDataSourceId = ruleDataSource.getId();
        this.datasourceType = ruleDataSource.getDatasourceType();
        this.clusterName = ruleDataSource.getClusterName();
        this.dbName = ruleDataSource.getDbName();
        this.tableName = ruleDataSource.getTableName();
        this.username = username;
    }

    public Long getRuleDataSourceId() {
        return ruleDataSourceId;
    }

    public void setRuleDataSourceId(Long ruleDataSourceId) {
        this.ruleDataSourceId = ruleDataSourceId;
    }

    public Integer getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(Integer datasourceType) {
        this.datasourceType = datasourceType;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
