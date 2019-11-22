/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.rule.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author howeye
 */
public class RuleGroupResponse {

    @JsonProperty("rule_group_id")
    private Long ruleGroupId;
    @JsonProperty("rule_list")
    private List<RuleResponse> ruleList;

    public RuleGroupResponse() {

    }

    public RuleGroupResponse(Long ruleGroupId, List<RuleResponse> ruleList) {
        this.ruleGroupId = ruleGroupId;
        this.ruleList = ruleList;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public List<RuleResponse> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<RuleResponse> ruleList) {
        this.ruleList = ruleList;
    }
}