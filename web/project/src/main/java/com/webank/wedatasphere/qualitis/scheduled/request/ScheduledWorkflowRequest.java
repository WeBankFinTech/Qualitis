package com.webank.wedatasphere.qualitis.scheduled.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.request.ParameterChecker;
import com.webank.wedatasphere.qualitis.scheduled.constant.ExecuteIntervalEnum;
import com.webank.wedatasphere.qualitis.scheduled.request.checker.AbstractJsonParameterChecker;
import com.webank.wedatasphere.qualitis.scheduled.request.checker.ScheduledSignalChecker;
import com.webank.wedatasphere.qualitis.scheduled.service.ScheduledTaskPushService;
import com.webank.wedatasphere.qualitis.scheduled.util.CronUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-14 9:34
 * @description
 */
public class ScheduledWorkflowRequest {

    @JsonProperty("scheduled_workflow_id")
    private Long workflowId;
    @JsonProperty(value = "scheduled_workflow_name", required = true)
    private String workflowName;
    @JsonProperty(value = "proxy_user", required = true)
    private String proxyUser;
    @JsonProperty(value = "scheduled_type", required = true)
    private String scheduledType;
    @JsonProperty(value = "execute_interval")
    private String executeInterval;
    @JsonProperty("execute_date_in_interval")
    private Integer executeDateInInterval;
    @JsonProperty(value = "execute_time_in_date")
    private String executeTimeInDate;
    @JsonProperty(value = "rule_group_list", required = true)
    private List<RuleGroupRequest> ruleGroupList;
    @JsonProperty("signal_parameter_list")
    private List<ScheduledSignalParameterRequest> signalParameterList;
    @JsonProperty("scheduled_signal_json")
    private String scheduledSignalJson;

    public static void checkRequest(ScheduledWorkflowRequest request) throws UnExpectedRequestException {
        try {
            ParameterChecker.checkEmpty(request);
        } catch (IllegalAccessException e) {
            throw new UnExpectedRequestException("Server error!Failed to check request parameter.");
        }
        ExecuteIntervalEnum executeIntervalEnum = ExecuteIntervalEnum.fromCode(request.getExecuteInterval());
        if (ExecuteIntervalEnum.WEEK.equals(executeIntervalEnum)) {
            CommonChecker.checkObject(request.getExecuteDateInInterval(), "execute_date_in_interval");
            CommonChecker.checkIntegerMaxLength(request.getExecuteDateInInterval(), 7, "execute_date_in_interval");
        } else if (ExecuteIntervalEnum.MONTH.equals(executeIntervalEnum)) {
            CommonChecker.checkObject(request.getExecuteDateInInterval(), "execute_date_in_interval");
            CommonChecker.checkIntegerMaxLength(request.getExecuteDateInInterval(), 31, "execute_date_in_interval");
        }
        List<ScheduledSignalParameterRequest> signalParameterList = request.getSignalParameterList();
        if (CollectionUtils.isNotEmpty(signalParameterList)) {
            for (ScheduledSignalParameterRequest parameterRequest: signalParameterList) {
                ScheduledSignalParameterRequest.checkRequest(parameterRequest);
            }
        }
        if (ScheduledTaskPushService.SCHEDULE_TYPE_SIGNAL.equals(request.getScheduledType())) {
            AbstractJsonParameterChecker jsonParameterChecker = new ScheduledSignalChecker();
            jsonParameterChecker.check(request.getScheduledSignalJson());
        }
    }

    public List<ScheduledSignalParameterRequest> getSignalParameterList() {
        return signalParameterList;
    }

    public void setSignalParameterList(List<ScheduledSignalParameterRequest> signalParameterList) {
        this.signalParameterList = signalParameterList;
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

    public List<RuleGroupRequest> getRuleGroupList() {
        return ruleGroupList;
    }

    public void setRuleGroupList(List<RuleGroupRequest> ruleGroupList) {
        this.ruleGroupList = ruleGroupList;
    }
}
