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
import com.webank.wedatasphere.qualitis.query.queryqo.DataSourceQo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
@Repository
public class ProjectUserDaoImpl implements ProjectUserDao {

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Override
    public List<ProjectUser> findByUsernameAndPermissionAndProjectType(String username, Integer permission, Integer projectType, int page, int size) {
        Sort sort = new Sort(Sort.Direction.ASC, "project");
        Pageable pageable = PageRequest.of(page, size, sort);
        return projectUserRepository.findByUserNameAndPermissionAndProjectType(username, permission, projectType, pageable).getContent();
    }

    @Override
    public List<ProjectUser> findByUsernameAndPermissionAndProjectType(String username, Integer projectType, int page, int size) {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return projectUserRepository.findByUserNameAndPermissionAndProjectType(username, projectType, pageable).getContent();
    }

    @Override
    public List<Project> findByUsernameAndPermission(String username, List<Integer> permissions) {
        return projectUserRepository.findByUserNameAndPermissions(username, permissions);
    }

    @Override
    public Long countByUsernameAndPermissionAndProjectType(String username, Integer permission, Integer projectType) {
        return projectUserRepository.countByUserNameAndPermissionAndProjectType(username, permission, projectType);
    }

    @Override
    public Long countByUsernameAndPermissionAndProjectType(String username, Integer projectType) {
        return projectUserRepository.countByUserNameAndPermissionAndProjectType(username, projectType);
    }

    @Override
    public Long countByUsernameAndPermission(String username, List<Integer> permissions) {
        return projectUserRepository.countByUserNameAndPermission(username, permissions);
    }

    @Override
    public List<ProjectUser> findByProject(Project project) {
        return projectUserRepository.findByProject(project);
    }

    @Override
    public List<ProjectUser> findByProjectPageable(Project project, int page, int size) {
        Sort sort = new Sort(Sort.Direction.ASC, "userName");
        Pageable pageable = PageRequest.of(page, size, sort);
        return projectUserRepository.findByProject(project, pageable).getContent();
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
        projectUserRepository.deleteByProjectAndUserName(project, userName);
    }
}
