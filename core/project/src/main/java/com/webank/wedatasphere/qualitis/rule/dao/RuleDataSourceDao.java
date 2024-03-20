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
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public interface RuleDataSourceDao {

    /**
     * Save all rule datasource.
     * @param ruleDataSources
     * @return
     */
    List<RuleDataSource> saveAllRuleDataSource(List<RuleDataSource> ruleDataSources);

    /**
     * Find rule datasource by project id for add multi db rules.
     * @param projectId
     * @return
     */
   List<RuleDataSource> findByProjectId(Long projectId);

    /**
     * Find only cols' name.
     * @param user
     * @param clusterName
     * @param dbName
     * @param tableName
     * @return
     */
    List<String> findColsByUser(String user, String clusterName, String dbName, String tableName);

    /**
     * Find rule datasoruce by project id and datasource(cluster, db, table).
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
    List<Map<String, Object>> findProjectDsByUserPage(String user, int page, int size);

 /**
  * find Column By DataSource
  * @param clusterName
  * @param dbName
  * @param tableName
  * @param colName
  * @param user
  * @param page
  * @param size
  * @return
  */
    List<Rule> findColumnByDataSource(String clusterName, String dbName, String tableName, String colName, String user, int page, int size);

    /**
     * count Rule Count By Group
     * @param clusterName
     * @param dbName
     * @param tableNames
     * @param user
     * @return
     */
    List<Map<String, Object>> countRuleCountByGroup(List<String> clusterName, List<String> dbName, List<String> tableNames, String user);

    /**
     * count Rule Count By Group
     * @param clusterName
     * @param dbName
     * @param tableNames
     * @param fieldNames
     * @param users
     * @return
     */
    List<Map<String, Object>> countRuleCountByGroup(List<String> clusterName, List<String> dbName, List<String> tableNames, List<String> fieldNames, List<String> users);

    /**
     * Count rule datasource.
     * @param user
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param datasourceType
     * @param subSystemId
     * @param departmentCode
     * @param devDepartmentName
     * @param tagCode
     * @param envName
     * @return
     */
    long countProjectDsByUser(String user, String clusterName, String dbName, String tableName, Integer datasourceType, Long subSystemId, String departmentCode, String devDepartmentName, String tagCode, String envName);

    /**
     * Filter rule datasource pageable.
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
     * @param page
     * @param size
     * @return
     */
    List<Map<String, Object>> filterProjectDsByUserPage(String user, String clusterName, String dbName, String tableName,
        Integer datasourceType, Long subSystemId, String departmentName, String devDepartmentName, String tagCode, String envName, int page, int size);

    /**
     * Save rule datasource
     * @param newRuleDataSource
     * @return
     */
    RuleDataSource saveRuleDataSource(RuleDataSource newRuleDataSource);

    /**
     * Find all datasources by user for datasource execution.
     * @param user
     * @param clusterName
     * @param dbName
     * @param tableName
     * @return
     */
    List<RuleDataSource> findDatasourcesByUser(String user, String clusterName, String dbName, String tableName);

    /**
     * Find rule create users for rule count.
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param userName
     * @return
     */
    List<String> findRuleCreateUserByDataSource(String clusterName, String dbName, String tableName, String userName);

    /**
     * Paging datasources
     * @param page
     * @param size
     * @return
     */
    Page<RuleDataSource> findAllWithPage(int page, int size);

    /**
     * find all tagCode and tagName in Table
     * @param loginUser
     * @return
     */
    List<RuleDataSource> findAllTagByUser(String loginUser);

    /**
     * find By Rule Id
     * @param ruleIds
     * @return
     */
    List<RuleDataSource> findByRuleId(List<Long> ruleIds);

    /**
     * findRuleGroupIds
     * @param projectId
     * @param dbName
     * @param tableName
     * @return
     */
    List<Long> findRuleGroupIds(Long projectId, String dbName, String tableName);

    /**
     * update linkidDataSourceName
     * @param linkisDataSourceId
     * @param linkisDataSourceName
     */
    void updateLinkisDataSourceName(Long linkisDataSourceId, String linkisDataSourceName);

    /**
     * delete by rule
     * @param rule
     */
    void deleteByRule(Rule rule);

    /**
     * delete by group
     * @param ruleGroup
     */
    void deleteByRuleGroup(RuleGroup ruleGroup);

    /**
     * delete By Rule List
     * @param rules
     */
    void deleteByRuleList(List<Rule> rules);

    /**
     * delete By Rule Group List
     * @param ruleGroups
     */
    void deleteByRuleGroupList(List<RuleGroup> ruleGroups);

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
    void updateMetadataFields(Long id, Long subSystemId, String subSystemName, String departmentCode, String departmentName, String devDepartmentName, String tagCode, String tagName);
}
