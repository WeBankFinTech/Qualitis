package com.webank.wedatasphere.qualitis.scheduled.constant;

/**
 * @author v_minminghe@webank.com
 * @date 2023-04-13 11:01
 * @description
 */
public enum ScheduledReleaseLevelEnum {
    /**
     * Scheduled Release Level
     */
    PROJECT(0, "项目"),
    WORK_FLOW(1, "工作流"),
    TASK(2, "任务");

    private Integer code;
    private String name;

    ScheduledReleaseLevelEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }


}
