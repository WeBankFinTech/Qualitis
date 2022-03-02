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
public class ExcelMultiTemplateRule extends BaseRowModel {

    @ExcelProperty(value = "Rule Group Name", index = 0)
    private String ruleGroupName;

    @ExcelProperty(value = "Rule Name", index = 1)
    private String ruleName;

    @ExcelProperty(value = "Template Name", index = 2)
    private String templateName;

    @ExcelProperty(value = "Cluster", index = 3)
    private String clusterName;

    @ExcelProperty(value = "Left Database", index = 4)
    private String leftDbName;

    @ExcelProperty(value = "Left Table", index = 5)
    private String leftTableName;

    @ExcelProperty(value = "Left Filter", index = 6)
    private String leftFilter;

    @ExcelProperty(value = "Right Database", index = 7)
    private String rightDbName;

    @ExcelProperty(value = "Right Table", index = 8)
    private String rightTableName;

    @ExcelProperty(value = "Right Filter", index = 9)
    private String rightFilter;

    @ExcelProperty(value = "Join Left Expression", index = 10)
    private String leftMappingStatement;

    @ExcelProperty(value = "Join Operation", index = 11)
    private String mappingOperation;

    @ExcelProperty(value = "Join Right Expression", index = 12)
    private String rightMappingStatement;

    @ExcelProperty(value = "Filter in Result Table", index = 13)
    private String whereFilter;

    @ExcelProperty(value = "Verification Check Column", index = 14)
    private String alarmCheckName;

    @ExcelProperty(value = "Verification Template Name", index = 15)
    private String checkTemplateName;

    @ExcelProperty(value = "Verification Compare Type", index = 16)
    private String compareType;

    @ExcelProperty(value = "Verification Threshold", index = 17)
    private String threshold;

    @ExcelProperty(value = "AbortOnFailure", index = 18)
    private Boolean abortOnFailure;

    @ExcelProperty(value = "Alert", index = 19)
    private Boolean alert;
    @ExcelProperty(value = "Alert Level", index = 20)
    private String alertLevel;
    @ExcelProperty(value = "Alert Receivers", index = 21)
    private String alertReceivers;

    @ExcelProperty(value = "Create User", index = 22)
    private String createUser;
    @ExcelProperty(value = "Create Time", index = 23)
    private String createTime;
    @ExcelProperty(value = "Modify User", index = 24)
    private String modifyUser;
    @ExcelProperty(value = "Modify Time", index = 25)
    private String modifyTime;

    @ExcelProperty(value = "Left Fps File Id", index = 26)
    private String leftFileId;
    @ExcelProperty(value = "Left Fps File Desc", index = 27)
    private String leftFileTableDesc;
    @ExcelProperty(value = "Left File Delimiter", index = 28)
    private String leftFileDelimiter;
    @ExcelProperty(value = "Left Fps File Type", index = 29)
    private String leftFileType;
    @ExcelProperty(value = "Left File Header", index = 30)
    private Boolean leftFileHeader;
    @ExcelProperty(value = "Left Fps File Hash Value", index = 31)
    private String leftFpsHashValue;
    @ExcelProperty(value = "Right Fps File Id", index = 32)
    private String rightFileId;
    @ExcelProperty(value = "Right Fps File Desc", index = 33)
    private String rightFileTableDesc;
    @ExcelProperty(value = "Right File Delimiter", index = 34)
    private String rightFileDelimiter;
    @ExcelProperty(value = "Right Fps File Type", index = 35)
    private String rightFileType;
    @ExcelProperty(value = "Right File Header", index = 36)
    private Boolean rightFileHeader;
    @ExcelProperty(value = "Right Fps File Hash Value", index = 37)
    private String rightFpsHashValue;

    @ExcelProperty(value = "Left Proxy User", index = 38)
    private String leftProxyUser;
    @ExcelProperty(value = "Right Proxy User", index = 39)
    private String rightProxyUser;

    @ExcelProperty(value = "Join Left Names", index = 40)
    private String leftMappingNames;
    @ExcelProperty(value = "Join Left Types", index = 41)
    private String leftMappingTypes;
    @ExcelProperty(value = "Join Right Names", index = 42)
    private String rightMappingNames;
    @ExcelProperty(value = "Join Right Types", index = 43)
    private String rightMappingTypes;
    @ExcelProperty(value = "Rule Metric En Code", index = 44)
    private String ruleMetricEnCode;
    @ExcelProperty(value = "Rule Metric Name", index = 45)
    private String ruleMetricName;
    @ExcelProperty(value = "delete_fail_check_result", index = 46)
    private Boolean deleteFailCheckResult;
    @ExcelProperty(value = "Specify Static Startup Param", index = 47)
    private Boolean specifyStaticStartupParam;
    @ExcelProperty(value = "Static Startup Param", index = 48)
    private String staticStartupParam;
    @ExcelProperty(value = "Upload Rule Metric Value", index = 49)
    private Boolean uploadRuleMetricValue;
    @ExcelProperty(value = "Upload Abnormal Value", index = 50)
    private Boolean uploadAbnormalValue;

    public ExcelMultiTemplateRule() {
    }

    public ExcelMultiTemplateRule(ExcelMultiTemplateRule excelMultiTemplateRule) {
        BeanUtils.copyProperties(excelMultiTemplateRule, this);
    }

    public String getLeftMappingNames() {
        return leftMappingNames;
    }

    public void setLeftMappingNames(String leftMappingNames) {
        this.leftMappingNames = leftMappingNames;
    }

    public String getLeftMappingTypes() {
        return leftMappingTypes;
    }

    public void setLeftMappingTypes(String leftMappingTypes) {
        this.leftMappingTypes = leftMappingTypes;
    }

    public String getRightMappingNames() {
        return rightMappingNames;
    }

    public void setRightMappingNames(String rightMappingNames) {
        this.rightMappingNames = rightMappingNames;
    }

    public String getRightMappingTypes() {
        return rightMappingTypes;
    }

    public void setRightMappingTypes(String rightMappingTypes) {
        this.rightMappingTypes = rightMappingTypes;
    }

    public String getLeftProxyUser() {
        return leftProxyUser;
    }

    public void setLeftProxyUser(String leftProxyUser) {
        this.leftProxyUser = leftProxyUser;
    }

    public String getRightProxyUser() {
        return rightProxyUser;
    }

    public void setRightProxyUser(String rightProxyUser) {
        this.rightProxyUser = rightProxyUser;
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

    public String getLeftDbName() {
        return leftDbName;
    }

    public void setLeftDbName(String leftDbName) {
        this.leftDbName = leftDbName;
    }

    public String getLeftTableName() {
        return leftTableName;
    }

    public void setLeftTableName(String leftTableName) {
        this.leftTableName = leftTableName;
    }

    public String getLeftFilter() {
        return leftFilter;
    }

    public void setLeftFilter(String leftFilter) {
        this.leftFilter = leftFilter;
    }

    public String getRightDbName() {
        return rightDbName;
    }

    public void setRightDbName(String rightDbName) {
        this.rightDbName = rightDbName;
    }

    public String getRightTableName() {
        return rightTableName;
    }

    public void setRightTableName(String rightTableName) {
        this.rightTableName = rightTableName;
    }

    public String getRightFilter() {
        return rightFilter;
    }

    public void setRightFilter(String rightFilter) {
        this.rightFilter = rightFilter;
    }

    public String getLeftMappingStatement() {
        return leftMappingStatement;
    }

    public void setLeftMappingStatement(String leftMappingStatement) {
        this.leftMappingStatement = leftMappingStatement;
    }

    public String getMappingOperation() {
        return mappingOperation;
    }

    public void setMappingOperation(String mappingOperation) {
        this.mappingOperation = mappingOperation;
    }

    public String getRightMappingStatement() {
        return rightMappingStatement;
    }

    public void setRightMappingStatement(String rightMappingStatement) {
        this.rightMappingStatement = rightMappingStatement;
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

    public String getWhereFilter() {
        return whereFilter;
    }

    public void setWhereFilter(String whereFilter) {
        this.whereFilter = whereFilter;
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

    public String getLeftFileId() {
        return leftFileId;
    }

    public void setLeftFileId(String leftFileId) {
        this.leftFileId = leftFileId;
    }

    public String getLeftFileTableDesc() {
        return leftFileTableDesc;
    }

    public void setLeftFileTableDesc(String leftFileTableDesc) {
        this.leftFileTableDesc = leftFileTableDesc;
    }

    public String getLeftFileDelimiter() {
        return leftFileDelimiter;
    }

    public void setLeftFileDelimiter(String leftFileDelimiter) {
        this.leftFileDelimiter = leftFileDelimiter;
    }

    public String getLeftFileType() {
        return leftFileType;
    }

    public void setLeftFileType(String leftFileType) {
        this.leftFileType = leftFileType;
    }

    public Boolean getLeftFileHeader() {
        return leftFileHeader;
    }

    public void setLeftFileHeader(Boolean leftFileHeader) {
        this.leftFileHeader = leftFileHeader;
    }

    public String getLeftFpsHashValue() {
        return leftFpsHashValue;
    }

    public void setLeftFpsHashValue(String leftFpsHashValue) {
        this.leftFpsHashValue = leftFpsHashValue;
    }

    public String getRightFileId() {
        return rightFileId;
    }

    public void setRightFileId(String rightFileId) {
        this.rightFileId = rightFileId;
    }

    public String getRightFileTableDesc() {
        return rightFileTableDesc;
    }

    public void setRightFileTableDesc(String rightFileTableDesc) {
        this.rightFileTableDesc = rightFileTableDesc;
    }

    public String getRightFileDelimiter() {
        return rightFileDelimiter;
    }

    public void setRightFileDelimiter(String rightFileDelimiter) {
        this.rightFileDelimiter = rightFileDelimiter;
    }

    public String getRightFileType() {
        return rightFileType;
    }

    public void setRightFileType(String rightFileType) {
        this.rightFileType = rightFileType;
    }

    public Boolean getRightFileHeader() {
        return rightFileHeader;
    }

    public void setRightFileHeader(Boolean rightFileHeader) {
        this.rightFileHeader = rightFileHeader;
    }

    public String getRightFpsHashValue() {
        return rightFpsHashValue;
    }

    public void setRightFpsHashValue(String rightFpsHashValue) {
        this.rightFpsHashValue = rightFpsHashValue;
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
        return "ExcelMultiTemplateRule{" +
            "ruleGroupName='" + ruleGroupName + '\'' +
            ", ruleName='" + ruleName + '\'' +
            ", templateName='" + templateName + '\'' +
            ", clusterName='" + clusterName + '\'' +
            ", leftDbName='" + leftDbName + '\'' +
            ", leftTableName='" + leftTableName + '\'' +
            ", leftFilter='" + leftFilter + '\'' +
            ", rightDbName='" + rightDbName + '\'' +
            ", rightTableName='" + rightTableName + '\'' +
            ", rightFilter='" + rightFilter + '\'' +
            ", leftMappingStatement='" + leftMappingStatement + '\'' +
            ", mappingOperation='" + mappingOperation + '\'' +
            ", rightMappingStatement='" + rightMappingStatement + '\'' +
            ", whereFilter='" + whereFilter + '\'' +
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
            ", leftFileId='" + leftFileId + '\'' +
            ", leftFileTableDesc='" + leftFileTableDesc + '\'' +
            ", leftFileDelimiter='" + leftFileDelimiter + '\'' +
            ", leftFileType='" + leftFileType + '\'' +
            ", leftFileHeader=" + leftFileHeader +
            ", leftFpsHashValue='" + leftFpsHashValue + '\'' +
            ", rightFileId='" + rightFileId + '\'' +
            ", rightFileTableDesc='" + rightFileTableDesc + '\'' +
            ", rightFileDelimiter='" + rightFileDelimiter + '\'' +
            ", rightFileType='" + rightFileType + '\'' +
            ", rightFileHeader=" + rightFileHeader +
            ", rightFpsHashValue='" + rightFpsHashValue + '\'' +
            ", leftProxyUser='" + leftProxyUser + '\'' +
            ", rightProxyUser='" + rightProxyUser + '\'' +
            ", leftMappingNames='" + leftMappingNames + '\'' +
            ", leftMappingTypes='" + leftMappingTypes + '\'' +
            ", rightMappingNames='" + rightMappingNames + '\'' +
            ", rightMappingTypes='" + rightMappingTypes + '\'' +
            ", ruleMetricId='" + ruleMetricEnCode + '\'' +
            ", ruleMetricName='" + ruleMetricName + '\'' +
            ", deleteFailCheckResult=" + deleteFailCheckResult +
            ", specifyStaticStartupParam=" + specifyStaticStartupParam +
            ", staticStartupParam='" + staticStartupParam + '\'' +
            ", uploadRuleMetricValue=" + uploadRuleMetricValue +
            ", uploadAbnormalValue=" + uploadAbnormalValue +
            '}';
    }
}
