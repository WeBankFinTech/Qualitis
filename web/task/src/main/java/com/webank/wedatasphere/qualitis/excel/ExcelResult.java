package com.webank.wedatasphere.qualitis.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

/**
 * @author allenzhou@webank.com
 * @date 2021/5/6 16:00
 */
public class ExcelResult extends BaseRowModel {
    @ExcelProperty(value = "Project Name", index = 0)
    private String projectName;
    @ExcelProperty(value = "Rule Name", index = 1)
    private String ruleName;
    @ExcelProperty(value = "Cluster Name", index = 2)
    private String clusterName;
    @ExcelProperty(value = "Database Name", index = 3)
    private String databaseName;
    @ExcelProperty(value = "Table Name", index = 4)
    private String tableName;
    @ExcelProperty(value = "Partition", index = 5)
    private String partition;
    @ExcelProperty(value = "Check Templates", index = 6)
    private String ruleCheckTemplates;
    @ExcelProperty(value = "Status", index = 7)
    private String status;
    @ExcelProperty(value = "History Result", index = 8)
    private String historyResult;
    @ExcelProperty(value = "Result Time", index = 9)
    private String createTime;
    @ExcelProperty(value = "Execution User", index = 10)
    private String executionUser;
    @ExcelProperty(value = "Begin Time", index = 11)
    private String beginTime;
    @ExcelProperty(value = "End Time", index = 12)
    private String endTime;
    @ExcelProperty(value = "Application ID", index = 13)
    private String applicationId;
    @ExcelProperty(value = "Application Comment", index = 14)
    private String applicationComment;

    public ExcelResult() {
        // Do nothing.
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

    public String getRuleCheckTemplates() {
        return ruleCheckTemplates;
    }

    public void setRuleCheckTemplates(String ruleCheckTemplates) {
        this.ruleCheckTemplates = ruleCheckTemplates;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHistoryResult() {
        return historyResult;
    }

    public void setHistoryResult(String historyResult) {
        this.historyResult = historyResult;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getExecutionUser() {
        return executionUser;
    }

    public void setExecutionUser(String executionUser) {
        this.executionUser = executionUser;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationComment() {
        return applicationComment;
    }

    public void setApplicationComment(String applicationComment) {
        this.applicationComment = applicationComment;
    }

    @Override
    public String toString() {
        return "ExcelResult{" +
            "ruleName='" + ruleName + '\'' +
            ", clusterName='" + clusterName + '\'' +
            ", databaseName='" + databaseName + '\'' +
            ", tableName='" + tableName + '\'' +
            ", ruleCheckTemplates='" + ruleCheckTemplates + '\'' +
            ", historyResult='" + historyResult + '\'' +
            ", createTime='" + createTime + '\'' +
            ", executionUser='" + executionUser + '\'' +
            '}';
    }
}
