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

    public ExcelMultiTemplateRule() {
    }

    public ExcelMultiTemplateRule(ExcelMultiTemplateRule excelMultiTemplateRule) {
        BeanUtils.copyProperties(excelMultiTemplateRule, this);
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
            '}';
    }
}
