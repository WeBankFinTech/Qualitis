package com.webank.wedatasphere.qualitis.rule.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.OneToMany;
import javax.persistence.UniqueConstraint;

/**
 * @author v_gaojiedeng
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_execution_parameters", uniqueConstraints = @UniqueConstraint(columnNames = {"project_id","name"}))
public class ExecutionParameters {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 128)
    private String name;

    @Column(name = "abort_on_failure")
    private Boolean abortOnFailure;

    @Column(name = "specify_static_startup_param")
    private Boolean specifyStaticStartupParam;

    @Column(name = "static_startup_param")
    private String staticStartupParam;

    @Column(name = "execution_param")
    private String executionParam;

    @Column(name = "alert")
    private Boolean alert;

    @Column(name = "alert_level")
    private Integer alertLevel;

    @Column(name = "alert_receiver")
    private String alertReceiver;

    @Column(name = "project_id", length = 20)
    private Long projectId;

    @Column(name = "abnormal_data_storage")
    private Boolean abnormalDataStorage;

    @Column(name = "abnormal_proxy_user", length = 50)
    private String abnormalProxyUser;

    @Column(name = "abnormal_database",length = 100)
    private String abnormalDatabase;

    @Column(name = "cluster",length = 100)
    private String cluster;

    @Column(name = "create_user", length = 50)
    private String createUser;

    @Column(name = "create_time", length = 25)
    private String createTime;

    @Column(name = "modify_user", length = 50)
    private String modifyUser;

    @Column(name = "modify_time", length = 25)
    private String modifyTime;

    @Column(name="specify_filter")
    private Boolean specifyFilter;

    @Column(length = 5000)
    private String filter;

    @Column(name="source_table_filter")
    private String sourceTableFilter;

    @Column(name="target_table_filter")
    private String targetTableFilter;

    @Column(name = "delete_fail_check_result")
    private Boolean deleteFailCheckResult;

    @Column(name = "upload_rule_metric_value")
    private Boolean uploadRuleMetricValue;

    @Column(name = "upload_abnormal_value")
    private Boolean uploadAbnormalValue;

    @Column(name = "union_way")
    private Integer unionWay;

    @OneToMany(mappedBy = "executionParameters", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<StaticExecutionParameters> staticExecutionParameters;

    @OneToMany(mappedBy = "executionParameters", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<AlarmArgumentsExecutionParameters> alarmArgumentsExecutionParameters;

    @OneToMany(mappedBy = "executionParameters", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<NoiseEliminationManagement> noiseEliminationManagement;

    @OneToMany(mappedBy = "executionParameters", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<ExecutionVariable> executionVariableSets;

    @Column(name="whether_noise")
    private Boolean whetherNoise;

    @Column(name="execution_variable")
    private Boolean executionVariable;

    @Column(name="advanced_execution")
    private Boolean advancedExecution;

    @Column(name="engine_reuse")
    private Boolean engineReuse;

    @Column(name="concurrency_granularity", columnDefinition = "MEDIUMTEXT")
    private String concurrencyGranularity;

    @Column(name="dynamic_partitioning")
    private Boolean dynamicPartitioning;

    @Column(name="top_partition")
    private String topPartition;


    public ExecutionParameters() {
        // Do nothing.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getExecutionParam() {
        return executionParam;
    }

    public void setExecutionParam(String executionParam) {
        this.executionParam = executionParam;
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

    public Boolean getAbnormalDataStorage() {
        return abnormalDataStorage;
    }

    public void setAbnormalDataStorage(Boolean abnormalDataStorage) {
        this.abnormalDataStorage = abnormalDataStorage;
    }

    public String getAbnormalProxyUser() {
        return abnormalProxyUser;
    }

    public void setAbnormalProxyUser(String abnormalProxyUser) {
        this.abnormalProxyUser = abnormalProxyUser;
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

    public Integer getUnionWay() {
        return unionWay;
    }

    public void setUnionWay(Integer unionWay) {
        this.unionWay = unionWay;
    }

    public Set<StaticExecutionParameters> getStaticExecutionParameters() {
        return staticExecutionParameters;
    }

    public void setStaticExecutionParameters(Set<StaticExecutionParameters> staticExecutionParameters) {
        this.staticExecutionParameters = staticExecutionParameters;
    }

    public Set<AlarmArgumentsExecutionParameters> getAlarmArgumentsExecutionParameters() {
        return alarmArgumentsExecutionParameters;
    }

    public void setAlarmArgumentsExecutionParameters(
        Set<AlarmArgumentsExecutionParameters> alarmArgumentsExecutionParameters) {
        this.alarmArgumentsExecutionParameters = alarmArgumentsExecutionParameters;
    }

    public Set<NoiseEliminationManagement> getNoiseEliminationManagement() {
        return noiseEliminationManagement;
    }

    public void setNoiseEliminationManagement(Set<NoiseEliminationManagement> noiseEliminationManagement) {
        this.noiseEliminationManagement = noiseEliminationManagement;
    }

    public Boolean getWhetherNoise() {
        return whetherNoise;
    }

    public void setWhetherNoise(Boolean whetherNoise) {
        this.whetherNoise = whetherNoise;
    }

    public Boolean getExecutionVariable() {
        return executionVariable;
    }

    public void setExecutionVariable(Boolean executionVariable) {
        this.executionVariable = executionVariable;
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

    public Set<ExecutionVariable> getExecutionVariableSets() {
        return executionVariableSets;
    }

    public void setExecutionVariableSets(Set<ExecutionVariable> executionVariableSets) {
        this.executionVariableSets = executionVariableSets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExecutionParameters that = (ExecutionParameters) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ExecutionParameters{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", abortOnFailure=" + abortOnFailure +
                ", specifyStaticStartupParam=" + specifyStaticStartupParam +
                ", staticStartupParam='" + staticStartupParam + '\'' +
                ", executionParam='" + executionParam + '\'' +
                ", alert=" + alert +
                ", alertLevel=" + alertLevel +
                ", alertReceiver='" + alertReceiver + '\'' +
                ", projectId=" + projectId +
                ", abnormalDataStorage=" + abnormalDataStorage +
                ", abnormalProxyUser='" + abnormalProxyUser + '\'' +
                ", abnormalDatabase='" + abnormalDatabase + '\'' +
                ", cluster='" + cluster + '\'' +
                ", createUser='" + createUser + '\'' +
                ", createTime='" + createTime + '\'' +
                ", modifyUser='" + modifyUser + '\'' +
                ", modifyTime='" + modifyTime + '\'' +
                ", specifyFilter=" + specifyFilter +
                ", filter='" + filter + '\'' +
                ", sourceTableFilter='" + sourceTableFilter + '\'' +
                ", targetTableFilter='" + targetTableFilter + '\'' +
                ", deleteFailCheckResult=" + deleteFailCheckResult +
                ", uploadRuleMetricValue=" + uploadRuleMetricValue +
                ", uploadAbnormalValue=" + uploadAbnormalValue +
                ", unionWay=" + unionWay +
                ", staticExecutionParameters=" + staticExecutionParameters +
                ", alarmArgumentsExecutionParameters=" + alarmArgumentsExecutionParameters +
                ", noiseEliminationManagement=" + noiseEliminationManagement +
                ", executionVariableSets=" + executionVariableSets +
                ", whetherNoise=" + whetherNoise +
                ", executionVariable=" + executionVariable +
                ", advancedExecution=" + advancedExecution +
                ", engineReuse=" + engineReuse +
                ", concurrencyGranularity='" + concurrencyGranularity + '\'' +
                ", dynamicPartitioning=" + dynamicPartitioning +
                ", topPartition='" + topPartition + '\'' +
                '}';
    }
}
