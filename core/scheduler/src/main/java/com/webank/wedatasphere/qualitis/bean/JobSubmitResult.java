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
    private Long taskRemoteId;
    private String taskExecId;

    public JobSubmitResult() {
    }

    public JobSubmitResult(Long taskId, String jobStatus, String clusterName, String ujesAddress, Long taskRemoteId, String execId) {
        this.taskId = taskId;
        this.jobStatus = jobStatus;
        this.clusterName = clusterName;
        this.ujesAddress = ujesAddress;
        this.taskRemoteId = taskRemoteId;
        this.taskExecId = execId;
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

    public Long getTaskRemoteId() {
        return taskRemoteId;
    }

    public void setTaskRemoteId(Long taskRemoteId) {
        this.taskRemoteId = taskRemoteId;
    }

    public String getTaskExecId() {
        return taskExecId;
    }

    public void setTaskExecId(String taskExecId) {
        this.taskExecId = taskExecId;
    }

    @Override
    public String toString() {
        return "JobSubmitResult{" +
            "taskId=" + taskId +
            ", jobStatus='" + jobStatus + '\'' +
            ", clusterName='" + clusterName + '\'' +
            ", ujesAddress='" + ujesAddress + '\'' +
            ", taskRemoteId=" + taskRemoteId +
            ", taskExecId='" + taskExecId + '\'' +
            '}';
    }
}
