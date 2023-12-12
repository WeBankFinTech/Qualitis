package com.webank.wedatasphere.qualitis.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author allenzhou@webank.com
 * @date 2021/6/15 11:35
 */
@Configuration
public class DataMapConfig {
    @Value("${datamap.isolateEnvFlag}")
    private String isolateEnvFlag;
    @Value("${datamap.address}")
    private String address;
    @Value("${datamap.dbs_path}")
    private String databasePath;
    @Value("${datamap.tables_path}")
    private String tablePath;
    @Value("${datamap.columns_path}")
    private String columnPath;
    @Value("${datamap.standard_path}")
    private String standardPath;
    @Value("${datamap.query_all_path}")
    private String queryAllPath;
    @Value("${datamap.dataset_tag_relations_path}")
    private String datasetTagRelationsPath;
    @Value("${datamap.tags_path}")
    private String tagsPath;
    @Value("${datamap.app_id}")
    private String appId;
    @Value("${datamap.app_token}")
    private String appToken;
    @Value("${datamap.user_id}")
    private String userId;
    @Value("${datamap.random_hash_salt}")
    private String randomHashSalt;
    @Value("${datamap.data_standard_urn_path}")
    private String dataStandardUrnPath;
    @Value("${datamap.data_standard_code_path}")
    private String dataStandardCodePath;
    @Value("${datamap.data_standard_code_table}")
    private String dataStandardCodeTable;

    @Value("${datamap.data_standard_category}")
    private String dataStandardCategory;
    @Value("${datamap.data_standard_big_category}")
    private String dataStandardBigCategory;
    @Value("${datamap.data_standard_small_category}")
    private String dataStandardSmallCategory;

    public String getIsolateEnvFlag() {
        return isolateEnvFlag;
    }

    public void setIsolateEnvFlag(String isolateEnvFlag) {
        this.isolateEnvFlag = isolateEnvFlag;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    public String getTablePath() {
        return tablePath;
    }

    public void setTablePath(String tablePath) {
        this.tablePath = tablePath;
    }

    public String getColumnPath() {
        return columnPath;
    }

    public void setColumnPath(String columnPath) {
        this.columnPath = columnPath;
    }

    public String getStandardPath() {
        return standardPath;
    }

    public void setStandardPath(String standardPath) {
        this.standardPath = standardPath;
    }

    public String getQueryAllPath() {
        return queryAllPath;
    }

    public void setQueryAllPath(String queryAllPath) {
        this.queryAllPath = queryAllPath;
    }

    public String getDatasetTagRelationsPath() {
        return datasetTagRelationsPath;
    }

    public void setDatasetTagRelationsPath(String datasetTagRelationsPath) {
        this.datasetTagRelationsPath = datasetTagRelationsPath;
    }

    public String getTagsPath() {
        return tagsPath;
    }

    public void setTagsPath(String tagsPath) {
        this.tagsPath = tagsPath;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRandomHashSalt() {
        return randomHashSalt;
    }

    public void setRandomHashSalt(String randomHashSalt) {
        this.randomHashSalt = randomHashSalt;
    }

    public String getDataStandardUrnPath() {
        return dataStandardUrnPath;
    }

    public void setDataStandardUrnPath(String dataStandardUrnPath) {
        this.dataStandardUrnPath = dataStandardUrnPath;
    }

    public String getDataStandardCodePath() {
        return dataStandardCodePath;
    }

    public void setDataStandardCodePath(String dataStandardCodePath) {
        this.dataStandardCodePath = dataStandardCodePath;
    }

    public String getDataStandardCategory() {
        return dataStandardCategory;
    }

    public void setDataStandardCategory(String dataStandardCategory) {
        this.dataStandardCategory = dataStandardCategory;
    }

    public String getDataStandardBigCategory() {
        return dataStandardBigCategory;
    }

    public void setDataStandardBigCategory(String dataStandardBigCategory) {
        this.dataStandardBigCategory = dataStandardBigCategory;
    }

    public String getDataStandardSmallCategory() {
        return dataStandardSmallCategory;
    }

    public void setDataStandardSmallCategory(String dataStandardSmallCategory) {
        this.dataStandardSmallCategory = dataStandardSmallCategory;
    }

    public String getDataStandardCodeTable() {
        return dataStandardCodeTable;
    }

    public void setDataStandardCodeTable(String dataStandardCodeTable) {
        this.dataStandardCodeTable = dataStandardCodeTable;
    }
}
