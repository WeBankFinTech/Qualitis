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

package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constant.MetaDataAuthEnum;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;

import java.util.List;

/**
 * @author howeye
 */
public class QueryUserOfMetaDataAuthRequest {

    @JsonProperty("auth_type")
    private Integer authType;
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("db_name")
    private String dbName;
    @JsonProperty("table_name")
    private String tableName;
    @JsonProperty("column_name")
    private String columnName;
    @JsonProperty("username")
    private String username;
    @JsonProperty("auth_types")
    private List<Integer> authTypes;
    private Integer page;
    private Integer size;

    public QueryUserOfMetaDataAuthRequest() {
        this.page = 0;
        this.size = 5;
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

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<Integer> getAuthTypes() {
        return authTypes;
    }

    public void setAuthTypes(List<Integer> authTypes) {
        this.authTypes = authTypes;
    }

    public static void checkRequest(QueryUserOfMetaDataAuthRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        CommonChecker.checkObject(request.getAuthType(), "Auth type");
        CommonChecker.checkString(request.getClusterName(), "Cluster name");
        CommonChecker.checkObject(request.getDbName(), "Db name");
        CommonChecker.checkObject(request.getTableName(), "Table name");
        CommonChecker.checkObject(request.getColumnName(), "Column name");

        AddMetaDataAuthRequest.checkMetaDataAuthEnum(request.getAuthType());
        if (request.getAuthType().equals(MetaDataAuthEnum.CLUSTER_AUTH.getCode())) {
            return;
        }

        CommonChecker.checkString(request.getDbName(), "Db name");
        if (request.getAuthType().equals(MetaDataAuthEnum.DB_AUTH.getCode())) {
            return;
        }

        CommonChecker.checkString(request.getTableName(), "Table name");
        if (request.getAuthType().equals(MetaDataAuthEnum.TABLE_AUTH.getCode())) {
            return;
        }

        CommonChecker.checkString(request.getColumnName(), "Column name");
    }

    @Override
    public String toString() {
        return "QueryUserOfMetaDataAuthRequest{" +
                "authType=" + authType +
                ", clusterName='" + clusterName + '\'' +
                ", dbName='" + dbName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", columnName='" + columnName + '\'' +
                ", username='" + username + '\'' +
                ", authTypes=" + authTypes +
                ", page=" + page +
                ", size=" + size +
                '}';
    }
}
