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

/**
 * @author howeye
 */
public class TaskSimpleKeyResponse {

    @JsonProperty("task_id")
    private Long taskId;
    @JsonProperty("cluster_name")
    private String clusterName;

    public TaskSimpleKeyResponse() {
    }

    public TaskSimpleKeyResponse(String clusterName) {
        this.clusterName = clusterName;
    }

    public TaskSimpleKeyResponse(Long taskId, String clusterName) {
        this.taskId = taskId;
        this.clusterName = clusterName;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }


}
