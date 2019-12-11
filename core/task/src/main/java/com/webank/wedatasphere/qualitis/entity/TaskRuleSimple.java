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

import com.webank.wedatasphere.qualitis.bean.TaskRule;
import com.webank.wedatasphere.qualitis.bean.TaskRuleAlarmConfigBean;
import com.webank.wedatasphere.qualitis.bean.TaskRule;
import com.webank.wedatasphere.qualitis.bean.TaskRuleAlarmConfigBean;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author howeye
 */
@Entity
@Table(name = "qualitis_application_task_rule_simple")
public class TaskRuleSimple {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "rule_name", length = 200)
    private String ruleName;
    @Column(name = "rule_id")
    private Long ruleId;
    @Column(name = "mid_table_name", length = 200)
    private String midTableName;
    @Column(name = "project_id")
    private Long projectId;
    @Column(name = "project_name", length = 170)
    private String projectName;
    @Column(name = "project_creator", length = 50)
    private String projectCreator;
    @Column(name = "application_id", length = 40)
    private String applicationId;
    @Column(name = "execute_user", length = 20)
    private String executeUser;
    @Column(name = "submit_time", length = 20)
    private String submitTime;

    @ManyToOne
    private Task task;

    @OneToMany(mappedBy = "taskRuleSimple", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<TaskRuleAlarmConfig> taskRuleAlarmConfigList;

    @OneToOne
    private TaskRuleSimple parentRuleSimple;

    @OneToOne(mappedBy = "parentRuleSimple", fetch = FetchType.EAGER)
    private TaskRuleSimple childRuleSimple;

    @Column(name = "rule_type")
    private Integer ruleType;

    public TaskRuleSimple() {
    }

    public TaskRuleSimple(TaskRule rule, Task task) {
        this.ruleName = rule.getRuleName();
        this.ruleId = rule.getRuleId();
        this.task = task;
        this.midTableName = rule.getMidTableName();
        this.projectId = rule.getProjectId();
        this.projectName = rule.getProjectName();
        this.projectCreator = rule.getProjectCreator();
        this.applicationId = task.getApplication().getId();
        this.executeUser = task.getApplication().getExecuteUser();
        this.submitTime = task.getApplication().getSubmitTime();
        this.taskRuleAlarmConfigList = new ArrayList<>();
        this.ruleType = rule.getRuleType();
        for (TaskRuleAlarmConfigBean taskRuleAlarmConfigBean : rule.getTaskRuleAlarmConfigBeans()) {
            this.taskRuleAlarmConfigList.add(new TaskRuleAlarmConfig(taskRuleAlarmConfigBean, this));
        }
    }

    public TaskRuleSimple(TaskRule rule, Task task, Boolean parent, TaskRuleSimple parentRuleSimple) {
        if (parent) {
            this.ruleName = rule.getRuleName();
            this.ruleId = rule.getRuleId();
            this.task = task;
            this.midTableName = rule.getMidTableName().split(",")[0];
            this.projectId = rule.getProjectId();
            this.projectName = rule.getProjectName();
            this.projectCreator = rule.getProjectCreator();
            this.applicationId = task.getApplication().getId();
            this.executeUser = task.getApplication().getExecuteUser();
            this.submitTime = task.getApplication().getSubmitTime();
            this.taskRuleAlarmConfigList = new ArrayList<>();
            this.ruleType = rule.getRuleType();
            for (TaskRuleAlarmConfigBean taskRuleAlarmConfigBean : rule.getTaskRuleAlarmConfigBeans()) {
                this.taskRuleAlarmConfigList.add(new TaskRuleAlarmConfig(taskRuleAlarmConfigBean, this));
            }
        } else {
            this.ruleName = rule.getRuleName();
            this.ruleId = rule.getChildRuleId();
            this.ruleType = rule.getChildRuleType();
            this.midTableName = rule.getMidTableName().split(",")[1];
            this.submitTime = task.getApplication().getSubmitTime();
            this.taskRuleAlarmConfigList = new ArrayList<>();
            for (TaskRuleAlarmConfigBean taskRuleAlarmConfigBean : rule.getChildTaskRuleAlarmConfigsBeans()) {
                this.taskRuleAlarmConfigList.add(new TaskRuleAlarmConfig(taskRuleAlarmConfigBean, this));
            }
            this.parentRuleSimple = parentRuleSimple;
            this.projectId = rule.getProjectId();
            this.projectName = rule.getProjectName();
            this.projectCreator = rule.getProjectCreator();
            this.applicationId = task.getApplication().getId();
            this.executeUser = task.getApplication().getExecuteUser();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getMidTableName() {
        return midTableName;
    }

    public void setMidTableName(String midTableName) {
        this.midTableName = midTableName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getExecuteUser() {
        return executeUser;
    }

    public void setExecuteUser(String executeUser) {
        this.executeUser = executeUser;
    }

    public String getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(String submitTime) {
        this.submitTime = submitTime;
    }

    public List<TaskRuleAlarmConfig> getTaskRuleAlarmConfigList() {
        return taskRuleAlarmConfigList;
    }

    public void setTaskRuleAlarmConfigList(List<TaskRuleAlarmConfig> taskRuleAlarmConfigList) {
        this.taskRuleAlarmConfigList = taskRuleAlarmConfigList;
    }

    public String getProjectCreator() {
        return projectCreator;
    }

    public void setProjectCreator(String projectCreator) {
        this.projectCreator = projectCreator;
    }

    public TaskRuleSimple getParentRuleSimple() {
        return parentRuleSimple;
    }

    public void setParentRuleSimple(TaskRuleSimple parentRuleSimple) {
        this.parentRuleSimple = parentRuleSimple;
    }

    public TaskRuleSimple getChildRuleSimple() {
        return childRuleSimple;
    }

    public void setChildRuleSimple(TaskRuleSimple childRuleSimple) {
        this.childRuleSimple = childRuleSimple;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        TaskRuleSimple that = (TaskRuleSimple) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
