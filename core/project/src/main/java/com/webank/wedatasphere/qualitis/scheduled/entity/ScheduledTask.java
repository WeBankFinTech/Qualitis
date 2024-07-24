package com.webank.wedatasphere.qualitis.scheduled.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_scheduled_task")
public class ScheduledTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToMany(mappedBy = "scheduledTask", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private Set<ScheduledFrontBackRule> scheduledFrontBackRule;

    @JsonIgnore
    @OneToOne(mappedBy = "scheduledTask", fetch = FetchType.EAGER)
    private ScheduledWorkflowTaskRelation scheduledWorkflowTaskRelation;

    @Column(name = "cluster_name")
    private String clusterName;

    /**
     * 调度系统类型(1WTSS)
     */
    @Column(name = "dispatching_system_type")
    private String dispatchingSystemType;

    /**
     * 项目
     */
    @Column(name = "wtss_project_name")
    private String projectName;

    /**
     * 工作流
     */
    @Column(name = "wtss_work_flow_name")
    private String workFlowName;

    /**
     * 任务类型 1.关联；2.发布定时任务
     */
    @Column(name = "task_type")
    private Integer taskType;

    /**
     * 任务
     */
    @Column(name = "wtss_task_name")
    private String taskName;

    @ManyToOne
    @JsonIgnore
    private Project project;

    @Column(name = "approve_number")
    private String approveNumber;

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

    /**
     * 修改人
     */
    @Column(name = "modify_user")
    private String modifyUser;

    /**
     * 修改时间
     */
    @Column(name = "modify_time")
    private String modifyTime;

    /**
     * 发布用户
     */
    @Column(name = "release_user")
    private String releaseUser;

    /**
     * 发布状态
     * 0-未发布，1-已发布，2-发布失败
     */
    @Column(name = "release_status")
    private Integer releaseStatus;

    public ScheduledTask() {
        // Do nothing because of X and Y.
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<ScheduledFrontBackRule> getScheduledFrontBackRule() {
        return scheduledFrontBackRule;
    }

    public void setScheduledFrontBackRule(Set<ScheduledFrontBackRule> scheduledFrontBackRule) {
        this.scheduledFrontBackRule = scheduledFrontBackRule;
    }

    public ScheduledWorkflowTaskRelation getScheduledWorkflowTaskRelation() {
        return scheduledWorkflowTaskRelation;
    }

    public void setScheduledWorkflowTaskRelation(ScheduledWorkflowTaskRelation scheduledWorkflowTaskRelation) {
        this.scheduledWorkflowTaskRelation = scheduledWorkflowTaskRelation;
    }

    public String getDispatchingSystemType() {
        return dispatchingSystemType;
    }

    public void setDispatchingSystemType(String dispatchingSystemType) {
        this.dispatchingSystemType = dispatchingSystemType;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getReleaseUser() {
        return releaseUser;
    }

    public void setReleaseUser(String releaseUser) {
        this.releaseUser = releaseUser;
    }
}
