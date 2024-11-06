package com.webank.wedatasphere.qualitis.request.user;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author v_minminghe@webank.com
 * @date 2024-03-26 18:04
 * @description
 */
public class ItsmUserRequest {
    @JsonProperty("oper_type")
    private String operType;
    private String webankName;
    @JsonProperty("proxy_user")
    private String proxyUser;
    private String depName;
    private String postName;
    private String orgName;
    @JsonProperty("env_type")
    private String envType;
    @JsonProperty("cluster_type")
    private String clusterType;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getEnvType() {
        return envType;
    }

    public void setEnvType(String envType) {
        this.envType = envType;
    }

    public String getClusterType() {
        return clusterType;
    }

    public void setClusterType(String clusterType) {
        this.clusterType = clusterType;
    }

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public String getWebankName() {
        return webankName;
    }

    public void setWebankName(String webankName) {
        this.webankName = webankName;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }
}
