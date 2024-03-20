package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.beans.BeanUtils;

/**
 * @author allenzhou
 */
@Entity
@Table(name = "qualitis_alarm_info")
public class AlarmInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "alarm_level", length = 1)
    private String alarmLevel;
    @Column(name = "alarm_reason", columnDefinition = "TEXT")
    private String alarmReason;
    @Column(name = "application_id", length = 40)
    private String applicationId;
    @Column(name = "begin_time", length = 20)
    private String beginTime;
    @Column(name = "end_time", length = 20)
    private String endTime;
    @Column(name = "alarm_time", length = 20)
    private String alarmTime;
    @Column(length = 50)
    private String username;
    @Column(name = "alarm_type")
    private Integer alarmType;
    @Column(name = "task_id")
    private Integer taskId;
    @Column(name = "project_name")
    private String projectName;

    public AlarmInfo() {
    }

    public AlarmInfo(AlarmInfo alarmInfo) {
        BeanUtils.copyProperties(alarmInfo, this);
    }

    public AlarmInfo(String alarmLevel, String alarmReason, String applicationId, String beginTime, String endTime, String alarmTime,
        String username, int alarmType, String projectName) {
        this.alarmLevel = alarmLevel;
        this.alarmReason = alarmReason;
        this.applicationId = applicationId;
        this.beginTime = beginTime;
        this.endTime = endTime;
        this.alarmTime = alarmTime;
        this.username = username;
        this.alarmType = alarmType;
        this.projectName = projectName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(String alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getAlarmReason() {
        return alarmReason;
    }

    public void setAlarmReason(String alarmReason) {
        this.alarmReason = alarmReason;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
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

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(Integer alarmType) {
        this.alarmType = alarmType;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
