package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public abstract class AbstractCommonRequest {
    @JsonProperty("rule_template_id")
    private Long ruleTemplateId;
    @JsonProperty("rule_id")
    private Long ruleId;
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("rule_detail")
    private String ruleDetail;
    @JsonProperty("rule_type")
    private Integer ruleType;
    @JsonProperty("cn_name")
    private String ruleCnName;
    @JsonProperty("alarm")
    private Boolean alarm;
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;
    @JsonProperty("new_rule_group_id")
    private Long newRuleGroupId;
    @JsonProperty("cs_id")
    private String csId;
    @JsonProperty("rule_group_name")
    private String ruleGroupName;
    private List<DataSourceRequest> datasource;
    @JsonProperty("bash_content")
    private String bashContent;
    private List<String> ruleMetricNamesForBdpClient;
    @JsonProperty("execution_parameters_name")
    private String executionParametersName;
    @JsonProperty("work_flow_name")
    private String workFlowName;
    @JsonProperty("work_flow_version")
    private String workFlowVersion;
    @JsonProperty("rule_enable")
    private Boolean ruleEnable;
    @JsonProperty("union_way")
    private Integer unionWay;
    private int ruleNo;

    @JsonProperty("alert")
    private Boolean alert;
    @JsonProperty("alert_level")
    private Integer alertLevel;
    @JsonProperty("alert_receiver")
    private String alertReceiver;
    @JsonProperty("specify_static_startup_param")
    private Boolean specifyStaticStartupParam;
    @JsonProperty("static_startup_param")
    private String staticStartupParam;
    @JsonProperty("abort_on_failure")
    private Boolean abortOnFailure;
    @JsonProperty("delete_fail_check_result")
    private Boolean deleteFailCheckResult;
    @JsonProperty("cluster")
    private String abnormalCluster;
    @JsonProperty("abnormal_database")
    private String abnormalDatabase;
    @JsonProperty("abnormal_proxy_user")
    private String abnormalProxyUser;
    @JsonProperty("upload_rule_metric_value")
    private Boolean uploadRuleMetricValue;
    @JsonProperty("upload_abnormal_value")
    private Boolean uploadAbnormalValue;

    @JsonProperty("work_flow_space")
    private String workFlowSpace;
    @JsonProperty("work_flow_project")
    private String workFlowProject;
    @JsonProperty("node_name")
    private String nodeName;

    @JsonProperty("verification_successful")
    private String verificationSuccessful;
    @JsonProperty("verification_failed")
    private String verificationFailed;
    @JsonProperty("execution_completed")
    private String executionCompleted;

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

    public Integer getUnionWay() {
        return unionWay;
    }

    public void setUnionWay(Integer unionWay) {
        this.unionWay = unionWay;
    }

    public Boolean getAlarm() {
        return alarm;
    }

    public void setAlarm(Boolean alarm) {
        this.alarm = alarm;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public Long getNewRuleGroupId() {
        return newRuleGroupId;
    }

    public void setNewRuleGroupId(Long newRuleGroupId) {
        this.newRuleGroupId = newRuleGroupId;
    }

    public String getCsId() {
        return csId;
    }

    public void setCsId(String csId) {
        this.csId = csId;
    }

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public void setRuleGroupName(String ruleGroupName) {
        this.ruleGroupName = ruleGroupName;
    }

    public List<DataSourceRequest> getDatasource() {
        return datasource;
    }

    public void setDatasource(List<DataSourceRequest> datasource) {
        this.datasource = datasource;
    }

    public String getBashContent() {
        return bashContent;
    }

    public void setBashContent(String bashContent) {
        this.bashContent = bashContent;
    }

    public List<String> getRuleMetricNamesForBdpClient() {
        return ruleMetricNamesForBdpClient;
    }

    public void setRuleMetricNamesForBdpClient(List<String> ruleMetricNamesForBdpClient) {
        this.ruleMetricNamesForBdpClient = ruleMetricNamesForBdpClient;
    }

    public String getExecutionParametersName() {
        return executionParametersName;
    }

    public void setExecutionParametersName(String executionParametersName) {
        this.executionParametersName = executionParametersName;
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

    public Boolean getRuleEnable() {
        return ruleEnable;
    }

    public void setRuleEnable(Boolean ruleEnable) {
        this.ruleEnable = ruleEnable;
    }

    public int getRuleNo() {
        return ruleNo;
    }

    public void setRuleNo(int ruleNo) {
        this.ruleNo = ruleNo;
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

    public Boolean getAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(Boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
    }

    public Boolean getDeleteFailCheckResult() {
        return deleteFailCheckResult;
    }

    public void setDeleteFailCheckResult(Boolean deleteFailCheckResult) {
        this.deleteFailCheckResult = deleteFailCheckResult;
    }

    public String getAbnormalCluster() {
        return abnormalCluster;
    }

    public void setAbnormalCluster(String abnormalCluster) {
        this.abnormalCluster = abnormalCluster;
    }

    public String getAbnormalDatabase() {
        return abnormalDatabase;
    }

    public void setAbnormalDatabase(String abnormalDatabase) {
        this.abnormalDatabase = abnormalDatabase;
    }

    public String getAbnormalProxyUser() {
        return abnormalProxyUser;
    }

    public void setAbnormalProxyUser(String abnormalProxyUser) {
        this.abnormalProxyUser = abnormalProxyUser;
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

    public Long getRuleTemplateId() {
        return ruleTemplateId;
    }

    public void setRuleTemplateId(Long ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
    }

    public String getWorkFlowSpace() {
        return workFlowSpace;
    }

    public void setWorkFlowSpace(String workFlowSpace) {
        this.workFlowSpace = workFlowSpace;
    }

    public String getWorkFlowProject() {
        return workFlowProject;
    }

    public void setWorkFlowProject(String workFlowProject) {
        this.workFlowProject = workFlowProject;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getVerificationSuccessful() {
        return verificationSuccessful;
    }

    public void setVerificationSuccessful(String verificationSuccessful) {
        this.verificationSuccessful = verificationSuccessful;
    }

    public String getVerificationFailed() {
        return verificationFailed;
    }

    public void setVerificationFailed(String verificationFailed) {
        this.verificationFailed = verificationFailed;
    }

    public String getExecutionCompleted() {
        return executionCompleted;
    }

    public void setExecutionCompleted(String executionCompleted) {
        this.executionCompleted = executionCompleted;
    }

    @Override
    public String toString() {
        return "AbstractCommonRequest{" +
                "ruleTemplateId=" + ruleTemplateId +
                ", ruleId=" + ruleId +
                ", ruleName='" + ruleName + '\'' +
                ", ruleDetail='" + ruleDetail + '\'' +
                ", ruleType=" + ruleType +
                ", ruleCnName='" + ruleCnName + '\'' +
                ", alarm=" + alarm +
                ", projectId=" + projectId +
                ", ruleGroupId=" + ruleGroupId +
                ", csId='" + csId + '\'' +
                ", ruleGroupName='" + ruleGroupName + '\'' +
                ", datasource=" + datasource +
                ", bashContent='" + bashContent + '\'' +
                ", ruleMetricNamesForBdpClient=" + ruleMetricNamesForBdpClient +
                ", executionParametersName='" + executionParametersName + '\'' +
                ", workFlowName='" + workFlowName + '\'' +
                ", workFlowVersion='" + workFlowVersion + '\'' +
                ", ruleEnable=" + ruleEnable +
                ", unionWay=" + unionWay +
                ", ruleNo=" + ruleNo +
                ", alert=" + alert +
                ", alertLevel=" + alertLevel +
                ", alertReceiver='" + alertReceiver + '\'' +
                ", specifyStaticStartupParam=" + specifyStaticStartupParam +
                ", staticStartupParam='" + staticStartupParam + '\'' +
                ", abortOnFailure=" + abortOnFailure +
                ", deleteFailCheckResult=" + deleteFailCheckResult +
                ", abnormalCluster='" + abnormalCluster + '\'' +
                ", abnormalDatabase='" + abnormalDatabase + '\'' +
                ", abnormalProxyUser='" + abnormalProxyUser + '\'' +
                ", uploadRuleMetricValue=" + uploadRuleMetricValue +
                ", uploadAbnormalValue=" + uploadAbnormalValue +
                ", workFlowSpace='" + workFlowSpace + '\'' +
                ", workFlowProject='" + workFlowProject + '\'' +
                ", nodeName='" + nodeName + '\'' +
                ", verificationSuccessful='" + verificationSuccessful + '\'' +
                ", verificationFailed='" + verificationFailed + '\'' +
                ", executionCompleted='" + executionCompleted + '\'' +
                '}';
    }
}
