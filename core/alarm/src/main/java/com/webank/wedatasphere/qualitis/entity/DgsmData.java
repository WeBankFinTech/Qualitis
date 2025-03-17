package com.webank.wedatasphere.qualitis.entity;

/**
 * @author allenzhou@webank.com
 * @date 2021/4/27 15:05
 */
public class DgsmData {
    private String checkRuleType;
    private String checkRuleDescription;
    private String regRuleCode;
    private String checkRuleCode;
    private CheckRuleContent checkRuleContent;
    private String businessSystem;
    private String checkDatabaseName;
    private String checkTableName;
    private String checkTableChineseName;
    private String checkFieldName;
    private String checkFieldChineseName;
    private String checkFieldDescription;
    private String comparisonMethod;
    private String alertType;
    private String alertReceiver;
    private String checkTime;
    private String checkStatus;
    private String checkSQL;
    private String checkPartition;
    private String ruleExecutionResult;

    public DgsmData() {
        // do nothing
    }

    public String getCheckRuleType() {
        return checkRuleType;
    }

    public void setCheckRuleType(String checkRuleType) {
        this.checkRuleType = checkRuleType;
    }

    public String getCheckRuleDescription() {
        return checkRuleDescription;
    }

    public void setCheckRuleDescription(String checkRuleDescription) {
        this.checkRuleDescription = checkRuleDescription;
    }

    public String getRegRuleCode() {
        return regRuleCode;
    }

    public void setRegRuleCode(String regRuleCode) {
        this.regRuleCode = regRuleCode;
    }

    public String getCheckRuleCode() {
        return checkRuleCode;
    }

    public void setCheckRuleCode(String checkRuleCode) {
        this.checkRuleCode = checkRuleCode;
    }

    public CheckRuleContent getCheckRuleContent() {
        return checkRuleContent;
    }

    public void setCheckRuleContent(CheckRuleContent checkRuleContent) {
        this.checkRuleContent = checkRuleContent;
    }

    public String getBusinessSystem() {
        return businessSystem;
    }

    public void setBusinessSystem(String businessSystem) {
        this.businessSystem = businessSystem;
    }

    public String getCheckDatabaseName() {
        return checkDatabaseName;
    }

    public void setCheckDatabaseName(String checkDatabaseName) {
        this.checkDatabaseName = checkDatabaseName;
    }

    public String getCheckTableName() {
        return checkTableName;
    }

    public void setCheckTableName(String checkTableName) {
        this.checkTableName = checkTableName;
    }

    public String getCheckTableChineseName() {
        return checkTableChineseName;
    }

    public void setCheckTableChineseName(String checkTableChineseName) {
        this.checkTableChineseName = checkTableChineseName;
    }

    public String getCheckFieldName() {
        return checkFieldName;
    }

    public void setCheckFieldName(String checkFieldName) {
        this.checkFieldName = checkFieldName;
    }

    public String getCheckFieldChineseName() {
        return checkFieldChineseName;
    }

    public void setCheckFieldChineseName(String checkFieldChineseName) {
        this.checkFieldChineseName = checkFieldChineseName;
    }

    public String getCheckFieldDescription() {
        return checkFieldDescription;
    }

    public void setCheckFieldDescription(String checkFieldDescription) {
        this.checkFieldDescription = checkFieldDescription;
    }

    public String getComparisonMethod() {
        return comparisonMethod;
    }

    public void setComparisonMethod(String comparisonMethod) {
        this.comparisonMethod = comparisonMethod;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getAlertReceiver() {
        return alertReceiver;
    }

    public void setAlertReceiver(String alertReceiver) {
        this.alertReceiver = alertReceiver;
    }

    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getCheckSQL() {
        return checkSQL;
    }

    public void setCheckSQL(String checkSQL) {
        this.checkSQL = checkSQL;
    }

    public String getCheckPartition() {
        return checkPartition;
    }

    public void setCheckPartition(String checkPartition) {
        this.checkPartition = checkPartition;
    }

    public String getRuleExecutionResult() {
        return ruleExecutionResult;
    }

    public void setRuleExecutionResult(String ruleExecutionResult) {
        this.ruleExecutionResult = ruleExecutionResult;
    }
}
