package com.webank.wedatasphere.qualitis.metadata.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_minminghe@webank.com
 * @date 2024-07-17 17:59
 * @description
 */
public class BuzDomainResponse {

    @JsonProperty("appdomain_cnname")
    private String name;

    public BuzDomainResponse() {
//       doing nothing
    }

    public BuzDomainResponse(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
