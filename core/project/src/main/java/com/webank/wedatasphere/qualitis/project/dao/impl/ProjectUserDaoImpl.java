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

package com.webank.wedatasphere.qualitis.project.dao.impl;

import com.webank.wedatasphere.qualitis.project.dao.ProjectUserDao;
import com.webank.wedatasphere.qualitis.project.dao.repository.ProjectUserRepository;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectUser;
import com.webank.wedatasphere.qualitis.project.queryqo.DataSourceQo;
import groovy.util.logging.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author howeye
 */
@Repository
@Slf4j
public class ProjectUserDaoImpl implements ProjectUserDao {

    private static final Logger log = LoggerFactory.getLogger(ProjectUserDaoImpl.class);
    @Autowired
    private ProjectUserRepository projectUserRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<ProjectUser> findByUsernameAndPermissionAndProjectType(String username, Integer permission, Integer projectType, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "project");
        Pageable pageable = PageRequest.of(page, size, sort);
        return projectUserRepository.findByUsernameAndPermissionAndProjectType(username, permission, projectType, pageable).getContent();
    }

    @Override
    public List<ProjectUser> findByUserNameAndProjectType(String username, Integer projectType, int page, int size) {
        Sort sort = Sort.by(Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return projectUserRepository.findByUserNameAndProjectType(username, projectType, pageable).getContent();
    }

    @Override
    public List<ProjectUser> findByUserNameAndProjectTypeWithOutPage(String username, Integer projectType) {
        return projectUserRepository.findByUserNameAndProjectTypeWithOutPage(username, projectType);
    }

    @Override
    public Page<ProjectUser> findByAdvanceConditions(String username, Integer projectType, String projectName, Integer subsystemId, String createUser, String db, String table, Long startTime, Long endTime, int page, int size) {
        Sort sort = Sort.by(Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return projectUserRepository.findByAdvanceConditions(username, projectType, projectName, subsystemId, createUser, db, table, startTime, endTime, pageable);
    }

    @Override
    public List<Project> findByUsernameAndPermissions(String username, List<Integer> permissions) {
        return projectUserRepository.findByUserNameAndPermissions(username, permissions);
    }

    @Override
    public List<Map<String, Object>> findProjectByUserName(String username, Integer projectType) {
        return projectUserRepository.findProjectByUserName(username, projectType);
    }

    @Override
    public Long countByUsernameAndPermissionAndProjectType(String username, Integer permission, Integer projectType) {
        return projectUserRepository.countByUserNameAndPermissionAndProjectType(username, permission, projectType);
    }

    @Override
    public Long countByUserNameAndProjectType(String username, Integer projectType) {
        return projectUserRepository.countByUserNameAndProjectType(username, projectType);
    }

    @Override
    public Long countProjectByUserName(String username, Integer projectType) {
        return projectUserRepository.countProjectByUserName(username, projectType);
    }

    @Override
    public List<ProjectUser> findByProject(Project project) {
        return projectUserRepository.findByProject(project);
    }

    @Override
    public List<ProjectUser> findByUsernameAndPermissionsIn(DataSourceQo param){
        return projectUserRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(param.getUser())) {
                predicates.add(cb.equal(root.get("userName"), param.getUser()));
            }
            if (param.getUserType() != null && param.getUserType().length > 0) {
                predicates.add(root.get("permission").in((Object[]) param.getUserType()));
            }
            Predicate[] p = new Predicate[predicates.size()];
            query.where(cb.and(predicates.toArray(p)));

            return query.getRestriction();
        });
    }

    @Override
    public void saveAll(Iterable<ProjectUser> projectUsers) {
        projectUserRepository.saveAll(projectUsers);
    }

    @Override
    public void deleteByProject(Project project) {
        projectUserRepository.deleteByProject(project);
    }

    @Override
    public void deleteByProjectAndUserName(Project project, String userName) {
        projectUserRepository.deleteByProjectAndUserName(project.getId(), userName);
    }

    @Override
    public List<ProjectUser> findUserNameAndAutomatic(String userName, Boolean flag) {
        return projectUserRepository.findUserNameAndAutomatic(userName, flag);
    }

    @Override
    public List<ProjectUser> findByUserName(String userName) {
        return projectUserRepository.findByUserName(userName);
    }


    @Override
    @Async("asyncServiceExecutor")
    public void deleteInBatch(List<ProjectUser> projectUserList, CountDownLatch countDownLatch) {
        try{
            log.warn("start executeAsync");
            projectUserRepository.deleteAll(projectUserList);
            log.warn("end executeAsync");
        }finally {
            countDownLatch.countDown();
        }
    }

    @Override
    @Async("asyncServiceExecutor")
    public void batchInsert(List<ProjectUser> projectUserList,CountDownLatch countDownLatch) {
        try{
            log.warn("start executeAsync");
            projectUserRepository.saveAll(projectUserList);
            log.warn("end executeAsync");
        }finally {
            countDownLatch.countDown();
        }
    }

    @Override
    public List<ProjectUser> findByIds(List<Long> projectUserIds) {
        return projectUserRepository.findAllById(projectUserIds);
    }

}
