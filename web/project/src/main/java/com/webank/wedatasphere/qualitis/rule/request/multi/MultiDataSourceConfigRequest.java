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
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.rule.constant.TemplateDataSourceTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.LinkisDataSourceEnvDao;
import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSourceEnv;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSourceEnv;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceEnvRequest;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
public class MultiDataSourceConfigRequest {
    @JsonProperty("cluster_name")
    private String clusterName;
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

    @JsonProperty("linkis_datasource_id")
    private Long linkisDataSourceId;
    @JsonProperty("linkis_datasource_name")
    private String linkisDataSourceName;
    @JsonProperty("linkis_datasource_type")
    private String linkisDataSourceType;
    @JsonProperty("linkis_datasource_dcn_range_values")
    private List<String> dcnRangeValues;
    @JsonProperty("linkis_datasource_envs")
    private List<DataSourceEnvRequest> dataSourceEnvRequests;
    @JsonProperty("linkis_datasource_version_id")
    private Long linkisDataSourceVersionId;

    @JsonProperty("dcn_range_type")
    private String dcnRangeType;

    @JsonProperty("type")
    private String type;
    @JsonProperty("collect_sql")
    private String collectSql;
    @JsonProperty("udf_name")
    private String udfName;

    public MultiDataSourceConfigRequest() {
    }

    public MultiDataSourceConfigRequest(String dbName, String tableName, String filter, String proxyUser) {
        this.dbName = dbName;
        this.tableName = tableName;
        this.filter = filter;
        this.proxyUser = proxyUser;
    }

    public MultiDataSourceConfigRequest(String clusterName, String dbName, String tableName, String filter, String proxyUser) {
        this.clusterName = clusterName;
        this.dbName = dbName;
        this.tableName = tableName;
        this.filter = filter;
        this.proxyUser = proxyUser;
    }

    public List<String> getDcnRangeValues() {
        return dcnRangeValues;
    }

    public void setDcnRangeValues(List<String> dcnRangeValues) {
        this.dcnRangeValues = dcnRangeValues;
    }

    public String getDcnRangeType() {
        return dcnRangeType;
    }

    public void setDcnRangeType(String dcnRangeType) {
        this.dcnRangeType = dcnRangeType;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public MultiDataSourceConfigRequest(Set<RuleDataSource> ruleDataSources, Integer index) {
        for (RuleDataSource ruleDataSource : ruleDataSources) {
            if (!ruleDataSource.getDatasourceIndex().equals(index)) {
                continue;
            }
            this.clusterName = ruleDataSource.getClusterName();
            this.dbName = ruleDataSource.getDbName();
            String table = ruleDataSource.getTableName();
            // UUID remove.
            if (StringUtils.isNotBlank(ruleDataSource.getFileId()) && StringUtils.isNotBlank(table) && table.contains("_") && table.length() - UuidGenerator.generate().length() - 1 > 0) {
                this.tableName = table.substring(0, table.length() - UuidGenerator.generate().length() - 1);
            } else {
                this.tableName = ruleDataSource.getTableName();
            }
            this.filter = ruleDataSource.getFilter();
            this.proxyUser = ruleDataSource.getProxyUser();
            if (StringUtils.isNotBlank(ruleDataSource.getFileId())) {
                this.fileId = ruleDataSource.getFileId();
                this.fileTableDesc = ruleDataSource.getFileTableDesc();
                this.fileDelimiter = " ".equals(ruleDataSource.getFileDelimiter()) ? SpecCharEnum.STAR.getValue() : ruleDataSource.getFileDelimiter();
                this.fileType = ruleDataSource.getFileType();
                this.fileHeader = ruleDataSource.getFileHeader();
                this.fileHashValues = ruleDataSource.getFileHashValue();
                this.fpsFile = true;
            }
            this.linkisDataSourceId = ruleDataSource.getLinkisDataSourceId();
            if (null != linkisDataSourceId) {
                this.linkisDataSourceType = TemplateDataSourceTypeEnum.getMessage(ruleDataSource.getDatasourceType());
            }
            this.linkisDataSourceName = ruleDataSource.getLinkisDataSourceName();
            this.linkisDataSourceVersionId = ruleDataSource.getLinkisDataSourceVersionId();
            this.type = TemplateDataSourceTypeEnum.getMessage(ruleDataSource.getDatasourceType());
            //dataSourceEnvRequests
            this.dataSourceEnvRequests = ruleDataSource.getRuleDataSourceEnvs().stream().distinct().map(item -> {
                DataSourceEnvRequest dataSourceEnvRequest = new DataSourceEnvRequest();
                dataSourceEnvRequest.setEnvId(item.getEnvId());
                dataSourceEnvRequest.setEnvName(item.getEnvName());
                return dataSourceEnvRequest;
            }).collect(Collectors.toList());

            this.dcnRangeType = ruleDataSource.getDcnRangeType();
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

    public String getCollectSql() {
        return collectSql;
    }

    public void setCollectSql(String collectSql) {
        this.collectSql = collectSql;
    }

    public String getUdfName() {
        return udfName;
    }

    public void setUdfName(String udfName) {
        this.udfName = udfName;
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

    public List<DataSourceEnvRequest> getDataSourceEnvRequests() {
        return dataSourceEnvRequests;
    }

    public void setDataSourceEnvRequests(List<DataSourceEnvRequest> dataSourceEnvRequests) {
        this.dataSourceEnvRequests = dataSourceEnvRequests;
    }

    public Long getLinkisDataSourceVersionId() {
        return linkisDataSourceVersionId;
    }

    public void setLinkisDataSourceVersionId(Long linkisDataSourceVersionId) {
        this.linkisDataSourceVersionId = linkisDataSourceVersionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static void checkRequest(MultiDataSourceConfigRequest request, Boolean cs, Boolean tableStructureConsistent) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        if (!cs) {
            CommonChecker.checkString(request.getDbName(), "Db name");
        }
        CommonChecker.checkString(request.getTableName(), "Table name");
        if (!tableStructureConsistent) {
            CommonChecker.checkString(request.getFilter(), "Filter");
        }
    }

    public static void checkCustomConsistentRequest(MultiDataSourceConfigRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
    }
}
