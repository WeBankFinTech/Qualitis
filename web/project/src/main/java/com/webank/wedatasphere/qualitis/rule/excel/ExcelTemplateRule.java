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

package com.webank.wedatasphere.qualitis.rule.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import org.springframework.beans.BeanUtils;

/**
 * @author howeye
 */
public class ExcelTemplateRule extends BaseRowModel {

    @ExcelProperty(value = "Rule Group Name", index = 0)
    private String ruleGroupName;

    @ExcelProperty(value = "Rule Name", index = 1)
    private String ruleName;

    @ExcelProperty(value = "Template Name", index = 2)
    private String templateName;

    @ExcelProperty(value = "Cluster", index = 3)
    private String clusterName;

    @ExcelProperty(value = "Database", index = 4)
    private String dbName;

    @ExcelProperty(value = "Table", index = 5)
    private String tableName;

    @ExcelProperty(value = "Column", index = 6)
    private String colNames;

    @ExcelProperty(value = "Filter", index = 7)
    private String filter;

    @ExcelProperty(value = "Argument Key", index = 8)
    private String argumentKey;

    @ExcelProperty(value = "Argument Value", index = 9)
    private String argumentValue;

    @ExcelProperty(value = "Verification Check Column", index = 10)
    private String alarmCheckName;

    @ExcelProperty(value = "Verification Template", index = 11)
    private String checkTemplateName;

    @ExcelProperty(value = "Verification Compare Type", index = 12)
    private String compareType;

    @ExcelProperty(value = "Verification Threshold", index = 13)
    private String threshold;

    @ExcelProperty(value = "Abort On Failure", index = 14)
    private Boolean abortOnFailure;

    @ExcelProperty(value = "Alert", index = 15)
    private Boolean alert;
    @ExcelProperty(value = "Alert Level", index = 16)
    private String alertLevel;
    @ExcelProperty(value = "Alert Receivers", index = 17)
    private String alertReceivers;

    @ExcelProperty(value = "Create User", index = 18)
    private String createUser;
    @ExcelProperty(value = "Create Time", index = 19)
    private String createTime;
    @ExcelProperty(value = "Modify User", index = 20)
    private String modifyUser;
    @ExcelProperty(value = "Modify Time", index = 21)
    private String modifyTime;

    @ExcelProperty(value = "Fps File Id", index = 22)
    private String fileId;
    @ExcelProperty(value = "Fps File Desc", index = 23)
    private String fileTableDesc;
    @ExcelProperty(value = "File Delimiter", index = 24)
    private String fileDelimiter;
    @ExcelProperty(value = "Fps File Type", index = 25)
    private String fileType;
    @ExcelProperty(value = "File Header", index = 26)
    private Boolean fileHeader;
    @ExcelProperty(value = "Proxy User", index = 27)
    private String proxyUser;
    @ExcelProperty(value = "Fps File Hash Value", index = 28)
    private String fpsHashValue;
    @ExcelProperty(value = "Rule Metric En Code", index = 29)
    private String ruleMetricEnCode;
    @ExcelProperty(value = "Rule Metric Name", index = 30)
    private String ruleMetricName;
    @ExcelProperty(value = "Delete Fail Check Result", index = 31)
    private Boolean deleteFailCheckResult;
    @ExcelProperty(value = "Specify Static Startup Param", index = 32)
    private Boolean specifyStaticStartupParam;
    @ExcelProperty(value = "Static Startup Param", index = 33)
    private String staticStartupParam;
    @ExcelProperty(value = "Upload Rule Metric Value", index = 34)
    private Boolean uploadRuleMetricValue;
    @ExcelProperty(value = "Upload Abnormal Value", index = 35)
    private Boolean uploadAbnormalValue;

    public ExcelTemplateRule() {
    }

    public ExcelTemplateRule(ExcelTemplateRule excelTemplateRule) {
        BeanUtils.copyProperties(excelTemplateRule, this);
    }

    public String getFpsHashValue() {
        return fpsHashValue;
    }

    public void setFpsHashValue(String fpsHashValue) {
        this.fpsHashValue = fpsHashValue;
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

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColNames() {
        return colNames;
    }

    public void setColNames(String colNames) {
        this.colNames = colNames;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getArgumentKey() {
        return argumentKey;
    }

    public void setArgumentKey(String argumentKey) {
        this.argumentKey = argumentKey;
    }

    public String getArgumentValue() {
        return argumentValue;
    }

    public void setArgumentValue(String argumentValue) {
        this.argumentValue = argumentValue;
    }

    public String getAlarmCheckName() {
        return alarmCheckName;
    }

    public void setAlarmCheckName(String alarmCheckName) {
        this.alarmCheckName = alarmCheckName;
    }

    public String getCheckTemplateName() {
        return checkTemplateName;
    }

    public void setCheckTemplateName(String checkTemplateName) {
        this.checkTemplateName = checkTemplateName;
    }

    public String getCompareType() {
        return compareType;
    }

    public void setCompareType(String compareType) {
        this.compareType = compareType;
    }

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public void setRuleGroupName(String ruleGroupName) {
        this.ruleGroupName = ruleGroupName;
    }

    public Boolean getAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(Boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
    }

    public Boolean getAlert() {
        return alert;
    }

    public void setAlert(Boolean alert) {
        this.alert = alert;
    }

    public String getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(String alertLevel) {
        this.alertLevel = alertLevel;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
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

    public Boolean getFileHeader() {
        return fileHeader;
    }

    public void setFileHeader(Boolean fileHeader) {
        this.fileHeader = fileHeader;
    }

    public String getAlertReceivers() {
        return alertReceivers;
    }

    public void setAlertReceivers(String alertReceivers) {
        this.alertReceivers = alertReceivers;
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

    public Boolean getDeleteFailCheckResult() {
        return deleteFailCheckResult;
    }

    public void setDeleteFailCheckResult(Boolean deleteFailCheckResult) {
        this.deleteFailCheckResult = deleteFailCheckResult;
    }

    public String getRuleMetricEnCode() {
        return ruleMetricEnCode;
    }

    public void setRuleMetricEnCode(String ruleMetricEnCode) {
        this.ruleMetricEnCode = ruleMetricEnCode;
    }

    public String getRuleMetricName() {
        return ruleMetricName;
    }

    public void setRuleMetricName(String ruleMetricName) {
        this.ruleMetricName = ruleMetricName;
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

    @Override
    public String toString() {
        return "ExcelTemplateRule{" +
            "ruleGroupName='" + ruleGroupName + '\'' +
            ", ruleName='" + ruleName + '\'' +
            ", templateName='" + templateName + '\'' +
            ", clusterName='" + clusterName + '\'' +
            ", dbName='" + dbName + '\'' +
            ", tableName='" + tableName + '\'' +
            ", colNames='" + colNames + '\'' +
            ", filter='" + filter + '\'' +
            ", argumentKey='" + argumentKey + '\'' +
            ", argumentValue='" + argumentValue + '\'' +
            ", alarmCheckName='" + alarmCheckName + '\'' +
            ", checkTemplateName='" + checkTemplateName + '\'' +
            ", compareType='" + compareType + '\'' +
            ", threshold='" + threshold + '\'' +
            ", abortOnFailure=" + abortOnFailure +
            ", alert=" + alert +
            ", alertLevel='" + alertLevel + '\'' +
            ", alertReceivers='" + alertReceivers + '\'' +
            ", createUser='" + createUser + '\'' +
            ", createTime='" + createTime + '\'' +
            ", modifyUser='" + modifyUser + '\'' +
            ", modifyTime='" + modifyTime + '\'' +
            ", fileId='" + fileId + '\'' +
            ", fileTableDesc='" + fileTableDesc + '\'' +
            ", fileDelimiter='" + fileDelimiter + '\'' +
            ", fileType='" + fileType + '\'' +
            ", fileHeader=" + fileHeader +
            ", proxyUser='" + proxyUser + '\'' +
            ", fpsHashValue='" + fpsHashValue + '\'' +
            ", ruleMetricId='" + ruleMetricEnCode + '\'' +
            ", ruleMetricName='" + ruleMetricName + '\'' +
            ", deleteFailCheckResult=" + deleteFailCheckResult +
            ", specifyStaticStartupParam=" + specifyStaticStartupParam +
            ", staticStartupParam='" + staticStartupParam + '\'' +
            ", uploadRuleMetricValue='" + uploadRuleMetricValue + '\'' +
            ", uploadAbnormalValue='" + uploadAbnormalValue + '\'' +
            '}';
    }
}
