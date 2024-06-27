package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OmnisScriptRequest {

    @JsonProperty("metricIds")
    private String metricIds;

    @JsonProperty("startDate")
    private String startDate;

    @JsonProperty("endDate")
    private String endDate;

    @JsonProperty("udfName")
    private String udfName;

    @JsonProperty("runModel")
    private Integer runModel;

    @JsonProperty("envPath")
    private String envPath;

    @JsonProperty("uploadPath")
    private String uploadPath;

    @JsonProperty("queryDate")
    private String queryDate;

    @JsonProperty("appId")
    private String appId;

    @JsonProperty("qualitisHost")
    private String qualitisHost;

    @JsonProperty("param")
    private String param;

    public String getQualitisHost() {
        return qualitisHost;
    }

    public void setQualitisHost(String qualitisHost) {
        this.qualitisHost = qualitisHost;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getQueryDate() {
        return queryDate;
    }

    public void setQueryDate(String queryDate) {
        this.queryDate = queryDate;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public String getMetricIds() {
        return metricIds;
    }

    public void setMetricIds(String metricIds) {
        this.metricIds = metricIds;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getUdfName() {
        return udfName;
    }

    public void setUdfName(String udfName) {
        this.udfName = udfName;
    }

    public Integer getRunModel() {
        return runModel;
    }

    public void setRunModel(Integer runModel) {
        this.runModel = runModel;
    }

    public String getEnvPath() {
        return envPath;
    }

    public void setEnvPath(String envPath) {
        this.envPath = envPath;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "OmnisScriptRequest{" +
                "metricIds='" + metricIds + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", udfName='" + udfName + '\'' +
                ", runModel=" + runModel +
                ", envPath='" + envPath + '\'' +
                ", uploadPath='" + uploadPath + '\'' +
                ", queryDate='" + queryDate + '\'' +
                ", appId='" + appId + '\'' +
                ", qualitisHost='" + qualitisHost + '\'' +
                ", param='" + param + '\'' +
                '}';
    }
}
