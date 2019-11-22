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

import com.webank.wedatasphere.qualitis.project.dao.ProjectUserDao;
import com.webank.wedatasphere.qualitis.query.queryqo.DataSourceQo;
import com.webank.wedatasphere.qualitis.query.request.RuleQueryRequest;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryCluster;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryDb;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryProject;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryTable;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectUser;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryCol;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryRule;
import com.webank.wedatasphere.qualitis.query.service.RuleQueryService;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;

import com.webank.wedatasphere.qualitis.query.queryqo.DataSourceQo;
import com.webank.wedatasphere.qualitis.query.request.RuleQueryRequest;
import com.webank.wedatasphere.qualitis.query.response.*;
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

  /**
   * Initialize rule query
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

  /**
   * Structure:
   * 1、cluster -> DB
   * 2、DB->TABLE
   * 3、All TABLE
   * 4、cluster ->TABLE
   * 5、All DB
   * 6、All cluster
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

  private List<RuleQueryProject> getProjectsByUserPerm(DataSourceQo param,
      List<ProjectUser> projectUsers, Map<Long, RuleQueryProject> projectMap) {
    if (projectUsers == null || projectUsers.isEmpty()) {
      return null;
    }
    for (ProjectUser projectUser : projectUsers) {
      List<RuleDataSource> projectDSs = ruleDataSourceDao.findByProjectUser(
          projectUser.getProject().getId(), param.getCluster(), param.getDb(), param.getTable());
      if (projectDSs == null || projectDSs.isEmpty()) {
        continue;
      }
      addRuleDataSource(projectDSs, projectMap, projectUser.getProject());
    }
    return new ArrayList<>(projectMap.values());
  }

  /**
   *
   * Add the list of datasource into response in hierarchical relationship.
   * @param projectDSs
   * @param projectMap
   * @param project
   */
  private void addRuleDataSource(List<RuleDataSource> projectDSs,
      Map<Long, RuleQueryProject> projectMap, Project project) {
    for (RuleDataSource ds : projectDSs) {
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
      putIntoDBOfCluster(ds, queryCluster);
      return;
    }
    // If not contain
    RuleQueryCluster cluster = newRuleQueryCluster(ds);
    queryRule.addRuleQueryCluster(cluster);
  }

  private void putIntoDBOfCluster(RuleDataSource ds, RuleQueryCluster queryCluster) {
    String dbKey = String.format("%s.%s", ds.getClusterName(), ds.getDbName());
    if (queryCluster.getDataMap().containsKey(dbKey)) {
      RuleQueryDb queryDB = queryCluster.getDataMap().get(dbKey);
      putIntoTableOfDB(ds, queryDB);
      return;
    }
    // If not contain
    RuleQueryDb db = newRuleQueryDB(ds);
    queryCluster.addRuleQueryDB(db);
  }

  private void putIntoTableOfDB(RuleDataSource ds, RuleQueryDb queryDB) {
    String tableKey = String.format("%s.%s.%s", ds.getClusterName(), ds.getDbName(),
                                    ds.getTableName());
    if (queryDB.getDataMap().containsKey(tableKey)) {
      RuleQueryTable queryTable = queryDB.getDataMap().get(tableKey);
      RuleQueryCol col = new RuleQueryCol(ds);
      queryTable.addRuleQueryCol(col);
      return;
    }
    // If not contain
    RuleQueryTable table = newRuleQueryTable(ds);
    queryDB.addRuleQueryTable(table);
  }

  private RuleQueryTable newRuleQueryTable(RuleDataSource ds) {
    RuleQueryCol col = new RuleQueryCol(ds);
    return new RuleQueryTable(col);
  }

  private RuleQueryDb newRuleQueryDB(RuleDataSource ds) {
    return new RuleQueryDb(newRuleQueryTable(ds));
  }

  private RuleQueryCluster newRuleQueryCluster(RuleDataSource ds) {
    return new RuleQueryCluster(newRuleQueryDB(ds));
  }

  private RuleQueryRule newRuleQueryRule(Rule rule, RuleDataSource ds) {
    return new RuleQueryRule(newRuleQueryCluster(ds), rule);
  }

  private RuleQueryProject newRuleQueryProject(Project project, Rule rule, RuleDataSource ds) {
    return new RuleQueryProject(newRuleQueryRule(rule, ds), project);
  }

  private Map<String, Object> getConditionsByDs(List<Map<String, String>> projectDs) {
    // result
    Map<String, Object> results = new HashMap<>(6);
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
    return results;
  }

  private void putIntoTotalData(Map<String, String> ds, Set<String> clusters, Set<String> dbs,
      Set<String> tables) {
    String clusterName = ds.get("cluster_name");
    String dbName = ds.get("db_name");
    String tableName = ds.get("table_name");

    clusters.add(clusterName);
    dbs.add(dbName);
    tables.add(tableName);
  }

  private void putIntoCascadeData(Map<String, String> ds, Map<String, Set<String>> clusterDbs,
      Map<String, Set<String>> dbTables, Map<String, Set<String>> clusterTables) {
    String clusterName = ds.get("cluster_name");
    String dbName = ds.get("db_name");
    String tableName = ds.get("table_name");
    Set<String> clusterDbsSet = clusterDbs.computeIfAbsent(clusterName, k -> new HashSet<>());
    clusterDbsSet.add(dbName);

    Set<String> dbTablesSet = dbTables.computeIfAbsent(dbName, k -> new HashSet<>());
    dbTablesSet.add(tableName);

    Set<String> clusterTablesSet = clusterTables.computeIfAbsent(clusterName, k -> new HashSet<>());
    clusterTablesSet.add(tableName);
  }
}
