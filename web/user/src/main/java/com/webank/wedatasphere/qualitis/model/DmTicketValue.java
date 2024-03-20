package com.webank.wedatasphere.qualitis.model;

/**
 * @author allenzhou@webank.com
 * @date 2022/6/22 16:10
 */
public class DmTicketValue {
    private String loginFullName;
    private String loginName;
    private long expires;
    private String hash;

    public DmTicketValue() {
    }

    public DmTicketValue(String loginFullName, String loginName) {
        this.loginFullName = loginFullName;
        this.loginName = loginName;
    }

    public String getLoginFullName() {
        return loginFullName;
    }

    public void setLoginFullName(String loginFullName) {
        this.loginFullName = loginFullName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
