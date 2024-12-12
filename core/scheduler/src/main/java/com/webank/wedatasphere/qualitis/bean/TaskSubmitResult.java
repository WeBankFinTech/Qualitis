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

package com.webank.wedatasphere.qualitis.bean;

/**
 * @author howeye
 */
public class TaskSubmitResult {

    private String applicationId;
    private Long taskRemoteId;
    private String clusterName;

    public TaskSubmitResult(String applicationId, Long taskRemoteId, String clusterName) {
        this.applicationId = applicationId;
        this.taskRemoteId = taskRemoteId;
        this.clusterName = clusterName;
    }

    public TaskSubmitResult() {

    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Long getTaskRemoteId() {
        return taskRemoteId;
    }

    public void setTaskRemoteId(Long taskRemoteId) {
        this.taskRemoteId = taskRemoteId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    @Override
    public String toString() {
        return "TaskSubmitResult{" +
                "applicationId='" + applicationId + '\'' +
                ", taskRemoteId=" + taskRemoteId +
                ", clusterName='" + clusterName + '\'' +
                '}';
    }
}
