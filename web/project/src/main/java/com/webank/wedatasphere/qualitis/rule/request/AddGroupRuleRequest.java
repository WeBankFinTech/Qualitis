package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2022/6/2 10:30
 */
public class AddGroupRuleRequest {
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("rule_type")
    private Integer ruleType;
    @JsonProperty("cn_name")
    private String ruleCnName;
    @JsonProperty("rule_detail")
    private String ruleDetail;
    @JsonProperty("rule_template_id")
    private Long ruleTemplateId;
    @JsonProperty("alert")
    private Boolean alert;
    @JsonProperty("alert_level")
    private Integer alertLevel;
    @JsonProperty("alert_receiver")
    private String alertReceiver;
    @JsonProperty("alarm")
    private Boolean alarm;
    @JsonProperty("alarm_variable")
    private List<AlarmConfigRequest> alarmVariable;
    @JsonProperty("file_alarm_variable")
    private List<FileAlarmConfigRequest> fileAlarmVariable;
    @JsonProperty("col_names")
    private List<DataSourceColumnRequest> colNames;
    private List<DataSourceRequest> datasource;
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("template_arguments")
    private List<TemplateArgumentRequest> templateArgumentRequests;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;
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
    @JsonProperty("bash_content")
    private String bashContent;

    private List<String> ruleMetricNamesForBdpClient;

    @JsonProperty("execution_parameters_name")
    private String executionParametersName;

    @JsonProperty("cluster")
    private String abnormalCluster;

    @JsonProperty("abnormal_database")
    private String abnormalDatabase;

    @JsonProperty("abnormal_proxy_user")
    private String abnormalProxyUser;

    @JsonProperty("filter")
    private String filter;
    @JsonProperty("rule_enable")
    private Boolean ruleEnable;
    @JsonProperty("union_all")
    private Boolean unionAll;

    public AddGroupRuleRequest() {
        // Default Constructor
    }

    public Boolean getUnionAll() {
        return unionAll;
    }

    public void setUnionAll(Boolean unionAll) {
        this.unionAll = unionAll;
    }

    public Boolean getRuleEnable() {
        return ruleEnable;
    }

    public void setRuleEnable(Boolean ruleEnable) {
        this.ruleEnable = ruleEnable;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
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

    public List<AlarmConfigRequest> getAlarmVariable() {
        return alarmVariable;
    }

    public void setAlarmVariable(List<AlarmConfigRequest> alarmVariable) {
        this.alarmVariable = alarmVariable;
    }

    public List<FileAlarmConfigRequest> getFileAlarmVariable() {
        return fileAlarmVariable;
    }

    public void setFileAlarmVariable(List<FileAlarmConfigRequest> fileAlarmVariable) {
        this.fileAlarmVariable = fileAlarmVariable;
    }

    public Boolean getAlarm() {
        return alarm;
    }

    public void setAlarm(Boolean alarm) {
        this.alarm = alarm;
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

    public List<DataSourceColumnRequest> getColNames() {
        return colNames;
    }

    public void setColNames(List<DataSourceColumnRequest> colNames) {
        this.colNames = colNames;
    }

    public List<DataSourceRequest> getDatasource() {
        return datasource;
    }

    public void setDatasource(List<DataSourceRequest> datasource) {
        this.datasource = datasource;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getCsId() { return csId; }

    public void setCsId(String csId) { this.csId = csId; }

    public Boolean getAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(Boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
    }

    public List<TemplateArgumentRequest> getTemplateArgumentRequests() {
        return templateArgumentRequests;
    }

    public void setTemplateArgumentRequests(List<TemplateArgumentRequest> templateArgumentRequests) {
        this.templateArgumentRequests = templateArgumentRequests;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public String getAlertReceiver() {
        return alertReceiver;
    }

    public void setAlertReceiver(String alertReceiver) {
        this.alertReceiver = alertReceiver;
    }

    public Boolean getDeleteFailCheckResult() {
        return deleteFailCheckResult;
    }

    public void setDeleteFailCheckResult(Boolean deleteFailCheckResult) {
        this.deleteFailCheckResult = deleteFailCheckResult;
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

    public List<String> getRuleMetricNamesForBdpClient() {
        return ruleMetricNamesForBdpClient;
    }

    public void setRuleMetricNamesForBdpClient(List<String> ruleMetricNamesForBdpClient) {
        this.ruleMetricNamesForBdpClient = ruleMetricNamesForBdpClient;
    }

    public String getBashContent() {
        return bashContent;
    }

    public void setBashContent(String bashContent) {
        this.bashContent = bashContent;
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

    public String getAbnormalCluster() {
        return abnormalCluster;
    }

    public void setAbnormalCluster(String abnormalCluster) {
        this.abnormalCluster = abnormalCluster;
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
}
