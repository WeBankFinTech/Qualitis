package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2021/5/17 17:00
 */
public class DownloadRuleMetricRequest {
    @JsonProperty("rule_metric_ids")
    private List<Long> ruleMetricIds;

    public DownloadRuleMetricRequest() {
    }

    public DownloadRuleMetricRequest(List<Long> ruleMetricIds) {
        this.ruleMetricIds = ruleMetricIds;
    }

    public List<Long> getRuleMetricIds() {
        return ruleMetricIds;
    }

    public void setRuleMetricIds(List<Long> ruleMetricIds) {
        this.ruleMetricIds = ruleMetricIds;
    }

    public static void checkRequest(DownloadRuleMetricRequest downloadRuleMetricRequest) throws UnExpectedRequestException {
        CommonChecker.checkObject(downloadRuleMetricRequest, "Download Request");
        CommonChecker.checkCollections(downloadRuleMetricRequest.getRuleMetricIds(), "Download Rule Metric IDs");
    }
}
