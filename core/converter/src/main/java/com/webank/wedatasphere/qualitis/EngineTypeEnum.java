package com.webank.wedatasphere.qualitis;

/**
 * @author allenzhou@webank.com
 * @date 2022/7/28 11:45
 */
public enum EngineTypeEnum {
    /**
     * 1 SHELL ENGINE
     * 2 SPARK ENGINE
     */
    DEFAULT_ENGINE(1, "shell"),
    SPARK_ENGINE(2, "spark");

    private Integer code;
    private String message;

    EngineTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
