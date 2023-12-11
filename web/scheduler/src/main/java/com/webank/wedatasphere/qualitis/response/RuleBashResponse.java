package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author allenzhou@webank.com
 * @date 2022/3/7 12:25
 */
public class RuleBashResponse {
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;

    @JsonProperty("bash_content")
    private String bashContent;

    public RuleBashResponse() {
        // Default do nothing.
    }

    public RuleBashResponse(List<Rule> ruleList) {
        StringBuilder bashContentStringBuffer = new StringBuilder();

        for (Rule rule : ruleList) {
            if (StringUtils.isNotEmpty(rule.getBashContent()) && Boolean.TRUE.equals(rule.getEnable())) {
                bashContentStringBuffer.append(rule.getBashContent()).append("\n");
            }
        }

        this.bashContent = bashContentStringBuffer.toString();
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public String getBashContent() {
        return bashContent;
    }

    public void setBashContent(String bashContent) {
        this.bashContent = bashContent;
    }
}
