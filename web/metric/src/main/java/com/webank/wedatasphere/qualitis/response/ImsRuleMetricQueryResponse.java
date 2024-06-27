package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * @author v_wenxuanzhang
 */
public class ImsRuleMetricQueryResponse {
    @JsonProperty("metricId")
    private Long metricId;
    @JsonProperty("metricType")
    private int metricType;
    @JsonProperty("metricName")
    private String metricName;
    private LineDataList lineDataList;

    public Long getMetricId() {
        return metricId;
    }

    public void setMetricId(Long metricId) {
        this.metricId = metricId;
    }

    public int getMetricType() {
        return metricType;
    }

    public void setMetricType(int metricType) {
        this.metricType = metricType;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public LineDataList getLineDataList() {
        return lineDataList;
    }

    public void setLineDataList(LineDataList lineDataList) {
        this.lineDataList = lineDataList;
    }
}
