/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author v_wenxuanzhang
 */
@Entity
@Table(name = "qualitis_imsmetric_fields_analyse")
@IdClass(FieldsAnalysePrimaryKey.class)
public class FieldsAnalyse {

    @Id
    @Column(name = "rule_id")
    private Long ruleId;

    @Id
    @Column(name = "data_date")
    private Integer dataDate;

    @Column(name = "analyse_type", columnDefinition = "TINYINT(5)")
    private Integer analyseType;

    @Column(name = "datasource_type", columnDefinition = "TINYINT(5)")
    private Integer datasourceType;

    @Column(name = "database_name")
    private String databaseName;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "field_name")
    private String fieldName;

    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "partition_attrs")
    private String partitionAttrs;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "datasource_user")
    private String datasourceUser;

    @Column(name = "remark")
    private String remark;


    public FieldsAnalyse() {
        // Default Constructor
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getDataDate() {
        return dataDate;
    }

    public void setDataDate(Integer dataDate) {
        this.dataDate = dataDate;
    }

    public Integer getAnalyseType() {
        return analyseType;
    }

    public void setAnalyseType(Integer analyseType) {
        this.analyseType = analyseType;
    }

    public Integer getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(Integer datasourceType) {
        this.datasourceType = datasourceType;
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

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getPartitionAttrs() {
        return partitionAttrs;
    }

    public void setPartitionAttrs(String partitionAttrs) {
        this.partitionAttrs = partitionAttrs;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
