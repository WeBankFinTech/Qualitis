package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author allenzhou@webank.com
 * @date 2022/1/7 14:40
 */
public class GatewayJobInfoResponse {
    @JsonProperty("application_num")
    private Integer applicationNum;
    @JsonProperty("application_query_timeout")
    private Integer applicationQueryTimeOut;

    public GatewayJobInfoResponse() {
    }

    public GatewayJobInfoResponse(Integer applicationNum) {
        this.applicationNum = applicationNum;
    }

    public GatewayJobInfoResponse(Integer applicationNum, Integer applicationQueryTimeOut) {
        this.applicationNum = applicationNum;
        this.applicationQueryTimeOut = applicationQueryTimeOut;
    }

    public Integer getApplicationNum() {
        return applicationNum;
    }

    public void setApplicationNum(Integer applicationNum) {
        this.applicationNum = applicationNum;
    }

    public Integer getApplicationQueryTimeOut() {
        return applicationQueryTimeOut;
    }

    public void setApplicationQueryTimeOut(Integer applicationQueryTimeOut) {
        this.applicationQueryTimeOut = applicationQueryTimeOut;
    }

}
