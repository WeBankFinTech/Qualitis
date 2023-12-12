package com.webank.wedatasphere.qualitis.exception;

/**
 * @author allenzhou
 */
public class HttpMethodException extends RuntimeException {

    public HttpMethodException() {
        super();
    }

    public HttpMethodException(String message) {
        super(message);
    }

    public HttpMethodException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpMethodException(Throwable cause) {
        super(cause);
    }

    protected HttpMethodException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
