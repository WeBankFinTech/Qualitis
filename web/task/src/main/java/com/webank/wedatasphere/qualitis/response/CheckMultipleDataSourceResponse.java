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
public class CheckMultipleDataSourceResponse {

    @JsonProperty("datasource")
    private List<MultiDataSourceResponse> multiDataSourceResponse;

    @JsonProperty("check_table")
    private List<MultiRuleCheckTableResponse> checkTableResponse;

    public CheckMultipleDataSourceResponse() {
    }

    public CheckMultipleDataSourceResponse(List<Map<TaskRuleSimple, List<TaskDataSource>>> taskDataSourcesList, Map<Long, List<TaskResult>> taskResultMap) {
        this.multiDataSourceResponse = new ArrayList<>();
        this.checkTableResponse = new ArrayList<>();
        for (Map<TaskRuleSimple, List<TaskDataSource>> tmp : taskDataSourcesList) {
            for (TaskRuleSimple taskRuleSimple : tmp.keySet()) {
                MultiRuleCheckTableResponse multiRuleCheckTableResponse = new MultiRuleCheckTableResponse(taskRuleSimple, taskResultMap);
                this.checkTableResponse.add(multiRuleCheckTableResponse);
            }
        }

        TaskRuleSimple key = taskDataSourcesList.get(0).keySet().iterator().next();
        for (TaskDataSource taskDataSource : taskDataSourcesList.get(0).get(key)) {
            this.multiDataSourceResponse.add(new MultiDataSourceResponse(taskDataSource));
        }
    }

    public List<MultiDataSourceResponse> getMultiDataSourceResponse() {
        return multiDataSourceResponse;
    }

    public void setMultiDataSourceResponse(List<MultiDataSourceResponse> multiDataSourceResponse) {
        this.multiDataSourceResponse = multiDataSourceResponse;
    }

    public List<MultiRuleCheckTableResponse> getCheckTableResponse() {
        return checkTableResponse;
    }

    public void setCheckTableResponse(List<MultiRuleCheckTableResponse> checkTableResponse) {
        this.checkTableResponse = checkTableResponse;
    }
}
