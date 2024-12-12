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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constant.BlackListFilterTypeEnum;
import com.webank.wedatasphere.qualitis.constant.DepartmentSourceTypeEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constant.TemplateFunctionNameEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.dto.DataVisibilityPermissionDto;
import com.webank.wedatasphere.qualitis.entity.*;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.DataStandardClient;
import com.webank.wedatasphere.qualitis.metadata.client.LinkisMetaDataManager;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.client.OperateCiService;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.request.*;
import com.webank.wedatasphere.qualitis.metadata.response.CmdbDepartmentResponse;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.DepartmentSubResponse;
import com.webank.wedatasphere.qualitis.metadata.response.cluster.ClusterInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.datasource.LinkisDataSourceInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.datasource.LinkisDataSourceParamsResponse;
import com.webank.wedatasphere.qualitis.metadata.response.db.DbInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.CsTableInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableInfoDetail;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.request.*;
import com.webank.wedatasphere.qualitis.response.DepartmentSubInfoResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllClusterResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.constant.*;
import com.webank.wedatasphere.qualitis.rule.dao.LinkisDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.entity.DataVisibility;
import com.webank.wedatasphere.qualitis.rule.entity.LinkisDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.request.AlarmConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplateArgumentRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.AddMultiSourceRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceConfigRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinColumnRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.MultiDataSourceJoinConfigRequest;
import com.webank.wedatasphere.qualitis.rule.service.LinkisDataSourceService;
import com.webank.wedatasphere.qualitis.rule.service.MultiSourceRuleService;
import com.webank.wedatasphere.qualitis.rule.service.RuleLimitationService;
import com.webank.wedatasphere.qualitis.service.*;
import com.webank.wedatasphere.qualitis.util.CryptoUtils;
import com.webank.wedatasphere.qualitis.util.DateUtils;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.map.CustomObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author allenzhou
 */
@Service
public class MetaDataServiceImpl implements MetaDataService {
    @Autowired
    private LinkisConfig linkisConfig;

    @Autowired
    private MetaDataClient metaDataClient;
    @Autowired
    private DataStandardClient dataStandardClient;
    @Autowired
    private RuleLimitationService ruleLimitationService;
    @Autowired
    private MultiSourceRuleService multiSourceRuleService;
    @Autowired
    private SubDepartmentPermissionService subDepartmentPermissionService;
    @Autowired
    private DataVisibilityService dataVisibilityService;
    @Autowired
    private ProjectEventService projectEventService;
    @Autowired
    private OperateCiService operateCiService;
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
    @Autowired
    private LinkisDataSourceDao linkisDataSourceDao;
    @Autowired
    private LinkisDataSourceService linkisDataSourceService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private RoleService roleService;
    @Autowired
    private LinkisMetaDataManager linkisMetaDataManager;
    @Autowired
    private DepartmentService departmentService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataServiceImpl.class);

    private final String INFO = "info";
    private final String ENV_NAME_REGEX = "^[^,:]*$";

    @Value("${department.data_source_from: hr}")
    private String departmentSourceType;

    private HttpServletRequest httpServletRequest;

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
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_DB_SUCCESSFULLY}", result);
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
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_TABLE_SUCCESSFULLY}", result);
    }

    @Override
    public GeneralResponse<GetAllResponse<CsTableInfoDetail>> getUserTableByCsId(GetUserTableByCsIdRequest request)
            throws Exception {
        checkRequest(request);
        request.setLoginUser(HttpUtils.getUserName(httpServletRequest));
        DataInfo<CsTableInfoDetail> response = metaDataClient.getTableByCsId(request);
        GetAllResponse<CsTableInfoDetail> result = new GetAllResponse<>();
        result.setTotal(response.getTotalCount());
        result.setData(response.getContent());

        LOGGER.info("Succeed to get table by context service ID. csId: {}", request.getCsId());
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_TABLE_SUCCESSFULLY}", result);
    }

    @Override
    public GeneralResponse<GetAllResponse<ColumnInfoDetail>> getUserColumnByTableId(GetUserColumnByTableIdRequest request)
            throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check Arguments
        checkRequest(request);
        // Get login user
        String userName;
        if (request.getLoginUser() != null) {
            userName = request.getLoginUser();
            LOGGER.info("Recover user[{}] from is get column info.", userName);
        } else {
            userName = HttpUtils.getUserName(httpServletRequest);
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
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_COLUMN_SUCCESSFULLY}", result);
    }

    @Override
    public GeneralResponse<GetAllResponse<ColumnInfoDetail>> getUserColumnByCsId(GetUserColumnByCsRequest request)
            throws Exception {
        checkRequest(request);
        request.setLoginUser(HttpUtils.getUserName(httpServletRequest));
        DataInfo<ColumnInfoDetail> response = metaDataClient.getColumnByCsId(request);
        GetAllResponse<ColumnInfoDetail> result = new GetAllResponse<>();
        result.setTotal(response.getTotalCount());
        result.setData(response.getContent());

        LOGGER.info("Succeed to get column by context service ID. csId: {}", request.getCsId());
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_COLUMN_SUCCESSFULLY}", result);
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
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_CLUSTER_SUCCESSFULLY}", result);
    }

    @Override
    public String getDbFromDatamap(String searchKey, String clusterName, String proxyUser) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        Map<String, Object> response = dataStandardClient.getDatabase(searchKey, StringUtils.isEmpty(proxyUser) ? loginUser : proxyUser);
        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(clusterName);
        if (clusterInfo == null) {
            throw new UnExpectedRequestException(String.format("%s 集群名称不存在", clusterName));
        }

        String id = (String) ((List<Map<String, Object>>) response.get("content")).stream()
                .filter(map -> clusterInfo.getClusterType().contains((String) map.get("value")) && searchKey.equals(map.get("name")))
                .iterator().next().get("id");
        return id;
    }

    @Override
    public Integer getDatasetFromDatamap(String dbId, String datasetName, String clusterName, String proxyUser) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        int page = 0;
        int size = 200;
        Map<String, Object> response = dataStandardClient.getDataset(dbId, datasetName, page, size, StringUtils.isEmpty(proxyUser) ? loginUser : proxyUser);
        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(clusterName);
        if (clusterInfo == null) {
            throw new UnExpectedRequestException(String.format("%s 集群名称不存在", clusterName));
        }
        Double id = (Double) ((List<Map<String, Object>>) response.get("content")).stream()
                .filter(map -> clusterInfo.getClusterType().contains((String) map.get("clusterType")) && BigDecimal.valueOf(Double.parseDouble(dbId)).compareTo(BigDecimal.valueOf((Double) map.get("dbId"))) == 0 && datasetName.equals(map.get("datasetName")))
                .iterator().next().get("datasetId");
        return id.intValue();
    }

    @Override
    public Map<String, Object> getColumnFromDatamap(Long datasetId, String fieldName, String proxyUser) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return dataStandardClient.getColumnStandard(datasetId, fieldName, StringUtils.isEmpty(proxyUser) ? loginUser : proxyUser);
    }

    @Override
    public Map<String, Object> getDataStandardDetailFromDatamap(String stdCode, String source, String proxyUser) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        return dataStandardClient.getDataStandardDetail(stdCode, source, StringUtils.isEmpty(proxyUser) ? loginUser : proxyUser);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, UnExpectedRequestException.class})
    public String addMultiDbRules(MulDbRequest request) throws Exception {
        MulDbRequest.checkRequst(request);
        if (request.getRuleEnable() == null) {
            request.setRuleEnable(true);
        }
        if (request.getUnionAll() == null) {
            request.setUnionAll(false);
        }
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        LOGGER.info("Start to get all tables with source database and target database.");
        if (StringUtils.isEmpty(request.getProxyUser())) {
            request.setProxyUser(loginUser);
        }
        List<String> sourceTableName = new ArrayList<>();
        List<String> targetTableName = new ArrayList<>();
        handleDataOrLinkis(request, sourceTableName, targetTableName);

        // Collect the table with the same name that exists in the source database and the target database.
        sourceTableName.retainAll(targetTableName);

        Set<String> ruleFailedTable = new HashSet<>();
        Long projectId = request.getProjectId();
        List<String> ruleTables = new ArrayList<>(ruleDataSourceDao.findByProjectId(projectId).stream().map(RuleDataSource::getTableName).distinct()
                .collect(Collectors.toList()));
        // Filter tables for which rules have been created.
        sourceTableName = sourceTableName.stream().filter(tableName -> !ruleTables.contains(tableName)).collect(Collectors.toList());
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
        for (; i < ruleIndex + sourceTableSize; i++) {
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
        return request.getSourceDb() + " vs " + request.getSourceDb() + ": " + "{&CONTINUE_CREATE_RULES}";
    }

    private void handleDataOrLinkis(MulDbRequest request, List<String> sourceTableName, List<String> targetTableName) throws Exception {
        if (request.getSourceLinkisDataSourceId() == null) {
            GetUserTableByDbIdRequest sourceRequest = new GetUserTableByDbIdRequest(0, Integer.MAX_VALUE, request.getClusterName(), request.getSourceDb());
            sourceRequest.setProxyUser(request.getProxyUser());
            GeneralResponse<GetAllResponse<TableInfoDetail>> sourceTableInfos = getUserTableByDbId(sourceRequest);

            if (sourceTableInfos.getData().getTotal() <= 0) {
                throw new UnExpectedRequestException("Source database has no tables.");
            }
            sourceTableName.addAll(sourceTableInfos.getData().getData().stream().map(TableInfoDetail::getTableName).collect(Collectors.toList()));
        } else {
            Map<String, Object> response = getTablesByDataSource(request.getClusterName(), request.getProxyUser(), request.getSourceLinkisDataSourceId(), request.getSourceDb(), null);
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
            Map<String, Object> response = getTablesByDataSource(request.getClusterName(), request.getProxyUser(), request.getTargetLinkisDataSourceId(), request.getTargetDb(), null);
            List<String> tables = (List<String>) response.get("tables");
            for (String table : tables) {
                targetTableName.add(table);
            }
        }
    }

    @Override
    public GeneralResponse<Map<String, Object>> getAllDataSourceTypes(String clusterName, String proxyUser) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isNotBlank(proxyUser)) {
            userName = proxyUser;
        }

        GeneralResponse<Map<String, Object>> generalResponse = metaDataClient.getAllDataSourceTypes(clusterName, userName);
        if (generalResponse.getData() != null && generalResponse.getData().containsKey("typeList")) {
            List<Map<String, Object>> typeListMap = (List<Map<String, Object>>) generalResponse.getData().get("typeList");
            ListIterator<Map<String, Object>> listIterator = typeListMap.listIterator();
            while (listIterator.hasNext()) {
                Map<String, Object> typeMap = listIterator.next();
                if(!Arrays.asList(linkisConfig.getDatasourceSupportTypes().split(SpecCharEnum.COMMA.getValue())).contains(typeMap.get("name"))) {
                    listIterator.remove();
                }
            }
        }
        return generalResponse;
    }

    @Override
    public GeneralResponse<Map<String, Object>> getDataSourceEnv(String clusterName, String proxyUser)
            throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isNotBlank(proxyUser)) {
            userName = proxyUser;
        }

        return metaDataClient.getDataSourceEnv(clusterName, userName);
    }

    @Override
    public GeneralResponse<Map<String, Object>> getDataSourceInfoWithAdvance(QueryDataSourceRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException, IOException {
        Page<LinkisDataSource> linkisDataSourcePage = filterLinkisDataSource(request);

        if (linkisDataSourcePage == null || CollectionUtils.isEmpty(linkisDataSourcePage.getContent())) {
            Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
            dataMap.put("totalPage", 0);
            dataMap.put("queryList", Collections.emptyList());
            return new GeneralResponse<>(ResponseStatusConstants.OK, "success", dataMap);
        }
        List<Long> dataSourceIds = linkisDataSourcePage.stream().map(LinkisDataSource::getLinkisDataSourceId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(dataSourceIds)) {
            Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
            dataMap.put("totalPage", 0);
            dataMap.put("queryList", Collections.emptyList());
            return new GeneralResponse<>(ResponseStatusConstants.OK, "success", dataMap);
        }
        GeneralResponse<Map<String, Object>> mapGeneralResponse = metaDataClient.getDataSourceInfoByIds(linkisConfig.getDatasourceCluster(), linkisConfig.getDatasourceAdmin(), dataSourceIds);
        Map<String, Object> dataMap = mapGeneralResponse.getData();
        if (MapUtils.isEmpty(dataMap)) {
            dataMap.put("totalPage", 0);
            dataMap.put("queryList", Collections.emptyList());
            return mapGeneralResponse;
        }

        dataMap.put("totalPage", linkisDataSourcePage.getTotalElements());
        List<Map<String, Object>> queryList = getQueryList(mapGeneralResponse);
        setFieldInfo(queryList);

        Map<Long, LinkisDataSource> linkisDataSourceMap = linkisDataSourcePage.getContent().stream().collect(Collectors.toMap(LinkisDataSource::getLinkisDataSourceId, Function.identity(), (oldVal, newVal) -> oldVal));
        replaceUserInfo(queryList, linkisDataSourceMap);

        return mapGeneralResponse;
    }

    private void replaceUserInfo(List<Map<String, Object>> queryList, Map<Long, LinkisDataSource> linkisDataSourceMap) {
        queryList.forEach(map -> {
            Long linkisDataSourceId = MapUtils.getLong(map, "id");
            LinkisDataSource linkisDataSource = linkisDataSourceMap.get(linkisDataSourceId);
            if (Objects.nonNull(linkisDataSource)) {
                map.put("modifyUser", linkisDataSource.getModifyUser());
                map.put("createUser", linkisDataSource.getCreateUser());
            }
        });
    }

    private Page<LinkisDataSource> filterLinkisDataSource(QueryDataSourceRequest request) throws UnExpectedRequestException {
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        int size = request.getSize() <= 0 ? 10 : request.getSize();
        int page = request.getPage();

        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);

        List<Long> visibleDepartmentIds = request.getVisibleDepartmentIds();
        Page<LinkisDataSource> linkisDataSourcePage;
        if (RoleSystemTypeEnum.ADMIN.getCode().equals(roleType)) {
            linkisDataSourcePage = linkisDataSourceDao.filterWithPage(request.getName(), request.getDataSourceTypeId(), null
                    , userName, request.getCreateUser(), request.getModifyUser()
                    , request.getSubSystem(), request.getDevDepartmentId(), request.getOpsDepartmentId()
                    , true, visibleDepartmentIds,  page, size);
        } else if (RoleSystemTypeEnum.DEPARTMENT_ADMIN.getCode().equals(roleType)) {
            List<Long> departmentIds = userRoles.stream().map(UserRole::getRole)
                    .filter(Objects::nonNull).map(Role::getDepartment)
                    .filter(Objects::nonNull).map(Department::getId)
                    .collect(Collectors.toList());
            if (Objects.nonNull(userInDb.getDepartment())) {
                departmentIds.add(userInDb.getDepartment().getId());
            }
//            过滤高级筛选中的可见范围
            List<Long> devAndOpsInfoWithDeptList = subDepartmentPermissionService.getSubDepartmentIdList(departmentIds);
            linkisDataSourcePage = linkisDataSourceDao.filterWithPage(request.getName(), request.getDataSourceTypeId(), devAndOpsInfoWithDeptList
                    , userName, request.getCreateUser(), request.getModifyUser()
                    , request.getSubSystem(), request.getDevDepartmentId(), request.getOpsDepartmentId()
                    , false, visibleDepartmentIds, page, size);
        } else if (RoleSystemTypeEnum.PROJECTOR.getCode().equals(roleType)) {
            linkisDataSourcePage = linkisDataSourceDao.filterWithPage(request.getName(), request.getDataSourceTypeId(), Arrays.asList(userInDb.getSubDepartmentCode())
                    , userName, request.getCreateUser(), request.getModifyUser()
                    , request.getSubSystem(), request.getDevDepartmentId(), request.getOpsDepartmentId()
                    , false, visibleDepartmentIds, page, size);
        } else {
            throw new UnExpectedRequestException("role type doesn't exists");
        }
        return linkisDataSourcePage;
    }

    private List<Map<String, Object>> getQueryList(GeneralResponse<Map<String, Object>> mapGeneralResponse) {
        Map<String, Object> dataMap = mapGeneralResponse.getData();
        if (MapUtils.isEmpty(dataMap)) {
            return Collections.emptyList();
        }
        List<Object> queryList = (List<Object>) dataMap.getOrDefault("queryList", Collections.emptyList());
        if (CollectionUtils.isEmpty(queryList)) {
            return Collections.emptyList();
        }
        return queryList.stream().filter(Objects::nonNull).map(obj -> (Map<String, Object>) obj).collect(Collectors.toList());
    }

    private void setFieldInfo(List<Map<String, Object>> queryList) {
        List<Long> dataSourceIds = queryList.stream()
                .filter(Objects::nonNull)
                .map(map -> MapUtils.getLong(map, "id"))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(dataSourceIds)) {
            return;
        }
        List<LinkisDataSource> linkisDataSourceList = linkisDataSourceDao.getByLinkisDataSourceIds(dataSourceIds);
        List<Long> tableDataIds = linkisDataSourceList.stream().map(LinkisDataSource::getId).collect(Collectors.toList());
        List<DataVisibility> dataVisibilityList = dataVisibilityService.filterByIds(tableDataIds, TableDataTypeEnum.LINKIS_DATA_SOURCE);
        Map<Long, LinkisDataSource> linkisDataSourceMap = linkisDataSourceList.stream()
                .collect(Collectors.toMap(LinkisDataSource::getLinkisDataSourceId, Function.identity(), (oldVal, newVal) -> oldVal));
        Map<Long, List<DataVisibility>> dataVisibilityListMap = dataVisibilityList.stream().collect(Collectors.groupingBy(DataVisibility::getTableDataId));
        for (Object obj : queryList) {
            Map<String, Object> datasourceResp = (Map<String, Object>) obj;
            Long datasourceId = MapUtils.getLong(datasourceResp, "id");
            if (linkisDataSourceMap.containsKey(datasourceId)) {
                LinkisDataSource linkisDataSource = linkisDataSourceMap.get(datasourceId);
                datasourceResp.put("dev_department_id", linkisDataSource.getDevDepartmentId());
                datasourceResp.put("dev_department_name", linkisDataSource.getDevDepartmentName());
                datasourceResp.put("ops_department_id", linkisDataSource.getOpsDepartmentId());
                datasourceResp.put("ops_department_name", linkisDataSource.getOpsDepartmentName());

                if (dataVisibilityListMap.containsKey(linkisDataSource.getId())) {
                    List<DataVisibility> subDataVisibilityList = dataVisibilityListMap.get(linkisDataSource.getId());
                    List<DepartmentSubInfoResponse> visibilityDepartmentList = subDataVisibilityList.stream().map(DepartmentSubInfoResponse::new).collect(Collectors.toList());
                    datasourceResp.put("visibility_department_list", visibilityDepartmentList);
                }

                datasourceResp.put("subSystem", linkisDataSource.getSubSystem());
            }
        }
    }

    @Override
    public GeneralResponse<Map<String, Object>> getDataSourceVersions(String clusterName, String proxyUser, Long dataSourceId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return metaDataClient.getDataSourceVersions(clusterName, linkisConfig.getDatasourceAdmin(), dataSourceId);
    }

    @Override
    public GeneralResponse<Map<String, Object>> getDataSourceInfoDetail(String clusterName, String proxyUser, Long dataSourceId, Long versionId) throws Exception {
        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);
        if (!RoleSystemTypeEnum.ADMIN.getCode().equals(roleType)) {
            LinkisDataSource linkisDataSource = linkisDataSourceDao.getByLinkisDataSourceId(dataSourceId);
            DataVisibilityPermissionDto dataVisibilityPermissionDto = new DataVisibilityPermissionDto.Builder()
                    .createUser(linkisDataSource.getCreateUser())
                    .devDepartmentId(linkisDataSource.getDevDepartmentId())
                    .opsDepartmentId(linkisDataSource.getOpsDepartmentId())
                    .build();
            subDepartmentPermissionService.checkAccessiblePermission(linkisDataSource.getId(), TableDataTypeEnum.LINKIS_DATA_SOURCE, dataVisibilityPermissionDto);
        }
        GeneralResponse generalResponse = metaDataClient.getDataSourceInfoDetail(clusterName, linkisConfig.getDatasourceAdmin(), dataSourceId, versionId);
        setDatasourceEnv(clusterName, linkisConfig.getDatasourceAdmin(), generalResponse, dataSourceId);
        convertFieldInResponse(generalResponse);
        setDepartment(dataSourceId, generalResponse);
        return generalResponse;
    }

    private void convertFieldInResponse(GeneralResponse<Map<String, Object>> datasourceResponse) {
        Map<String, Object> dataMap = datasourceResponse.getData();
        if (!dataMap.containsKey(INFO)) {
            return;
        }
        Map<String, Object> infoMap = (Map<String, Object>) dataMap.get(INFO);
        if (infoMap.containsKey("labels")) {
            String labels = (String) infoMap.get("labels");
            infoMap.put("labels", StringUtils.split(labels, SpecCharEnum.COMMA.getValue()));
        }

        if (infoMap.containsKey("connectParams")) {
            Map<String, Object> connectParams = (Map<String, Object>) infoMap.get("connectParams");
            infoMap.put("subSystem", connectParams.get("subSystem"));
            connectParams.remove("subSystem");
            connectParams.put("connectParam", connectParams.get("params"));
            connectParams.put("appId", connectParams.get("appid"));
            connectParams.put("objectId", connectParams.get("objectid"));
            connectParams.remove("params");
            connectParams.remove("appid");
            connectParams.remove("objectid");
            connectParams.remove("mkPrivate");
            if (connectParams.containsKey("password") && connectParams.get("password") != null) {
                connectParams.put("password", CryptoUtils.encode(connectParams.get("password").toString()));
            }
            if (connectParams.containsKey("share")) {
                Boolean isShare = MapUtils.getBoolean(connectParams, "share");
                infoMap.put("verifyType", isShare ? QualitisConstants.DATASOURCE_MANAGER_VERIFY_TYPE_SHARE : QualitisConstants.DATASOURCE_MANAGER_VERIFY_TYPE_NON_SHARE);
            }
            if (connectParams.containsKey("dcn")) {
                Boolean isDcn = MapUtils.getBoolean(connectParams, "dcn");
                infoMap.put("inputType", isDcn ? QualitisConstants.DATASOURCE_MANAGER_INPUT_TYPE_AUTO : QualitisConstants.DATASOURCE_MANAGER_INPUT_TYPE_MANUAL);
            }
        }
    }

    private void setDepartment(Long dataSourceId, GeneralResponse<Map<String, Object>> datasourceResponse) throws UnExpectedRequestException {
        Map<String, Object> dataMap = datasourceResponse.getData();
        if (!dataMap.containsKey(INFO)) {
            return;
        }
        LinkisDataSource linkisDataSource = linkisDataSourceDao.getByLinkisDataSourceId(dataSourceId);
        if (linkisDataSource == null) {
            linkisDataSource = new LinkisDataSource();
        }
        Map<String, Object> infoMap = (Map<String, Object>) dataMap.get("info");
        boolean isEditable = subDepartmentPermissionService.isEditable(linkisDataSource.getCreateUser(), linkisDataSource.getDevDepartmentId(), linkisDataSource.getOpsDepartmentId());
        infoMap.put("is_editable", isEditable);
        infoMap.put("dev_department_name", linkisDataSource.getDevDepartmentName());
        infoMap.put("ops_department_name", linkisDataSource.getOpsDepartmentName());
        infoMap.put("dev_department_id", linkisDataSource.getDevDepartmentId());
        infoMap.put("ops_department_id", linkisDataSource.getOpsDepartmentId());

        List<DepartmentSubInfoResponse> departmentInfoResponses = null;
        List<DataVisibility> dataVisibilityList = dataVisibilityService.filter(linkisDataSource.getId(), TableDataTypeEnum.LINKIS_DATA_SOURCE);
        if (CollectionUtils.isNotEmpty(dataVisibilityList)) {
            departmentInfoResponses = dataVisibilityList.stream().map(DepartmentSubInfoResponse::new).collect(Collectors.toList());
        }
        infoMap.put("visibility_department_list", departmentInfoResponses);
        if (StringUtils.isNotBlank(linkisDataSource.getDcnSequence())) {
            try {
                infoMap.put("dcnSequence", new ObjectMapper().readValue(linkisDataSource.getDcnSequence(), List.class));
            } catch (IOException e) {
                LOGGER.warn("failed to desirable dcnSequence");
            }
        }
    }

    private void setDatasourceEnv(String clusterName, String userName, GeneralResponse<Map<String, Object>> datasourceResponse, Long dataSourceId) throws UnExpectedRequestException, MetaDataAcquireFailedException, JsonProcessingException, IOException {
        Map<String, Object> dataMap = datasourceResponse.getData();
        if (!dataMap.containsKey(INFO)) {
            return;
        }
        Map<String, Object> infoMap = (Map<String, Object>) dataMap.get("info");
        ObjectMapper objectMapper = new ObjectMapper();
        String infoJson = objectMapper.writeValueAsString(infoMap);
        LinkisDataSourceInfoDetail linkisDataSourceInfoDetail = objectMapper.readValue(infoJson, LinkisDataSourceInfoDetail.class);
        List<Object> envIdArray = Collections.emptyList();
        if (MapUtils.isNotEmpty(linkisDataSourceInfoDetail.getConnectParams())) {
            Map<String, Object> connectParams = linkisDataSourceInfoDetail.getConnectParams();
            if (connectParams.containsKey("envIdArray")) {
                envIdArray = (List<Object>) connectParams.get("envIdArray");
            }
        }
        LinkisDataSource linkisDataSource = linkisDataSourceDao.getByLinkisDataSourceId(dataSourceId);
        List<String> envNameListFromLinkis = Lists.newArrayListWithExpectedSize(envIdArray.size());
        List<Map<String, Object>> dataSourceEnvs = Lists.newArrayListWithExpectedSize(envIdArray.size());
        for (Object envId : envIdArray) {
            GeneralResponse<Map<String, Object>> envResponse = metaDataClient.getDatasourceEnvById(clusterName, userName, Long.valueOf(envId.toString()));
            if (!ResponseStatusConstants.OK.equals(envResponse.getCode())) {
                continue;
            }
            Map<String, Object> envResponseData = envResponse.getData();
            if (MapUtils.isEmpty(envResponseData)) {
                continue;
            }
            if (envResponseData.containsKey("env")) {
                Map<String, Object> dataSourceEnv = (Map<String, Object>) envResponseData.get("env");

                if (dataSourceEnv.containsKey("connectParams")) {
                    Map<String, Object> connectParams = (Map<String, Object>) dataSourceEnv.get("connectParams");
                    connectParams.put("connectParam", connectParams.get("params"));
                    connectParams.put("appId", connectParams.get("appid"));
                    connectParams.put("objectId", connectParams.get("objectid"));
                    connectParams.remove("params");
                    connectParams.remove("appid");
                    connectParams.remove("objectid");
                    connectParams.remove("mkPrivate");

                    String host = MapUtils.getString(dataSourceEnv, "host");
                    String database = MapUtils.getString(dataSourceEnv, "database");
                    dataSourceEnv.put("dcnId", database + host + MapUtils.getString(dataSourceEnv, "port"));
                }

                String linkisEnvName = MapUtils.getString(dataSourceEnv, "envName");
                envNameListFromLinkis.add(linkisEnvName);

                if (Objects.nonNull(linkisDataSource)) {
                    String originalEnvName = linkisDataSourceService.convertLinkisEnvNameToOriginal(linkisDataSource.getLinkisDataSourceId(), linkisEnvName, linkisDataSource.getInputType());
                    dataSourceEnv.put("envName", originalEnvName);
                }

                dataSourceEnvs.add(dataSourceEnv);
            }
        }

//        提取存在于Qualits但不存在与Linkis的环境。这种情况发生在导入项目时，导出环境的env多于导入环境的env
        if (Objects.nonNull(linkisDataSource)) {
            List<String> envNameListFromQualitis = linkisDataSourceService.getLinkisEnvNameList(linkisDataSource);
            envNameListFromQualitis.forEach(linkisEnvName -> {
                if (!envNameListFromLinkis.contains(linkisEnvName)) {
                    Map<String, Object> dataSourceEnv = Maps.newHashMapWithExpectedSize(1);
                    String originalEnvName = linkisDataSourceService.convertLinkisEnvNameToOriginal(linkisDataSource.getLinkisDataSourceId(), linkisEnvName, linkisDataSource.getInputType());
                    dataSourceEnv.put("envName", originalEnvName);
                    dataSourceEnvs.add(dataSourceEnv);
                }
            });
        }

        infoMap.put("dataSourceEnvs", dataSourceEnvs);
    }

    @Override
    public GeneralResponse<Map<String, Object>> getDataSourceKeyDefine(String clusterName, String proxyUser, Long keyId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Get login user
        String userName = HttpUtils.getUserName(httpServletRequest);
        if (StringUtils.isNotBlank(proxyUser)) {
            userName = proxyUser;
        }
        return metaDataClient.getDataSourceKeyDefine(clusterName, userName, keyId);
    }

    @Override
    public GeneralResponse<Map<String, Object>> connectDataSource(String clusterName, String proxyUser, DataSourceConnectRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException, IOException, JSONException {
        // Get login user
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(request);
        Map<String, Object> jsonMap = objectMapper.readValue(jsonRequest, Map.class);
        Map<String, Object> connectMap = new HashMap<>();
        boolean isShare = QualitisConstants.DATASOURCE_MANAGER_VERIFY_TYPE_SHARE == request.getVerifyType();
        if (isShare) {
            ConnectParams connectParams = request.getConnectParams();
            String authType = connectParams.getAuthType();
            connectMap.put("authType", authType);
            if (QualitisConstants.AUTH_TYPE_DPM.equals(authType)) {
                connectMap.put("objectid", connectParams.getObjectId());
                connectMap.put("mkPrivate", connectParams.getMkPrivate());
                connectMap.put("appid", connectParams.getAppId());
                String dk = connectParams.getDk();
                if (StringUtils.isNotEmpty(dk)) {
                    connectMap.put("dk", dk);
                }
            } else if (QualitisConstants.AUTH_TYPE_ACCOUNT_PWD.equals(authType)) {
                connectMap.put("username", connectParams.getUsername());
                connectMap.put("password", CryptoUtils.decode(connectParams.getPassword()));
            }
            connectMap.put("timestamp", connectParams.getTimeStamp());
        }

        jsonMap.put("connectParams", connectMap);
        List<DataSourceEnv> dataSourceEnvs = request.getDataSourceEnvs();
        if (CollectionUtils.isNotEmpty(dataSourceEnvs)) {
            for (DataSourceEnv dataSourceEnv : dataSourceEnvs) {
                ConnectParams connectParams = dataSourceEnv.getConnectParams();
                if (!isShare) {
                    String authType = connectParams.getAuthType();
                    connectMap.put("authType", authType);
                    if (QualitisConstants.AUTH_TYPE_DPM.equals(authType)) {
                        connectMap.put("objectid", connectParams.getObjectId());
                        connectMap.put("mkPrivate", connectParams.getMkPrivate());
                        connectMap.put("appid", connectParams.getAppId());
                        String dk = connectParams.getDk();
                        if (StringUtils.isNotEmpty(dk)) {
                            connectMap.put("dk", dk);
                        }
                    } else if (QualitisConstants.AUTH_TYPE_ACCOUNT_PWD.equals(authType)) {
                        connectMap.put("username", connectParams.getUsername());
                        connectMap.put("password", CryptoUtils.decode(connectParams.getPassword()));
                    }
                    connectMap.put("timestamp", connectParams.getTimeStamp());
                }
                connectMap.put("host", connectParams.getHost());
                connectMap.put("port", connectParams.getPort());
                connectMap.put("params", connectParams.getConnectParam());
                jsonRequest = objectMapper.writeValueAsString(jsonMap);
                try {
                    GeneralResponse<Map<String, Object>> resultMap = metaDataClient.connectDataSource(clusterName, linkisConfig.getDatasourceAdmin(), jsonRequest);
                    if (!resultMap.getData().containsKey("ok")) {
                        return resultMap;
                    }
                } catch (MetaDataAcquireFailedException e) {
                    String errorMsg = String.format("环境{%s}连接失败", dataSourceEnv.getEnvName());
                    throw new MetaDataAcquireFailedException(errorMsg, 500);
                }
            }
        }
        return new GeneralResponse(ResponseStatusConstants.OK, "Connected!", null);
    }

    @Override
    public GeneralResponse<Map<String, Object>> publishDataSource(String clusterName, String proxyUser, Long dataSourceId, Long versionId) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        return metaDataClient.publishDataSource(clusterName, linkisConfig.getDatasourceAdmin(), dataSourceId, versionId);
    }

    @Override
    public GeneralResponse<Map<String, Object>> expireDataSource(String clusterName, String proxyUser, Long dataSourceId) throws UnExpectedRequestException
            , MetaDataAcquireFailedException {
        return metaDataClient.expireDataSource(clusterName, linkisConfig.getDatasourceAdmin(), dataSourceId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public GeneralResponse modifyDataSource(String clusterName, Long dataSourceId, DataSourceModifyRequest request)
            throws Exception {
        checkRequest(request);
        LinkisDataSource linkisDataSource = linkisDataSourceDao.getByLinkisDataSourceId(dataSourceId);
        if (Objects.isNull(linkisDataSource)) {
            throw new UnExpectedRequestException("DataSource is not exists");
        }
        if (CollectionUtils.isEmpty(request.getDataSourceEnvs())) {
            throw new UnExpectedRequestException("env must be not null");
        }
        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);
        subDepartmentPermissionService.checkEditablePermission(roleType, userInDb, userInDb.getUsername(), request.getDevDepartmentId(), request.getOpsDepartmentId(), false);

//        save datasource to linkis
        LinkisDataSourceRequest linkisDataSourceRequest = new LinkisDataSourceRequest();
        BeanUtils.copyProperties(request, linkisDataSourceRequest);
        if (CollectionUtils.isNotEmpty(request.getDataSourceEnvs())) {
            ConnectParams sharedConnectParams = request.getDataSourceEnvs().get(0).getConnectParams();
            LinkisConnectParamsRequest connectParamsRequest = new LinkisConnectParamsRequest();
            BeanUtils.copyProperties(sharedConnectParams, connectParamsRequest);
            linkisDataSourceRequest.setSharedConnectParams(connectParamsRequest);
        }
        linkisDataSourceRequest.setLabels(StringUtils.join(request.getLabels(), SpecCharEnum.COMMA.getValue()));
        linkisDataSourceRequest.setLinkisDataSourceId(dataSourceId);
//        由于Linkis对数据变更的权限控制，因此使用linkisDataSource.getCreateUser()
        linkisMetaDataManager.modifyDataSource(linkisDataSourceRequest, clusterName, linkisDataSource.getCreateUser());

//        save datasourceEnv to linkis
        List<DataSourceEnv> dataSourceEnvList = request.getDataSourceEnvs();
        validateAndResetEnvName(dataSourceId, request.getInputType(), dataSourceEnvList);

        // delete env if exists deleted envs on page
        if (StringUtils.isNotBlank(linkisDataSource.getEnvs())) {
            Collection<Long> envIdsInDb = linkisDataSourceService.getEnvNameAndIdMap(linkisDataSource).values();
            List<Long> envIdsInRequest = dataSourceEnvList.stream().map(dataSourceEnv -> dataSourceEnv.getId()).collect(Collectors.toList());
            List<Long> envIdsInDeleted = envIdsInDb.stream().filter(envIdInDb -> !envIdsInRequest.contains(envIdInDb)).collect(Collectors.toList());
            for (Long envIdInDeleted: envIdsInDeleted) {
                try {
                    metaDataClient.deleteEnv(clusterName, QualitisConstants.FPS_DEFAULT_USER, envIdInDeleted);
                } catch (UnExpectedRequestException | MetaDataAcquireFailedException e) {
                    LOGGER.error("Failed to delete env deleted on page, envId: {}", envIdInDeleted, e);
                }
            }
        }

        List<LinkisDataSourceEnvRequest> modifyDatasourceEnvList = dataSourceEnvList.stream().filter(dataSourceEnv -> Objects.nonNull(dataSourceEnv.getId()))
                .map(dataSourceEnv -> {
                    LinkisDataSourceEnvRequest linkisDataSourceEnvRequest = new LinkisDataSourceEnvRequest();
                    linkisDataSourceEnvRequest.setId(dataSourceEnv.getId());
                    linkisDataSourceEnvRequest.setDataSourceTypeId(request.getDataSourceTypeId());
                    linkisDataSourceEnvRequest.setEnvName(dataSourceEnv.getEnvName());
                    linkisDataSourceEnvRequest.setEnvDesc(dataSourceEnv.getEnvDesc());
                    linkisDataSourceEnvRequest.setDatabase(dataSourceEnv.getDatabase());
                    LinkisConnectParamsRequest connectParamsRequest = new LinkisConnectParamsRequest();
                    BeanUtils.copyProperties(dataSourceEnv.getConnectParams(), connectParamsRequest);
                    linkisDataSourceEnvRequest.setConnectParamsRequest(connectParamsRequest);
                    return linkisDataSourceEnvRequest;
                }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(modifyDatasourceEnvList)) {
            linkisMetaDataManager.modifyDataSourceEnv(request.getInputType(), request.getVerifyType(), modifyDatasourceEnvList, clusterName, userInDb.getUsername());
        }

        List<LinkisDataSourceEnvRequest> createDatasourceEnvList = dataSourceEnvList.stream().filter(dataSourceEnv -> Objects.isNull(dataSourceEnv.getId()))
                .map(dataSourceEnv -> {
                    LinkisDataSourceEnvRequest linkisDataSourceEnvRequest = new LinkisDataSourceEnvRequest();
                    linkisDataSourceEnvRequest.setDataSourceTypeId(request.getDataSourceTypeId());
                    linkisDataSourceEnvRequest.setEnvName(dataSourceEnv.getEnvName());
                    linkisDataSourceEnvRequest.setEnvDesc(dataSourceEnv.getEnvDesc());
                    linkisDataSourceEnvRequest.setDatabase(dataSourceEnv.getDatabase());
                    LinkisConnectParamsRequest connectParamsRequest = new LinkisConnectParamsRequest();
                    BeanUtils.copyProperties(dataSourceEnv.getConnectParams(), connectParamsRequest);
                    linkisDataSourceEnvRequest.setConnectParamsRequest(connectParamsRequest);
                    return linkisDataSourceEnvRequest;
                })
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(createDatasourceEnvList)) {
            linkisMetaDataManager.createDataSourceEnv(request.getInputType(), request.getVerifyType(), createDatasourceEnvList, clusterName, userInDb.getUsername());
        }

        List<LinkisDataSourceEnvRequest> linkisDataSourceEnvRequestList = Lists.newArrayListWithExpectedSize(dataSourceEnvList.size());
        linkisDataSourceEnvRequestList.addAll(createDatasourceEnvList);
        linkisDataSourceEnvRequestList.addAll(modifyDatasourceEnvList);

//        save relation data
        linkisDataSource.setLinkisDataSourceId(dataSourceId);
        linkisDataSource.setLinkisDataSourceName(request.getDataSourceName());
        linkisDataSource.setDataSourceTypeId(request.getDataSourceTypeId());
        linkisDataSource.setDevDepartmentId(request.getDevDepartmentId());
        linkisDataSource.setOpsDepartmentId(request.getOpsDepartmentId());
        linkisDataSource.setDevDepartmentName(request.getDevDepartmentName());
        linkisDataSource.setOpsDepartmentName(request.getOpsDepartmentName());
        linkisDataSource.setModifyUser(userInDb.getUsername());
        linkisDataSource.setModifyTime(DateUtils.now());
        linkisDataSource.setDcnSequence(new ObjectMapper().writeValueAsString(request.getDcnSequence()));
        String envs = linkisDataSourceEnvRequestList.stream().map(dataSourceEnvRequest -> dataSourceEnvRequest.getId()
                + SpecCharEnum.COLON.getValue()
                + dataSourceEnvRequest.getEnvName()).collect(Collectors.joining(SpecCharEnum.COMMA.getValue()));
        linkisDataSource.setEnvs(envs);
        linkisDataSource.setDatasourceDesc(request.getDataSourceDesc());
        linkisDataSource.setLabels(StringUtils.join(request.getLabels(), SpecCharEnum.COMMA.getValue()));
        linkisDataSource.setSubSystem(request.getSubSystem());
        linkisDataSource.setInputType(request.getInputType());
        linkisDataSource.setVerifyType(request.getVerifyType());
        linkisDataSourceDao.save(linkisDataSource);

//        save data visibility range
        dataVisibilityService.delete(linkisDataSource.getId(), TableDataTypeEnum.LINKIS_DATA_SOURCE);
        dataVisibilityService.saveBatch(linkisDataSource.getId(), TableDataTypeEnum.LINKIS_DATA_SOURCE, request.getVisibilityDepartmentList());

//        update rule_datasource.linkis_datasource_name if datasourceName was updated
        ruleDataSourceDao.updateLinkisDataSourceName(dataSourceId, request.getDataSourceName());

        Map<String, Object> dataResponse = Maps.newHashMapWithExpectedSize(2);
        dataResponse.put("updateId", dataSourceId);
        dataResponse.put("envIdArray", linkisDataSourceEnvRequestList.stream().map(LinkisDataSourceEnvRequest::getId).collect(Collectors.toList()));
        return new GeneralResponse(ResponseStatusConstants.OK, "success", dataResponse);
    }

    @Override
    public GeneralResponse modifyDataSourceParam(String clusterName, Long dataSourceId, DataSourceParamModifyRequest request)
            throws UnExpectedRequestException, MetaDataAcquireFailedException {
        LinkisDataSource linkisDataSource = linkisDataSourceDao.getByLinkisDataSourceId(dataSourceId);
        if (Objects.isNull(linkisDataSource)) {
            throw new UnExpectedRequestException("DataSource is not exists");
        }
        // Get login user
        String userName = linkisDataSource.getCreateUser();
        List<DataSourceEnv> dataSourceEnvs = request.getDataSourceEnvs();
        CommonChecker.checkCollections(dataSourceEnvs, "dataSourceEnvs");
        DataSourceEnv dataSourceEnv = dataSourceEnvs.get(0);

        Map<String, Object> connectMap = new HashMap<>();
        connectMap.put("envIdArray", request.getEnvIdArray());
        if (QualitisConstants.DATASOURCE_MANAGER_VERIFY_TYPE_SHARE == request.getVerifyType()) {
            ConnectParams connectParams = dataSourceEnv.getConnectParams();
            String authType = connectParams.getAuthType();
            connectMap.put("authType", authType);
            if (QualitisConstants.AUTH_TYPE_ACCOUNT_PWD.equals(authType)) {
                connectMap.put("username", connectParams.getUsername());
                connectMap.put("password", connectParams.getPassword());
            } else if (QualitisConstants.AUTH_TYPE_DPM.equals(authType)) {
                connectMap.put("appid", connectParams.getAppId());
                connectMap.put("objectid", connectParams.getObjectId());
                connectMap.put("mkPrivate", connectParams.getMkPrivate());
            }
        }

        ModifyDataSourceParameterRequest modifyDataSourceParameterRequest = new ModifyDataSourceParameterRequest();
        modifyDataSourceParameterRequest.setLinkisDataSourceId(dataSourceId);
        modifyDataSourceParameterRequest.setComment(request.getComment());
        modifyDataSourceParameterRequest.setConnectParams(connectMap);
        LinkisDataSourceParamsResponse linkisDataSourceParamsResponse = linkisMetaDataManager.modifyDataSourceParams(modifyDataSourceParameterRequest, clusterName, userName);
        linkisDataSource.setVersionId(linkisDataSourceParamsResponse.getVersionId());
        linkisDataSourceDao.save(linkisDataSource);
        return new GeneralResponse<>(ResponseStatusConstants.OK, "success", linkisDataSourceParamsResponse);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public GeneralResponse createDataSource(String clusterName, DataSourceModifyRequest request)
            throws UnExpectedRequestException, MetaDataAcquireFailedException, IOException, JSONException, PermissionDeniedRequestException {
        checkRequest(request);
        User userInDb = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        List<UserRole> userRoles = userRoleDao.findByUser(userInDb);
        Integer roleType = roleService.getRoleType(userRoles);
        subDepartmentPermissionService.checkEditablePermission(roleType, userInDb, null, request.getDevDepartmentId(), request.getOpsDepartmentId(), false);

        LinkisDataSource linkisDataSourceInDb = linkisDataSourceDao.getByLinkisDataSourceName(request.getDataSourceName());
        if (linkisDataSourceInDb != null) {
            throw new UnExpectedRequestException("DataSourceName already existed!");
        }

        // save datasource to Linkis
        LinkisDataSourceRequest linkisDataSourceRequest = new LinkisDataSourceRequest();
        BeanUtils.copyProperties(request, linkisDataSourceRequest);
        if (CollectionUtils.isNotEmpty(request.getDataSourceEnvs())) {
            ConnectParams sharedConnectParams = request.getDataSourceEnvs().get(0).getConnectParams();
            LinkisConnectParamsRequest connectParamsRequest = new LinkisConnectParamsRequest();
            BeanUtils.copyProperties(sharedConnectParams, connectParamsRequest);
            linkisDataSourceRequest.setSharedConnectParams(connectParamsRequest);
        }
        linkisDataSourceRequest.setLabels(StringUtils.join(request.getLabels(), SpecCharEnum.COMMA.getValue()));
        Long linkisDataSourceId = linkisMetaDataManager.createDataSource(linkisDataSourceRequest, clusterName, userInDb.getUsername());

//        save env to Linkis
        validateAndResetEnvName(linkisDataSourceId, request.getInputType(), request.getDataSourceEnvs());
        List<LinkisDataSourceEnvRequest> linkisDataSourceEnvRequestList = request.getDataSourceEnvs().stream().map(dataSourceEnv -> {
            LinkisDataSourceEnvRequest linkisDataSourceEnvRequest = new LinkisDataSourceEnvRequest();
            linkisDataSourceEnvRequest.setDataSourceTypeId(request.getDataSourceTypeId());
            linkisDataSourceEnvRequest.setEnvName(dataSourceEnv.getEnvName());
            linkisDataSourceEnvRequest.setDatabase(dataSourceEnv.getDatabase());
            LinkisConnectParamsRequest connectParamsRequest = new LinkisConnectParamsRequest();
            BeanUtils.copyProperties(dataSourceEnv.getConnectParams(), connectParamsRequest);
            linkisDataSourceEnvRequest.setConnectParamsRequest(connectParamsRequest);
            return linkisDataSourceEnvRequest;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(linkisDataSourceEnvRequestList)) {
            try {
                linkisMetaDataManager.createDataSourceEnv(request.getInputType(), request.getVerifyType(), linkisDataSourceEnvRequestList, clusterName, userInDb.getUsername());
            } catch (MetaDataAcquireFailedException e) {
                LOGGER.warn("Failed to create DataSourceEnv, preparing to rollback.");
                linkisMetaDataManager.deleteDataSource(linkisDataSourceId, clusterName, userInDb.getUsername());
                throw e;
            }
        }

//        save dataSource to local
        LinkisDataSource linkisDataSource = new LinkisDataSource();
        linkisDataSource.setLinkisDataSourceId(linkisDataSourceId);
        linkisDataSource.setLinkisDataSourceName(request.getDataSourceName());
        linkisDataSource.setDataSourceTypeId(request.getDataSourceTypeId());
        linkisDataSource.setDevDepartmentId(request.getDevDepartmentId());
        linkisDataSource.setOpsDepartmentId(request.getOpsDepartmentId());
        linkisDataSource.setDevDepartmentName(request.getDevDepartmentName());
        linkisDataSource.setOpsDepartmentName(request.getOpsDepartmentName());
        linkisDataSource.setCreateUser(userInDb.getUsername());
        linkisDataSource.setCreateTime(DateUtils.now());
        linkisDataSource.setDcnSequence(new ObjectMapper().writeValueAsString(request.getDcnSequence()));
        String envs = linkisDataSourceEnvRequestList.stream().map(dataSourceEnvRequest -> dataSourceEnvRequest.getId()
                + SpecCharEnum.COLON.getValue()
                + dataSourceEnvRequest.getEnvName()).collect(Collectors.joining(SpecCharEnum.COMMA.getValue()));
        linkisDataSource.setEnvs(envs);
        linkisDataSource.setDatasourceDesc(request.getDataSourceDesc());
        linkisDataSource.setLabels(StringUtils.join(request.getLabels(), SpecCharEnum.COMMA.getValue()));
        linkisDataSource.setSubSystem(request.getSubSystem());
        linkisDataSource.setInputType(request.getInputType());
        linkisDataSource.setVerifyType(request.getVerifyType());
        linkisDataSource.setVersionId(1L);
        linkisDataSourceDao.save(linkisDataSource);

        dataVisibilityService.saveBatch(linkisDataSource.getId(), TableDataTypeEnum.LINKIS_DATA_SOURCE, request.getVisibilityDepartmentList());

        Map<String, Object> dataResponse = Maps.newHashMapWithExpectedSize(2);
        dataResponse.put("insertId", linkisDataSourceId);
        dataResponse.put("envIdArray", linkisDataSourceEnvRequestList.stream().map(LinkisDataSourceEnvRequest::getId).collect(Collectors.toList()));
        return new GeneralResponse(ResponseStatusConstants.OK, "success", dataResponse);
    }

    @Override
    public Map<String, Object> getDbsByDataSource(String clusterName, String proxyUser, Long dataSourceId, Long envId) throws Exception {
        LinkisDataSourceInfoDetail linkisDataSourceInfoDetail = metaDataClient.getDataSourceInfoById(clusterName, linkisConfig.getDatasourceAdmin(), dataSourceId);
        return metaDataClient.getDbsByDataSourceName(clusterName, linkisConfig.getDatasourceAdmin(), linkisDataSourceInfoDetail.getDataSourceName(), envId);
    }

    @Override
    public Map<String, Object> getTablesByDataSource(String clusterName, String proxyUser, Long dataSourceId, String dbName, Long envId) throws Exception {
        LinkisDataSourceInfoDetail linkisDataSourceInfoDetail = metaDataClient.getDataSourceInfoById(clusterName, linkisConfig.getDatasourceAdmin(), dataSourceId);
        return metaDataClient.getTablesByDataSourceName(clusterName, linkisConfig.getDatasourceAdmin(), linkisDataSourceInfoDetail.getDataSourceName(), dbName, envId);
    }

    @Override
    public GeneralResponse<GetAllResponse<ColumnInfoDetail>> getColumnsByDataSource(String clusterName, String proxyUser, Long dataSourceId, String dbName, String tableName, Long envId) throws Exception {
        LinkisDataSourceInfoDetail linkisDataSourceInfoDetail = metaDataClient.getDataSourceInfoById(clusterName, linkisConfig.getDatasourceAdmin(), dataSourceId);
        DataInfo<ColumnInfoDetail> response = metaDataClient.getColumnsByDataSourceName(clusterName, linkisConfig.getDatasourceAdmin(), linkisDataSourceInfoDetail.getDataSourceName(), dbName, tableName, envId);

        GetAllResponse<ColumnInfoDetail> result = new GetAllResponse<>();
        result.setTotal(response.getTotalCount());
        result.setData(response.getContent());

        LOGGER.info("Succeed to get column by data source table. table: {}.{}.{}", clusterName, dbName, tableName);
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_COLUMN_SUCCESSFULLY}", result);
    }

    @Override
    public GeneralResponse<List<Map<String, Object>>> getEnvList(String clusterName, String proxyUser, Long dataSourceId, Long versionId) throws Exception {
        GeneralResponse<Map<String, Object>> datasourceResponse = metaDataClient.getDataSourceInfoDetail(clusterName, linkisConfig.getDatasourceAdmin(), dataSourceId, versionId);
        Map<String, Object> dataMap = datasourceResponse.getData();
        if (!dataMap.containsKey(INFO)) {
            return new GeneralResponse();
        }

        Map<String, Object> infoMap = (Map<String, Object>) dataMap.get("info");
        ObjectMapper objectMapper = new ObjectMapper();
        String infoJson = objectMapper.writeValueAsString(infoMap);
        LinkisDataSourceInfoDetail linkisDataSourceInfoDetail = objectMapper.readValue(infoJson, LinkisDataSourceInfoDetail.class);

        List<Object> envIdArray = Collections.emptyList();
        if (MapUtils.isNotEmpty(linkisDataSourceInfoDetail.getConnectParams())) {
            Map<String, Object> connectParams = linkisDataSourceInfoDetail.getConnectParams();
            if (connectParams.containsKey("envIdArray")) {
                envIdArray = (List) connectParams.get("envIdArray");
            }
        }
        List<Map<String, Object>> dataSourceEnvs = Lists.newArrayListWithExpectedSize(envIdArray.size());
        for (Object envId : envIdArray) {
            GeneralResponse<Map<String, Object>> envResponse = metaDataClient.getDatasourceEnvById(clusterName, linkisConfig.getDatasourceAdmin(), Long.valueOf(envId.toString()));
            if (!ResponseStatusConstants.OK.equals(envResponse.getCode())) {
                continue;
            }
            Map<String, Object> envResponseData = envResponse.getData();
            if (MapUtils.isEmpty(envResponseData)) {
                continue;
            }
            if (envResponseData.containsKey("env")) {
                dataSourceEnvs.add((Map) envResponseData.get("env"));
            }
        }
        List<Map<String, Object>> envList = dataSourceEnvs.stream().map(entries -> {
            Map<String, Object> map = Maps.newHashMapWithExpectedSize(2);
            map.put("id", entries.get("id"));
            map.put("envName", entries.get("envName"));
            return map;
        }).collect(Collectors.toList());
        return new GeneralResponse(ResponseStatusConstants.OK, "Success to get datasource env list", envList);
    }

    @Override
    public List<CmdbDepartmentResponse> findAllDepartment(DepartmentSourceTypeEnum departmentSourceTypeEnum) throws UnExpectedRequestException {
        String tmpSourceType = departmentSourceType;
        List<CmdbDepartmentResponse> allDepartmentInfo;
        if (DepartmentSourceTypeEnum.CUSTOM.getValue().equals(tmpSourceType)) {
            List<Department> departmentResponses = departmentService.findAllDepartmentCodeAndName();
            allDepartmentInfo = departmentResponses.stream().map(departmentResponse -> {
                CmdbDepartmentResponse cmdbDepartmentResponse = new CmdbDepartmentResponse();
                cmdbDepartmentResponse.setCode(departmentResponse.getDepartmentCode());
                cmdbDepartmentResponse.setName(departmentResponse.getName());
                return cmdbDepartmentResponse;
            }).collect(Collectors.toList());
        } else {
            allDepartmentInfo = operateCiService.getAllDepartmetInfo();
        }
        return allDepartmentInfo;
    }

    @Override
    public List<DepartmentSubResponse> getSubDepartmentByDeptCode(DepartmentSourceTypeEnum departmentSourceTypeEnum, Integer deptCode) throws UnExpectedRequestException {
        String tmpSourceType = departmentSourceType;
        List<DepartmentSubResponse> allDepartmentSubList;
        if (DepartmentSourceTypeEnum.CUSTOM.getValue().equals(tmpSourceType)) {
            allDepartmentSubList = departmentService.getSubDepartmentByDeptCode(deptCode);
        } else {
            allDepartmentSubList = operateCiService.getDevAndOpsInfo(deptCode);
        }
        return allDepartmentSubList;
    }

    @Override
    public List<CmdbDepartmentResponse> getDepartmentInfoListByRoleType(DepartmentSourceTypeEnum departmentSourceTypeEnum) throws UnExpectedRequestException {
        List<CmdbDepartmentResponse> allDepartmentInfo = findAllDepartment(departmentSourceTypeEnum);

        Map<String, CmdbDepartmentResponse> departmentCodeMap = allDepartmentInfo.stream().collect(Collectors.toMap(CmdbDepartmentResponse::getCode, Function.identity(), (oldVal, newVal) -> oldVal));
        String userName = HttpUtils.getUserName(httpServletRequest);
        User loginUser = userDao.findByUsername(userName);
        List<UserRole> userRoles = userRoleDao.findByUser(loginUser);
        Integer roleType = roleService.getRoleType(userRoles);
        if (roleType.equals(RoleSystemTypeEnum.ADMIN.getCode())) {
            return allDepartmentInfo;
        } else if (roleType.equals(RoleSystemTypeEnum.DEPARTMENT_ADMIN.getCode())) {
            List<Department> departments = userRoles.stream()
                    .map(UserRole::getRole).filter(Objects::nonNull)
                    .map(Role::getDepartment).filter(Objects::nonNull)
                    .collect(Collectors.toList());
            departments.add(loginUser.getDepartment());
            return departments.stream()
                    .filter(Objects::nonNull)
                    .map(Department::getDepartmentCode)
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .map(departmentCodeMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            Department department = loginUser.getDepartment();
            if (Objects.nonNull(department)) {
                return Arrays.asList(departmentCodeMap.get(department.getDepartmentCode()));
            }
        }

        return Collections.emptyList();
    }

    @Override
    public List<DepartmentSubResponse> getDevAndOpsInfoListByRoleType(DepartmentSourceTypeEnum departmentSourceTypeEnum, Integer deptCode) throws UnExpectedRequestException {
        List<DepartmentSubResponse> allDepartmentSubList = getSubDepartmentByDeptCode(departmentSourceTypeEnum, deptCode);

        Map<String, DepartmentSubResponse> departmentSubIdMap = allDepartmentSubList.stream().collect(Collectors.toMap(DepartmentSubResponse::getId, Function.identity(), (oldVal, newVal) -> oldVal));
        String userName = HttpUtils.getUserName(httpServletRequest);
        User loginUser = userDao.findByUsername(userName);
        List<UserRole> userRoles = userRoleDao.findByUser(loginUser);
        Integer roleType = roleService.getRoleType(userRoles);
        if (roleType.equals(RoleSystemTypeEnum.ADMIN.getCode())) {
            return allDepartmentSubList;
        } else if (roleType.equals(RoleSystemTypeEnum.DEPARTMENT_ADMIN.getCode())) {
            List<Department> departments = userRoles.stream()
                    .map(UserRole::getRole).filter(Objects::nonNull)
                    .map(Role::getDepartment).filter(Objects::nonNull)
                    .collect(Collectors.toList());
            departments.add(loginUser.getDepartment());
            List<Long> departmentIds = departments.stream().filter(Objects::nonNull).map(Department::getId).collect(Collectors.toList());
            List<Long> devAndOpsInfoWithDeptList = subDepartmentPermissionService.getSubDepartmentIdList(departmentIds);
            return devAndOpsInfoWithDeptList.stream().map(String::valueOf).map(departmentSubIdMap::get).filter(Objects::nonNull).collect(Collectors.toList());
        } else {
            Long departmentSubId = loginUser.getSubDepartmentCode();
            if (Objects.nonNull(departmentSubId)) {
                return Arrays.asList(departmentSubIdMap.get(String.valueOf(departmentSubId)));
            }
        }

        return Collections.emptyList();
    }

    @Override
    public List<String> getDataSourceNameList() {
        return linkisDataSourceDao.getAllDataSourceNameList();
    }

    private List<String> filterTablesWithBlackList(List<String> blackList, List<String> sourceTableName) throws UnExpectedRequestException {
        for (String black : blackList) {
            int filterType = Integer.parseInt(black.split(SpecCharEnum.COLON.getValue())[0]);
            if (BlackListFilterTypeEnum.BEGIN_WITH.getCode().intValue() == filterType) {
                String blackStr = black.split(SpecCharEnum.COLON.getValue())[1];
                sourceTableName = sourceTableName.stream().filter(tableName -> !tableName.startsWith(blackStr)).collect(Collectors.toList());
            } else if (BlackListFilterTypeEnum.CONTAINS.getCode().intValue() == filterType) {
                String blackStr = black.split(SpecCharEnum.COLON.getValue())[1];
                sourceTableName = sourceTableName.stream().filter(tableName -> !tableName.contains(blackStr)).collect(Collectors.toList());
            } else if (BlackListFilterTypeEnum.END_WITH.getCode().intValue() == filterType) {
                String blackStr = black.split(SpecCharEnum.COLON.getValue())[1];
                sourceTableName = sourceTableName.stream().filter(tableName -> !tableName.endsWith(blackStr)).collect(Collectors.toList());
            } else if (BlackListFilterTypeEnum.SAME_TABLE.getCode().intValue() == filterType) {
                sourceTableName.clear();
            } else if (BlackListFilterTypeEnum.REG_TABLE.getCode().intValue() == filterType) {
                String blackStr = black.split(SpecCharEnum.COLON.getValue())[1];
                Pattern blackPattern = Pattern.compile(blackStr);
                sourceTableName = sourceTableName.stream().filter(tableName -> !blackPattern.matcher(tableName).matches()).collect(Collectors.toList());
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
        for (; i < total - filterRequests.size(); i++) {
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
        for (; i < total; i++) {
            FilterRequest filterRequest = filterRequests.get(i - ruleIndex - sourceTableSize - whiteList.size());
            try {
                AddMultiSourceRuleRequest addMultiSourceRuleRequest = constructRequest(request, filterRequest, "", "", i, total, loginUser);
                LOGGER.info("Start to add {}th multi source rule.", i);
                if (i > 0) {
                    check = false;
                }
                multiSourceRuleService.addMultiSourceRule(addMultiSourceRuleRequest, check);
            } catch (Exception e) {
                LOGGER.error("One of rule failed to add, rule index:[{}], exception:{}", i, e.getMessage());
            }
            LOGGER.info("Finish to add {}th multi source rule.", i);
        }
    }

    private AddMultiSourceRuleRequest constructRequest(MulDbRequest request, FilterRequest filterRequest, String currentSourceTable, String currentTargetTable, int index, int total, String loginUser) throws Exception {
        AddMultiSourceRuleRequest addMultiSourceRuleRequest = setMulDbRequestData(request, filterRequest, currentSourceTable, currentTargetTable, index, total);
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
                    , currentSourceTable, null).getData().getData();
        }

        if (request.getTargetLinkisDataSourceId() == null) {
            GetUserColumnByTableIdRequest getUserColumnTargetRequest = new GetUserColumnByTableIdRequest(0, Integer.MAX_VALUE, request.getClusterName(), request.getTargetDb()
                    , filterRequest == null ? currentTargetTable : filterRequest.getTargetTable());
            getUserColumnTargetRequest.setProxyUser(request.getProxyUser());
            getUserColumnTargetRequest.setLoginUser(loginUser);
            targetFields = getUserColumnByTableId(getUserColumnTargetRequest).getData().getData();
        } else {
            targetFields = getColumnsByDataSource(request.getClusterName(), request.getProxyUser(), request.getTargetLinkisDataSourceId(), request.getTargetDb()
                    , currentTargetTable, null).getData().getData();
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
                || !sourceFieldStr.equals(targetFieldStr) || !sourceFieldTypeStr.equals(targetFieldTypeStr)) {
            throw new UnExpectedRequestException("Create batch rules failed, because the fields' name of table is different.");
        }
        List<TemplateArgumentRequest> templateArgumentRequests = Lists.newArrayList();

        if (filterRequest != null) {
            List<MultiDataSourceJoinConfigRequest> compareCols = new ArrayList<>(sourceFields.size());
            List<MultiDataSourceJoinConfigRequest> mappings = new ArrayList<>(sourceFields.size());
            baseRequestInfo(filterRequest, sourceFields, targetFields, mappings, compareCols);

            TemplateArgumentRequest templateArgumentRequest = new TemplateArgumentRequest();
            templateArgumentRequest.setArgumentType(TemplateInputTypeEnum.CONNECT_FIELDS.getCode());
            templateArgumentRequest.setArgumentValue(CustomObjectMapper.transObjectToJson(mappings));
            templateArgumentRequests.add(templateArgumentRequest);

            TemplateArgumentRequest templateArgumentRequestCompareCols = new TemplateArgumentRequest();
            templateArgumentRequestCompareCols.setArgumentType(TemplateInputTypeEnum.COMPARISON_FIELD_SETTINGS.getCode());
            templateArgumentRequestCompareCols.setArgumentValue(CustomObjectMapper.transObjectToJson(compareCols));
            templateArgumentRequests.add(templateArgumentRequestCompareCols);

//            addMultiSourceRuleRequest.setCompareCols(compareCols);
//            addMultiSourceRuleRequest.setMappings(mappings);
        }
        addMultiSourceRuleRequest.setLoginUser(loginUser);
        TemplateArgumentRequest templateArgumentRequestFilter = new TemplateArgumentRequest();
        templateArgumentRequestFilter.setArgumentType(TemplateInputTypeEnum.COMPARISON_RESULTS_FOR_FILTER.getCode());
        templateArgumentRequestFilter.setArgumentValue("true");
        templateArgumentRequests.add(templateArgumentRequestFilter);
        addMultiSourceRuleRequest.setTemplateArgumentRequests(templateArgumentRequests);
        //addMultiSourceRuleRequest.setFilter("true");
        LOGGER.info("Success to construct add multi source rule request. Request[{}]", new ObjectMapper().writeValueAsString(addMultiSourceRuleRequest));
        return addMultiSourceRuleRequest;
    }

    private void baseRequestInfo(FilterRequest filterRequest, List<ColumnInfoDetail> sourceFields, List<ColumnInfoDetail> targetFields
            , List<MultiDataSourceJoinConfigRequest> mappings, List<MultiDataSourceJoinConfigRequest> compareCols) throws UnExpectedRequestException {
        for (int j = 0; j < sourceFields.size() && j < targetFields.size(); j++) {
            ColumnInfoDetail currentSourceField = sourceFields.get(j);
            ColumnInfoDetail currentTargetField = targetFields.get(j);
            if (filterRequest != null && CollectionUtils.isNotEmpty(filterRequest.getFilterColumnList()) && filterRequest.getFilterColumnList().contains(currentSourceField.getFieldName())) {
                continue;
            }
            if (currentSourceField.getFieldName().equals(currentTargetField.getFieldName()) && currentSourceField.getDataType().equals(currentTargetField.getDataType())) {
                MultiDataSourceJoinConfigRequest multiDataSourceJoinConfigRequest = new MultiDataSourceJoinConfigRequest();
                MultiDataSourceJoinColumnRequest leftJoinColumnRequest = new MultiDataSourceJoinColumnRequest("tmp1.".concat(currentSourceField.getFieldName()), currentSourceField.getDataType());
                MultiDataSourceJoinColumnRequest rightJoinColumnRequest = new MultiDataSourceJoinColumnRequest("tmp2.".concat(currentTargetField.getFieldName()), currentTargetField.getDataType());
                multiDataSourceJoinConfigRequest.setOperation(MappingOperationEnum.EQUAL.getCode());
                multiDataSourceJoinConfigRequest.setLeft(Arrays.asList(leftJoinColumnRequest));
                multiDataSourceJoinConfigRequest.setRight(Arrays.asList(rightJoinColumnRequest));
                multiDataSourceJoinConfigRequest.setLeftStatement("tmp1.".concat(currentSourceField.getFieldName()));
                multiDataSourceJoinConfigRequest.setRightStatement("tmp2.".concat(currentTargetField.getFieldName()));
                mappings.add(multiDataSourceJoinConfigRequest);
                compareCols.add(multiDataSourceJoinConfigRequest);
            } else {
                throw new UnExpectedRequestException("Create multi db rules failed, because the fields' name of tables is different");
            }
        }
    }

    private AddMultiSourceRuleRequest setMulDbRequestData(MulDbRequest request, FilterRequest filterRequest, String currentSourceTable, String currentTargetTable, int index, int total) {
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

        setConfigRequest(filterRequest, currentSourceTable, currentTargetTable, addMultiSourceRuleRequest, sourceConfigRequest, targetConfigRequest);
        addMultiSourceRuleRequest.setSource(sourceConfigRequest);
        addMultiSourceRuleRequest.setTarget(targetConfigRequest);
        return addMultiSourceRuleRequest;
    }

    private void setConfigRequest(FilterRequest filterRequest, String currentSourceTable, String currentTargetTable, AddMultiSourceRuleRequest addMultiSourceRuleRequest, MultiDataSourceConfigRequest sourceConfigRequest, MultiDataSourceConfigRequest targetConfigRequest) {
        if (filterRequest == null) {
            // source table and target table
            sourceConfigRequest.setTableName(currentSourceTable);
            sourceConfigRequest.setFilter("true");
            targetConfigRequest.setTableName(currentTargetTable);
            targetConfigRequest.setFilter("true");

            addMultiSourceRuleRequest.setMultiSourceRuleTemplateId(TemplateFunctionNameEnum.EXPECT_TABLE_CONSISTENT.getCode());
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
            addMultiSourceRuleRequest.setMultiSourceRuleTemplateId(TemplateFunctionNameEnum.EXPECT_SPECIFIED_COLUMN_CONSISTENT.getCode());
            List<AlarmConfigRequest> alarmConfigRequests = new ArrayList<>();
            alarmConfigRequests.add(new AlarmConfigRequest(33L, CheckTemplateEnum.FIXED_VALUE.getCode(), CompareTypeEnum.EQUAL.getCode(), 0.0));
            addMultiSourceRuleRequest.setAlarmVariable(alarmConfigRequests);
        }
    }

    private void basicInfo(AddMultiSourceRuleRequest addMultiSourceRuleRequest, MulDbRequest request, int index, int total) {
        addMultiSourceRuleRequest.setProjectId(request.getProjectId());
        addMultiSourceRuleRequest.setClusterName(request.getClusterName());
        addMultiSourceRuleRequest.setRuleName(request.getRuleName() + "_" + index + "_" + total);
        addMultiSourceRuleRequest.setRuleCnName(request.getCnName());
        addMultiSourceRuleRequest.setRuleDetail(request.getRuleDetail());

        addMultiSourceRuleRequest.setAlert(request.getAlert());
        addMultiSourceRuleRequest.setAlertLevel(request.getAlertLevel());
        addMultiSourceRuleRequest.setAlertReceiver(request.getAlertReceiver());
        addMultiSourceRuleRequest.setAlarm(true);
        addMultiSourceRuleRequest.setDeleteFailCheckResult(true);
        addMultiSourceRuleRequest.setAbortOnFailure(request.getAbortOnFailure());
        addMultiSourceRuleRequest.setStaticStartupParam(request.getStaticStartupParam());
        addMultiSourceRuleRequest.setSpecifyStaticStartupParam(request.getSpecifyStaticStartupParam());
        addMultiSourceRuleRequest.setExecutionParametersName(request.getExecutionParametersName());
        addMultiSourceRuleRequest.setAbnormalCluster(request.getAbnormalCluster());
        addMultiSourceRuleRequest.setAbnormalDatabase(request.getAbnormalDatabase());
        addMultiSourceRuleRequest.setAbnormalProxyUser(request.getAbnormalProxyUser());
        addMultiSourceRuleRequest.setWorkFlowName(request.getWorkFlowName());
        addMultiSourceRuleRequest.setContrastType(request.getContrastType());
        addMultiSourceRuleRequest.setWorkFlowVersion(request.getWorkFlowVersion());
        addMultiSourceRuleRequest.setUploadAbnormalValue(request.getUploadAbnormalValue());
        addMultiSourceRuleRequest.setUploadRuleMetricValue(request.getUploadRuleMetricValue());
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

    private void checkRequest(DataSourceModifyRequest request) throws UnExpectedRequestException {
        List<DataSourceEnv> dataSourceEnvs = request.getDataSourceEnvs();
        CommonChecker.checkObject(dataSourceEnvs, "dataSourceEnvs");
        CommonChecker.checkListMinSize(dataSourceEnvs, 1, "dataSourceEnvs");
        Pattern pattern = Pattern.compile(ENV_NAME_REGEX);
//            连接参数不能为空
        for (DataSourceEnv dataSourceEnv : dataSourceEnvs) {
            ConnectParams connectParams = dataSourceEnv.getConnectParams();
            CommonChecker.checkObject(connectParams, "dataSourceEnvs.connectParams");
            CommonChecker.checkObject(connectParams.getHost(), "dataSourceEnvs.connectParams.host");
            CommonChecker.checkObject(connectParams.getPort(), "dataSourceEnvs.connectParams.port");
            CommonChecker.checkString(dataSourceEnv.getEnvName(), "dataSourceEnvs.envName");
            Matcher matcher = pattern.matcher(dataSourceEnv.getEnvName());
            if (!matcher.matches()) {
                throw new UnExpectedRequestException("Invalid envName, cannot use ',' and ':'");
            }
        }
    }

    private void validateAndResetEnvName(Long dataSourceId, Integer inputType, List<DataSourceEnv> dataSourceEnvList) throws UnExpectedRequestException {
//        add prefix to env_name
        int totalLength = 32;
        for (DataSourceEnv dataSourceEnv : dataSourceEnvList) {
            String linkisEnvName = linkisDataSourceService.convertOriginalEnvNameToLinkis(dataSourceId, dataSourceEnv.getEnvName(), inputType, dataSourceEnv.getConnectParams().getHost());
            if (linkisEnvName.length() > totalLength) {
                int maxLength = totalLength - String.valueOf(dataSourceId).length() - 1;
                throw new UnExpectedRequestException("env_name {&EXCEED_MAX_LENGTH}: " + maxLength);
            }
            dataSourceEnv.setEnvName(linkisEnvName);
        }
        boolean isDuplicateEnvName = dataSourceEnvList.stream().map(DataSourceEnv::getEnvName).distinct().count() < dataSourceEnvList.size();
        if (isDuplicateEnvName) {
            throw new UnExpectedRequestException("Duplicate env name");
        }
    }

}
