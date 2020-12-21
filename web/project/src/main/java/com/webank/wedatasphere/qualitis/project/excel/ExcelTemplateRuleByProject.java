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

/**
 * @author howeye
 */
public class ExcelTemplateRuleByProject extends BaseRowModel {

    @ExcelProperty(value = "Project", index = 0)
    private String projectName;

    @ExcelProperty(value = "Rule Group Name", index = 1)
    private String ruleGroupName;

    @ExcelProperty(value = "Rule Name", index = 2)
    private String ruleName;

    @ExcelProperty(value = "Template Name", index = 3)
    private String templateName;

    @ExcelProperty(value = "Cluster", index = 4)
    private String clusterName;

    @ExcelProperty(value = "Database", index = 5)
    private String dbName;

    @ExcelProperty(value = "Table", index = 6)
    private String tableName;

    @ExcelProperty(value = "Column", index = 7)
    private String colNames;

    @ExcelProperty(value = "Filter", index = 8)
    private String filter;

    @ExcelProperty(value = "Argument Key", index = 9)
    private String argumentKey;

    @ExcelProperty(value = "Argument Value", index = 10)
    private String argumentValue;

    @ExcelProperty(value = "Verification Check Column", index = 11)
    private String alarmCheckName;

    @ExcelProperty(value = "Verification Template", index = 12)
    private String checkTemplateName;

    @ExcelProperty(value = "Verification Compare Type", index = 13)
    private String compareType;

    @ExcelProperty(value = "Verification Threshold", index = 14)
    private String threshold;

    @ExcelProperty(value = "AbortOnFailure", index = 15)
    private Boolean abortOnFailure;

    public ExcelTemplateRuleByProject() {
        // Default Constructor
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    @Override
    public String toString() {
        return "ExcelTemplateRuleByProject{" +
            "projectName='" + projectName + '\'' +
            ", ruleGroupName='" + ruleGroupName + '\'' +
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
            '}';
    }
}
