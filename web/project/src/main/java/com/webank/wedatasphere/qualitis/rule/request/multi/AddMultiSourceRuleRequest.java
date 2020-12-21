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

package com.webank.wedatasphere.qualitis.rule.request.multi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.rule.request.AlarmConfigRequest;

import java.util.List;

/**
 * @author howeye
 */
public class AddMultiSourceRuleRequest {

    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("multi_source_rule_template_id")
    private Long multiSourceRuleTemplateId;
    @JsonProperty("project_id")
    private Long projectId;
    private MultiDataSourceConfigRequest source;
    private MultiDataSourceConfigRequest target;
    private List<MultiDataSourceJoinConfigRequest> mappings;
    private String filter;
    @JsonProperty("alarm")
    private Boolean alarm;
    @JsonProperty("alarm_variable")
    private List<AlarmConfigRequest> alarmVariable;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;

    @JsonProperty("abort_on_failure")
    private Boolean abortOnFailure;


    public AddMultiSourceRuleRequest() {
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public Long getMultiSourceRuleTemplateId() {
        return multiSourceRuleTemplateId;
    }

    public void setMultiSourceRuleTemplateId(Long multiSourceRuleTemplateId) {
        this.multiSourceRuleTemplateId = multiSourceRuleTemplateId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public MultiDataSourceConfigRequest getSource() {
        return source;
    }

    public void setSource(MultiDataSourceConfigRequest source) {
        this.source = source;
    }

    public MultiDataSourceConfigRequest getTarget() {
        return target;
    }

    public void setTarget(MultiDataSourceConfigRequest target) {
        this.target = target;
    }

    public List<MultiDataSourceJoinConfigRequest> getMappings() {
        return mappings;
    }

    public void setMappings(List<MultiDataSourceJoinConfigRequest> mappings) {
        this.mappings = mappings;
    }

    public Boolean getAlarm() {
        return alarm;
    }

    public void setAlarm(Boolean alarm) {
        this.alarm = alarm;
    }

    public List<AlarmConfigRequest> getAlarmVariable() {
        return alarmVariable;
    }

    public void setAlarmVariable(List<AlarmConfigRequest> alarmVariable) {
        this.alarmVariable = alarmVariable;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public Boolean getAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(Boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
    }

    public static void checkRequest(AddMultiSourceRuleRequest request, Boolean modifyOrNot) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        CommonChecker.checkString(request.getRuleName(), "Rule name");
        CommonChecker.checkString(request.getClusterName(), "Cluster name");
        CommonChecker.checkObject(request.getMultiSourceRuleTemplateId(), "Multi source template id");
        MultiDataSourceConfigRequest.checkRequest(request.getSource());
        MultiDataSourceConfigRequest.checkRequest(request.getTarget());
        CommonChecker.checkCollections(request.getMappings(), "Mapping");
        for (MultiDataSourceJoinConfigRequest mapping : request.getMappings()) {
            MultiDataSourceJoinConfigRequest.checkRequest(mapping);
        }

        CommonChecker.checkObject(request.getAlarm(), "alarm");
        CommonChecker.checkObject(request.getAbortOnFailure(), "abort_on_failure");
        if (request.getAlarm()) {
            CommonChecker.checkObject(request.getAlarmVariable(), "alarm_variable");
            if (request.getAlarmVariable().isEmpty()) {
                throw new UnExpectedRequestException("alarm_variable can not be empty");
            }
        }

        if (!modifyOrNot) {
            CommonChecker.checkObject(request.getProjectId(), "Project id");
        }
    }
}
