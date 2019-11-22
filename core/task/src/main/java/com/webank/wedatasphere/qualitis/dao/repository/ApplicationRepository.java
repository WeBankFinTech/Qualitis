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

package com.webank.wedatasphere.qualitis.dao.repository;

import com.webank.wedatasphere.qualitis.entity.Application;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author howeye
 */
public interface ApplicationRepository extends JpaRepository<Application, String>, JpaSpecificationExecutor<Application> {

    /**
     * Paging find application by creator
     * @param createUser
     * @param pageable
     * @return
     */
    List<Application> findByCreateUser(String createUser, Pageable pageable);

    /**
     * Count application by creator
     * @param createUser
     * @return
     */
    Long countByCreateUser(String createUser);

    /**
     * Paging find application by creator and status
     * @param createUser
     * @param pageable
     * @param status
     * @return
     */
    List<Application> findByCreateUserAndStatus(String createUser, Integer status, Pageable pageable);

    /**
     * Count by creator and status
     * @param createUser
     * @param status
     * @return
     */
    Long countByCreateUserAndStatus(String createUser, Integer status);

    /**
     * Find application by creator and time between
     * @param createUser
     * @param startSubmitDate
     * @param endSubmitDate
     * @return
     */
    @Query("select app from Application app where (app.createUser = ?1) and (app.submitTime between ?2 and ?3)")
    List<Application> findApplicationByUserAndSubmitTimeBetween(String createUser, String startSubmitDate, String endSubmitDate);

    /**
     * Paging find application by creator and time between
     * @param user
     * @param startSubmitDate
     * @param endSubmitDate
     * @param pageable
     * @return
     */
    @Query("select app from Application app where (app.createUser = ?1) and (app.submitTime between ?2 and ?3)")
    List<Application> findApplicationByUserAndSubmitTimeBetweenPage(String user, String startSubmitDate, String endSubmitDate, Pageable pageable);

    /**
     * Find application by status not in
     * @param statusList
     * @return
     */
    List<Application> findByStatusNotIn(List<Integer> statusList);


    /**
     * Find application by creator and id like
     * @param createUser
     * @param applicationId
     * @return
     */
    List<Application> findByCreateUserAndIdLike(String createUser, String applicationId);
}
