package com.webank.wedatasphere.qualitis.entity;

import java.util.List;

/**
 * @author allenzhou@webank.com
 * @date 2021/4/26 20:55
 */
public class ReportBatchInfo {
    private String userAuthKey;
    private List<MetricData> metricDataList;

    public ReportBatchInfo() {
        // Do nothing.
    }

    public String getUserAuthKey() {
        return userAuthKey;
    }

    public void setUserAuthKey(String userAuthKey) {
        this.userAuthKey = userAuthKey;
    }

    public List<MetricData> getMetricDataList() {
        return metricDataList;
    }

    public void setMetricDataList(List<MetricData> metricDataList) {
        this.metricDataList = metricDataList;
    }
}
