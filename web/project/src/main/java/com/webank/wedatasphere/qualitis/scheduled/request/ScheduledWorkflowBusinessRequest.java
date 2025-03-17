package com.webank.wedatasphere.qualitis.scheduled.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;

/**
 * @author v_minminghe@webank.com
 * @date 2024-07-17 9:57
 * @description
 */
public class ScheduledWorkflowBusinessRequest {

    private Long id;

    private String name;

    @JsonProperty("project_id")
    private Long projectId;

    @JsonProperty("business_domain")
    private String businessDomain;

    @JsonProperty("sub_system_id")
    private String subSystemId;

    @JsonProperty("bus_res_lvl")
    private String busResLvl;

    @JsonProperty("plan_start_time")
    private String planStartTime;

    @JsonProperty("plan_finish_time")
    private String planFinishTime;

    @JsonProperty("last_start_time")
    private String lastStartTime;

    @JsonProperty("last_finish_time")
    private String lastFinishTime;

    @JsonProperty("dev_department_name")
    private String devDepartmentName;

    @JsonProperty("ops_department_name")
    private String opsDepartmentName;

    @JsonProperty("dev_department_id")
    private Long devDepartmentId;

    @JsonProperty("ops_department_id")
    private Long opsDepartmentId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public void checkRequest() throws UnExpectedRequestException {
        CommonChecker.checkObject(this.projectId, "project_id");
        CommonChecker.checkString(this.businessDomain, "business_domain");
        CommonChecker.checkString(this.subSystemId, "sub_system_id");
        CommonChecker.checkString(this.busResLvl, "bus_res_lvl");
        CommonChecker.checkString(this.planStartTime, "plan_start_time");
        CommonChecker.checkString(this.planFinishTime, "plan_finish_time");
        CommonChecker.checkString(this.lastStartTime, "last_start_time");
        CommonChecker.checkString(this.lastFinishTime, "last_finish_time");
        CommonChecker.checkString(this.devDepartmentName, "dev_department_name");
        CommonChecker.checkObject(this.devDepartmentId, "dev_department_id");
        CommonChecker.checkString(this.opsDepartmentName, "ops_department_name");
        CommonChecker.checkObject(this.opsDepartmentId, "ops_department_id");
    }
}
