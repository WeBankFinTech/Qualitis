package com.webank.wedatasphere.qualitis.rule.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author v_gaojiedeng@webank.com
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_noise_elimination_management")
public class NoiseEliminationManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_selection_method")
    private Integer dateSelectionMethod;

    @Column(name = "business_date", columnDefinition = "MEDIUMTEXT")
    private String businessDate;

    @Column(name = "template_id")
    private Long templateId;

    @Column(name = "noise_norm_ratio", columnDefinition = "MEDIUMTEXT")
    private String noiseNormRatio;

    @Column(name = "eliminate_strategy")
    private Integer eliminateStrategy;

    @Column(name = "available")
    private Boolean available;

    @ManyToOne
    @JsonIgnore
    private ExecutionParameters executionParameters;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDateSelectionMethod() {
        return dateSelectionMethod;
    }

    public void setDateSelectionMethod(Integer dateSelectionMethod) {
        this.dateSelectionMethod = dateSelectionMethod;
    }

    public String getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getNoiseNormRatio() {
        return noiseNormRatio;
    }

    public void setNoiseNormRatio(String noiseNormRatio) {
        this.noiseNormRatio = noiseNormRatio;
    }

    public Integer getEliminateStrategy() {
        return eliminateStrategy;
    }

    public void setEliminateStrategy(Integer eliminateStrategy) {
        this.eliminateStrategy = eliminateStrategy;
    }

    public ExecutionParameters getExecutionParameters() {
        return executionParameters;
    }

    public void setExecutionParameters(ExecutionParameters executionParameters) {
        this.executionParameters = executionParameters;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
