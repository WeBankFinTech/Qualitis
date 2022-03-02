package com.webank.wedatasphere.qualitis.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

/**
 * @author allenzhou@webank.com
 * @date 2021/5/6 16:00
 */
public class ExcelResult extends BaseRowModel {
    @ExcelProperty(value = "Rule Name", index = 0)
    private String ruleName;
    @ExcelProperty(value = "Cluster Name", index = 1)
    private String clusterName;
    @ExcelProperty(value = "Database Name", index = 2)
    private String databaseName;
    @ExcelProperty(value = "Table Name", index = 3)
    private String tableName;
    @ExcelProperty(value = "Check Templates", index = 4)
    private String ruleCheckTemplates;
    @ExcelProperty(value = "History Result", index = 5)
    private String historyResult;
    @ExcelProperty(value = "Result Time", index = 6)
    private String createTime;
    @ExcelProperty(value = "Execution User", index = 7)
    private String executionUser;
    @ExcelProperty(value = "Begin Time", index = 8)
    private String beginTime;
    @ExcelProperty(value = "Eng Time", index = 9)
    private String endTime;

    public ExcelResult() {
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
