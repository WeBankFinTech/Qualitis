package com.webank.wedatasphere.qualitis.constant;

/**
 * @author v_minminghe@webank.com
 * @date 2024-03-20 16:11
 * @description
 */
public enum AlertWayEnum {

    DEFAULT(0, "不发送"),
    RTX(1, "企业微信"),
    EMAIL(2, "email"),
    WECHAT(3, "个人微信"),
    ERP(4, "企业微信群");

    private Integer code;
    private String desc;

    AlertWayEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
