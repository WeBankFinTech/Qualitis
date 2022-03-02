package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author allenzhou
 */
public class MulDbResponse {
    @JsonProperty("mul_db")
    private String dbs;

    public MulDbResponse(String dbs) {
        this.dbs = dbs;
    }

    public String getDbs() {
        return dbs;
    }

    public void setDbs(String dbs) {
        this.dbs = dbs;
    }
}
