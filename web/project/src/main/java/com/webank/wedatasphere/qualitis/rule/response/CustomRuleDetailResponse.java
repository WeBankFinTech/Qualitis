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
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;

import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author howeye
 */
public class CustomRuleDetailResponse {

    @JsonProperty("rule_id")
    private Long ruleId;
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("rule_detail")
    private String ruleDetail;
    @JsonProperty("cn_name")
    private String ruleCnName;
    @JsonProperty("output_name")
    private String outputName;
    @JsonProperty("save_mid_table")
    private Boolean saveMidTable;
    @JsonProperty("function_type")
    private Integer functionType;
    @JsonProperty("function_content")
    private String functionContent;
    @JsonProperty("from_content")
    private String fromContent;
    @JsonProperty("where_content")
    private String whereContent;
    private Boolean alarm;
    @JsonProperty("alarm_variable")
    private List<AlarmConfigResponse> alarmVariable;
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;
    @JsonProperty("context_service")
    private boolean contextService;
    @JsonProperty("cs_id")
    private String csId;
    @JsonProperty("abort_on_failure")
    private Boolean abortOnFailure;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("modify_user")
    private String modifyUser;
    @JsonProperty("modify_time")
    private String modifyTime;
    @JsonProperty("proxy_user")
    private String proxyUser;
    @JsonProperty("rule_metric_id")
    private Long ruleMetricId;
    @JsonProperty("rule_metric_name")
    private String ruleMetricName;
    @JsonProperty("delete_fail_check_result")
    private Boolean deleteFailCheckResult;
    @JsonProperty("sql_check_area")
    private String sqlCheckArea;
    @JsonProperty("specify_static_startup_param")
    private Boolean specifyStaticStartupParam;
    @JsonProperty("static_startup_param")
    private String staticStartupParam;

    @JsonProperty("linkis_datasoure_id")
    private Long linkisDataSourceId;
    @JsonProperty("linkis_datasoure_version_id")
    private Long linkisDataSourceVersionId;
    @JsonProperty("linkis_datasource_name")
    private String linkisDataSourceName;

    public CustomRuleDetailResponse(Rule customRule) {
        this.ruleId = customRule.getId();
        this.ruleName = customRule.getName();
        this.ruleCnName = customRule.getCnName();
        this.ruleDetail = customRule.getDetail();
        this.outputName = customRule.getOutputName();
        this.saveMidTable = customRule.getTemplate().getSaveMidTable();
        this.functionType = customRule.getFunctionType();
        this.functionContent = customRule.getFunctionContent();
        this.fromContent = customRule.getFromContent();
        this.whereContent = customRule.getWhereContent();
        this.alarm = customRule.getAlarm();
        this.alarmVariable = new ArrayList<>();
        this.ruleGroupId = customRule.getRuleGroup().getId();
        this.abortOnFailure = customRule.getAbortOnFailure();
        this.createUser = customRule.getCreateUser();
        this.createTime = customRule.getCreateTime();
        this.modifyUser = customRule.getModifyUser();
        this.modifyTime = customRule.getModifyTime();
        // 根据contextService是否为true，决定页面是否开启上游表的显示
        if (StringUtils.isNotBlank(customRule.getCsId())) {
            contextService = true;
        } else {
            contextService = false;
        }
        for (AlarmConfig alarmConfig : customRule.getAlarmConfigs()) {
            this.alarmVariable.add(new AlarmConfigResponse(alarmConfig, RuleTypeEnum.CUSTOM_RULE.getCode()));
        }
        if (CollectionUtils.isNotEmpty(customRule.getRuleDataSources())) {
            RuleDataSource originalRuleDataSource = customRule.getRuleDataSources().stream()
                .filter(
                    ruleDataSource -> (ruleDataSource.getDatasourceIndex() != null && ruleDataSource.getDatasourceIndex().equals(-1))
                        || StringUtils.isNotBlank(ruleDataSource.getTableName())).iterator().next();
            this.clusterName = originalRuleDataSource.getClusterName();
            this.proxyUser = originalRuleDataSource.getProxyUser();

            this.linkisDataSourceId = originalRuleDataSource.getLinkisDataSourceId();
            this.linkisDataSourceName = originalRuleDataSource.getLinkisDataSourceName();
            this.linkisDataSourceVersionId = originalRuleDataSource.getLinkisDataSourceVersionId();
        }
        this.specifyStaticStartupParam = customRule.getSpecifyStaticStartupParam();
        this.deleteFailCheckResult = customRule.getDeleteFailCheckResult();
        this.sqlCheckArea = customRule.getTemplate().getMidTableAction();
        this.staticStartupParam = customRule.getStaticStartupParam();
    }

    public String getRuleCnName() {
        return ruleCnName;
    }

    public void setRuleCnName(String ruleCnName) {
        this.ruleCnName = ruleCnName;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
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

    public String getRuleDetail() {
        return ruleDetail;
    }

    public void setRuleDetail(String ruleDetail) {
        this.ruleDetail = ruleDetail;
    }

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public Boolean getSaveMidTable() {
        return saveMidTable;
    }

    public void setSaveMidTable(Boolean saveMidTable) {
        this.saveMidTable = saveMidTable;
    }

    public Integer getFunctionType() {
        return functionType;
    }

    public void setFunctionType(Integer functionType) {
        this.functionType = functionType;
    }

    public String getFunctionContent() {
        return functionContent;
    }

    public void setFunctionContent(String functionContent) {
        this.functionContent = functionContent;
    }

    public String getFromContent() {
        return fromContent;
    }

    public void setFromContent(String fromContent) {
        this.fromContent = fromContent;
    }

    public String getWhereContent() {
        return whereContent;
    }

    public void setWhereContent(String whereContent) {
        this.whereContent = whereContent;
    }

    public Boolean getAlarm() {
        return alarm;
    }

    public void setAlarm(Boolean alarm) {
        this.alarm = alarm;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public List<AlarmConfigResponse> getAlarmVariable() {
        return alarmVariable;
    }

    public void setAlarmVariable(List<AlarmConfigResponse> alarmVariable) {
        this.alarmVariable = alarmVariable;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
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

    public Long getRuleMetricId() {
        return ruleMetricId;
    }

    public void setRuleMetricId(Long ruleMetricId) {
        this.ruleMetricId = ruleMetricId;
    }

    public String getRuleMetricName() {
        return ruleMetricName;
    }

    public void setRuleMetricName(String ruleMetricName) {
        this.ruleMetricName = ruleMetricName;
    }

    public Boolean getDeleteFailCheckResult() {
        return deleteFailCheckResult;
    }

    public void setDeleteFailCheckResult(Boolean deleteFailCheckResult) {
        this.deleteFailCheckResult = deleteFailCheckResult;
    }

    public String getSqlCheckArea() {
        return sqlCheckArea;
    }

    public void setSqlCheckArea(String sqlCheckArea) {
        this.sqlCheckArea = sqlCheckArea;
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

    public Long getLinkisDataSourceId() {
        return linkisDataSourceId;
    }

    public void setLinkisDataSourceId(Long linkisDataSourceId) {
        this.linkisDataSourceId = linkisDataSourceId;
    }

    public Long getLinkisDataSourceVersionId() {
        return linkisDataSourceVersionId;
    }

    public void setLinkisDataSourceVersionId(Long linkisDataSourceVersionId) {
        this.linkisDataSourceVersionId = linkisDataSourceVersionId;
    }

    public String getLinkisDataSourceName() {
        return linkisDataSourceName;
    }

    public void setLinkisDataSourceName(String linkisDataSourceName) {
        this.linkisDataSourceName = linkisDataSourceName;
    }

    @Override
    public String toString() {
        return "CustomRuleDetailResponse{" +
            "ruleId=" + ruleId +
            ", ruleName='" + ruleName + '\'' +
            ", outputName='" + outputName + '\'' +
            ", saveMidTable=" + saveMidTable +
            ", functionType=" + functionType +
            ", functionContent='" + functionContent + '\'' +
            ", fromContent='" + fromContent + '\'' +
            ", whereContent='" + whereContent + '\'' +
            ", alarm=" + alarm +
            ", alarmVariable=" + alarmVariable +
            ", clusterName='" + clusterName + '\'' +
            ", ruleGroupId=" + ruleGroupId +
            ", contextService=" + contextService +
            ", csId='" + csId + '\'' +
            ", abortOnFailure=" + abortOnFailure +
            '}';
    }
}
