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

package com.webank.wedatasphere.qualitis.rule.dao;

import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;

import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public interface RuleDataSourceDao {

    /**
     * Save all rule datasource
     * @param ruleDataSources
     * @return
     */
    List<RuleDataSource> saveAllRuleDataSource(List<RuleDataSource> ruleDataSources);

    /**
     * Find rule datasource by rule
     * @param rule
     * @return
     */
    List<RuleDataSource> findByRule(Rule rule);

    /**
     * Find rule datasource by project id
     * @param projectId
     * @return
     */
   List<RuleDataSource> findByProjectId(Long projectId);


    /**
     * Find rule datasoruce by project id and datasource(cluster, db, table)
     * @param projectId
     * @param cluster
     * @param db
     * @param table
     * @return
     */
    List<RuleDataSource> findByProjectUser(Long projectId, String cluster, String db, String table);

    /**
     * Find project datasource by user
     * @param user
     * @return
     */
    List<Map<String, Object>> findProjectDsByUser(String user);

    /**
     * Paging query rule datasource
     * @param user
     * @param page
     * @param size
     * @return
     */
    List<Map<String, Object>> findProjectDsByUser(String user, int page, int size);

    /**
     * Find rules related with cluster name, database name ,table name, column name.
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param colName
     * @param user
     * @return
     */
    List<Rule> findRuleByDataSource(String clusterName, String dbName, String tableName, String colName, String user);

    /**
     * Paging rules related with cluster name, database name ,table name, column name.
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param colName
     * @param user
     * @param page
     * @param size
     * @return
     */
    List<Rule> findRuleByDataSource(String clusterName, String dbName, String tableName, String colName, String user, int page, int size);

    /**
     * Count.
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param colName
     * @param user
     * @return
     */
    int countRuleByDataSource(String clusterName, String dbName, String tableName, String colName, String user);

    /**
     * Filter rule datasource
     * @param user
     * @param clusterName
     * @param dbName
     * @param tableName
     * @return
     */
    List<Map<String, Object>> filterProjectDsByUser(String user, String clusterName, String dbName, String tableName);

    /**
     * Filter rule datasource pageable.
     * @param user
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param page
     * @param size
     * @return
     */
    List<Map<String, Object>> filterProjectDsByUserPage(String user, String clusterName, String dbName, String tableName, int page, int size);

    /**
     * Save rule datasource
     * @param newRuleDataSource
     * @return
     */
    RuleDataSource saveRuleDataSource(RuleDataSource newRuleDataSource);

    /**
     * Find cols' name.
     * @param user
     * @param clusterName
     * @param dbName
     * @param tableName
     * @return
     */
    List<String> findColsByUser(String user, String clusterName, String dbName, String tableName);

    /**
     * Find all datasources by user.
     * @param user
     * @param clusterName
     * @param dbName
     * @param tableName
     * @return
     */
    List<RuleDataSource> findDatasourcesByUser(String user, String clusterName, String dbName, String tableName);
}
