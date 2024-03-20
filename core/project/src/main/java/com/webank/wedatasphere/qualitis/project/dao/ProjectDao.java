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

package com.webank.wedatasphere.qualitis.project.dao;

import com.webank.wedatasphere.qualitis.project.entity.Project;

import java.util.List;

/**
 * @author howeye
 */
public interface ProjectDao {

    /**
     * Find project by name
     * @param name
     * @return
     */
    Project findByName(String name);

    /**
     * Find project by name and creator
     * @param name
     * @param createUser
     * @return
     */
    Project findByNameAndCreateUser(String name, String createUser);

    /**
     * Save project
     * @param project
     * @return
     */
    Project saveProject(Project project);

    /**
     * Paging get all project
     * @param page
     * @param size
     * @return
     */
    List<Project> findAllProject(int page, int size);

    /**
     * get all project
     * @return
     */
    List<Project> findAll();

    /**
     * Count all projects
     * @return
     */
    Long countAll();

    /**
     * Find project by id
     * @param projectId
     * @return
     */
    Project findById(Long projectId);

    /**
     * Delete project
     * @param project
     */
    void deleteProject(Project project);

    /**
     * Find project by creator
     * @param createUser
     * @return
     */
    List<Project> findByCreateUser(String createUser);

    /**
     * find All By Id
     * @param projectIds
     * @return
     */
    List<Project> findAllById(List<Long> projectIds);
}
