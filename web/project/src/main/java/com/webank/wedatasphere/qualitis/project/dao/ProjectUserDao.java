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
import com.webank.wedatasphere.qualitis.project.entity.ProjectUser;
import com.webank.wedatasphere.qualitis.query.queryqo.DataSourceQo;
import com.webank.wedatasphere.qualitis.query.queryqo.DataSourceQo;

import java.util.List;

/**
 * @author howeye
 */
public interface ProjectUserDao {

    /**
     * Paging find project user by userId
     * @param username
     * @param page
     * @param size
     * @param permission
     * @param projectType
     * @return
     */
    List<ProjectUser> findByUsernameAndPermissionAndProjectType(String username, Integer permission, Integer projectType, int page, int size);

    /**
     * Coung project user by userId
     * @param username
     * @param permission
     * @param projectType
     * @return
     */
    Long countByUsernameAndPermissionAndProjectType(String username, Integer permission, Integer projectType);

    /**
     * Find by project user by project
     * @param project
     * @return
     */
    List<ProjectUser> findByProject(Project project);

    /**
     * Find project user by username and permissions
     * @param param
     * @return
     */
    List<ProjectUser> findByUsernameAndPermissionsIn(DataSourceQo param);

    /**
     * Save all project users
     * @param projectUsers
     */
    void saveAll(Iterable<ProjectUser> projectUsers);

    /**
     * Delete project user
     * @param project
     */
    void deleteByProject(Project project);
}
