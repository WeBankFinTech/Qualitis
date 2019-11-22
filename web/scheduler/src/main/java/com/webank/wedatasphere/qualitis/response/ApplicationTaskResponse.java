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
import com.webank.wedatasphere.qualitis.entity.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
public class ApplicationTaskResponse {

    @JsonProperty("application_id")
    private String applicationId;
    @JsonProperty("task")
    private List<TaskSimpleResponse> taskSimpleResponses;

    public ApplicationTaskResponse() {
    }

    public ApplicationTaskResponse(List<Task> tasks) {
        this.taskSimpleResponses = new ArrayList<>();
        for (Task task : tasks) {
            this.taskSimpleResponses.add(new TaskSimpleResponse(task));
            this.applicationId = task.getApplication().getId();
        }
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public List<TaskSimpleResponse> getTaskSimpleResponses() {
        return taskSimpleResponses;
    }

    public void setTaskSimpleResponses(List<TaskSimpleResponse> taskSimpleResponses) {
        this.taskSimpleResponses = taskSimpleResponses;
    }

    @Override
    public String toString() {
        return "ApplicationTaskResponse{" +
                "applicationId='" + applicationId + '\'' +
                ", taskSimpleResponses=" + taskSimpleResponses +
                '}';
    }
}
