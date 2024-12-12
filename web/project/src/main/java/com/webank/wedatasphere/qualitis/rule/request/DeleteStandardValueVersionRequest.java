package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author v_gaojiedeng@webank.com
 */
public class DeleteStandardValueVersionRequest {

    @JsonProperty("edition_id")
    private Long editionId;

    public Long getEditionId() {
        return editionId;
    }

    public void setEditionId(Long editionId) {
        this.editionId = editionId;
    }

    public static void checkRequest(DeleteStandardValueVersionRequest request)
            throws UnExpectedRequestException {
        CommonChecker.checkObject(request.getEditionId(), "edition_id");
    }


}
