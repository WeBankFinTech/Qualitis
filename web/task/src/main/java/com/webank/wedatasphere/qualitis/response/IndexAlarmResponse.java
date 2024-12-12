package com.webank.wedatasphere.qualitis.response;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.webank.wedatasphere.qualitis.entity.AlarmInfo;
import org.springframework.beans.BeanUtils;

/**
 * @author allenzhou
 * @date 2018-11-14
 */
public class IndexAlarmResponse {

    @JsonProperty("alarm_level")
    private String alarmLevel;
    @JsonProperty("alarm_reason")
    private String alarmReason;
    @JsonProperty("alarm_time")
    private String alarmTime;
    @JsonProperty("application_id")
    private String applicationId;
    @JsonProperty("application_begin_time")
    private String beginTime;
    @JsonProperty("application_end_time")
    private String endTime;
    @JsonProperty("project_name")
    private String projectName;

    public IndexAlarmResponse() {
    }

    public IndexAlarmResponse(AlarmInfo alarmInfo) {
        BeanUtils.copyProperties(alarmInfo, this);
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

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
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

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
