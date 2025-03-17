package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author v_wenxuanzhang
 */
//@Entity
//@Table(name = "qualitis_imsmetric_check_result")
public class ImsmetricCheckResult {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "metric_id")
    private Long metricId;

    @Column(name = "metric_value")
    private Double metricValue;

    @Column(name = "predict_value")
    private Double predictValue;

    @Column(name = "alarm_receivers")
    private String alarmReceivers;

    @Column(name = "alarm_level", columnDefinition = "TINYINT(5)")
    private Integer alarmLevel;

    @Column(name = "is_alarm", columnDefinition = "TINYINT(5)")
    private Integer isAlarm;

    @Column(name = "run_model", columnDefinition = "TINYINT(5)")
    private Integer runModel;

    @Column(name = "remark")
    private String remark;

    @Column(name = "batch_no")
    private String batchNo;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMetricId() {
        return metricId;
    }

    public void setMetricId(Long metricId) {
        this.metricId = metricId;
    }

    public Double getMetricValue() {
        return metricValue;
    }

    public void setMetricValue(Double metricValue) {
        this.metricValue = metricValue;
    }

    public Double getPredictValue() {
        return predictValue;
    }

    public void setPredictValue(Double predictValue) {
        this.predictValue = predictValue;
    }

    public String getAlarmReceivers() {
        return alarmReceivers;
    }

    public void setAlarmReceivers(String alarmReceivers) {
        this.alarmReceivers = alarmReceivers;
    }

    public Integer getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(Integer alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public Integer getIsAlarm() {
        return isAlarm;
    }

    public void setIsAlarm(Integer isAlarm) {
        this.isAlarm = isAlarm;
    }

    public Integer getRunModel() {
        return runModel;
    }

    public void setRunModel(Integer runModel) {
        this.runModel = runModel;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
