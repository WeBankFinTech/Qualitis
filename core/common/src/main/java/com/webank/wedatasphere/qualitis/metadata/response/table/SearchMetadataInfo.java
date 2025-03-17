package com.webank.wedatasphere.qualitis.metadata.response.table;

/**
 * @author v_minminghe@webank.com
 * @date 2022-06-01 11:36
 * @description
 */
public class SearchMetadataInfo {

    private String source;
    private String dbName;
    private String tbName;
    private String fieldName;
    private String fieldComment;
    private String subsystemName;
    private String clusterName;

    private String tagCodes;
    private String tagNames;
    private String department;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldComment() {
        return fieldComment;
    }

    public void setFieldComment(String fieldComment) {
        this.fieldComment = fieldComment;
    }

    public String getTbName() {
        return tbName;
    }

    public void setTbName(String tbName) {
        this.tbName = tbName;
    }

    public String getTagCodes() {
        return tagCodes;
    }

    public void setTagCodes(String tagCodes) {
        this.tagCodes = tagCodes;
    }

    public String getTagNames() {
        return tagNames;
    }

    public void setTagNames(String tagNames) {
        this.tagNames = tagNames;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSubsystemName() {
        return subsystemName;
    }

    public void setSubsystemName(String subsystemName) {
        this.subsystemName = subsystemName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "TableMetadataInfo{" +
                "source='" + source + '\'' +
                ", dbName='" + dbName + '\'' +
                ", tbName='" + tbName + '\'' +
                ", subsystemName='" + subsystemName + '\'' +
                ", tagCodes='" + tagCodes + '\'' +
                ", tagNames='" + tagNames + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
