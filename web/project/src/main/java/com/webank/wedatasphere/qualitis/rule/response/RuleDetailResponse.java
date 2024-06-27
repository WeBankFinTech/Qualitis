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
import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.ExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.dao.TaskNewVauleDao;
import com.webank.wedatasphere.qualitis.rule.entity.*;
import com.webank.wedatasphere.qualitis.rule.request.AbstractCommonRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateArgumentRequest;
import com.webank.wedatasphere.qualitis.rule.util.AlarmConfigTypeUtil;
import com.webank.wedatasphere.qualitis.rule.util.TemplateMidTableUtil;
import com.webank.wedatasphere.qualitis.rule.util.TemplateStatisticsUtil;
import com.webank.wedatasphere.qualitis.scheduled.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
public class RuleDetailResponse extends AbstractCommonRequest {

    @JsonProperty("rule_template_name")
    private String ruleTemplateName;
    @JsonProperty("template_variable")
    private List<RuleVariableResponse> templateVariable;
    @JsonProperty("alarm_variable")
    private List<AlarmConfigResponse> alarmVariable;
    @JsonProperty("file_alarm_variable")
    private List<AlarmConfigResponse> fileAlarmVariable;
    @JsonProperty("template_arguments")
    private List<TemplateArgumentRequest> templateArguments;
    private List<DataSourceResponse> datasource;

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

    @JsonProperty("cluster")
    private String cluster;

    @JsonProperty("standard_value")
    private Boolean standardValue;
    @JsonProperty("new_value_exists")
    private Integer newValueExists;
    @JsonProperty("standard_value_version_id")
    private Long standardValueVersionId;
    @JsonProperty("standard_value_version_en_name")
    private String standardValueVersionEnName;
    @JsonProperty("filter")
    private String filter;
    @JsonProperty("abnormal_data_storage")
    private Boolean abnormalDataStorage;

    public RuleDetailResponse() {
        // Default Constructor
    }

    public RuleDetailResponse(Rule rule, List<RuleVariable> ruleVariableList) {
        super.setRuleId(rule.getId());
        super.setRuleName(rule.getName());
        super.setRuleType(rule.getRuleType());
        super.setRuleDetail(rule.getDetail());
        super.setRuleCnName(rule.getCnName());
        super.setRuleTemplateId(rule.getTemplate().getId());
        this.ruleTemplateName = rule.getTemplate().getName();
        super.setRuleGroupId(rule.getRuleGroup().getId());
        this.createUser = rule.getCreateUser();
        this.createTime = rule.getCreateTime();
        this.modifyUser = rule.getModifyUser();
        this.modifyTime = rule.getModifyTime();
        super.setWorkFlowName(rule.getWorkFlowName());
        super.setWorkFlowVersion(rule.getWorkFlowVersion());
        super.setWorkFlowProject(rule.getProject().getName());
        super.setWorkFlowSpace(rule.getWorkFlowSpace());
        super.setNodeName(rule.getNodeName());

        // 根据csID是否为空，确定contextService是否为true，是否开启上游表的显示
        if (StringUtils.isNotBlank(rule.getCsId())) {
            contextService = true;
            super.setCsId(rule.getCsId());
        } else {
            contextService = false;
        }

        this.templateArguments = new ArrayList<>();
        Template template = rule.getTemplate();
        for (TemplateMidTableInputMeta templateMidTableInputMeta : template.getTemplateMidTableInputMetas()) {
            if (TemplateMidTableUtil.shouldResponse(templateMidTableInputMeta)) {
                for (RuleVariable ruleVariable : ruleVariableList) {
                    TemplateArgumentRequest templateArgumentRequest = new TemplateArgumentRequest();

                    if (ruleVariable.getTemplateMidTableInputMeta().equals(templateMidTableInputMeta)) {
                        String value = StringEscapeUtils.unescapeJava(ruleVariable.getValue());
                        if (templateMidTableInputMeta.getInputType().equals(TemplateInputTypeEnum.REGEXP.getCode()) && templateMidTableInputMeta.getRegexpType() != null) {
                            value = ruleVariable.getOriginValue();
                        }
                        templateArgumentRequest.setArgumentPlaceholder("${" + templateMidTableInputMeta.getPlaceholder() + "}");
                        templateArgumentRequest.setArgumentStep(InputActionStepEnum.TEMPLATE_INPUT_META.getCode());
                        templateArgumentRequest.setArgumentId(templateMidTableInputMeta.getId());
                        templateArgumentRequest.setArgumentValue(value);
                        templateArgumentRequest.setArgumentType(templateMidTableInputMeta.getInputType());

                        this.templateArguments.add(templateArgumentRequest);
                    }
                }
            }
        }
        for (TemplateStatisticsInputMeta templateStatisticsInputMeta : template.getStatisticAction()) {
            if (TemplateStatisticsUtil.shouldResponse(templateStatisticsInputMeta)) {
                TemplateArgumentRequest templateArgumentRequest = new TemplateArgumentRequest();
                templateArgumentRequest.setArgumentStep(InputActionStepEnum.STATISTICS_ARG.getCode());
                templateArgumentRequest.setArgumentId(templateStatisticsInputMeta.getId());
                this.templateArguments.add(templateArgumentRequest);
            }
        }

        this.standardValueVersionId = rule.getStandardValueVersionId();
        this.standardValueVersionEnName = rule.getStandardValueVersionEnName();

        super.setAlarm(rule.getAlarm());
        // Set alarmVariable
        addAlarmVariable(rule);

        addDataSource(rule);
        // Set rule variable
        this.templateVariable = new ArrayList<>();
        for (RuleVariable ruleVariable : ruleVariableList) {
            this.templateVariable.add(new RuleVariableResponse(ruleVariable, this.standardValueVersionId));
        }
        super.setExecutionParametersName(rule.getExecutionParametersName());

        if (StringUtils.isNotBlank(rule.getExecutionParametersName())) {
            ExecutionParameters executionParameters = SpringContextHolder.getBean(ExecutionParametersDao.class).findByNameAndProjectId(rule.getExecutionParametersName(), rule.getProject().getId());
            if (executionParameters != null) {
                super.setSpecifyStaticStartupParam(executionParameters.getSpecifyStaticStartupParam());
                if (super.getSpecifyStaticStartupParam() != null && super.getSpecifyStaticStartupParam()) {
                    super.setStaticStartupParam(executionParameters.getStaticStartupParam());
                }
                super.setAbortOnFailure(executionParameters.getAbortOnFailure());
                super.setAlert(executionParameters.getAlert());
                if (super.getAlert()) {
                    super.setAlertLevel(executionParameters.getAlertLevel());
                    super.setAlertReceiver(executionParameters.getAlertReceiver());
                }
                super.setAbnormalDatabase(executionParameters.getAbnormalDatabase());
                this.cluster = executionParameters.getCluster();

                super.setAbnormalProxyUser(executionParameters.getAbnormalProxyUser());
                super.setDeleteFailCheckResult(executionParameters.getDeleteFailCheckResult());
                super.setUploadAbnormalValue(executionParameters.getUploadAbnormalValue());
                super.setUploadRuleMetricValue(executionParameters.getUploadRuleMetricValue());
                super.setRuleEnable(rule.getEnable());
                super.setUnionWay(executionParameters.getUnionWay());
                if (StringUtils.isNotBlank(executionParameters.getCluster()) || StringUtils.isNotBlank(executionParameters.getAbnormalProxyUser()) || StringUtils.isNotBlank(executionParameters.getAbnormalDatabase())) {
                    this.abnormalDataStorage = true;
                } else {
                    this.abnormalDataStorage = false;
                }
                this.filter = executionParameters.getFilter();
            } else {
                setBaseInfo(rule.getUnionWay(), rule.getEnable(), rule.getSpecifyStaticStartupParam(), rule.getStaticStartupParam(), rule.getAbortOnFailure(), rule.getAlert(), rule.getAlertLevel(), rule.getAlertReceiver(), rule.getAbnormalDatabase(), rule.getAbnormalCluster(), rule.getAbnormalProxyUser(), rule.getDeleteFailCheckResult(), this.alarmVariable, this.fileAlarmVariable);
            }
        } else {
            setBaseInfo(rule.getUnionWay(), rule.getEnable(), rule.getSpecifyStaticStartupParam(), rule.getStaticStartupParam(), rule.getAbortOnFailure(), rule.getAlert(), rule.getAlertLevel(), rule.getAlertReceiver(), rule.getAbnormalDatabase(), rule.getAbnormalCluster(), rule.getAbnormalProxyUser(), rule.getDeleteFailCheckResult(), this.alarmVariable, this.fileAlarmVariable);
        }

        if (StringUtils.isNotBlank(rule.getStandardValueVersionEnName())) {
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

    private void addAlarmVariable(Rule rule) {
        this.alarmVariable = new ArrayList<>();
        this.fileAlarmVariable = new ArrayList<>();
        for (AlarmConfig alarmConfig : rule.getAlarmConfigs()) {
            this.alarmVariable.add(new AlarmConfigResponse(alarmConfig,rule.getRuleType()));
        }
    }

    private void setBaseInfo(Integer unionWay, Boolean enable, Boolean specifyStaticStartupParam, String staticStartupParam, Boolean abortOnFailure, Boolean alert, Integer alertLevel, String alertReceiver, String abnormalDatabase, String cluster, String abnormalProxyUser, Boolean deleteFailCheckResult, List<AlarmConfigResponse> alarmVariable, List<AlarmConfigResponse> fileAlarmVariable) {
        super.setSpecifyStaticStartupParam(specifyStaticStartupParam);
        super.setRuleEnable(enable);
        super.setUnionWay(unionWay);
        if (specifyStaticStartupParam != null && specifyStaticStartupParam) {
            super.setStaticStartupParam(staticStartupParam);
        }
        super.setAbortOnFailure(abortOnFailure);
        super.setAlert(alert);
        if (alert != null && alert) {
            super.setAlertLevel(alertLevel);
            super.setAlertReceiver(alertReceiver);
        }
        super.setAbnormalDatabase(abnormalDatabase);
        this.cluster = cluster;
        super.setAbnormalProxyUser(abnormalProxyUser);
        super.setDeleteFailCheckResult(deleteFailCheckResult);
        if (StringUtils.isNotBlank(cluster) || StringUtils.isNotBlank(abnormalProxyUser) || StringUtils.isNotBlank(abnormalDatabase)) {
            this.abnormalDataStorage = true;
        } else {
            this.abnormalDataStorage = false;
        }

        if (CollectionUtils.isNotEmpty(alarmVariable)) {
            AlarmConfigResponse alarmConfigResponse = AlarmConfigTypeUtil.checkAlarmConfigResponse(alarmVariable.get(0));
            super.setUploadAbnormalValue(alarmConfigResponse.getUploadAbnormalValue() != null ? alarmConfigResponse.getUploadAbnormalValue() : false);
            super.setUploadRuleMetricValue(alarmConfigResponse.getUploadRuleMetricValue() != null ? alarmConfigResponse.getUploadRuleMetricValue() : false);
        }
        if (CollectionUtils.isNotEmpty(fileAlarmVariable)) {
            super.setUploadAbnormalValue(fileAlarmVariable.get(0).getUploadAbnormalValue() != null ? fileAlarmVariable.get(0).getUploadAbnormalValue() : false);
            super.setUploadRuleMetricValue(fileAlarmVariable.get(0).getUploadRuleMetricValue() != null ? fileAlarmVariable.get(0).getUploadRuleMetricValue() : false);
        }

    }

    public List<DataSourceResponse> getDAtasource() {
        return datasource;
    }

    public Boolean getAbnormalDataStorage() {
        return abnormalDataStorage;
    }

    public void setAbnormalDataStorage(Boolean abnormalDataStorage) {
        this.abnormalDataStorage = abnormalDataStorage;
    }

    public boolean isContextService() {
        return contextService;
    }

    public void setContextService(boolean contextService) {
        this.contextService = contextService;
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

    public List<RuleVariableResponse> getTemplateVariable() {
        return templateVariable;
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

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
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

    public List<TemplateArgumentRequest> getTemplateArguments() {
        return templateArguments;
    }

    public void setTemplateArguments(List<TemplateArgumentRequest> templateArguments) {
        this.templateArguments = templateArguments;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<AlarmConfigResponse> getFileAlarmVariable() {
        return fileAlarmVariable;
    }

    public void setFileAlarmVariable(List<AlarmConfigResponse> fileAlarmVariable) {
        this.fileAlarmVariable = fileAlarmVariable;
    }

    public List<AlarmConfigResponse> getAlarmVariable() {
        return alarmVariable;
    }

    public void setAlarmVariable(List<AlarmConfigResponse> alarmVariable) {
        this.alarmVariable = alarmVariable;
    }

    private void addDataSource(Rule rule) {
        List<DataSourceResponse> list = Lists.newArrayList();
        if (rule.getRuleType().equals(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode())) {
            list.add(new DataSourceResponse(rule.getRuleDataSources().iterator().next()));
        } else {
            if (CollectionUtils.isNotEmpty(rule.getRuleDataSources())) {
                for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
                    list.add(new DataSourceResponse(ruleDataSource));
                }
            }
        }
        this.datasource = list;
    }
}
