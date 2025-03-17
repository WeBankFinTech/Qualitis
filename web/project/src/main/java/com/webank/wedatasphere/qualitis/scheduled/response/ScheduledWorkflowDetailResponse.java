package com.webank.wedatasphere.qualitis.scheduled.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.scheduled.dao.ScheduledSignalDao;
import com.webank.wedatasphere.qualitis.scheduled.dao.ScheduledWorkflowTaskRelationDao;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledSignal;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflow;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflowTaskRelation;
import com.webank.wedatasphere.qualitis.scheduled.request.ScheduledSignalParameterRequest;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-14 9:34
 * @description
 */
public class ScheduledWorkflowDetailResponse {


    @JsonProperty("scheduled_workflow_id")
    private Long workflowId;
    @JsonProperty("scheduled_workflow_name")
    private String workflowName;
    @JsonProperty("proxy_user")
    private String proxyUser;
    @JsonProperty("scheduled_type")
    private String scheduledType;
    @JsonProperty("execute_interval")
    private String executeInterval;
    @JsonProperty("execute_date_in_interval")
    private Integer executeDateInInterval;
    @JsonProperty("execute_time_in_date")
    private String executeTimeInDate;
    @JsonProperty("rule_group_list")
    private List<RuleGroupResponse> ruleGroupList;
    @JsonProperty("signal_parameter_list")
    private List<ScheduledSignalParameterResponse> signalParameterList;
    @JsonProperty("scheduled_signal_json")
    private String scheduledSignalJson;

    public ScheduledWorkflowDetailResponse(ScheduledWorkflow scheduledWorkflow) {
        this.workflowId = scheduledWorkflow.getId();
        this.workflowName = scheduledWorkflow.getName();
        this.proxyUser = scheduledWorkflow.getProxyUser();
        this.executeInterval = scheduledWorkflow.getExecuteInterval();
        this.executeDateInInterval = scheduledWorkflow.getExecuteDateInInterval();
        this.executeTimeInDate = scheduledWorkflow.getExecuteTimeInDate();
        this.scheduledType = scheduledWorkflow.getScheduledType();
        this.scheduledSignalJson = scheduledWorkflow.getScheduledSignalJson();
    }

    public String getScheduledSignalJson() {
        return scheduledSignalJson;
    }

    public void setScheduledSignalJson(String scheduledSignalJson) {
        this.scheduledSignalJson = scheduledSignalJson;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public String getScheduledType() {
        return scheduledType;
    }

    public void setScheduledType(String scheduledType) {
        this.scheduledType = scheduledType;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getExecuteInterval() {
        return executeInterval;
    }

    public void setExecuteInterval(String executeInterval) {
        this.executeInterval = executeInterval;
    }

    public Integer getExecuteDateInInterval() {
        return executeDateInInterval;
    }

    public void setExecuteDateInInterval(Integer executeDateInInterval) {
        this.executeDateInInterval = executeDateInInterval;
    }

    public String getExecuteTimeInDate() {
        return executeTimeInDate;
    }

    public void setExecuteTimeInDate(String executeTimeInDate) {
        this.executeTimeInDate = executeTimeInDate;
    }

    public List<RuleGroupResponse> getRuleGroupList() {
        return ruleGroupList;
    }

    public void setRuleGroupList(List<RuleGroupResponse> ruleGroupList) {
        this.ruleGroupList = ruleGroupList;
    }

    public List<ScheduledSignalParameterResponse> getSignalParameterList() {
        return signalParameterList;
    }

    public void setSignalParameterList(List<ScheduledSignalParameterResponse> signalParameterList) {
        this.signalParameterList = signalParameterList;
    }
}
