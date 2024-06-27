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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public interface ApplicationRepository extends JpaRepository<Application, String>, JpaSpecificationExecutor<Application> {

    /**
     * Find application by creator and ID like
     * @param user
     * @param applicationId
     * @param pageable
     * @return
     */
    @Query("select a from Application a where exists (select pu.id from ProjectUser pu where pu.project.id = a.projectId and pu.userName = ?1) and a.id like ?2")
    List<Application> findByCreateUserAndId(String user, String applicationId, Pageable pageable);

    /**
     * Count application
     * @param createUser
     * @param applicationId
     * @return
     */
    @Query("select count(a.id) from Application a where exists (select pu.id from ProjectUser pu where pu.project.id = a.projectId and pu.userName = ?1) and a.id like ?2")
    long countByCreateUserAndId(String createUser, String applicationId);

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
     * Paging find application by advanced conditions.
     * @param user
     * @param projectId
     * @param status
     * @param startSubmitDate
     * @param endSubmitDate
     * @param commentType
     * @param ruleGroupId
     * @param pageable
     * @param executeUser
     * @param stopStatus
     * @param startFinishTime
     * @param endFinishTime
     * @return
     */
    @Query(value ="select a.* from qualitis_application a " +
            "where exists (select pu.id from qualitis_project_user pu where pu.project_id = a.project_id and pu.user_name = ?1) " +
            "and (?2 is null or a.project_id = ?2) " +
            "and (?3 is null or ?3 = 0 or a.status = ?3) " +
            "and (a.submit_time between ?4 and ?5) " +
            "and (?6 is null or a.application_comment = ?6) " +
            "and (?7 is null or a.rule_group_id = ?7) " +
            "and if(nullif(?8,'')!='',  a.execute_user = ?8,1=1) " +
            "and (coalesce(?9,null) is null or a.status in (?9) )" +
            "and (LENGTH(?10) = 0 or a.finish_time >= ?10) " +
            "and (LENGTH(?11) = 0 or a.finish_time <= ?11)",
            countQuery="select count(*) from qualitis_application a " +
                    "where exists (select pu.id from qualitis_project_user pu where pu.project_id = a.project_id and pu.user_name = ?1) " +
                    "and (?2 is null or a.project_id = ?2) " +
                    "and (?3 is null or ?3 = 0 or a.status = ?3) " +
                    "and (a.submit_time between ?4 and ?5) " +
                    "and (?6 is null or a.application_comment = ?6) " +
                    "and (?7 is null or a.rule_group_id = ?7) " +
                    "and if(nullif(?8,'')!='',  a.execute_user = ?8,1=1) " +
                    "and (coalesce(?9,null) is null or a.status in (?9) )" +
                    "and (LENGTH(?10) = 0 or a.finish_time >= ?10) " +
                    "and (LENGTH(?11) = 0 or a.finish_time <= ?11)",nativeQuery = true)
    Page<Application> findApplicationByAdvanceConditions(String user, Long projectId, Integer status, String startSubmitDate, String endSubmitDate,
                                                         Integer commentType, Long ruleGroupId, String executeUser, List<Integer> stopStatus, String startFinishTime, String endFinishTime, Pageable pageable);

    /**
     * With datasource advance.
     * @param userName
     * @param clusterName
     * @param databaseName
     * @param tableName
     * @param projectId
     * @param taskStatus
     * @param applicationStatus
     * @param commentType
     * @param startTime
     * @param endTime
     * @param ruleGroupId
     * @param pageable
     * @param executeUser
     * @return
     */
    @Query(value = "SELECT DISTINCT qat.application_id FROM qualitis_application_task qat join qualitis_application_task_datasource qatd on qat.id = qatd.task_id " +
            "inner join qualitis_application qa on qa.id = qat.application_id " +
            "WHERE exists (select pu.id from qualitis_project_user pu where pu.project_id = qatd.project_id and pu.user_name = ?1) " +
            "and if(nullif(?2,'')!='',  qatd.cluster_name = ?2,1=1) " +
            "and if(nullif(?3,'')!='',  qatd.database_name = ?3,1=1) " +
            "and if(nullif(?4,'')!='',  qatd.table_name = ?4,1=1) " +
            "and (?5 IS NULL OR qatd.project_id = ?5) " +
            "and (?6 IS NULL OR qat.status = ?6) " +
            "and (?7 IS NULL OR qa.status = ?7) " +
            "and (?8 IS NULL OR qat.task_comment = ?8) " +
            "and qat.begin_time > ?9 and qat.end_time < ?10 " +
            "and (?11 is null or exists (select ac.id from qualitis_application_task ac join qualitis_application ad on ac.application_id=ad.id where ad.rule_group_id=?11)) " +
            "and if(nullif(?12,'')!='',  qatd.execute_user = ?12,1=1)",
            countQuery="SELECT count(DISTINCT qat.application_id) FROM qualitis_application_task qat join qualitis_application_task_datasource qatd on qat.id = qatd.task_id " +
                    "inner join qualitis_application qa on qa.id = qat.application_id " +
                    "WHERE exists (select pu.id from qualitis_project_user pu where pu.project_id = qatd.project_id and pu.user_name = ?1) " +
                    "and if(nullif(?2,'')!='',  qatd.cluster_name = ?2,1=1) " +
                    "and if(nullif(?3,'')!='',  qatd.database_name = ?3,1=1) " +
                    "and if(nullif(?4,'')!='',  qatd.table_name = ?4,1=1) " +
                    "and (?5 IS NULL OR qatd.project_id = ?5) " +
                    "and (?6 IS NULL OR qat.status = ?6) " +
                    "and (?7 IS NULL OR qa.status = ?7) " +
                    "and (?8 IS NULL OR qat.task_comment = ?8) " +
                    "and qat.begin_time > ?9 and qat.end_time < ?10 " +
                    "and (?11 is null or exists (select ac.id from qualitis_application_task ac join qualitis_application ad on ac.application_id=ad.id where ad.rule_group_id=?11)) " +
                    "and if(nullif(?12,'')!='',  qatd.execute_user = ?12,1=1)",
            nativeQuery = true)
    List<String> findApplicationByAdvanceConditionsWithDatasource(String userName, String clusterName, String databaseName, String tableName, Long projectId
            , Integer taskStatus, Integer applicationStatus, Integer commentType, String startTime, String endTime, Long ruleGroupId, String executeUser, Pageable pageable);

    /**
     * Count with datasource advance
     * @param userName
     * @param clusterName
     * @param databaseName
     * @param tableName
     * @param projectId
     * @param taskStatus
     * @param applicationStatus
     * @param commentType
     * @param startTime
     * @param endTime
     * @param ruleGroupId
     * @param executeUser
     * @return
     */
    @Query(value = "SELECT count(DISTINCT qat.application_id) FROM qualitis_application_task qat join qualitis_application_task_datasource qatd on qat.id = qatd.task_id " +
            "inner join qualitis_application qa on qa.id = qat.application_id " +
            "WHERE exists (select pu.id from qualitis_project_user pu where pu.project_id = qatd.project_id and pu.user_name = ?1) " +
            "and if(nullif(?2,'')!='',  qatd.cluster_name = ?2,1=1) " +
            "and if(nullif(?3,'')!='',  qatd.database_name = ?3,1=1) " +
            "and if(nullif(?4,'')!='',  qatd.table_name = ?4,1=1) " +
            "and (?5 IS NULL OR qatd.project_id = ?5) " +
            "and (?6 IS NULL OR qat.status = ?6) " +
            "and (?7 IS NULL OR qa.status = ?7) " +
            "and (?8 IS NULL OR qat.task_comment = ?8) " +
            "and qat.begin_time > ?9 and qat.end_time < ?10 " +
            "and (?11 is null or exists (select ac.id from qualitis_application_task ac join qualitis_application ad on ac.application_id=ad.id where ad.rule_group_id=?11)) " +
            "and if(nullif(?12,'')!='',  qatd.execute_user = ?12,1=1)",
            nativeQuery = true)
    long countApplicationByAdavnceConditionsWithDatasource(String userName, String clusterName, String databaseName, String tableName, Long projectId, Integer taskStatus, Integer applicationStatus, Integer commentType, String startTime, String endTime, Long ruleGroupId, String executeUser);

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
     * Paging find application by creator and submit time
     * @param user
     * @param startSubmitDate
     * @param endSubmitDate
     * @param pageable
     * @return
     */
    @Query("select app from Application app where exists (select pu.id from ProjectUser pu where pu.project.id = app.projectId and pu.userName = ?1) and (app.submitTime between ?2 and ?3)")
    Page<Application> findApplicationByUserAndSubmitTimeBetween(String user, String startSubmitDate, String endSubmitDate, Pageable pageable);

    /**
     * Paging find application by creator and time between
     * @param user
     * @param startSubmitDate
     * @param endSubmitDate
     * @return
     */
    @Query("select count(app.id) from Application app where exists (select pu.id from ProjectUser pu where pu.project.id = app.projectId and pu.userName = ?1) and (app.submitTime between ?2 and ?3)")
    long countApplicationByUserAndSubmitTimeBetween(String user, String startSubmitDate, String endSubmitDate);

    /**
     * Paging find application by creator and time between and status
     * @param user
     * @param startSubmitDate
     * @param endSubmitDate
     * @param status
     * @return
     */
    @Query("select count(app.id) from Application app where exists (select pu.id from ProjectUser pu where pu.project.id = app.projectId and pu.userName = ?1) and (app.submitTime between ?2 and ?3) and app.status = ?4")
    long countApplicationByUserAndSubmitTimeBetweenAndStatus(String user, String startSubmitDate, String endSubmitDate, Integer status);

    /**
     * Chart application by submit time
     * @param user
     * @param startSubmitDate
     * @param endSubmitDate
     * @return
     */
    @Query("select new map(DATE_FORMAT(app.submitTime, '%Y-%m-%d') as submit_time, app.status as status, count(app.id) as count) from Application app where exists (select pu.id from ProjectUser pu where pu.project.id = app.projectId and pu.userName = ?1) and (app.submitTime between ?2 and ?3) group by DATE_FORMAT(app.submitTime, '%Y-%m-%d'), app.status")
    List<Map<String, Object>> chartApplicationByUserAndSubmitTimeBetween(String user, String startSubmitDate, String endSubmitDate);

    /**
     * get service with application num
     * @return
     */
    @Query(value = "SELECT new map(a.ip as ip, count(a) as num) FROM Application a where a.status = 3 or a.status = 10 group by ip")
    List<Map<String, Object>> getServiceWithApplicationNum();

    /**
     * Get service with application num in list
     * @param ipList
     * @return
     */
    @Query(value = "SELECT new map(a.ip as ip, count(*) as num) FROM Application a where (a.status = 3 or a.status = 10) or (a.ip in (?1)) GROUP BY ip")
    List<Map<String, Object>> getServiceWithApplicationNumIn(List<String> ipList);

    /**
     * Count not finish application
     * @param currentIp
     * @return
     */
    @Query(value = "SELECT count(a.id) FROM Application a where (a.status = 3 or a.status = 10) and a.ip = ?1")
    int countNotFinishApplicationNum(String currentIp);

    /**
     * get All ExecuteUser
     * @param userName
     * @return
     */
    @Query(value = "select distinct a.executeUser from Application a where exists (select pu.id from ProjectUser pu where pu.project.id = a.projectId and pu.userName = ?1)")
    List<String> getAllExecuteUser(String userName);

    /**
     * find By Project
     * @param projectId
     * @return
     */
    @Query(value = "select /*slave*/ * from qualitis_application  where project_id=?1", nativeQuery = true)
    List<Application> findByProject(Long projectId);
}
