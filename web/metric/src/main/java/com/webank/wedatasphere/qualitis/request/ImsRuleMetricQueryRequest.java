package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;


/**
 * @author v_wenxuanzhang
 */
public class ImsRuleMetricQueryRequest {

    @JsonProperty("metricId")
    private String metricId;
    @JsonProperty("startDate")
    private int startDate;
    @JsonProperty("endDate")
    private int endDate;
    @JsonProperty("requestSource")
    private String requestSource;
    private String username;
    @JsonProperty("executeUser")
    private String executeUser;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getExecuteUser() {
        return executeUser;
    }

    public void setExecuteUser(String executeUser) {
        this.executeUser = executeUser;
    }

    public String getRequestSource() {
        return requestSource;
    }

    public void setRequestSource(String requestSource) {
        this.requestSource = requestSource;
    }

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

    public void checkRequest() throws UnExpectedRequestException {
        CommonChecker.checkString(this.metricId, "metricId");
        CommonChecker.checkString(this.username, "username");
    }

    @Override
    public String toString() {
        return "ImsRuleMetricQueryRequest{" +
                "metricId='" + metricId + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", requestSource='" + requestSource + '\'' +
                ", username='" + username + '\'' +
                ", executeUser='" + executeUser + '\'' +
                '}';
    }
}
