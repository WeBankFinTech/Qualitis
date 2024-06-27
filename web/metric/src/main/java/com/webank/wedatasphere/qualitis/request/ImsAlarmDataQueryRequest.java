package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * @author v_wenxuanzhang
 */
public class ImsAlarmDataQueryRequest {
    @JsonProperty("metricId")
    private String metricId;
    @JsonProperty("startDate")
    private int startDate;
    @JsonProperty("endDate")
    private int endDate;

    public String getMetricId() {
        return metricId;
    }

    public void setMetricId(String metricId) {
        this.metricId = metricId;
    }

    public int getStartDate() {
        return startDate;
    }

    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }

    public int getEndDate() {
        return endDate;
    }

    public void setEndDate(int endDate) {
        this.endDate = endDate;
    }

}
