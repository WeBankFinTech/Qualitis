package com.webank.wedatasphere.qualitis.scheduled.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;

/**
 * @author v_minminghe@webank.com
 * @date 2022-11-02 14:53
 * @description
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_scheduled_signal")
public class ScheduledSignal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ScheduledProject scheduledProject;

    @ManyToOne
    private ScheduledWorkflow scheduledWorkflow;

    @Column
    private String ruleGroupIds;

    @Column
    private Integer type;

    @Column
    private String name;

    @Column
    private String contentJson;

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

    public ScheduledWorkflow getScheduledWorkflow() {
        return scheduledWorkflow;
    }

    public void setScheduledWorkflow(ScheduledWorkflow scheduledWorkflow) {
        this.scheduledWorkflow = scheduledWorkflow;
    }

    public String getRuleGroupIds() {
        return ruleGroupIds;
    }

    public void setRuleGroupIds(String ruleGroupIds) {
        this.ruleGroupIds = ruleGroupIds;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentJson() {
        return contentJson;
    }

    public void setContentJson(String contentJson) {
        this.contentJson = contentJson;
    }
}
