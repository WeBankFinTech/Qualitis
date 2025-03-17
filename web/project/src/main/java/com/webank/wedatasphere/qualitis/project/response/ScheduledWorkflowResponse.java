package com.webank.wedatasphere.qualitis.project.response;

import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflow;
import org.springframework.beans.BeanUtils;

/**
 * @author v_minminghe@webank.com
 * @date 2023-04-21 16:46
 * @description
 */
public class ScheduledWorkflowResponse {

    private String scheduledProjectName;

    private String name;

    private String proxyUser;

    private String scheduledType;

    private String executeInterval;

    private Integer executeDateInInterval;

    private String executeTimeInDate;

    private Long scheduleId;

    private String scheduledSignalJson;

    private String createTime;

    private String createUser;

    private String modifyTime;

    private String modifyUser;

    private Long workflowBusinessId;

    public ScheduledWorkflowResponse() {
//        Doing something
    }

    public ScheduledWorkflowResponse(ScheduledWorkflow scheduledWorkflow) {
        BeanUtils.copyProperties(scheduledWorkflow, this);
        this.scheduledProjectName = scheduledWorkflow.getScheduledProject().getName();
    }

    public Long getWorkflowBusinessId() {
        return workflowBusinessId;
    }

    public void setWorkflowBusinessId(Long workflowBusinessId) {
        this.workflowBusinessId = workflowBusinessId;
    }

    public String getScheduledProjectName() {
        return scheduledProjectName;
    }

    public void setScheduledProjectName(String scheduledProjectName) {
        this.scheduledProjectName = scheduledProjectName;
    }

    public String getScheduledSignalJson() {
        return scheduledSignalJson;
    }

    public void setScheduledSignalJson(String scheduledSignalJson) {
        this.scheduledSignalJson = scheduledSignalJson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getScheduledType() {
        return scheduledType;
    }

    public void setScheduledType(String scheduledType) {
        this.scheduledType = scheduledType;
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

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }
}
