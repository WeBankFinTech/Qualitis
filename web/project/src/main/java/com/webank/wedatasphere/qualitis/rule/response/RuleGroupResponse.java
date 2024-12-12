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
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
public class RuleGroupResponse {
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;

    @JsonProperty("rule_group_name")
    private String ruleGroupName;

    @JsonProperty("rule_list")
    private List<RuleResponse> ruleList;

    private List<DataSourceResponse> datasource;

    @JsonProperty("save_datasource")
    private Boolean saveDatasource;
    @JsonProperty("context_service")
    private boolean contextService;
    @JsonProperty("cs_id")
    private String csId;
    @JsonProperty("node_name")
    private String nodeName;

    private String message;
    private String code;

    public RuleGroupResponse() {

    }

    public RuleGroupResponse(RuleGroup ruleGroup, List<RuleResponse> ruleList, List<Rule> rules) {
        this.ruleGroupId = ruleGroup.getId();
        this.ruleGroupName = ruleGroup.getRuleGroupName();

        if (CollectionUtils.isNotEmpty(rules)) {
            contextService = true;
            csId = rules.stream().map(Rule::getCsId).collect(Collectors.toSet()).iterator().next();
            nodeName = rules.stream().map(Rule::getNodeName).collect(Collectors.toSet()).iterator().next();
        } else {
            contextService = false;
        }

        if (CollectionUtils.isNotEmpty(ruleGroup.getRuleDataSources())) {
            saveDatasource = true;
        } else {
            saveDatasource = false;
        }
        if (CollectionUtils.isNotEmpty(ruleGroup.getRuleDataSources())) {
            datasource = new ArrayList<>(1);
            for (RuleDataSource ruleDataSource : ruleGroup.getRuleDataSources()) {
                datasource.add(new DataSourceResponse(ruleDataSource));
            }
        }
        this.ruleList = ruleList;
    }

    public RuleGroupResponse(RuleGroup savedRuleGroup) {
        datasource = new ArrayList<>(1);

        this.ruleGroupId = savedRuleGroup.getId();
        this.ruleGroupName = savedRuleGroup.getRuleGroupName();

        if (CollectionUtils.isNotEmpty(savedRuleGroup.getRuleDataSources())) {
            saveDatasource = true;
        } else {
            saveDatasource = false;
        }

        if (CollectionUtils.isNotEmpty(savedRuleGroup.getRuleDataSources())) {
            for (RuleDataSource ruleDataSource : savedRuleGroup.getRuleDataSources()) {
                datasource.add(new DataSourceResponse(ruleDataSource));
            }
        }
    }

    public RuleGroupResponse(RuleGroup savedRuleGroup, String message) {
        datasource = new ArrayList<>(1);

        this.ruleGroupId = savedRuleGroup.getId();
        this.ruleGroupName = savedRuleGroup.getRuleGroupName();

        if (CollectionUtils.isNotEmpty(savedRuleGroup.getRuleDataSources())) {
            saveDatasource = true;
        } else {
            saveDatasource = false;
        }

        if (CollectionUtils.isNotEmpty(savedRuleGroup.getRuleDataSources())) {
            for (RuleDataSource ruleDataSource : savedRuleGroup.getRuleDataSources()) {
                datasource.add(new DataSourceResponse(ruleDataSource));
            }
        }
        this.message = message;
    }

    public RuleGroupResponse(String code, RuleGroup savedRuleGroup, String message) {
        datasource = new ArrayList<>(1);

        this.ruleGroupId = savedRuleGroup.getId();
        this.ruleGroupName = savedRuleGroup.getRuleGroupName();

        if (CollectionUtils.isNotEmpty(savedRuleGroup.getRuleDataSources())) {
            saveDatasource = true;
        } else {
            saveDatasource = false;
        }

        if (CollectionUtils.isNotEmpty(savedRuleGroup.getRuleDataSources())) {
            for (RuleDataSource ruleDataSource : savedRuleGroup.getRuleDataSources()) {
                datasource.add(new DataSourceResponse(ruleDataSource));
            }
        }
        this.message = message;
        this.code = code;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public void setRuleGroupName(String ruleGroupName) {
        this.ruleGroupName = ruleGroupName;
    }

    public List<RuleResponse> getRuleList() {
        return ruleList;
    }

    public void setRuleList(List<RuleResponse> ruleList) {
        this.ruleList = ruleList;
    }

    public List<DataSourceResponse> getDatasource() {
        return datasource;
    }

    public void setDatasource(List<DataSourceResponse> datasource) {
        this.datasource = datasource;
    }

    public Boolean getSaveDatasource() {
        return saveDatasource;
    }

    public void setSaveDatasource(Boolean saveDatasource) {
        this.saveDatasource = saveDatasource;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isContextService() {
        return contextService;
    }

    public void setContextService(boolean contextService) {
        this.contextService = contextService;
    }

    public String getCsId() {
        return csId;
    }

    public void setCsId(String csId) {
        this.csId = csId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
}