package com.webank.wedatasphere.qualitis.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author v_minminghe@webank.com
 * @date 2024-04-26 10:08
 * @description
 */
public class ImsMetricCollectDto {

    private Long id;
    private Long metricId;
    private Long collectId;
    private BigDecimal metricValue;
    private String metricName;
    private Integer datasourceType;
    private String clusterName;
    private String dbName;
    private String tableName;
    private String columnName;
    private Long templateId;
    private String templateEnName;
    private String templateCnName;
    private String partition;
    private String datasourceUser;
    private String proxyUser;
    private Date updateTime;
    private Long dataDate;
    private String calcuUnitName;
    private String columnType;
    private String identifyValue;
    private String executionParametersName;
    private String createUser;
    private String modifyUser;
    private String createTime;
    private String modifyTime;
    private String execFreq;

    public Long getCollectId() {
        return collectId;
    }

    public void setCollectId(Long collectId) {
        this.collectId = collectId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExecFreq() {
        return execFreq;
    }

    public void setExecFreq(String execFreq) {
        this.execFreq = execFreq;
    }

    public String getTemplateEnName() {
        return templateEnName;
    }

    public void setTemplateEnName(String templateEnName) {
        this.templateEnName = templateEnName;
    }

    public String getTemplateCnName() {
        return templateCnName;
    }

    public void setTemplateCnName(String templateCnName) {
        this.templateCnName = templateCnName;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getExecutionParametersName() {
        return executionParametersName;
    }

    public void setExecutionParametersName(String executionParametersName) {
        this.executionParametersName = executionParametersName;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getIdentifyValue() {
        return identifyValue;
    }

    public void setIdentifyValue(String identifyValue) {
        this.identifyValue = identifyValue;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getCalcuUnitName() {
        return calcuUnitName;
    }

    public void setCalcuUnitName(String calcuUnitName) {
        this.calcuUnitName = calcuUnitName;
    }

    public Long getMetricId() {
        return metricId;
    }

    public void setMetricId(Long metricId) {
        this.metricId = metricId;
    }

    public BigDecimal getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(BigDecimal metricValue) {
        this.metricValue = metricValue;
    }

    public Integer getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(Integer datasourceType) {
        this.datasourceType = datasourceType;
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


    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getDatasourceUser() {
        return datasourceUser;
    }

    public void setDatasourceUser(String datasourceUser) {
        this.datasourceUser = datasourceUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getDataDate() {
        return dataDate;
    }

    public void setDataDate(Long dataDate) {
        this.dataDate = dataDate;
    }
}
