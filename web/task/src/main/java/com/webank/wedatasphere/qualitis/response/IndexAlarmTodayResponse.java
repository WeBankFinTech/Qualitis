package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.entity.AlarmInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * @author v_wblwyan
 * @date 2018-11-14
 */
public class IndexAlarmTodayResponse {

    @JsonProperty("alarm_critical_num")
    private long alarmCriticalNum;
    @JsonProperty("alarm_major_num")
    private long alarmMajorNum;
    @JsonProperty("alarm_minor_num")
    private long alarmMinorNum;
    @JsonProperty("alarm_warning_num")
    private long alarmWarningNum;
    @JsonProperty("alarm_info_num")
    private long alarmInfoNum;

    @JsonProperty("total_num")
    private long totalNum;

    private List<IndexAlarmResponse> alarms;

    public IndexAlarmTodayResponse() {
        // Default Constructor
    }

    public IndexAlarmTodayResponse(List<AlarmInfo> alarmInfos, long totalNum) {
        this.totalNum = totalNum;
        alarms = new ArrayList<>();
        for (AlarmInfo alarmInfo : alarmInfos) {
            IndexAlarmResponse alarmResponse = new IndexAlarmResponse(alarmInfo);
            alarms.add(alarmResponse);
        }
    }

    public long getAlarmCriticalNum() {
        return alarmCriticalNum;
    }

    public void setAlarmCriticalNum(long alarmCriticalNum) {
        this.alarmCriticalNum = alarmCriticalNum;
    }

    public long getAlarmMajorNum() {
        return alarmMajorNum;
    }

    public void setAlarmMajorNum(long alarmMajorNum) {
        this.alarmMajorNum = alarmMajorNum;
    }

    public long getAlarmMinorNum() {
        return alarmMinorNum;
    }

    public void setAlarmMinorNum(long alarmMinorNum) {
        this.alarmMinorNum = alarmMinorNum;
    }

    public long getAlarmWarningNum() {
        return alarmWarningNum;
    }

    public void setAlarmWarningNum(long alarmWarningNum) {
        this.alarmWarningNum = alarmWarningNum;
    }

    public long getAlarmInfoNum() {
        return alarmInfoNum;
    }

    public void setAlarmInfoNum(long alarmInfoNum) {
        this.alarmInfoNum = alarmInfoNum;
    }

    public long getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(long totalNum) {
        this.totalNum = totalNum;
    }

    public List<IndexAlarmResponse> getAlarms() {
        return alarms;
    }

    public void setAlarms(List<IndexAlarmResponse> alarms) {
        this.alarms = alarms;
    }
}
