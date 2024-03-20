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
import com.webank.wedatasphere.qualitis.rule.constant.InputActionStepEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateDataSourceTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateInputTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.ExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.entity.*;
import com.webank.wedatasphere.qualitis.rule.request.AbstractCommonRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceEnvMappingRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceEnvRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateArgumentRequest;
import com.webank.wedatasphere.qualitis.rule.util.AlarmConfigTypeUtil;
import com.webank.wedatasphere.qualitis.rule.util.TemplateMidTableUtil;
import com.webank.wedatasphere.qualitis.rule.util.TemplateStatisticsUtil;
import com.webank.wedatasphere.qualitis.scheduled.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
public class CustomRuleDetailResponse extends AbstractCommonRequest {

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

    @JsonProperty("cluster_name")
    private String clusterName;

    @JsonProperty("context_service")
    private boolean contextService;

    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("modify_user")
    private String modifyUser;
    @JsonProperty("modify_time")
    private String modifyTime;
    @JsonProperty("file_id")
    private String fileId;
    @JsonProperty("file_db")
    private String fileDb;
    @JsonProperty("file_table")
    private String fileTable;
    @JsonProperty("file_table_desc")
    private String fileTableDesc;
    @JsonProperty("file_delimiter")
    private String fileDelimiter;
    @JsonProperty("file_type")
    private String fileType;
    @JsonProperty("file_header")
    private Boolean fileHeader;
    @JsonProperty("fps_file")
    private boolean fpsFile;
    @JsonProperty("proxy_user")
    private String proxyUser;
    @JsonProperty("file_hash_values")
    private String fileHashValues;

    @JsonProperty("rule_metric_id")
    private Long ruleMetricId;
    @JsonProperty("rule_metric_name")
    private String ruleMetricName;

    @JsonProperty("sql_check_area")
    private String sqlCheckArea;

    @JsonProperty("linkis_datasource_id")
    private Long linkisDataSourceId;
    @JsonProperty("linkis_datasource_version_id")
    private Long linkisDataSourceVersionId;
    @JsonProperty("linkis_datasource_name")
    private String linkisDataSourceName;
    @JsonProperty("linkis_datasource_type")
    private String linkisDataSourceType;
    @JsonProperty("linkis_datasource_envs")
    private List<DataSourceEnvRequest> dataSourceEnvRequests;
    @JsonProperty("linkis_datasource_envs_mappings")
    private List<DataSourceEnvMappingRequest> dataSourceEnvMappingRequests;
    @JsonProperty("type")
    private String type;

    @JsonProperty("cluster")
    private String cluster;

    @JsonProperty("template_arguments")
    private List<TemplateArgumentRequest> templateArguments;
    @JsonProperty("abnormal_data_storage")
    private Boolean abnormalDataStorage;
    @JsonProperty("alarm_variable")
    private List<AlarmConfigResponse> alarmVariable;

    @JsonProperty("linkis_udf_names")
    private List<String> linkisUdfNames;


    public CustomRuleDetailResponse(Rule customRule) {
        super.setRuleId(customRule.getId());
        super.setRuleName(customRule.getName());
        super.setRuleDetail(customRule.getDetail());
        super.setRuleCnName(customRule.getCnName());
        this.outputName = customRule.getOutputName();
        this.saveMidTable = customRule.getTemplate().getSaveMidTable();
        this.functionType = customRule.getFunctionType();
        this.functionContent = customRule.getFunctionContent();
        this.fromContent = customRule.getFromContent();
        this.whereContent = customRule.getWhereContent();
        super.setAlarm(customRule.getAlarm());
        super.setRuleGroupId(customRule.getRuleGroup().getId());
        this.createUser = customRule.getCreateUser();
        this.createTime = customRule.getCreateTime();
        this.modifyUser = customRule.getModifyUser();
        this.modifyTime = customRule.getModifyTime();
        super.setWorkFlowName(customRule.getWorkFlowName());
        super.setWorkFlowVersion(customRule.getWorkFlowVersion());
        super.setWorkFlowProject(customRule.getProject().getName());
        super.setWorkFlowSpace(customRule.getWorkFlowSpace());
        super.setNodeName(customRule.getNodeName());
        // 根据contextService是否为true，决定页面是否开启上游表的显示
        if (StringUtils.isNotBlank(customRule.getCsId())) {
            contextService = true;
        } else {
            contextService = false;
        }
        this.templateArguments = new ArrayList<>();
        for (TemplateMidTableInputMeta templateMidTableInputMeta : customRule.getTemplate().getTemplateMidTableInputMetas()) {
            if (TemplateMidTableUtil.shouldResponse(templateMidTableInputMeta)) {
                for (RuleVariable ruleVariable : customRule.getRuleVariables()) {
                    TemplateArgumentRequest templateArgumentRequest = new TemplateArgumentRequest();

                    if (ruleVariable.getTemplateMidTableInputMeta().equals(templateMidTableInputMeta)) {
                        String value = StringEscapeUtils.unescapeJava(ruleVariable.getValue());
                        if (templateMidTableInputMeta.getInputType().equals(TemplateInputTypeEnum.REGEXP.getCode()) && templateMidTableInputMeta.getRegexpType() != null) {
                            value = ruleVariable.getOriginValue();
                        }
                        templateArgumentRequest.setArgumentStep(InputActionStepEnum.TEMPLATE_INPUT_META.getCode());
                        templateArgumentRequest.setArgumentId(templateMidTableInputMeta.getId());
                        templateArgumentRequest.setArgumentValue(value);
                        templateArgumentRequest.setArgumentType(templateMidTableInputMeta.getInputType());

                        this.templateArguments.add(templateArgumentRequest);
                    }
                }
            }
        }

        for (TemplateStatisticsInputMeta templateStatisticsInputMeta : customRule.getTemplate().getStatisticAction()) {
            if (TemplateStatisticsUtil.shouldResponse(templateStatisticsInputMeta)) {
                TemplateArgumentRequest templateArgumentRequest = new TemplateArgumentRequest();
                templateArgumentRequest.setArgumentStep(InputActionStepEnum.STATISTICS_ARG.getCode());
                templateArgumentRequest.setArgumentId(templateStatisticsInputMeta.getId());
                this.templateArguments.add(templateArgumentRequest);
            }
        }

        addAlarmVariable(customRule);

        if (CollectionUtils.isNotEmpty(customRule.getRuleDataSources())) {
            RuleDataSource originalRuleDataSource = customRule.getRuleDataSources().stream()
                    .filter(ruleDataSource -> (ruleDataSource.getDatasourceIndex() != null && ruleDataSource.getDatasourceIndex().equals(-1))).iterator().next();
            this.clusterName = originalRuleDataSource.getClusterName();
            this.proxyUser = originalRuleDataSource.getProxyUser();

            this.linkisDataSourceId = originalRuleDataSource.getLinkisDataSourceId();
            this.linkisDataSourceName = originalRuleDataSource.getLinkisDataSourceName();
            this.linkisDataSourceVersionId = originalRuleDataSource.getLinkisDataSourceVersionId();
            if (null != linkisDataSourceId) {
                this.linkisDataSourceType = TemplateDataSourceTypeEnum.getMessage(originalRuleDataSource.getDatasourceType());
                Set<RuleDataSourceEnv> dataSourceEnvs = originalRuleDataSource.getRuleDataSourceEnvs().stream().collect(Collectors.toSet());
                if (CollectionUtils.isNotEmpty(dataSourceEnvs)) {
                    List<DataSourceEnvRequest> dataSourceEnvRequestList = new ArrayList<>(dataSourceEnvs.size());
                    List<DataSourceEnvMappingRequest> dataSourceEnvMappingRequestList = new ArrayList<>(dataSourceEnvs.size());

                    Map<String, List<RuleDataSourceEnv>> dataSourceEnvMappingResponseMap = new HashMap<>();
                    for (RuleDataSourceEnv env : dataSourceEnvs) {
                        if (StringUtils.isNotEmpty(env.getDbAndTable())) {
                            if (CollectionUtils.isEmpty(dataSourceEnvMappingResponseMap.get(env.getDbAndTable()))) {
                                List<RuleDataSourceEnv> envList = new ArrayList<>();
                                envList.add(env);
                                dataSourceEnvMappingResponseMap.put(env.getDbAndTable(), envList);
                            } else {
                                dataSourceEnvMappingResponseMap.get(env.getDbAndTable()).add(env);
                            }
                        } else {
                            DataSourceEnvRequest dataSourceEnvRequest = new DataSourceEnvRequest(env);
                            dataSourceEnvRequestList.add(dataSourceEnvRequest);
                        }
                    }

                    for (String key : dataSourceEnvMappingResponseMap.keySet()) {
                        DataSourceEnvMappingRequest dataSourceEnvMappingRequest = new DataSourceEnvMappingRequest(key, dataSourceEnvMappingResponseMap.get(key));
                        dataSourceEnvMappingRequestList.add(dataSourceEnvMappingRequest);
                    }
                    this.dataSourceEnvRequests = dataSourceEnvRequestList;
                    this.dataSourceEnvMappingRequests = dataSourceEnvMappingRequestList;
                }
            }
            this.type = TemplateDataSourceTypeEnum.getMessage(originalRuleDataSource.getDatasourceType());
            for (RuleDataSource ruleDataSource : customRule.getRuleDataSources()) {
                if (StringUtils.isNotBlank(ruleDataSource.getFileId())) {
                    this.fileId = ruleDataSource.getFileId();
                    this.fileDb = ruleDataSource.getDbName();
                    String table = ruleDataSource.getTableName();
                    // UUID remove.
                    if (StringUtils.isNotBlank(ruleDataSource.getFileId()) && StringUtils.isNotBlank(table) && table.contains("_") && table.length() - 33 > 0) {
                        table = table.substring(0, table.length() - 33);
                    }
                    this.fileTable = table;
                    this.fileTableDesc = ruleDataSource.getFileTableDesc();
                    this.fileDelimiter = " ".equals(ruleDataSource.getFileDelimiter()) ? SpecCharEnum.STAR.getValue() : ruleDataSource.getFileDelimiter();
                    this.fileType = ruleDataSource.getFileType();
                    this.fileHeader = ruleDataSource.getFileHeader();
                    this.fpsFile = true;
                    this.fileHashValues = ruleDataSource.getFileHashValue();
                }
            }
        }

        this.sqlCheckArea = customRule.getTemplate().getMidTableAction();
        super.setExecutionParametersName(customRule.getExecutionParametersName());
        if (StringUtils.isNotBlank(customRule.getExecutionParametersName())) {
            ExecutionParameters executionParameters = SpringContextHolder.getBean(ExecutionParametersDao.class).findByNameAndProjectId(customRule.getExecutionParametersName(), customRule.getProject().getId());
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
                super.setRuleEnable(customRule.getEnable());
                super.setUnionAll(executionParameters.getUnionAll());
                if (StringUtils.isNotBlank(executionParameters.getCluster()) || StringUtils.isNotBlank(executionParameters.getAbnormalProxyUser()) || StringUtils.isNotBlank(executionParameters.getAbnormalDatabase())) {
                    this.abnormalDataStorage = true;
                } else {
                    this.abnormalDataStorage = false;
                }
            } else {
                setBaseInfo(customRule.getUnionAll(), customRule.getEnable(), customRule.getSpecifyStaticStartupParam(), customRule.getStaticStartupParam(), customRule.getAbortOnFailure(), customRule.getAlert(), customRule.getAlertLevel(), customRule.getAlertReceiver(), customRule.getAbnormalDatabase(), customRule.getAbnormalCluster(), customRule.getAbnormalProxyUser(), customRule.getDeleteFailCheckResult(), this.alarmVariable);
            }
        } else {
            setBaseInfo(customRule.getUnionAll(), customRule.getEnable(), customRule.getSpecifyStaticStartupParam(), customRule.getStaticStartupParam(), customRule.getAbortOnFailure(), customRule.getAlert(), customRule.getAlertLevel(), customRule.getAlertReceiver(), customRule.getAbnormalDatabase(), customRule.getAbnormalCluster(), customRule.getAbnormalProxyUser(), customRule.getDeleteFailCheckResult(), this.alarmVariable);
        }

    }


    private void setBaseInfo(Boolean unionAll, Boolean enable, Boolean specifyStaticStartupParam, String staticStartupParam, Boolean abortOnFailure, Boolean alert, Integer alertLevel, String alertReceiver, String abnormalDatabase, String cluster, String abnormalProxyUser, Boolean deleteFailCheckResult, List<AlarmConfigResponse> alarmVariable) {
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
        super.setUnionAll(unionAll);
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
    }

    public String getFileHashValues() {
        return fileHashValues;
    }

    public void setFileHashValues(String fileHashValues) {
        this.fileHashValues = fileHashValues;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
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

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public boolean isContextService() {
        return contextService;
    }

    public void setContextService(boolean contextService) {
        this.contextService = contextService;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileDb() {
        return fileDb;
    }

    public void setFileDb(String fileDb) {
        this.fileDb = fileDb;
    }

    public String getFileTable() {
        return fileTable;
    }

    public void setFileTable(String fileTable) {
        this.fileTable = fileTable;
    }

    public String getFileTableDesc() {
        return fileTableDesc;
    }

    public void setFileTableDesc(String fileTableDesc) {
        this.fileTableDesc = fileTableDesc;
    }

    public String getFileDelimiter() {
        return fileDelimiter;
    }

    public void setFileDelimiter(String fileDelimiter) {
        this.fileDelimiter = fileDelimiter;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public boolean isFpsFile() {
        return fpsFile;
    }

    public void setFpsFile(boolean fpsFile) {
        this.fpsFile = fpsFile;
    }

    public Boolean getFileHeader() {
        return fileHeader;
    }

    public void setFileHeader(Boolean fileHeader) {
        this.fileHeader = fileHeader;
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

    public String getSqlCheckArea() {
        return sqlCheckArea;
    }

    public void setSqlCheckArea(String sqlCheckArea) {
        this.sqlCheckArea = sqlCheckArea;
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

    public String getLinkisDataSourceType() {
        return linkisDataSourceType;
    }

    public void setLinkisDataSourceType(String linkisDataSourceType) {
        this.linkisDataSourceType = linkisDataSourceType;
    }

    public List<DataSourceEnvRequest> getDataSourceEnvRequests() {
        return dataSourceEnvRequests;
    }

    public void setDataSourceEnvRequests(List<DataSourceEnvRequest> dataSourceEnvRequests) {
        this.dataSourceEnvRequests = dataSourceEnvRequests;
    }

    public List<DataSourceEnvMappingRequest> getDataSourceEnvMappingRequests() {
        return dataSourceEnvMappingRequests;
    }

    public void setDataSourceEnvMappingRequests(
        List<DataSourceEnvMappingRequest> dataSourceEnvMappingRequests) {
        this.dataSourceEnvMappingRequests = dataSourceEnvMappingRequests;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Boolean getAbnormalDataStorage() {
        return abnormalDataStorage;
    }

    public void setAbnormalDataStorage(Boolean abnormalDataStorage) {
        this.abnormalDataStorage = abnormalDataStorage;
    }

    public List<AlarmConfigResponse> getAlarmVariable() {
        return alarmVariable;
    }

    public void setAlarmVariable(List<AlarmConfigResponse> alarmVariable) {
        this.alarmVariable = alarmVariable;
    }

    private void addAlarmVariable(Rule customRule) {
        this.alarmVariable=new ArrayList<>();
        for (AlarmConfig alarmConfig : customRule.getAlarmConfigs()) {
            this.alarmVariable.add(new AlarmConfigResponse(alarmConfig, RuleTypeEnum.CUSTOM_RULE.getCode()));
        }
    }

    public List<String> getLinkisUdfNames() {
        return linkisUdfNames;
    }

    public void setLinkisUdfNames(List<String> linkisUdfNames) {
        this.linkisUdfNames = linkisUdfNames;
    }
}
