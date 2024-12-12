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

package com.webank.wedatasphere.qualitis.rule.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateDataSourceTypeEnum;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceEnv;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author howeye
 */
public class DataSourceRequest {
    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("db_name")
    private String dbName;
    @JsonProperty("table_name")
    private String tableName;
    @JsonProperty("col_names")
    private List<DataSourceColumnRequest> colNames;
    private String filter;
    private Integer datasourceIndex;
    @JsonProperty("proxy_user")
    private String proxyUser;

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

    @JsonProperty("black_list")
    private Boolean blackList;

    @JsonProperty("type")
    private String type;

    public DataSourceRequest() {
        // Default Constructor
    }

    public DataSourceRequest(RuleDataSource ruleDataSource) {
        this.clusterName = ruleDataSource.getClusterName();
        this.dbName = ruleDataSource.getDbName();
        String table = ruleDataSource.getTableName();
        // UUID remove.
        if (StringUtils.isNotBlank(ruleDataSource.getFileId()) && StringUtils.isNotBlank(table) && table.contains(SpecCharEnum.BOTTOM_BAR.getValue()) && table.length() - UuidGenerator
            .generate().length() - 1 > 0) {
            this.tableName = table.substring(0, table.length() - UuidGenerator.generate().length() - 1);
        } else {
            this.tableName = ruleDataSource.getTableName();
        }
        this.proxyUser = ruleDataSource.getProxyUser();
        if (StringUtils.isNotBlank(ruleDataSource.getColName())) {
            this.colNames = convertColNames(Stream.of(ruleDataSource.getColName().split(SpecCharEnum.VERTICAL_BAR.getValue())).collect(Collectors.toList()));
        }
        this.blackList = ruleDataSource.getBlackColName();
        this.filter = ruleDataSource.getFilter();
        if (StringUtils.isNotBlank(ruleDataSource.getFileId())) {
            this.fileId = ruleDataSource.getFileId();
            this.fileTablesDesc = ruleDataSource.getFileTableDesc();
            this.fileDelimiter = " ".equals(ruleDataSource.getFileDelimiter()) ? SpecCharEnum.STAR.getValue() : ruleDataSource.getFileDelimiter();
            this.fileType = ruleDataSource.getFileType();
            this.fileHeader = ruleDataSource.getFileHeader();
            this.fileHashValues = ruleDataSource.getFileHashValue();
        }
        this.linkisDataSourceId = ruleDataSource.getLinkisDataSourceId();
        this.linkisDataSourceName = ruleDataSource.getLinkisDataSourceName();
        if (null != linkisDataSourceId) {
            this.linkisDataSourceType = TemplateDataSourceTypeEnum.getMessage(ruleDataSource.getDatasourceType());
            if (CollectionUtils.isNotEmpty(ruleDataSource.getRuleDataSourceEnvs())) {
                this.dataSourceEnvRequests = ruleDataSource.getRuleDataSourceEnvs().stream().map(DataSourceEnvRequest::new).collect(Collectors.toList());
            }
        }
        this.linkisDataSourceVersionId = ruleDataSource.getLinkisDataSourceVersionId();
    }

    private List<DataSourceColumnRequest> convertColNames(List<String> colTypes) {
        colNames = new ArrayList<>();
        for (String col : colTypes){
            DataSourceColumnRequest columnRequest = new DataSourceColumnRequest(col);
            colNames.add(columnRequest);
        }
        return colNames;
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

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Integer getDatasourceIndex() {
        return datasourceIndex;
    }

    public void setDatasourceIndex(Integer datasourceIndex) {
        this.datasourceIndex = datasourceIndex;
    }

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

    public static void checkRequest(DataSourceRequest request, boolean cs, boolean fps) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "request");
        CommonChecker.checkString(request.getFilter(), "filter");
        CommonChecker.checkStringLength(request.getFilter(), 1000, "filter");
        CommonChecker.checkString(request.getTableName(), "table_name");
        if (! (cs || fps)) {
            CommonChecker.checkString(request.getDbName(), "db_name");
        }
        CommonChecker.checkString(request.getClusterName(), "cluster_name");
        DataSourceColumnRequest.checkRequest(request.getColNames(), fps);
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getFileHashValues() {
        return fileHashValues;
    }

    public void setFileHashValues(String fileHashValues) {
        this.fileHashValues = fileHashValues;
    }

    public Boolean getBlackList() {
        return blackList;
    }

    public void setBlackList(Boolean blackList) {
        this.blackList = blackList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static void checkRequest(DataSourceRequest request, boolean cs, String desc) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, desc);
        CommonChecker.checkString(request.getTableName(), "table_name");
        if (! cs) {
            CommonChecker.checkString(request.getDbName(), "db_name");
        }
        CommonChecker.checkString(request.getClusterName(), "cluster_name");
    }

    @Override
    public String toString() {
        return "DataSourceRequest{" +
            "clusterName='" + clusterName + '\'' +
            ", dbName='" + dbName + '\'' +
            ", tableName='" + tableName + '\'' +
            ", filter='" + filter + '\'' +
            ", datasourceIndex=" + datasourceIndex +
            ", fileId='" + fileId + '\'' +
            ", fileTablesDesc='" + fileTablesDesc + '\'' +
            ", fileDelimiter='" + fileDelimiter + '\'' +
            ", fileType='" + fileType + '\'' +
            ", fileHeader=" + fileHeader +
            ", proxyUser=" + proxyUser +
            '}';
    }
}
