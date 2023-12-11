package com.webank.wedatasphere.qualitis.query.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author v_minminghe@webank.com
 * @date 2022-05-31 9:34
 * @description
 */
public class LineagesQueryRequest {
    @JsonProperty("datasource_type")
    private Integer datasourceType;
    @JsonProperty("cluster_name")
    private String cluster;
    @JsonProperty("db_name")
    private String db;
    @JsonProperty("table_name")
    private String table;
    @JsonProperty("columns")
    private String columns;

    public void checkRequest() throws UnExpectedRequestException {
        if (Objects.isNull(this.datasourceType)
                || StringUtils.isEmpty(this.cluster)
                || StringUtils.isEmpty(this.db)
                || StringUtils.isEmpty(this.table)) {
            throw new UnExpectedRequestException("Params of {&REQUEST_CAN_NOT_BE_NULL}");
        }
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public Integer getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(Integer datasourceType) {
        this.datasourceType = datasourceType;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

}
