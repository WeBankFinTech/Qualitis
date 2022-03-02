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
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

/**
 * @author howeye
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {
    /**
     * Find project by id
     * @param id
     * @return
     */
    @Query("select p from Project p where p.id = ?1")
    Project findByOwnId(Long id);

    /**
     * Find project by name
     * @param name
     * @return
     */
    Project findByName(String name);

    /**
     * Find project by creator
     * @param createUser
     * @return
     */
    List<Project> findByCreateUser(String createUser);

    /**
     * Find project by name and create user
     * @param name
     * @param createUser
     * @return
     */
    Project findByNameAndCreateUser(String name, String createUser);

}
