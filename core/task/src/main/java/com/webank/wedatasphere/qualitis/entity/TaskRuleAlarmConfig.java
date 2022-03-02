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

import com.webank.wedatasphere.qualitis.bean.TaskRuleAlarmConfigBean;
import com.webank.wedatasphere.qualitis.constant.AlarmConfigStatusEnum;
import com.webank.wedatasphere.qualitis.bean.TaskRuleAlarmConfigBean;
import com.webank.wedatasphere.qualitis.constant.AlarmConfigStatusEnum;

import javax.persistence.*;

/**
 * @author howeye
 */
@Entity
@Table(name = "qualitis_application_task_rule_alarm_config")
public class TaskRuleAlarmConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "output_name", length = 500)
    private String outputName;

    @Column(name = "output_Unit", length = 500)
    private String outputUnit;

    @Column(name = "check_template")
    private Integer checkTemplate;

    private Double threshold;

    @Column(name = "compare_type")
    private Integer compareType;

    private Integer status;

    @ManyToOne
    private TaskRuleSimple taskRuleSimple;

    @ManyToOne
    private RuleMetric ruleMetric;

    @Column(name = "upload_rule_metric_value")
    private Boolean uploadRuleMetricValue;

    @Column(name = "upload_abnormal_value")
    private Boolean uploadAbnormalValue;

    @Column(name = "delete_fail_check_result")
    private Boolean deleteFailCheckResult;

    public TaskRuleAlarmConfig() {
    }

    public TaskRuleAlarmConfig(TaskRuleAlarmConfigBean taskRuleAlarmConfigBean, TaskRuleSimple taskRuleSimple) {
        this.outputName = taskRuleAlarmConfigBean.getOutputName();
        this.checkTemplate = taskRuleAlarmConfigBean.getCheckTemplate();
        this.compareType = taskRuleAlarmConfigBean.getCompareType();
        this.threshold = taskRuleAlarmConfigBean.getThreshold();
        this.taskRuleSimple = taskRuleSimple;
        this.status = AlarmConfigStatusEnum.NOT_CHECK.getCode();
        this.ruleMetric = taskRuleAlarmConfigBean.getRuleMetric();
        this.uploadAbnormalValue = taskRuleAlarmConfigBean.getUploadAbnormalValue();
        this.uploadRuleMetricValue = taskRuleAlarmConfigBean.getUploadRuleMetricValue();
        this.deleteFailCheckResult = taskRuleAlarmConfigBean.getDeleteFailCheckResult();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public String getOutputUnit() {
        return outputUnit;
    }

    public void setOutputUnit(String outputUnit) {
        this.outputUnit = outputUnit;
    }

    public Integer getCheckTemplate() {
        return checkTemplate;
    }

    public void setCheckTemplate(Integer checkTemplate) {
        this.checkTemplate = checkTemplate;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public Integer getCompareType() {
        return compareType;
    }

    public void setCompareType(Integer compareType) {
        this.compareType = compareType;
    }

    public TaskRuleSimple getTaskRuleSimple() {
        return taskRuleSimple;
    }

    public void setTaskRuleSimple(TaskRuleSimple taskRuleSimple) {
        this.taskRuleSimple = taskRuleSimple;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public RuleMetric getRuleMetric() {
        return ruleMetric;
    }

    public void setRuleMetric(RuleMetric ruleMetric) {
        this.ruleMetric = ruleMetric;
    }

    public Boolean getUploadRuleMetricValue() {
        return uploadRuleMetricValue;
    }

    public void setUploadRuleMetricValue(Boolean uploadRuleMetricValue) {
        this.uploadRuleMetricValue = uploadRuleMetricValue;
    }

    public Boolean getUploadAbnormalValue() {
        return uploadAbnormalValue;
    }

    public void setUploadAbnormalValue(Boolean uploadAbnormalValue) {
        this.uploadAbnormalValue = uploadAbnormalValue;
    }

    public Boolean getDeleteFailCheckResult() {
        return deleteFailCheckResult;
    }

    public void setDeleteFailCheckResult(Boolean deleteFailCheckResult) {
        this.deleteFailCheckResult = deleteFailCheckResult;
    }

    @Override
    public String toString() {
        return "TaskRuleAlarmConfig{" +
                "id=" + id +
                ", outputName='" + outputName + '\'' +
                ", checkTemplate=" + checkTemplate +
                ", threshold=" + threshold +
                ", compareType=" + compareType +
                ", status=" + status +
                '}';
    }
}
