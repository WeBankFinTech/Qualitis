package com.webank.wedatasphere.qualitis.scheduled.constant;

/**
 * @author v_minminghe@webank.com
 * @date 2022-11-02 11:16
 * @description
 */
public enum SignalTypeEnum {
    /**
     * 1 接收信号
     * 6 WTSS发送内部信号
     * 7 RMB发送信号
     */
    EVENT_RECEIVE(1, "接收信号"),
    EVENT_SEND(6,"WTSS发送内部信号"),
    RMB_SEND(7,"RMB发送信号");

    private Integer code;
    private String name;

    SignalTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static SignalTypeEnum fromCode(Integer code) {
        for (SignalTypeEnum signalTypeEnum: SignalTypeEnum.values()) {
            if (signalTypeEnum.code.equals(code)) {
                return signalTypeEnum;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }
}
