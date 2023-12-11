package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2021/5/17 17:00
 */
public class DeleteBatchRuleMetricRequest {
    @JsonProperty("rule_metric_ids")
    private List<Long> ruleMetricIds;

    public DeleteBatchRuleMetricRequest() {
        // Default Constructor
    }

    public DeleteBatchRuleMetricRequest(List<Long> ruleMetricIds) {
        this.ruleMetricIds = ruleMetricIds;
    }

    public List<Long> getRuleMetricIds() {
        return ruleMetricIds;
    }

    public void setRuleMetricIds(List<Long> ruleMetricIds) {
        this.ruleMetricIds = ruleMetricIds;
    }

    public static void checkRequest(DeleteBatchRuleMetricRequest deleteBatchRuleMetricRequest) throws UnExpectedRequestException {
        CommonChecker.checkObject(deleteBatchRuleMetricRequest, "Delete Request");
        CommonChecker.checkCollections(deleteBatchRuleMetricRequest.getRuleMetricIds(), "Delete Rule Metric IDs");
    }
}
