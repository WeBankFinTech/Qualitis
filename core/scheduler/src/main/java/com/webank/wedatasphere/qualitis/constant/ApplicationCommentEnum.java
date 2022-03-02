package com.webank.wedatasphere.qualitis.constant;

import org.apache.commons.lang.StringUtils;

/**
 * @author allenzhou@webank.com
 * @date 2021/7/14 1:12
 */
public enum ApplicationCommentEnum {
    /**
     * Thirteen type refinement error:
     */
    PERMISSION_ISSUES(1,"数据被修改或者权限问题", "Data has been modified or permission issues"),
    YARN_QUEUE_ISSUES(2,"队列权限问题", "Yarn queue permission issues"),
    MEMORY_ISSUES(3,"内存不足问题", "Not enough memory issues"),
    GRAMMAR_ISSUES(4,"校验语法问题", "SQL grammatical issues"),
    ENGINE_ISSUES(5,"请求引擎失败", "Request engine failed"),
    UNKNOWN_ERROR_ISSUES(6,"未知错误", "Unknown error"),
    DIFF_DATA_ISSUES(7,"数据不符合校验规则", "Data does not meet the rules"),
    LEFT_NULL_DATA_ISSUES(8,"左表存在为空的表或者分区", "Left ull table or partition"),
    SAME_ISSUES(9,"数据符合校验规则", "Data does meet the rules"),
    BOTH_NULL_ISSUES(10,"两个表都为空", "Both datasource null"),
    METADATA_ISSUES(11,"元数据信息接口异常，可能是数据不存在导致接口请求失败", "Metadata information interface is abnormal, maybe the datasource does not exist"),
    TIMEOUT_KILL(12,"任务被取消", "Task killed"),
    RIGHT_NULL_DATA_ISSUES(13,"右表存在为空的表或者分区", "Right null table or partition")
        ;

    private Integer code;
    private String zhMessage;
    private String enMessage;

    ApplicationCommentEnum(Integer code, String zhMessage, String enMessage) {
        this.code = code;
        this.zhMessage = zhMessage;
        this.enMessage = enMessage;
    }

    public Integer getCode() {
        return code;
    }

    public String getZhMessage() {
        return zhMessage;
    }

    public String getEnMessage() {
        return enMessage;
    }

    public static String getCommentName(Integer code) {
        for (ApplicationCommentEnum c : ApplicationCommentEnum.values()) {
            if (c.getCode().equals(code)) {
                return c.getZhMessage();
            }
        }
        return null;
    }

    public static String getCommentName(Integer code, String local) {
        if (StringUtils.isEmpty(local)) {
            return getCommentName(code);
        }
        for (ApplicationCommentEnum c : ApplicationCommentEnum.values()) {
            if (c.getCode().equals(code)) {
                if ("en_US".equals(local)) {
                    return c.getEnMessage();
                } else {
                    return c.getZhMessage();
                }
            }
        }
        return null;
    }

    public static Integer getCommentCode(String checkTemplateName, String local) {
        for (ApplicationCommentEnum c : ApplicationCommentEnum.values()) {
            if ("en_US".equals(local)) {
                if (c.getEnMessage().equals(checkTemplateName)) {
                    return c.getCode();
                }
            } else {
                if (c.getZhMessage().equals(checkTemplateName)) {
                    return c.getCode();
                }
            }
        }
        return null;
    }
}
