package com.webank.wedatasphere.qualitis.constant;


/**
 * @author v_wenxuanzhang
 */
public enum ImsAlarmLevelEnum {
    /**
     告警级别 1 表示 CRITICAL，2 表示 MAJOR，3 表示 MINOR，4 表示 WARNING，5 表示 INFO
     */
    CRITICAL(1, "CRITICAL"),
    MAJOR(2,"MAJOR"),
    MINOR(3,"MINOR"),
    WARNING(4,"WARNING"),
    INFO(5,"INFO");

    private int code;
    private String alarmLevel;

    ImsAlarmLevelEnum(int code,String alarmLevel) {
        this.code = code;
        this.alarmLevel = alarmLevel;
    }

    public int getCode() {
        return code;
    }
    public String getAlarmLevel() {
        return alarmLevel;
    }

    public static ImsAlarmLevelEnum match(int code) {
        ImsAlarmLevelEnum[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            ImsAlarmLevelEnum item = var1[var3];
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }
}
