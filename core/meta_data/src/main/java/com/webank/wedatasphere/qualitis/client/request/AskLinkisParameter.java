package com.webank.wedatasphere.qualitis.client.request;

/**
 * @author v_gaojiedeng
 */
public class AskLinkisParameter {

    private String url;
    private String linkisToken;
    private String authUser;
    private String logmessage;

    public AskLinkisParameter() {
    }

    public AskLinkisParameter(String url, String linkisToken, String authUser, String logmessage) {
        this.url = url;
        this.linkisToken = linkisToken;
        this.authUser = authUser;
        this.logmessage = logmessage;
    }

    public String getLogmessage() {
        return logmessage;
    }

    public void setLogmessage(String logmessage) {
        this.logmessage = logmessage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLinkisToken() {
        return linkisToken;
    }

    public void setLinkisToken(String linkisToken) {
        this.linkisToken = linkisToken;
    }

    public String getAuthUser() {
        return authUser;
    }

    public void setAuthUser(String authUser) {
        this.authUser = authUser;
    }
}
