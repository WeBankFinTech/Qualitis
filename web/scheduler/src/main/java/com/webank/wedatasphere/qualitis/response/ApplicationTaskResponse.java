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
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.Task;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author howeye
 */
public class ApplicationTaskResponse {
    @JsonProperty("application_id")
    private String applicationId;
    @JsonProperty("application_status")
    private Integer applicationStatus;
    @JsonProperty("task")
    private List<TaskSimpleResponse> taskSimpleResponses;

    public ApplicationTaskResponse(Application application, List<Task> tasks) {
        this.applicationId = application.getId();
        this.applicationStatus = application.getStatus();
        this.taskSimpleResponses = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(tasks)) {
            for (Task task : tasks) {
                this.taskSimpleResponses.add(new TaskSimpleResponse(task));
            }
        }

    }

    public ApplicationTaskResponse(Application application) {
        this.applicationId = application.getId();
        this.applicationStatus = application.getStatus();
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(Integer applicationStatus) {
        this.applicationStatus = applicationStatus;
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
