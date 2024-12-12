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
    private StringBuffer partition;

    public ApplicationSubmitRequest() {
    }

    public ApplicationSubmitRequest(Long projectId, Long ruleGroupId, List<Long> ruleIds, StringBuffer partition) {
        this.projectId = projectId;
        this.ruleGroupId = ruleGroupId;
        this.ruleIds = ruleIds;
        this.partition = partition;
    }

    public ApplicationSubmitRequest(String jobId, Long projectId, Long ruleGroupId, List<Long> ruleIds, StringBuffer partition) {
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

    public StringBuffer getPartition() {
        return partition;
    }

    public void setPartition(StringBuffer partition) {
        this.partition = partition;
    }
}
