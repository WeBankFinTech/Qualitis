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
public class ExcelCustomRule extends BaseRowModel {

    @ExcelProperty(value = "Rule Group Name", index = 0)
    private String ruleGroupName;

    @ExcelProperty(value = "Rule Name", index = 1)
    private String ruleName;

    @ExcelProperty(value = "Check Column", index = 2)
    private String outputName;

    @ExcelProperty(value = "Save Not Pass Verification Data", index = 3)
    private Boolean saveMidTable;

    @ExcelProperty(value = "Cluster", index = 4)
    private String clusterName;

    @ExcelProperty(value = "Function", index = 5)
    private String functionName;

    @ExcelProperty(value = "Function Content", index = 6)
    private String functionContent;

    @ExcelProperty(value = "From Content", index = 7)
    private String fromContent;

    @ExcelProperty(value = "Where Content", index = 8)
    private String whereContent;

    @ExcelProperty(value = "Verification Check Column", index = 9)
    private String alarmCheckName;

    @ExcelProperty(value = "Verification Template", index = 10)
    private String checkTemplateName;

    @ExcelProperty(value = "Verification Compare Type", index = 11)
    private String compareType;

    @ExcelProperty(value = "Verification Threshold", index = 12)
    private String threshold;

    public ExcelCustomRule() {
    }

    public ExcelCustomRule(ExcelCustomRule excelTemplateRule) {
        BeanUtils.copyProperties(excelTemplateRule, this);
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
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

    public Boolean getSaveMidTable() {
        return saveMidTable;
    }

    public void setSaveMidTable(Boolean saveMidTable) {
        this.saveMidTable = saveMidTable;
    }

    public String getRuleGroupName() {
        return ruleGroupName;
    }

    public void setRuleGroupName(String ruleGroupName) {
        this.ruleGroupName = ruleGroupName;
    }

    @Override
    public String toString() {
        return "ExcelCustomRule{" +
                "ruleName='" + ruleName + '\'' +
                ", outputName='" + outputName + '\'' +
                ", saveMidTable=" + saveMidTable +
                ", clusterName='" + clusterName + '\'' +
                ", functionName='" + functionName + '\'' +
                ", functionContent='" + functionContent + '\'' +
                ", fromContent='" + fromContent + '\'' +
                ", whereContent='" + whereContent + '\'' +
                ", alarmCheckName='" + alarmCheckName + '\'' +
                ", checkTemplateName='" + checkTemplateName + '\'' +
                ", compareType='" + compareType + '\'' +
                ", threshold='" + threshold + '\'' +
                '}';
    }
}
