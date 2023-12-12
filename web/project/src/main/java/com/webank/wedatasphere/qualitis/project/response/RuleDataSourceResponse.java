package com.webank.wedatasphere.qualitis.project.response;

import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

/**
 * @author v_minminghe@webank.com
 * @date 2023-04-25 15:39
 * @description
 */
public class RuleDataSourceResponse {
    private Long oldId;
    private String clusterName;
    private String dbName;
    private String tableName;
    private String colName;
    private Boolean blackColName;
    private String fileUuid;

    private Long projectId;

    private String ruleName;

    private String ruleGroupName;

    private String filter;

    private Integer datasourceIndex;

    private String fileId;

    private String fileHashValue;

    private String fileTableDesc;

    private String fileDelimiter;

    private String fileType;

    private Boolean fileHeader;

    private String fileSheetName;

    private String proxyUser;

    private Long linkisDataSourceId;

    private Long linkisDataSourceVersionId;

    private String linkisDataSourceName;

    public RuleDataSourceResponse() {
    }

    public RuleDataSourceResponse(RuleDataSource dataSource) {
        BeanUtils.copyProperties(dataSource, this);
        this.oldId = dataSource.getId();
        if (Objects.nonNull(dataSource.getRule())) {
            this.ruleName = dataSource.getRule().getName();
        }
        if (Objects.nonNull(dataSource.getRuleGroup())) {
            this.ruleGroupName = dataSource.getRuleGroup().getRuleGroupName();
        }
    }

    public Long getOldId() {
        return oldId;
    }

    public void setOldId(Long oldId) {
        this.oldId = oldId;
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

    public String getColName() {
        return colName;
    }

    public void setColName(String colName) {
        this.colName = colName;
    }

    public Boolean getBlackColName() {
        return blackColName;
    }

    public void setBlackColName(Boolean blackColName) {
        this.blackColName = blackColName;
    }

    public String getFileUuid() {
        return fileUuid;
    }

    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public void setRuleGroupName(String ruleGroupName) {
        this.ruleGroupName = ruleGroupName;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Integer getDatasourceIndex() {
        return datasourceIndex;
    }

    public void setDatasourceIndex(Integer datasourceIndex) {
        this.datasourceIndex = datasourceIndex;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileHashValue() {
        return fileHashValue;
    }

    public void setFileHashValue(String fileHashValue) {
        this.fileHashValue = fileHashValue;
    }

    public String getFileTableDesc() {
        return fileTableDesc;
    }

    public void setFileTableDesc(String fileTableDesc) {
        this.fileTableDesc = fileTableDesc;
    }

    public String getFileDelimiter() {
        return fileDelimiter;
    }

    public void setFileDelimiter(String fileDelimiter) {
        this.fileDelimiter = fileDelimiter;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Boolean getFileHeader() {
        return fileHeader;
    }

    public void setFileHeader(Boolean fileHeader) {
        this.fileHeader = fileHeader;
    }

    public String getFileSheetName() {
        return fileSheetName;
    }

    public void setFileSheetName(String fileSheetName) {
        this.fileSheetName = fileSheetName;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public Long getLinkisDataSourceId() {
        return linkisDataSourceId;
    }

    public void setLinkisDataSourceId(Long linkisDataSourceId) {
        this.linkisDataSourceId = linkisDataSourceId;
    }

    public Long getLinkisDataSourceVersionId() {
        return linkisDataSourceVersionId;
    }

    public void setLinkisDataSourceVersionId(Long linkisDataSourceVersionId) {
        this.linkisDataSourceVersionId = linkisDataSourceVersionId;
    }

    public String getLinkisDataSourceName() {
        return linkisDataSourceName;
    }

    public void setLinkisDataSourceName(String linkisDataSourceName) {
        this.linkisDataSourceName = linkisDataSourceName;
    }
}
