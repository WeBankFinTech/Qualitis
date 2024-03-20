package com.webank.wedatasphere.qualitis.constant;

/**
 * @author v_minminghe@webank.com
 * @date 2023-06-13 15:43
 * @description
 */
public enum DepartmentSourceTypeEnum {
    /**
     * HR（0, "hr"），CUSTOM（1, "custom"）
     */
    HR(0, "hr"),
    CUSTOM(1, "custom");

    private Integer code;
    public String value;

    DepartmentSourceTypeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public static DepartmentSourceTypeEnum fromCode(Integer code) {
        for (DepartmentSourceTypeEnum departmentSourceTypeEnum : DepartmentSourceTypeEnum.values()) {
            if (departmentSourceTypeEnum.getCode().equals(code)) {
                return departmentSourceTypeEnum;
            }
        }
        return null;
    }

}
