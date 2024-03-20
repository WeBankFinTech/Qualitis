package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.*;

/**
 * @author v_minminghe@webank.com
 * @date 2022-11-14 9:51
 * @description
 */
@Entity
@Table(name = "qualitis_application_task_result_status")
public class TaskResultStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_id")
    private String applicationId;

    @Column
    private Long ruleId;

    @OneToOne
    private TaskResult taskResult;

    @Column(name = "task_rule_alarm_config_id")
    private Long taskRuleAlarmConfigId;

    @Column
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public TaskResult getTaskResult() {
        return taskResult;
    }

    public void setTaskResult(TaskResult taskResult) {
        this.taskResult = taskResult;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Long getTaskRuleAlarmConfigId() {
        return taskRuleAlarmConfigId;
    }

    public void setTaskRuleAlarmConfigId(Long taskRuleAlarmConfigId) {
        this.taskRuleAlarmConfigId = taskRuleAlarmConfigId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }
}
