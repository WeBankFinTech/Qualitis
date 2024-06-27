package com.webank.wedatasphere.qualitis.constant;


/**
 * @author v_wenxuanzhang
 */
public enum ImsAlarmStatusEnum {
    /**
     是否已告警，0表示未告警，1表示已告警
     */
    NOALARM(0, "未告警"),
    ALARMED(1,"已告警");

    private int code;
    private String alarmStatus;

    ImsAlarmStatusEnum(int code, String alarmStatus) {
        this.code = code;
        this.alarmStatus = alarmStatus;
    }

    public int getCode() {
        return code;
    }
    public String getAlarmStatus() {
        return alarmStatus;
    }

    public static ImsAlarmStatusEnum match(int code) {
        ImsAlarmStatusEnum[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            ImsAlarmStatusEnum item = var1[var3];
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }
}
