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

import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserColumnByCsRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserTableByCsIdRequest;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.CsTableInfoDetail;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectUserDao;
import com.webank.wedatasphere.qualitis.project.response.HiveRuleDetail;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.query.queryqo.DataSourceQo;
import com.webank.wedatasphere.qualitis.query.request.RuleQueryRequest;
import com.webank.wedatasphere.qualitis.query.request.RulesDeleteRequest;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryCluster;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryDataSource;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryDb;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryProject;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryTable;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectUser;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryCol;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryRule;
import com.webank.wedatasphere.qualitis.query.service.RuleQueryService;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceCountDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;

import com.webank.wedatasphere.qualitis.rule.service.RuleDataSourceService;
import com.webank.wedatasphere.qualitis.rule.service.RuleTemplateService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author v_wblwyan
 * @date 2018-11-1
 */
@Service
public class RuleQueryServiceImpl implements RuleQueryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleQueryServiceImpl.class);
    @Autowired
    private UserDao userDao;
    @Autowired
    private RuleDao ruleDao;
    @Autowired
    private ProjectUserDao projectUserDao;
    @Autowired
    private RuleDataSourceDao ruleDataSourceDao;
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

    private HttpServletRequest httpServletRequest;

    public RuleQueryServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * Initialize rule query
     *
     * @param user
     * @return List<RuleQueryProject>
     */
    @Override
    public List<RuleQueryProject> init(String user) {
        RuleQueryRequest param = new RuleQueryRequest(user);
        return query(param);
    }

    /**
     * Query rule by creator
     *
     * @param queryParam
     * @return List<RuleQueryProject>
     */
    @Override
    public List<RuleQueryProject> query(RuleQueryRequest queryParam) {
        DataSourceQo param = new DataSourceQo(queryParam);
        // Query project user by creator
        List<ProjectUser> projectUsers = projectUserDao.findByUsernameAndPermissionsIn(param);
        boolean projectUsersNull = (projectUsers == null || projectUsers.isEmpty());
        if (projectUsersNull) {
            LOGGER.info("[My DataSource] Find no projects of user:{},", queryParam.getUser());
            return null;
        }

        Map<Long, RuleQueryProject> projectMap = new HashMap<>(4);

        getProjectsByUserPerm(param, projectUsers, projectMap);
        if (projectMap.values().isEmpty()) {
            LOGGER.info("[My DataSource] Find no datasources/rules of user, user: {}", queryParam.getUser());
            return null;
        }
        return new ArrayList<>(projectMap.values());
    }

    @Override
    public List<RuleQueryDataSource> filter(PageRequest pageRequest, String user, String clusterName,
        String dbName, String tableName, boolean getTotal) {
        if (clusterName == null || "".equals(clusterName)) {
            clusterName = "%";
        }
        if (dbName == null || "".equals(dbName)) {
            dbName = "%";
        }
        if (tableName == null || "".equals(tableName)) {
            tableName = "%";
        }
        List<Map<String, Object>> results = null;
        if (pageRequest == null) {
            results = ruleDataSourceDao.filterProjectDsByUser(user, clusterName, dbName, tableName);
        } else {
            results = ruleDataSourceDao.filterProjectDsByUserPage(user, clusterName, dbName, tableName, pageRequest.getPage(), pageRequest.getSize());
        }

        List<RuleQueryDataSource> ruleQueryDataSources = new ArrayList<>(8);
        if (results != null && ! results.isEmpty()) {
            for (Map<String, Object> temp : results) {
                RuleQueryDataSource ruleQueryDataSource = new RuleQueryDataSource();
                String tempClusterName = (String) temp.get("cluster_name");
                String tempDbName = (String) temp.get("db_name");
                String tempTableName = (String) temp.get("table_name");
                String proxyUser = (String) temp.get("proxy_user");
                Long datasourceId = (Long) temp.get("datasource_id");
                if (StringUtils.isNotBlank(proxyUser)) {
                    user = proxyUser;
                }
                ruleQueryDataSource.setClusterName(tempClusterName);
                ruleQueryDataSource.setDbName(tempDbName);
                ruleQueryDataSource.setTableName(tempTableName);
                ruleQueryDataSource.setProxyUser(proxyUser);
                ruleQueryDataSource.setDatasourceId(datasourceId != null ? datasourceId.toString() : "");
                if (tempDbName != null && ! "".equals(tempDbName)) {
                    String comment = null;
                    try {
                        if (! getTotal) {
                            if (datasourceId == null) {
                                comment = metaDataClient.getTableBasicInfo(tempClusterName, tempDbName, tempTableName, user);
                            } else {
                                comment = "Not hive table";
                            }
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
        return null;
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
        List<Map<String, Object>> results = ruleDataSourceDao.findProjectDsByUser(user, page, size);
        List<RuleQueryDataSource> ruleQueryDataSources = new ArrayList<>(8);
        if (results != null && ! results.isEmpty()) {
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
        return null;
    }

    @Override
    public List<ColumnInfoDetail> getColumnsByTableName(String clusterName, Long datasourceId, String dbName, String tableName, String userName)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        List<ColumnInfoDetail> result = null;
        User user = userDao.findByUsername(userName);
        if (user == null) {
            throw new UnExpectedRequestException(String.format("{&FAILED_TO_FIND_USER} %s", userName));
        }
        try {
            if (StringUtils.isBlank(dbName)) {
                List<Rule> rules =  ruleDataSourceDao.findRuleByDataSource(clusterName, dbName, tableName, "%", userName);
                if (rules == null || rules.isEmpty()) {
                    LOGGER.info("No rules for this datasource!");
                    return null;
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
                    return null;
                }
                for (CsTableInfoDetail csTableInfoDetail : csTableInfoDetails.getContent()) {
                    if (csTableInfoDetail.getTableName().equals(tableName)) {
                        GetUserColumnByCsRequest getUserColumnByCsRequest = new GetUserColumnByCsRequest();
                        getUserColumnByCsRequest.setClusterName(clusterName);
                        getUserColumnByCsRequest.setContextKey(csTableInfoDetail.getContextKey());
                        getUserColumnByCsRequest.setCsId(csId);
                        getUserColumnByCsRequest.setLoginUser(userName);
                        result = metaDataClient.getColumnByCsId(getUserColumnByCsRequest).getContent();
                        setRuleCount(result, user.getId(), clusterName, dbName, tableName);
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
                setRuleCount(result, user.getId(), clusterName, dbName, tableName);
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
    public DataInfo<HiveRuleDetail> getRulesByColumn(String cluster, String db, String table, String column, String userName, int page, int size) {
        LOGGER.info("Start to get rules with column. cluster: {}, db: {}, table: {}, column: {}", cluster, db, table, column);
        DataInfo<HiveRuleDetail> dataInfo = new DataInfo<>();
        List<Rule> rules = ruleDataSourceDao.findRuleByDataSource(cluster, db, table, column, userName, page, size);
        int total = ruleDataSourceDao.countRuleByDataSource(cluster, db, table, column, userName);

        if (rules == null || rules.isEmpty()) {
            return null;
        }
        List<HiveRuleDetail> result = new ArrayList<>(rules.size());
        LOGGER.info("Success to get rules with column. Rules: {}", rules);
        for (Rule rule : rules) {
            HiveRuleDetail hiveRuleDetail = new HiveRuleDetail(rule);
            hiveRuleDetail.setTemplateName(rule.getRuleTemplateName());
            result.add(hiveRuleDetail);
        }
        dataInfo.setContent(result);
        dataInfo.setTotalCount(total);
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
            // Update rule count of datasource
            ruleDataSourceService.updateRuleDataSourceCount(ruleInDb, -1);
            // Delete rule
            ruleDao.deleteRule(ruleInDb);
            LOGGER.info("Succeed to delete rule. rule_id: {}", ruleInDb.getId());
            if (ruleInDb.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
                // Delete template of custom rule
                ruleTemplateService.deleteCustomTemplate(ruleInDb.getTemplate());
                LOGGER.info("Succeed to delete custom rule. rule_id: {}", ruleInDb.getId());
            }
        }
    }

    @Override
    public boolean compareDataSource(List<String> cols, List<ColumnInfoDetail> columnInfoDetails) {
        for (String colInfo : cols) {
            if(metaDataClient.fieldExist(colInfo, columnInfoDetails, null)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> findCols(String cluster, String db, String table, String user) {
        return ruleDataSourceDao.findColsByUser(user, cluster, db, table);
    }

    private List<RuleQueryProject> getProjectsByUserPerm(DataSourceQo param,
        List<ProjectUser> projectUsers, Map<Long, RuleQueryProject> projectMap) {
        if (projectUsers == null || projectUsers.isEmpty()) {
            return null;
        }
        for (ProjectUser projectUser : projectUsers) {
            List<RuleDataSource> projectDataSources = ruleDataSourceDao.findByProjectUser(
                projectUser.getProject().getId(), param.getCluster(), param.getDb(), param.getTable());
            if (projectDataSources == null || projectDataSources.isEmpty()) {
                continue;
            }
            addRuleDataSource(projectDataSources, projectMap, projectUser.getProject());
        }
        return new ArrayList<>(projectMap.values());
    }

    /**
     * Add the list of datasource into response in hierarchical relationship.
     *
     * @param projectDataSources
     * @param projectMap
     * @param project
     */
    private void addRuleDataSource(List<RuleDataSource> projectDataSources,
        Map<Long, RuleQueryProject> projectMap, Project project) {
        for (RuleDataSource ds : projectDataSources) {
            putIntoProject(ds, projectMap, project);
        }
    }

    private void putIntoProject(RuleDataSource ds, Map<Long, RuleQueryProject> projectMap,
        Project project) {
        Rule rule = ds.getRule();
        Long projectId = ds.getProjectId();
        // If contains
        if (projectMap.containsKey(projectId)) {
            RuleQueryProject queryProject = projectMap.get(projectId);
            putIntoRuleOfProject(ds, queryProject);
            return;
        }
        // If not contain
        RuleQueryProject queryProject = newRuleQueryProject(project, rule, ds);
        projectMap.put(projectId, queryProject);
    }

    private void putIntoRuleOfProject(RuleDataSource ds, RuleQueryProject queryProject) {
        Rule rule = ds.getRule();
        Long ruleId = rule.getId();
        if (queryProject.getDataMap().containsKey(ruleId)) {
            RuleQueryRule queryRule = queryProject.getDataMap().get(ruleId);
            putIntoClusterOfRule(ds, queryRule);
            return;
        }
        // If not contain
        RuleQueryRule ruleSrc = newRuleQueryRule(rule, ds);
        queryProject.addRuleQueryRule(ruleSrc);
    }

    private void putIntoClusterOfRule(RuleDataSource ds, RuleQueryRule queryRule) {
        String clusterKey = ds.getClusterName();
        if (queryRule.getDataMap().containsKey(clusterKey)) {
            RuleQueryCluster queryCluster = queryRule.getDataMap().get(clusterKey);
            putIntoDbOfCluster(ds, queryCluster);
            return;
        }
        // If not contain
        RuleQueryCluster cluster = newRuleQueryCluster(ds);
        queryRule.addRuleQueryCluster(cluster);
    }

    private void putIntoDbOfCluster(RuleDataSource ds, RuleQueryCluster queryCluster) {
        String dbKey = String.format("%s.%s", ds.getClusterName(), ds.getDbName());
        if (queryCluster.getDataMap().containsKey(dbKey)) {
            RuleQueryDb queryDb = queryCluster.getDataMap().get(dbKey);
            putIntoTableOfDb(ds, queryDb);
            return;
        }
        // If not contain
        RuleQueryDb db = newRuleQueryDb(ds);
        queryCluster.addRuleQueryDb(db);
    }

    private void putIntoTableOfDb(RuleDataSource ds, RuleQueryDb queryDb) {
        String tableKey = String.format("%s.%s.%s", ds.getClusterName(), ds.getDbName(),
            ds.getTableName());
        if (queryDb.getDataMap().containsKey(tableKey)) {
            RuleQueryTable queryTable = queryDb.getDataMap().get(tableKey);
            RuleQueryCol col = new RuleQueryCol(ds);
            queryTable.addRuleQueryCol(col);
            return;
        }
        // If not contain
        RuleQueryTable table = newRuleQueryTable(ds);
        queryDb.addRuleQueryTable(table);
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

    private RuleQueryProject newRuleQueryProject(Project project, Rule rule, RuleDataSource ds) {
        return new RuleQueryProject(newRuleQueryRule(rule, ds), project);
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

    private void setRuleCount(List<ColumnInfoDetail> result, Long userId, String clusterName, String dbName, String tableName) {
        for (ColumnInfoDetail columnInfoDetail : result) {
            Integer count = ruleDataSourceCountDao.findCount(clusterName + "-" + dbName + "-" + tableName
                + "-" + columnInfoDetail.getFieldName() + ":" + columnInfoDetail.getDataType(), userId);
            if (count == null || count < 0) {
                LOGGER.error("Rule count of datasource is unlegal.");
            }
            columnInfoDetail.setRuleCount(count);
        }
    }
}
