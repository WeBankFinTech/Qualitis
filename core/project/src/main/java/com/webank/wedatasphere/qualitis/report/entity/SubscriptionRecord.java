package com.webank.wedatasphere.qualitis.report.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author v_gaojiedeng@webank.com
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_subscription_record")
public class SubscriptionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private Project project;

    @Column(name = "execution_frequency")
    private Integer executionFrequency;

    @Column(name = "configured_rules_table_num")
    private Long configuredRulesTableNum;
    @Column(name = "configured_rules_num")
    private Long configuredRulesNum;
    @Column(name = "configured_rules_kpi_table_num")
    private Long configuredRulesKpiTableNum;
    @Column(name = "configured_rules_kpi_num")
    private Long configuredRulesKpiNum;

    @Column(name = "scheduling_rules")
    private Long schedulingRules;
    @Column(name = "pass_rules")
    private Long passRules;
    @Column(name = "no_pass_rules")
    private Long noPassRules;
    @Column(name = "fail_rules")
    private Long failRules;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Integer getExecutionFrequency() {
        return executionFrequency;
    }

    public void setExecutionFrequency(Integer executionFrequency) {
        this.executionFrequency = executionFrequency;
    }

    public Long getConfiguredRulesTableNum() {
        return configuredRulesTableNum;
    }

    public void setConfiguredRulesTableNum(Long configuredRulesTableNum) {
        this.configuredRulesTableNum = configuredRulesTableNum;
    }

    public Long getConfiguredRulesNum() {
        return configuredRulesNum;
    }

    public void setConfiguredRulesNum(Long configuredRulesNum) {
        this.configuredRulesNum = configuredRulesNum;
    }

    public Long getConfiguredRulesKpiTableNum() {
        return configuredRulesKpiTableNum;
    }

    public void setConfiguredRulesKpiTableNum(Long configuredRulesKpiTableNum) {
        this.configuredRulesKpiTableNum = configuredRulesKpiTableNum;
    }

    public Long getConfiguredRulesKpiNum() {
        return configuredRulesKpiNum;
    }

    public void setConfiguredRulesKpiNum(Long configuredRulesKpiNum) {
        this.configuredRulesKpiNum = configuredRulesKpiNum;
    }

    public Long getSchedulingRules() {
        return schedulingRules;
    }

    public void setSchedulingRules(Long schedulingRules) {
        this.schedulingRules = schedulingRules;
    }

    public Long getPassRules() {
        return passRules;
    }

    public void setPassRules(Long passRules) {
        this.passRules = passRules;
    }

    public Long getNoPassRules() {
        return noPassRules;
    }

    public void setNoPassRules(Long noPassRules) {
        this.noPassRules = noPassRules;
    }

    public Long getFailRules() {
        return failRules;
    }

    public void setFailRules(Long failRules) {
        this.failRules = failRules;
    }

    @Override
    public String toString() {
        return "SubscriptionRecord{" +
                "id=" + id +
                ", project=" + project +
                ", executionFrequency=" + executionFrequency +
                ", configuredRulesTableNum=" + configuredRulesTableNum +
                ", configuredRulesNum=" + configuredRulesNum +
                ", configuredRulesKpiTableNum=" + configuredRulesKpiTableNum +
                ", configuredRulesKpiNum=" + configuredRulesKpiNum +
                ", schedulingRules=" + schedulingRules +
                ", passRules=" + passRules +
                ", noPassRules=" + noPassRules +
                ", failRules=" + failRules +
                '}';
    }
}
