package com.webank.wedatasphere.qualitis.scheduled.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.*;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-13 18:02
 * @description
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_scheduled_workflow")
public class ScheduledWorkflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ScheduledProject scheduledProject;

    @Column
    private String name;

    @Column
    private String proxyUser;

    @Column(name = "scheduled_type")
    private String scheduledType;

    @Column(name = "execute_interval")
    private String executeInterval;

    @Column(name = "execute_date_in_interval")
    private Integer executeDateInInterval;

    @Column(name = "execute_time_in_date")
    private String executeTimeInDate;

    @Column(name = "schedule_id")
    private Long scheduleId;

    @Column(name = "scheduled_signal_json")
    private String scheduledSignalJson;

    @Column(name = "workflow_business_name")
    private String workflowBusinessName;

    @Column
    private String createTime;

    @Column
    private String createUser;

    @Column
    private String modifyTime;

    @Column
    private String modifyUser;

    public String getWorkflowBusinessName() {
        return workflowBusinessName;
    }

    public void setWorkflowBusinessName(String workflowBusinessName) {
        this.workflowBusinessName = workflowBusinessName;
    }

    public String getScheduledSignalJson() {
        return scheduledSignalJson;
    }

    public void setScheduledSignalJson(String scheduledSignalJson) {
        this.scheduledSignalJson = scheduledSignalJson;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ScheduledProject getScheduledProject() {
        return scheduledProject;
    }

    public void setScheduledProject(ScheduledProject scheduledProject) {
        this.scheduledProject = scheduledProject;
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

    public String getScheduledType() {
        return scheduledType;
    }

    public void setScheduledType(String scheduledType) {
        this.scheduledType = scheduledType;
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

    public Map<String, Object> getScheduledSignalJsonMap(){
        if (Strings.isNotEmpty(scheduledSignalJson)) {
            try {
                return new ObjectMapper().readValue(scheduledSignalJson, Map.class);
            } catch (IOException e) {
                //No exception handing
            }
        }
        return Collections.emptyMap();
    }

}
