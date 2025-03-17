package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author v_wenxuanzhang
 */
//@Entity
//@Table(name = "qualitis_imsmetric_identify")
public class ImsmetricIdentify {

    @Id
    @Column(name = "metric_id")
    private Long metricId;
    @Column(name = "metric_type", columnDefinition = "TINYINT(2)")
    private Integer metricType;
    @Column(name = "calc_type", columnDefinition = "TINYINT(2)")
    private Integer calcType;
    @Column(name = "datasource_type", columnDefinition = "TINYINT(2)")
    private Integer datasourceType;
    @Column(name = "partition_attrs")
    private String partitionAttrs;
    @Column(name = "database_name")
    private String databaseName;
    @Column(name = "table_name")
    private String tableName;
    @Column(name = "database_id")
    private Integer databaseId;
    @Column(name = "table_id")
    private Integer tableId;
    @Column(name = "groupbyattr_ids")
    private String groupbyattrIds;
    @Column(name = "groupbyattr_names")
    private String groupbyattrNames;
    @Column(name = "attr_id")
    private Integer attrId;
    @Column(name = "attr_name")
    private String attrName;
    @Column(name = "rowkey_id")
    private Long rowkeyId;
    @Column(name = "rowkey_name")
    private String rowkeyName;
    @Column(name = "rowvalueenum_id")
    private Long rowvalueenumId;
    @Column(name = "rowvalueenum_name")
    private String rowvalueenumName;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "update_time")
    private Date updateTime;
    @Column(name = "datasource_user")
    private String datasourceUser;
    @Column(name = "ageing", columnDefinition = "TINYINT(2)")
    private Integer ageing;
    @Column(name = "remark")
    private String remark;

    public Long getMetricId() {
        return metricId;
    }

    public void setMetricId(Long metricId) {
        this.metricId = metricId;
    }

    public Integer getMetricType() {
        return metricType;
    }

    public void setMetricType(Integer metricType) {
        this.metricType = metricType;
    }

    public Integer getCalcType() {
        return calcType;
    }

    public void setCalcType(Integer calcType) {
        this.calcType = calcType;
    }

    public Integer getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(Integer datasourceType) {
        this.datasourceType = datasourceType;
    }

    public String getPartitionAttrs() {
        return partitionAttrs;
    }

    public void setPartitionAttrs(String partitionAttrs) {
        this.partitionAttrs = partitionAttrs;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Integer databaseId) {
        this.databaseId = databaseId;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public String getGroupbyattrIds() {
        return groupbyattrIds;
    }

    public void setGroupbyattrIds(String groupbyattrIds) {
        this.groupbyattrIds = groupbyattrIds;
    }

    public String getGroupbyattrNames() {
        return groupbyattrNames;
    }

    public void setGroupbyattrNames(String groupbyattrNames) {
        this.groupbyattrNames = groupbyattrNames;
    }

    public Integer getAttrId() {
        return attrId;
    }

    public void setAttrId(Integer attrId) {
        this.attrId = attrId;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public Long getRowkeyId() {
        return rowkeyId;
    }

    public void setRowkeyId(Long rowkeyId) {
        this.rowkeyId = rowkeyId;
    }

    public String getRowkeyName() {
        return rowkeyName;
    }

    public void setRowkeyName(String rowkeyName) {
        this.rowkeyName = rowkeyName;
    }

    public Long getRowvalueenumId() {
        return rowvalueenumId;
    }

    public void setRowvalueenumId(Long rowvalueenumId) {
        this.rowvalueenumId = rowvalueenumId;
    }

    public String getRowvalueenumName() {
        return rowvalueenumName;
    }

    public void setRowvalueenumName(String rowvalueenumName) {
        this.rowvalueenumName = rowvalueenumName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDatasourceUser() {
        return datasourceUser;
    }

    public void setDatasourceUser(String datasourceUser) {
        this.datasourceUser = datasourceUser;
    }

    public Integer getAgeing() {
        return ageing;
    }

    public void setAgeing(Integer ageing) {
        this.ageing = ageing;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
