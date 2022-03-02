package com.webank.wedatasphere.qualitis.constant;

/**
 * @author allenzhou@webank.com
 * @date 2021/6/4 18:07
 */
public enum LinkisResponseKeyEnum {
    /**
     * Linkis response map object key enum.
     */
    ERROR_CODE("errCode"),
    PROGRESS("progress"),
    STATUS("status"),
    TASK("task"),
    DATA("data")
        ;

    private String key;

    LinkisResponseKeyEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
