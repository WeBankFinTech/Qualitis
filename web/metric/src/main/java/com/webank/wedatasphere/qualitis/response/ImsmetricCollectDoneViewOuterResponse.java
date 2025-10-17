package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author
 * @date 2025-03-03 9:43
 * @description
 */
public class ImsmetricCollectDoneViewOuterResponse {

    @JsonProperty("metric_info_list")
    private List<MetricInfo> metricInfoList;

    @JsonProperty("start_time")
    private String startTime;

    @JsonProperty("end_time")
    private String endTime;

    public static class MetricInfo {

        private Long id;

        @JsonProperty("enum_value")
        private String enumValue;

        @JsonProperty("proxy_user")
        private String proxyUser;

        @JsonProperty("db_name")
        private String dbName;

        @JsonProperty("table_name")
        private String tableName;

        @JsonProperty("calcu_en_name")
        private String calcuEnName;

        @JsonProperty("calcu_cn_name")
        private String calcuCnName;

        @JsonProperty("partition_value")
        private String partition;

        @JsonProperty("dep_name")
        private String depName;

        private String column;

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        public String getDepName() {
            return depName;
        }

        public void setDepName(String depName) {
            this.depName = depName;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getEnumValue() {
            return enumValue;
        }

        public void setEnumValue(String enumValue) {
            this.enumValue = enumValue;
        }

        public String getProxyUser() {
            return proxyUser;
        }

        public void setProxyUser(String proxyUser) {
            this.proxyUser = proxyUser;
        }

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getCalcuEnName() {
            return calcuEnName;
        }

        public void setCalcuEnName(String calcuEnName) {
            this.calcuEnName = calcuEnName;
        }

        public String getCalcuCnName() {
            return calcuCnName;
        }

        public void setCalcuCnName(String calcuCnName) {
            this.calcuCnName = calcuCnName;
        }

        public String getPartition() {
            return partition;
        }

        public void setPartition(String partition) {
            this.partition = partition;
        }
    }

    public List<MetricInfo> getMetricInfoList() {
        return metricInfoList;
    }

    public void setMetricInfoList(List<MetricInfo> metricInfoList) {
        this.metricInfoList = metricInfoList;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
