package com.webank.wedatasphere.qualitis.rule.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.rule.dao.AlarmArgumentsExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.dao.ExecutionVariableDao;
import com.webank.wedatasphere.qualitis.rule.dao.NoiseEliminationManagementDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleTemplateDao;
import com.webank.wedatasphere.qualitis.rule.dao.StaticExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.entity.AlarmArgumentsExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionVariable;
import com.webank.wedatasphere.qualitis.rule.entity.NoiseEliminationManagement;
import com.webank.wedatasphere.qualitis.rule.entity.StaticExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author v_gaojiedeng
 */
public class ExecutionParametersResponse {
    @JsonProperty("execution_parameters_id")
    private Long executionParametersId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("abort_on_failure")
    private Boolean abortOnFailure;
    @JsonProperty("specify_static_startup_param")
    private Boolean specifyStaticStartupParam;
    @JsonProperty("static_startup_param")
    private String staticStartupParam;
    @JsonProperty("alert")
    private Boolean alert;
    @JsonProperty("alert_level")
    private Integer alertLevel;
    @JsonProperty("alert_receiver")
    private String alertReceiver;
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("abnormal_database")
    private String abnormalDatabase;
    @JsonProperty("cluster")
    private String cluster;
    @JsonProperty("abnormal_proxy_user")
    private String abnormalProxyUser;
    @JsonProperty("abnormal_data_storage")
    private Boolean abnormalDataStorage;

    @JsonProperty("specify_filter")
    private Boolean specifyFilter;
    private String filter;

    @JsonProperty("delete_fail_check_result")
    private Boolean deleteFailCheckResult;
    @JsonProperty("upload_rule_metric_value")
    private Boolean uploadRuleMetricValue;
    @JsonProperty("upload_abnormal_value")
    private Boolean uploadAbnormalValue;
    @JsonProperty("union_all")
    private Boolean unionAll;
    @JsonProperty("source_table_filter")
    private String sourceTableFilter;
    @JsonProperty("target_table_filter")
    private String targetTableFilter;
    @JsonProperty("static_execution_parameters")
    private List<StaticExecutionParametersResponse> staticExecutionParametersRequests;
    @JsonProperty("alarm_arguments_execution_parameters")
    private List<AlarmArgumentsExecutionParametersResponse> alarmArgumentsExecutionParametersRequests;

    @JsonProperty("whether_noise")
    private Boolean whetherNoise;
    @JsonProperty("noise_elimination_management")
    private List<NoiseEliminationManagementResponse> noiseEliminationManagementRequests;

    @JsonProperty("execution_variable")
    private Boolean executionVariable;
    @JsonProperty("execution_management")
    private List<ExecutionVariableResponse> executionManagementRequests;

    @JsonProperty("advanced_execution")
    private Boolean advancedExecution;
    @JsonProperty("engine_reuse")
    private Boolean engineReuse;
    @JsonProperty("concurrency_granularity")
    private String concurrencyGranularity;
    @JsonProperty("dynamic_partitioning")
    private Boolean dynamicPartitioning;
    @JsonProperty("top_partition")
    private String topPartition;


    public ExecutionParametersResponse() {
    }

    public ExecutionParametersResponse(ExecutionParameters executionParameters) {
        this.executionParametersId = executionParameters.getId();
        this.name = executionParameters.getName();
        this.abortOnFailure = executionParameters.getAbortOnFailure();
        this.unionAll = executionParameters.getUnionAll();
        this.specifyStaticStartupParam = executionParameters.getSpecifyStaticStartupParam();
        if (specifyStaticStartupParam) {
            this.staticStartupParam = executionParameters.getStaticStartupParam();
        }

        this.alert = executionParameters.getAlert();
        if (alert) {
            this.alertLevel = executionParameters.getAlertLevel();
            this.alertReceiver = executionParameters.getAlertReceiver();
        }
        this.projectId = executionParameters.getProjectId();
        this.abnormalDatabase = executionParameters.getAbnormalDatabase();
        this.cluster = executionParameters.getCluster();
        this.abnormalProxyUser = executionParameters.getAbnormalProxyUser();

        if (StringUtils.isNotBlank(executionParameters.getCluster()) || StringUtils.isNotBlank(executionParameters.getAbnormalProxyUser()) || StringUtils.isNotBlank(executionParameters.getAbnormalDatabase())) {
            this.abnormalDataStorage = true;
        } else {
            this.abnormalDataStorage = false;
        }

        this.filter = executionParameters.getFilter();
        this.sourceTableFilter = executionParameters.getSourceTableFilter();
        this.targetTableFilter = executionParameters.getTargetTableFilter();
        if (StringUtils.isNotBlank(this.filter) || StringUtils.isNotBlank(this.sourceTableFilter) || StringUtils.isNotBlank(this.targetTableFilter)) {
            specifyFilter = true;
        } else {
            specifyFilter = false;
        }
        this.deleteFailCheckResult = executionParameters.getDeleteFailCheckResult();
        this.uploadRuleMetricValue = executionParameters.getUploadRuleMetricValue();
        this.uploadAbnormalValue = executionParameters.getUploadAbnormalValue();

        List<StaticExecutionParameters> staticExecutionParameters = SpringContextHolder.getBean(StaticExecutionParametersDao.class).findByExecutionParameters(executionParameters);
        List<StaticExecutionParametersResponse> collect = staticExecutionParameters.stream().map(temp -> {
            StaticExecutionParametersResponse staticExecutionParametersResponse = new StaticExecutionParametersResponse();
            staticExecutionParametersResponse.setParameterName(temp.getParameterName());
            staticExecutionParametersResponse.setParameterType(temp.getParameterType());
            staticExecutionParametersResponse.setParameterValue(temp.getParameterValue());
            staticExecutionParametersResponse.setParameterId(temp.getId());
            return staticExecutionParametersResponse;
        }).collect(Collectors.toList());
        this.staticExecutionParametersRequests = CollectionUtils.isNotEmpty(collect) ? collect : new ArrayList<>();
        List<AlarmArgumentsExecutionParameters> list = SpringContextHolder.getBean(AlarmArgumentsExecutionParametersDao.class).findByExecutionParameters(executionParameters);
        List<AlarmArgumentsExecutionParametersResponse> collectResponse = list.stream().map(temp -> {
            AlarmArgumentsExecutionParametersResponse alarmArgumentsExecutionParametersResponse = new AlarmArgumentsExecutionParametersResponse();
            alarmArgumentsExecutionParametersResponse.setAlarmEvent(temp.getAlarmEvent());
            alarmArgumentsExecutionParametersResponse.setAlarmId(temp.getId());
            alarmArgumentsExecutionParametersResponse.setAlarmLevel(temp.getAlarmLevel());
            alarmArgumentsExecutionParametersResponse.setAlarmReceiver(temp.getAlarmReceiver());
            return alarmArgumentsExecutionParametersResponse;
        }).collect(Collectors.toList());

        this.alarmArgumentsExecutionParametersRequests = CollectionUtils.isNotEmpty(collectResponse) ? collectResponse : new ArrayList<>();

        this.whetherNoise = executionParameters.getWhetherNoise();
        List<NoiseEliminationManagement> noiseEliminationManagements = SpringContextHolder.getBean(NoiseEliminationManagementDao.class).findByExecutionParameters(executionParameters);
        List<NoiseEliminationManagementResponse> noiseLists = noiseEliminationManagements.stream().map(temp -> {
            NoiseEliminationManagementResponse gather = new NoiseEliminationManagementResponse();
            gather.setNoiseId(temp.getId());
            gather.setDateSelectionMethod(temp.getDateSelectionMethod());
            gather.setBusinessDate(StringUtils.isNotBlank(temp.getBusinessDate()) ? StringEscapeUtils.unescapeJava(temp.getBusinessDate()) : "");
            gather.setTemplateId(temp.getTemplateId());
            gather.setNoiseNormRatio(StringUtils.isNotBlank(temp.getNoiseNormRatio()) ? StringEscapeUtils.unescapeJava(temp.getNoiseNormRatio()) : "");
            gather.setEliminateStrategy(temp.getEliminateStrategy());
            gather.setAvailable(temp.getAvailable());
            if (temp.getTemplateId() != null) {
                Template template = SpringContextHolder.getBean(RuleTemplateDao.class).findById(temp.getTemplateId());
                gather.setCuringConditions(template != null ? template.getWhetherSolidification() : false);
            } else {
                gather.setCuringConditions(false);
            }
            return gather;
        }).collect(Collectors.toList());

        this.noiseEliminationManagementRequests = CollectionUtils.isNotEmpty(noiseLists) ? noiseLists : new ArrayList<>();

        this.executionVariable = executionParameters.getExecutionVariable();
        List<ExecutionVariable> variableLists = SpringContextHolder.getBean(ExecutionVariableDao.class).findByExecutionParameters(executionParameters);

        List<ExecutionVariableResponse> executionVariableLists = variableLists.stream().map(temp -> {
            ExecutionVariableResponse gather = new ExecutionVariableResponse();
            gather.setVariableId(temp.getId());
            gather.setVariableType(temp.getVariableType());
            gather.setVariableName(temp.getVariableName());
            gather.setVariableValue(temp.getVariableValue());

            return gather;
        }).collect(Collectors.toList());
        this.executionManagementRequests = CollectionUtils.isNotEmpty(executionVariableLists) ? executionVariableLists : new ArrayList<>();

        this.advancedExecution = executionParameters.getAdvancedExecution();
        this.engineReuse = executionParameters.getEngineReuse();
        this.concurrencyGranularity = executionParameters.getConcurrencyGranularity();
        this.dynamicPartitioning = executionParameters.getDynamicPartitioning();
        this.topPartition = executionParameters.getTopPartition();

    }

    public Boolean getUnionAll() {
        return unionAll;
    }

    public void setUnionAll(Boolean unionAll) {
        this.unionAll = unionAll;
    }

    public Long getExecutionParametersId() {
        return executionParametersId;
    }

    public void setExecutionParametersId(Long executionParametersId) {
        this.executionParametersId = executionParametersId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(Boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

    public Boolean getAbnormalDataStorage() {
        return abnormalDataStorage;
    }

    public void setAbnormalDataStorage(Boolean abnormalDataStorage) {
        this.abnormalDataStorage = abnormalDataStorage;
    }

    public Boolean getSpecifyFilter() {
        return specifyFilter;
    }

    public void setSpecifyFilter(Boolean specifyFilter) {
        this.specifyFilter = specifyFilter;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Boolean getDeleteFailCheckResult() {
        return deleteFailCheckResult;
    }

    public void setDeleteFailCheckResult(Boolean deleteFailCheckResult) {
        this.deleteFailCheckResult = deleteFailCheckResult;
    }

    public Boolean getUploadRuleMetricValue() {
        return uploadRuleMetricValue;
    }

    public void setUploadRuleMetricValue(Boolean uploadRuleMetricValue) {
        this.uploadRuleMetricValue = uploadRuleMetricValue;
    }

    public Boolean getUploadAbnormalValue() {
        return uploadAbnormalValue;
    }

    public void setUploadAbnormalValue(Boolean uploadAbnormalValue) {
        this.uploadAbnormalValue = uploadAbnormalValue;
    }

    public String getSourceTableFilter() {
        return sourceTableFilter;
    }

    public void setSourceTableFilter(String sourceTableFilter) {
        this.sourceTableFilter = sourceTableFilter;
    }

    public String getTargetTableFilter() {
        return targetTableFilter;
    }

    public void setTargetTableFilter(String targetTableFilter) {
        this.targetTableFilter = targetTableFilter;
    }

    public List<StaticExecutionParametersResponse> getStaticExecutionParametersRequests() {
        return staticExecutionParametersRequests;
    }

    public void setStaticExecutionParametersRequests(List<StaticExecutionParametersResponse> staticExecutionParametersRequests) {
        this.staticExecutionParametersRequests = staticExecutionParametersRequests;
    }

    public List<AlarmArgumentsExecutionParametersResponse> getAlarmArgumentsExecutionParametersRequests() {
        return alarmArgumentsExecutionParametersRequests;
    }

    public void setAlarmArgumentsExecutionParametersRequests(List<AlarmArgumentsExecutionParametersResponse> alarmArgumentsExecutionParametersRequests) {
        this.alarmArgumentsExecutionParametersRequests = alarmArgumentsExecutionParametersRequests;
    }

    public Boolean getWhetherNoise() {
        return whetherNoise;
    }

    public void setWhetherNoise(Boolean whetherNoise) {
        this.whetherNoise = whetherNoise;
    }

    public List<NoiseEliminationManagementResponse> getNoiseEliminationManagementRequests() {
        return noiseEliminationManagementRequests;
    }

    public void setNoiseEliminationManagementRequests(List<NoiseEliminationManagementResponse> noiseEliminationManagementRequests) {
        this.noiseEliminationManagementRequests = noiseEliminationManagementRequests;
    }

    public Boolean getExecutionVariable() {
        return executionVariable;
    }

    public void setExecutionVariable(Boolean executionVariable) {
        this.executionVariable = executionVariable;
    }

    public List<ExecutionVariableResponse> getExecutionManagementRequests() {
        return executionManagementRequests;
    }

    public void setExecutionManagementRequests(List<ExecutionVariableResponse> executionManagementRequests) {
        this.executionManagementRequests = executionManagementRequests;
    }

    public Boolean getAdvancedExecution() {
        return advancedExecution;
    }

    public void setAdvancedExecution(Boolean advancedExecution) {
        this.advancedExecution = advancedExecution;
    }

    public Boolean getEngineReuse() {
        return engineReuse;
    }

    public void setEngineReuse(Boolean engineReuse) {
        this.engineReuse = engineReuse;
    }

    public String getConcurrencyGranularity() {
        return concurrencyGranularity;
    }

    public void setConcurrencyGranularity(String concurrencyGranularity) {
        this.concurrencyGranularity = concurrencyGranularity;
    }

    public Boolean getDynamicPartitioning() {
        return dynamicPartitioning;
    }

    public void setDynamicPartitioning(Boolean dynamicPartitioning) {
        this.dynamicPartitioning = dynamicPartitioning;
    }

    public String getTopPartition() {
        return topPartition;
    }

    public void setTopPartition(String topPartition) {
        this.topPartition = topPartition;
    }

    @Override
    public String toString() {
        return "ExecutionParametersResponse{" +
                "executionParametersId=" + executionParametersId +
                ", name='" + name + '\'' +
                ", abortOnFailure=" + abortOnFailure +
                ", specifyStaticStartupParam=" + specifyStaticStartupParam +
                ", staticStartupParam='" + staticStartupParam + '\'' +
                ", alert=" + alert +
                ", alertLevel=" + alertLevel +
                ", alertReceiver='" + alertReceiver + '\'' +
                ", projectId=" + projectId +
                '}';
    }
}
