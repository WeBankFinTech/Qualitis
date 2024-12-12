package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author allenzhou@webank.com
 * @date 2021/5/7 10:30
 */
public class EnvResponse {
    @JsonProperty("env_id")
    private Long envId;
    @JsonProperty("env_name")
    private String envName;

    public EnvResponse() {
       //default constructor
    }

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }
}
