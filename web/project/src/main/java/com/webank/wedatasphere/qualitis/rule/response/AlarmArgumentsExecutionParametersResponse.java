package com.webank.wedatasphere.qualitis.rule.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_gaojiedeng@webank.com
 */
public class AlarmArgumentsExecutionParametersResponse {

    @JsonProperty("alarm_id")
    private Long alarmId;

    @JsonProperty("alarm_event")
    private Integer alarmEvent;

    @JsonProperty("alarm_level")
    private Integer alarmLevel;

    @JsonProperty("alarm_receiver")
    private String alarmReceiver;

    public Long getAlarmId() {
        return alarmId;
    }
    public void setAlarmId(Long alarmId) {
        this.alarmId = alarmId;
    }

    public Integer getAlarmEvent() {
        return alarmEvent;
    }

    public void setAlarmEvent(Integer alarmEvent) {
        this.alarmEvent = alarmEvent;
    }

    public Integer getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(Integer alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getAlarmReceiver() {
        return alarmReceiver;
    }

    public void setAlarmReceiver(String alarmReceiver) {
        this.alarmReceiver = alarmReceiver;
    }
}
