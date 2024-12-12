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

package com.webank.wedatasphere.qualitis.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedatasphere.qualitis.constant.BlackListFilterTypeEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.request.GetClusterByUserRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetColumnByUserAndTableRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetDbByUserAndClusterRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetTableByUserAndDbRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserColumnByCsRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserTableByCsIdRequest;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.cluster.ClusterInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.db.DbInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.CsTableInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableInfoDetail;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.request.DataSourceConnectRequest;
import com.webank.wedatasphere.qualitis.request.DataSourceModifyRequest;
import com.webank.wedatasphere.qualitis.request.DataSourceParamModifyRequest;
import com.webank.wedatasphere.qualitis.request.FilterRequest;
import com.webank.wedatasphere.qualitis.request.GetUserClusterRequest;
import com.webank.wedatasphere.qualitis.request.GetUserColumnByTableIdRequest;
import com.webank.wedatasphere.qualitis.request.GetUserDbByClusterRequest;
import com.webank.wedatasphere.qualitis.request.GetUserTableByDbIdRequest;
import com.webank.wedatasphere.qualitis.request.MulDbRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllClusterResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.constant.CheckTemplateEnum;
import com.webank.wedatasphere.qualitis.rule.constant.CompareTypeEnum;
import com.webank.wedatasphere.qualitis.rule.constant.MappingOperationEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.request.AlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.AddMultiSourceRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinConfigRequest;
import com.webank.wedatasphere.qualitis.rule.service.MultiSourceRuleService;
import com.webank.wedatasphere.qualitis.rule.service.RuleLimitationService;
import com.webank.wedatasphere.qualitis.service.MetaDataService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author howeye
 */
@Service
public class MetaDataServiceImpl implements MetaDataService {

    @Autowired
    private MetaDataClient metaDataClient;
    @Autowired
    private RuleLimitationService ruleLimitationService;
    @Autowired
    private MultiSourceRuleService multiSourceRuleService;
    @Autowired
    private ProjectEventService projectEventService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private ClusterInfoDao clusterInfoDao;
    @Autowired
    private RuleDataSourceDao ruleDataSourceDao;

    private HttpServletRequest httpServletRequest;
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataServiceImpl.class);
    private static final String ACCOUNT_PWD_AUTH = "accountPwd";
    private static final String DPM_AUTH = "dpm";

    public MetaDataServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public GeneralResponse<GetAllResponse<DbInfoDetail>> getUserDbByCluster(GetUserDbByClusterRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check Arguments
        checkRequest(request);
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isNotBlank(request.getProxyUser())) {
            userName = request.getProxyUser();
        }
        GetDbByUserAndClusterRequest getDbByUserAndClusterRequest = new GetDbByUserAndClusterRequest(userName, request.getStartIndex(),
                request.getPageSize(), request.getClusterName());
        DataInfo<DbInfoDetail> response = metaDataClient.getDbByUserAndCluster(getDbByUserAndClusterRequest);

        GetAllResponse<DbInfoDetail> result = new GetAllResponse<>();
        result.setTotal(response.getTotalCount());
        result.setData(response.getContent());
        LOGGER.info("Succeed to get database by cluster, cluster: {}", request.getClusterName());
        return new GeneralResponse<>("200", "{&GET_DB_SUCCESSFULLY}", result);
    }

    @Override
    public GeneralResponse<GetAllResponse<TableInfoDetail>> getUserTableByDbId(GetUserTableByDbIdRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check Arguments
        checkRequest(request);
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isNotBlank(request.getProxyUser())) {
            userName = request.getProxyUser();
        }
        GetTableByUserAndDbRequest getTableByUserAndDbRequest = new GetTableByUserAndDbRequest(userName, request.getStartIndex(),
                request.getPageSize(), request.getClusterName(), request.getDbName());
        DataInfo<TableInfoDetail> response = metaDataClient.getTableByUserAndDb(getTableByUserAndDbRequest);

        GetAllResponse<TableInfoDetail> result = new GetAllResponse<>();
        result.setTotal(response.getTotalCount());
        result.setData(response.getContent());

        LOGGER.info("Succeed to get table by database. database: {}.{}", request.getClusterName(), request.getDbName());
        return new GeneralResponse<>("200", "{&GET_TABLE_SUCCESSFULLY}", result);
    }

    @Override
    public GeneralResponse<GetAllResponse<CsTableInfoDetail>> getUserTableByCsId(GetUserTableByCsIdRequest request)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        checkRequest(request);
        request.setLoginUser(HttpUtils.getUserName(httpServletRequest));
        DataInfo<CsTableInfoDetail> response = metaDataClient.getTableByCsId(request);
        GetAllResponse<CsTableInfoDetail> result = new GetAllResponse<>();
        result.setTotal(response.getTotalCount());
        result.setData(response.getContent());

        LOGGER.info("Succeed to get table by context service ID. csId: {}", request.getCsId());
        return new GeneralResponse<>("200", "{&GET_TABLE_SUCCESSFULLY}", result);
    }

    @Override
    public GeneralResponse<GetAllResponse<ColumnInfoDetail>> getUserColumnByTableId(GetUserColumnByTableIdRequest request)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check Arguments
        checkRequest(request);
        // Get login user
        String userName = "";
        if (request.getLoginUser() != null) {
            userName = request.getLoginUser();
            LOGGER.info("Recover user[{}] from is get column info.", userName);
        } else {
            userName = HttpUtils.getUserName(httpServletRequest);;
        }
        if (StringUtils.isNotBlank(request.getProxyUser())) {
            userName = request.getProxyUser();
        }
        GetColumnByUserAndTableRequest getColumnByUserAndTableRequest = new GetColumnByUserAndTableRequest(userName, request.getStartIndex(),
                request.getPageSize(), request.getClusterName(), request.getDbName(), request.getTableName());
        DataInfo<ColumnInfoDetail> response = metaDataClient.getColumnByUserAndTable(getColumnByUserAndTableRequest);

        GetAllResponse<ColumnInfoDetail> result = new GetAllResponse<>();
        result.setTotal(response.getTotalCount());
        result.setData(response.getContent());

        LOGGER.info("Succeed to get column by table. table: {}.{}.{}", request.getClusterName(), request.getDbName(), request.getTableName());
        return new GeneralResponse<>("200", "{&GET_COLUMN_SUCCESSFULLY}", result);
    }

    @Override
    public GeneralResponse<GetAllResponse<ColumnInfoDetail>> getUserColumnByCsId(GetUserColumnByCsRequest request)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        checkRequest(request);
        request.setLoginUser(HttpUtils.getUserName(httpServletRequest));
        DataInfo<ColumnInfoDetail> response = metaDataClient.getColumnByCsId(request);
        GetAllResponse<ColumnInfoDetail> result = new GetAllResponse<>();
        result.setTotal(response.getTotalCount());
        result.setData(response.getContent());

        LOGGER.info("Succeed to get column by context service ID. csId: {}", request.getCsId());
        return new GeneralResponse<>("200", "{&GET_COLUMN_SUCCESSFULLY}", result);
    }

    @Override
    public GeneralResponse<GetAllClusterResponse<ClusterInfoDetail>> getUserCluster(GetUserClusterRequest request)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check Arguments
        checkRequest(request);
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);

        GetClusterByUserRequest getClusterByUserRequest = new GetClusterByUserRequest(userName, request.getStartIndex(), request.getPageSize());
        DataInfo<ClusterInfoDetail> response = metaDataClient.getClusterByUser(getClusterByUserRequest);

        GetAllClusterResponse<ClusterInfoDetail> result = new GetAllClusterResponse<>();
        result.setOptionalClusters(ruleLimitationService.getLimitClusters());
        result.setTotal(response.getTotalCount());
        result.setData(response.getContent());

        LOGGER.info("Succeed to get cluster. response: {}", result);
        return new GeneralResponse<>("200", "{&GET_CLUSTER_SUCCESSFULLY}", result);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, UnExpectedRequestException.class})
    public String addMultiDbRules(MulDbRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        MulDbRequest.checkRequst(request);
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        LOGGER.info("Start to get all tables with source database and target database.");
        List<String> sourceTableName = new ArrayList<>();
        List<String> targetTableName = new ArrayList<>();
        if (request.getSourceLinkisDataSourceId() == null) {
            GetUserTableByDbIdRequest sourceRequest = new GetUserTableByDbIdRequest(0, Integer.MAX_VALUE, request.getClusterName(), request.getSourceDb());
            sourceRequest.setProxyUser(request.getProxyUser());
            GeneralResponse<GetAllResponse<TableInfoDetail>> sourceTableInfos = getUserTableByDbId(sourceRequest);

            if (sourceTableInfos.getData().getTotal() <= 0) {
                throw new UnExpectedRequestException("Source database has no tables.");
            }
            sourceTableName.addAll(sourceTableInfos.getData().getData().stream().map(TableInfoDetail::getTableName).collect(Collectors.toList()));
        } else {
            Map response = getTablesByDataSource(request.getClusterName(), request.getProxyUser(), request.getSourceLinkisDataSourceId(), request.getSourceDb());
            List<String> tables = (List<String>) response.get("tables");
            for (String table : tables) {
                sourceTableName.add(table);
            }
        }

        if (request.getTargetLinkisDataSourceId() == null) {
            GetUserTableByDbIdRequest targetRequest = new GetUserTableByDbIdRequest(0, Integer.MAX_VALUE, request.getClusterName(), request.getTargetDb());
            targetRequest.setProxyUser(request.getProxyUser());
            GeneralResponse<GetAllResponse<TableInfoDetail>> targetTableInfos = getUserTableByDbId(targetRequest);

            if (targetTableInfos.getData().getTotal() <= 0) {
                throw new UnExpectedRequestException("Target database has no tables.");
            }
            targetTableName.addAll(targetTableInfos.getData().getData().stream().map(TableInfoDetail::getTableName).collect(Collectors.toList()));
        } else {
            Map response = getTablesByDataSource(request.getClusterName(), request.getProxyUser(), request.getTargetLinkisDataSourceId(), request.getTargetDb());
            List<String> tables = (List<String>) response.get("tables");
            for (String table : tables) {
                targetTableName.add(table);
            }
        }

        // Collect the table with the same name that exists in the source database and the target database.
        sourceTableName.retainAll(targetTableName);

        Set<String> ruleFailedTable = new HashSet<>();
        Long projectId = request.getProjectId();
        List<String> ruleTables = new ArrayList<>(ruleDataSourceDao.findByProjectId(projectId).stream().map(RuleDataSource::getTableName).distinct()
            .collect(Collectors.toList()));
        // Filter tables for which rules have been created.
        sourceTableName = sourceTableName.stream().filter(tableName -> ! ruleTables.contains(tableName)).collect(Collectors.toList());
        // Rule name index starts with the last created rule.
        int ruleIndex = ruleDao.findByProject(projectDao.findById(projectId)).size();
        // Filter tables with black list.
        sourceTableName = filterTablesWithBlackList(request.getBlackList(), sourceTableName);
        // White list.
        List<String> whiteList = request.getWhiteList();
        // Filter special table to compare with accuracy template.
        List<FilterRequest> filterRequests = request.getFilterRequests();
        List<FilterRequest> sameTableFilterRequests = filterRequests.stream().filter(filterRequest ->
            filterRequest.getSourceTable().equals(filterRequest.getTargetTable())).collect(Collectors.toList());
        List<String> filterSameTableName = sameTableFilterRequests.stream().map(FilterRequest::getSourceTable).collect(Collectors.toList());
        sourceTableName.removeAll(filterSameTableName);

        filterRequests.removeAll(sameTableFilterRequests);
        List<String> filterDiffTableName = filterRequests.stream().map(filterRequest -> filterRequest.getSourceTable() + SpecCharEnum.COLON.getValue() + filterRequest.getTargetTable()).collect(Collectors.toList());
        whiteList.removeAll(filterDiffTableName);
        filterRequests.addAll(sameTableFilterRequests);

        // Check flag: create rules in a loop, and only do permission verification for the first time.
        boolean check = true;
        int sourceTableSize = sourceTableName.size();
        int filterRequestsSize = filterRequests.size();
        int total = ruleIndex + sourceTableSize + whiteList.size() + filterRequestsSize;
        int i = ruleIndex;
        for (; i < ruleIndex + sourceTableSize; i ++) {
            String currentSameTable = sourceTableName.get(i - ruleIndex);
            try {
                AddMultiSourceRuleRequest addMultiSourceRuleRequest = constructRequest(request, null, currentSameTable, currentSameTable, i, total, loginUser);
                LOGGER.info("Start to add {}th multi source rule.", i);
                if (i > 0) {
                    check = false;
                }
                multiSourceRuleService.addMultiSourceRule(addMultiSourceRuleRequest, check);
            } catch (Exception e) {
                LOGGER.error("One of rule failed to add, rule index:[{}], table name: [{}], exception:{}", i, currentSameTable, e.getMessage());
                ruleFailedTable.add(currentSameTable);
            }

            LOGGER.info("Finish to add {}th multi source rule.", i);
        }
        // With white list.
        withWhiteListAndFilterRequest(i, ruleIndex, sourceTableSize, total, whiteList, filterRequests, request, loginUser, check);
        // Record the table that failed to create a rule.
        saveRuleFailedTableInLabel(ruleFailedTable, projectId);
//        projectEventService.record(projectId, loginUser, "create multi-source rule with dbs", "Success: " + (total - ruleFailedTable.size()) + "; Failed: " + ruleFailedTable.size(), EventTypeEnum.MODIFY_PROJECT.getCode());
        return request.getSourceDb() + " vs " + request.getSourceDb() + ": " + "{&CONTINUE_CREATE_RULES}";
    }

    @Override
    public GeneralResponse<Map> getAllDataSourceTypes(String clusterName, String proxyUser) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isNotBlank(proxyUser)) {
            userName = proxyUser;
        }

        return metaDataClient.getAllDataSourceTypes(clusterName, userName);
    }

    @Override
    public GeneralResponse<Map> getDataSourceEnv(String clusterName, String proxyUser)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isNotBlank(proxyUser)) {
            userName = proxyUser;
        }

        return metaDataClient.getDataSourceEnv(clusterName, userName);
    }

    @Override
    public GeneralResponse<Map> getDataSourceInfoPage(String clusterName, String proxyUser, int page, int size, String searchName,
        Long typeId) throws UnExpectedRequestException, MetaDataAcquireFailedException, UnsupportedEncodingException {
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isNotBlank(proxyUser)) {
            userName = proxyUser;
        }
        return metaDataClient.getDataSourceInfoPage(clusterName, userName, page, size, searchName, typeId);
    }

    @Override
    public GeneralResponse<Map> getDataSourceVersions(String clusterName, String proxyUser, Long dataSourceId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isNotBlank(proxyUser)) {
            userName = proxyUser;
        }
        return metaDataClient.getDataSourceVersions(clusterName, userName, dataSourceId);
    }

    @Override
    public GeneralResponse<Map> getDataSourceInfoDetail(String clusterName, String proxyUser, Long dataSourceId, Long versionId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isNotBlank(proxyUser)) {
            userName = proxyUser;
        }
        return metaDataClient.getDataSourceInfoDetail(clusterName, userName, dataSourceId, versionId);
    }

    @Override
    public GeneralResponse<Map> getDataSourceKeyDefine(String clusterName, String proxyUser, Long keyId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isNotBlank(proxyUser)) {
            userName = proxyUser;
        }
        return metaDataClient.getDataSourceKeyDefine(clusterName, userName, keyId);
    }

    @Override
    public GeneralResponse<Map> connectDataSource(String clusterName, String proxyUser, DataSourceConnectRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException, IOException {
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isNotBlank(proxyUser)) {
            userName = proxyUser;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(request);
        Map<String, Object> jsonMap = objectMapper.readValue(jsonRequest, Map.class);
        Map<String, Object> connectMap = (Map<String, Object>) jsonMap.get("connectParams");
        String authType = (String) connectMap.get("authType");
        if (DPM_AUTH.equals(authType)) {
            // remove sec properties
            String dk = (String) connectMap.get("dk");
            if (StringUtils.isEmpty(dk)) {
                connectMap.remove("dk");
            }
        }
        jsonMap.remove("connectParams");
        jsonMap.put("connectParams", connectMap);
        jsonRequest = objectMapper.writeValueAsString(jsonMap);
        return metaDataClient.connectDataSource(clusterName, userName, jsonRequest);
    }

    @Override
    public GeneralResponse<Map> publishDataSource(String clusterName, String proxyUser, Long dataSourceId, Long versionId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isNotBlank(proxyUser)) {
            userName = proxyUser;
        }
        return metaDataClient.publishDataSource(clusterName, userName, dataSourceId, versionId);
    }

    @Override
    public GeneralResponse<Map> expireDataSource(String clusterName, String proxyUser, Long dataSourceId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isNotBlank(proxyUser)) {
            userName = proxyUser;
        }
        return metaDataClient.expireDataSource(clusterName, userName, dataSourceId);
    }

    @Override
    public GeneralResponse<Map> modifyDataSource(String clusterName, String proxyUser, Long dataSourceId, DataSourceModifyRequest request)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, JsonProcessingException {
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isNotBlank(proxyUser)) {
            userName = proxyUser;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(request);
        return metaDataClient.modifyDataSource(clusterName, userName, dataSourceId, jsonRequest);
    }

    @Override
    public GeneralResponse<Map> modifyDataSourceParam(String clusterName, String proxyUser, Long dataSourceId, DataSourceParamModifyRequest request)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, IOException {
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isNotBlank(proxyUser)) {
            userName = proxyUser;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(request);
        Map<String, Object> jsonMap = objectMapper.readValue(jsonRequest, Map.class);
        Map<String, Object> connectMap = (Map<String, Object>) jsonMap.get("connectParams");
        String authType = (String) connectMap.get("authType");
        if (ACCOUNT_PWD_AUTH.equals(authType)) {
            // remove sec properties
            connectMap.remove("dk");
            connectMap.remove("appid");
            connectMap.remove("objectid");
            connectMap.remove("mkPrivate");
        }
        jsonMap.remove("connectParams");
        jsonMap.put("connectParams", connectMap);
        jsonRequest = objectMapper.writeValueAsString(jsonMap);
        return metaDataClient.modifyDataSourceParam(clusterName, userName, dataSourceId, jsonRequest);
    }

    @Override
    public GeneralResponse<Map> createDataSource(String clusterName, String proxyUser, DataSourceModifyRequest request)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, JsonProcessingException {
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isNotBlank(proxyUser)) {
            userName = proxyUser;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(request);
        return metaDataClient.createDataSource(clusterName, userName, jsonRequest);
    }

    @Override
    public Map getDbsByDataSource(String clusterName, String proxyUser, Long dataSourceId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isNotBlank(proxyUser)) {
            userName = proxyUser;
        }
        GeneralResponse<Map> dataSourceConnectParams = metaDataClient.getDataSourceConnectParams(clusterName, userName, dataSourceId, null);
        Map connectParamsReal = (Map) dataSourceConnectParams.getData().get("connectParams");
        if (connectParamsReal.size() == 0) {
            throw new UnExpectedRequestException("{&THE_DATASOURCE_IS_NOT_DEPLOYED}");
        }
        return metaDataClient.getDbsByDataSource(clusterName, userName, dataSourceId);
    }

    @Override
    public Map getTablesByDataSource(String clusterName, String proxyUser, Long dataSourceId, String dbName) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isNotBlank(proxyUser)) {
            userName = proxyUser;
        }
        return metaDataClient.getTablesByDataSource(clusterName, userName, dataSourceId, dbName);
    }

    @Override
    public GeneralResponse<GetAllResponse<ColumnInfoDetail>> getColumnsByDataSource(String clusterName, String proxyUser, Long dataSourceId, String dbName, String tableName) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isNotBlank(proxyUser)) {
            userName = proxyUser;
        }
        DataInfo<ColumnInfoDetail> response = metaDataClient.getColumnsByDataSource(clusterName, userName, dataSourceId, dbName, tableName);

        GetAllResponse<ColumnInfoDetail> result = new GetAllResponse<>();
        result.setTotal(response.getTotalCount());
        result.setData(response.getContent());

        LOGGER.info("Succeed to get column by data source table. table: {}.{}.{}", clusterName, dbName, tableName);
        return new GeneralResponse<>("200", "{&GET_COLUMN_SUCCESSFULLY}", result);
    }

    private List<String> filterTablesWithBlackList(List<String> blackList, List<String> sourceTableName) throws UnExpectedRequestException {
        for (String black : blackList) {
            int filterType = Integer.parseInt(black.split(SpecCharEnum.COLON.getValue())[0]);
            if (BlackListFilterTypeEnum.BEGIN_WITH.getCode().intValue() == filterType) {
                String blackStr = black.split(SpecCharEnum.COLON.getValue())[1];
                sourceTableName = sourceTableName.stream().filter(tableName -> ! tableName.startsWith(blackStr)).collect(Collectors.toList());
            } else if (BlackListFilterTypeEnum.CONTAINS.getCode().intValue() == filterType) {
                String blackStr = black.split(SpecCharEnum.COLON.getValue())[1];
                sourceTableName = sourceTableName.stream().filter(tableName -> ! tableName.contains(blackStr)).collect(Collectors.toList());
            } else if (BlackListFilterTypeEnum.END_WITH.getCode().intValue() == filterType) {
                String blackStr = black.split(SpecCharEnum.COLON.getValue())[1];
                sourceTableName = sourceTableName.stream().filter(tableName -> ! tableName.endsWith(blackStr)).collect(Collectors.toList());
            } else if (BlackListFilterTypeEnum.SAME_TABLE.getCode().intValue() == filterType) {
                sourceTableName.clear();
            } else if (BlackListFilterTypeEnum.REG_TABLE.getCode().intValue() == filterType) {
                String blackStr = black.split(SpecCharEnum.COLON.getValue())[1];
                Pattern blackPattern = Pattern.compile(blackStr);
                sourceTableName = sourceTableName.stream().filter(tableName -> ! blackPattern.matcher(tableName).matches()).collect(Collectors.toList());
            } else {
                throw new UnExpectedRequestException("Black list filter filter type error.");
            }
        }

        return sourceTableName;
    }

    private void saveRuleFailedTableInLabel(Set<String> failedTable, Long projectId) {
        if (failedTable.size() > 0) {
            Project projectInDb = projectDao.findById(projectId);
            projectService.addProjectLabels(failedTable, projectInDb);
            projectDao.saveProject(projectInDb);
        }
    }

    private void withWhiteListAndFilterRequest(int i, int ruleIndex, int sourceTableSize, int total, List<String> whiteList,
        List<FilterRequest> filterRequests, MulDbRequest request, String loginUser, boolean check) {
        for (; i < total - filterRequests.size(); i ++) {
            String currentTables = whiteList.get(i - ruleIndex - sourceTableSize);
            String currentSourceTable = currentTables.split(SpecCharEnum.COLON.getValue())[0];
            String currentTargetTable = currentTables.split(SpecCharEnum.COLON.getValue())[1];
            try {
                // Construct add multi source rule request.
                AddMultiSourceRuleRequest addMultiSourceRuleRequest = constructRequest(request, null, currentSourceTable, currentTargetTable, i, total, loginUser);
                LOGGER.info("Start to add {}th multi source rule.", i);
                if (ruleIndex > 0) {
                    check = false;
                }
                multiSourceRuleService.addMultiSourceRule(addMultiSourceRuleRequest, check);
            } catch (Exception e) {
                LOGGER.error("One of rule failed to add, rule index:[{}], table name: [{}], exception:{}", i, currentSourceTable + " VS "
                    + currentTargetTable, e.getMessage());
            }

            LOGGER.info("Finish to add {}th multi source rule.", i);
        }
        for (; i < total; i ++) {
            FilterRequest filterRequest = filterRequests.get(i - ruleIndex - sourceTableSize - whiteList.size());
            try {
                AddMultiSourceRuleRequest addMultiSourceRuleRequest = constructRequest(request, filterRequest, "", "", i, total, loginUser);
                LOGGER.info("Start to add {}th multi source rule.", i);
                if (i > 0) {
                    check = false;
                }
                multiSourceRuleService.addMultiSourceRule(addMultiSourceRuleRequest, check);
            } catch (Exception e) {
                LOGGER.error("One of rule failed to add, rule index:[{}], table name: [{}], exception:{}", i, filterRequest.getSourceTable() + SpecCharEnum.COMMA.getValue() + filterRequest.getTargetTable(), e.getMessage());
            }
            LOGGER.info("Finish to add {}th multi source rule.", i);
        }
    }

    private AddMultiSourceRuleRequest constructRequest(MulDbRequest request, FilterRequest filterRequest,
        String currentSourceTable, String currentTargetTable, int index, int total, String loginUser)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, IOException {
        LOGGER.info("Start to construct add multi source rule request.");
        // rule basic info.
        AddMultiSourceRuleRequest addMultiSourceRuleRequest = new AddMultiSourceRuleRequest();
        basicInfo(addMultiSourceRuleRequest, request, index, total);
        MultiDataSourceConfigRequest sourceConfigRequest = new MultiDataSourceConfigRequest();
        sourceConfigRequest.setLinkisDataSourceName(request.getSourceLinkisDataSourceName());
        sourceConfigRequest.setLinkisDataSourceType(request.getSourceLinkisDataSourceType());
        sourceConfigRequest.setLinkisDataSourceId(request.getSourceLinkisDataSourceId());
        sourceConfigRequest.setProxyUser(request.getProxyUser());
        sourceConfigRequest.setDbName(request.getSourceDb());
        sourceConfigRequest.setContextService(false);

        MultiDataSourceConfigRequest targetConfigRequest = new MultiDataSourceConfigRequest();
        targetConfigRequest.setLinkisDataSourceName(request.getTargetLinkisDataSourceName());
        targetConfigRequest.setLinkisDataSourceType(request.getTargetLinkisDataSourceType());
        targetConfigRequest.setLinkisDataSourceId(request.getTargetLinkisDataSourceId());
        targetConfigRequest.setProxyUser(request.getProxyUser());
        targetConfigRequest.setDbName(request.getTargetDb());
        targetConfigRequest.setContextService(false);


        if (filterRequest == null) {
            // source table and target table
            sourceConfigRequest.setTableName(currentSourceTable);
            sourceConfigRequest.setFilter("true");
            targetConfigRequest.setTableName(currentTargetTable);
            targetConfigRequest.setFilter("true");

            addMultiSourceRuleRequest.setMultiSourceRuleTemplateId(20L);
            List<AlarmConfigRequest> alarmConfigRequests = new ArrayList<>();
            alarmConfigRequests.add(new AlarmConfigRequest(645L, CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.EQUAL.getCode(), 0.0));
            addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        } else {
            sourceConfigRequest.setTableName(filterRequest.getSourceTable());
            if (StringUtils.isNotBlank(filterRequest.getSourceFilter())) {
                sourceConfigRequest.setFilter("! (" + filterRequest.getSourceFilter() + ")");
            } else {
                sourceConfigRequest.setFilter("true");
            }
            targetConfigRequest.setTableName(filterRequest.getTargetTable());
            if (StringUtils.isNotBlank(filterRequest.getTargetFilter())) {
                targetConfigRequest.setFilter("! (" + filterRequest.getTargetFilter() + ")");
            } else {
                targetConfigRequest.setFilter("true");
            }
            addMultiSourceRuleRequest.setMultiSourceRuleTemplateId(17L);
            List<AlarmConfigRequest> alarmConfigRequests = new ArrayList<>();
            alarmConfigRequests.add(new AlarmConfigRequest(33L, CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.EQUAL.getCode(), 0.0));
            addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        }
        addMultiSourceRuleRequest.setSource(sourceConfigRequest);
        addMultiSourceRuleRequest.setTarget(targetConfigRequest);

        // All fields mappings
        LOGGER.info("Start to get all fields with source database's table and target database's table.");
        List<ColumnInfoDetail> sourceFields;
        List<ColumnInfoDetail> targetFields;
        if (request.getSourceLinkisDataSourceId() == null) {
            GetUserColumnByTableIdRequest getUserColumnSourceRequest = new GetUserColumnByTableIdRequest(0, Integer.MAX_VALUE, request.getClusterName(), request.getSourceDb()
                , filterRequest == null ? currentSourceTable : filterRequest.getSourceTable());
            getUserColumnSourceRequest.setProxyUser(request.getProxyUser());
            getUserColumnSourceRequest.setLoginUser(loginUser);
            sourceFields = getUserColumnByTableId(getUserColumnSourceRequest).getData().getData();
        } else {
            sourceFields = getColumnsByDataSource(request.getClusterName(), request.getProxyUser(), request.getSourceLinkisDataSourceId(), request.getSourceDb()
                , currentSourceTable).getData().getData();
        }

        if (request.getTargetLinkisDataSourceId() == null) {
            GetUserColumnByTableIdRequest getUserColumnTargetRequest = new GetUserColumnByTableIdRequest(0, Integer.MAX_VALUE, request.getClusterName(), request.getTargetDb()
                , filterRequest == null ? currentTargetTable : filterRequest.getTargetTable());
            getUserColumnTargetRequest.setProxyUser(request.getProxyUser());
            getUserColumnTargetRequest.setLoginUser(loginUser);
            targetFields =  getUserColumnByTableId(getUserColumnTargetRequest).getData().getData();
        } else {
            targetFields = getColumnsByDataSource(request.getClusterName(), request.getProxyUser(), request.getTargetLinkisDataSourceId(), request.getTargetDb()
                , currentTargetTable).getData().getData();
        }
        if (CollectionUtils.isEmpty(sourceFields) || CollectionUtils.isEmpty(targetFields)) {
            throw new UnExpectedRequestException("There is table which has none field");
        }
        LOGGER.info("Success to get all fields with source database's table and target database's table.");

        // Sorted columns.
        Collections.sort(sourceFields, new Comparator<ColumnInfoDetail>() {
            @Override
            public int compare(ColumnInfoDetail front, ColumnInfoDetail back) {
                return front.getFieldName().compareTo(back.getFieldName());
            }
        });
        Collections.sort(targetFields, new Comparator<ColumnInfoDetail>() {
            @Override
            public int compare(ColumnInfoDetail front, ColumnInfoDetail back) {
                return front.getFieldName().compareTo(back.getFieldName());
            }
        });
        String sourceFieldStr = sourceFields.stream().map(ColumnInfoDetail::getFieldName).collect(Collectors.joining(SpecCharEnum.COMMA.getValue()));
        String sourceFieldTypeStr = sourceFields.stream().map(ColumnInfoDetail::getDataType).collect(Collectors.joining(SpecCharEnum.COMMA.getValue()));
        String targetFieldStr = targetFields.stream().map(ColumnInfoDetail::getFieldName).collect(Collectors.joining(SpecCharEnum.COMMA.getValue()));
        String targetFieldTypeStr = targetFields.stream().map(ColumnInfoDetail::getDataType).collect(Collectors.joining(SpecCharEnum.COMMA.getValue()));
        if (sourceFields.size() == 0 || targetFields.size() == 0 || sourceFields.size() != targetFields.size()
            || ! sourceFieldStr.equals(targetFieldStr) || ! sourceFieldTypeStr.equals(targetFieldTypeStr)) {
            throw new UnExpectedRequestException("Create multi-db rules failed, because the fields' name of table is different.");
        }
        List<MultiDataSourceJoinConfigRequest> mappings = new ArrayList<>(sourceFields.size());
        for (int j = 0; j < sourceFields.size() && j < targetFields.size(); j ++) {
            ColumnInfoDetail currentSourceField = sourceFields.get(j);
            ColumnInfoDetail currentTargetField = targetFields.get(j);
            if (filterRequest != null && CollectionUtils.isNotEmpty(filterRequest.getFilterColumnList()) && filterRequest.getFilterColumnList().contains(currentSourceField.getFieldName())) {
                continue;
            }
            if (currentSourceField.getFieldName().equals(currentTargetField.getFieldName()) && currentSourceField.getDataType().equals(currentTargetField.getDataType())) {
                // TODO: continue
                MultiDataSourceJoinConfigRequest multiDataSourceJoinConfigRequest = new MultiDataSourceJoinConfigRequest();
                MultiDataSourceJoinColumnRequest leftJoinColumnRequest = new MultiDataSourceJoinColumnRequest("tmp1.".concat(currentSourceField.getFieldName()), currentSourceField.getDataType());
                MultiDataSourceJoinColumnRequest rightJoinColumnRequest = new MultiDataSourceJoinColumnRequest("tmp2.".concat(currentTargetField.getFieldName()), currentTargetField.getDataType());
                multiDataSourceJoinConfigRequest.setOperation(MappingOperationEnum.EQUAL.getCode());
                multiDataSourceJoinConfigRequest.setLeft(Arrays.asList(leftJoinColumnRequest));
                multiDataSourceJoinConfigRequest.setRight(Arrays.asList(rightJoinColumnRequest));
                multiDataSourceJoinConfigRequest.setLeftStatement("tmp1.".concat(currentSourceField.getFieldName()));
                multiDataSourceJoinConfigRequest.setRightStatement("tmp2.".concat(currentTargetField.getFieldName()));
                mappings.add(multiDataSourceJoinConfigRequest);
            } else {
                throw new UnExpectedRequestException("Create multi-db rules failed, because the fields' name of tables is different");
            }
        }

        addMultiSourceRuleRequest.setMappings(mappings);
        addMultiSourceRuleRequest.setLoginUser(loginUser);
        LOGGER.info("Success to construct add multi source rule request. Request[{}]", new ObjectMapper().writeValueAsString(addMultiSourceRuleRequest));
        return addMultiSourceRuleRequest;
    }

    private void basicInfo(AddMultiSourceRuleRequest addMultiSourceRuleRequest, MulDbRequest request, int index, int total) {
        addMultiSourceRuleRequest.setProjectId(request.getProjectId());
        addMultiSourceRuleRequest.setClusterName(request.getClusterName());
        addMultiSourceRuleRequest.setRuleName(request.getRuleName() + "_" + index + "_" + total);

        addMultiSourceRuleRequest.setAlert(request.getAlert());
        addMultiSourceRuleRequest.setAlertLevel(request.getAlertLevel());
        addMultiSourceRuleRequest.setAlertReceiver(request.getAlertReceiver());
        addMultiSourceRuleRequest.setAlarm(true);
        addMultiSourceRuleRequest.setDeleteFailCheckResult(true);
        addMultiSourceRuleRequest.setAbortOnFailure(request.getAbortOnFailure());
        addMultiSourceRuleRequest.setStaticStartupParam(request.getStaticStartupParam());
        addMultiSourceRuleRequest.setSpecifyStaticStartupParam(request.getSpecifyStaticStartupParam());
    }

    private void checkRequest(GetUserClusterRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
    }

    private void checkRequest(GetUserColumnByTableIdRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }

        checkString(request.getClusterName(), "cluster name");
        checkString(request.getDbName(), "db name");
        checkString(request.getTableName(), "table name");
    }

    private void checkRequest(GetUserTableByDbIdRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }

        checkString(request.getClusterName(), "cluster name");
        checkString(request.getDbName(), "db name");
    }

    private void checkRequest(GetUserColumnByCsRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        if (request.getCsId() == null || "".equals(request.getCsId())) {
            throw new UnExpectedRequestException("{&CSID_CAN_NOT_BE_NULL}");
        }
        if (request.getContextKey() == null || "".equals(request.getContextKey())) {
            throw new UnExpectedRequestException("{&CONTEXT_KEY_CAN_NOT_BE_NULL}");
        }
    }

    private void checkRequest(GetUserTableByCsIdRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        if (request.getCsId() == null || "".equals(request.getCsId())) {
            throw new UnExpectedRequestException("{&CSID_CAN_NOT_BE_NULL}");
        }
        if (request.getNodeName() == null || "".equals(request.getNodeName())) {
            throw new UnExpectedRequestException("{&NODENAME_CAN_NOT_BE_NULL}");
        }


        checkString(request.getClusterName(), "cluster name");
    }

    private void checkRequest(GetUserDbByClusterRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }

        checkString(request.getClusterName(), "cluster name");
    }

    private void checkString(String str, String strName) throws UnExpectedRequestException {
        if (StringUtils.isBlank(str)) {
            throw new UnExpectedRequestException(strName + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }
}
