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
public class ExcelMultiTemplateRuleByProject extends BaseRowModel {

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
    private String clusterName;

    @ExcelProperty(value = "Left Proxy User", index = 6)
    private String leftProxyUser;

    @ExcelProperty(value = "Left Data Source ID", index = 7)
    private String leftLinkisDataSourceId;

    @ExcelProperty(value = "Left Data Source Name", index = 8)
    private String leftLinkisDataSourceName;

    @ExcelProperty(value = "Left Data Source Type", index = 9)
    private String leftLinkisDataSourceType;

    @ExcelProperty(value = "Left Database", index = 10)
    private String leftDbName;

    @ExcelProperty(value = "Left Table", index = 11)
    private String leftTableName;

    @ExcelProperty(value = "Left Filter", index = 12)
    private String leftFilter;

    @ExcelProperty(value = "Right Proxy User", index = 13)
    private String rightProxyUser;

    @ExcelProperty(value = "Right Data Source ID", index = 14)
    private String rightLinkisDataSourceId;

    @ExcelProperty(value = "Right Data Source Name", index = 15)
    private String rightLlinkisDataSourceName;

    @ExcelProperty(value = "Right Data Source Type", index = 16)
    private String rightLlinkisDataSourceType;

    @ExcelProperty(value = "Right Database", index = 17)
    private String rightDbName;

    @ExcelProperty(value = "Right Table", index = 18)
    private String rightTableName;

    @ExcelProperty(value = "Right Filter", index = 19)
    private String rightFilter;

    @ExcelProperty(value = "Join Left Expression", index = 20)
    private String leftMappingStatement;

    @ExcelProperty(value = "Join Operation", index = 21)
    private String mappingOperation;

    @ExcelProperty(value = "Join Right Expression", index = 22)
    private String rightMappingStatement;

    @ExcelProperty(value = "Filter in Result Table", index = 23)
    private String whereFilter;

    @ExcelProperty(value = "Verification Check Column", index = 24)
    private String alarmCheckName;

    @ExcelProperty(value = "Verification Template Name", index = 25)
    private String checkTemplateName;

    @ExcelProperty(value = "Verification Compare Type", index = 26)
    private String compareType;

    @ExcelProperty(value = "Verification Threshold", index = 27)
    private String threshold;

    @ExcelProperty(value = "AbortOnFailure", index = 28)
    private Boolean abortOnFailure;

    @ExcelProperty(value = "Create User", index = 29)
    private String createUser;
    @ExcelProperty(value = "Create Time", index = 30)
    private String createTime;
    @ExcelProperty(value = "Modify User", index = 31)
    private String modifyUser;
    @ExcelProperty(value = "Modify Time", index = 32)
    private String modifyTime;


    @ExcelProperty(value = "Join Left Names", index = 33)
    private String leftMappingNames;
    @ExcelProperty(value = "Join Left Types", index = 34)
    private String leftMappingTypes;
    @ExcelProperty(value = "Join Right Names", index = 35)
    private String rightMappingNames;
    @ExcelProperty(value = "Join Right Types", index = 36)
    private String rightMappingTypes;
    @ExcelProperty(value = "Rule Metric En Code", index = 37)
    private String ruleMetricEnCode;
    @ExcelProperty(value = "Rule Metric Name", index = 38)
    private String ruleMetricName;
    @ExcelProperty(value = "delete_fail_check_result", index = 39)
    private Boolean deleteFailCheckResult;
    @ExcelProperty(value = "Upload Rule Metric Value", index = 40)
    private Boolean uploadRuleMetricValue;
    @ExcelProperty(value = "Upload Abnormal Value", index = 41)
    private Boolean uploadAbnormalValue;

    @ExcelProperty(value = "Specify Static Startup Param", index = 42)
    private Boolean specifyStaticStartupParam;
    @ExcelProperty(value = "Static Startup Param", index = 43)
    private String staticStartupParam;
    @ExcelProperty(value = "Rule Detail", index = 44)
    private String ruleDetail;

    public ExcelMultiTemplateRuleByProject() {
    }

    public ExcelMultiTemplateRuleByProject(String ruleName) {
        this.ruleName = ruleName;
    }

    public ExcelMultiTemplateRuleByProject(ExcelMultiTemplateRuleByProject excelMultiTemplateRuleByProject) {
        BeanUtils.copyProperties(excelMultiTemplateRuleByProject, this);
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

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getLeftProxyUser() {
        return leftProxyUser;
    }

    public void setLeftProxyUser(String leftProxyUser) {
        this.leftProxyUser = leftProxyUser;
    }

    public String getLeftLinkisDataSourceId() {
        return leftLinkisDataSourceId;
    }

    public void setLeftLinkisDataSourceId(String leftLinkisDataSourceId) {
        this.leftLinkisDataSourceId = leftLinkisDataSourceId;
    }

    public String getLeftLinkisDataSourceName() {
        return leftLinkisDataSourceName;
    }

    public void setLeftLinkisDataSourceName(String leftLinkisDataSourceName) {
        this.leftLinkisDataSourceName = leftLinkisDataSourceName;
    }

    public String getLeftLinkisDataSourceType() {
        return leftLinkisDataSourceType;
    }

    public void setLeftLinkisDataSourceType(String leftLinkisDataSourceType) {
        this.leftLinkisDataSourceType = leftLinkisDataSourceType;
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

    public String getRightProxyUser() {
        return rightProxyUser;
    }

    public void setRightProxyUser(String rightProxyUser) {
        this.rightProxyUser = rightProxyUser;
    }

    public String getRightLinkisDataSourceId() {
        return rightLinkisDataSourceId;
    }

    public void setRightLinkisDataSourceId(String rightLinkisDataSourceId) {
        this.rightLinkisDataSourceId = rightLinkisDataSourceId;
    }

    public String getRightLlinkisDataSourceName() {
        return rightLlinkisDataSourceName;
    }

    public void setRightLlinkisDataSourceName(String rightLlinkisDataSourceName) {
        this.rightLlinkisDataSourceName = rightLlinkisDataSourceName;
    }

    public String getRightLlinkisDataSourceType() {
        return rightLlinkisDataSourceType;
    }

    public void setRightLlinkisDataSourceType(String rightLlinkisDataSourceType) {
        this.rightLlinkisDataSourceType = rightLlinkisDataSourceType;
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

    public String getWhereFilter() {
        return whereFilter;
    }

    public void setWhereFilter(String whereFilter) {
        this.whereFilter = whereFilter;
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

    public String getRuleDetail() {
        return ruleDetail;
    }

    public void setRuleDetail(String ruleDetail) {
        this.ruleDetail = ruleDetail;
    }

    @Override
    public String toString() {
        return "ExcelMultiTemplateRuleByProject{" +
            "projectName='" + projectName + '\'' +
            ", ruleGroupName='" + ruleGroupName + '\'' +
            ", ruleName='" + ruleName + '\'' +
            ", templateName='" + templateName + '\'' +
            ", clusterName='" + clusterName + '\'' +
            '}';
    }
}
