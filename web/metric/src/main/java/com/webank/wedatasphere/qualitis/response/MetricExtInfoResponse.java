package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2024-11-19 9:56
 * @description
 */
public class MetricExtInfoResponse {

    // 拓展字段
    @JsonProperty("calculation_mode")
    private String calculationMode;
    @JsonProperty("monitoring_capabilities")
    private List<String> monitoringCapabilities;
    @JsonProperty("metric_definition")
    private String metricDefinition;
    @JsonProperty("business_domain")
    private String businessDomain;
    @JsonProperty("business_strategy")
    private String businessStrategy;
    @JsonProperty("business_system")
    private String businessSystem;
    @JsonProperty("business_model")
    private String businessModel;
    @JsonProperty("imsmetric_desc")
    private String imsmetricDesc;

    public String getImsmetricDesc() {
        return imsmetricDesc;
    }

    public void setImsmetricDesc(String imsmetricDesc) {
        this.imsmetricDesc = imsmetricDesc;
    }

    public String getCalculationMode() {
        return calculationMode;
    }

    public void setCalculationMode(String calculationMode) {
        this.calculationMode = calculationMode;
    }

    public List<String> getMonitoringCapabilities() {
        return monitoringCapabilities;
    }

    public void setMonitoringCapabilities(List<String> monitoringCapabilities) {
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
}
