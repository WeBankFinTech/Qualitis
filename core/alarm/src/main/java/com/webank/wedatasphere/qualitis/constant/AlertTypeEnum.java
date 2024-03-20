package com.webank.wedatasphere.qualitis.constant;

/**
 * @author allenzhou
 */
public enum AlertTypeEnum {
    /**
     * 告警消息类型
     */
    TASK_TIME_OUT(1, "Task time out", "任务运行时间过长"),
    TASK_FAILED(2, "Task failed", "任务执行失败"),
    TASK_FAIL_CHECKOUT(3, "Task failed to checkout", "任务执行成功,未通过阈值校验"),
    TASK_INIT_FAIL(4, "Task failed to initial", "任务初始化失败"),
    TASK_SUCCESS(5, "Task success", "任务执行成功")
    ;

    private int code;
    private String status;
    private String message;

    AlertTypeEnum(int code, String status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
