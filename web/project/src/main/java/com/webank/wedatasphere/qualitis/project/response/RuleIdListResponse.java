package com.webank.wedatasphere.qualitis.project.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public class RuleIdListResponse {

    @JsonProperty("rule_id_list")
    private String ruleIdList;

    public String getRuleIdList() {
        return ruleIdList;
    }

    public void setRuleIdList(String ruleIdList) {
        this.ruleIdList = ruleIdList;
    }

    public RuleIdListResponse(List<Long> ruleIdList) {
        Gson gson = new Gson();
        this.ruleIdList= gson.toJson(ruleIdList);
    }
}
