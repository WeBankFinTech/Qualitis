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

package com.webank.wedatasphere.qualitis.rule.dao.impl;

import com.webank.wedatasphere.qualitis.rule.dao.RuleDataSourceDao;
import com.webank.wedatasphere.qualitis.rule.dao.repository.RuleDataSourceRepository;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleDataSource;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Repository
public class RuleDataSourceDaoImpl implements RuleDataSourceDao {

    @Autowired
    private RuleDataSourceRepository ruleDataSourceRepository;

    @Override
    public List<RuleDataSource> findByLinkisDataSourceId(Long linkisDataSourceId) {
        return ruleDataSourceRepository.findByLinkisDataSourceId(linkisDataSourceId);
    }

    @Override
    public List<RuleDataSource> saveAllRuleDataSource(List<RuleDataSource> ruleDataSources) {
        return ruleDataSourceRepository.saveAll(ruleDataSources);
    }

    @Override
    public List<RuleDataSource> findByProjectId(Long projectId) {
        return ruleDataSourceRepository.findByProjectId(projectId);
    }

    @Override
    public List<String> findColsByUser(String user, String clusterName, String dbName, String tableName) {
        return ruleDataSourceRepository.findColsByUser(user, clusterName, dbName, tableName);
    }

    @Override
    public List<RuleDataSource> findByProjectUser(Long projectId, String cluster, String db,
                                                  String table) {
        return ruleDataSourceRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (projectId != null) {
                predicates.add(cb.equal(root.get("projectId"), projectId));
            }
            if (StringUtils.isNotBlank(cluster)) {
                predicates.add(cb.equal(root.get("clusterName"), cluster));
            }
            if (StringUtils.isNotBlank(db)) {
                predicates.add(cb.equal(root.get("dbName"), db));
            }
            if (StringUtils.isNotBlank(table)) {
                predicates.add(cb.equal(root.get("tableName"), table));
            }
            Predicate[] p = new Predicate[predicates.size()];
            query.where(cb.and(predicates.toArray(p)));

            return query.getRestriction();
        });
    }

    @Override
    public List<Map<String, Object>> findProjectDsByUser(String user) {
        return ruleDataSourceRepository.findProjectDsByUser(user);
    }

    @Override
    public List<Map<String, Object>> findProjectDsByUserPage(String user, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleDataSourceRepository.findProjectDsByUserPage(user, pageable).getContent();
    }

    @Override
    public List<Rule> findColumnByDataSource(String clusterName, String dbName, String tableName, String colName, String user, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleDataSourceRepository.findColumnByDataSource(clusterName, dbName, tableName, colName, user, pageable);
    }

    @Override
    public List<Map<String, Object>> countRuleCountByGroup(List<String> clusterName, List<String> dbName, List<String> tableNames, String user) {
        return ruleDataSourceRepository.countRuleCountByGroup(clusterName, dbName, tableNames, user);
    }

    @Override
    public List<Map<String, Object>> countRuleCountByGroup(List<String> clusterName, List<String> dbName, List<String> tableNames, List<String> fieldNames, List<String> users) {
        return ruleDataSourceRepository.countRuleCountByGroup(clusterName, dbName, tableNames, fieldNames, users);
    }

    @Override
    public List<Map<String, Object>> filterProjectDsByUserPage(String user, String clusterName, String dbName, String tableName,
                                                               Integer datasourceType, String subSystemId, String departmentName, String devDepartmentName, String tagCode, String envName, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "clusterName", "dbName", "tableName");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleDataSourceRepository.filterProjectDsByUserPage(user, clusterName, dbName, tableName, datasourceType, subSystemId, departmentName, devDepartmentName, tagCode, envName, pageable);
    }

    @Override
    public long countProjectDsByUser(String user, String clusterName, String dbName, String tableName, Integer datasourceType
            , String subSystemId, String departmentName, String devDepartmentName, String tagCode, String envName) {
        return ruleDataSourceRepository.countProjectDsByUser(user, clusterName, dbName, tableName, datasourceType, subSystemId, departmentName, devDepartmentName, tagCode, envName);
    }

    @Override
    public RuleDataSource saveRuleDataSource(RuleDataSource ruleDataSource) {
        return ruleDataSourceRepository.save(ruleDataSource);
    }

    @Override
    public List<RuleDataSource> findDatasourcesByUser(String user, String clusterName, String dbName, String tableName) {
        return ruleDataSourceRepository.findDatasourcesByUser(user, clusterName, dbName, tableName);
    }

    @Override
    public List<String> findRuleCreateUserByDataSource(String clusterName, String dbName, String tableName, String userName) {
        return ruleDataSourceRepository.findRuleCreateUserByDataSource(clusterName, dbName, tableName, userName);
    }

    @Override
    public Page<RuleDataSource> findAllWithPage(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleDataSourceRepository.findAll(pageable);
    }

    @Override
    public List<RuleDataSource> findAllTagByUser(String loginUser) {
        return ruleDataSourceRepository.findTagsByUser(loginUser);
    }

    @Override
    public List<RuleDataSource> findByRuleId(List<Long> ruleIds) {
        return ruleDataSourceRepository.findByRuleId(ruleIds);
    }

    @Override
    public List<Long> findRuleGroupIds(Long projectId, String dbName, String tableName) {
        List<BigInteger> ruleGroupIds = ruleDataSourceRepository.findRuleGroupIds(projectId, dbName, tableName);
        if (CollectionUtils.isNotEmpty(ruleGroupIds)) {
            return ruleGroupIds.stream().map(BigInteger::longValue).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateLinkisDataSourceName(Long linkisDataSourceId, String linkisDataSourceName) {
        ruleDataSourceRepository.updateLinkisDataSourceName(linkisDataSourceId, linkisDataSourceName);
    }

    @Override
    public void deleteByRule(Rule rule) {
        ruleDataSourceRepository.deleteByRule(rule);
    }

    @Override
    public void deleteByRuleGroup(RuleGroup ruleGroup) {
        ruleDataSourceRepository.deleteByRuleGroup(ruleGroup);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByRuleList(List<Rule> rules) {
        ruleDataSourceRepository.deleteByRuleIn(rules);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByRuleGroupList(List<RuleGroup> ruleGroups) {
        ruleDataSourceRepository.deleteByRuleGroupIn(ruleGroups);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateMetadataFields(Long id, String subSystemId, String subSystemName, String departmentCode, String departmentName, String devDepartmentName, String tagCode, String tagName) {
        ruleDataSourceRepository.updateMetadataFields(id, subSystemId, subSystemName, departmentCode, departmentName, devDepartmentName, tagCode, tagName);
    }

}
