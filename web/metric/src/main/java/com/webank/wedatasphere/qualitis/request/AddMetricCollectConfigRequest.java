package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2024-05-14 10:53
 * @description
 */
public class AddMetricCollectConfigRequest {

    @JsonProperty("collect_type")
    private boolean isManualCollect;
    @JsonProperty("execution_parameters_name")
    private String executionParametersName;
    @JsonProperty("calcu_unit_configs")
    private List<AddMetricCalcuUnitConfigRequest> metricCalcuUnitConfigRequestList;

    public boolean isManualCollect() {
        return isManualCollect;
    }

    public void setManualCollect(boolean manualCollect) {
        isManualCollect = manualCollect;
    }

    public String getExecutionParametersName() {
        return executionParametersName;
    }

    public void setExecutionParametersName(String executionParametersName) {
        this.executionParametersName = executionParametersName;
    }

    public List<AddMetricCalcuUnitConfigRequest> getMetricCalcuUnitConfigRequestList() {
        return metricCalcuUnitConfigRequestList;
    }

    public void setMetricCalcuUnitConfigRequestList(List<AddMetricCalcuUnitConfigRequest> metricCalcuUnitConfigRequestList) {
        this.metricCalcuUnitConfigRequestList = metricCalcuUnitConfigRequestList;
    }
}
