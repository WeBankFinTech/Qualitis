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

    @Column(name = "check_template")
    private Integer checkTemplate;

    private Double threshold;

    @Column(name = "compare_type")
    private Integer compareType;

    private Integer status;

    @ManyToOne
    private TaskRuleSimple taskRuleSimple;

    public TaskRuleAlarmConfig() {
    }

    public TaskRuleAlarmConfig(TaskRuleAlarmConfigBean taskRuleAlarmConfigBean, TaskRuleSimple taskRuleSimple) {
        this.outputName = taskRuleAlarmConfigBean.getOutputName();
        this.checkTemplate = taskRuleAlarmConfigBean.getCheckTemplate();
        this.threshold = taskRuleAlarmConfigBean.getThreshold();
        this.compareType = taskRuleAlarmConfigBean.getCompareType();
        this.taskRuleSimple = taskRuleSimple;
        this.status = AlarmConfigStatusEnum.NOT_CHECK.getCode();
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
