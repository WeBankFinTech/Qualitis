package com.webank.wedatasphere.qualitis.request;

import static com.webank.wedatasphere.qualitis.project.request.CommonChecker.checkObject;
import static com.webank.wedatasphere.qualitis.project.request.CommonChecker.checkString;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import java.util.List;

/**
 * @author allenzhou
 */
public class MulDbRequest {
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("source_db")
    private String sourceDb;
    @JsonProperty("source_linkis_datasoure_id")
    private Long sourceLinkisDataSourceId;
    @JsonProperty("source_linkis_datasource_name")
    private String sourceLinkisDataSourceName;
    @JsonProperty("source_linkis_datasource_type")
    private String sourceLinkisDataSourceType;

    @JsonProperty("target_db")
    private String targetDb;
    @JsonProperty("target_linkis_datasoure_id")
    private Long targetLinkisDataSourceId;
    @JsonProperty("target_linkis_datasource_name")
    private String targetLinkisDataSourceName;
    @JsonProperty("target_linkis_datasource_type")
    private String targetLinkisDataSourceType;
    @JsonProperty("alert")
    private Boolean alert;
    @JsonProperty("alert_level")
    private Integer alertLevel;
    @JsonProperty("alert_receiver")
    private String alertReceiver;
    @JsonProperty("abort_on_failure")
    private Boolean abortOnFailure;
    @JsonProperty("proxy_user")
    private String proxyUser;

    @JsonProperty("white_list")
    private List<String> whiteList;
    @JsonProperty("black_list")
    private List<String> blackList;
    @JsonProperty("filter_list")
    private List<FilterRequest> filterRequests;

    @JsonProperty("specify_static_startup_param")
    private Boolean specifyStaticStartupParam;
    @JsonProperty("static_startup_param")
    private String staticStartupParam;

    public MulDbRequest() {
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getSourceDb() {
        return sourceDb;
    }

    public void setSourceDb(String sourceDb) {
        this.sourceDb = sourceDb;
    }

    public String getTargetDb() {
        return targetDb;
    }

    public void setTargetDb(String targetDb) {
        this.targetDb = targetDb;
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

    public Boolean getAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(Boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public List<String> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }

    public List<String> getBlackList() {
        return blackList;
    }

    public void setBlackList(List<String> blackList) {
        this.blackList = blackList;
    }

    public List<FilterRequest> getFilterRequests() {
        return filterRequests;
    }

    public void setFilterRequests(List<FilterRequest> filterRequests) {
        this.filterRequests = filterRequests;
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

    public Long getSourceLinkisDataSourceId() {
        return sourceLinkisDataSourceId;
    }

    public void setSourceLinkisDataSourceId(Long sourceLinkisDataSourceId) {
        this.sourceLinkisDataSourceId = sourceLinkisDataSourceId;
    }

    public String getSourceLinkisDataSourceName() {
        return sourceLinkisDataSourceName;
    }

    public void setSourceLinkisDataSourceName(String sourceLinkisDataSourceName) {
        this.sourceLinkisDataSourceName = sourceLinkisDataSourceName;
    }

    public String getSourceLinkisDataSourceType() {
        return sourceLinkisDataSourceType;
    }

    public void setSourceLinkisDataSourceType(String sourceLinkisDataSourceType) {
        this.sourceLinkisDataSourceType = sourceLinkisDataSourceType;
    }

    public Long getTargetLinkisDataSourceId() {
        return targetLinkisDataSourceId;
    }

    public void setTargetLinkisDataSourceId(Long targetLinkisDataSourceId) {
        this.targetLinkisDataSourceId = targetLinkisDataSourceId;
    }

    public String getTargetLinkisDataSourceName() {
        return targetLinkisDataSourceName;
    }

    public void setTargetLinkisDataSourceName(String targetLinkisDataSourceName) {
        this.targetLinkisDataSourceName = targetLinkisDataSourceName;
    }

    public String getTargetLinkisDataSourceType() {
        return targetLinkisDataSourceType;
    }

    public void setTargetLinkisDataSourceType(String targetLinkisDataSourceType) {
        this.targetLinkisDataSourceType = targetLinkisDataSourceType;
    }

    @Override
    public String toString() {
        return "MulDbRequest{" +
            "ruleName='" + ruleName + '\'' +
            ", clusterName='" + clusterName + '\'' +
            ", projectId=" + projectId +
            ", sourceDb='" + sourceDb + '\'' +
            ", targetDb='" + targetDb + '\'' +
            ", alert=" + alert +
            ", alertLevel=" + alertLevel +
            ", alertReceiver='" + alertReceiver + '\'' +
            ", abortOnFailure=" + abortOnFailure +
            ", proxyUser='" + proxyUser + '\'' +
            ", specifyStaticStartupParam=" + specifyStaticStartupParam +
            ", staticStartupParam='" + staticStartupParam + '\'' +
            '}';
    }

    public static void checkRequst(MulDbRequest request) throws UnExpectedRequestException {
        checkObject(request.getProjectId(), "project ID");
        checkString(request.getProjectId().toString(), "project ID string");
        checkString(request.getClusterName(), "cluster name");
        checkString(request.getSourceDb(), "source db name");
        checkString(request.getTargetDb(), "target db name");

    }
}
