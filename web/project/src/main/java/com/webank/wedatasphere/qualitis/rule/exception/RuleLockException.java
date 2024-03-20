package com.webank.wedatasphere.qualitis.rule.exception;

/**
 * @author v_minminghe@webank.com
 * @date 2022-12-19 10:30
 * @description
 */
public class RuleLockException extends Exception {
    private Integer status;

    public RuleLockException(String message) {
        super(message);
        this.status = 403;
    }

    public RuleLockException(String message, Integer status) {
        super(message);
        this.status = status;
    }

}
