package com.webank.wedatasphere.qualitis.project.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.metadata.request.LinkisConnectParamsRequest;

/**
 * @author v_minminghe@webank.com
 * @date 2023-11-02 10:54
 * @description
 */
public class OuterDataSourceVersionResponse {

    @JsonProperty("version_id")
    private Long versionId;
    private String comment;
    @JsonProperty("connect_params")
    private LinkisConnectParamsRequest connectParams;

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LinkisConnectParamsRequest getConnectParams() {
        return connectParams;
    }

    public void setConnectParams(LinkisConnectParamsRequest connectParams) {
        this.connectParams = connectParams;
    }
}
