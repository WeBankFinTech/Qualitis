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

package com.webank.wedatasphere.qualitis.project.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import org.springframework.beans.BeanUtils;

/**
 * @author howeye
 */
public class ExcelTemplateRuleByProject extends BaseRowModel {

    @ExcelProperty(value = "Project Name", index = 0)
    private String projectName;

    @ExcelProperty(value = "Rule Group Name", index = 1)
    private String ruleGroupName;

    @ExcelProperty(value = "Rule Name", index = 2)
    private String ruleName;

    @ExcelProperty(value = "Rule Chinese Name", index = 3)
    private String ruleCnName;

    @ExcelProperty(value = "Template Name", index = 4)
    private String templateName;

    @ExcelProperty(value = "Cluster", index = 5)
    private String cluster;

    @ExcelProperty(value = "Proxy User", index = 6)
    private String proxyUser;

    @ExcelProperty(value = "Data Source ID", index = 7)
    private String linkisDataSourceId;

    @ExcelProperty(value = "Data Source Name", index = 8)
    private String linkisDataSourceName;

    @ExcelProperty(value = "Data Source Type", index = 9)
    private String linkisDataSourceType;

    @ExcelProperty(value = "Database", index = 10)
    private String dbName;

    @ExcelProperty(value = "Table", index = 11)
    private String tableName;

    @ExcelProperty(value = "Column", index = 12)
    private String columnNames;

    @ExcelProperty(value = "Filter", index = 13)
    private String filter;

    @ExcelProperty(value = "Argument Key", index = 14)
    private String argumentKey;

    @ExcelProperty(value = "Argument Value", index = 15)
    private String argumentValue;

    @ExcelProperty(value = "Verification Check", index = 16)
    private String alarmCheckName;

    @ExcelProperty(value = "Verification Template", index = 17)
    private String checkTemplateName;

    @ExcelProperty(value = "Verification Compare Type", index = 18)
    private String compareType;

    @ExcelProperty(value = "Verification Rule Threshold", index = 19)
    private String threshold;

    @ExcelProperty(value = "AbortOnFailure", index = 20)
    private Boolean abortOnFailure;

    @ExcelProperty(value = "Create User", index = 21)
    private String createUser;
    @ExcelProperty(value = "Create Time", index = 22)
    private String createTime;
    @ExcelProperty(value = "Modify User", index = 23)
    private String modifyUser;
    @ExcelProperty(value = "Modify Time", index = 24)
    private String modifyTime;

    @ExcelProperty(value = "Rule Metric Code", index = 25)
    private String ruleMetricEnCode;
    @ExcelProperty(value = "Rule Metric Name", index = 26)
    private String ruleMetricName;
    @ExcelProperty(value = "delete_fail_check_result", index = 27)
    private Boolean deleteFailCheckResult;
    @ExcelProperty(value = "Upload Rule Metric Value", index = 28)
    private Boolean uploadRuleMetricValue;
    @ExcelProperty(value = "Upload Abnormal Value", index = 29)
    private Boolean uploadAbnormalValue;

    @ExcelProperty(value = "Static Startup Param", index = 30)
    private String staticStartupParam;
    @ExcelProperty(value = "Specify Static Startup Param", index = 31)
    private Boolean specifyStaticStartupParam;

    @ExcelProperty(value = "Rule Detail", index = 32)
    private String ruleDetail;
    @ExcelProperty(value = "Black Column Name", index = 33)
    private Boolean blackColName;

    public ExcelTemplateRuleByProject() {
        // Default Constructor
    }

    public ExcelTemplateRuleByProject(String ruleName) {
        this.ruleName = ruleName;
    }

    public ExcelTemplateRuleByProject(ExcelTemplateRuleByProject excelTemplateRuleByProject) {
        BeanUtils.copyProperties(excelTemplateRuleByProject, this);
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public void setRuleGroupName(String ruleGroupName) {
        this.ruleGroupName = ruleGroupName;
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

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getLinkisDataSourceId() {
        return linkisDataSourceId;
    }

    public void setLinkisDataSourceId(String linkisDataSourceId) {
        this.linkisDataSourceId = linkisDataSourceId;
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

    public String getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String columnNames) {
        this.columnNames = columnNames;
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

    public Boolean getAbortOnFailure() {
        return abortOnFailure;
    }

    public void setAbortOnFailure(Boolean abortOnFailure) {
        this.abortOnFailure = abortOnFailure;
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

    public String getStaticStartupParam() {
        return staticStartupParam;
    }

    public void setStaticStartupParam(String staticStartupParam) {
        this.staticStartupParam = staticStartupParam;
    }

    public Boolean getSpecifyStaticStartupParam() {
        return specifyStaticStartupParam;
    }

    public void setSpecifyStaticStartupParam(Boolean specifyStaticStartupParam) {
        this.specifyStaticStartupParam = specifyStaticStartupParam;
    }

    public String getRuleDetail() {
        return ruleDetail;
    }

    public void setRuleDetail(String ruleDetail) {
        this.ruleDetail = ruleDetail;
    }

    public Boolean getBlackColName() {
        return blackColName;
    }

    public void setBlackColName(Boolean blackColName) {
        this.blackColName = blackColName;
    }

    @Override
    public String toString() {
        return "ExcelTemplateRuleByProject{" +
            "projectName='" + projectName + '\'' +
            ", ruleGroupName='" + ruleGroupName + '\'' +
            ", ruleName='" + ruleName + '\'' +
            ", templateName='" + templateName + '\'' +
            ", cluster='" + cluster + '\'' +
            ", createUser='" + createUser + '\'' +
            ", createTime='" + createTime + '\'' +
            '}';
    }
}
