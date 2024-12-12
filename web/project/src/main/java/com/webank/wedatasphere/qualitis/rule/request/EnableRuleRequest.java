package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

import java.util.List;

public class EnableRuleRequest {

    @JsonProperty("rule_enable_list")
    private List<EnableRequest> ruleEnableList;

    public static void checkRequest(EnableRuleRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request,"request");
        for (EnableRequest enableRequest : request.getRuleEnableList()) {
            CommonChecker.checkObject(enableRequest.isRuleEnable(),"rule_enable");
        }
    }

    public List<EnableRequest> getRuleEnableList() {
        return ruleEnableList;
    }

    public void setRuleEnableList(List<EnableRequest> ruleEnableList) {
        this.ruleEnableList = ruleEnableList;
    }

    @Override
    public String toString() {
        return "EnableRuleRequest{" +
                "ruleEnableList=" + ruleEnableList +
                '}';
    }
}
