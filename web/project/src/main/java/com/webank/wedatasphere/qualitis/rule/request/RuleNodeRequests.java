package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.List;

/**
 * @author allenzhou
 */
public class RuleNodeRequests {
    private String csId;
    private String userName;
    private Long newProjectId;
    @JsonProperty("rule_node_rule")
    private List<RuleNodeRequest> ruleNodeRequestList;

    public RuleNodeRequests() {
        //Do nothing.
    }

    public String getCsId() {
        return csId;
    }

    public void setCsId(String csId) {
        this.csId = csId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getNewProjectId() {
        return newProjectId;
    }

    public void setNewProjectId(Long newProjectId) {
        this.newProjectId = newProjectId;
    }

    public List<RuleNodeRequest> getRuleNodeRequests() {
        return ruleNodeRequestList;
    }

    public void setRuleNodeRequests(List<RuleNodeRequest> ruleNodeRequests) {
        this.ruleNodeRequestList = ruleNodeRequests;
    }

    @Override
    public String toString() {
        return "RuleNodeRequests{" +
            "csId='" + csId + '\'' +
            ", userName='" + userName + '\'' +
            ", newProjectId=" + newProjectId +
            '}';
    }
}
