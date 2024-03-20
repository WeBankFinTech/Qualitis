package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2021/11/2 10:50
 */
public class ConnectParams {
    private String username;
    private String password;
    private String host;
    private String port;
    /**
     * 连接参数
     */
    private String connectParam;
    private String appId;
    private String authType;
    private String objectId;
    private String mkPrivate;
    private String dk;
    @JsonProperty(value = "timestamp")
    private String timeStamp;

    public String getConnectParam() {
        return connectParam;
    }

    public void setConnectParam(String connectParam) {
        this.connectParam = connectParam;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getMkPrivate() {
        return mkPrivate;
    }

    public void setMkPrivate(String mkPrivate) {
        this.mkPrivate = mkPrivate;
    }

    public String getDk() {
        return dk;
    }

    public void setDk(String dk) {
        this.dk = dk;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

}
