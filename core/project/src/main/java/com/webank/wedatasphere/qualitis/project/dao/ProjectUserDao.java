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
import com.webank.wedatasphere.qualitis.project.queryqo.DataSourceQo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

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
     * Paging find project user by all permission.
     * @param username
     * @param projectType
     * @param page
     * @param size
     * @return
     */
    List<ProjectUser> findByUserNameAndProjectType(String username, Integer projectType, int page, int size);

    /**
     * Paging find project user by all permission.
     * @param username
     * @param projectType
     * @return
     */
    List<ProjectUser> findByUserNameAndProjectTypeWithOutPage(String username, Integer projectType);

    /**
     * pagin find project user by advance conditions
     * @param username
     * @param projectType
     * @param projectName
     * @param subsystemId
     * @param createUser
     * @param db
     * @param table
     * @param startTime
     * @param endTime
     * @param page
     * @param size
     * @return
     */
    Page<ProjectUser> findByAdvanceConditions(String username, Integer projectType, String projectName, Integer subsystemId, String createUser, String db, String table, Long startTime, Long endTime, int page, int size);

    /**
     * Find project by user and permissions
     * @param username
     * @param permissions
     * @return
     */
    List<Project> findByUsernameAndPermissions(String username, List<Integer> permissions);

    /**
     * Find project by user
     * @param username
     * @param projectType
     * @return
     */
    List<Map<String, Object>> findProjectByUserName(String username,Integer projectType);

    /**
     * Count project user by userId with project type
     * @param username
     * @param permission
     * @param projectType
     * @return
     */
    Long countByUsernameAndPermissionAndProjectType(String username, Integer permission, Integer projectType);

    /**
     * Count project user by userId with all permissions.
     * @param username
     * @param projectType
     * @return
     */
    Long countByUserNameAndProjectType(String username, Integer projectType);

    /**
     * Count project user by user name
     * @param username
     * @param projectType
     * @return
     */
    Long countProjectByUserName(String username,Integer projectType);

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

    /**
     * Delete project user by project and user name
     * @param project
     * @param userName
     */
    void deleteByProjectAndUserName(Project project, String userName);

    /**
     * find User Name And Automatic
     *
     * @param userName
     * @param flag
     * @return
     */
    List<ProjectUser> findUserNameAndAutomatic(String userName, Boolean flag);

    /**
     * find User Name And Automatic
     *
     * @param userName
     * @return
     */
    List<ProjectUser> findByUserName(String userName);

    /**
     * Delete all
     * @param projectUserList
     * @param countDownLatch
     */
    void deleteInBatch(List<ProjectUser> projectUserList,CountDownLatch countDownLatch);

    /**
     * batchInsert
     * @param projectUserList
     * @param countDownLatch
     */
    void batchInsert(List<ProjectUser> projectUserList,CountDownLatch countDownLatch);

    /**
     * Find projectUser by ids
     * @param projectUserIds
     * @return
     */
    List<ProjectUser> findByIds(List<Long> projectUserIds);

}
