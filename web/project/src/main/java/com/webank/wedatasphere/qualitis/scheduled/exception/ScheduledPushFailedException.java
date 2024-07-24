package com.webank.wedatasphere.qualitis.scheduled.exception;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-18 15:05
 * @description
 */
public class ScheduledPushFailedException extends Exception{
    public ScheduledPushFailedException(String message) {
        super(message);
    }
}
