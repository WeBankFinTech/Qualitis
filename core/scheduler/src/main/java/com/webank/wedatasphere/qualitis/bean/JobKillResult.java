package com.webank.wedatasphere.qualitis.bean;

/**
 * @author allenzhou
 */
public class JobKillResult {
    private String message;

    public JobKillResult() {
    }

    public JobKillResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
