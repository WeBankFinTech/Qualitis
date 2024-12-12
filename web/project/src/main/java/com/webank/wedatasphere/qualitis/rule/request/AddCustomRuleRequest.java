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
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.rule.util.AlarmConfigTypeUtil;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author howeye
 */
public class AddCustomRuleRequest extends AbstractCommonRequest {
    @JsonProperty("output_name")
    private String outputName;
    @JsonProperty("save_mid_table")
    private Boolean saveMidTable;
    @JsonProperty("function_type")
    private Integer functionType;
    @JsonProperty("function_content")
    private String functionContent;
    @JsonProperty("from_content")
    private String fromContent;
    @JsonProperty("where_content")
    private String whereContent;

    @JsonProperty("cluster_name")
    private String clusterName;
    @JsonProperty("proxy_user")
    private String proxyUser;
    @JsonProperty("sql_check_area")
    private String sqlCheckArea;
    private String fifter;

    @JsonProperty("file_id")
    private String fileId;
    @JsonProperty("file_db")
    private String fileDb;
    @JsonProperty("file_table")
    private String fileTable;
    @JsonProperty("file_table_desc")
    private String fileTableDesc;
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
    @JsonProperty("linkis_datasource_envs_mappings")
    private List<DataSourceEnvMappingRequest> dataSourceEnvMappingRequests;
    @JsonProperty("alarm_variable")
    private List<CustomAlarmConfigRequest> alarmVariable;

    @JsonProperty("linkis_udf_names")
    private List<String> linkisUdfNames;

    public AddCustomRuleRequest() {
        // Default Constructor
    }


    public String getFileHashValues() {
        return fileHashValues;
    }

    public void setFileHashValues(String fileHashValues) {
        this.fileHashValues = fileHashValues;
    }

    public String getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(String proxyUser) {
        this.proxyUser = proxyUser;
    }


    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public Boolean getSaveMidTable() {
        return saveMidTable;
    }

    public void setSaveMidTable(Boolean saveMidTable) {
        this.saveMidTable = saveMidTable;
    }

    public Integer getFunctionType() {
        return functionType;
    }

    public void setFunctionType(Integer functionType) {
        this.functionType = functionType;
    }

    public String getFunctionContent() {
        return functionContent;
    }

    public void setFunctionContent(String functionContent) {
        this.functionContent = functionContent;
    }

    public String getFromContent() {
        return fromContent;
    }

    public void setFromContent(String fromContent) {
        this.fromContent = fromContent;
    }

    public String getWhereContent() {
        return whereContent;
    }

    public void setWhereContent(String whereContent) {
        this.whereContent = whereContent;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileTable() {
        return fileTable;
    }

    public void setFileTable(String fileTable) {
        this.fileTable = fileTable;
    }

    public String getFileTableDesc() {
        return fileTableDesc;
    }

    public void setFileTableDesc(String fileTableDesc) {
        this.fileTableDesc = fileTableDesc;
    }

    public String getFileDb() {
        return fileDb;
    }

    public void setFileDb(String fileDb) {
        this.fileDb = fileDb;
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

    public String getSqlCheckArea() {
        return sqlCheckArea;
    }

    public void setSqlCheckArea(String sqlCheckArea) {
        this.sqlCheckArea = sqlCheckArea;
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

    public List<DataSourceEnvMappingRequest> getDataSourceEnvMappingRequests() {
        return dataSourceEnvMappingRequests;
    }

    public void setDataSourceEnvMappingRequests(
        List<DataSourceEnvMappingRequest> dataSourceEnvMappingRequests) {
        this.dataSourceEnvMappingRequests = dataSourceEnvMappingRequests;
    }

    public String getFifter() {
        return fifter;
    }

    public void setFifter(String fifter) {
        this.fifter = fifter;
    }

    public List<CustomAlarmConfigRequest> getAlarmVariable() {
        return alarmVariable;
    }

    public void setAlarmVariable(List<CustomAlarmConfigRequest> alarmVariable) {
        this.alarmVariable = alarmVariable;
    }

    public List<String> getLinkisUdfNames() {
        return linkisUdfNames;
    }

    public void setLinkisUdfNames(List<String> linkisUdfNames) {
        this.linkisUdfNames = linkisUdfNames;
    }

    public static void checkRequest(AddCustomRuleRequest request) throws UnExpectedRequestException {
        CommonChecker.checkObject(request, "Request");
        CommonChecker.checkObject(request.getProjectId(), "Project id");
        AddCustomRuleRequest.checkCustomRuleRequest(request);
    }

    public static void checkCustomRuleRequest(AddCustomRuleRequest request) throws UnExpectedRequestException {
        CommonChecker.checkString(request.getRuleName(), "Rule name");
        CommonChecker.checkString(request.getClusterName(), "Cluster name");

        CommonChecker.checkObject(request.getAlarm(), "alarm");
        if (request.getAlarm()) {
            CommonChecker.checkObject(request.getAlarmVariable(), "alarm variable");
            if (request.getAlarmVariable().size() == 0) {
                throw new UnExpectedRequestException("alarm_variable can not be empty");
            }

            for (AbstractCommonAlarmConfigRequest abstractCommonAlarmConfigRequest : request.getAlarmVariable()) {
                AlarmConfigTypeUtil.checkAlarmConfigType(abstractCommonAlarmConfigRequest);
            }
        }
        if (CollectionUtils.isNotEmpty(request.getDataSourceEnvMappingRequests())) {
            boolean repeatDbAliasName = request.getDataSourceEnvMappingRequests().stream().map(dataSourceEnvMappingRequest -> dataSourceEnvMappingRequest.getDbAliasName()).collect(
                Collectors.toSet()).size() != request.getDataSourceEnvMappingRequests().size();

            if (repeatDbAliasName) {
                throw new UnExpectedRequestException("There are duplicate db or db alias names.");
            }
        }
        if (StringUtils.isNotBlank(request.getSqlCheckArea())) {
            return;
        }

        CommonChecker.checkString(request.getOutputName(), "Output name");

        CommonChecker.checkFunctionTypeEnum(request.getFunctionType());
        CommonChecker.checkString(request.getFunctionContent(), "Function content");
        CommonChecker.checkString(request.getWhereContent(), "Where content");
        CommonChecker.checkString(request.getFromContent(), "From content");

        CommonChecker.checkStringLength(request.getRuleName(), 128, "Rule name");
        CommonChecker.checkStringLength(request.getOutputName(), 50, "Output name");
        CommonChecker.checkStringLength(request.getFromContent(), 1000, "From content");
        CommonChecker.checkStringLength(request.getWhereContent(), 1000, "Where content");
        CommonChecker.checkStringLength(request.getFunctionContent(), 1000, "Function content");
    }
}
