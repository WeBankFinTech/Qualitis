package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


/**
 * @author v_wenxuanzhang
 */
public class ImsAlarmDataQueryResponse {
    @JsonProperty("alarmData")
    private List<AlarmData> alarmData;

    public List<AlarmData> getAlarmData() {
        return alarmData;
    }

    public void setAlarmData(List<AlarmData> alarmData) {
        this.alarmData = alarmData;
    }
}
