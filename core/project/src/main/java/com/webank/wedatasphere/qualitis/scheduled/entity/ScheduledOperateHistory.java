package com.webank.wedatasphere.qualitis.scheduled.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;

/**
 * @author v_minminghe@webank.com
 * @date 2023-04-13 17:31
 * @description
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_scheduled_operate_history")
public class ScheduledOperateHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cluster_name")
    private String clusterName;

    /**
     * 调度系统类型(1WTSS)
     */
    @Column(name = "dispatching_system_type")
    private String dispatchingSystemType;

    /**
     * 任务类型 1.关联；2.发布定时任务
     */
    @Column(name = "task_type")
    private Integer taskType;

    /**
     * 项目
     */
    @Column(name = "wtss_project_name")
    private String wtssProjectName;

    /**
     * 工作流
     */
    @Column(name = "wtss_work_flow_name")
    private String workFlowName;

    /**
     * 任务
     */
    @Column(name = "wtss_task_name")
    private String taskName;

    @Column(name = "approve_number")
    private String approveNumber;

    @Column(name = "progress_status")
    private Integer progressStatus;

    @Column(name = "operate_type")
    private Integer operateType;

    @Column(name = "error_message")
    private String errorMessage;

    /**
     * 创建人
     */
    @Column(name = "create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private String createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(Integer progressStatus) {
        this.progressStatus = progressStatus;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public String getWtssProjectName() {
        return wtssProjectName;
    }

    public void setWtssProjectName(String wtssProjectName) {
        this.wtssProjectName = wtssProjectName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getDispatchingSystemType() {
        return dispatchingSystemType;
    }

    public void setDispatchingSystemType(String dispatchingSystemType) {
        this.dispatchingSystemType = dispatchingSystemType;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
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

    public String getApproveNumber() {
        return approveNumber;
    }

    public void setApproveNumber(String approveNumber) {
        this.approveNumber = approveNumber;
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
}
