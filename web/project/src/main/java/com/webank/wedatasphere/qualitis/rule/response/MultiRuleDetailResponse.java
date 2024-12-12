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
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.ExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmConfig;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleVariable;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateMidTableInputMeta;
import com.webank.wedatasphere.qualitis.rule.entity.TemplateStatisticsInputMeta;
import com.webank.wedatasphere.qualitis.rule.request.AbstractCommonRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateArgumentRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceConfigRequest;
import com.webank.wedatasphere.qualitis.rule.util.AlarmConfigTypeUtil;
import com.webank.wedatasphere.qualitis.rule.util.TemplateMidTableUtil;
import com.webank.wedatasphere.qualitis.rule.util.TemplateStatisticsUtil;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
public class MultiRuleDetailResponse extends AbstractCommonRequest {
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("multi_source_rule_template_id")
    private Long multiSourceRuleTemplateId;
    @JsonProperty("rule_template_name")
    private String ruleTemplateName;
    private MultiDataSourceConfigRequest source;
    private MultiDataSourceConfigRequest target;

    @JsonProperty("contrast_type")
    private Integer contrastType;
    @JsonProperty("filter_col_names")
    private List<DataSourceColumnRequest> colNames;

    private String filter;

    @JsonProperty("template_arguments")
    private List<TemplateArgumentRequest> templateArguments;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("modify_user")
    private String modifyUser;
    @JsonProperty("modify_time")
    private String modifyTime;

    @JsonProperty("cluster")
    private String cluster;
    @JsonProperty("abnormal_data_storage")
    private Boolean abnormalDataStorage;
    @JsonProperty("alarm_variable")
    private List<AlarmConfigResponse> alarmVariable;

    @JsonProperty("left_linkis_udf_names")
    private List<String> leftLinkisUdfNames;
    @JsonProperty("right_linkis_udf_names")
    private List<String> rightLinkisUdfNames;

    public MultiRuleDetailResponse() {
    }

    public MultiRuleDetailResponse(Rule rule) {
        super.setRuleId(rule.getId());
        super.setRuleName(rule.getName());
        super.setRuleDetail(rule.getDetail());
        super.setRuleCnName(rule.getCnName());
        this.clusterName = rule.getRuleDataSources().stream().filter(ruleDataSource -> StringUtils.isNotEmpty(ruleDataSource.getClusterName())).map(ruleDataSource -> ruleDataSource.getClusterName()).collect(Collectors.joining(SpecCharEnum.COMMA.getValue()));
        this.multiSourceRuleTemplateId = rule.getTemplate().getId();
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
        this.contrastType = rule.getContrastType();
        this.source = new MultiDataSourceConfigRequest(rule.getRuleDataSources(), 0);
        this.target = new MultiDataSourceConfigRequest(rule.getRuleDataSources(), 1);

        Boolean isCustomConsistence = QualitisConstants.isCustomColumnConsistence(rule.getTemplate().getEnName());
        if (isCustomConsistence) {
            this.source.setContextService(false);
            this.target.setContextService(false);
        }

        this.templateArguments = new ArrayList<>();
        for (TemplateMidTableInputMeta templateMidTableInputMeta : rule.getTemplate().getTemplateMidTableInputMetas()) {
            if (TemplateMidTableUtil.shouldResponse(templateMidTableInputMeta)) {
                for (RuleVariable ruleVariable : rule.getRuleVariables()) {
                    TemplateArgumentRequest templateArgumentRequest = new TemplateArgumentRequest();

                    if (ruleVariable.getTemplateMidTableInputMeta().equals(templateMidTableInputMeta)) {
                        String value = StringEscapeUtils.unescapeJava(ruleVariable.getValue());

                        if (TemplateInputTypeEnum.CONNECT_FIELDS.getCode().equals(ruleVariable.getTemplateMidTableInputMeta().getInputType()) || TemplateInputTypeEnum.COMPARISON_FIELD_SETTINGS.getCode().equals(ruleVariable.getTemplateMidTableInputMeta().getInputType())) {
                            value = ruleVariable.getOriginValue();
                        }
                        templateArgumentRequest.setArgumentPlaceholder("${" + templateMidTableInputMeta.getPlaceholder() + "}");
                        templateArgumentRequest.setArgumentStep(InputActionStepEnum.TEMPLATE_INPUT_META.getCode());
                        templateArgumentRequest.setArgumentId(templateMidTableInputMeta.getId());
                        templateArgumentRequest.setArgumentType(templateMidTableInputMeta.getInputType());

                        String argumentValue = value;
                        if (isCustomConsistence) {
                            Integer inputType = templateMidTableInputMeta.getInputType();
                            if (TemplateInputTypeEnum.CONNECT_FIELDS.getCode().equals(inputType)) {
                                argumentValue = value.replace("tmp1.", "");
                            } else if (TemplateInputTypeEnum.COMPARISON_FIELD_SETTINGS.getCode().equals(inputType)) {
                                argumentValue = value.replace("tmp2.", "");
                            }
                        }
                        templateArgumentRequest.setArgumentValue(argumentValue);

                        this.templateArguments.add(templateArgumentRequest);
                    }
                }
            }
        }

        for (TemplateStatisticsInputMeta templateStatisticsInputMeta : rule.getTemplate().getStatisticAction()) {
            if (TemplateStatisticsUtil.shouldResponse(templateStatisticsInputMeta)) {
                TemplateArgumentRequest templateArgumentRequest = new TemplateArgumentRequest();
                templateArgumentRequest.setArgumentStep(InputActionStepEnum.STATISTICS_ARG.getCode());
                templateArgumentRequest.setArgumentId(templateStatisticsInputMeta.getId());
                this.templateArguments.add(templateArgumentRequest);
            }
        }
        // Set filterColNames
        addFilterColNames(rule);

        super.setAlarm(rule.getAlarm());
        // 设置alarmVariable
        addAlarmVariable(rule);

        super.setBashContent(rule.getBashContent());
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
            } else {
                setBaseInfo(rule.getUnionWay(), rule.getEnable(), rule.getSpecifyStaticStartupParam(), rule.getStaticStartupParam(), rule.getAbortOnFailure(), rule.getAlert(), rule.getAlertLevel(), rule.getAlertReceiver(), rule.getAbnormalDatabase(), rule.getAbnormalCluster(), rule.getAbnormalProxyUser(), rule.getDeleteFailCheckResult(), this.alarmVariable);
            }
        } else {
            setBaseInfo(rule.getUnionWay(), rule.getEnable(), rule.getSpecifyStaticStartupParam(), rule.getStaticStartupParam(), rule.getAbortOnFailure(), rule.getAlert(), rule.getAlertLevel(), rule.getAlertReceiver(), rule.getAbnormalDatabase(), rule.getAbnormalCluster(), rule.getAbnormalProxyUser(), rule.getDeleteFailCheckResult(), this.alarmVariable);
        }

    }

    private void setBaseInfo(Integer unionWay, Boolean enable, Boolean specifyStaticStartupParam, String staticStartupParam, Boolean abortOnFailure, Boolean alert, Integer alertLevel, String alertReceiver, String abnormalDatabase, String cluster, String abnormalProxyUser, Boolean deleteFailCheckResult, List<AlarmConfigResponse> alarmVariable) {
        super.setSpecifyStaticStartupParam(specifyStaticStartupParam);
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
        super.setRuleEnable(enable);
        super.setUnionWay(unionWay);
        if (StringUtils.isNotBlank(cluster) || StringUtils.isNotBlank(abnormalProxyUser) || StringUtils.isNotBlank(abnormalDatabase)) {
            this.abnormalDataStorage = true;
        } else {
            this.abnormalDataStorage = false;
        }

        if (CollectionUtils.isNotEmpty(alarmVariable)) {
            AlarmConfigResponse alarmConfigResponse = AlarmConfigTypeUtil.checkAlarmConfigResponse(alarmVariable.get(0));
            if (alarmConfigResponse != null) {
                super.setUploadAbnormalValue(alarmConfigResponse.getUploadAbnormalValue() != null ? alarmConfigResponse.getUploadAbnormalValue() : false);
                super.setUploadRuleMetricValue(alarmConfigResponse.getUploadRuleMetricValue() != null ? alarmConfigResponse.getUploadRuleMetricValue() : false);
            } else {
                super.setUploadAbnormalValue(false);
                super.setUploadRuleMetricValue(false);
            }
        }
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

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getRuleTemplateName() {
        return ruleTemplateName;
    }

    public void setRuleTemplateName(String ruleTemplateName) {
        this.ruleTemplateName = ruleTemplateName;
    }

    public Boolean getAbnormalDataStorage() {
        return abnormalDataStorage;
    }

    public void setAbnormalDataStorage(Boolean abnormalDataStorage) {
        this.abnormalDataStorage = abnormalDataStorage;
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

    public List<TemplateArgumentRequest> getTemplateArguments() {
        return templateArguments;
    }

    public void setTemplateArguments(List<TemplateArgumentRequest> templateArguments) {
        this.templateArguments = templateArguments;
    }

    public List<AlarmConfigResponse> getAlarmVariable() {
        return alarmVariable;
    }

    public void setAlarmVariable(List<AlarmConfigResponse> alarmVariable) {
        this.alarmVariable = alarmVariable;
    }

    public Integer getContrastType() {
        return contrastType;
    }

    public void setContrastType(Integer contrastType) {
        this.contrastType = contrastType;
    }

    public List<DataSourceColumnRequest> getColNames() {
        return colNames;
    }

    public void setColNames(List<DataSourceColumnRequest> colNames) {
        this.colNames = colNames;
    }

    public List<String> getLeftLinkisUdfNames() {
        return leftLinkisUdfNames;
    }

    public void setLeftLinkisUdfNames(List<String> leftLinkisUdfNames) {
        this.leftLinkisUdfNames = leftLinkisUdfNames;
    }

    public List<String> getRightLinkisUdfNames() {
        return rightLinkisUdfNames;
    }

    public void setRightLinkisUdfNames(List<String> rightLinkisUdfNames) {
        this.rightLinkisUdfNames = rightLinkisUdfNames;
    }

    private void addAlarmVariable(Rule rule) {
        this.alarmVariable = new ArrayList<>();
        for (AlarmConfig alarmConfig : rule.getAlarmConfigs()) {
            this.alarmVariable.add(new AlarmConfigResponse(alarmConfig));
        }
    }

    private void addFilterColNames(Rule rule) {
        if (CollectionUtils.isEmpty(rule.getRuleDataSources())) {
            return;
        }
        for (RuleDataSource ruleDataSource : rule.getRuleDataSources()) {
            if (ruleDataSource.getBlackColName() != null && ruleDataSource.getBlackColName()) {
                DataSourceRequest dataSourceRequest = new DataSourceRequest(ruleDataSource);
                //filter_col_names
                this.colNames = dataSourceRequest.getColNames();
            }
        }

    }
}
