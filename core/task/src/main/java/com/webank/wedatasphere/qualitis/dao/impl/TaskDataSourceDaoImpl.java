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

package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.TaskDataSourceDao;
import com.webank.wedatasphere.qualitis.dao.repository.TaskDataSourceRepository;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.entity.TaskDataSource;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
@Repository
public class TaskDataSourceDaoImpl implements TaskDataSourceDao {

    @Autowired
    private TaskDataSourceRepository taskDataSourceRepository;

    @Override
    public List<TaskDataSource> findByCreateUserAndDatasource(String createUser, String clusteName, String databaseName, String tableName, int page
        , int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return taskDataSourceRepository.findByCreateUserAndDatasource(createUser, clusteName, databaseName, tableName, pageable).getContent();
    }

    @Override
    public Long countByCreateUserAndDatasource(String username, String clusteName, String databaseName, String tableName) {
        return taskDataSourceRepository.countByCreateUserAndDatasource(username, clusteName, databaseName, tableName);
    }

    @Override
    public List<Map<String, String>> findByUser(String username) {
        return taskDataSourceRepository.findByCreateUser(username);
    }

    @Override
    public List<TaskDataSource> findByUserAndDataSource(String username, String clusterName, String databaseName, String tableName, Integer page, Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<TaskDataSource> specification = getUserAndDataSourceSpecification(username, clusterName, databaseName, tableName);
        return taskDataSourceRepository.findAll(specification, pageable).getContent();
    }

    @Override
    public long countByUserAndDataSource(String username, String clusterName, String databaseName, String tableName) {
        Specification<TaskDataSource> specification = getUserAndDataSourceSpecification(username, clusterName, databaseName, tableName);
        return taskDataSourceRepository.count(specification);
    }

    private Specification<TaskDataSource> getUserAndDataSourceSpecification(String username, String clusterName, String databaseName, String tableName) {
        return new Specification<TaskDataSource>() {
            @Override
            public Predicate toPredicate(Root<TaskDataSource> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (clusterName != null) {
                    predicates.add(criteriaBuilder.equal(root.get("clusterName"), clusterName));
                }
                if (databaseName != null) {
                    predicates.add(criteriaBuilder.equal(root.get("databaseName"), databaseName));
                }
                if (tableName != null) {
                    predicates.add(criteriaBuilder.equal(root.get("tableName"), tableName));
                }
                predicates.add(criteriaBuilder.equal(root.get("createUser"), username));

                Predicate[] p = new Predicate[predicates.size()];
                query.where(criteriaBuilder.and(predicates.toArray(p)));

                return query.getRestriction();
            }
        };
    }

    @Override
    public List<TaskDataSource> findByTaskAndRuleId(Task task, Long ruleId) {
        return taskDataSourceRepository.findByTaskAndRuleId(task, ruleId);
    }
}
