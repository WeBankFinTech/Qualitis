package com.webank.wedatasphere.qualitis.report.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author v_gaojiedeng@webank.com
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_subscribe_operate_report_associated_projects")
public class SubscribeOperateReportProjects {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private Project project;

    @ManyToOne
    @JoinColumn(name = "operate_report_id")
    private SubscribeOperateReport subscribeOperateReport;

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

    public SubscribeOperateReport getSubscribeOperateReport() {
        return subscribeOperateReport;
    }

    public void setSubscribeOperateReport(SubscribeOperateReport subscribeOperateReport) {
        this.subscribeOperateReport = subscribeOperateReport;
    }

    @Override
    public String toString() {
        return "SubscribeOperateReportProjects{" +
                "id=" + id +
                ", project=" + project +
                ", subscribeOperateReport=" + subscribeOperateReport +
                '}';
    }
}
