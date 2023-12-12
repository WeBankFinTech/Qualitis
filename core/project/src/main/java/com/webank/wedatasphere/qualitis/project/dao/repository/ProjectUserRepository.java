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

package com.webank.wedatasphere.qualitis.project.dao.repository;

import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectUser;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author howeye
 */
public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long>, JpaSpecificationExecutor<ProjectUser> {

    /**
     * Paging find project user by userId
     * @param userName
     * @param permission
     * @param projectType
     * @param pageable
     * @return
     */
    @Query("select pu from ProjectUser pu inner join pu.project p where pu.userName = ?1 and pu.permission = ?2 and p.projectType = ?3")
    Page<ProjectUser> findByUsernameAndPermissionAndProjectType(String userName, Integer permission, Integer projectType, Pageable pageable);

    /**
     * Paging find project user by userId
     * @param userName
     * @param projectType
     * @param pageable
     * @return
     */
    @Query("select pu from ProjectUser pu inner join Project p on pu.project = p where pu.userName = ?1 and p.projectType = ?2 group by pu.project")
    Page<ProjectUser> findByUserNameAndProjectType(String userName, Integer projectType, Pageable pageable);

    /**
     * Paging find project user by userId
     * @param userName
     * @param projectType
     * @return
     */
    @Query("select pu from ProjectUser pu inner join Project p on pu.project = p where pu.userName = ?1 and p.projectType = ?2 group by pu.project")
    List<ProjectUser> findByUserNameAndProjectTypeWithOutPage(String userName, Integer projectType);

    /**
     * find By Advance Conditions
     * @param username
     * @param projectType
     * @param projectName
     * @param subSystemName
     * @param createUser
     * @param db
     * @param table
     * @param startTime
     * @param endTime
     * @param pageable
     * @return
     */
    @Query(value = "select pu from Project p inner join ProjectUser pu on pu.project = p left join RuleDataSource ds on ds.projectId = p.id " +
            "where pu.userName = ?1 and p.projectType = ?2 and p.name like ?3 and (?4 is null or p.subSystemName like ?4) " +
            "and (?5 is null or p.createUser = ?5) and (?6 is null or ds.dbName = ?6) and (?7 is null or ds.tableName = ?7) " +
            "and (?8 is null or UNIX_TIMESTAMP(p.createTime) >= ?8) and (?9 is null or UNIX_TIMESTAMP(p.createTime) < ?9) group by pu.project"
        , countQuery = "select count(DISTINCT pu.project) from ProjectUser pu inner join Project p on pu.project = p left join RuleDataSource ds on ds.projectId = p.id " +
            "where pu.userName = ?1 and p.projectType = ?2 and p.name like ?3 and (?4 is null or p.subSystemId = ?4) " +
            "and (?5 is null or p.createUser = ?5) and (?6 is null or ds.dbName = ?6) and (?7 is null or ds.tableName = ?7) " +
            "and (?8 is null or UNIX_TIMESTAMP(p.createTime) >= ?8) and (?9 is null or UNIX_TIMESTAMP(p.createTime) < ?9)")
    Page<ProjectUser> findByAdvanceConditions(String username, Integer projectType, String projectName, String subSystemName, String createUser, String db, String table, Long startTime, Long endTime, Pageable pageable);

    /**
     * Count by user name and permission and project type
     * @param userName
     * @param permission
     * @param projectType
     * @return
     */
    @Query("select count(pu) from ProjectUser pu inner join pu.project p where pu.userName = ?1 and pu.permission = ?2 and p.projectType = ?3")
    Long countByUserNameAndPermissionAndProjectType(String userName, Integer permission, Integer projectType);

    /**
     * Count by user name and project type
     * @param userName
     * @param projectType
     * @return
     */
    @Query("select count(DISTINCT pu.project) from ProjectUser pu inner join Project p on pu.project = p where pu.userName = ?1 and p.projectType = ?2")
    Long countByUserNameAndProjectType(String userName, Integer projectType);

    /**
     * Find project user by userId
     * @param userName
     * @param permission
     * @return
     */
    @Query("select DISTINCT pu.project from ProjectUser pu inner join pu.project p where pu.userName = ?1 and pu.permission = ?2")
    List<Project> findByUserNameAndPermission(String userName, Integer permission);

    /**
     * Count by username and permissions
     * @param userName
     * @param permissions
     * @return
     */
    @Query("select count(DISTINCT pu.project) from ProjectUser pu inner join pu.project p where pu.userName = ?1 and pu.permission in (?2)")
    Long countByUserNameAndPermission(String userName, List<Integer> permissions);

    /**
     * Find project user by user name
     * @param userName
     * @return
     */
    @Query("select DISTINCT new map(p.name as project_name, p.id as project_id) from ProjectUser pu inner join pu.project p where pu.userName = ?1")
    List<Map<String, Object>> findProjectByUserName(String userName);

    /**
     * Count by username
     * @param userName
     * @return
     */
    @Query("select count(DISTINCT pu.project) from ProjectUser pu inner join pu.project p where pu.userName = ?1")
    Long countProjectByUserName(String userName);

    /**
     * Find project user by userId
     * @param userName
     * @param permissions
     * @return
     */
    @Query("select DISTINCT pu.project from ProjectUser pu inner join pu.project p where pu.userName = ?1 and pu.permission in (?2)")
    List<Project> findByUserNameAndPermissions(String userName, List<Integer> permissions);

    /**
     * Delete project user by project
     * @param project
     */
    void deleteByProject(Project project);

    /**
     * Delete project user by project and user name
     *
     * @param projectId
     * @param userName
     */
    @Modifying
    @Query(value = "delete from qualitis_project_user where project_id = ?1 and user_name = ?2", nativeQuery = true)
    void deleteByProjectAndUserName(Long projectId, String userName);

    /**
     * Find project user by project
     * @param project
     * @return
     */
    List<ProjectUser> findByProject(Project project);

    /**
     * Find project user by username
     * @param userName
     * @return
     */
    List<ProjectUser> findByUserName(String userName);

    /**
     * Find project user by project with page.
     * @param project
     * @param pageable
     * @return
     */
    Page<ProjectUser> findByProject(Project project, Pageable pageable);

    /**
     * find User Name And Automatic
     *
     * @param userName
     * @param flag
     * @return
     */
    @Query("select pu from ProjectUser pu where pu.userName = ?1 and pu.automaticSwitch = ?2")
    List<ProjectUser> findUserNameAndAutomatic(String userName, Boolean flag);
}
