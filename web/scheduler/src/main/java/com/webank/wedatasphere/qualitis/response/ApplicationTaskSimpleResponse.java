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
import com.webank.wedatasphere.qualitis.bean.TaskSubmitResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
public class ApplicationTaskSimpleResponse {

    @JsonProperty("application_id")
    private String applicationId;
    @JsonProperty("tasks")
    private List<TaskSimpleKeyResponse> tasks;

    public ApplicationTaskSimpleResponse() {
    }

    public ApplicationTaskSimpleResponse(List<TaskSubmitResult> taskDivideResults) {
        this.tasks = new ArrayList<>();
        for (TaskSubmitResult taskSubmitResult : taskDivideResults) {
            this.applicationId = taskSubmitResult.getApplicationId();
            this.tasks.add(new TaskSimpleKeyResponse(taskSubmitResult.getTaskRemoteId(), taskSubmitResult.getClusterName()));
        }
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public List<TaskSimpleKeyResponse> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskSimpleKeyResponse> tasks) {
        this.tasks = tasks;
    }
}
