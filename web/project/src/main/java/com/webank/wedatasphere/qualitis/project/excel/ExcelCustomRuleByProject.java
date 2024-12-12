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
public class ExcelCustomRuleByProject extends BaseRowModel {

    @ExcelProperty(value = "Project Name", index = 0)
    private String projectName;

    @ExcelProperty(value = "Rule Group Name", index = 1)
    private String ruleGroupName;

    @ExcelProperty(value = "Rule Name", index = 2)
    private String ruleName;

    @ExcelProperty(value = "Rule Chinese Name", index = 3)
    private String ruleCnName;

    @ExcelProperty(value = "Save Not Pass Verification Data", index = 4)
    private Boolean saveMidTable;

    @ExcelProperty(value = "Cluster", index = 5)
    private String clusterName;

    @ExcelProperty(value = "Proxy User", index = 6)
    private String proxyUser;

    @ExcelProperty(value = "Data Source ID", index = 7)
    private String linkisDataSourceId;

    @ExcelProperty(value = "Data Source Name", index = 8)
    private String linkisDataSourceName;

    @ExcelProperty(value = "Data Source Type", index = 9)
    private String linkisDataSourceType;

    @ExcelProperty(value = "Function", index = 10)
    private String functionName;

    @ExcelProperty(value = "Function Content", index = 11)
    private String functionContent;

    @ExcelProperty(value = "From Content", index = 12)
    private String fromContent;

    @ExcelProperty(value = "Where Content", index = 13)
    private String whereContent;

    @ExcelProperty(value = "Check Column", index = 14)
    private String outputName;

    @ExcelProperty(value = "Verification Check Column", index = 15)
    private String alarmCheckName;

    @ExcelProperty(value = "Verification Template", index = 16)
    private String checkTemplateName;

    @ExcelProperty(value = "Verification Compare Type", index = 17)
    private String compareType;

    @ExcelProperty(value = "Verification Threshold", index = 18)
    private String threshold;

    @ExcelProperty(value = "AbortOnFailure", index = 19)
    private Boolean abortOnFailure;

    @ExcelProperty(value = "Create User", index = 20)
    private String createUser;
    @ExcelProperty(value = "Create Time", index = 21)
    private String createTime;
    @ExcelProperty(value = "Modify User", index = 22)
    private String modifyUser;
    @ExcelProperty(value = "Modify Time", index = 23)
    private String modifyTime;

    @ExcelProperty(value = "Rule Metric Name", index = 24)
    private String ruleMetricName;
    @ExcelProperty(value = "Rule Metric En Code", index = 25)
    private String ruleMetricEnCode;
    @ExcelProperty(value = "Upload Rule Metric Value", index = 26)
    private Boolean uploadRuleMetricValue;
    @ExcelProperty(value = "Upload Abnormal Value", index = 27)
    private Boolean uploadAbnormalValue;

    @ExcelProperty(value = "delete_fail_check_result", index = 28)
    private Boolean deleteFailCheckResult;
    @ExcelProperty(value = "Specify Static Startup Param", index = 29)
    private Boolean specifyStaticStartupParam;
    @ExcelProperty(value = "Static Startup Param", index = 30)
    private String staticStartupParam;

    @ExcelProperty(value = "Sql Check Area", index = 31)
    private String sqlCheckArea;
    @ExcelProperty(value = "Rule Detail", index = 32)
    private String ruleDetail;

    public ExcelCustomRuleByProject() {
    }

    public ExcelCustomRuleByProject(String ruleName) {
        this.ruleName = ruleName;
    }

    public ExcelCustomRuleByProject(ExcelCustomRuleByProject excelCustomRuleByProject) {
        BeanUtils.copyProperties(excelCustomRuleByProject, this);
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

    public Boolean getSaveMidTable() {
        return saveMidTable;
    }

    public void setSaveMidTable(Boolean saveMidTable) {
        this.saveMidTable = saveMidTable;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
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

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
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

    public String getRuleMetricName() {
        return ruleMetricName;
    }

    public void setRuleMetricName(String ruleMetricName) {
        this.ruleMetricName = ruleMetricName;
    }

    public String getRuleMetricEnCode() {
        return ruleMetricEnCode;
    }

    public void setRuleMetricEnCode(String ruleMetricEnCode) {
        this.ruleMetricEnCode = ruleMetricEnCode;
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

    public String getSqlCheckArea() {
        return sqlCheckArea;
    }

    public void setSqlCheckArea(String sqlCheckArea) {
        this.sqlCheckArea = sqlCheckArea;
    }

    public String getRuleDetail() {
        return ruleDetail;
    }

    public void setRuleDetail(String ruleDetail) {
        this.ruleDetail = ruleDetail;
    }

    @Override
    public String toString() {
        return "ExcelCustomRuleByProject{" +
            "projectName='" + projectName + '\'' +
            ", ruleGroupName='" + ruleGroupName + '\'' +
            ", ruleName='" + ruleName + '\'' +
            ", outputName='" + outputName + '\'' +
            ", clusterName='" + clusterName + '\'' +
            ", createUser='" + createUser + '\'' +
            ", createTime='" + createTime + '\'' +
            '}';
    }
}
