package com.webank.wedatasphere.qualitis.rule.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * 实现 DSS appjoint 的 NodeSerivce 的导入导出接口，采用 json 序列化 rule 对象及其相关引用为字符串的方法，实现对象在服务间的传递
 * @author allenzhou
 */
public class RuleNodeResponses {
    @JsonProperty("rule_node_rule")
    private List<RuleNodeResponse> ruleNodeResponses;

    public RuleNodeResponses() {
    }

    public RuleNodeResponses(List<RuleNodeResponse> responses) {
        this.ruleNodeResponses = responses;
    }

    public List<RuleNodeResponse> getRuleNodeResponses() {
        return ruleNodeResponses;
    }

    public void setRuleNodeResponses(List<RuleNodeResponse> ruleNodeResponses) {
        this.ruleNodeResponses = ruleNodeResponses;
    }
}
