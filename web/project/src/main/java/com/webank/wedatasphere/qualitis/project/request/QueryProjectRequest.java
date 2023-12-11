package com.webank.wedatasphere.qualitis.project.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * @author v_minminghe@webank.com
 * @date 2022-06-02 14:21
 * @description
 */
public class QueryProjectRequest extends PageRequest {

    @JsonProperty("project_name")
    private String projectName;
    @JsonProperty(value = "subsystem_id")
    private Integer subsystemId;
    @JsonProperty(value = "subsystem_name")
    private String subsystemName;
    @JsonProperty("db_name")
    private String db;
    @JsonProperty("table_name")
    private String table;
    @JsonProperty("create_user")
    private String createUser;
    @JsonProperty("start_time")
    private Long startTime;
    @JsonProperty("end_time")
    private Long endTime;

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getSubsystemId() {
        return subsystemId;
    }

    public void setSubsystemId(Integer subsystemId) {
        this.subsystemId = subsystemId;
    }

    public String getSubsystemName() {
        return subsystemName;
    }

    public void setSubsystemName(String subsystemName) {
        this.subsystemName = subsystemName;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public void convertParameter(){
        this.db = StringUtils.trimToNull(this.db);
        this.table = StringUtils.trimToNull(this.table);
        this.createUser = StringUtils.trimToNull(this.createUser);
        if (StringUtils.isEmpty(this.projectName)) {
            this.projectName = "%";
        } else {
            this.projectName = "%" +this.projectName+ "%";
        }
    }
}
