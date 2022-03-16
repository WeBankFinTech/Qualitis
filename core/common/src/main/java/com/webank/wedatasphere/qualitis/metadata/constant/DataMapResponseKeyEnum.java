package com.webank.wedatasphere.qualitis.metadata.constant;

/**
 * @author allenzhou@webank.com
 * @date 2021/6/4 18:07
 */
public enum DataMapResponseKeyEnum {
    /**
     * Linkis response map object key enum.
     */
    PROGRESS("progress"),
    CODE("code"),
    TASK("task"),
    DATA("data")
        ;

    private String key;

    DataMapResponseKeyEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
