package com.webank.wedatasphere.qualitis.scheduled.request;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.scheduled.constant.ExecuteIntervalEnum;
//import com.webank.wedatasphere.qualitis.scheduled.constant.SignalTypeEnum;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledProject;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledSignal;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledTask;
import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledWorkflow;
import com.webank.wedatasphere.qualitis.scheduled.service.ScheduledTaskPushService;
import com.webank.wedatasphere.qualitis.scheduled.util.CronUtil;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2022-08-11 18:59
 * @description
 */
public class WtssScheduledJobRequest {
    private String projectName;
    private String releaseUser;
    private String approveNumber;
    private Long workflowId;
    private String workflowName;
    private Long scheduleId;
    private String scheduleName;
    private String cronExpression;
    private String proxyUser;
    private String jobRsa;
    private Long ruleGroupId;
    private String dependencies;
    private String scheduledType;
    private Map<String, Object> signalParameterMap;
    private Map<String, Object> signalTypeMap;
    /**
     * 1-flow; 2-task; 3-signal
     */
    private Integer nodeLevel;

    public static WtssScheduledJobRequest fromScheduledWorkflow(ScheduledProject project, ScheduledWorkflow scheduledWorkflow) throws UnExpectedRequestException {
        WtssScheduledJobRequest wtssScheduledJobRequest = new WtssScheduledJobRequest();
        wtssScheduledJobRequest.setProjectName(project.getName());
        wtssScheduledJobRequest.setWorkflowId(scheduledWorkflow.getId());
        wtssScheduledJobRequest.setWorkflowName(scheduledWorkflow.getName());
        wtssScheduledJobRequest.setScheduleId(scheduledWorkflow.getScheduleId());
        wtssScheduledJobRequest.setScheduleName(scheduledWorkflow.getName());
        wtssScheduledJobRequest.setNodeLevel(ScheduledTaskPushService.NODE_LEVEL_FLOW);
        wtssScheduledJobRequest.setProxyUser(scheduledWorkflow.getProxyUser());
        wtssScheduledJobRequest.setReleaseUser(project.getReleaseUser());
        wtssScheduledJobRequest.setScheduledType(scheduledWorkflow.getScheduledType());
        if (ScheduledTaskPushService.SCHEDULE_TYPE_INTERVAL.equals(scheduledWorkflow.getScheduledType())) {
            try {
                String cronExpression = CronUtil.createIntervalCron(ExecuteIntervalEnum.fromCode(scheduledWorkflow.getExecuteInterval()), scheduledWorkflow.getExecuteDateInInterval(), scheduledWorkflow.getExecuteTimeInDate());
                wtssScheduledJobRequest.setCronExpression(cronExpression);
            } catch (ParseException e) {
                throw new UnExpectedRequestException("Error parameter format: execute_interval");
            }
        } else if (ScheduledTaskPushService.SCHEDULE_TYPE_SIGNAL.equals(scheduledWorkflow.getScheduledType())) {
            wtssScheduledJobRequest.setSignalTypeMap(scheduledWorkflow.getScheduledSignalJsonMap());
        }
        return wtssScheduledJobRequest;
    }

    public static WtssScheduledJobRequest fromScheduledTask(ScheduledProject project, ScheduledWorkflow scheduledWorkflow, ScheduledTask scheduledTask) {
        WtssScheduledJobRequest wtssScheduledJobRequest = new WtssScheduledJobRequest();
        wtssScheduledJobRequest.setProjectName(project.getName());
        wtssScheduledJobRequest.setWorkflowId(scheduledWorkflow.getId());
        wtssScheduledJobRequest.setWorkflowName(scheduledWorkflow.getName());
        wtssScheduledJobRequest.setScheduleId(scheduledWorkflow.getScheduleId());
        wtssScheduledJobRequest.setScheduleName(scheduledTask.getTaskName());
        wtssScheduledJobRequest.setNodeLevel(ScheduledTaskPushService.NODE_LEVEL_TASK);
        Long ruleGroupId = scheduledTask.getScheduledWorkflowTaskRelation().getRuleGroup().getId();
        wtssScheduledJobRequest.setRuleGroupId(ruleGroupId);
        wtssScheduledJobRequest.setReleaseUser(project.getReleaseUser());
        wtssScheduledJobRequest.setProxyUser(scheduledWorkflow.getProxyUser());
        wtssScheduledJobRequest.setApproveNumber(scheduledTask.getApproveNumber());
        return wtssScheduledJobRequest;
    }

    public static WtssScheduledJobRequest fromScheduledSignal(ScheduledProject project, ScheduledWorkflow scheduledWorkflow, ScheduledSignal scheduledSignal) throws UnExpectedRequestException {
        WtssScheduledJobRequest wtssScheduledJobRequest = new WtssScheduledJobRequest();
        wtssScheduledJobRequest.setProjectName(project.getName());
        wtssScheduledJobRequest.setWorkflowId(scheduledWorkflow.getId());
        wtssScheduledJobRequest.setWorkflowName(scheduledWorkflow.getName());
        wtssScheduledJobRequest.setScheduleId(scheduledWorkflow.getScheduleId());
        wtssScheduledJobRequest.setScheduleName(scheduledSignal.getName());
        wtssScheduledJobRequest.setNodeLevel(ScheduledTaskPushService.NODE_LEVEL_SIGNAL);
        try {
            Map<String, Object> signalParameterMap = new ObjectMapper().readValue(scheduledSignal.getContentJson(), Map.class);
            signalParameterMap.put("type", getTypeParameter(scheduledSignal.getType()));
            wtssScheduledJobRequest.setSignalParameterMap(signalParameterMap);
        } catch (IOException e) {
            throw new UnExpectedRequestException("Error parameter format");
        }
        return wtssScheduledJobRequest;
    }

    private static String getTypeParameter(Integer signalType) {
//        SignalTypeEnum signalTypeEnum = SignalTypeEnum.fromCode(signalType);
//        switch (signalTypeEnum) {
//            case RMB_SEND:
//                return "rmbsender";
//            default:
//                return "eventchecker";
//        }
        return "";
    }

    public Map<String, Object> getSignalTypeMap() {
        return signalTypeMap;
    }

    public void setSignalTypeMap(Map<String, Object> signalTypeMap) {
        this.signalTypeMap = signalTypeMap;
    }

    public Map<String, Object> getSignalParameterMap() {
        return signalParameterMap;
    }

    public void setSignalParameterMap(Map<String, Object> signalParameterMap) {
        this.signalParameterMap = signalParameterMap;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    public Integer getNodeLevel() {
        return nodeLevel;
    }

    public void setNodeLevel(Integer nodeLevel) {
        this.nodeLevel = nodeLevel;
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

    public String getDependencies() {
        return dependencies;
    }

    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public String getApproveNumber() {
        return approveNumber;
    }

    public void setApproveNumber(String approveNumber) {
        this.approveNumber = approveNumber;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getJobRsa() {
        return jobRsa;
    }

    public void setJobRsa(String jobRsa) {
        this.jobRsa = jobRsa;
    }

    public Long getRuleGroupId() {
        return ruleGroupId;
    }

    public void setRuleGroupId(Long ruleGroupId) {
        this.ruleGroupId = ruleGroupId;
    }

    public String getReleaseUser() {
        return releaseUser;
    }

    public void setReleaseUser(String releaseUser) {
        this.releaseUser = releaseUser;
    }

    @Override
    public String toString() {
        return "ScheduledTaskDto{" +
                "projectName='" + projectName + '\'' +
                ", releaseUser='" + releaseUser + '\'' +
                ", approveNumber='" + approveNumber + '\'' +
                ", workflowId=" + workflowId +
                ", workflowName='" + workflowName + '\'' +
                ", scheduleId=" + scheduleId +
                ", scheduleName='" + scheduleName + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", proxyUser='" + proxyUser + '\'' +
                ", jobRsa='" + jobRsa + '\'' +
                ", ruleGroupId=" + ruleGroupId +
                ", dependencies='" + dependencies + '\'' +
                ", signalParameterMap=" + signalParameterMap +
                ", nodeLevel=" + nodeLevel +
                '}';
    }
}
