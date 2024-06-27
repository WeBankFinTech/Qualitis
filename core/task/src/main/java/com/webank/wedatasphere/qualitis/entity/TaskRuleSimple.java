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
import com.webank.wedatasphere.qualitis.checkalert.entity.CheckAlert;
import com.webank.wedatasphere.qualitis.constant.AlarmConfigStatusEnum;
import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import com.webank.wedatasphere.qualitis.rule.constant.FileOutputNameEnum;
import com.webank.wedatasphere.qualitis.rule.constant.FileOutputUnitEnum;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;

import com.webank.wedatasphere.qualitis.scheduled.constant.RuleGroupTypeEnum;
import com.webank.wedatasphere.qualitis.scheduled.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author howeye
 */
@Entity
@Table(name = "qualitis_application_task_rule_simple")
public class TaskRuleSimple {
    private static final String QUALITIS_DELETE_FAIL_CHECK_RESULT = "qualitis_delete_fail_check_result";
    private static final String QUALITIS_UPLOAD_RULE_METRIC_VALUE = "qualitis_upload_rule_metric_value";
    private static final String QUALITIS_UPLOAD_ABNORMAL_VALUE = "qualitis_upload_abnormal_value";
    private static final String QUALITIS_ALERT_RECEIVERS = "qualitis_alert_receivers";
    private static final String QUALITIS_ALERT_LEVEL = "qualitis_alert_level";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "rule_name", length = 170)
    private String ruleName;
    @Column(name = "cn_name", length = 170)
    private String cnName;
    @Column(name = "template_name", length = 200)
    private String templateName;
    @Column(name = "template_en_name", length = 300)
    private String templateEnName;
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
    @Column(name = "alert_level")
    private Integer alertLevel;
    @Column(name = "alert_receiver")
    private String alertReceiver;

    @ManyToOne
    private Task task;

    @OneToMany(mappedBy = "taskRuleSimple", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<TaskRuleAlarmConfig> taskRuleAlarmConfigList;

    @Column(name = "rule_type")
    private Integer ruleType;

    @Column(name = "delete_fail_check_result")
    private Boolean deleteFailCheckResult;

    public TaskRuleSimple() {
    }

    public TaskRuleSimple(TaskRule rule, Task task, Map<Long, Map<String, Object>> ruleReplaceInfo) {
        this.ruleName = rule.getRuleName();
        this.cnName = rule.getCnName();
        this.ruleId = rule.getRuleId();
        this.ruleGroupName = rule.getRuleGroupName();
        this.templateName = rule.getTemplateName();
        this.templateEnName = rule.getTemplateEnName();
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
        this.alertLevel = rule.getAlertLevel();
        this.alertReceiver = rule.getAlertReceiver();
        LocaleParser localeParser = SpringContextHolder.getBean(LocaleParser.class);
        for (TaskRuleAlarmConfigBean taskRuleAlarmConfigBean : rule.getTaskRuleAlarmConfigBeans()) {
            TaskRuleAlarmConfig taskRuleAlarmConfig = new TaskRuleAlarmConfig(taskRuleAlarmConfigBean, this);
            String outputCnName = localeParser.replacePlaceHolderByLocale(taskRuleAlarmConfigBean.getOutputName(), "zh_CN");
            taskRuleAlarmConfig.setOutputName(outputCnName);
            this.taskRuleAlarmConfigList.add(taskRuleAlarmConfig);
        }
        if (ruleReplaceInfo.get(rule.getRuleId()) != null && ruleReplaceInfo.get(rule.getRuleId()).keySet().contains(QUALITIS_DELETE_FAIL_CHECK_RESULT)) {
            this.deleteFailCheckResult = (Boolean) ruleReplaceInfo.get(rule.getRuleId()).get(QUALITIS_DELETE_FAIL_CHECK_RESULT);
        } else {
            this.deleteFailCheckResult = rule.getDeleteFailCheckResult();
        }
    }

    public TaskRuleSimple(Rule rule, Task task, Map<Long, Map<String, Object>> ruleReplaceInfo) {
        this.ruleName = rule.getName();
        this.cnName = rule.getCnName();
        this.ruleDetail = rule.getDetail();
        this.templateName = rule.getTemplate().getName();
        this.templateEnName = rule.getTemplate().getEnName();
        if (ruleReplaceInfo.get(rule.getId()) != null && ruleReplaceInfo.get(rule.getId()).keySet().contains(QUALITIS_ALERT_LEVEL)) {
            this.alertReceiver = (String) ruleReplaceInfo.get(rule.getId()).get(QUALITIS_ALERT_RECEIVERS);
            this.alertLevel = (Integer) ruleReplaceInfo.get(rule.getId()).get(QUALITIS_ALERT_LEVEL);
        } else if (Boolean.TRUE.equals(rule.getAlert())) {
            this.alertReceiver = rule.getAlertReceiver();
            this.alertLevel = rule.getAlertLevel();
        }

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

    public TaskRuleSimple(Rule rule, Task task, Map<Long, Map<String, Object>> ruleReplaceInfo, boolean fileRule) {
        this.ruleName = rule.getName();
        this.cnName = rule.getCnName();
        this.ruleDetail = rule.getDetail();
        this.templateName = rule.getTemplate().getName();
        this.templateEnName = rule.getTemplate().getEnName();
        if (ruleReplaceInfo.get(rule.getId()) != null && ruleReplaceInfo.get(rule.getId()).keySet().contains(QUALITIS_ALERT_LEVEL)) {
            this.alertReceiver = (String) ruleReplaceInfo.get(rule.getId()).get(QUALITIS_ALERT_RECEIVERS);
            this.alertLevel = (Integer) ruleReplaceInfo.get(rule.getId()).get(QUALITIS_ALERT_LEVEL);
        } else if (rule.getAlert() != null && rule.getAlert()){
            this.alertReceiver = rule.getAlertReceiver();
            this.alertLevel = rule.getAlertLevel();
        }
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
        LocaleParser localeParser = SpringContextHolder.getBean(LocaleParser.class);
        for (AlarmConfig alarmConfig : rule.getAlarmConfigs()) {
            TaskRuleAlarmConfig taskRuleAlarmConfig = new TaskRuleAlarmConfig();
            String outputCnName = localeParser.replacePlaceHolderByLocale(alarmConfig.getTemplateOutputMeta().getOutputName(), "zh_CN");
            taskRuleAlarmConfig.setOutputName(outputCnName);
            if (alarmConfig.getFileOutputUnit() != null) {
                taskRuleAlarmConfig.setOutputUnit(FileOutputUnitEnum.fileOutputUnit(alarmConfig.getFileOutputUnit()));
            }
            taskRuleAlarmConfig.setCheckTemplate(alarmConfig.getCheckTemplate());
            taskRuleAlarmConfig.setCompareType(alarmConfig.getCompareType());
            taskRuleAlarmConfig.setThreshold(alarmConfig.getThreshold());
            taskRuleAlarmConfig.setTaskRuleSimple(this);
            taskRuleAlarmConfig.setRuleMetric(alarmConfig.getRuleMetric());
            taskRuleAlarmConfig.setStatus(AlarmConfigStatusEnum.NOT_CHECK.getCode());

            if (ruleReplaceInfo.get(rule.getId()) != null && ruleReplaceInfo.get(rule.getId()).keySet().contains(QUALITIS_UPLOAD_ABNORMAL_VALUE)) {
                taskRuleAlarmConfig.setUploadAbnormalValue((Boolean) ruleReplaceInfo.get(rule.getId()).get(QUALITIS_UPLOAD_ABNORMAL_VALUE));
            } else {
                taskRuleAlarmConfig.setUploadAbnormalValue(alarmConfig.getUploadAbnormalValue());
            }
            if (ruleReplaceInfo.get(rule.getId()) != null && ruleReplaceInfo.get(rule.getId()).keySet().contains(QUALITIS_UPLOAD_RULE_METRIC_VALUE)) {
                taskRuleAlarmConfig.setUploadRuleMetricValue((Boolean) ruleReplaceInfo.get(rule.getId()).get(QUALITIS_UPLOAD_RULE_METRIC_VALUE));
            } else {
                taskRuleAlarmConfig.setUploadRuleMetricValue(alarmConfig.getUploadRuleMetricValue());
            }
            if (ruleReplaceInfo.get(rule.getId()) != null && ruleReplaceInfo.get(rule.getId()).keySet().contains(QUALITIS_DELETE_FAIL_CHECK_RESULT)) {
                taskRuleAlarmConfig.setDeleteFailCheckResult((Boolean) ruleReplaceInfo.get(rule.getId()).get(QUALITIS_DELETE_FAIL_CHECK_RESULT));
            } else {
                taskRuleAlarmConfig.setDeleteFailCheckResult(alarmConfig.getDeleteFailCheckResult());
            }

            this.taskRuleAlarmConfigList.add(taskRuleAlarmConfig);
        }

        if (fileRule) {
            this.ruleType = rule.getRuleType();
        }
        this.ruleType = rule.getRuleType();
    }

    public TaskRuleSimple(CheckAlert currentCheckAlert, Task savedTask) {
        this.task = savedTask;
        this.ruleId = currentCheckAlert.getId();
        this.ruleName = currentCheckAlert.getTopic();
        this.ruleGroupName = currentCheckAlert.getRuleGroup().getRuleGroupName();
        this.projectCreator = currentCheckAlert.getProject().getCreateUser();
        this.executeUser = savedTask.getApplication().getExecuteUser();
        this.submitTime = savedTask.getApplication().getSubmitTime();
        this.projectName = currentCheckAlert.getProject().getName();
        this.projectId = currentCheckAlert.getProject().getId();
        this.applicationId = savedTask.getApplication().getId();
        this.ruleType = RuleTypeEnum.CHECK_ALERT_RULE.getCode();

        this.taskRuleAlarmConfigList = new ArrayList<>();
        this.taskRuleAlarmConfigList.add(new TaskRuleAlarmConfig(this));
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

    public String getTemplateEnName() {
        return templateEnName;
    }

    public void setTemplateEnName(String templateEnName) {
        this.templateEnName = templateEnName;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Integer getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(Integer alertLevel) {
        this.alertLevel = alertLevel;
    }

    public String getAlertReceiver() {
        return alertReceiver;
    }

    public void setAlertReceiver(String alertReceiver) {
        this.alertReceiver = alertReceiver;
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

    @Override
    public String toString() {
        return "TaskRuleSimple{" +
            "id=" + id +
            ", ruleName='" + ruleName + '\'' +
            ", cnName='" + cnName + '\'' +
            ", templateName='" + templateName + '\'' +
            ", ruleDetail='" + ruleDetail + '\'' +
            ", ruleId=" + ruleId +
            ", ruleGroupName='" + ruleGroupName + '\'' +
            ", midTableName='" + midTableName + '\'' +
            ", projectId=" + projectId +
            ", projectName='" + projectName + '\'' +
            ", projectCnName='" + projectCnName + '\'' +
            ", projectCreator='" + projectCreator + '\'' +
            ", applicationId='" + applicationId + '\'' +
            ", executeUser='" + executeUser + '\'' +
            ", submitTime='" + submitTime + '\'' +
            ", alertLevel=" + alertLevel +
            ", task=" + task +
            ", taskRuleAlarmConfigList=" + taskRuleAlarmConfigList +
            ", ruleType=" + ruleType +
            ", deleteFailCheckResult=" + deleteFailCheckResult +
            '}';
    }
}
