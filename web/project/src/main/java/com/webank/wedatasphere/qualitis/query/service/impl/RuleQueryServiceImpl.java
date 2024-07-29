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

package com.webank.wedatasphere.qualitis.query.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.config.FrontEndConfig;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.DataStandardClient;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.client.RuleClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserColumnByCsRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserTableByCsIdRequest;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.CsTableInfoDetail;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectUserDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.request.RuleQueryRequest;
import com.webank.wedatasphere.qualitis.project.response.HiveRuleDetail;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.query.request.LineageParameterRequest;
import com.webank.wedatasphere.qualitis.query.request.RulesDeleteRequest;
import com.webank.wedatasphere.qualitis.query.response.*;
import com.webank.wedatasphere.qualitis.query.service.RuleQueryService;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceCountDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDatasourceEnvDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.service.RuleDataSourceService;
import com.webank.wedatasphere.qualitis.rule.service.RuleTemplateService;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
//import com.webank.wedatasphere.qualitis.scheduled.service.ScheduledTaskService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author v_wblwyan
 * @date 2018-11-1
 */
@Service
public class RuleQueryServiceImpl implements RuleQueryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleQueryServiceImpl.class);
    @Autowired
    private FrontEndConfig frontEndConfig;
    @Autowired
    private UserDao userDao;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private ProjectUserDao projectUserDao;
    @Autowired
    private RuleDataSourceDao ruleDataSourceDao;
    @Autowired
    private RuleDatasourceEnvDao ruleDataSourceEnvDao;
    @Autowired
    private RuleDataSourceCountDao ruleDataSourceCountDao;
    @Autowired
    private MetaDataClient metaDataClient;
    @Autowired
    private RuleDataSourceService ruleDataSourceService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private RuleTemplateService ruleTemplateService;
    @Autowired
    private RuleClient ruleClient;
    @Autowired
    private ClusterInfoDao clusterInfoDao;
    @Autowired
    private DataStandardClient dataStandardClient;
//    @Autowired
//    private ScheduledTaskService scheduledTaskService;

    private HttpServletRequest httpServletRequest;

    public RuleQueryServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }


    @Override
    public DataInfo<RuleQueryDataSource> filter(PageRequest pageRequest, String user, String clusterName, String dbName, String tableName,
                                                Integer datasourceType, String subSystemId, String tagCode, String departmentName, String devDepartmentName, String envName) {
        DataInfo<RuleQueryDataSource> dataInfo = new DataInfo<>();
        List<Map<String, Object>> results = ruleDataSourceDao.filterProjectDsByUserPage(user, clusterName, dbName, tableName, datasourceType,
                subSystemId, departmentName, devDepartmentName, tagCode, envName
                , pageRequest.getPage(), pageRequest.getSize());
        if (CollectionUtils.isEmpty(results)) {
            return dataInfo;
        }
        List<RuleQueryDataSource> ruleQueryDataSources = Lists.newArrayListWithCapacity(results.size());
        for (Map<String, Object> temp : results) {
            RuleQueryDataSource ruleQueryDataSource = new RuleQueryDataSource();
            ruleQueryDataSource.setClusterName((String) temp.get("cluster_name"));
            ruleQueryDataSource.setDbName((String) temp.get("db_name"));
            ruleQueryDataSource.setTableName((String) temp.get("table_name"));
            ruleQueryDataSource.setProxyUser((String) temp.get("proxy_user"));
            ruleQueryDataSource.setDatasourceType((Integer) temp.get("datasource_type"));
            Long linkisDatasourceId = (Long) temp.get("links_datasource_id");
            ruleQueryDataSource.setDatasourceId(linkisDatasourceId != null ? linkisDatasourceId.toString() : "");
            ruleQueryDataSource.setSubSystemName((String) temp.get("sub_system_name"));
            ruleQueryDataSource.setDepartmentName((String) temp.get("department_name"));
            ruleQueryDataSource.setDevDepartmentName((String) temp.get("dev_department_name"));
            ruleQueryDataSource.setTagName((String) temp.get("tag_name"));
            ruleQueryDataSource.setEnvName((String) temp.get("env_name"));
            ruleQueryDataSources.add(ruleQueryDataSource);
        }
        setRuleCountOnTable(ruleQueryDataSources, user);
        setCommentToResp(ruleQueryDataSources, user);

        long total = ruleDataSourceDao.countProjectDsByUser(user, clusterName, dbName, tableName, datasourceType,
                subSystemId, departmentName, devDepartmentName, tagCode, envName);
        LOGGER.info("[My DataSource] Query successfully. The number of results:{}", total);
        dataInfo.setContent(ruleQueryDataSources);
        dataInfo.setTotalCount(Integer.valueOf("" + total));
        return dataInfo;
    }

    private void setCommentToResp(List<RuleQueryDataSource> ruleQueryDataSources, String user) {
        List<RuleQueryDataSource> commentRuleQueryDataSources = Lists.newArrayList();
        for (RuleQueryDataSource ruleQueryDataSource : ruleQueryDataSources) {
            String tempDbName = ruleQueryDataSource.getDbName();
            String linkisDatasourceId = ruleQueryDataSource.getDatasourceId();
            if (StringUtils.isNotEmpty(tempDbName)) {
                if (StringUtils.isBlank(linkisDatasourceId)) {
                    commentRuleQueryDataSources.add(ruleQueryDataSource);
                } else {
                    ruleQueryDataSource.setTableCommit("Not hive table");
                }
            } else {
                ruleQueryDataSource.setTableCommit("Context service temp table");
            }
        }
        try {
            for (RuleQueryDataSource ruleQueryDataSource : commentRuleQueryDataSources) {
                String tempClusterName = ruleQueryDataSource.getClusterName();
                String tempDbName = ruleQueryDataSource.getDbName();
                String tempTableName = ruleQueryDataSource.getTableName();
                String proxyUser = ruleQueryDataSource.getProxyUser();
                String comment = metaDataClient.getTableBasicInfo(tempClusterName, tempDbName, tempTableName, StringUtils.isNotBlank(proxyUser) ? proxyUser : user);
                ruleQueryDataSource.setTableCommit(comment);
            }
        } catch (Exception e) {
            LOGGER.error("Datasource {&DOES_NOT_EXIST}");
        }
    }

    /**
     * Structure: 1、cluster -> DB 2、DB->TABLE 3、All TABLE 4、cluster ->TABLE 5、All DB 6、All cluster
     *
     * @param user
     * @return
     */
    @Override
    public Map<String, Object> conditions(String user) {
        List<Map<String, Object>> projectDs = ruleDataSourceDao.findProjectDsByUser(user);

        boolean projectUsersNull = (projectDs == null || projectDs.isEmpty());
        if (projectUsersNull) {
            LOGGER.info("[My DataSource] no projects of user:{},", user);
            return null;
        }
        // Unique and classify datasource
        return getConditionsByDs(projectDs);
    }

    @Override
    public List<RuleQueryDataSource> all(String user, PageRequest pageRequest) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        List<Map<String, Object>> results = ruleDataSourceDao.findProjectDsByUserPage(user, page, size);
        List<RuleQueryDataSource> ruleQueryDataSources = new ArrayList<>(8);
        if (results != null && !results.isEmpty()) {
            for (Map<String, Object> temp : results) {
                RuleQueryDataSource ruleQueryDataSource = new RuleQueryDataSource();
                String clusterName = (String) temp.get("cluster_name");
                String dbName = (String) temp.get("db_name");
                String tableName = (String) temp.get("table_name");
                Long datasourceId = (Long) temp.get("datasource_id");
                ruleQueryDataSource.setClusterName(clusterName);
                ruleQueryDataSource.setDbName(dbName);
                ruleQueryDataSource.setTableName(tableName);
                ruleQueryDataSource.setDatasourceId(datasourceId != null ? datasourceId.toString() : "");
                String proxyUser = (String) temp.get("proxy_user");
                ruleQueryDataSource.setProxyUser(proxyUser);
                if (StringUtils.isNotBlank(dbName)) {
                    String comment = null;
                    try {
                        if (StringUtils.isNotBlank(proxyUser)) {
                            user = proxyUser;
                        }
                        if (datasourceId == null) {
                            comment = metaDataClient.getTableBasicInfo(clusterName, dbName, tableName, user);
                        } else {
                            comment = "Not hive table";
                        }
                    } catch (Exception e) {
                        LOGGER.error("Datasource {&DOES_NOT_EXIST}");
                    }
                    ruleQueryDataSource.setTableCommit(comment);
                } else {
                    ruleQueryDataSource.setTableCommit("Context service temp table");
                }
                ruleQueryDataSources.add(ruleQueryDataSource);
            }
            return ruleQueryDataSources;
        }
        return Collections.emptyList();
    }

    @Override
    public List<ColumnInfoDetail> getColumnsFromMetaService(String clusterName, Long datasourceId, String dbName, String tableName, String userName)
            throws UnExpectedRequestException, MetaDataAcquireFailedException {
        List<ColumnInfoDetail> result = null;
        User user = userDao.findByUsername(userName);
        if (user == null) {
            throw new UnExpectedRequestException(String.format("{&FAILED_TO_FIND_USER} %s", userName));
        }
        // Get create user, node name of rules.
        List<String> createUsers = ruleDataSourceDao.findRuleCreateUserByDataSource(clusterName, dbName, tableName, userName);
        try {
            if (StringUtils.isBlank(dbName)) {
                List<Rule> rules = ruleDataSourceDao.findColumnByDataSource(clusterName, dbName, tableName, "%", userName, 0, 1);
                if (rules == null || rules.isEmpty()) {
                    LOGGER.info("No rules for this datasource!");
                    return Collections.emptyList();
                }
                LOGGER.info("Rules related with context service table: [{}] are: {}", tableName, rules.toArray());
                String csId = rules.get(0).getCsId();
                String nodeName = rules.get(0).getName();
                GetUserTableByCsIdRequest getUserTableByCsIdRequest = new GetUserTableByCsIdRequest();
                getUserTableByCsIdRequest.setClusterName(clusterName);
                getUserTableByCsIdRequest.setLoginUser(userName);
                getUserTableByCsIdRequest.setNodeName(nodeName);
                getUserTableByCsIdRequest.setCsId(csId);
                DataInfo<CsTableInfoDetail> csTableInfoDetails = metaDataClient.getTableByCsId(getUserTableByCsIdRequest);
                if (csTableInfoDetails.getTotalCount() == 0) {
                    LOGGER.info("Cannot find context service table with existed rules!");
                    return Collections.emptyList();
                }
                for (CsTableInfoDetail csTableInfoDetail : csTableInfoDetails.getContent()) {
                    if (csTableInfoDetail.getTableName().equals(tableName)) {
                        GetUserColumnByCsRequest getUserColumnByCsRequest = new GetUserColumnByCsRequest();
                        getUserColumnByCsRequest.setClusterName(clusterName);
                        getUserColumnByCsRequest.setContextKey(csTableInfoDetail.getContextKey());
                        getUserColumnByCsRequest.setCsId(csId);
                        getUserColumnByCsRequest.setLoginUser(userName);
                        result = metaDataClient.getColumnByCsId(getUserColumnByCsRequest).getContent();
                        setRuleCountOnColumn(result, createUsers, clusterName, dbName, tableName);
                        break;
                    } else {
                        continue;
                    }
                }

            } else {
                if (datasourceId == null) {
                    result = metaDataClient.getColumnInfo(clusterName, dbName, tableName, userName);
                } else {
                    result = metaDataClient.getColumnsByDataSource(clusterName, userName, datasourceId, dbName, tableName).getContent();
                }
                setRuleCountOnColumn(result, createUsers, clusterName, dbName, tableName);
            }
            LOGGER.info("Datasource table number of columns: {}", result == null ? 0 : result.size());
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error("Datasource colums info {&DOES_NOT_EXIST}. Exception: {}", e);
            throw new MetaDataAcquireFailedException("Datasource colums info {&DOES_NOT_EXIST}. Exception: " + e.getMessage());
        } catch (UnExpectedRequestException e) {
            LOGGER.error("{&QUERY_PARAM_HAS_ERROR}. Exception: {}", e);
            throw new UnExpectedRequestException("{&QUERY_PARAM_HAS_ERROR}. Exception: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("exception: {}", e);
        }
        return result;
    }

    @Override
    public List<ColumnInfoDetail> filterColumns(List<ColumnInfoDetail> columnInfoDetailList, RuleQueryRequest filterCondition) {
        if (CollectionUtils.isEmpty(columnInfoDetailList)) {
            return columnInfoDetailList;
        }
        return columnInfoDetailList.stream()
                .filter(columnInfoDetail -> {
                    if (StringUtils.isNotEmpty(filterCondition.getFieldName())) {
                        return StringUtils.contains(columnInfoDetail.getFieldName(), filterCondition.getFieldName());
                    }
                    return true;
                })
                .filter(columnInfoDetail -> {
                    if (StringUtils.isNotEmpty(filterCondition.getDataType())) {
                        return filterCondition.getDataType().equals(columnInfoDetail.getDataType());
                    }
                    return true;
                })
                .filter(columnInfoDetail -> {
                    if (Objects.nonNull(filterCondition.getPartitionField())) {
                        boolean isPartitionField = Objects.nonNull(columnInfoDetail.getPartitionField()) ? columnInfoDetail.getPartitionField() : false;
                        return filterCondition.getPartitionField().equals(isPartitionField);
                    }
                    return true;
                })
                .filter(columnInfoDetail -> {
                    if (Objects.nonNull(filterCondition.getPrimary())) {
                        boolean isPrimary = Objects.nonNull(columnInfoDetail.getPrimary()) ? columnInfoDetail.getPrimary() : false;
                        return filterCondition.getPrimary().equals(isPrimary);
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    @Override
    public DataInfo<HiveRuleDetail> getRulesByCondition(String cluster, String db, String table, String column, String userName, Long ruleTemplateId, Integer relationObjectType, int page, int size) {
        LOGGER.info("Start to get rules with column. cluster: {}, db: {}, table: {}, column: {}", cluster, db, table, column);
        DataInfo<HiveRuleDetail> dataInfo = new DataInfo<>();
        Page<Rule> rules = ruleDao.findRuleByDataSource(cluster, db, table, column, userName, ruleTemplateId, relationObjectType, page, size);
        LOGGER.info("Success to get rules with column. Rules: {}", rules);
        List<HiveRuleDetail> result = new ArrayList<>(rules.getSize());
        for (Rule rule : rules) {
            HiveRuleDetail hiveRuleDetail = new HiveRuleDetail(rule);
            hiveRuleDetail.setTemplateName(rule.getTemplate().getName());
            hiveRuleDetail.setProjectName(rule.getProject().getName());
            Set<RuleDataSource> ruleDataSources = rule.getRuleDataSources();
            if (CollectionUtils.isNotEmpty(ruleDataSources)) {
                Optional<RuleDataSource> optional = ruleDataSources.stream().findFirst();
                if (optional.isPresent()) {
                    RuleDataSource ruleDataSource = optional.get();
                    hiveRuleDetail.setRelationObject(StringUtils.isNotEmpty(ruleDataSource.getColName()) ? ruleDataSource.getColName() : ruleDataSource.getTableName());
                }
            }
            result.add(hiveRuleDetail);
        }
        dataInfo.setContent(result);
        dataInfo.setTotalCount(Long.valueOf(rules.getTotalElements()).intValue());
        return dataInfo;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public void deleteRules(RulesDeleteRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        for (long ruleId : request.getRuleIdList()) {
            Rule ruleInDb = ruleDao.findById(ruleId);
            String loginUser = HttpUtils.getUserName(httpServletRequest);
            // Check existence of rule
            if (ruleInDb == null) {
                throw new UnExpectedRequestException("Rule id [" + ruleId + "]) {&DOES_NOT_EXIST}");
            }
            // Check existence of project
            Project projectInDb = projectService.checkProjectExistence(ruleInDb.getProject().getId(),
                    loginUser);
            // Check permissions of project
            List<Integer> permissions = new ArrayList<>();
            permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
            projectService.checkProjectPermission(projectInDb, loginUser, permissions);
//            scheduledTaskService.checkRuleGroupIfDependedBySchedule(ruleInDb.getRuleGroup());
            // Delete rule
            ruleDao.deleteRule(ruleInDb);
            LOGGER.info("Succeed to delete rule, rule id: {}", ruleInDb.getId());
            if (ruleInDb.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
                // Delete template of custom rule
                ruleTemplateService.deleteCustomTemplate(ruleInDb.getTemplate());
                LOGGER.info("Succeed to delete custom rule, rule id: {}", ruleInDb.getId());
            }
        }
    }

    @Override
    public boolean compareDataSource(List<String> cols, List<ColumnInfoDetail> columnInfoDetails) {
        for (String colInfo : cols) {
            if (metaDataClient.fieldExist(colInfo, columnInfoDetails, null)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> findCols(String cluster, String db, String table, String user) {
        return ruleDataSourceDao.findColsByUser(user, cluster, db, table);
    }

    @Override
    public int count(String user, String clusterName, String dbName, String tableName, Integer datasourceType, String subSystemId,
                     String departmentName, String devDepartmentName, String tagCode, String envName) {
        long count = ruleDataSourceDao.countProjectDsByUser(user, clusterName, dbName, tableName, datasourceType, subSystemId, departmentName, devDepartmentName, tagCode, envName);
        return (int) count;
    }

    @Override
    public DataInfo<Map<String, Object>> getTagList() throws MetaDataAcquireFailedException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        List<RuleDataSource> ruleDataSourceList = ruleDataSourceDao.findAllTagByUser(loginUser);
        List<Map<String, Object>> dataList = ruleDataSourceList.stream().map(ruleDataSource -> {
            Map<String, Object> dataMap = Maps.newHashMapWithExpectedSize(2);
            dataMap.put("tag_code", ruleDataSource.getTagCode());
            dataMap.put("tag_name", ruleDataSource.getTagName());
            return dataMap;
        }).collect(Collectors.toList());
        DataInfo dataInfo = new DataInfo();
        dataInfo.setTotalCount(dataList.size());
        dataInfo.setContent(dataList);
        return dataInfo;
    }

    /**
     * get some parameter for lineage view
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    @Override
    public LineageParameterResponse getLineageParameter(LineageParameterRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        LOGGER.info("getLineageParameter, req: {}", request);
        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(request.getClusterName());
        if (clusterInfo == null) {
            throw new UnExpectedRequestException(String.format("集群不存在: %s", request.getClusterName()));
        }
        if (StringUtils.isEmpty(clusterInfo.getHiveUrn())) {
            throw new UnExpectedRequestException(String.format("该集群不存在urn: %s", request.getClusterName()));
        }
        String dbId = getDbFromDatamap(request.getDbName(), clusterInfo);
        if (StringUtils.isEmpty(dbId)) {
            throw new UnExpectedRequestException(String.format("找不到对应的库id: %s", request.getDbName()));
        }
        Integer tableId = getDatasetFromDatamap(dbId, request.getTableName(), clusterInfo);
        if (Objects.isNull(tableId)) {
            throw new UnExpectedRequestException(String.format("找不到对应的表id: %s", request.getTableName()));
        }
        LineageParameterResponse response = new LineageParameterResponse();
        response.setClusterName(request.getClusterName());
        response.setDbName(request.getDbName());
        response.setTableName(request.getTableName());
        response.setTableId(tableId);
        response.setDbId(dbId);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("hive:///");
        stringBuilder.append(clusterInfo.getHiveUrn());
        stringBuilder.append("/");
        stringBuilder.append(request.getDbName());
        stringBuilder.append("/");
        stringBuilder.append(request.getTableName());

        if (StringUtils.isNotEmpty(request.getColumnName())) {
            stringBuilder.append("/");
            stringBuilder.append(request.getColumnName());
            String type = "column";
            stringBuilder.append("&type=").append(type)
                         .append("&baseUrl=").append(frontEndConfig.getDomainName().replace("{IP}", QualitisConstants.QUALITIS_SERVER_HOST)).append("/dms");
        } else {
            String type = "table";
            stringBuilder.append("&type=").append(type)
                .append("&baseUrl=").append(frontEndConfig.getDomainName().replace("{IP}", QualitisConstants.QUALITIS_SERVER_HOST)).append("/dms");
        }

        response.setUrn(stringBuilder.toString());
        return response;
    }

    /**
     * get dbId from dataMap
     *
     * @param searchKey
     * @param clusterInfo
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    public String getDbFromDatamap(String searchKey, ClusterInfo clusterInfo) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        Map<String, Object> response = dataStandardClient.getDatabase(searchKey, loginUser);
        Optional<Map<String, Object>> idOptional = ((List<Map<String, Object>>) response.get("content")).stream()
                .filter(map -> clusterInfo.getClusterType().contains((String) map.get("value")) && searchKey.equals(map.get("name")))
                .findFirst();
        if (idOptional.isPresent()) {
            return (String) idOptional.get().get("id");
        }
        return null;
    }

    /**
     * get tableId from dataMap
     *
     * @param dbId
     * @param datasetName
     * @param clusterInfo
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    public Integer getDatasetFromDatamap(String dbId, String datasetName, ClusterInfo clusterInfo) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        int page = 0;
        int size = 200;
        Map<String, Object> response = dataStandardClient.getDataset(dbId, datasetName, page, size, loginUser);
        Optional<Map<String, Object>> idOptional = ((List<Map<String, Object>>) response.get("content")).stream()
                .filter(map -> clusterInfo.getClusterType().contains((String) map.get("clusterType")) && BigDecimal.valueOf(Double.parseDouble(dbId)).compareTo(BigDecimal.valueOf((Double) map.get("dbId"))) == 0 && datasetName.equals(map.get("datasetName")))
                .findFirst();
        if (idOptional.isPresent()) {
            Double id = (Double) idOptional.get().get("datasetId");
            return id.intValue();
        }
        return null;
    }

    private RuleQueryTable newRuleQueryTable(RuleDataSource ds) {
        RuleQueryCol col = new RuleQueryCol(ds);
        return new RuleQueryTable(col);
    }

    private RuleQueryDb newRuleQueryDb(RuleDataSource ds) {
        return new RuleQueryDb(newRuleQueryTable(ds));
    }

    private RuleQueryCluster newRuleQueryCluster(RuleDataSource ds) {
        return new RuleQueryCluster(newRuleQueryDb(ds));
    }

    private RuleQueryRule newRuleQueryRule(Rule rule, RuleDataSource ds) {
        return new RuleQueryRule(newRuleQueryCluster(ds), rule);
    }

    private Map<String, Object> getConditionsByDs(List<Map<String, Object>> projectDs) {
        Integer total = projectDs.size();
        // result
        Map<String, Object> results = new HashMap<>(7);
        // all clusters
        Set<String> clusters = new HashSet<>();
        // all dbs
        Set<String> dbs = new HashSet<>();
        // all tables
        Set<String> tables = new HashSet<>();
        // relationship between clusters and dbs
        Map<String, Set<String>> clusterDbs = new HashMap<>(projectDs.size());
        // relationship between dbs and tables
        Map<String, Set<String>> dbTables = new HashMap<>(projectDs.size());
        // relationship between clusters and tables
        Map<String, Set<String>> clusterTables = new HashMap<>(projectDs.size());
        for (Map<String, Object> ds : projectDs) {
            putIntoTotalData(ds, clusters, dbs, tables);
            putIntoCascadeData(ds, clusterDbs, dbTables, clusterTables);
        }
        results.put("clusters", clusters);
        results.put("dbs", dbs);
        results.put("tables", tables);
        results.put("cluster_dbs", clusterDbs);
        results.put("db_tables", dbTables);
        results.put("cluster_tables", clusterTables);
        results.put("total", total);

        // DCN
        List<String> envNames = ruleDataSourceEnvDao.findAllEnvName();
        if (CollectionUtils.isNotEmpty(envNames)) {
            results.put("envNames", envNames.stream().distinct().collect(Collectors.toList()));
        } else {
            results.put("envNames", new ArrayList<String>());
        }
        return results;
    }

    private void putIntoTotalData(Map<String, Object> ds, Set<String> clusters, Set<String> dbs,
                                  Set<String> tables) {
        String clusterName = (String) ds.get("cluster_name");
        String dbName = (String) ds.get("db_name");
        String tableName = (String) ds.get("table_name");

        clusters.add(clusterName);
        if (dbName == null) {
            dbs.add("default");
        } else {
            dbs.add(dbName);
        }
        tables.add(tableName);
    }

    private void putIntoCascadeData(Map<String, Object> ds, Map<String, Set<String>> clusterDbs, Map<String, Set<String>> dbTables, Map<String, Set<String>> clusterTables) {
        String clusterName = (String) ds.get("cluster_name");
        String dbName = (String) ds.get("db_name");
        String tableName = (String) ds.get("table_name");
        Set<String> clusterDbsSet = clusterDbs.computeIfAbsent(clusterName, k -> new HashSet<>());
        Set<String> dbTablesSet = null;
        if (dbName == null) {
            clusterDbsSet.add("default");
            dbTablesSet = dbTables.computeIfAbsent("default", k -> new HashSet<>());
        } else {
            clusterDbsSet.add(dbName);
            dbTablesSet = dbTables.computeIfAbsent(dbName, k -> new HashSet<>());
        }

        dbTablesSet.add(tableName);

        Set<String> clusterTablesSet = clusterTables.computeIfAbsent(clusterName, k -> new HashSet<>());
        clusterTablesSet.add(tableName);
    }

    /**
     * add field on column: rule_count
     *
     * @param result
     * @param users
     * @param clusterName
     * @param dbName
     * @param tableName
     */
    private void setRuleCountOnColumn(List<ColumnInfoDetail> result, List<String> users, String clusterName, String dbName, String tableName) {
        if (CollectionUtils.isEmpty(result)) {
            LOGGER.warn("ColumnInfoDetails is empty");
            return;
        }
        List<String> fieldNames = result.stream().map(columnInfoDetail -> columnInfoDetail.getFieldName() + ":" + columnInfoDetail.getDataType()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(fieldNames)) {
            LOGGER.warn("Columns is empty");
            return;
        }
        List<Map<String, Object>> ruleMaps = ruleDataSourceDao.countRuleCountByGroup(Arrays.asList(clusterName), Arrays.asList(dbName), Arrays.asList(tableName), fieldNames, users);
        Map<String, Integer> ruleCountMap = Maps.newHashMapWithExpectedSize(ruleMaps.size());
        ruleMaps.stream().forEach(map -> {
            String cluster = (String) map.getOrDefault("clusterName", "");
            String db = (String) map.getOrDefault("dbName", "");
            String table = (String) map.getOrDefault("tableName", "");
            String field = (String) map.getOrDefault("colName", "");
            long ruleCount = (Long) map.getOrDefault("ruleCount", 0);
            ruleCountMap.put(cluster + db + table + field, (int) ruleCount);
        });

        String prefix = clusterName + dbName + tableName;
        result.forEach(columnInfoDetail -> {
            String key = prefix + columnInfoDetail.getFieldName() + ":" + columnInfoDetail.getDataType();
            columnInfoDetail.setRuleCount(ruleCountMap.get(key));
        });
    }

    /**
     * add field on table: rule_count
     *
     * @param ruleQueryDataSources
     * @param user
     */
    private void setRuleCountOnTable(List<RuleQueryDataSource> ruleQueryDataSources, String user) {
        List<String> tableNames = ruleQueryDataSources.stream().map(RuleQueryDataSource::getTableName).collect(Collectors.toList());
        List<String> clusterNames = ruleQueryDataSources.stream().map(RuleQueryDataSource::getClusterName).distinct().collect(Collectors.toList());
        List<String> dbNames = ruleQueryDataSources.stream().map(RuleQueryDataSource::getDbName).distinct().collect(Collectors.toList());
        List<Map<String, Object>> ruleMaps = ruleDataSourceDao.countRuleCountByGroup(clusterNames, dbNames, tableNames, user);
        Map<String, Integer> ruleCountMap = Maps.newHashMapWithExpectedSize(tableNames.size());
        ruleMaps.stream().forEach(map -> {
            String clusterName = (String) map.getOrDefault("clusterName", "");
            String dbName = (String) map.getOrDefault("dbName", "");
            String tableName = (String) map.getOrDefault("tableName", "");
            long ruleCount = (Long) map.getOrDefault("ruleCount", 0);
            ruleCountMap.put(clusterName + dbName + tableName, (int) ruleCount);
        });

        ruleQueryDataSources.forEach(ruleQueryDataSource -> {
            String key = ruleQueryDataSource.getClusterName() + ruleQueryDataSource.getDbName() + ruleQueryDataSource.getTableName();
            ruleQueryDataSource.setRuleCount(ruleCountMap.get(key));
        });
    }

}
