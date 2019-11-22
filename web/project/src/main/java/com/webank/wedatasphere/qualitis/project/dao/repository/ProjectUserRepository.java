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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author howeye
 */
public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long>, JpaSpecificationExecutor<ProjectUser> {

    /**
     * Paging find project user by userId
     * @param username
     * @param permission
     * @param projectType
     * @param pageable
     * @return
     */
    @Query("select pu from ProjectUser pu inner join pu.project p where pu.userName = ?1 and pu.permission = ?2 and p.projectType = ?3")
    Page<ProjectUser> findByUserNameAndPermissionAndProjectType(String username, Integer permission, Integer projectType, Pageable pageable);

    /**
     * Count by username and permission
     * @param username
     * @param permission
     * @param projectType
     * @return
     */
    @Query("select count(pu) from ProjectUser pu inner join pu.project p where pu.userName = ?1 and pu.permission = ?2 and p.projectType = ?3")
    Long countByUserNameAndPermissionAndProjectType(String username, Integer permission, Integer projectType);


    /**
     * Find project user by username
     * @param userName
     * @return
     */
    List<ProjectUser> findByUserName(String userName);

    /**
     * Delete project user by project
     * @param project
     */
    void deleteByProject(Project project);

    /**
     * Find project user by project
     * @param project
     * @return
     */
    List<ProjectUser> findByProject(Project project);
}
