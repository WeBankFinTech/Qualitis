package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_gaojiedeng@webank.com
 */
public class DeleteStandardVauleRequest {

    @JsonProperty("standard_value_id")
    private Long standardValueId;

    public DeleteStandardVauleRequest() {
    }

    public DeleteStandardVauleRequest(Long standardValueId) {
        this.standardValueId = standardValueId;
    }

    public Long getStandardValueId() {
        return standardValueId;
    }

    public void setStandardValueId(Long standardValueId) {
        this.standardValueId = standardValueId;
    }
}
