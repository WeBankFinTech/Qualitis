package com.webank.wedatasphere.qualitis.project.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisConnectParamsRequest;

/**
 * @author v_minminghe@webank.com
 * @date 2023-10-24 9:39
 * @description
 */
public class OuterDataSourceEnvResponse {

    /**
     * 环境ID
     */
    private Long id;
    /**
     * 环境名称
     */
    @JsonProperty("env_name")
    private String envName;
    /**
     * 环境描述
     */
    @JsonProperty("env_desc")
    private String envDesc;
    @JsonProperty("dcn_num")
    private String dcnNum;
    @JsonProperty("logic_area")
    private String logicArea;
    /**
     * 非共享登录认证信息
     */
    @JsonProperty("connect_params")
    private LinkisConnectParamsRequest connectParams;

    public String getDcnNum() {
        return dcnNum;
    }

    public void setDcnNum(String dcnNum) {
        this.dcnNum = dcnNum;
    }

    public String getLogicArea() {
        return logicArea;
    }

    public void setLogicArea(String logicArea) {
        this.logicArea = logicArea;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public String getEnvDesc() {
        return envDesc;
    }

    public void setEnvDesc(String envDesc) {
        this.envDesc = envDesc;
    }

    public LinkisConnectParamsRequest getConnectParams() {
        return connectParams;
    }

    public void setConnectParams(LinkisConnectParamsRequest connectParams) {
        this.connectParams = connectParams;
    }
}
