/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author howeye
 */
@Configuration
public class LinkisConfig {
    @Value("${linkis.udf_admin}")
    private String udfAdmin;
    @Value("${linkis.datasource_admin}")
    private String datasourceAdmin;


    @Value("${linkis.check_start_time}")
    private String startTime;

    @Value("${linkis.datasource_cluster}")
    private String datasourceCluster;

    @Value("${linkis.bdap_check_alert_cluster}")
    private String bdapCheckAlertCluster;

    @Value("${linkis.api.prefix}")
    private String prefix;

    @Value("${linkis.api.submitJob}")
    private String submitJob;

    @Value("${linkis.api.submitJobNew}")
    private String submitJobNew;

    @Value("${linkis.api.killJob}")
    private String killJob;

    @Value("${linkis.api.runningLog}")
    private String runningLog;

    @Value("${linkis.api.finishLog}")
    private String finishLog;

    @Value("${linkis.api.status}")
    private String status;

    @Value("${linkis.api.unDone}")
    private String unDone;

    @Value("${linkis.api.unDone_days}")
    private Integer unDoneDays;

    @Value("${linkis.api.unDone_switch}")
    private Boolean unDoneSwitch;

    @Value("${linkis.api.maxUnDone}")
    private Integer maxUnDone;

    @Value("${linkis.log.maskKey}")
    private String logMaskKey;

    @Value("${linkis.api.meta_data.db_path}")
    private String dbPath;

    @Value("${linkis.api.meta_data.table_path}")
    private String tablePath;
    @Value("${linkis.api.meta_data.table_info}")
    private String tableInfo;
    @Value("${linkis.api.meta_data.table_statistics}")
    private String tableStatistics;
    @Value("${linkis.api.meta_data.partition_statistics}")
    private String partitionStatistics;

    @Value("${linkis.api.meta_data.datasource_types}")
    private String datasourceTypes;
    @Value("${linkis.api.meta_data.datasource_env}")
    private String datasourceEnv;
    @Value("${linkis.api.meta_data.datasource_env_detail}")
    private String datasourceEnvDetail;
    @Value("${linkis.api.meta_data.datasource_info}")
    private String datasourceInfo;
    @Value("${linkis.api.meta_data.datasource_info_ids}")
    private String datasourceInfoIds;
    @Value("${linkis.api.meta_data.datasource_publish}")
    private String datasourcePublish;
    @Value("${linkis.api.meta_data.datasource_connect}")
    private String datasourceConnect;
    @Value("${linkis.api.meta_data.datasource_connect_param}")
    private String datasourceConnectParam;
    @Value("${linkis.api.meta_data.datasource_info_name}")
    private String datasourceInfoName;
    @Value("${linkis.api.meta_data.datasource_key_define}")
    private String datasourceKeyDefine;
    @Value("${linkis.api.meta_data.datasource_expire}")
    private String datasourceExpire;
    @Value("${linkis.api.meta_data.datasource_modify}")
    private String datasourceModify;
    @Value("${linkis.api.meta_data.datasource_create}")
    private String datasourceCreate;
    @Value("${linkis.api.meta_data.datasource_delete}")
    private String datasourceDelete;
    @Value("${linkis.api.meta_data.datasource_init_version}")
    private String datasourceInitVersion;
    @Value("${linkis.api.meta_data.datasource_versions}")
    private String datasourceVersions;
    @Value("${linkis.api.meta_data.datasource_db}")
    private String datasourceDb;
    @Value("${linkis.api.meta_data.datasource_query_db}")
    private String datasourceQueryDb;
    @Value("${linkis.api.meta_data.datasource_query_table}")
    private String datasourceQueryTable;
    @Value("${linkis.api.meta_data.datasource_query_column}")
    private String datasourceQueryColumn;
    @Value("${linkis.api.meta_data.datasource_table}")
    private String datasourceTable;
    @Value("${linkis.api.meta_data.datasource_column}")
    private String datasourceColumn;
    @Value("${linkis.api.meta_data.datasource_env_create_batch}")
    private String datasourceEnvCreateBatch;
    @Value("${linkis.api.meta_data.datasource_env_modify_batch}")
    private String datasourceEnvModifyBatch;
    @Value("${linkis.api.meta_data.datasource_env_delete}")
    private String envDelete;
    @Value("${linkis.api.meta_data.column_path}")
    private String columnPath;
    @Value("${linkis.api.meta_data.column_info}")
    private String columnInfo;
    @Value("${linkis.api.meta_data.udf_add}")
    private String udfAdd;
    @Value("${linkis.api.meta_data.udf_delete}")
    private String udfDelete;
    @Value("${linkis.api.meta_data.udf_modify}")
    private String udfModify;
    @Value("${linkis.api.meta_data.udf_page}")
    private String udfPage;
    @Value("${linkis.api.meta_data.udf_detail}")
    private String udfDetail;
    @Value("${linkis.api.meta_data.udf_share}")
    private String udfShare;
    @Value("${linkis.api.meta_data.udf_share_user}")
    private String udfShareUser;
    @Value("${linkis.api.meta_data.udf_handover}")
    private String udfHandover;
    @Value("${linkis.api.meta_data.udf_user_directory}")
    private String udfDirectory;
    @Value("${linkis.api.meta_data.udf_switch_status}")
    private String udfSwitchStatus;
    @Value("${linkis.api.meta_data.udf_name_list}")
    private String udfByNameList;
    @Value("${linkis.api.meta_data.udf_new_version}")
    private String udfNewVersion;
    @Value("${linkis.api.meta_data.udf_publish}")
    private String udfPublish;

    @Value("${linkis.spark.application.name}")
    private String appName;
    @Value("${linkis.spark.engine.name}")
    private String engineName;
    @Value("${linkis.spark.engine.version}")
    private String engineVersion;
    @Value("${linkis.spark.engine.limit}")
    private String engineLimit;
    @Value("${linkis.shell.engine.name}")
    private String shellEngineName;
    @Value("${linkis.shell.engine.version}")
    private String shellEngineVersion;
    @Value("${linkis.trino.engine.name:trino}")
    private String trinoEngineName;
    @Value("${linkis.trino.engine.version:371}")
    private String trinoEngineVersion;

    @Value("${linkis.kill_stuck_tasks}")
    private Boolean killStuckTasks;
    @Value("${linkis.kill_stuck_tasks_time}")
    private Integer killStuckTasksTime;
    @Value("${linkis.kill_total_tasks_time}")
    private Integer killTotalTasksTime;
    @Value("${linkis.api.upload}")
    private String upload;
    @Value("${linkis.api.upload_tmp_path}")
    private String uploadTmpPath;
    @Value("${linkis.api.upload_prefix}")
    private String uploadPrefix;
    @Value("${linkis.api.upload_workspace_prefix}")
    private String uploadWorkspacePrefix;
    @Value("${linkis.api.upload_root}")
    private String uploadRoot;
    @Value("${linkis.api.upload_dir}")
    private String uploadDir;
    @Value("${linkis.api.upload_create_dir}")
    private String uploadCreateDir;
    @Value("${linkis.api.delete_dir}")
    private String deleteDir;
    @Value("${linkis.api.getFullTree}")
    private String getFullTree;
    @Value("${linkis.api.saveFullTree}")
    private String saveFullTree;

    @Value("${linkis.gateway.query_time_out}")
    private Integer gatewayQueryTimeout;
    @Value("${linkis.gateway.retry_time}")
    private Integer restTemplateMaxAttempt;
    @Value("${linkis.gateway.retry_interval}")
    private Integer retryTimeInterval;

    @Value("${linkis.label.jobQueuingTimeoutName}")
    private String jobQueuingTimeoutName;
    @Value("${linkis.label.jobRunningTimeoutName}")
    private String jobRunningTimeoutName;
    @Value("${linkis.label.jobRetryTimeoutName}")
    private String jobRetryTimeoutName;
    @Value("${linkis.label.jobRetryName}")
    private String jobRetryName;

    @Value("${linkis.label.jobQueuingTimeout}")
    private String jobQueuingTimeout;
    @Value("${linkis.label.jobRunningTimeout}")
    private String jobRunningTimeout;
    @Value("${linkis.label.jobRetryTimeout}")
    private String jobRetryTimeout;
    @Value("${linkis.label.jobRetryNumber}")
    private String jobRetry;

    @Value("${linkis.checkAlert.template}")
    private String checkAlertTemplate;

    @Value("${linkis.git.private-key}")
    private String gitPrivateKey;

//    @Value("${linkis.collect.template}")
//    private String collectTemplate;

    public String getEnvDelete() {
        return envDelete;
    }

    public void setEnvDelete(String envDelete) {
        this.envDelete = envDelete;
    }

    public String getUdfAdd() {
        return udfAdd;
    }

    public void setUdfAdd(String udfAdd) {
        this.udfAdd = udfAdd;
    }

    public String getUdfDelete() {
        return udfDelete;
    }

    public void setUdfDelete(String udfDelete) {
        this.udfDelete = udfDelete;
    }

    public String getUdfModify() {
        return udfModify;
    }

    public void setUdfModify(String udfModify) {
        this.udfModify = udfModify;
    }

    public String getUdfPage() {
        return udfPage;
    }

    public void setUdfPage(String udfPage) {
        this.udfPage = udfPage;
    }

    public String getUdfDetail() {
        return udfDetail;
    }

    public void setUdfDetail(String udfDetail) {
        this.udfDetail = udfDetail;
    }

    public String getUdfShare() {
        return udfShare;
    }

    public void setUdfShare(String udfShare) {
        this.udfShare = udfShare;
    }

    public String getUdfShareUser() {
        return udfShareUser;
    }

    public void setUdfShareUser(String udfShareUser) {
        this.udfShareUser = udfShareUser;
    }

    public String getUdfHandover() {
        return udfHandover;
    }

    public void setUdfHandover(String udfHandover) {
        this.udfHandover = udfHandover;
    }

    public String getUdfDirectory() {
        return udfDirectory;
    }

    public void setUdfDirectory(String udfDirectory) {
        this.udfDirectory = udfDirectory;
    }

    public String getUdfSwitchStatus() {
        return udfSwitchStatus;
    }

    public void setUdfSwitchStatus(String udfSwitchStatus) {
        this.udfSwitchStatus = udfSwitchStatus;
    }

    public String getUdfByNameList() {
        return udfByNameList;
    }

    public void setUdfByNameList(String udfByNameList) {
        this.udfByNameList = udfByNameList;
    }

    public String getUdfNewVersion() {
        return udfNewVersion;
    }

    public void setUdfNewVersion(String udfNewVersion) {
        this.udfNewVersion = udfNewVersion;
    }

    public String getUdfPublish() {
        return udfPublish;
    }

    public void setUdfPublish(String udfPublish) {
        this.udfPublish = udfPublish;
    }

    public String getUploadWorkspacePrefix() {
        return uploadWorkspacePrefix;
    }

    public void setUploadWorkspacePrefix(String uploadWorkspacePrefix) {
        this.uploadWorkspacePrefix = uploadWorkspacePrefix;
    }

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getUploadCreateDir() {
        return uploadCreateDir;
    }

    public void setUploadCreateDir(String uploadCreateDir) {
        this.uploadCreateDir = uploadCreateDir;
    }

    public String getDeleteDir() {
        return deleteDir;
    }

    public void setDeleteDir(String deleteDir) {
        this.deleteDir = deleteDir;
    }

    public String getDatasourceInfoIds() {
        return datasourceInfoIds;
    }

    public void setDatasourceInfoIds(String datasourceInfoIds) {
        this.datasourceInfoIds = datasourceInfoIds;
    }

    public String getUdfAdmin() {
        return udfAdmin;
    }

    public void setUdfAdmin(String udfAdmin) {
        this.udfAdmin = udfAdmin;
    }

    public String getDatasourceAdmin() {
        return datasourceAdmin;
    }

    public void setDatasourceAdmin(String datasourceAdmin) {
        this.datasourceAdmin = datasourceAdmin;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDatasourceCluster() {
        return datasourceCluster;
    }

    public void setDatasourceCluster(String datasourceCluster) {
        this.datasourceCluster = datasourceCluster;
    }

    public String getBdapCheckAlertCluster() {
        return bdapCheckAlertCluster;
    }

    public void setBdapCheckAlertCluster(String bdapCheckAlertCluster) {
        this.bdapCheckAlertCluster = bdapCheckAlertCluster;
    }

    public String getDatasourceEnvModifyBatch() {
        return datasourceEnvModifyBatch;
    }

    public void setDatasourceEnvModifyBatch(String datasourceEnvModifyBatch) {
        this.datasourceEnvModifyBatch = datasourceEnvModifyBatch;
    }

    public String getDatasourceDelete() {
        return datasourceDelete;
    }

    public String getDatasourceQueryDb() {
        return datasourceQueryDb;
    }

    public void setDatasourceQueryDb(String datasourceQueryDb) {
        this.datasourceQueryDb = datasourceQueryDb;
    }

    public String getDatasourceQueryTable() {
        return datasourceQueryTable;
    }

    public void setDatasourceQueryTable(String datasourceQueryTable) {
        this.datasourceQueryTable = datasourceQueryTable;
    }

    public String getDatasourceQueryColumn() {
        return datasourceQueryColumn;
    }

    public void setDatasourceQueryColumn(String datasourceQueryColumn) {
        this.datasourceQueryColumn = datasourceQueryColumn;
    }

    public void setDatasourceDelete(String datasourceDelete) {
        this.datasourceDelete = datasourceDelete;
    }

    public String getDatasourceEnvDetail() {
        return datasourceEnvDetail;
    }

    public void setDatasourceEnvDetail(String datasourceEnvDetail) {
        this.datasourceEnvDetail = datasourceEnvDetail;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getLogMaskKey() {
        return logMaskKey;
    }

    public void setLogMaskKey(String logMaskKey) {
        this.logMaskKey = logMaskKey;
    }

    public String getSubmitJob() {
        return submitJob;
    }

    public void setSubmitJob(String submitJob) {
        this.submitJob = submitJob;
    }

    public String getSubmitJobNew() {
        return submitJobNew;
    }

    public void setSubmitJobNew(String submitJobNew) {
        this.submitJobNew = submitJobNew;
    }

    public String getKillJob() {
        return killJob;
    }

    public void setKillJob(String killJob) {
        this.killJob = killJob;
    }

    public String getRunningLog() {
        return runningLog;
    }

    public void setRunningLog(String runningLog) {
        this.runningLog = runningLog;
    }

    public String getFinishLog() {
        return finishLog;
    }

    public void setFinishLog(String finishLog) {
        this.finishLog = finishLog;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUnDone() {
        return unDone;
    }

    public void setUnDone(String unDone) {
        this.unDone = unDone;
    }

    public Integer getMaxUnDone() {
        return maxUnDone;
    }

    public void setMaxUnDone(Integer maxUnDone) {
        this.maxUnDone = maxUnDone;
    }

    public Integer getUnDoneDays() {
        return unDoneDays;
    }

    public void setUnDoneDays(Integer unDoneDays) {
        this.unDoneDays = unDoneDays;
    }

    public Boolean getUnDoneSwitch() {
        return unDoneSwitch;
    }

    public void setUnDoneSwitch(Boolean unDoneSwitch) {
        this.unDoneSwitch = unDoneSwitch;
    }

    public String getDbPath() {
        return dbPath;
    }

    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }

    public String getTablePath() {
        return tablePath;
    }

    public void setTablePath(String tablePath) {
        this.tablePath = tablePath;
    }

    public String getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(String tableInfo) {
        this.tableInfo = tableInfo;
    }

    public String getTableStatistics() {
        return tableStatistics;
    }

    public void setTableStatistics(String tableStatistics) {
        this.tableStatistics = tableStatistics;
    }

    public String getPartitionStatistics() {
        return partitionStatistics;
    }

    public void setPartitionStatistics(String partitionStatistics) {
        this.partitionStatistics = partitionStatistics;
    }

    public String getDatasourceTypes() {
        return datasourceTypes;
    }

    public void setDatasourceTypes(String datasourceTypes) {
        this.datasourceTypes = datasourceTypes;
    }

    public String getDatasourceEnv() {
        return datasourceEnv;
    }

    public void setDatasourceEnv(String datasourceEnv) {
        this.datasourceEnv = datasourceEnv;
    }

    public String getDatasourceInfo() {
        return datasourceInfo;
    }

    public void setDatasourceInfo(String datasourceInfo) {
        this.datasourceInfo = datasourceInfo;
    }

    public String getDatasourcePublish() {
        return datasourcePublish;
    }

    public void setDatasourcePublish(String datasourcePublish) {
        this.datasourcePublish = datasourcePublish;
    }

    public String getDatasourceConnect() {
        return datasourceConnect;
    }

    public void setDatasourceConnect(String datasourceConnect) {
        this.datasourceConnect = datasourceConnect;
    }

    public String getDatasourceConnectParam() {
        return datasourceConnectParam;
    }

    public void setDatasourceConnectParam(String datasourceConnectParam) {
        this.datasourceConnectParam = datasourceConnectParam;
    }

    public String getDatasourceInfoName() {
        return datasourceInfoName;
    }

    public void setDatasourceInfoName(String datasourceInfoName) {
        this.datasourceInfoName = datasourceInfoName;
    }

    public String getDatasourceKeyDefine() {
        return datasourceKeyDefine;
    }

    public void setDatasourceKeyDefine(String datasourceKeyDefine) {
        this.datasourceKeyDefine = datasourceKeyDefine;
    }

    public String getDatasourceExpire() {
        return datasourceExpire;
    }

    public void setDatasourceExpire(String datasourceExpire) {
        this.datasourceExpire = datasourceExpire;
    }

    public String getDatasourceModify() {
        return datasourceModify;
    }

    public void setDatasourceModify(String datasourceModify) {
        this.datasourceModify = datasourceModify;
    }

    public String getDatasourceCreate() {
        return datasourceCreate;
    }

    public void setDatasourceCreate(String datasourceCreate) {
        this.datasourceCreate = datasourceCreate;
    }

    public String getDatasourceInitVersion() {
        return datasourceInitVersion;
    }

    public void setDatasourceInitVersion(String datasourceInitVersion) {
        this.datasourceInitVersion = datasourceInitVersion;
    }

    public String getDatasourceVersions() {
        return datasourceVersions;
    }

    public void setDatasourceVersions(String datasourceVersions) {
        this.datasourceVersions = datasourceVersions;
    }

    public String getDatasourceDb() {
        return datasourceDb;
    }

    public void setDatasourceDb(String datasourceDb) {
        this.datasourceDb = datasourceDb;
    }

    public String getDatasourceTable() {
        return datasourceTable;
    }

    public void setDatasourceTable(String datasourceTable) {
        this.datasourceTable = datasourceTable;
    }

    public String getDatasourceColumn() {
        return datasourceColumn;
    }

    public void setDatasourceColumn(String datasourceColumn) {
        this.datasourceColumn = datasourceColumn;
    }

    public String getColumnPath() {
        return columnPath;
    }

    public void setColumnPath(String columnPath) {
        this.columnPath = columnPath;
    }

    public String getColumnInfo() {
        return columnInfo;
    }

    public void setColumnInfo(String columnInfo) {
        this.columnInfo = columnInfo;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public String getEngineVersion() {
        return engineVersion;
    }

    public void setEngineVersion(String engineVersion) {
        this.engineVersion = engineVersion;
    }

    public String getShellEngineName() {
        return shellEngineName;
    }

    public void setShellEngineName(String shellEngineName) {
        this.shellEngineName = shellEngineName;
    }

    public String getShellEngineVersion() {
        return shellEngineVersion;
    }

    public void setShellEngineVersion(String shellEngineVersion) {
        this.shellEngineVersion = shellEngineVersion;
    }

    public String getTrinoEngineName() {
        return trinoEngineName;
    }

    public void setTrinoEngineName(String trinoEngineName) {
        this.trinoEngineName = trinoEngineName;
    }

    public String getTrinoEngineVersion() {
        return trinoEngineVersion;
    }

    public void setTrinoEngineVersion(String trinoEngineVersion) {
        this.trinoEngineVersion = trinoEngineVersion;
    }

    public Boolean getKillStuckTasks() {
        return killStuckTasks;
    }

    public void setKillStuckTasks(Boolean killStuckTasks) {
        this.killStuckTasks = killStuckTasks;
    }

    public Integer getKillStuckTasksTime() {
        return killStuckTasksTime;
    }

    public void setKillStuckTasksTime(Integer killStuckTasksTime) {
        this.killStuckTasksTime = killStuckTasksTime;
    }

    public Integer getKillTotalTasksTime() {
        return killTotalTasksTime;
    }

    public void setKillTotalTasksTime(Integer killTotalTasksTime) {
        this.killTotalTasksTime = killTotalTasksTime;
    }

    public String getUpload() {
        return upload;
    }

    public void setUpload(String upload) {
        this.upload = upload;
    }

    public String getUploadTmpPath() {
        return uploadTmpPath;
    }

    public void setUploadTmpPath(String uploadTmpPath) {
        this.uploadTmpPath = uploadTmpPath;
    }

    public String getUploadPrefix() {
        return uploadPrefix;
    }

    public void setUploadPrefix(String uploadPrefix) {
        this.uploadPrefix = uploadPrefix;
    }

    public String getUploadRoot() {
        return uploadRoot;
    }

    public void setUploadRoot(String uploadRoot) {
        this.uploadRoot = uploadRoot;
    }

    public String getGetFullTree() {
        return getFullTree;
    }

    public void setGetFullTree(String getFullTree) {
        this.getFullTree = getFullTree;
    }

    public String getSaveFullTree() {
        return saveFullTree;
    }

    public void setSaveFullTree(String saveFullTree) {
        this.saveFullTree = saveFullTree;
    }

    public Integer getGatewayQueryTimeout() {
        return gatewayQueryTimeout;
    }

    public void setGatewayQueryTimeout(Integer gatewayQueryTimeout) {
        this.gatewayQueryTimeout = gatewayQueryTimeout;
    }

    public Integer getRestTemplateMaxAttempt() {
        return restTemplateMaxAttempt;
    }

    public void setRestTemplateMaxAttempt(Integer restTemplateMaxAttempt) {
        this.restTemplateMaxAttempt = restTemplateMaxAttempt;
    }

    public Integer getRetryTimeInterval() {
        return retryTimeInterval;
    }

    public void setRetryTimeInterval(Integer retryTimeInterval) {
        this.retryTimeInterval = retryTimeInterval;
    }

    public String getJobQueuingTimeoutName() {
        return jobQueuingTimeoutName;
    }

    public void setJobQueuingTimeoutName(String jobQueuingTimeoutName) {
        this.jobQueuingTimeoutName = jobQueuingTimeoutName;
    }

    public String getJobRunningTimeoutName() {
        return jobRunningTimeoutName;
    }

    public void setJobRunningTimeoutName(String jobRunningTimeoutName) {
        this.jobRunningTimeoutName = jobRunningTimeoutName;
    }

    public String getJobRetryTimeoutName() {
        return jobRetryTimeoutName;
    }

    public void setJobRetryTimeoutName(String jobRetryTimeoutName) {
        this.jobRetryTimeoutName = jobRetryTimeoutName;
    }

    public String getJobRetryName() {
        return jobRetryName;
    }

    public void setJobRetryName(String jobRetryName) {
        this.jobRetryName = jobRetryName;
    }

    public String getJobQueuingTimeout() {
        return jobQueuingTimeout;
    }

    public void setJobQueuingTimeout(String jobQueuingTimeout) {
        this.jobQueuingTimeout = jobQueuingTimeout;
    }

    public String getJobRunningTimeout() {
        return jobRunningTimeout;
    }

    public void setJobRunningTimeout(String jobRunningTimeout) {
        this.jobRunningTimeout = jobRunningTimeout;
    }

    public String getJobRetryTimeout() {
        return jobRetryTimeout;
    }

    public void setJobRetryTimeout(String jobRetryTimeout) {
        this.jobRetryTimeout = jobRetryTimeout;
    }

    public String getJobRetry() {
        return jobRetry;
    }

    public void setJobRetry(String jobRetry) {
        this.jobRetry = jobRetry;
    }

    public String getDatasourceEnvCreateBatch() {
        return datasourceEnvCreateBatch;
    }

    public void setDatasourceEnvCreateBatch(String datasourceEnvCreateBatch) {
        this.datasourceEnvCreateBatch = datasourceEnvCreateBatch;
    }

    public String getCheckAlertTemplate() {
        return checkAlertTemplate;
    }

    public void setCheckAlertTemplate(String checkAlertTemplate) {
        this.checkAlertTemplate = checkAlertTemplate;
    }

    public String getGitPrivateKey() {
        return gitPrivateKey;
    }

    public void setGitPrivateKey(String gitPrivateKey) {
        this.gitPrivateKey = gitPrivateKey;
    }

    public String getEngineLimit() {
        return engineLimit;
    }

    public void setEngineLimit(String engineLimit) {
        this.engineLimit = engineLimit;
    }

//    public String getCollectTemplate() {
//        return collectTemplate;
//    }
//
//    public void setCollectTemplate(String collectTemplate) {
//        this.collectTemplate = collectTemplate;
//    }
}
