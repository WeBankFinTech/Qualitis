package com.webank.wedatasphere.qualitis.dto;

import com.webank.wedatasphere.qualitis.entity.Application;

import java.util.List;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2023-01-11 9:56
 * @description
 */
public class SubmitRuleBaseInfo {

    private String jobId;
    private StringBuilder partition;
    private String createUser;
    private String executionUser;
    private String nodeName;
    private Long projectId;
    private Long ruleGroupId;
    private String fpsFileId;
    private String fpsHashValue;
    private String startupParam;
    private String clusterName;
    private String setFlag;
    private Map<String, String> execParams;
    private String execParamStr;
    private StringBuilder runDate;
    private StringBuilder runToday;
    private StringBuilder splitBy;
    private Integer invokeCode;
    private Application pendingApplication;
    private String subSystemId;
    private String partitionFullSize;
    private Boolean engineReuse;
    private String envNames;

    public static SubmitRuleBaseInfo build(String jobId, StringBuilder partition, String createUser, String executionUser, String nodeName, Long projectId
            , Long ruleGroupId, String fpsFileId, String fpsHashValue, String startupParam, String clusterName, String setFlag, Map<String, String> execParams
            , String execParamStr, StringBuilder runDate, StringBuilder runToday, StringBuilder splitBy, Integer invokeCode, Application pendingApplication, String subSystemId, String partitionFullSize, Boolean engineReuse
            , String envNames) {
        SubmitRuleBaseInfo submitRuleBaseInfo = new SubmitRuleBaseInfo();
        submitRuleBaseInfo.setJobId(jobId);
        submitRuleBaseInfo.setPartition(partition);
        submitRuleBaseInfo.setCreateUser(createUser);
        submitRuleBaseInfo.setExecutionUser(executionUser);
        submitRuleBaseInfo.setNodeName(nodeName);
        submitRuleBaseInfo.setProjectId(projectId);
        submitRuleBaseInfo.setRuleGroupId(ruleGroupId);
        submitRuleBaseInfo.setFpsFileId(fpsFileId);
        submitRuleBaseInfo.setFpsHashValue(fpsHashValue);
        submitRuleBaseInfo.setStartupParam(startupParam);
        submitRuleBaseInfo.setClusterName(clusterName);
        submitRuleBaseInfo.setSetFlag(setFlag);
        submitRuleBaseInfo.setExecParams(execParams);
        submitRuleBaseInfo.setExecParamStr(execParamStr);
        submitRuleBaseInfo.setRunDate(runDate);
        submitRuleBaseInfo.setRunToday(runToday);
        submitRuleBaseInfo.setSplitBy(splitBy);
        submitRuleBaseInfo.setInvokeCode(invokeCode);
        submitRuleBaseInfo.setPendingApplication(pendingApplication);
        submitRuleBaseInfo.setSubSystemId(subSystemId);
        submitRuleBaseInfo.setPartitionFullSize(partitionFullSize);
        submitRuleBaseInfo.setEngineReuse(engineReuse);
        submitRuleBaseInfo.setEnvNames(envNames);

        return submitRuleBaseInfo;
    }

    public SubmitRuleBaseInfo jobId(String jobId) {
        this.jobId = jobId;
        return this;
    }

    public String getEnvNames() {
        return envNames;
    }

    public void setEnvNames(String envNames) {
        this.envNames = envNames;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public StringBuilder getPartition() {
        return partition;
    }

    public void setPartition(StringBuilder partition) {
        this.partition = partition;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getExecutionUser() {
        return executionUser;
    }

    public void setExecutionUser(String executionUser) {
        this.executionUser = executionUser;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public String getFpsFileId() {
        return fpsFileId;
    }

    public void setFpsFileId(String fpsFileId) {
        this.fpsFileId = fpsFileId;
    }

    public String getFpsHashValue() {
        return fpsHashValue;
    }

    public void setFpsHashValue(String fpsHashValue) {
        this.fpsHashValue = fpsHashValue;
    }

    public String getStartupParam() {
        return startupParam;
    }

    public void setStartupParam(String startupParam) {
        this.startupParam = startupParam;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getSetFlag() {
        return setFlag;
    }

    public void setSetFlag(String setFlag) {
        this.setFlag = setFlag;
    }

    public Map<String, String> getExecParams() {
        return execParams;
    }

    public void setExecParams(Map<String, String> execParams) {
        this.execParams = execParams;
    }

    public String getExecParamStr() {
        return execParamStr;
    }

    public void setExecParamStr(String execParamStr) {
        this.execParamStr = execParamStr;
    }

    public StringBuilder getRunDate() {
        return runDate;
    }

    public void setRunDate(StringBuilder runDate) {
        this.runDate = runDate;
    }

    public StringBuilder getSplitBy() {
        return splitBy;
    }

    public void setSplitBy(StringBuilder splitBy) {
        this.splitBy = splitBy;
    }

    public Integer getInvokeCode() {
        return invokeCode;
    }

    public void setInvokeCode(Integer invokeCode) {
        this.invokeCode = invokeCode;
    }

    public Application getPendingApplication() {
        return pendingApplication;
    }

    public void setPendingApplication(Application pendingApplication) {
        this.pendingApplication = pendingApplication;
    }

    public String getSubSystemId() {
        return subSystemId;
    }

    public void setSubSystemId(String subSystemId) {
        this.subSystemId = subSystemId;
    }

    public String getPartitionFullSize() {
        return partitionFullSize;
    }

    public void setPartitionFullSize(String partitionFullSize) {
        this.partitionFullSize = partitionFullSize;
    }

    public Boolean getEngineReuse() {
        return engineReuse;
    }

    public void setEngineReuse(Boolean engineReuse) {
        this.engineReuse = engineReuse;
    }

    public StringBuilder getRunToday() {
        return runToday;
    }

    public void setRunToday(StringBuilder runToday) {
        this.runToday = runToday;
    }
}
