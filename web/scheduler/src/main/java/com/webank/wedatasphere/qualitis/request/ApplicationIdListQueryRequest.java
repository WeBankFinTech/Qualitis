package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author v_wenxuanzhang
 */
public class ApplicationIdListQueryRequest {
    @JsonProperty("applicationIdList")
    private List<String> applicationIdList;

    public ApplicationIdListQueryRequest() {
        // Default do nothing.
    }

    public List< String > getApplicationIdList() {
        return applicationIdList;
    }

    public void setApplicationIdList(List< String > applicationIdList) {
        this.applicationIdList = applicationIdList;
    }
}
