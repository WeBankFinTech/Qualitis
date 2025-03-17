package com.webank.wedatasphere.qualitis.scheduled.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;

/**
 * @author v_minminghe@webank.com
 * @date 2024-07-17 9:45
 * @description
 */
//@Entity
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
//@Table(name = "qualitis_scheduled_workflow_business")
public class ScheduledWorkflowBusiness {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "business_domain")
    private String businessDomain;

    /**
     * 子系统ID
     */
    @Column(name = "sub_system_id")
    private String subSystemId;

    @Column(name = "bus_res_lvl")
    private String busResLvl;

    @Column(name = "plan_start_time")
    private String planStartTime;

    @Column(name = "plan_finish_time")
    private String planFinishTime;

    @Column(name = "last_start_time")
    private String lastStartTime;

    @Column(name = "last_finish_time")
    private String lastFinishTime;

    @Column(name = "dev_department_name")
    private String devDepartmentName;

    @Column(name = "ops_department_name")
    private String opsDepartmentName;

    @Column(name = "dev_department_id")
    private Long devDepartmentId;

    @Column(name = "ops_department_id")
    private Long opsDepartmentId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getBusinessDomain() {
        return businessDomain;
    }

    public void setBusinessDomain(String businessDomain) {
        this.businessDomain = businessDomain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubSystemId() {
        return subSystemId;
    }

    public void setSubSystemId(String subSystemId) {
        this.subSystemId = subSystemId;
    }

    public String getBusResLvl() {
        return busResLvl;
    }

    public void setBusResLvl(String busResLvl) {
        this.busResLvl = busResLvl;
    }

    public String getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(String planStartTime) {
        this.planStartTime = planStartTime;
    }

    public String getPlanFinishTime() {
        return planFinishTime;
    }

    public void setPlanFinishTime(String planFinishTime) {
        this.planFinishTime = planFinishTime;
    }

    public String getLastStartTime() {
        return lastStartTime;
    }

    public void setLastStartTime(String lastStartTime) {
        this.lastStartTime = lastStartTime;
    }

    public String getLastFinishTime() {
        return lastFinishTime;
    }

    public void setLastFinishTime(String lastFinishTime) {
        this.lastFinishTime = lastFinishTime;
    }

    public String getDevDepartmentName() {
        return devDepartmentName;
    }

    public void setDevDepartmentName(String devDepartmentName) {
        this.devDepartmentName = devDepartmentName;
    }

    public String getOpsDepartmentName() {
        return opsDepartmentName;
    }

    public void setOpsDepartmentName(String opsDepartmentName) {
        this.opsDepartmentName = opsDepartmentName;
    }

    public Long getDevDepartmentId() {
        return devDepartmentId;
    }

    public void setDevDepartmentId(Long devDepartmentId) {
        this.devDepartmentId = devDepartmentId;
    }

    public Long getOpsDepartmentId() {
        return opsDepartmentId;
    }

    public void setOpsDepartmentId(Long opsDepartmentId) {
        this.opsDepartmentId = opsDepartmentId;
    }
}
