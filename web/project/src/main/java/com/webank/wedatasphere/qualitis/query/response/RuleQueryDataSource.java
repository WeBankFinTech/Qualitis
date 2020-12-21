package com.webank.wedatasphere.qualitis.query.response;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author allenzhou
 */
public class RuleQueryDataSource {
    @JsonProperty("cluster_name")
    private String clusterName;

    @JsonProperty("db_name")
    private String dbName;

    @JsonProperty("table_name")
    private String tableName;

    @JsonProperty("table_commit")
    private String tableCommit;

    public RuleQueryDataSource() {
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

    public String getTableCommit() {
        return tableCommit;
    }

    public void setTableCommit(String tableCommit) {
        this.tableCommit = tableCommit;
    }

    @Override
    public String toString() {
        return "RuleQueryDataSource{" +
            "clusterName=" + clusterName +
            ", dbName='" + dbName + '\'' +
            ", tableName=" + tableName +
            ", tableCommit='" + tableCommit + '\'' +
            '}';
    }
}
