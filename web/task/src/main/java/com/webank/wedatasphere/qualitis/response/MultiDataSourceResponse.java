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

package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.entity.TaskDataSource;

/**
 * @author howeye
 */
public class MultiDataSourceResponse {

    private Integer index;
    @JsonProperty("db_name")
    private String dbName;
    @JsonProperty("table_name")
    private String tableName;

    public MultiDataSourceResponse() {
    }

    public MultiDataSourceResponse(TaskDataSource taskDataSource) {
        this.index = taskDataSource.getDatasourceIndex();
        this.dbName = taskDataSource.getDatabaseName();
        this.tableName = taskDataSource.getTableName();
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
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
}
