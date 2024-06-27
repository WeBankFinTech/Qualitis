package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Splitter;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.StreamSupport;

/**
 * @author v_gaojiedeng
 */
public class ModifyExecutionParametersRequest {
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

    @JsonProperty("union_way")
    private Integer unionWay;

    @JsonProperty("source_table_filter")
    private String sourceTableFilter;
    @JsonProperty("target_table_filter")
    private String targetTableFilter;
    @JsonProperty("static_execution_parameters")
    private List<StaticExecutionParametersRequest> staticExecutionParametersRequests;
    @JsonProperty("alarm_arguments_execution_parameters")
    private List<AlarmArgumentsExecutionParametersRequest> alarmArgumentsExecutionParametersRequests;

    @JsonProperty("whether_noise")
    private Boolean whetherNoise;
    @JsonProperty("noise_elimination_management")
    private List<NoiseEliminationManagementRequest> noiseEliminationManagementRequests;

    @JsonProperty("execution_variable")
    private Boolean executionVariable;
    @JsonProperty("execution_management")
    private List<ExecutionExecutionParametersRequest> executionManagementRequests;

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


    public ModifyExecutionParametersRequest() {
    }

    public ModifyExecutionParametersRequest(AbstractCommonRequest abstrackAddRequest, ExecutionParameters executionParametersInDb) {
        BeanUtils.copyProperties(abstrackAddRequest, this);
        this.executionParametersId = executionParametersInDb.getId();
    }

    public Integer getUnionWay() {
        return unionWay;
    }

    public void setUnionWay(Integer unionWay) {
        this.unionWay = unionWay;
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

    public List<StaticExecutionParametersRequest> getStaticExecutionParametersRequests() {
        return staticExecutionParametersRequests;
    }

    public void setStaticExecutionParametersRequests(List<StaticExecutionParametersRequest> staticExecutionParametersRequests) {
        this.staticExecutionParametersRequests = staticExecutionParametersRequests;
    }

    public List<AlarmArgumentsExecutionParametersRequest> getAlarmArgumentsExecutionParametersRequests() {
        return alarmArgumentsExecutionParametersRequests;
    }

    public void setAlarmArgumentsExecutionParametersRequests(List<AlarmArgumentsExecutionParametersRequest> alarmArgumentsExecutionParametersRequests) {
        this.alarmArgumentsExecutionParametersRequests = alarmArgumentsExecutionParametersRequests;
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

    public Boolean getWhetherNoise() {
        return whetherNoise;
    }

    public void setWhetherNoise(Boolean whetherNoise) {
        this.whetherNoise = whetherNoise;
    }

    public List<NoiseEliminationManagementRequest> getNoiseEliminationManagementRequests() {
        return noiseEliminationManagementRequests;
    }

    public void setNoiseEliminationManagementRequests(List<NoiseEliminationManagementRequest> noiseEliminationManagementRequests) {
        this.noiseEliminationManagementRequests = noiseEliminationManagementRequests;
    }

    public Boolean getExecutionVariable() {
        return executionVariable;
    }

    public void setExecutionVariable(Boolean executionVariable) {
        this.executionVariable = executionVariable;
    }

    public List<ExecutionExecutionParametersRequest> getExecutionManagementRequests() {
        return executionManagementRequests;
    }

    public void setExecutionManagementRequests(List<ExecutionExecutionParametersRequest> executionManagementRequests) {
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
        return "ModifyExecutionParametersRequest{" +
                "executionParametersId=" + executionParametersId +
                ", name='" + name + '\'' +
                ", abortOnFailure=" + abortOnFailure +
                ", specifyStaticStartupParam=" + specifyStaticStartupParam +
                ", staticStartupParam='" + staticStartupParam + '\'' +
                ", alert=" + alert +
                ", alertLevel=" + alertLevel +
                ", alertReceiver='" + alertReceiver + '\'' +
                ", projectId=" + projectId +
                ", abnormalDatabase='" + abnormalDatabase + '\'' +
                ", cluster='" + cluster + '\'' +
                ", abnormalProxyUser='" + abnormalProxyUser + '\'' +
                ", abnormalDataStorage=" + abnormalDataStorage +
                ", specifyFilter=" + specifyFilter +
                ", filter='" + filter + '\'' +
                ", deleteFailCheckResult=" + deleteFailCheckResult +
                ", uploadRuleMetricValue=" + uploadRuleMetricValue +
                ", uploadAbnormalValue=" + uploadAbnormalValue +
                ", unionAll=" + unionAll +
                ", unionWay=" + unionWay +
                ", sourceTableFilter='" + sourceTableFilter + '\'' +
                ", targetTableFilter='" + targetTableFilter + '\'' +
                ", staticExecutionParametersRequests=" + staticExecutionParametersRequests +
                ", alarmArgumentsExecutionParametersRequests=" + alarmArgumentsExecutionParametersRequests +
                ", whetherNoise=" + whetherNoise +
                ", noiseEliminationManagementRequests=" + noiseEliminationManagementRequests +
                ", executionVariable=" + executionVariable +
                ", executionManagementRequests=" + executionManagementRequests +
                ", advancedExecution=" + advancedExecution +
                ", engineReuse=" + engineReuse +
                ", concurrencyGranularity='" + concurrencyGranularity + '\'' +
                ", dynamicPartitioning=" + dynamicPartitioning +
                ", topPartition='" + topPartition + '\'' +
                '}';
    }

    public static void checkRequest(ModifyExecutionParametersRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkObject(request.getExecutionParametersId(), "ExecutionParametersId");
        AddExecutionParametersRequest addExecutionParametersRequest = new AddExecutionParametersRequest();
        BeanUtils.copyProperties(request, addExecutionParametersRequest);
        AddExecutionParametersRequest.checkRequest(addExecutionParametersRequest,true);

        if (StringUtils.isNotBlank(request.getAlertReceiver())) {
            Long alertReceiverCount = StreamSupport.stream(Splitter.on(SpecCharEnum.COMMA.getValue()).omitEmptyStrings().trimResults().split(request.getAlertReceiver()).spliterator(), false).count();
            if (alertReceiverCount > 13L) {
                throw new UnExpectedRequestException("alert_receiver {&EXCEED_MAX_LENGTH}: 13");
            }
        }
    }


}
