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

package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author howeye
 */
@Entity
@Table(name = "qualitis_application_task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_remote_id")
    private Integer taskRemoteId;
    @Column(name = "begin_time", length = 25)
    private String beginTime;
    @Column(name = "end_time", length = 25)
    private String endTime;
    private Integer status;

    @OneToMany(mappedBy = "task", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<TaskRuleSimple> taskRuleSimples;

    @OneToMany(mappedBy = "task", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<TaskDataSource> taskDataSources;

    @ManyToOne(fetch = FetchType.EAGER)
    private Application application;

    @Column(name = "cluster_id", length = 100)
    private String clusterId;

    @Column(name = "submit_address")
    private String submitAddress;

    public Task() {
    }

    public Task(Application application, String beginTime, Integer status, String clusterId, String submitAddress) {
        this.beginTime = beginTime;
        this.application = application;
        this.status = status;
        taskRuleSimples = new HashSet<>();
        taskDataSources = new HashSet<>();
        this.clusterId = clusterId;
        this.submitAddress = submitAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTaskRemoteId() {
        return taskRemoteId;
    }

    public void setTaskRemoteId(Integer taskRemoteId) {
        this.taskRemoteId = taskRemoteId;
    }

    public Set<TaskRuleSimple> getTaskRuleSimples() {
        return taskRuleSimples;
    }

    public void setTaskRuleSimples(Set<TaskRuleSimple> taskRuleSimples) {
        this.taskRuleSimples = taskRuleSimples;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Set<TaskDataSource> getTaskDataSources() {
        return taskDataSources;
    }

    public void setTaskDataSources(Set<TaskDataSource> taskDataSources) {
        this.taskDataSources = taskDataSources;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getSubmitAddress() {
        return submitAddress;
    }

    public void setSubmitAddress(String submitAddress) {
        this.submitAddress = submitAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
