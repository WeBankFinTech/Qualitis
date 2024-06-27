package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.rule.constant.AlarmEventEnum;
import com.webank.wedatasphere.qualitis.rule.constant.DynamicEngineEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author v_gaojiedeng
 */
public class AddExecutionParametersRequest extends AbstractAddRequest {
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
    @JsonProperty("cluster")
    private String cluster;
    @JsonProperty("abnormal_database")
    private String abnormalDatabase;
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

    private static final String RM_YARNQUEUE = "wds.linkis.rm.yarnqueue";
    private static final String SPARK_DRIVER = "spark.driver";
    private static final String SPARK_EXECUTOR = "spark.executor";
    private static final String SPARK_ENGINECONN = "wds.linkis.engineconn";

    private static final Map<String, Integer> ALERT_LEVEL_CODE = new HashMap<String, Integer>();

    static {
        ALERT_LEVEL_CODE.put("CRITICAL", 1);
        ALERT_LEVEL_CODE.put("MAJOR", 2);
        ALERT_LEVEL_CODE.put("MINOR", 3);
        ALERT_LEVEL_CODE.put("WARNING", 4);
        ALERT_LEVEL_CODE.put("INFO", 5);
    }

    public AddExecutionParametersRequest() {
    }

    /**
     * 1.告警人 alert_receiver  告警级别alert_level
     * 2.动态引擎配置 staticStartupParam   格式(可以是多个，匹配字符串前缀
     * (1)yarn参数 wds.linkis.rm.yarnqueue,
     * (2)spark参数 a.spark.driver；b.spark.executor; )
     * 参数类型(1.yarn参数 2.spark参数)：wds.linkis.rm.yarnqueue=queue_05;
     *
     * @param abstrackAddRequest
     */
    public AddExecutionParametersRequest(AbstractCommonRequest abstrackAddRequest) throws UnExpectedRequestException {
        BeanUtils.copyProperties(abstrackAddRequest, this);

        List<AlarmArgumentsExecutionParametersRequest> lists = Lists.newArrayList();
        if (StringUtils.isNotBlank(abstrackAddRequest.getAlertReceiver()) && abstrackAddRequest.getAlertLevel() != null) {
            AlarmArgumentsExecutionParametersRequest alarmArgumentsExecutionParameters = new AlarmArgumentsExecutionParametersRequest();
            alarmArgumentsExecutionParameters.setAlarmEvent(AlarmEventEnum.CHECK_FAILURE.getCode());
            alarmArgumentsExecutionParameters.setAlarmLevel(abstrackAddRequest.getAlertLevel());
            alarmArgumentsExecutionParameters.setAlarmReceiver(abstrackAddRequest.getAlertReceiver());
            lists.add(alarmArgumentsExecutionParameters);
            this.alert = true;
        }

        if (StringUtils.isNotBlank(abstrackAddRequest.getExecutionCompleted())) {
            handleAlarmEvent(lists, abstrackAddRequest.getExecutionCompleted(), AlarmEventEnum.EXECUTION_COMPLETED);
        }
        if (StringUtils.isNotBlank(abstrackAddRequest.getVerificationSuccessful())) {
            handleAlarmEvent(lists, abstrackAddRequest.getVerificationSuccessful(), AlarmEventEnum.CHECK_SUCCESS);
        }
        if (StringUtils.isNotBlank(abstrackAddRequest.getVerificationFailed())) {
            List<AlarmArgumentsExecutionParametersRequest> resultList = lists.stream().filter(item -> item.getAlarmEvent().equals(AlarmEventEnum.CHECK_FAILURE.getCode())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(resultList)) {
                lists.removeAll(resultList);
            }
            handleAlarmEvent(lists, abstrackAddRequest.getVerificationFailed(), AlarmEventEnum.CHECK_FAILURE);
        }
        this.alarmArgumentsExecutionParametersRequests = lists;

        if (StringUtils.isNotBlank(abstrackAddRequest.getStaticStartupParam())) {
            String[] dynamicEngine = abstrackAddRequest.getStaticStartupParam().split(SpecCharEnum.DIVIDER.getValue());
            List<StaticExecutionParametersRequest> parametersRequests = Lists.newArrayList();
            for (String temp : dynamicEngine) {
                String[] strs = temp.split(SpecCharEnum.EQUAL.getValue());
                StaticExecutionParametersRequest staticExecutionParametersRequest = new StaticExecutionParametersRequest();
                staticExecutionParametersRequest.setParameterName(strs[0]);
                staticExecutionParametersRequest.setParameterType(prefixMatching(strs[0]));
                staticExecutionParametersRequest.setParameterValue(strs[1]);
                parametersRequests.add(staticExecutionParametersRequest);
            }
            this.staticExecutionParametersRequests = parametersRequests;
            this.specifyStaticStartupParam = true;
        }

        if (null == this.getAdvancedExecution()) {
            this.setAdvancedExecution(true);
        }
        if (StringUtils.isBlank(this.getConcurrencyGranularity())) {
            this.setConcurrencyGranularity("split_by:merge");
        }
        if (null == this.getEngineReuse()) {
            this.setEngineReuse(true);
        }
        if (null == this.getDynamicPartitioning()) {
            this.setDynamicPartitioning(false);
        }
    }

    private void handleAlarmEvent(List<AlarmArgumentsExecutionParametersRequest> lists, String verificationSuccessful, AlarmEventEnum checkSuccess) throws UnExpectedRequestException {
        String[] alertStrs = verificationSuccessful.split(SpecCharEnum.COLON.getValue());
        String alarmLevel = alertStrs[0].toUpperCase();
        if (ALERT_LEVEL_CODE.keySet().contains(alarmLevel)) {
            AlarmArgumentsExecutionParametersRequest alarmArgumentsExecutionParameters = new AlarmArgumentsExecutionParametersRequest();
            alarmArgumentsExecutionParameters.setAlarmEvent(checkSuccess.getCode());
            alarmArgumentsExecutionParameters.setAlarmLevel(ALERT_LEVEL_CODE.get(alarmLevel));
            alarmArgumentsExecutionParameters.setAlarmReceiver(alertStrs[1].trim());
            lists.add(alarmArgumentsExecutionParameters);
            this.alert = true;
        } else {
            throw new UnExpectedRequestException("Please check alert level name. Alert name: " + alarmLevel);
        }
    }

    public Integer prefixMatching(String name) {
        if (name.startsWith(RM_YARNQUEUE)) {
            return DynamicEngineEnum.YARN_ARGUMENTS.getCode();
        } else if (name.startsWith(SPARK_DRIVER)) {
            return DynamicEngineEnum.SPARK_ARGUMENTS.getCode();
        } else if (name.startsWith(SPARK_EXECUTOR)) {
            return DynamicEngineEnum.SPARK_ARGUMENTS.getCode();
        } else if (name.startsWith(SPARK_ENGINECONN)) {
            return DynamicEngineEnum.SPARK_ARGUMENTS.getCode();
        } else {
            return DynamicEngineEnum.CUSTOM_ARGUMENTS.getCode();
        }
    }

    public Integer getUnionWay() {
        return unionWay;
    }

    public void setUnionWay(Integer unionWay) {
        this.unionWay = unionWay;
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
        return "AddExecutionParametersRequest{" +
                "name='" + name + '\'' +
                ", abortOnFailure=" + abortOnFailure +
                ", specifyStaticStartupParam=" + specifyStaticStartupParam +
                ", staticStartupParam='" + staticStartupParam + '\'' +
                ", alert=" + alert +
                ", alertLevel=" + alertLevel +
                ", alertReceiver='" + alertReceiver + '\'' +
                ", projectId=" + projectId +
                ", cluster='" + cluster + '\'' +
                ", abnormalDatabase='" + abnormalDatabase + '\'' +
                ", abnormalProxyUser='" + abnormalProxyUser + '\'' +
                ", abnormalDataStorage=" + abnormalDataStorage +
                ", specifyFilter=" + specifyFilter +
                ", filter='" + filter + '\'' +
                ", deleteFailCheckResult=" + deleteFailCheckResult +
                ", uploadRuleMetricValue=" + uploadRuleMetricValue +
                ", uploadAbnormalValue=" + uploadAbnormalValue +
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
                "} " + super.toString();
    }

    public static void checkRequest(AddExecutionParametersRequest request, boolean modifyOrNot) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkString(request.getName(), "name");
        CommonChecker.checkObject(request.getAbortOnFailure(), "abort_on_failure");

        CommonChecker.checkObject(request.getSpecifyStaticStartupParam(), "specify_static_startup_param");
//        if (request.getSpecifyStaticStartupParam()) {
//            CommonChecker.checkCollections(request.getStaticExecutionParametersRequests(),"static_execution_parameters");
//        }
        CommonChecker.checkObject(request.getAlert(), "alert");
//        if (request.getAlert()) {
//            CommonChecker.checkCollections(request.getAlarmArgumentsExecutionParametersRequests(),"alarm_arguments_execution_parameters");
//        }

//        CommonChecker.checkObject(request.getAbnormalDataStorage(), "abnormal_data_storage");
//        if (request.getAbnormalDataStorage()) {
//            CommonChecker.checkString(request.getCluster(),"cluster");
//            CommonChecker.checkString(request.getAbnormalDatabase(),"abnormal_database");
//            CommonChecker.checkString(request.getAbnormalProxyUser(),"abnormal_proxy_user");
//        }
//
//        CommonChecker.checkObject(request.getSpecifyFilter(), "specify_filter");

        if (!modifyOrNot) {
            CommonChecker.checkObject(request.getProjectId(), "project_id");
        }

        CommonChecker.checkStringLength(request.getName(), 128, "name");
        if (StringUtils.isNotBlank(request.getAlertReceiver())) {
            Long alertReceiverCount = StreamSupport.stream(Splitter.on(SpecCharEnum.COMMA.getValue()).omitEmptyStrings().trimResults().split(request.getAlertReceiver()).spliterator(), false).count();
            if (alertReceiverCount > 13L) {
                throw new UnExpectedRequestException("alert_receiver {&EXCEED_MAX_LENGTH}: 13");
            }
        }
    }
}
