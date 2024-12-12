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

package com.webank.wedatasphere.qualitis.rule.request.multi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateDataSourceTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;

import com.webank.wedatasphere.qualitis.rule.entity.TemplateDataSourceType;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

/**
 * @author howeye
 */
public class MultiDataSourceConfigRequest {

    @JsonProperty("db_name")
    private String dbName;
    @JsonProperty("table_name")
    private String tableName;
    private String filter;

    @JsonProperty("context_service")
    private boolean contextService;

    @JsonProperty("file_id")
    private String fileId;
    @JsonProperty("file_table_desc")
    private String fileTableDesc;
    @JsonProperty("file_delimiter")
    private String fileDelimiter;
    @JsonProperty("file_type")
    private String fileType;
    @JsonProperty("file_header")
    private Boolean fileHeader;
    @JsonProperty("fps_file")
    private boolean fpsFile;
    @JsonProperty("proxy_user")
    private String proxyUser;
    @JsonProperty("file_hash_values")
    private String fileHashValues;

    @JsonProperty("linkis_datasoure_id")
    private Long linkisDataSourceId;
    @JsonProperty("linkis_datasource_name")
    private String linkisDataSourceName;
    @JsonProperty("linkis_datasource_type")
    private String linkisDataSourceType;
    @JsonProperty("linkis_datasoure_version_id")
    private Long linkisDataSourceVersionId;

    public MultiDataSourceConfigRequest() {
    }

    public MultiDataSourceConfigRequest(String dbName, String tableName, String filter) {
        this.dbName = dbName;
        this.tableName = tableName;
        this.filter = filter;
    }

    public MultiDataSourceConfigRequest(Set<RuleDataSource> ruleDataSources, Integer index) {
        for (RuleDataSource ruleDataSource : ruleDataSources) {
            if (! ruleDataSource.getDatasourceIndex().equals(index)) {
                continue;
            }
            this.dbName = ruleDataSource.getDbName();
            this.tableName = ruleDataSource.getTableName();
            this.filter = ruleDataSource.getFilter();
            this.proxyUser = ruleDataSource.getProxyUser();
            this.linkisDataSourceId = ruleDataSource.getLinkisDataSourceId();
            this.linkisDataSourceType = TemplateDataSourceTypeEnum.getMessage(ruleDataSource.getDatasourceType());
            this.linkisDataSourceName = ruleDataSource.getLinkisDataSourceName();
            this.linkisDataSourceVersionId = ruleDataSource.getLinkisDataSourceVersionId();
        }

        if (this.getDbName() == null || "".equals(this.getDbName())) {
            contextService = true;
        } else {
            contextService = false;
        }
    }

    public MultiDataSourceConfigRequest(String dbName, String tableName, String filter, Long linkisDataSourceId, String linkisDataSourceName, String linkisDataSourceType) {
        this.dbName = dbName;
        this.tableName = tableName;
        this.filter = filter;
        this.linkisDataSourceId = linkisDataSourceId;
        this.linkisDataSourceType = linkisDataSourceType;
        this.linkisDataSourceName = linkisDataSourceName;
    }

    public String getFileHashValues() {
        return fileHashValues;
    }

    public void setFileHashValues(String fileHashValues) {
        this.fileHashValues = fileHashValues;
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

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public boolean isContextService() {
        return contextService;
    }

    public void setContextService(boolean contextService) {
        this.contextService = contextService;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileTableDesc() {
        return fileTableDesc;
    }

    public void setFileTableDesc(String fileTableDesc) {
        this.fileTableDesc = fileTableDesc;
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

    public boolean isFpsFile() {
        return fpsFile;
    }

    public void setFpsFile(boolean fpsFile) {
        this.fpsFile = fpsFile;
    }

    public Boolean getFileHeader() {
        return fileHeader;
    }

    public void setFileHeader(Boolean fileHeader) {
        this.fileHeader = fileHeader;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public Long getLinkisDataSourceId() {
        return linkisDataSourceId;
    }

    public void setLinkisDataSourceId(Long linkisDataSourceId) {
        this.linkisDataSourceId = linkisDataSourceId;
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

    public Long getLinkisDataSourceVersionId() {
        return linkisDataSourceVersionId;
    }

    public void setLinkisDataSourceVersionId(Long linkisDataSourceVersionId) {
        this.linkisDataSourceVersionId = linkisDataSourceVersionId;
    }

    public static void checkRequest(MultiDataSourceConfigRequest request, Boolean cs) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        if (! cs) {
            CommonChecker.checkString(request.getDbName(), "Db name");
        }
        CommonChecker.checkString(request.getTableName(), "Table name");
        CommonChecker.checkString(request.getFilter(), "Filter");
    }
}
