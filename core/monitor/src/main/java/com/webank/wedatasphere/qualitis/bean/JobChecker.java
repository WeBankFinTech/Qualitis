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

import com.webank.wedatasphere.qualitis.entity.Task;

/**
 * @author howeye
 */
public class JobChecker implements Comparable<JobChecker> {
    private String applicationId;
    private String username;
    private Integer count;
    private String oldStatus;
    private Double oldProgress;
    private String ujesAddress;
    private String clusterName;
    private Task task;

    public JobChecker(String applicationId, String oldStatus, String username, String ujesAddress, String clusterName, Task task) {
        this.applicationId = applicationId;
        this.oldStatus = oldStatus;
        this.username = username;
        count = 0;
        this.ujesAddress = ujesAddress;
        this.clusterName = clusterName;
        this.task = task;
    }

    public JobChecker(String applicationId, String oldStatus, Double oldProgress, String username, String ujesAddress, String clusterName, Task task) {
        this.applicationId = applicationId;
        this.oldStatus = oldStatus;
        this.oldProgress = oldProgress;
        this.username = username;
        count = 0;
        this.ujesAddress = ujesAddress;
        this.clusterName = clusterName;
        this.task = task;
    }

    public Long getTaskId() {
        return task.getTaskRemoteId();
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getCount() {
        return count;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public Double getOldProgress() {
        return oldProgress;
    }

    public void setOldProgress(Double oldProgress) {
        this.oldProgress = oldProgress;
    }

    public void addCount() {
        count++;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getUjesAddress() {
        return ujesAddress;
    }

    public void setUjesAddress(String ujesAddress) {
        this.ujesAddress = ujesAddress;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public String toString() {
        return "JobChecker{" +
                "applicationId='" + applicationId + '\'' +
                ", username='" + username + '\'' +
                ", count=" + count +
                ", oldStatus='" + oldStatus + '\'' +
                ", ujesAddress='" + ujesAddress + '\'' +
                ", clusterName='" + clusterName + '\'' +
                ", task=" + task.getId() +
                '}';
    }

    @Override
    public int compareTo(JobChecker jobChecker) {
        return this.count - jobChecker.getCount();
    }
}
