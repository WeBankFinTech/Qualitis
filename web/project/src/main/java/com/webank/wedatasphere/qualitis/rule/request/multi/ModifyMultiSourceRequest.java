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
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author howeye
 */
public class ModifyMultiSourceRequest {

    @JsonProperty("rule_id")
    private Long ruleId;
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("multi_source_rule_template_id")
    private Long multiSourceRuleTemplateId;
    private MultiDataSourceConfigRequest source;
    private MultiDataSourceConfigRequest target;
    private List<MultiDataSourceJoinConfigRequest> mappings;
    @JsonProperty("has_filter")
    private Boolean hasFilter;
    private String filter;
    @JsonProperty("alarm")
    private Boolean alarm;
    @JsonProperty("alarm_variable")
    private List<AlarmConfigRequest> alarmVariable;

    public ModifyMultiSourceRequest() {
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
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

    public Boolean getHasFilter() {
        return hasFilter;
    }

    public void setHasFilter(Boolean hasFilter) {
        this.hasFilter = hasFilter;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
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

    public static void checkRequest(ModifyMultiSourceRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkObject(request.getRuleId(), "rule_id");
        AddMultiSourceRuleRequest addMultiSourceRuleRequest = new AddMultiSourceRuleRequest();
        BeanUtils.copyProperties(request, addMultiSourceRuleRequest);
        AddMultiSourceRuleRequest.checkRequest(addMultiSourceRuleRequest, true);
    }

    @Override
    public String toString() {
        return "ModifyMultiSourceRequest{" +
                "ruleId=" + ruleId +
                ", ruleName='" + ruleName + '\'' +
                ", clusterName='" + clusterName + '\'' +
                ", multiSourceRuleTemplateId=" + multiSourceRuleTemplateId +
                ", source=" + source +
                ", target=" + target +
                ", mappings=" + mappings +
                ", hasFilter=" + hasFilter +
                ", filter='" + filter + '\'' +
                ", alarm=" + alarm +
                ", alarmVariable=" + alarmVariable +
                '}';
    }
}
