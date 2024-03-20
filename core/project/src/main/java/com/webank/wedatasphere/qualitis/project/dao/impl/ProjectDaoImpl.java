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

import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.dao.repository.ProjectRepository;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author howeye
 */
@Repository
public class ProjectDaoImpl implements ProjectDao {

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public Project findByName(String name) {
        return projectRepository.findByName(name);
    }

    @Override
    public Project findByNameAndCreateUser(String name, String createUser) {
        return projectRepository.findByNameAndCreateUser(name, createUser);
    }

    @Override
    public Project saveProject(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public List<Project> findAllProject(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page, size, sort);
        return projectRepository.findAll(pageable).getContent();
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public Long countAll() {
        return projectRepository.count();
    }

    @Override
    public Project findById(Long projectId) {
        return projectRepository.findByOwnId(projectId);
    }

    @Override
    public void deleteProject(Project project) {
        projectRepository.delete(project);
    }

    @Override
    public List<Project> findByCreateUser(String createUser) {
        return projectRepository.findByCreateUser(createUser);
    }

    @Override
    public List<Project> findAllById(List<Long> projectIds) {
        return projectRepository.findAllById(projectIds);
    }
}
