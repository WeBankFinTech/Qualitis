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

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
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
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;

import com.webank.wedatasphere.qualitis.rule.service.RuleTemplateService;
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

    private static final Logger LOG = LoggerFactory.getLogger(RuleQueryServiceImpl.class);
    @Autowired
    private ProjectUserDao projectUserDao;
    @Autowired
    private RuleDataSourceDao ruleDataSourceDao;
    @Autowired
    private MetaDataClient metaDataClient;
    @Autowired
    RuleDao ruleDao;
    @Autowired
    RuleTemplateService ruleTemplateService;
    @Autowired
    ProjectService projectService;

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
            LOG.info("[My DataSource] Find no projects of user:{},", queryParam.getUser());
            return null;
        }

        Map<Long, RuleQueryProject> projectMap = new HashMap<>(4);

        getProjectsByUserPerm(param, projectUsers, projectMap);
        if (projectMap.values().isEmpty()) {
            LOG.info("[My DataSource] Find no datasources/rules of user, user: {}", queryParam.getUser());
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
        List<Map<String, String>> results = null;
        if (pageRequest == null) {
            results = ruleDataSourceDao.filterProjectDsByUser(user, clusterName, dbName, tableName);
        } else {
            results = ruleDataSourceDao.filterProjectDsByUserPage(user, clusterName, dbName, tableName, pageRequest.getPage(), pageRequest.getSize());
        }

        List<RuleQueryDataSource> ruleQueryDataSources = new ArrayList<>(8);
        if (results != null && ! results.isEmpty()) {
            for (Map<String, String> temp : results) {
                RuleQueryDataSource ruleQueryDataSource = new RuleQueryDataSource();
                String tempClusterName = temp.get("cluster_name");
                String tempDbName = temp.get("db_name");
                String tempTableName = temp.get("table_name");
                ruleQueryDataSource.setClusterName(tempClusterName);
                ruleQueryDataSource.setDbName(tempDbName);
                ruleQueryDataSource.setTableName(tempTableName);
                if (tempDbName != null && ! "".equals(tempDbName)) {
                    String comment = null;
                    try {
                        if (! getTotal) {
                            comment = metaDataClient.getTableComment(tempClusterName, tempDbName, tempTableName, user);
                        }
                    } catch (Exception e) {
                        LOG.error("Datasource {&DOES_NOT_EXIST}");
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
        List<Map<String, String>> projectDs = ruleDataSourceDao.findProjectDsByUser(user);

        boolean projectUsersNull = (projectDs == null || projectDs.isEmpty());
        if (projectUsersNull) {
            LOG.info("[My DataSource] no projects of user:{},", user);
            return null;
        }
        // Unique and classify datasource
        return getConditionsByDs(projectDs);
    }

    @Override
    public List<RuleQueryDataSource> all(String user, PageRequest pageRequest) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        List<Map<String, String>> results = ruleDataSourceDao.findProjectDsByUser(user, page, size);
        LOG.info("Success to find all datasource, datasource: {}", results);
        List<RuleQueryDataSource> ruleQueryDataSources = new ArrayList<>(8);
        if (results != null && ! results.isEmpty()) {
            for (Map<String, String> temp : results) {
                RuleQueryDataSource ruleQueryDataSource = new RuleQueryDataSource();
                String clusterName = temp.get("cluster_name");
                String dbName = temp.get("db_name");
                String tableName = temp.get("table_name");
                ruleQueryDataSource.setClusterName(clusterName);
                ruleQueryDataSource.setDbName(dbName);
                ruleQueryDataSource.setTableName(tableName);
                if (dbName != null && ! "".equals(dbName)) {
                    String comment = null;
                    try {
                        comment = metaDataClient.getTableComment(clusterName, dbName, tableName, user);
                    } catch (Exception e) {
                        LOG.error("Datasource {&DOES_NOT_EXIST}");
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
    public List<ColumnInfoDetail> getColumnsByTableName(String clusterName, String dbName, String tableName, String user) {
        List<ColumnInfoDetail> result = null;
        try {
            if (dbName == null || "".equals(dbName)) {
                LOG.info("No rules for this datasource!");
                return null;
            } else {
                result = metaDataClient.getColumnInfo(clusterName, dbName, tableName, user);
            }
            LOG.info("Datasource table number of columns: {}", result == null ? 0 : result.size());
        } catch (MetaDataAcquireFailedException e) {
            LOG.error("Datasource colums info {&DOES_NOT_EXIST}, exception: {}", e);
        } catch (UnExpectedRequestException e) {
            LOG.error("Request param error for datasource colums info , exception: {}", e);
        }
        return result;
    }

    @Override
    public List<HiveRuleDetail> getRulesByColumn(String cluster, String db, String table, String column, String user) {
        LOG.info("Start to get rules with column. cluster: {}, db: {}, table: {}, column: {}", cluster, db, table, column);
        List<Rule> rules =  ruleDataSourceDao.findRuleByDataSource(cluster, db, table, column, user);
        if (rules == null || rules.isEmpty()) {
            return null;
        }
        List<HiveRuleDetail> result = new ArrayList<>(8);
        LOG.info("Success to get rules with column. Rules: {}", rules);
        for (Rule rule : rules) {
            HiveRuleDetail hiveRuleDetail = new HiveRuleDetail(rule);
            hiveRuleDetail.setTemplateName(rule.getRuleTemplateName());
            result.add(hiveRuleDetail);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public void deleteRules(RulesDeleteRequest request) throws UnExpectedRequestException {
        for (long ruleId : request.getRuleIdList()) {
            Rule ruleInDb = ruleDao.findById(ruleId);
            projectService.checkProjectExistence(ruleInDb.getProject().getId());
            // Delete rule
            ruleDao.deleteRule(ruleInDb);
            LOG.info("Succeed to delete rule. rule_id: {}", ruleInDb.getId());
            if (ruleInDb.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
                // Delete template of custom rule
                ruleTemplateService.deleteCustomTemplate(ruleInDb.getTemplate());
                LOG.info("Succeed to delete custom rule. rule_id: {}", ruleInDb.getId());
            }
        }
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

    private Map<String, Object> getConditionsByDs(List<Map<String, String>> projectDs) {
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
        for (Map<String, String> ds : projectDs) {
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

    private void putIntoTotalData(Map<String, String> ds, Set<String> clusters, Set<String> dbs,
        Set<String> tables) {
        String clusterName = ds.get("cluster_name");
        String dbName = ds.get("db_name");
        String tableName = ds.get("table_name");

        clusters.add(clusterName);
        if (dbName == null) {
            dbs.add("default");
        } else {
            dbs.add(dbName);
        }
        tables.add(tableName);
    }

    private void putIntoCascadeData(Map<String, String> ds, Map<String, Set<String>> clusterDbs,
        Map<String, Set<String>> dbTables, Map<String, Set<String>> clusterTables) {
        String clusterName = ds.get("cluster_name");
        String dbName = ds.get("db_name");
        String tableName = ds.get("table_name");
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
}
