package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.List;

/**
 * @author allenzhou
 */
public class RuleNodeRequests {
    private Long newProjectId;
    private String userName;
    @JsonProperty("rule_node_rule")
    private List<RuleNodeRequest> ruleNodeRequests;

    public RuleNodeRequests() {
    }

    public Long getNewProjectId() {
        return newProjectId;
    }

    public void setNewProjectId(Long newProjectId) {
        this.newProjectId = newProjectId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<RuleNodeRequest> getRuleNodeRequests() {
        return ruleNodeRequests;
    }

    public void setRuleNodeRequests(List<RuleNodeRequest> ruleNodeRequests) {
        this.ruleNodeRequests = ruleNodeRequests;
    }

    @Override
    public String toString() {
        return "RuleNodeRequests{" +
            "ruleNodeRequests=" + Arrays.toString(ruleNodeRequests.toArray()) +
            '}';
    }
}
