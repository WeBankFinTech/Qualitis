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
import com.webank.wedatasphere.qualitis.rule.request.AbstractAddRequest;
import com.webank.wedatasphere.qualitis.rule.request.AlarmConfigRequest;

import java.util.Collection;
import java.util.List;
import jodd.util.CollectionUtil;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author howeye
 */
public class AddMultiSourceRuleRequest extends AbstractAddRequest {
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("rule_detail")
    private String ruleDetail;
    @JsonProperty("cn_name")
    private String ruleCnName;
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
    @JsonProperty("alert")
    private Boolean alert;
    @JsonProperty("alert_level")
    private Integer alertLevel;
    @JsonProperty("alert_receiver")
    private String alertReceiver;
    @JsonProperty("alarm_variable")
    private List<AlarmConfigRequest> alarmVariable;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;
    @JsonProperty("cs_id")
    private String csId;
    @JsonProperty("abort_on_failure")
    private Boolean abortOnFailure;
    private String loginUser;
    @JsonProperty("delete_fail_check_result")
    private Boolean deleteFailCheckResult;

    @JsonProperty("specify_static_startup_param")
    private Boolean specifyStaticStartupParam;
    @JsonProperty("static_startup_param")
    private String staticStartupParam;

    private List<String> ruleMetricNamesForBdpClient;

    public AddMultiSourceRuleRequest() {
    }

    public String getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(String loginUser) {
        this.loginUser = loginUser;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleCnName() {
        return ruleCnName;
    }

    public void setRuleCnName(String ruleCnName) {
        this.ruleCnName = ruleCnName;
    }

    public String getRuleDetail() {
        return ruleDetail;
    }

    public void setRuleDetail(String ruleDetail) {
        this.ruleDetail = ruleDetail;
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

    public Boolean getAlert() {
        return alert;
    }

    public void setAlert(Boolean alert) {
        this.alert = alert;
    }

    public Integer getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(Integer alertLevel) {
        this.alertLevel = alertLevel;
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

    public String getCsId() { return csId; }

    public void setCsId(String csId) { this.csId = csId; }

    public Boolean getAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(Boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
    }

    public String getAlertReceiver() {
        return alertReceiver;
    }

    public void setAlertReceiver(String alertReceiver) {
        this.alertReceiver = alertReceiver;
    }

    public Boolean getDeleteFailCheckResult() {
        return deleteFailCheckResult;
    }

    public void setDeleteFailCheckResult(Boolean deleteFailCheckResult) {
        this.deleteFailCheckResult = deleteFailCheckResult;
    }

    public Boolean getSpecifyStaticStartupParam() {
        return specifyStaticStartupParam;
    }

    public void setSpecifyStaticStartupParam(Boolean specifyStaticStartupParam) {
        this.specifyStaticStartupParam = specifyStaticStartupParam;
    }

    public String getStaticStartupParam() {
        return staticStartupParam;
    }

    public void setStaticStartupParam(String staticStartupParam) {
        this.staticStartupParam = staticStartupParam;
    }

    public List<String> getRuleMetricNamesForBdpClient() {
        return ruleMetricNamesForBdpClient;
    }

    public void setRuleMetricNamesForBdpClient(List<String> ruleMetricNamesForBdpClient) {
        this.ruleMetricNamesForBdpClient = ruleMetricNamesForBdpClient;
    }

    public static void checkRequest(AddMultiSourceRuleRequest request, Boolean modifyOrNot, Boolean cs) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        CommonChecker.checkString(request.getRuleName(), "Rule name");
        CommonChecker.checkString(request.getClusterName(), "Cluster name");
        CommonChecker.checkObject(request.getMultiSourceRuleTemplateId(), "Multi source template id");
        MultiDataSourceConfigRequest.checkRequest(request.getSource(), cs);
        MultiDataSourceConfigRequest.checkRequest(request.getTarget(), cs);
        if (CollectionUtils.isNotEmpty(request.getMappings())) {
            for (MultiDataSourceJoinConfigRequest mapping : request.getMappings()) {
                MultiDataSourceJoinConfigRequest.checkRequest(mapping);
            }
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
