package com.webank.wedatasphere.qualitis.project.response;

import com.webank.wedatasphere.qualitis.scheduled.entity.ScheduledTask;
import org.springframework.beans.BeanUtils;

/**
 * @author v_minminghe@webank.com
 * @date 2023-04-21 16:21
 * @description
 */
public class ScheduledTaskResponse {

    private String clusterName;

    /**
     * 调度系统类型(1WTSS)
     */
    private String dispatchingSystemType;

    /**
     * 项目
     */
    private String wtssProjectName;

    /**
     * 工作流
     */
    private String workFlowName;

    /**
     * 任务类型 1.关联；2.发布定时任务
     */
    private Integer taskType;

    /**
     * 任务
     */
    private String taskName;

    private String projectName;

    private String approveNumber;

    /**
     * 创建人
     */
    private String createUser;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 修改人
     */
    private String modifyUser;

    /**
     * 修改时间
     */
    private String modifyTime;

    /**
     * 发布用户
     */
    private String releaseUser;

    /**
     * 发布状态
     */
    private Integer releaseStatus;

    public ScheduledTaskResponse() {
//        Doing something
    }

    public ScheduledTaskResponse(ScheduledTask scheduledTask) {
        BeanUtils.copyProperties(scheduledTask, this);
        this.projectName = scheduledTask.getProject().getName();
        this.wtssProjectName = scheduledTask.getProjectName();
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getReleaseStatus() {
        return releaseStatus;
    }

    public void setReleaseStatus(Integer releaseStatus) {
        this.releaseStatus = releaseStatus;
    }

    public String getApproveNumber() {
        return approveNumber;
    }

    public void setApproveNumber(String approveNumber) {
        this.approveNumber = approveNumber;
    }

    public String getDispatchingSystemType() {
        return dispatchingSystemType;
    }

    public void setDispatchingSystemType(String dispatchingSystemType) {
        this.dispatchingSystemType = dispatchingSystemType;
    }

    public String getWtssProjectName() {
        return wtssProjectName;
    }

    public void setWtssProjectName(String wtssProjectName) {
        this.wtssProjectName = wtssProjectName;
    }

    public String getWorkFlowName() {
        return workFlowName;
    }

    public void setWorkFlowName(String workFlowName) {
        this.workFlowName = workFlowName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
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

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getReleaseUser() {
        return releaseUser;
    }

    public void setReleaseUser(String releaseUser) {
        this.releaseUser = releaseUser;
    }
}
