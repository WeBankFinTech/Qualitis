package com.webank.wedatasphere.qualitis.metadata.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2023-05-12 11:12
 * @description
 */
public class LinkisConnectParamsRequest {
    private String username;
    private String password;
    private String host;
    private String port;
    /**
     * 连接参数
     */
    @JsonProperty("connect_param")
    private String connectParam;
    @JsonProperty("app_id")
    private String appId;
    @JsonProperty("auth_type")
    private String authType;
    @JsonProperty("object_id")
    private String objectId;
    @JsonProperty("mk_private")
    private String mkPrivate;
    private String dk;
    @JsonProperty(value = "timestamp")
    private String timeStamp;

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    private List<String> envIdArray;

    public String getDk() {
        return dk;
    }

    public void setDk(String dk) {
        this.dk = dk;
    }

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

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public List<String> getEnvIdArray() {
        return envIdArray;
    }

    public void setEnvIdArray(List<String> envIdArray) {
        this.envIdArray = envIdArray;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getMkPrivate() {
        return mkPrivate;
    }

    public void setMkPrivate(String mkPrivate) {
        this.mkPrivate = mkPrivate;
    }
}
