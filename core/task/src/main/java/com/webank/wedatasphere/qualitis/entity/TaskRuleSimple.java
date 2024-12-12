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

import com.webank.wedatasphere.qualitis.constant.AlarmConfigStatusEnum;
import com.webank.wedatasphere.qualitis.rule.constant.FileOutputNameEnum;
import com.webank.wedatasphere.qualitis.rule.constant.FileOutputUnitEnum;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
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
    @Column(name = "rule_name", length = 170)
    private String ruleName;
    @Column(name = "cn_name", length = 170)
    private String cnName;
    @Column(name = "template_name", length = 200)
    private String templateName;
    @Column(name = "rule_detail", length = 340)
    private String ruleDetail;
    @Column(name = "rule_id")
    private Long ruleId;
    @Column(name = "rule_group_name")
    private String ruleGroupName;
    @Column(name = "mid_table_name", length = 300)
    private String midTableName;
    @Column(name = "project_id")
    private Long projectId;
    @Column(name = "project_name", length = 170)
    private String projectName;
    @Column(name = "project_cn_name", length = 170)
    private String projectCnName;
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

    @Column(name = "delete_fail_check_result")
    private Boolean deleteFailCheckResult;

    public TaskRuleSimple() {
    }

    public TaskRuleSimple(TaskRule rule, Task task) {
        this.ruleName = rule.getRuleName();
        this.cnName = rule.getCnName();
        this.ruleId = rule.getRuleId();
        this.ruleGroupName = rule.getRuleGroupName();
        this.templateName = rule.getTemplateName();
        this.task = task;
        this.midTableName = rule.getMidTableName();
        this.projectId = rule.getProjectId();
        this.projectName = rule.getProjectName();
        this.projectCnName = rule.getProjectCnName();
        this.projectCreator = rule.getProjectCreator();
        this.applicationId = task.getApplication().getId();
        this.executeUser = task.getApplication().getExecuteUser();
        this.submitTime = task.getApplication().getSubmitTime();
        this.taskRuleAlarmConfigList = new ArrayList<>();
        this.ruleType = rule.getRuleType();
        for (TaskRuleAlarmConfigBean taskRuleAlarmConfigBean : rule.getTaskRuleAlarmConfigBeans()) {
            this.taskRuleAlarmConfigList.add(new TaskRuleAlarmConfig(taskRuleAlarmConfigBean, this));
        }
        this.deleteFailCheckResult = rule.getDeleteFailCheckResult();
    }

    public TaskRuleSimple(TaskRule rule, Task task, Boolean parent, TaskRuleSimple parentRuleSimple) {
        this.ruleGroupName = rule.getRuleGroupName();
        this.templateName = rule.getTemplateName();
        this.ruleDetail = rule.getRuleDetail();
        this.ruleName = rule.getRuleName();
        this.cnName = rule.getCnName();
        this.ruleId = rule.getRuleId();
        this.applicationId = task.getApplication().getId();
        this.submitTime = task.getApplication().getSubmitTime();
        this.executeUser = task.getApplication().getExecuteUser();

        this.projectId = rule.getProjectId();
        this.projectName = rule.getProjectName();
        this.projectCnName = rule.getProjectCnName();
        this.projectCreator = rule.getProjectCreator();

        this.taskRuleAlarmConfigList = new ArrayList<>();

        if (parent) {
            this.task = task;
            this.ruleType = rule.getRuleType();
            this.midTableName = rule.getMidTableName();
            for (TaskRuleAlarmConfigBean taskRuleAlarmConfigBean : rule.getTaskRuleAlarmConfigBeans()) {
                this.taskRuleAlarmConfigList.add(new TaskRuleAlarmConfig(taskRuleAlarmConfigBean, this));
            }
        } else {
            this.ruleType = rule.getChildRuleType();
            this.parentRuleSimple = parentRuleSimple;
            for (TaskRuleAlarmConfigBean taskRuleAlarmConfigBean : rule.getChildTaskRuleAlarmConfigsBeans()) {
                this.taskRuleAlarmConfigList.add(new TaskRuleAlarmConfig(taskRuleAlarmConfigBean, this));
            }
        }
        this.deleteFailCheckResult = rule.getDeleteFailCheckResult();
    }

    public TaskRuleSimple(Rule rule, Task task) {
        this.ruleName = rule.getName();
        this.cnName = rule.getCnName();
        this.ruleDetail = rule.getDetail();
        this.templateName = rule.getTemplate().getName();

        this.task = task;
        this.ruleId = rule.getId();
        this.projectId = rule.getProject().getId();
        this.projectName = rule.getProject().getName();
        this.projectCnName = rule.getProject().getCnName();
        this.applicationId = task.getApplication().getId();
        this.projectCreator = rule.getProject().getCreateUser();
        this.executeUser = task.getApplication().getExecuteUser();
        this.ruleGroupName = rule.getRuleGroup().getRuleGroupName();
        this.submitTime = task.getApplication().getSubmitTime();
        this.ruleType = rule.getRuleType();
    }

    public TaskRuleSimple(Rule rule, Task task, String localeStr) {
        this.ruleName = rule.getName();
        this.cnName = rule.getCnName();
        this.ruleDetail = rule.getDetail();
        this.templateName = rule.getTemplate().getName();

        this.task = task;
        this.ruleId = rule.getId();
        this.projectId = rule.getProject().getId();
        this.projectName = rule.getProject().getName();
        this.projectCnName = rule.getProject().getCnName();
        this.applicationId = task.getApplication().getId();
        this.projectCreator = rule.getProject().getCreateUser();
        this.executeUser = task.getApplication().getExecuteUser();
        this.ruleGroupName = rule.getRuleGroup().getRuleGroupName();
        this.submitTime = task.getApplication().getSubmitTime();
        this.taskRuleAlarmConfigList = new ArrayList<>();
        for (AlarmConfig alarmConfig : rule.getAlarmConfigs()) {
            TaskRuleAlarmConfig taskRuleAlarmConfig = new TaskRuleAlarmConfig();
            taskRuleAlarmConfig.setOutputName(FileOutputNameEnum.getFileOutputName(alarmConfig.getFileOutputName(), localeStr));
            if (alarmConfig.getFileOutputUnit() != null) {
                taskRuleAlarmConfig.setOutputUnit(FileOutputUnitEnum.fileOutputUnit(alarmConfig.getFileOutputUnit()));
            }
            taskRuleAlarmConfig.setCheckTemplate(alarmConfig.getCheckTemplate());
            taskRuleAlarmConfig.setCompareType(alarmConfig.getCompareType());
            taskRuleAlarmConfig.setThreshold(alarmConfig.getThreshold());
            taskRuleAlarmConfig.setTaskRuleSimple(this);
            taskRuleAlarmConfig.setRuleMetric(alarmConfig.getRuleMetric());
            taskRuleAlarmConfig.setStatus(AlarmConfigStatusEnum.NOT_CHECK.getCode());
            taskRuleAlarmConfig.setUploadAbnormalValue(alarmConfig.getUploadAbnormalValue());
            taskRuleAlarmConfig.setUploadRuleMetricValue(alarmConfig.getUploadRuleMetricValue());
            taskRuleAlarmConfig.setDeleteFailCheckResult(alarmConfig.getDeleteFailCheckResult());

            this.taskRuleAlarmConfigList.add(taskRuleAlarmConfig);
        }
        this.ruleType = rule.getRuleType();
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

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getRuleDetail() {
        return ruleDetail;
    }

    public void setRuleDetail(String ruleDetail) {
        this.ruleDetail = ruleDetail;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public void setRuleGroupName(String ruleGroupName) {
        this.ruleGroupName = ruleGroupName;
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

    public String getProjectCnName() {
        return projectCnName;
    }

    public void setProjectCnName(String projectCnName) {
        this.projectCnName = projectCnName;
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

    public Boolean getDeleteFailCheckResult() {
        return deleteFailCheckResult;
    }

    public void setDeleteFailCheckResult(Boolean deleteFailCheckResult) {
        this.deleteFailCheckResult = deleteFailCheckResult;
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
