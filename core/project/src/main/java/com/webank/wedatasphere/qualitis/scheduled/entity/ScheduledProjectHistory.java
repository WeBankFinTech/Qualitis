package com.webank.wedatasphere.qualitis.scheduled.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-14 16:29
 * @description
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_scheduled_project_history")
public class ScheduledProjectHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long scheduledProjectId;

    @Column
    private String scheduledProjectName;

    @Column
    private String approveNumber;

    @Column
    private String releaseUser;

    @Column
    private String createTime;

    @Column
    private String createUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getScheduledProjectId() {
        return scheduledProjectId;
    }

    public void setScheduledProjectId(Long scheduledProjectId) {
        this.scheduledProjectId = scheduledProjectId;
    }

    public String getScheduledProjectName() {
        return scheduledProjectName;
    }

    public void setScheduledProjectName(String scheduledProjectName) {
        this.scheduledProjectName = scheduledProjectName;
    }

    public String getApproveNumber() {
        return approveNumber;
    }

    public void setApproveNumber(String approveNumber) {
        this.approveNumber = approveNumber;
    }

    public String getReleaseUser() {
        return releaseUser;
    }

    public void setReleaseUser(String releaseUser) {
        this.releaseUser = releaseUser;
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
}
