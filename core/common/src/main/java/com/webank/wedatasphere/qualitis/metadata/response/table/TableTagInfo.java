package com.webank.wedatasphere.qualitis.metadata.response.table;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_minminghe@webank.com
 * @date 2022-06-01 11:31
 * @description
 */
public class TableTagInfo {

    private Integer id;
    @JsonProperty("source_type")
    private String sourceType;
    @JsonProperty("cluster_type")
    private String clusterType;
    @JsonProperty("db_code")
    private String dbCode;
    @JsonProperty("dataset_name")
    private String datasetName;
    @JsonProperty("tag_code")
    private String tagCode;
    @JsonProperty("tag_name")
    private String tagName;

    public static TableTagInfo build() {
        return new TableTagInfo();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getClusterType() {
        return clusterType;
    }

    public void setClusterType(String clusterType) {
        this.clusterType = clusterType;
    }

    public String getDbCode() {
        return dbCode;
    }

    public void setDbCode(String dbCode) {
        this.dbCode = dbCode;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public String toString() {
        return "TableTagInfo{" +
                "id=" + id +
                ", sourceType='" + sourceType + '\'' +
                ", clusterType='" + clusterType + '\'' +
                ", dbCode='" + dbCode + '\'' +
                ", datasetName='" + datasetName + '\'' +
                ", tagCode='" + tagCode + '\'' +
                ", tagName='" + tagName + '\'' +
                '}';
    }
}
