package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author v_minminghe@webank.com
 * @date 2024-11-18 11:47
 * @description
 */
@Entity
@Table(name = "qualitis_metric_ext")
public class MetricExtInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "metric_id")
    private Long metricId;

    /**
     * rule、ims
     */
    @Column(name = "metric_class")
    private String metricClass;

    @Column(name = "calculation_mode")
    private String calculationMode;

    @Column(name = "monitoring_capabilities")
    private String monitoringCapabilities;

    @Column(name = "metric_definition")
    private String metricDefinition;

    @Column(name = "business_domain")
    private String businessDomain;

    @Column(name = "business_strategy")
    private String businessStrategy;

    @Column(name = "business_system")
    private String businessSystem;

    @Column(name = "business_model")
    private String businessModel;

    @Column(name = "imsmetric_desc")
    private String imsmetricDesc;

    @Column(name = "create_user", length = 50)
    private String createUser;
    @Column(name = "create_time", length = 25)
    private Date createTime;
    @Column(name = "modify_user", length = 50)
    private String modifyUser;
    @Column(name = "modify_time", length = 25)
    private Date modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMetricId() {
        return metricId;
    }

    public void setMetricId(Long metricId) {
        this.metricId = metricId;
    }

    public String getMetricClass() {
        return metricClass;
    }

    public void setMetricClass(String metricClass) {
        this.metricClass = metricClass;
    }

    public String getCalculationMode() {
        return calculationMode;
    }

    public void setCalculationMode(String calculationMode) {
        this.calculationMode = calculationMode;
    }

    public String getMonitoringCapabilities() {
        return monitoringCapabilities;
    }

    public void setMonitoringCapabilities(String monitoringCapabilities) {
        this.monitoringCapabilities = monitoringCapabilities;
    }

    public String getMetricDefinition() {
        return metricDefinition;
    }

    public void setMetricDefinition(String metricDefinition) {
        this.metricDefinition = metricDefinition;
    }

    public String getBusinessDomain() {
        return businessDomain;
    }

    public void setBusinessDomain(String businessDomain) {
        this.businessDomain = businessDomain;
    }

    public String getBusinessStrategy() {
        return businessStrategy;
    }

    public void setBusinessStrategy(String businessStrategy) {
        this.businessStrategy = businessStrategy;
    }

    public String getBusinessSystem() {
        return businessSystem;
    }

    public void setBusinessSystem(String businessSystem) {
        this.businessSystem = businessSystem;
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    public String getImsmetricDesc() {
        return imsmetricDesc;
    }

    public void setImsmetricDesc(String imsmetricDesc) {
        this.imsmetricDesc = imsmetricDesc;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
