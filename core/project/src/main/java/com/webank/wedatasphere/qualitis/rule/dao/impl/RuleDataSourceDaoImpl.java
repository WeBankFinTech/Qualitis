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

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.Predicate;

/**
 * @author howeye
 */
@Repository
public class RuleDataSourceDaoImpl implements RuleDataSourceDao {

    @Autowired
    private RuleDataSourceRepository ruleDataSourceRepository;

    @Override
    public List<RuleDataSource> saveAllRuleDataSource(List<RuleDataSource> ruleDataSources) {
        return ruleDataSourceRepository.saveAll(ruleDataSources);
    }

    @Override
    public List<RuleDataSource> findByRule(Rule rule) {
        return ruleDataSourceRepository.findByRule(rule);
    }

    @Override
    public List<RuleDataSource> findByProjectId(Long projectId) {
        return ruleDataSourceRepository.findByProjectId(projectId);
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
    public List<Map<String, String>> findProjectDsByUser(String user) {
        return ruleDataSourceRepository.findProjectDsByUser(user);
    }

    @Override
    public List<Map<String, String>> findProjectDsByUser(String user, int page, int size) {
        Sort sort = new Sort(Sort.Direction.ASC, "clusterName", "dbName", "tableName");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleDataSourceRepository.findProjectDsByUser(user, pageable).getContent();
    }


    @Override
    public List<Rule> findRuleByDataSource(String clusterName, String dbName, String tableName, String colName, String user) {
        return ruleDataSourceRepository.findRuleByDataSource(clusterName, dbName, tableName, colName, user);
    }

    @Override
    public List<Map<String, String>> filterProjectDsByUser(String user, String clusterName, String dbName, String tableName) {
        return ruleDataSourceRepository.filterProjectDsByUser(user, clusterName, dbName, tableName);
    }

    @Override
    public List<Map<String, String>> filterProjectDsByUserPage(String user, String clusterName, String dbName, String tableName, int page, int size) {
        Sort sort = new Sort(Sort.Direction.ASC, "clusterName", "dbName", "tableName");
        Pageable pageable = PageRequest.of(page, size, sort);
        return ruleDataSourceRepository.filterProjectDsByUser(user, clusterName, dbName, tableName, pageable).getContent();
    }

}
