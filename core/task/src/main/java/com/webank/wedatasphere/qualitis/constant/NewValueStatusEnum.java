package com.webank.wedatasphere.qualitis.constant;

/**
 * @author v_gaojiedeng@webank.com
 */
public enum NewValueStatusEnum {
    /**
     *  （未处理/已录入/已丢弃）
     */
    UNTREATED(1, "未处理"),
    ENTERED(2, "已录入"),
    DISCARDED(3, "已丢弃");

    private Integer code;
    private String message;

    NewValueStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(Integer status) {
        for (NewValueStatusEnum newValueStatusEnum : NewValueStatusEnum.values()) {
            if (newValueStatusEnum.getCode().equals(status)) {
                return newValueStatusEnum.getMessage();
            }
        }
        return "Not support status code";
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
