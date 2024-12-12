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

    @JsonProperty("datasource_type")
    private Integer datasourceType;

    @JsonProperty("sub_system_name")
    private String subSystemName;
    @JsonProperty("department_name")
    private String departmentName;
    @JsonProperty("dev_department_name")
    private String devDepartmentName;
    @JsonProperty("tag_name")
    private String tagName;
    private String envName;
    @JsonProperty("rule_count")
    private Integer ruleCount;

    public RuleQueryDataSource() {
        // Default Constructor
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

    public Integer getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(Integer datasourceType) {
        this.datasourceType = datasourceType;
    }

    public String getSubSystemName() {
        return subSystemName;
    }

    public void setSubSystemName(String subSystemName) {
        this.subSystemName = subSystemName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDevDepartmentName() {
        return devDepartmentName;
    }

    public void setDevDepartmentName(String devDepartmentName) {
        this.devDepartmentName = devDepartmentName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public Integer getRuleCount() {
        return ruleCount;
    }

    public void setRuleCount(Integer ruleCount) {
        this.ruleCount = ruleCount;
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
