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
 * @author allenzhou
 */
public class CheckCustomDataSourceResponse {
    private String database;
    private String table;

    @JsonProperty("from_content")
    private String fromContent;
    @JsonProperty("check_table")
    private List<CheckTableResponse> checkTableResponse;

    public CheckCustomDataSourceResponse() {
        // Do nothing.
    }

    public CheckCustomDataSourceResponse(List<TaskDataSource> taskDataSources, Map<Long, TaskRuleSimple> taskRuleSimpleMap, Map<Long, List<TaskResult>> taskResultMap) {
        StringBuilder fromContentStr = new StringBuilder();
        StringBuilder tableStr = new StringBuilder();
        StringBuilder dbStr = new StringBuilder();

        this.checkTableResponse = new ArrayList<>();
        for (TaskDataSource taskDataSource : taskDataSources) {
            fromContentStr.append(taskDataSource.getDatabaseName() + "." + taskDataSource.getTableName()).append("\n");
            tableStr.append(taskDataSource.getTableName() + ";").append("\n");
            dbStr.append(taskDataSource.getDatabaseName() + ";").append("\n");
        }
        this.fromContent = fromContentStr.toString();
        this.database = dbStr.toString();
        this.table = tableStr.toString();
        CheckTableResponse currentCheckTableResponse = new CheckTableResponse(taskDataSources.get(0), taskRuleSimpleMap, taskResultMap);
        this.checkTableResponse.add(currentCheckTableResponse);
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

    public String getFromContent() {
        return fromContent;
    }

    public void setFromContent(String fromContent) {
        this.fromContent = fromContent;
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
            "fromContent='" + fromContent + '\'' +
            ", checkTableResponse=" + checkTableResponse +
            '}';
    }
}