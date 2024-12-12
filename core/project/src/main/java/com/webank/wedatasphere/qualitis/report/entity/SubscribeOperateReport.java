package com.webank.wedatasphere.qualitis.report.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

/**
 * @author v_gaojiedeng@webank.com
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_subscribe_operate_report")
public class SubscribeOperateReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String receiver;

    @Column(name = "execution_frequency")
    private Integer executionFrequency;
    @Column(name = "create_user", length = 50)
    private String createUser;
    @Column(name = "create_time", length = 25)
    private String createTime;
    @Column(name = "modify_user", length = 50)
    private String modifyUser;
    @Column(name = "modify_time", length = 25)
    private String modifyTime;

    @OneToMany(mappedBy = "subscribeOperateReport", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<SubscribeOperateReportProjects> subscribeOperateReportProjectsSet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Integer getExecutionFrequency() {
        return executionFrequency;
    }

    public void setExecutionFrequency(Integer executionFrequency) {
        this.executionFrequency = executionFrequency;
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

    public Set<SubscribeOperateReportProjects> getSubscribeOperateReportProjectsSet() {
        return subscribeOperateReportProjectsSet;
    }

    public void setSubscribeOperateReportProjectsSet(Set<SubscribeOperateReportProjects> subscribeOperateReportProjectsSet) {
        this.subscribeOperateReportProjectsSet = subscribeOperateReportProjectsSet;
    }

    @Override
    public String toString() {
        return "SubscribeOperateReport{" +
                "id=" + id +
                ", receiver='" + receiver + '\'' +
                ", executionFrequency='" + executionFrequency + '\'' +
                ", createUser='" + createUser + '\'' +
                ", createTime='" + createTime + '\'' +
                ", modifyUser='" + modifyUser + '\'' +
                ", modifyTime='" + modifyTime + '\'' +
                '}';
    }
}
