package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author v_minminghe@webank.com
 * @date 2024-08-15 14:20
 * @description
 */
public class ImsMetricTaskStatusQueryRequest {
    @JsonProperty("metric_id")
    private Long metricId;
    /**
     * example: 20240819
     */
    @JsonProperty("data_date")
    private String dataDate;

    public Long getMetricId() {
        return metricId;
    }

    public void setMetricId(Long metricId) {
        this.metricId = metricId;
    }

    public String getDataDate() {
        return dataDate;
    }

    public void setDataDate(String dataDate) {
        this.dataDate = dataDate;
    }
}
