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
import com.webank.wedatasphere.qualitis.entity.TaskResult;
import com.webank.wedatasphere.qualitis.entity.TaskRuleSimple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public class CheckSingleDataSourceResponse {

    private String database;
    private String table;
    @JsonProperty("check_table")
    private List<CheckTableResponse> checkTableResponse;

    public CheckSingleDataSourceResponse() {
    }

    public CheckSingleDataSourceResponse(String database, String table, List<TaskDataSource> taskDataSources, Map<Long, TaskRuleSimple> taskRuleSimpleMap
            , Map<Long, List<TaskResult>> taskResultMap) {
        this.database = database;
        this.table = table;
        this.checkTableResponse = new ArrayList<>();
        for (TaskDataSource taskDataSource : taskDataSources) {
            CheckTableResponse checkTableResponse = new CheckTableResponse(taskDataSource, taskRuleSimpleMap, taskResultMap);
            this.checkTableResponse.add(checkTableResponse);
        }
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

    public List<CheckTableResponse> getCheckTableResponse() {
        return checkTableResponse;
    }

    public void setCheckTableResponse(List<CheckTableResponse> checkTableResponse) {
        this.checkTableResponse = checkTableResponse;
    }

    @Override
    public String toString() {
        return "CheckSingleDataSourceResponse{" +
                "database='" + database + '\'' +
                ", table='" + table + '\'' +
                ", checkTableResponse=" + checkTableResponse +
                '}';
    }
}
