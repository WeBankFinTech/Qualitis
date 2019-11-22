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
public class JobSubmitResult {

    private Long taskId;
    private String jobStatus;
    private String clusterName;
    private String ujesAddress;
    private Integer taskRemoteId;

    public JobSubmitResult() {
    }

    public JobSubmitResult(Long taskId, String jobStatus, String clusterName, String ujesAddress, Integer taskRemoteId) {
        this.taskId = taskId;
        this.jobStatus = jobStatus;
        this.clusterName = clusterName;
        this.ujesAddress = ujesAddress;
        this.taskRemoteId = taskRemoteId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getUjesAddress() {
        return ujesAddress;
    }

    public void setUjesAddress(String ujesAddress) {
        this.ujesAddress = ujesAddress;
    }

    public Integer getTaskRemoteId() {
        return taskRemoteId;
    }

    public void setTaskRemoteId(Integer taskRemoteId) {
        this.taskRemoteId = taskRemoteId;
    }

    @Override
    public String toString() {
        return "JobSubmitResult{" +
                "taskId=" + taskId +
                ", jobStatus='" + jobStatus + '\'' +
                ", clusterName='" + clusterName + '\'' +
                ", ujesAddress='" + ujesAddress + '\'' +
                ", taskRemoteId=" + taskRemoteId +
                '}';
    }
}
