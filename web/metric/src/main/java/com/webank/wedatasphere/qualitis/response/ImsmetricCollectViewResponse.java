package com.webank.wedatasphere.qualitis.response;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.dto.ImsMetricCollectDto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author v_minminghe@webank.com
 * @date 2024-04-17 15:11
 * @description
 */
public class ImsmetricCollectViewResponse {

    @JsonProperty("metric_id")
    private Long metricId;
    @JsonProperty("metric_value")
    private BigDecimal metricValue;
    @JsonProperty("metric_name")
    private String metricName;
    @JsonProperty("cluster_name")
    private String clusterName;
    private String database;
    private String table;
    private String column;
    @JsonProperty("template_name")
    private String templateName;
    @JsonProperty("data_user")
    private String dataUser;
    @JsonProperty("data_date")
    private String dataDate;
    @JsonProperty("update_time")
    private String updateTime;
    @JsonProperty("datasource_type")
    private Integer datasourceType;

    public ImsmetricCollectViewResponse(ImsMetricCollectDto imsMetricCollectDto, String templateName) {
        this.metricId = imsMetricCollectDto.getMetricId();
        this.metricValue = imsMetricCollectDto.getMetricValue();
        this.metricName = imsMetricCollectDto.getMetricName();
        this.dataUser = imsMetricCollectDto.getDatasourceUser();
        this.updateTime = DateUtil.format(imsMetricCollectDto.getUpdateTime(), DatePattern.NORM_DATETIME_FORMATTER);
        this.dataDate = DateUtil.formatDate(new Date(imsMetricCollectDto.getDataDate() * 1000));
        this.clusterName = imsMetricCollectDto.getClusterName();
        this.database = imsMetricCollectDto.getDbName();
        this.table = imsMetricCollectDto.getTableName();
        this.column = imsMetricCollectDto.getColumnName();
        this.datasourceType = imsMetricCollectDto.getDatasourceType();
        this.templateName = templateName;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public Integer getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(Integer datasourceType) {
        this.datasourceType = datasourceType;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
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

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
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

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getDataUser() {
        return dataUser;
    }

    public void setDataUser(String dataUser) {
        this.dataUser = dataUser;
    }

    public String getDataDate() {
        return dataDate;
    }

    public void setDataDate(String dataDate) {
        this.dataDate = dataDate;
    }
}
