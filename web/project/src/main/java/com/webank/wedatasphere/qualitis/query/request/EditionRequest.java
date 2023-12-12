package com.webank.wedatasphere.qualitis.query.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author v_gaojiedeng@webank.com
 */
public class EditionRequest {

    @JsonProperty("edition_id")
    private Long editionId;

    private int page;
    private int size;

    public EditionRequest() {
        this.page = 0;
        this.size = 15;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Long getEditionId() {
        return editionId;
    }

    public void setEditionId(Long editionId) {
        this.editionId = editionId;
    }

    public static void checkRequest(EditionRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        CommonChecker.checkObject(request.getEditionId(), "edition_id");

        long page = request.getPage();
        long size = request.getSize();
        if (page < 0) {
            throw new UnExpectedRequestException("page should >= 0, request: " + request);
        }
        if (size <= 0) {
            throw new UnExpectedRequestException("size should > 0, request: " + request);
        }
    }



}
