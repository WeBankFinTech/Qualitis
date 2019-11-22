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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author howeye
 */
@Entity
@Table(name = "auth_meta_data")
public class AuthMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "auth_type")
    @JsonProperty("auth_type")
    private Integer authType;

    @Column(name = "cluster_name", length = 300)
    @JsonProperty("cluster_name")
    private String clusterName;

    @Column(name = "db_name", length = 300)
    @JsonProperty("db_name")
    private String dbName;

    @Column(name = "table_name", length = 300)
    @JsonProperty("table_name")
    private String tableName;

    @Column(name = "column_name", length = 300)
    @JsonProperty("column_name")
    private String columnName;

    @Column(name = "column_type", length = 300)
    @JsonIgnore
    private String columnType;

    @Column(length = 300)
    private String username;

    @Column(name = "is_org")
    @JsonProperty("is_org")
    private Boolean isOrg;

    @Column(name = "create_time")
    @JsonProperty("create_time")
    private Date createTime;

    public AuthMetaData() {
    }

    public AuthMetaData(Integer authType, String clusterName, String dbName, String tableName, String columnName,
                        String columnType, String username, Boolean isOrg) {
        this.authType = authType;
        this.clusterName = clusterName;
        this.dbName = dbName;
        this.tableName = tableName;
        this.columnName = columnName;
        this.columnType = columnType;
        this.username = username;
        this.isOrg = isOrg;
        this.createTime = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAuthType() {
        return authType;
    }

    public void setAuthType(Integer authType) {
        this.authType = authType;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    @JsonProperty("is_org")
    public Boolean getOrg() {
        return isOrg;
    }

    public void setOrg(Boolean org) {
        isOrg = org;
    }
}
