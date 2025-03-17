package com.webank.wedatasphere.qualitis.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//@Entity
//@Table(name = "qualitis_column_analysis_result")
public class ColumnAnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cluster_name")
    private String clusterName;
    @Column(name = "db_name")
    private String dbName;
    @Column(name = "table_name")
    private String tableName;
    @Column(name = "column_name")
    private String columnName;
    @Column(name = "column_type")
    private String columnType;
    @Column(name = "column_analysis_result", columnDefinition = "TEXT")
    private String columnAnalysisResultJson;
    @Column(name = "create_time")
    private String createTime;
    @Column(name = "update_time")
    private String updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getColumnAnalysisResultJson() {
        return columnAnalysisResultJson;
    }

    public void setColumnAnalysisResultJson(String columnAnalysisResultJson) {
        this.columnAnalysisResultJson = columnAnalysisResultJson;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
