package com.webank.wedatasphere.qualitis.scheduled.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author v_gaojiedeng@webank.com
 */
public class RelationRuleGroupResponse {

    @JsonProperty("scheduled_task_id")
    private Long scheduledTaskId;
    @JsonProperty("release_user")
    private String releaseUser;

    @JsonProperty("rule_group_type")
    List<ScheduledTaskFrontAndBackResponse> ruleGroupType;

    public Long getScheduledTaskId() {
        return scheduledTaskId;
    }

    public void setScheduledTaskId(Long scheduledTaskId) {
        this.scheduledTaskId = scheduledTaskId;
    }

    public List<ScheduledTaskFrontAndBackResponse> getRuleGroupType() {
        return ruleGroupType;
    }

    public void setRuleGroupType(List<ScheduledTaskFrontAndBackResponse> ruleGroupType) {
        this.ruleGroupType = ruleGroupType;
    }

    public String getReleaseUser() {
        return releaseUser;
    }

    public void setReleaseUser(String releaseUser) {
        this.releaseUser = releaseUser;
    }

}
