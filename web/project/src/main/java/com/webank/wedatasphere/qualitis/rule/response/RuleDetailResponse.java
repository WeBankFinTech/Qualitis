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
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author howeye
 */
public class RuleDetailResponse {
    @JsonProperty("rule_id")
    private Long ruleId;
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("rule_detail")
    private String ruleDetail;
    @JsonProperty("cn_name")
    private String ruleCnName;
    @JsonProperty("rule_template_id")
    private Long ruleTemplateId;
    @JsonProperty("rule_template_name")
    private String ruleTemplateName;
    @JsonProperty("template_variable")
    private List<RuleVariableResponse> templateVariable;
    @JsonProperty("alarm")
    private Boolean alarm;
    @JsonProperty("alarm_variable")
    private List<AlarmConfigResponse> alarmVariable;
    private List<DataSourceResponse> datasource;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("modify_user")
    private String modifyUser;
    @JsonProperty("modify_time")
    private String modifyTime;
    @JsonProperty("context_service")
    private boolean contextService;
    @JsonProperty("cs_id")
    private String csId;

    @JsonProperty("abort_on_failure")
    private Boolean abortOnFailure;

    @JsonProperty("delete_fail_check_result")
    private Boolean deleteFailCheckResult;

    @JsonProperty("specify_static_startup_param")
    private Boolean specifyStaticStartupParam;
    @JsonProperty("static_startup_param")
    private String staticStartupParam;

    public RuleDetailResponse() {
    }

    public RuleDetailResponse(Rule rule) {
        this.ruleId = rule.getId();
        this.ruleName = rule.getName();
        this.ruleDetail = rule.getDetail();
        this.ruleCnName = rule.getCnName();
        this.ruleTemplateId = rule.getTemplate().getId();
        this.ruleTemplateName = rule.getRuleTemplateName();
        this.ruleGroupId = rule.getRuleGroup().getId();
        this.abortOnFailure = rule.getAbortOnFailure();
        this.createUser = rule.getCreateUser();
        this.createTime = rule.getCreateTime();
        this.modifyUser = rule.getModifyUser();
        this.modifyTime = rule.getModifyTime();
        // 根据csID是否为空，确定contextService是否为true，是否开启上游表的显示
        if (StringUtils.isNotBlank(rule.getCsId())) {
            contextService = true;
            this.csId = rule.getCsId();
        } else {
            contextService = false;
        }

        this.alarm = rule.getAlarm();
        // Set alarmVariable
        this.alarmVariable = new ArrayList<>();
        if (rule.getRuleType().equals(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode())) {
            for (AlarmConfig alarmConfig : rule.getAlarmConfigs()) {
                this.alarmVariable.add(new AlarmConfigResponse(alarmConfig, RuleTypeEnum.FILE_TEMPLATE_RULE.getCode()));
            }
        } else {
            for (AlarmConfig alarmConfig : rule.getAlarmConfigs()) {
                this.alarmVariable.add(new AlarmConfigResponse(alarmConfig));
            }
        }
        // Set datasource
        this.datasource = new ArrayList<>();
        addDataSource(rule);
        // Set rule variable
        this.templateVariable = new ArrayList<>();
        for (RuleVariable ruleVariable : rule.getRuleVariables()) {
            this.templateVariable.add(new RuleVariableResponse(ruleVariable));
        }
        this.specifyStaticStartupParam = rule.getSpecifyStaticStartupParam();
        this.deleteFailCheckResult = rule.getDeleteFailCheckResult();
        this.staticStartupParam = rule.getStaticStartupParam();
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

    public Long getRuleTemplateId() {
        return ruleTemplateId;
    }

    public void setRuleTemplateId(Long ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
    }

    public Boolean getAlarm() {
        return alarm;
    }

    public void setAlarm(Boolean alarm) {
        this.alarm = alarm;
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

    public Boolean getAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(Boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
    }

    public String getRuleTemplateName() {
        return ruleTemplateName;
    }

    public void setRuleTemplateName(String ruleTemplateName) {
        this.ruleTemplateName = ruleTemplateName;
    }

    public void setTemplateVariable(List<RuleVariableResponse> templateVariable) {
        this.templateVariable = templateVariable;
    }

    public void setAlarmVariable(List<AlarmConfigResponse> alarmVariable) {
        this.alarmVariable = alarmVariable;
    }

    public List<DataSourceResponse> getDatasource() {
        return datasource;
    }

    public void setDatasource(List<DataSourceResponse> datasource) {
        this.datasource = datasource;
    }

    public List<RuleVariableResponse> getTemplateVariable() {
        return templateVariable;
    }

    public List<AlarmConfigResponse> getAlarmVariable() {
        return alarmVariable;
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

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Boolean getDeleteFailCheckResult() {
        return deleteFailCheckResult;
    }

    public void setDeleteFailCheckResult(Boolean deleteFailCheckResult) {
        this.deleteFailCheckResult = deleteFailCheckResult;
    }

    private void addDataSource(Rule rule) {
        if (rule.getRuleType().equals(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode())) {
            datasource.add(new DataSourceResponse(rule.getRuleDataSources().iterator().next()));
        } else {
            if (CollectionUtils.isNotEmpty(rule.getRuleDataSources())) {
                for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
                    datasource.add(new DataSourceResponse(ruleDataSource));
                }
            }
        }
    }
}
