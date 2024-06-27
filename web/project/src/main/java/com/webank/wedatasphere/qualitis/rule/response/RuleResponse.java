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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.rule.dao.ExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.dao.TaskNewVauleDao;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.scheduled.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public class RuleResponse {
    @JsonProperty("rule_id")
    private Long ruleId;
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("cn_name")
    private String ruleCnName;
    @JsonProperty("rule_detail")
    private String ruleDetail;
    @JsonProperty("cluster_name")
    private List<String> clusterName;
    @JsonProperty("table_name")
    private List<String> tableName;
    @JsonProperty("rule_template_name")
    private String ruleTemplateName;
    @JsonProperty("rule_template_id")
    private Long ruleTemplateId;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;
    @JsonProperty("rule_group_name")
    private String ruleGroupName;
    @JsonProperty("rule_type")
    private Integer ruleType;
    @JsonProperty("table_type")
    private String tableType;
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("project_name")
    private String projectName;

    @JsonProperty("abort_on_failure")
    private Boolean abortOnFailure;
    @JsonProperty("alert")
    private Boolean alert;
    @JsonProperty("alert_level")
    private Integer alertLevel;
    @JsonProperty("alert_receiver")
    private String alertReceiver;
    @JsonProperty("delete_fail_check_result")
    private Boolean deleteFailCheckResult;

    @JsonProperty("add_rule_metric_names")
    private String addRuleMetricNames;

    @JsonProperty("specify_static_startup_param")
    private Boolean specifyStaticStartupParam;
    @JsonProperty("static_startup_param")
    private String staticStartupParam;

    @JsonProperty("execution_parameters_name")
    private String executionParametersName;

    @JsonProperty("abnormal_database")
    private String abnormalDatabase;

    @JsonProperty("cluster")
    private String cluster;

    @JsonProperty("abnormal_proxy_user")
    private String abnormalProxyUser;

    @JsonProperty("standard_value")
    private Boolean standardValue;

    @JsonProperty("new_value_exists")
    private Integer newValueExists;
    @JsonProperty("standard_value_version_id")
    private Long standardValueVersionId;
    @JsonProperty("standard_value_version_en_name")
    private String standardValueVersionEnName;
    @JsonProperty("work_flow_name")
    private String workFlowName;
    @JsonProperty("rule_enable")
    private Boolean ruleEnable;
    @JsonProperty("union_way")
    private Integer unionWay;
    @JsonProperty("work_flow_version")
    private String workFlowVersion;
    @JsonIgnore
    private Rule rule;
    @JsonIgnore
    private Boolean isModifiedRule;

    public RuleResponse() {
    }

    public RuleResponse(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public RuleResponse (Rule rule, Map<String, ExecutionParameters> executionParameterNameMap, Map<Long, Long> taskNewValueMap) {
        this.rule = rule;
        this.ruleId = rule.getId();
        this.ruleName = rule.getName();
        this.ruleDetail = rule.getDetail();
        this.ruleCnName = rule.getCnName();
        this.ruleTemplateName = rule.getTemplate().getName();
        this.ruleTemplateId = rule.getTemplate().getId();
        this.ruleGroupId = rule.getRuleGroup().getId();
        this.ruleGroupName = rule.getRuleGroup().getRuleGroupName();
        this.projectId = rule.getProject().getId();
        this.projectName = rule.getProject().getName();
        this.ruleType = rule.getRuleType();
        this.workFlowName = rule.getWorkFlowName();
        this.workFlowVersion = rule.getWorkFlowVersion();
        if (ruleType.equals(RuleTypeEnum.CUSTOM_RULE.getCode()) && StringUtils.isEmpty(rule.getFromContent())) {
            // Just combine 2-2 code for sql check.
            this.tableType = RuleTypeEnum.CUSTOM_RULE.getCode().toString();
        }
        clusterName = new ArrayList<>();
        tableName = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(rule.getRuleDataSources())) {
            for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
                if (StringUtils.isBlank(ruleDataSource.getTableName())) {
                    continue;
                }
                clusterName.add(ruleDataSource.getClusterName());
                tableName.add(ruleDataSource.getDbName() + "." + ruleDataSource.getTableName());
            }
        }

        this.deleteFailCheckResult = rule.getDeleteFailCheckResult();
        this.executionParametersName = rule.getExecutionParametersName();
        this.standardValueVersionId = rule.getStandardValueVersionId();
        this.standardValueVersionEnName = rule.getStandardValueVersionEnName();

        if (StringUtils.isNotBlank(rule.getExecutionParametersName())) {
            ExecutionParameters executionParameters = executionParameterNameMap.get(rule.getExecutionParametersName());
            if (executionParameters != null) {
                this.specifyStaticStartupParam = executionParameters.getSpecifyStaticStartupParam();
                if (specifyStaticStartupParam != null && specifyStaticStartupParam) {
                    this.staticStartupParam = executionParameters.getStaticStartupParam();
                }
                this.abortOnFailure = executionParameters.getAbortOnFailure();
                this.alert = executionParameters.getAlert();
                if (alert) {
                    this.alertLevel = executionParameters.getAlertLevel();
                    this.alertReceiver = executionParameters.getAlertReceiver();
                }
                this.abnormalDatabase = executionParameters.getAbnormalDatabase();
                this.cluster = executionParameters.getCluster();
                this.abnormalProxyUser = executionParameters.getAbnormalProxyUser();
                this.ruleEnable = rule.getEnable();
                this.unionWay = executionParameters.getUnionWay();
            } else {
                setBaseInfo(rule.getUnionWay(), rule.getEnable(), rule.getSpecifyStaticStartupParam(), rule.getStaticStartupParam(), rule.getAbortOnFailure(), rule.getAlert(), rule.getAlertLevel(), rule.getAlertReceiver(), rule.getAbnormalDatabase(), rule.getAbnormalCluster(), rule.getAbnormalProxyUser());
            }
        } else {
            setBaseInfo(rule.getUnionWay(), rule.getEnable(), rule.getSpecifyStaticStartupParam(), rule.getStaticStartupParam(), rule.getAbortOnFailure(), rule.getAlert(), rule.getAlertLevel(), rule.getAlertReceiver(), rule.getAbnormalDatabase(), rule.getAbnormalCluster(), rule.getAbnormalProxyUser());
        }

        //是否为标准值或新值判断
        if (rule.getStandardValueVersionId() != null && StringUtils.isNotBlank(rule.getStandardValueVersionEnName())) {
            this.standardValue = true;
        } else {
            this.standardValue = false;
        }

        //是否有新值
        if (rule.getId() != null) {
            Long matchTaskNewValue = taskNewValueMap.getOrDefault(rule.getId(), 0L);
            if (matchTaskNewValue > 0) {
                this.newValueExists = 1;
            } else {
                this.newValueExists = 0;
            }

        }

    }

    public RuleResponse(Rule rule) {
        this.rule = rule;
        this.ruleId = rule.getId();
        this.ruleName = rule.getName();
        this.ruleDetail = rule.getDetail();
        this.ruleCnName = rule.getCnName();
        this.ruleTemplateName = rule.getTemplate().getName();
        this.ruleTemplateId = rule.getTemplate().getId();
        this.ruleGroupId = rule.getRuleGroup().getId();
        this.ruleGroupName = rule.getRuleGroup().getRuleGroupName();
        this.projectId = rule.getProject().getId();
        this.projectName = rule.getProject().getName();
        this.ruleType = rule.getRuleType();
        this.workFlowName = rule.getWorkFlowName();
        this.workFlowVersion = rule.getWorkFlowVersion();
        if (ruleType.equals(RuleTypeEnum.CUSTOM_RULE.getCode()) && StringUtils.isEmpty(rule.getFromContent())) {
            // Just combine 2-2 code for sql check.
            this.tableType = RuleTypeEnum.CUSTOM_RULE.getCode().toString();
        }
        clusterName = new ArrayList<>();
        tableName = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(rule.getRuleDataSources())) {
            for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
                if (StringUtils.isBlank(ruleDataSource.getTableName())) {
                    continue;
                }
                clusterName.add(ruleDataSource.getClusterName());
                tableName.add(ruleDataSource.getDbName() + "." + ruleDataSource.getTableName());
            }
        }

        this.deleteFailCheckResult = rule.getDeleteFailCheckResult();
        this.executionParametersName = rule.getExecutionParametersName();
        this.standardValueVersionId = rule.getStandardValueVersionId();
        this.standardValueVersionEnName = rule.getStandardValueVersionEnName();

        if (StringUtils.isNotBlank(rule.getExecutionParametersName())) {
            ExecutionParameters executionParameters = SpringContextHolder.getBean(ExecutionParametersDao.class).findByNameAndProjectId(rule.getExecutionParametersName(), rule.getProject().getId());
            if (executionParameters != null) {
                this.specifyStaticStartupParam = executionParameters.getSpecifyStaticStartupParam();
                if (specifyStaticStartupParam != null && specifyStaticStartupParam) {
                    this.staticStartupParam = executionParameters.getStaticStartupParam();
                }
                this.abortOnFailure = executionParameters.getAbortOnFailure();
                this.alert = executionParameters.getAlert();
                if (alert) {
                    this.alertLevel = executionParameters.getAlertLevel();
                    this.alertReceiver = executionParameters.getAlertReceiver();
                }
                this.abnormalDatabase = executionParameters.getAbnormalDatabase();
                this.cluster = executionParameters.getCluster();
                this.abnormalProxyUser = executionParameters.getAbnormalProxyUser();
                this.ruleEnable = rule.getEnable();
                this.unionWay = executionParameters.getUnionWay();
            } else {
                setBaseInfo(rule.getUnionWay(), rule.getEnable(), rule.getSpecifyStaticStartupParam(), rule.getStaticStartupParam(), rule.getAbortOnFailure(), rule.getAlert(), rule.getAlertLevel(), rule.getAlertReceiver(), rule.getAbnormalDatabase(), rule.getAbnormalCluster(), rule.getAbnormalProxyUser());
            }
        } else {
            setBaseInfo(rule.getUnionWay(), rule.getEnable(), rule.getSpecifyStaticStartupParam(), rule.getStaticStartupParam(), rule.getAbortOnFailure(), rule.getAlert(), rule.getAlertLevel(), rule.getAlertReceiver(), rule.getAbnormalDatabase(), rule.getAbnormalCluster(), rule.getAbnormalProxyUser());
        }

        //是否为标准值或新值判断
        if (rule.getStandardValueVersionId() != null && StringUtils.isNotBlank(rule.getStandardValueVersionEnName())) {
            this.standardValue = true;
        } else {
            this.standardValue = false;
        }

        //是否有新值
        if (rule.getId() != null) {
            Long matchTaskNewValue = SpringContextHolder.getBean(TaskNewVauleDao.class).findMatchTaskNewValue(rule.getId());
            if (matchTaskNewValue > 0) {
                this.newValueExists = 1;
            } else {
                this.newValueExists = 0;
            }

        }

    }

    private void setBaseInfo(Integer unionWay, Boolean enable, Boolean specifyStaticStartupParam, String staticStartupParam, Boolean abortOnFailure, Boolean alert, Integer alertLevel, String alertReceiver, String abnormalDatabase, String cluster, String abnormalProxyUser) {
        this.specifyStaticStartupParam = specifyStaticStartupParam;
        this.ruleEnable = enable;
        this.unionWay = unionWay;
        if (specifyStaticStartupParam != null && specifyStaticStartupParam) {
            this.staticStartupParam = staticStartupParam;
        }
        this.abortOnFailure = abortOnFailure;
        this.alert = alert;
        if (alert != null && alert) {
            this.alertLevel = alertLevel;
            this.alertReceiver = alertReceiver;
        }
        this.abnormalDatabase = abnormalDatabase;
        this.cluster = cluster;
        this.abnormalProxyUser = abnormalProxyUser;
    }

    public Rule getRule() {
        return rule;
    }

    public Boolean getModifiedRule() {
        return isModifiedRule;
    }

    public void setModifiedRule(Boolean modifiedRule) {
        isModifiedRule = modifiedRule;
    }

    public Long getStandardValueVersionId() {
        return standardValueVersionId;
    }

    public void setStandardValueVersionId(Long standardValueVersionId) {
        this.standardValueVersionId = standardValueVersionId;
    }

    public String getStandardValueVersionEnName() {
        return standardValueVersionEnName;
    }

    public void setStandardValueVersionEnName(String standardValueVersionEnName) {
        this.standardValueVersionEnName = standardValueVersionEnName;
    }

    public Boolean getRuleEnable() {
        return ruleEnable;
    }

    public void setRuleEnable(Boolean ruleEnable) {
        this.ruleEnable = ruleEnable;
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

    public String getAlertReceiver() {
        return alertReceiver;
    }

    public void setAlertReceiver(String alertReceiver) {
        this.alertReceiver = alertReceiver;
    }

    public List<String> getClusterName() {
        return clusterName;
    }

    public void setClusterName(List<String> clusterName) {
        this.clusterName = clusterName;
    }

    public List<String> getTableName() {
        return tableName;
    }

    public void setTableName(List<String> tableName) {
        this.tableName = tableName;
    }

    public String getRuleTemplateName() {
        return ruleTemplateName;
    }

    public void setRuleTemplateName(String ruleTemplateName) {
        this.ruleTemplateName = ruleTemplateName;
    }

    public Long getRuleTemplateId() {
        return ruleTemplateId;
    }

    public void setRuleTemplateId(Long ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
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

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public Boolean getAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(Boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Boolean getDeleteFailCheckResult() {
        return deleteFailCheckResult;
    }

    public void setDeleteFailCheckResult(Boolean deleteFailCheckResult) {
        this.deleteFailCheckResult = deleteFailCheckResult;
    }

    public String getAddRuleMetricNames() {
        return addRuleMetricNames;
    }

    public void setAddRuleMetricNames(String addRuleMetricNames) {
        this.addRuleMetricNames = addRuleMetricNames;
    }

    public String getExecutionParametersName() {
        return executionParametersName;
    }

    public void setExecutionParametersName(String executionParametersName) {
        this.executionParametersName = executionParametersName;
    }

    public String getAbnormalDatabase() {
        return abnormalDatabase;
    }

    public void setAbnormalDatabase(String abnormalDatabase) {
        this.abnormalDatabase = abnormalDatabase;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getAbnormalProxyUser() {
        return abnormalProxyUser;
    }

    public void setAbnormalProxyUser(String abnormalProxyUser) {
        this.abnormalProxyUser = abnormalProxyUser;
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

    public Boolean getStandardValue() {
        return standardValue;
    }

    public void setStandardValue(Boolean standardValue) {
        this.standardValue = standardValue;
    }

    public Integer getNewValueExists() {
        return newValueExists;
    }

    public void setNewValueExists(Integer newValueExists) {
        this.newValueExists = newValueExists;
    }

    public String getWorkFlowName() {
        return workFlowName;
    }

    public void setWorkFlowName(String workFlowName) {
        this.workFlowName = workFlowName;
    }

    public String getWorkFlowVersion() {
        return workFlowVersion;
    }

    public void setWorkFlowVersion(String workFlowVersion) {
        this.workFlowVersion = workFlowVersion;
    }

    public Integer getUnionWay() {
        return unionWay;
    }

    public void setUnionWay(Integer unionWay) {
        this.unionWay = unionWay;
    }

    @Override
    public String toString() {
        return "RuleResponse{" +
                "ruleId=" + ruleId +
                ", ruleName='" + ruleName + '\'' +
                ", ruleTemplateName='" + ruleTemplateName + '\'' +
                ", ruleTemplateId=" + ruleTemplateId +
                ", ruleGroupId=" + ruleGroupId +
                ", ruleGroupName='" + ruleGroupName + '\'' +
                ", projectId=" + projectId +
                ", abortOnFailure=" + abortOnFailure +
                ", alert=" + alert +
                '}';
    }
}
