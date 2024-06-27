package com.webank.wedatasphere.qualitis.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.dto.ImsMetricCollectDto;
import com.webank.wedatasphere.qualitis.scheduled.constant.ExecuteIntervalEnum;
import org.apache.commons.lang3.StringUtils;

import static com.webank.wedatasphere.qualitis.scheduled.util.CronUtil.cronToText;

/**
 * @author v_minminghe@webank.com
 * @date 2024-05-14 15:58
 * @description
 */
public class MetricCollectQueryResponse {

    private Long id;
    @JsonProperty("template_id")
    private Long templateId;
    @JsonProperty("en_name")
    private String templateEnName;
    @JsonProperty("cn_name")
    private String templateCnName;
    @JsonProperty("cluster_name")
    private String clusterName;
    private String database;
    private String table;
    private String column;
    private String partition;
    @JsonProperty("execution_parameters_name")
    private String executionParametersName;
    @JsonProperty("exec_freq")
    private String execFreq;
    @JsonProperty("create_name")
    private String createName;
    @JsonProperty("update_name")
    private String updateName;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("update_time")
    private String updateTime;
    @JsonProperty("sql_action")
    private String sqlAction;
    @JsonProperty("proxy_user")
    private String proxyUser;

    public MetricCollectQueryResponse() {
//        do nothing
    }

    public MetricCollectQueryResponse(ImsMetricCollectDto imsMetricCollectDto) {
        this.id = imsMetricCollectDto.getId();
        this.createName = imsMetricCollectDto.getCreateUser();
        this.updateName = imsMetricCollectDto.getModifyUser();
        this.createTime = imsMetricCollectDto.getCreateTime();
        this.updateTime = imsMetricCollectDto.getModifyTime();
        this.clusterName = imsMetricCollectDto.getClusterName();
        this.database = imsMetricCollectDto.getDbName();
        this.table = imsMetricCollectDto.getTableName();
        this.partition = imsMetricCollectDto.getPartition();
        this.column = imsMetricCollectDto.getColumnName();
        if (StringUtils.isNotBlank(imsMetricCollectDto.getExecFreq()) && !imsMetricCollectDto.getExecFreq().endsWith("2099")) {
            this.execFreq = replaceTextOfExecFreq(imsMetricCollectDto.getExecFreq());
        }
        this.executionParametersName = imsMetricCollectDto.getExecutionParametersName();
        this.templateId = imsMetricCollectDto.getTemplateId();
        this.templateCnName = imsMetricCollectDto.getTemplateCnName();
        this.templateEnName = imsMetricCollectDto.getTemplateEnName();
        this.proxyUser = imsMetricCollectDto.getProxyUser();
    }

    private String replaceTextOfExecFreq (String execFreq) {
        String text = "";
        try {
            String[] cronArray = cronToText(execFreq);
            String intervalCode = cronArray[0];
            String dateInInterval = cronArray[1];
            ExecuteIntervalEnum intervalEnum = ExecuteIntervalEnum.fromCode(intervalCode);
            switch (intervalEnum) {
                case WEEK:
                    text = intervalEnum.getName() + dateInInterval + " " + cronArray[2];
                    break;
                case MONTH:
                    text = intervalEnum.getName() + dateInInterval + "日 " + cronArray[2];
                    break;
                case DAY:
                    text = intervalEnum.getName() + " " + cronArray[2];
                    break;
                default:
                    text = cronArray[2];
                    break;
            }
        } catch (Exception e) {
//      doing nothing
        }
        return text;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSqlAction() {
        return sqlAction;
    }

    public void setSqlAction(String sqlAction) {
        this.sqlAction = sqlAction;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getTemplateEnName() {
        return templateEnName;
    }

    public void setTemplateEnName(String templateEnName) {
        this.templateEnName = templateEnName;
    }

    public String getTemplateCnName() {
        return templateCnName;
    }

    public void setTemplateCnName(String templateCnName) {
        this.templateCnName = templateCnName;
    }

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

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getExecutionParametersName() {
        return executionParametersName;
    }

    public void setExecutionParametersName(String executionParametersName) {
        this.executionParametersName = executionParametersName;
    }

    public String getExecFreq() {
        return execFreq;
    }

    public void setExecFreq(String execFreq) {
        this.execFreq = execFreq;
    }

    public String getCreateName() {
        return createName;
    }

    public void setCreateName(String createName) {
        this.createName = createName;
    }

    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
