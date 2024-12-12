package com.webank.wedatasphere.qualitis.request;

/**
 * @author allenzhou@webank.com
 * @date 2022/2/23 16:10
 */
public class AddServiceInfoRequest {
    private String ip;

    public AddServiceInfoRequest() {
        // Do nothing.
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
