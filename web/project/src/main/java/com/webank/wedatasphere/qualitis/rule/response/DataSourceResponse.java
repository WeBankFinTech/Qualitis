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

package com.webank.wedatasphere.qualitis.rule.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author howeye
 */
public class DataSourceResponse {

    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("db_name")
    private String dbName;
    @JsonProperty("table_name")
    private String tableName;
    @JsonProperty("col_names")
    private List<DataSourceColumnResponse> colNames;
    @JsonProperty("filter")
    private String filter;

    public DataSourceResponse() {
    }

    public DataSourceResponse(RuleDataSource ruleDataSource) {
        this.clusterName = ruleDataSource.getClusterName();
        this.dbName = ruleDataSource.getDbName();
        this.tableName = ruleDataSource.getTableName();
        this.colNames = convertColNames(Stream.of(ruleDataSource.getColName().split(",")).collect(Collectors.toList()));
        this.filter = ruleDataSource.getFilter();
    }

    private List<DataSourceColumnResponse> convertColNames(List<String> colTypes) {
        colNames = new ArrayList<>();
        for (String col : colTypes){
            DataSourceColumnResponse columnResponse = new DataSourceColumnResponse(col);
            colNames.add(columnResponse);
        }
        return colNames;
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

    public List<DataSourceColumnResponse> getColNames() {
        return colNames;
    }

    public void setColNames(List<DataSourceColumnResponse> colNames) {
        this.colNames = colNames;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

}
