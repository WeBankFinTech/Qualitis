package com.webank.wedatasphere.qualitis.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2024-05-15 10:48
 * @description
 */
public class AddMetricSchedulerRequest {

    @JsonProperty(value = "cluster_name", required = true)
    private String clusterName;
    @JsonProperty(value = "database", required = true)
    private String database;
    @JsonProperty(value = "table", required = true)
    private String table;
    @JsonProperty("scheduler_configs")
    private List<SchedulerConfig> schedulerConfigs;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public List<SchedulerConfig> getSchedulerConfigs() {
        return schedulerConfigs;
    }

    public void setSchedulerConfigs(List<SchedulerConfig> schedulerConfigs) {
        this.schedulerConfigs = schedulerConfigs;
    }

    public static class SchedulerConfig {
        @JsonProperty(value = "partition", required = true)
        private String partition;
        @JsonProperty(value = "execute_interval", required = true)
        private String executeInterval;

        @JsonProperty(value = "execute_date_in_interval")
        private Integer executeDateInInterval;

        @JsonProperty(value = "execute_time_in_date", required = true)
        private String executeTimeInDate;

        public String getPartition() {
            return partition;
        }

        public void setPartition(String partition) {
            this.partition = partition;
        }

        public String getExecuteInterval() {
            return executeInterval;
        }

        public void setExecuteInterval(String executeInterval) {
            this.executeInterval = executeInterval;
        }

        public Integer getExecuteDateInInterval() {
            return executeDateInInterval;
        }

        public void setExecuteDateInInterval(Integer executeDateInInterval) {
            this.executeDateInInterval = executeDateInInterval;
        }

        public String getExecuteTimeInDate() {
            return executeTimeInDate;
        }

        public void setExecuteTimeInDate(String executeTimeInDate) {
            this.executeTimeInDate = executeTimeInDate;
        }
    }
}
