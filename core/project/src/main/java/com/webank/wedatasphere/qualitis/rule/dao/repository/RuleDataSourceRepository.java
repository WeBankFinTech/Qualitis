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
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
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
     * Delete by rule group.
     * @param ruleGroup
     */
    void deleteByRuleGroup(RuleGroup ruleGroup);

    /**
     * Find rule datasource by project id for add multi db rules.
     * @param projectId
     * @return
     */
    List<RuleDataSource> findByProjectId(Long projectId);

    /**
     * delete By Rule In
     * @param ruleList
     */
    void deleteByRuleIn(List<Rule> ruleList);

    /**
     * delete By Rule Group In
     * @param ruleGroupList
     */
    void deleteByRuleGroupIn(List<RuleGroup> ruleGroupList);

    /**
     * Find only cols' name.
     * @param user
     * @param clusterName
     * @param dbName
     * @param tableName
     * @return
     */
    @Query(value = "select ds.colName from RuleDataSource ds, ProjectUser u where ds.projectId = u.project and u.userName = ?1 and ds.clusterName = ?2 and ds.dbName = ?3 and ds.tableName = ?4")
    List<String> findColsByUser(String user, String clusterName, String dbName, String tableName);

    /**
     * Find rule datasource by user for condition.
     * @param user
     * @return
     */
    @Query(value = "select new map(ds.clusterName as cluster_name, ds.datasourceType as datasource_type, ds.linkisDataSourceId as datasource_id, ds.dbName as db_name, ds.tableName as table_name, ds.proxyUser as proxy_user) from RuleDataSource ds where exists (select pu.id from ProjectUser pu where pu.project.id = ds.projectId and pu.userName = ?1)")
    List<Map<String, Object>> findProjectDsByUser(String user);

    /**
     * Paging query rule datasource.
     * @param user
     * @param pageable
     * @return
     */
    @Query(value = "select new map(ds.clusterName as cluster_name, ds.datasourceType as datasource_type, ds.linkisDataSourceId as datasource_id, ds.dbName as db_name, ds.tableName as table_name, ds.proxyUser as proxy_user) from RuleDataSource ds where exists (select pu.id from ProjectUser pu where pu.project.id = ds.projectId and pu.userName = ?1)")
    Page<Map<String, Object>> findProjectDsByUserPage(String user, Pageable pageable);

    /**
     * Paging column
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param colName
     * @param user
     * @param pageable
     * @return
     */
    @Query(value = "select DISTINCT ds.rule from RuleDataSource ds, ProjectUser u where ds.clusterName = ?1 and ds.dbName = ?2 and ds.tableName = ?3 and ds.colName like ?4 and ds.projectId = u.project and u.userName = ?5")
    List<Rule> findColumnByDataSource(String clusterName, String dbName, String tableName, String colName, String user, Pageable pageable);

    /**
     * count of rule_datasource on table
     * @param clusterNames
     * @param dbNames
     * @param tableNames
     * @param user
     * @return
     */
    @Query(value = "select new map(ds.clusterName as clusterName, ds.dbName as dbName, ds.tableName as tableName, count(DISTINCT ds.rule) as ruleCount) from RuleDataSource ds, ProjectUser u " +
            "where ds.projectId = u.project and ds.clusterName in (?1) and ds.dbName in (?2) and ds.tableName in (?3) and u.userName = ?4 " +
            "group by ds.clusterName, ds.dbName, ds.tableName")
    List<Map<String, Object>> countRuleCountByGroup(List<String> clusterNames, List<String> dbNames, List<String> tableNames, String user);

    /**
     * count of rule_datasource on field
     * @param clusterNames
     * @param dbNames
     * @param tableNames
     * @param fieldNames
     * @param users
     * @return
     */
    @Query(value = "select new map(ds.clusterName as clusterName, ds.dbName as dbName, ds.tableName as tableName, ds.colName as colName, count(DISTINCT ds.rule) as ruleCount) from RuleDataSource ds, ProjectUser u " +
            "where ds.projectId = u.project and ds.clusterName in (?1) and ds.dbName in (?2) and ds.tableName in (?3) and ds.colName in (?4) and u.userName in (?5) " +
            "group by ds.clusterName, ds.dbName, ds.tableName, ds.colName")
    List<Map<String, Object>> countRuleCountByGroup(List<String> clusterNames, List<String> dbNames, List<String> tableNames, List<String> fieldNames, List<String> users);

    /**
     * Filter rule datasource pageable.
     * countQuery: replace default count sql.
     * @param user
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param datasourceType
     * @param subSystemId
     * @param departmentName
     * @param devDepartmentName
     * @param tagCode
     * @param envName
     * @param pageable
     * @return
     */
    @Query(value = "select new map(ds.clusterName as cluster_name, ds.datasourceType as datasource_type, ds.linkisDataSourceId as links_datasource_id, ds.dbName as db_name, ds.tableName as table_name, ds.subSystemName as sub_system_name, ds.departmentName as department_name, ds.devDepartmentName as dev_department_name, ds.tagName as tag_name, coalesce(dsv.envName, '') as env_name) from RuleDataSource ds left join RuleDataSourceEnv dsv on ds = dsv.ruleDataSource where" +
            " EXISTS (select u.id from ProjectUser u where u.userName = ?1 and ds.projectId = u.project ) " +
            " and (?2 is null or ds.clusterName = ?2) and (?3 is null or ds.dbName = ?3) and (?4 is null or ds.tableName = ?4) and (?5 is null or ds.datasourceType = ?5)" +
            " and (?6 is null or ds.subSystemId = ?6) and (?7 is null or ds.departmentName like ?7) and (?8 is null or ds.devDepartmentName like ?8) and (?9 is null or ds.tagCode = ?9) " +
            " and (?10 is null or dsv.envName = ?10) group by ds.clusterName, ds.dbName, ds.tableName, dsv.envName"
            )
    List<Map<String, Object>> filterProjectDsByUserPage(String user, String clusterName, String dbName, String tableName,
        Integer datasourceType, Long subSystemId, String departmentName, String devDepartmentName, String tagCode, String envName, Pageable pageable);

    /**
     * find tags by login user
     * @param user
     * @return
     */
    @Query(value = "select ds from RuleDataSource ds where " +
            " EXISTS (select u.id from ProjectUser u where u.userName = ?1 and ds.projectId = u.project ) " +
            " AND tagCode is not null " +
            " AND tagName is not null" +
            " GROUP BY tagCode, tagName")
    List<RuleDataSource> findTagsByUser(String user);

    /**
     * Count rule datasource.
     * @param user
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param datasourceType
     * @param subSystemId
     * @param departmentName
     * @param devDepartmentName
     * @param tagCode
     * @param envName
     * @return
     */
    @Query(value = "select count(*) from (select 1 from qualitis_rule_datasource ds " +
            " left join qualitis_rule_datasource_env dsv on ds.id = dsv.rule_data_source_id where " +
            " EXISTS (select id from qualitis_project_user u where user_name = ?1 and u.project_id = ds.project_id )" +
            " and (?2 is null or ds.cluster_name = ?2) and (?3 is null or ds.db_name = ?3) and (?4 is null or ds.table_name = ?4) and (?5 is null or ds.datasource_type = ?5)" +
            " and (?6 is null or ds.sub_system_id = ?6) and (?7 is null or ds.department_name like ?7) and (?8 is null or ds.dev_department_name like ?8) and (?9 is null or ds.tag_code = ?9) and (?10 is null or dsv.linkis_env_name = ?10) group by ds.cluster_name, ds.db_name, ds.table_name, dsv.linkis_env_name" +
            " order by null) as a"
            , nativeQuery = true)
    long countProjectDsByUser(String user, String clusterName, String dbName, String tableName, Integer datasourceType, Long subSystemId, String departmentName, String devDepartmentName, String tagCode, String envName);

    /**
     * Find all datasources by user.
     * @param user
     * @param clusterName
     * @param dbName
     * @param tableName
     * @return
     */
    @Query(value = "select ds from RuleDataSource ds, ProjectUser u where ds.projectId = u.project.id and u.userName = ?1 and ds.clusterName = ?2 and ds.dbName = ?3 and ds.tableName = ?4")
    List<RuleDataSource> findDatasourcesByUser(String user, String clusterName, String dbName, String tableName);

    /**
     * Find rule create users for rule count.
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param userName
     * @return
     */
    @Query(value = "select DISTINCT ds.rule.createUser from RuleDataSource ds, ProjectUser u where ds.clusterName = ?1 and ds.dbName = ?2 and ds.tableName = ?3 and ds.projectId = u.project and u.userName = ?4")
    List<String> findRuleCreateUserByDataSource(String clusterName, String dbName, String tableName, String userName);

    /**
     * find By Rule Id
     * @param ruleIds
     * @return
     */
    @Query(value = "select * from qualitis_rule_datasource where rule_id in (?1)",nativeQuery = true)
    List<RuleDataSource> findByRuleId(List<Long> ruleIds);

    /**
     * find rule group by data source
     * @param projectId
     * @param dbName
     * @param tableName
     * @return
     */
    @Query(value = "select DISTINCT r.rule_group_id from qualitis_rule r " +
            "inner join qualitis_rule_datasource ds on r.id = ds.rule_id " +
            "where ds.project_id = ?1" +
            " and (?2 is null or ds.db_name = ?2) " +
            " and (?3 is null or ds.table_name = ?3)", nativeQuery = true)
    List<BigInteger> findRuleGroupIds(Long projectId, String dbName, String tableName);

    /**
     * update linkisDataSourceName
     * @param linkisDataSourceId
     * @param linkisDataSourceName
     */
    @Modifying
    @Query("update RuleDataSource set linkisDataSourceName=?2 where linkisDataSourceId = ?1")
    void updateLinkisDataSourceName(Long linkisDataSourceId, String linkisDataSourceName);

    /**
     * Delete bu IDs
     * @param ids
     */
    @Modifying
    @Query(value = "delete from qualitis_rule_datasource where id in (?1)", nativeQuery = true)
    void deleteByIdIn(List<Long> ids);

    /**
     * Update metadata fields
     * @param id
     * @param subSystemId
     * @param subSystemName
     * @param departmentCode
     * @param departmentName
     * @param devDepartmentName
     * @param tagCode
     * @param tagName
     */
    @Modifying
    @Query(value = "update qualitis_rule_datasource set sub_system_id=?2, sub_system_name=?3, department_code=?4, department_name=?5, dev_department_name=?6" +
            ", tag_code=?7, tag_name=?8 where id = ?1", nativeQuery = true)
    void updateMetadataFields(Long id, Long subSystemId, String subSystemName, String departmentCode, String departmentName, String devDepartmentName, String tagCode, String tagName);
}
