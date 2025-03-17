package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.entity.ImsMetricScheduler;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.scheduled.util.CronUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * @author v_minminghe@webank.com
 * @date 2024-05-15 10:39
 * @description
 */
public class MetricSchedulerDetailResponse {

    private Long id;
    @JsonProperty("exec_freq")
    private String execFreq;
    private String database;
    private String table;
    @JsonProperty(value = "execute_interval")
    private String executeInterval;

    @JsonProperty(value = "execute_date_in_interval")
    private Integer executeDateInInterval;

    @JsonProperty(value = "execute_time_in_date")
    private String executeTimeInDate;

    public MetricSchedulerDetailResponse() {
//        doing nothing
    }

    public MetricSchedulerDetailResponse(ImsMetricScheduler imsMetricScheduler) {
        this.id = imsMetricScheduler.getId();
        this.execFreq = imsMetricScheduler.getExecFreq();
        this.database = imsMetricScheduler.getDbName();
        this.table = imsMetricScheduler.getTableName();
        if (StringUtils.isNotBlank(imsMetricScheduler.getExecFreq()) && !imsMetricScheduler.getExecFreq().endsWith("2099")) {
            try {
                String[] cronTextArray = CronUtil.cronToText(imsMetricScheduler.getExecFreq());
                this.executeInterval = cronTextArray[0];
                this.executeDateInInterval = StringUtils.isNotBlank(cronTextArray[1]) ? Integer.parseInt(cronTextArray[1]) : null;
                this.executeTimeInDate = cronTextArray[2];
            } catch (UnExpectedRequestException e) {
//            doing nothing
            }
        }
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExecFreq() {
        return execFreq;
    }

    public void setExecFreq(String execFreq) {
        this.execFreq = execFreq;
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
}
