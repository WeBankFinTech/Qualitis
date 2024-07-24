package com.webank.wedatasphere.qualitis.scheduled.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;

import javax.persistence.*;

/**
 * @author v_minminghe@webank.com
 * @date 2022-07-15 10:00
 * @description
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_scheduled_workflow_task_relation")
public class ScheduledWorkflowTaskRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ScheduledProject scheduledProject;

    @ManyToOne
    private ScheduledWorkflow scheduledWorkflow;

    @OneToOne
    private ScheduledTask scheduledTask;

    @OneToOne
    private RuleGroup ruleGroup;


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

    public RuleGroup getRuleGroup() {
        return ruleGroup;
    }

    public void setRuleGroup(RuleGroup ruleGroup) {
        this.ruleGroup = ruleGroup;
    }

    public ScheduledTask getScheduledTask() {
        return scheduledTask;
    }

    public void setScheduledTask(ScheduledTask scheduledTask) {
        this.scheduledTask = scheduledTask;
    }
}
