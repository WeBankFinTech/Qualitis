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

package com.webank.wedatasphere.qualitis.rule.dao.repository;

import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public interface RuleDataSourceRepository extends JpaRepository<RuleDataSource, Long>, JpaSpecificationExecutor<RuleDataSource> {

    /**
     * Delete rule datasource by rule
     * @param rule
     */
    void deleteByRule(Rule rule);

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
     * Find rule datasource by user
     * @param user
     * @return
     */
    @Query(value = "select new map(ds.clusterName as cluster_name, ds.dbName as db_name, ds.tableName as table_name) from RuleDataSource ds, ProjectUser u where ds.projectId = u.project and u.userName = ?1 group by ds.clusterName, ds.dbName, ds.tableName")
    List<Map<String, String>> findProjectDsByUser(String user);

    /**
     * Paging query rule data source
     * @param user
     * @param pageable
     * @return
     */
    @Query(value = "select new map(ds.clusterName as cluster_name, ds.dbName as db_name, ds.tableName as table_name) from RuleDataSource ds, ProjectUser u where ds.projectId = u.project and u.userName = ?1 group by ds.clusterName, ds.dbName, ds.tableName")
    Page<Map<String, String>> findProjectDsByUser(String user, Pageable pageable);

    /**
     * Find rules related with cluster name, database name ,table name, column name.
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param colName
     * @param user
     * @return
     */
    @Query(value = "select ds.rule from RuleDataSource ds, ProjectUser u where ds.clusterName = ?1 and ds.dbName = ?2 and ds.tableName = ?3 and ds.colName like ?4 and ds.projectId = u.project and u.userName = ?5")
    List<Rule> findRuleByDataSource(String clusterName, String dbName, String tableName, String colName, String user);

    /**
     * Filter rule datasource
     * @param user
     * @param clusterName
     * @param dbName
     * @param tableName
     * @return
     */
    @Query(value = "select new map(ds.clusterName as cluster_name, ds.dbName as db_name, ds.tableName as table_name) from RuleDataSource ds, ProjectUser u where ds.projectId = u.project and u.userName = ?1 and ds.clusterName like ?2 and ds.dbName like ?3 and ds.tableName like ?4 group by ds.clusterName, ds.dbName, ds.tableName")
    List<Map<String, String>> filterProjectDsByUser(String user, String clusterName, String dbName, String tableName);

    /**
     * Filter rule datasource pageable.
     * @param user
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param pageable
     * @return
     */
    @Query(value = "select new map(ds.clusterName as cluster_name, ds.dbName as db_name, ds.tableName as table_name) from RuleDataSource ds, ProjectUser u where ds.projectId = u.project and u.userName = ?1 and ds.clusterName like ?2 and ds.dbName like ?3 and ds.tableName like ?4 group by ds.clusterName, ds.dbName, ds.tableName")
    Page<Map<String, String>> filterProjectDsByUser(String user, String clusterName, String dbName, String tableName, Pageable pageable);
}
