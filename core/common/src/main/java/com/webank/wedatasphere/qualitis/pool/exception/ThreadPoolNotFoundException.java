package com.webank.wedatasphere.qualitis.pool.exception;

/**
 * @author v_minminghe@webank.com
 * @date 2024-10-24 9:53
 * @description
 */
public class ThreadPoolNotFoundException extends Exception {

    public ThreadPoolNotFoundException() {
        super();
    }

    public ThreadPoolNotFoundException(String message) {
        super(message);
    }

    public ThreadPoolNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ThreadPoolNotFoundException(Throwable cause) {
        super(cause);
    }
}
