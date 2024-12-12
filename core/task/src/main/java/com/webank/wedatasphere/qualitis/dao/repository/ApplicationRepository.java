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
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectEvent;
import org.apache.hadoop.mapreduce.v2.app.webapp.App;
import org.springframework.data.domain.Page;
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
     * Paging find application by creator and project
     * @param user
     * @param projectId
     * @param pageable
     * @return
     */
    @Query("select a from Application a where exists (select pu.id from ProjectUser pu where pu.project.id = a.projectId and pu.userName = ?1) and a.projectId = ?2")
    Page<Application> findByCreateUserAndProject(String user, Long projectId, Pageable pageable);

    /**
     * Count by creator and project
     * @param user
     * @param projectId
     * @return
     */
    @Query("select count(a.id) from Application a where exists (select pu.id from ProjectUser pu where pu.project.id = a.projectId and pu.userName = ?1) and a.projectId = ?2")
    Long countByCreateUserAndProject(String user, Long projectId);

    /**
     * Paging find application by creator
     * @param user
     * @param pageable
     * @return
     */
    @Query("select a from Application a where exists (select pu.id from ProjectUser pu where pu.project.id = a.projectId and pu.userName = ?1)")
    Page<Application> findByCreateUser(String user, Pageable pageable);

    /**
     * Count application by creator
     * @param user
     * @return
     */
    @Query("select count(a.id) from Application a where exists (select pu.id from ProjectUser pu where pu.project.id = a.projectId and pu.userName = ?1)")
    Long countByCreateUser(String user);

    /**
     * Paging find application by creator and status
     * @param user
     * @param status
     * @param commentType
     * @param pageable
     * @return
     */
    @Query("select a from Application a where exists (select pu.id from ProjectUser pu where pu.project.id = a.projectId and pu.userName = ?1) and a.status = ?2 and (?3 is null or a.applicationComment = ?3)")
    Page<Application> findByCreateUserAndStatus(String user, Integer status, Integer commentType, Pageable pageable);

    /**
     * Count by creator and status
     * @param user
     * @param status
     * @param commentType
     * @return
     */
    @Query("select count(a.id) from Application a where exists (select pu.id from ProjectUser pu where pu.project.id = a.projectId and pu.userName = ?1) and a.status = ?2 and (?3 is null or a.applicationComment = ?3)")
    Long countByCreateUserAndStatus(String user, Integer status, Integer commentType);

    /**
     * Find application by creator and time between
     * @param user
     * @param startSubmitDate
     * @param endSubmitDate
     * @return
     */
    @Query("select app from Application app where exists (select pu.id from ProjectUser pu where pu.project.id = app.projectId and pu.userName = ?1) and (app.submitTime between ?2 and ?3)")
    List<Application> findApplicationByUserAndSubmitTimeBetween(String user, String startSubmitDate, String endSubmitDate);

    /**
     * Paging find application by creator and time between
     * @param user
     * @param startSubmitDate
     * @param endSubmitDate
     * @param pageable
     * @return
     */
    @Query("select app from Application app where exists (select pu.id from ProjectUser pu where pu.project.id = app.projectId and pu.userName = ?1) and (app.submitTime between ?2 and ?3)")
    Page<Application> findApplicationByUserAndSubmitTimeBetweenPage(String user, String startSubmitDate, String endSubmitDate, Pageable pageable);

    /**
     * Paging find application by advanced conditions.
     * @param user
     * @param projectId
     * @param status
     * @param startSubmitDate
     * @param endSubmitDate
     * @param commentType
     * @param pageable
     * @return
     */
    @Query("select a from Application a where exists (select pu.id from ProjectUser pu where pu.project.id = a.projectId and pu.userName = ?1) and (?2 is null or a.projectId = ?2) and (?3 is null or ?3 = 0 or a.status = ?3) and (a.submitTime between ?4 and ?5) and (?6 is null or a.applicationComment = ?6)")
    Page<Application> findApplicationByAdavnceConditions(String user, Long projectId, Integer status, String startSubmitDate, String endSubmitDate,
        Integer commentType, Pageable pageable);

    /**
     * Count application by advanced conditions.
     * @param user
     * @param projectId
     * @param status
     * @param startSubmitDate
     * @param endSubmitDate
     * @param commentType
     * @return
     */
    @Query("select count(a.id) from Application a where exists (select pu.id from ProjectUser pu where pu.project.id = a.projectId and pu.userName = ?1) and (?2 is null or a.projectId = ?2) and (?3 is null or a.status = ?3) and (a.submitTime between ?4 and ?5) and (?6 is null or a.applicationComment = ?6)")
    long countApplicationByAdavnceConditions(String user, Long projectId, Integer status, String startSubmitDate, String endSubmitDate,
        Integer commentType);

    /**
     * Find application by status not in
     * @param statusList
     * @return
     */
    List<Application> findByStatusIn(List<Integer> statusList);

    /**
     * Find application by status not in
     * @param statusList
     * @return
     */
    List<Application> findByStatusNotIn(List<Integer> statusList);


    /**
     * Find application by creator and ID like
     * @param user
     * @param applicationId
     * @return
     */
    List<Application> findByCreateUserAndId(String user, String applicationId);

    /**
     * With datasource advance.
     * @param userName
     * @param clusterName
     * @param databaseName
     * @param tableName
     * @param projectId
     * @param status
     * @param commentType
     * @param startTime
     * @param endTime
     * @param pageable
     * @return
     */
    @Query(value = "SELECT DISTINCT qat.application FROM Task qat join TaskDataSource qatd on qat = qatd.task WHERE exists (select pu.id from ProjectUser pu where pu.project.id = qatd.projectId and pu.userName = ?1) and (LENGTH(?2) = 0 OR qatd.clusterName = ?2) and (LENGTH(?3) = 0 OR qatd.databaseName = ?3) and (LENGTH(?4) = 0 OR qatd.tableName = ?4) and (?5 IS NULL OR qatd.projectId = ?5) and (?6 IS NULL OR qat.status = ?6) and (?7 IS NULL OR qat.taskComment = ?7) and qat.beginTime > ?8 and qat.endTime < ?9")
    Page<Application> findApplicationByAdavnceConditionsWithDatasource(String userName, String clusterName, String databaseName, String tableName, Long projectId, Integer status, Integer commentType, String startTime, String endTime, Pageable pageable);

    /**
     * Count with datasource advance
     * @param userName
     * @param clusterName
     * @param databaseName
     * @param tableName
     * @param projectId
     * @param status
     * @param commentType
     * @param startTime
     * @param endTime
     * @return
     */
    @Query(value = "SELECT count(DISTINCT qat.application) FROM Task qat join TaskDataSource qatd on qat = qatd.task WHERE exists (select pu.id from ProjectUser pu where pu.project.id = qatd.projectId and pu.userName = ?1) and (LENGTH(?2) = 0 OR qatd.clusterName = ?2) and (LENGTH(?3) = 0 OR qatd.databaseName = ?3) and (LENGTH(?4) = 0 OR qatd.tableName = ?4) and (?5 IS NULL OR qatd.projectId = ?5) and (?6 IS NULL OR qat.status = ?6) and (?7 IS NULL OR qat.taskComment = ?7) and qat.beginTime > ?8 and qat.endTime < ?9")
    long countApplicationByAdavnceConditionsWithDatasource(String userName, String clusterName, String databaseName, String tableName, Long projectId, Integer status, Integer commentType, String startTime, String endTime);
}
