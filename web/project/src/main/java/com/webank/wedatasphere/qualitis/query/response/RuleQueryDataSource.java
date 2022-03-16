package com.webank.wedatasphere.qualitis.query.response;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author allenzhou
 */
public class RuleQueryDataSource {
    @JsonProperty("cluster_name")
    private String clusterName;

    @JsonProperty("datasource_id")
    private String datasourceId;

    @JsonProperty("db_name")
    private String dbName;

    @JsonProperty("table_name")
    private String tableName;

    @JsonProperty("table_commit")
    private String tableCommit;

    @JsonProperty("proxy_user")
    private String proxyUser;

    public RuleQueryDataSource() {
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(String datasourceId) {
        this.datasourceId = datasourceId;
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

    public String getTableCommit() {
        return tableCommit;
    }

    public void setTableCommit(String tableCommit) {
        this.tableCommit = tableCommit;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    @Override
    public String toString() {
        return "RuleQueryDataSource{" +
            "clusterName='" + clusterName + '\'' +
            ", dbName='" + dbName + '\'' +
            ", tableName='" + tableName + '\'' +
            ", tableCommit='" + tableCommit + '\'' +
            ", proxyUser='" + proxyUser + '\'' +
            '}';
    }
}
