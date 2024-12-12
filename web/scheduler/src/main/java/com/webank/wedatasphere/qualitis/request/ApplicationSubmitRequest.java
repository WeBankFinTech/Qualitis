package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2022/1/6 16:05
 */
public class ApplicationSubmitRequest {
    @JsonProperty("job_id")
    private String jobId;
    private Long projectId;
    private Long ruleGroupId;
    private List<Long> ruleIds;
    private StringBuilder partition;
    private String partitionFullSize;
    private String clusterName;
    private String database;
    private String table;

    public ApplicationSubmitRequest() {
    }

    public ApplicationSubmitRequest(Long projectId, Long ruleGroupId, List<Long> ruleIds, StringBuilder partition) {
        this.projectId = projectId;
        this.ruleGroupId = ruleGroupId;
        this.ruleIds = ruleIds;
        this.partition = partition;
    }

    public ApplicationSubmitRequest(Long projectId, Long ruleGroupId, List<Long> ruleIds, StringBuilder partition, String clusterName, String database, String table) {
        this.projectId = projectId;
        this.ruleGroupId = ruleGroupId;
        this.ruleIds = ruleIds;
        this.partition = partition;
        this.clusterName = clusterName;
        this.database = database;
        this.table = table;
    }

    public ApplicationSubmitRequest(String jobId, Long projectId, Long ruleGroupId, List<Long> ruleIds, StringBuilder partition) {
        this.jobId = jobId;
        this.projectId = projectId;
        this.ruleGroupId = ruleGroupId;
        this.ruleIds = ruleIds;
        this.partition = partition;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
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

    public List<Long> getRuleIds() {
        return ruleIds;
    }

    public void setRuleIds(List<Long> ruleIds) {
        this.ruleIds = ruleIds;
    }

    public StringBuilder getPartition() {
        return partition;
    }

    public void setPartition(StringBuilder partition) {
        this.partition = partition;
    }

    public String getPartitionFullSize() {
        return partitionFullSize;
    }

    public void setPartitionFullSize(String partitionFullSize) {
        this.partitionFullSize = partitionFullSize;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
