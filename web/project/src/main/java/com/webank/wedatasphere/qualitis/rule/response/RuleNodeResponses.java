package com.webank.wedatasphere.qualitis.rule.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * 实现 Dss Appconn 的 NodeSerivce 的导入导出接口，采用 json 序列化 rule 对象及其相关引用为字符串的方法，实现对象在服务间的传递
 * @author allenzhou
 */
public class RuleNodeResponses {
    @JsonProperty("rule_node_rule")
    private List<RuleNodeResponse> ruleNodeResponseList;

    public RuleNodeResponses() {
        // Default do nothing.
    }

    public RuleNodeResponses(List<RuleNodeResponse> responses) {
        this.ruleNodeResponseList = responses;
    }

    public List<RuleNodeResponse> getRuleNodeResponses() {
        return ruleNodeResponseList;
    }

    public void setRuleNodeResponses(List<RuleNodeResponse> ruleNodeResponseList) {
        this.ruleNodeResponseList = ruleNodeResponseList;
    }
}
