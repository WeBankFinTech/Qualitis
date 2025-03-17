package com.webank.wedatasphere.qualitis.scheduled.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledSignal;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 * @author v_minminghe@webank.com
 * @date 2022-11-02 11:11
 * @description
 */
public class ScheduledSignalParameterResponse {
    private Long id;
    private Integer type;
    private String name;
    @JsonProperty("rule_group_list")
    private List<RuleGroupResponse> ruleGroupList;
    @JsonIgnore
    private List<Long> ruleGroupIds;
    @JsonProperty("content_json")
    private String contentJson;

    public ScheduledSignalParameterResponse(ScheduledSignal scheduledSignal) {
        this.id = scheduledSignal.getId();
        this.type = scheduledSignal.getType();
        this.name = scheduledSignal.getName();
        this.contentJson = scheduledSignal.getContentJson();
        if (StringUtils.isNotBlank(scheduledSignal.getRuleGroupIds())) {
            String[] currentRuleGroupIds = StringUtils.split(scheduledSignal.getRuleGroupIds(), SpecCharEnum.COMMA.getValue());
            this.ruleGroupIds = Arrays.stream(currentRuleGroupIds).map(Long::valueOf).collect(Collectors.toList());
        }
    }

    public List<Long> getRuleGroupIds() {
        return ruleGroupIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<RuleGroupResponse> getRuleGroupList() {
        return ruleGroupList;
    }

    public void setRuleGroupList(List<RuleGroupResponse> ruleGroupList) {
        this.ruleGroupList = ruleGroupList;
    }

    public String getContentJson() {
        return contentJson;
    }

    public void setContentJson(String contentJson) {
        this.contentJson = contentJson;
    }
}
