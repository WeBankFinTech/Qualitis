package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2023-01-04 9:58
 * @description
 */
public class AddBatchRuleRequest {

    @JsonProperty(value = "project_id", required = true)
    private Long projectId;
    @JsonProperty(value = "rule_type", required = true)
    private Integer ruleType;
    @JsonProperty(value = "rule_template_id", required = true)
    private Long ruleTemplateId;
    @JsonProperty(value = "rule_template_name", required = true)
    private String ruleTemplateName;
    @JsonProperty(value = "rule_name", required = true)
    private String ruleName;
    @JsonProperty(value = "rule_cn_name", required = true)
    private String ruleCnName;
    @JsonProperty(value = "rule_detail")
    private String ruleDetail;
    @JsonProperty(value = "execution_parameters_name", required = true)
    private String executionParametersName;
    @JsonProperty(value = "check_object_list", required = true)
    private List<AddBatchCheckObjectRequest> checkObjectList;
    @JsonProperty(value = "check_variable_list", required = true)
    private List<FileAlarmConfigRequest> checkVariableList;
    @JsonProperty(value = "template_arguments")
    private List<TemplateArgumentRequest> templateArgumentRequests;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
    }

    public Long getRuleTemplateId() {
        return ruleTemplateId;
    }

    public void setRuleTemplateId(Long ruleTemplateId) {
        this.ruleTemplateId = ruleTemplateId;
    }

    public String getRuleTemplateName() {
        return ruleTemplateName;
    }

    public void setRuleTemplateName(String ruleTemplateName) {
        this.ruleTemplateName = ruleTemplateName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleCnName() {
        return ruleCnName;
    }

    public void setRuleCnName(String ruleCnName) {
        this.ruleCnName = ruleCnName;
    }

    public String getRuleDetail() {
        return ruleDetail;
    }

    public void setRuleDetail(String ruleDetail) {
        this.ruleDetail = ruleDetail;
    }

    public List<FileAlarmConfigRequest> getCheckVariableList() {
        return checkVariableList;
    }

    public void setCheckVariableList(List<FileAlarmConfigRequest> checkVariableList) {
        this.checkVariableList = checkVariableList;
    }

    public List<TemplateArgumentRequest> getTemplateArgumentRequests() {
        return templateArgumentRequests;
    }

    public void setTemplateArgumentRequests(List<TemplateArgumentRequest> templateArgumentRequests) {
        this.templateArgumentRequests = templateArgumentRequests;
    }

    public String getExecutionParametersName() {
        return executionParametersName;
    }

    public void setExecutionParametersName(String executionParametersName) {
        this.executionParametersName = executionParametersName;
    }

    public List<AddBatchCheckObjectRequest> getCheckObjectList() {
        return checkObjectList;
    }

    public void setCheckObjectList(List<AddBatchCheckObjectRequest> checkObjectList) {
        this.checkObjectList = checkObjectList;
    }

    public static class AddBatchCheckObjectRequest {

        @JsonProperty(value = "cluster_name", required = true)
        private String clusterName;
        @JsonProperty(value = "db_name", required = true)
        private String dbName;
        @JsonProperty(value = "table_name", required = true)
        private String tableName;
        @JsonProperty(value = "col_names")
        private List<DataSourceColumnRequest> colNames;
        @JsonProperty(value = "proxy_user")
        private String proxyUser;
        @JsonProperty(value = "datasource_type", required = true)
        private String type;
        @JsonProperty(value = "rule_metric_id")
        private Long ruleMetricId;
        @JsonProperty(value = "rule_metric_name")
        private String ruleMetricName;
        @JsonProperty(value = "rule_metric_en_code")
        private String ruleMetricEnCode;
        @JsonProperty(value = "filter")
        private String filter;

        /**
         * FPS参数
         */
        @JsonProperty("file_id")
        private String fileId;
        @JsonProperty("file_table_desc")
        private String fileTablesDesc;
        @JsonProperty("file_delimiter")
        private String fileDelimiter;
        @JsonProperty("file_type")
        private String fileType;
        @JsonProperty("file_header")
        private Boolean fileHeader;
        @JsonProperty("file_hash_values")
        private String fileHashValues;

        /**
         * 数据源参数
         */
        @JsonProperty("linkis_datasource_id")
        private Long linkisDataSourceId;
        @JsonProperty("linkis_datasource_version_id")
        private Long linkisDataSourceVersionId;
        @JsonProperty("linkis_datasource_name")
        private String linkisDataSourceName;
        @JsonProperty("linkis_datasource_type")
        private String linkisDataSourceType;
        @JsonProperty("linkis_datasource_envs")
        private List<DataSourceEnvRequest> dataSourceEnvRequests;

        public String getFileId() {
            return fileId;
        }

        public void setFileId(String fileId) {
            this.fileId = fileId;
        }

        public String getFileTablesDesc() {
            return fileTablesDesc;
        }

        public void setFileTablesDesc(String fileTablesDesc) {
            this.fileTablesDesc = fileTablesDesc;
        }

        public String getFileDelimiter() {
            return fileDelimiter;
        }

        public void setFileDelimiter(String fileDelimiter) {
            this.fileDelimiter = fileDelimiter;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public Boolean getFileHeader() {
            return fileHeader;
        }

        public void setFileHeader(Boolean fileHeader) {
            this.fileHeader = fileHeader;
        }

        public String getFileHashValues() {
            return fileHashValues;
        }

        public void setFileHashValues(String fileHashValues) {
            this.fileHashValues = fileHashValues;
        }

        public Long getLinkisDataSourceId() {
            return linkisDataSourceId;
        }

        public void setLinkisDataSourceId(Long linkisDataSourceId) {
            this.linkisDataSourceId = linkisDataSourceId;
        }

        public Long getLinkisDataSourceVersionId() {
            return linkisDataSourceVersionId;
        }

        public void setLinkisDataSourceVersionId(Long linkisDataSourceVersionId) {
            this.linkisDataSourceVersionId = linkisDataSourceVersionId;
        }

        public String getLinkisDataSourceName() {
            return linkisDataSourceName;
        }

        public void setLinkisDataSourceName(String linkisDataSourceName) {
            this.linkisDataSourceName = linkisDataSourceName;
        }

        public String getLinkisDataSourceType() {
            return linkisDataSourceType;
        }

        public void setLinkisDataSourceType(String linkisDataSourceType) {
            this.linkisDataSourceType = linkisDataSourceType;
        }

        public List<DataSourceEnvRequest> getDataSourceEnvRequests() {
            return dataSourceEnvRequests;
        }

        public void setDataSourceEnvRequests(List<DataSourceEnvRequest> dataSourceEnvRequests) {
            this.dataSourceEnvRequests = dataSourceEnvRequests;
        }

        public String getFilter() {
            return filter;
        }

        public void setFilter(String filter) {
            this.filter = filter;
        }

        public String getClusterName() {
            return clusterName;
        }

        public void setClusterName(String clusterName) {
            this.clusterName = clusterName;
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

        public List<DataSourceColumnRequest> getColNames() {
            return colNames;
        }

        public void setColNames(List<DataSourceColumnRequest> colNames) {
            this.colNames = colNames;
        }

        public String getProxyUser() {
            return proxyUser;
        }

        public void setProxyUser(String proxyUser) {
            this.proxyUser = proxyUser;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Long getRuleMetricId() {
            return ruleMetricId;
        }

        public void setRuleMetricId(Long ruleMetricId) {
            this.ruleMetricId = ruleMetricId;
        }

        public String getRuleMetricName() {
            return ruleMetricName;
        }

        public void setRuleMetricName(String ruleMetricName) {
            this.ruleMetricName = ruleMetricName;
        }

        public String getRuleMetricEnCode() {
            return ruleMetricEnCode;
        }

        public void setRuleMetricEnCode(String ruleMetricEnCode) {
            this.ruleMetricEnCode = ruleMetricEnCode;
        }
    }
}
