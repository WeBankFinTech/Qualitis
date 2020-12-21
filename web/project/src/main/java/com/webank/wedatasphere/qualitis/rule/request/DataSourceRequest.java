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

package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

import java.util.List;

/**
 * @author howeye
 */
public class DataSourceRequest {

    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("db_name")
    private String dbName;
    @JsonProperty("table_name")
    private String tableName;
    @JsonProperty("col_names")
    private List<DataSourceColumnRequest> colNames;
    private String filter;
    private Integer datasourceIndex;

    public DataSourceRequest() {
        // Default Constructor
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

    public List<DataSourceColumnRequest> getColNames() {
        return colNames;
    }

    public void setColNames(List<DataSourceColumnRequest> colNames) {
        this.colNames = colNames;
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

    public static void checkRequest(DataSourceRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkString(request.getFilter(), "filter");
        CommonChecker.checkStringLength(request.getFilter(), 1000, "filter");
        CommonChecker.checkString(request.getTableName(), "table_name");
        CommonChecker.checkString(request.getDbName(), "db_name");
        CommonChecker.checkString(request.getClusterName(), "cluster_name");
        if (request.getColNames() == null) {
            throw new UnExpectedRequestException("col_names can not be null");
        }
        DataSourceColumnRequest.checkRequest(request.getColNames());
    }
    @Override
    public String toString() {
        return "DataSourceRequest{" +
                "clusterName='" + clusterName + '\'' +
                ", dbName='" + dbName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", colNames=" + colNames +
                ", filter='" + filter + '\'' +
                '}';
    }
}
