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
    @Value("${linkis.api.meta_data.datasource_info}")
    private String datasourceInfo;
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
    @Value("${linkis.api.meta_data.datasource_init_version}")
    private String datasourceInitVersion;
    @Value("${linkis.api.meta_data.datasource_versions}")
    private String datasourceVersions;
    @Value("${linkis.api.meta_data.datasource_db}")
    private String datasourceDb;
    @Value("${linkis.api.meta_data.datasource_table}")
    private String datasourceTable;
    @Value("${linkis.api.meta_data.datasource_column}")
    private String datasourceColumn;

    @Value("${linkis.api.meta_data.column_path}")
    private String columnPath;
    @Value("${linkis.api.meta_data.column_info}")
    private String columnInfo;

    @Value("${linkis.spark.application.name}")
    private String appName;
    @Value("${linkis.spark.engine.name}")
    private String engineName;
    @Value("${linkis.spark.engine.version}")
    private String engineVersion;

    @Value("${linkis.kill_stuck_tasks}")
    private Boolean killStuckTasks;
    @Value("${linkis.kill_stuck_tasks_time}")
    private Integer killStuckTasksTime;
    @Value("${linkis.api.upload}")
    private String upload;
    @Value("${linkis.api.upload_tmp_path}")
    private String uploadTmpPath;
    @Value("${linkis.api.upload_prefix}")
    private String uploadPrefix;
    @Value("${linkis.api.getFullTree}")
    private String getFullTree;
    @Value("${linkis.api.saveFullTree}")
    private String saveFullTree;


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

}
