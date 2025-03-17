package com.webank.wedatasphere.qualitis.scheduled.constant;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-18 9:44
 * @description
 */
public enum ScheduledTaskTypeEnum {
    /**
     * 关联自调度系统的任务
     */
    RELATION(1, "关联任务"),

    /**
     * 本地新建任务
     */
    PUBLISH(2, "发布任务");

    private Integer code;
    private String name;

    ScheduledTaskTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static ScheduledTaskTypeEnum fromCode(Integer code) {
        for (ScheduledTaskTypeEnum scheduledTaskType: ScheduledTaskTypeEnum.values()) {
            if (scheduledTaskType.getCode().equals(code)) {
                return scheduledTaskType;
            }
        }
        return null;
    }
}
