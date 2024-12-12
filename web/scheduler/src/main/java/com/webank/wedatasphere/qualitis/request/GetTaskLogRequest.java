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
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author howeye
 */
public class GetTaskLogRequest {

    @JsonProperty("task_id")
    private Long taskId;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("cluster_name")
    private String clusterId;

    public GetTaskLogRequest() {
        // Default Constructor
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public static void checkRequest(GetTaskLogRequest request) throws UnExpectedRequestException {
        com.webank.wedatasphere.qualitis.project.request.CommonChecker.checkObject(request, "Request");
        com.webank.wedatasphere.qualitis.project.request.CommonChecker.checkObject(request.getTaskId(), "Task_id");
        com.webank.wedatasphere.qualitis.project.request.CommonChecker.checkString(request.getCreateUser(), "CreateUser");
        CommonChecker.checkString(request.getClusterId(), "Cluster_name");
    }
}
