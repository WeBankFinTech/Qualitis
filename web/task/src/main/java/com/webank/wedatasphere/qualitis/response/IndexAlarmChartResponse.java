package com.webank.wedatasphere.qualitis.response;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.webank.wedatasphere.qualitis.constant.ImsLevelEnum;
import com.webank.wedatasphere.qualitis.entity.AlarmInfo;
import java.util.List;

/**
 * @author allenzhou
 * @date 2018-11-14
 */
public class IndexAlarmChartResponse {

    private String date;
    @JsonProperty("alarm_critical_num")
    private int alarmCriticalNum;
    @JsonProperty("alarm_major_num")
    private int alarmMajorNum;
    @JsonProperty("alarm_minor_num")
    private int alarmMinorNum;
    @JsonProperty("alarm_warning_num")
    private int alarmWarningNum;
    @JsonProperty("alarm_info_num")
    private int alarmInfoNum;

    public IndexAlarmChartResponse() {
    }

    public IndexAlarmChartResponse(String date) {
        this.date = date;
    }

    public IndexAlarmChartResponse(String date, List<AlarmInfo> alarmInfosOnDate) {
        this.date = date;
        for (AlarmInfo info : alarmInfosOnDate) {
            if (ImsLevelEnum.CRITICAL.getCode().equals(info.getAlarmLevel())) {
                alarmCriticalNum++;
            }
            if (ImsLevelEnum.MAJOR.getCode().equals(info.getAlarmLevel())) {
                alarmMajorNum++;
            }
            if (ImsLevelEnum.MINOR.getCode().equals(info.getAlarmLevel())) {
                alarmMinorNum++;
            }
            if (ImsLevelEnum.WARNING.getCode().equals(info.getAlarmLevel())) {
                alarmWarningNum++;
            }
            if (ImsLevelEnum.INFO.getCode().equals(info.getAlarmLevel())) {
                alarmInfoNum++;
            }
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAlarmCriticalNum() {
        return alarmCriticalNum;
    }

    public void setAlarmCriticalNum(int alarmCriticalNum) {
        this.alarmCriticalNum = alarmCriticalNum;
    }

    public int getAlarmMajorNum() {
        return alarmMajorNum;
    }

    public void setAlarmMajorNum(int alarmMajorNum) {
        this.alarmMajorNum = alarmMajorNum;
    }

    public int getAlarmMinorNum() {
        return alarmMinorNum;
    }

    public void setAlarmMinorNum(int alarmMinorNum) {
        this.alarmMinorNum = alarmMinorNum;
    }

    public int getAlarmWarningNum() {
        return alarmWarningNum;
    }

    public void setAlarmWarningNum(int alarmWarningNum) {
        this.alarmWarningNum = alarmWarningNum;
    }

    public int getAlarmInfoNum() {
        return alarmInfoNum;
    }

    public void setAlarmInfoNum(int alarmInfoNum) {
        this.alarmInfoNum = alarmInfoNum;
    }
}
