package com.webank.wedatasphere.qualitis.query.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author v_gaojiedeng@webank.com
 */
public class TaskNewValueRequest {

    @JsonProperty("rule_id")
    private Long ruleId;

    private int page;
    private int size;

    public TaskNewValueRequest() {
        this.page = 0;
        this.size = 15;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
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

    public static void checkRequest(TaskNewValueRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
    }
}
