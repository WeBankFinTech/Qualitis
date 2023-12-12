package com.webank.wedatasphere.qualitis.rule.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.rule.entity.TaskNewValue;

/**
 * @author v_gaojiedeng@webank.com
 */
public class TaskNewValueResponse {

    @JsonProperty("task_new_value_id")
    private Long taskNewValueId;
    @JsonProperty("rule_id")
    private Long ruleId;
    @JsonProperty("result_value")
    private String resultValue;
    @JsonProperty("status")
    private Long status;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("modify_user")
    private String modifyUser;
    @JsonProperty("modify_time")
    private String modifyTime;
    @JsonProperty("rule_version")
    private String ruleVersion;

    public Long getTaskNewValueId() {
        return taskNewValueId;
    }

    public void setTaskNewValueId(Long taskNewValueId) {
        this.taskNewValueId = taskNewValueId;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getResultValue() {
        return resultValue;
    }

    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
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

    public String getRuleVersion() {
        return ruleVersion;
    }

    public void setRuleVersion(String ruleVersion) {
        this.ruleVersion = ruleVersion;
    }

    public TaskNewValueResponse() {
    }

    public TaskNewValueResponse(TaskNewValue taskNewValue) {
        this.taskNewValueId = taskNewValue.getId();
        this.ruleId = taskNewValue.getRuleId();
        this.resultValue = taskNewValue.getResultValue();
        this.status = taskNewValue.getStatus();
        this.createUser = taskNewValue.getCreateUser();
        this.createTime = taskNewValue.getCreateTime();
        this.modifyTime = taskNewValue.getModifyTime();
        this.modifyUser = taskNewValue.getModifyUser();
        this.ruleVersion = taskNewValue.getRuleVersion();
    }
}
