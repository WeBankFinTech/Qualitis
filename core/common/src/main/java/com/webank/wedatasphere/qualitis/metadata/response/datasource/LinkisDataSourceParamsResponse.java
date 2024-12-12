package com.webank.wedatasphere.qualitis.metadata.response.datasource;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_minminghe@webank.com
 * @date 2023-05-15 9:49
 * @description
 */
public class LinkisDataSourceParamsResponse {

    @JsonProperty(value = "version")
    private Long versionId;

    public LinkisDataSourceParamsResponse() {
//        Doing something
    }

    public LinkisDataSourceParamsResponse(Long versionId) {
        this.versionId = versionId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }
}
