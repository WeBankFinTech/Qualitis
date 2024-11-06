package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2024-04-22 17:50
 * @description
 */
public class DataSourcesConditionResponse {

    @JsonProperty("clusters")
    private List<String> clusterNames;
    @JsonProperty("dbs")
    private List<String> databases;
    @JsonProperty("tables")
    private List<String> tables;
    @JsonProperty("columns")
    private List<String> columns;

    public List<String> getClusterNames() {
        return clusterNames;
    }

    public void setClusterNames(List<String> clusterNames) {
        this.clusterNames = clusterNames;
    }

    public List<String> getDatabases() {
        return databases;
    }

    public void setDatabases(List<String> databases) {
        this.databases = databases;
    }

    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
}
