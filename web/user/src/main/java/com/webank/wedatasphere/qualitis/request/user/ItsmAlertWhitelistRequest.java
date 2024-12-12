package com.webank.wedatasphere.qualitis.request.user;


import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author v_minminghe@webank.com
 * @date 2024-03-26 18:03
 * @description
 */
public class ItsmAlertWhitelistRequest {
    @JsonProperty("oper_type")
    private String operType;
    private String cluster;
    private String webankName;
    private String database;
    private String table;

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public String getWebankName() {
        return webankName;
    }

    public void setWebankName(String webankName) {
        this.webankName = webankName;
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
}
