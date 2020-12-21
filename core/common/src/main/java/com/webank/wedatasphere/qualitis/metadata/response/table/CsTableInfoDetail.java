package com.webank.wedatasphere.qualitis.metadata.response.table;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author allenzhou
 */
public class CsTableInfoDetail {

    @JsonProperty("table_name")
    private String tableName;
    @JsonProperty("context_Key")
    private String contextKey;

    public CsTableInfoDetail() {
        // Default Constructor
    }

    public CsTableInfoDetail(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getContextKey() { return contextKey; }

    public void setContextKey(String contextKey) { this.contextKey = contextKey; }
}
