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

package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 * @author howeye
 */
public class AddCustomRuleRequest extends AbstractAddRequest {
    @JsonProperty("project_id")
    private Long projectId;
    @JsonProperty("rule_name")
    private String ruleName;
    @JsonProperty("cn_name")
    private String ruleCnName;
    @JsonProperty("rule_detail")
    private String ruleDetail;
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
    @JsonProperty("alarm")
    private Boolean alarm;
    @JsonProperty("alert")
    private Boolean alert;
    @JsonProperty("alert_level")
    private Integer alertLevel;
    @JsonProperty("alert_receiver")
    private String alertReceiver;
    @JsonProperty("alarm_variable")
    private List<CustomAlarmConfigRequest> alarmVariable;
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("proxy_user")
    private String proxyUser;
    @JsonProperty("rule_group_id")
    private Long ruleGroupId;

    @JsonProperty("cs_id")
    private String csId;
    @JsonProperty("abort_on_failure")
    private Boolean abortOnFailure;

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
    @JsonProperty("file_hash_values")
    private String fileHashValues;

    @JsonProperty("delete_fail_check_result")
    private Boolean deleteFailCheckResult;

    @JsonProperty("sql_check_area")
    private String sqlCheckArea;


    @JsonProperty("specify_static_startup_param")
    private Boolean specifyStaticStartupParam;
    @JsonProperty("static_startup_param")
    private String staticStartupParam;

    @JsonProperty("linkis_datasoure_id")
    private Long linkisDataSourceId;
    @JsonProperty("linkis_datasoure_version_id")
    private Long linkisDataSourceVersionId;
    @JsonProperty("linkis_datasource_name")
    private String linkisDataSourceName;
    @JsonProperty("linkis_datasource_type")
    private String linkisDataSourceType;

    private List<String> ruleMetricNamesForBdpClient;

    public AddCustomRuleRequest() {
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

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
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

    public List<CustomAlarmConfigRequest> getAlarmVariable() {
        return alarmVariable;
    }

    public void setAlarmVariable(List<CustomAlarmConfigRequest> alarmVariable) {
        this.alarmVariable = alarmVariable;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public String getCsId() { return csId; }

    public void setCsId(String csId) { this.csId = csId; }

    public Boolean getAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(Boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
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

    public String getFileDb() {
        return fileDb;
    }

    public void setFileDb(String fileDb) {
        this.fileDb = fileDb;
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

    public Boolean getFileHeader() {
        return fileHeader;
    }

    public void setFileHeader(Boolean fileHeader) {
        this.fileHeader = fileHeader;
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

    public String getSqlCheckArea() {
        return sqlCheckArea;
    }

    public void setSqlCheckArea(String sqlCheckArea) {
        this.sqlCheckArea = sqlCheckArea;
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

    public List<String> getRuleMetricNamesForBdpClient() {
        return ruleMetricNamesForBdpClient;
    }

    public void setRuleMetricNamesForBdpClient(List<String> ruleMetricNamesForBdpClient) {
        this.ruleMetricNamesForBdpClient = ruleMetricNamesForBdpClient;
    }

    public static void checkRequest(AddCustomRuleRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        CommonChecker.checkObject(request.getProjectId(), "Project id");
        AddCustomRuleRequest.checkCustomRuleRequest(request);
    }

    public static void checkCustomRuleRequest(AddCustomRuleRequest request) throws UnExpectedRequestException {
        CommonChecker.checkString(request.getRuleName(), "Rule name");
        CommonChecker.checkString(request.getClusterName(), "Cluster name");
        CommonChecker.checkObject(request.getSaveMidTable(), "Save mid table");

        CommonChecker.checkObject(request.getAlarm(), "alarm");
        CommonChecker.checkObject(request.getAbortOnFailure(), "abort on failure");
        if (request.getAlarm()) {
            CommonChecker.checkObject(request.getAlarmVariable(), "alarm variable");
            if (request.getAlarmVariable().size() == 0) {
                throw new UnExpectedRequestException("alarm_variable can not be empty");
            }

            for (CustomAlarmConfigRequest customAlarmConfigRequest : request.getAlarmVariable()) {
                CustomAlarmConfigRequest.checkRequest(customAlarmConfigRequest);
            }
        }
        if (StringUtils.isNotBlank(request.getSqlCheckArea())) {
            return;
        }

        CommonChecker.checkString(request.getOutputName(), "Output name");

        CommonChecker.checkFunctionTypeEnum(request.getFunctionType());
        CommonChecker.checkString(request.getFunctionContent(), "Function content");
        CommonChecker.checkString(request.getWhereContent(), "Where content");
        CommonChecker.checkString(request.getFromContent(), "From content");

        CommonChecker.checkStringLength(request.getRuleName(), 128, "Rule name");
        CommonChecker.checkStringLength(request.getOutputName(), 50, "Output name");
        CommonChecker.checkStringLength(request.getFromContent(), 1000, "From content");
        CommonChecker.checkStringLength(request.getWhereContent(), 1000, "Where content");
        CommonChecker.checkStringLength(request.getFunctionContent(), 1000, "Function content");
    }
}
