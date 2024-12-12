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

package com.webank.wedatasphere.qualitis.dao;

import com.webank.wedatasphere.qualitis.entity.Application;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public interface ApplicationDao {

  /**
   * Save application
   * @param application
   * @return application
   */
  Application saveApplication(Application application);

  /**
   * Save and flush
   * @param application
   */
  void saveAndFlush(Application application);


  /**
   * Flush change.
   */
  void flush();

  /**
   * Find application by id
   * @param taskId
   * @return
   */
  Application findById(String taskId);

  /**
   * Find application by creator
   * @param createUser
   * @param page
   * @param size
   * @return
   */
  List<Application> findByCreateUser(String createUser, Integer page, Integer size);

  /**
   * Find application by creator
   * @param createUser
   * @return
   */
  Long countByCreateUser(String createUser);

  /**
   * Find application by creator and status
   * @param createUser
   * @param status
   * @param page
   * @param size
   * @param integer
   * @return
   */
  List<Application> findByCreateUserAndStatus(String createUser, Integer status, Integer page,
      Integer size, Integer integer);

  /**
   * Count application by creator and status
   * @param createUser
   * @param status
   * @param commentType
   * @return
   */
  Long countByCreateUserAndStatus(String createUser, Integer status, Integer commentType);

  /**
   * Find by project.
   * @param createUser
   * @param projectId
   * @param page
   * @param size
   * @return
   */
  List<Application> findByCreateUserAndProject(String createUser, Long projectId, Integer page,
      Integer size);

  /**
   * Count by project.
   * @param createUser
   * @param projectId
   * @return
   */
  Long countByCreateUserAndProject(String createUser, Long projectId);

  /**
   * Paging find application by user and time between
   * @param user
   * @param startSubmitDate
   * @param endSubmitDate
   * @param page
   * @param size
   * @return
   */
  List<Application> findApplicationByUserAndSubmitTimeBetween(String user, String startSubmitDate, String endSubmitDate, int page, int size);

  /**
   * Chart application by submit time
   * @param user
   * @param startSubmitDate
   * @param endSubmitDate
   * @return
   */
  List<Map<String, Object>> chartApplicationByUserAndSubmitTimeBetween(String user, String startSubmitDate, String endSubmitDate);

    /**
   * Count application by user and time between
   * @param user
   * @param startSubmitDate
   * @param endSubmitDate
   * @param status
   * @return
   */
  long countApplicationByUserAndSubmitTimeBetweenAndStatus(String user, String startSubmitDate, String endSubmitDate, Integer status);

  /**
   * Paging find application by advanced conditions.
   *
   * @param user
   * @param projectId
   * @param status
   * @param commentType
   * @param startSubmitDate
   * @param endSubmitDate
   * @param ruleGroupId
   * @param executeUser
   * @param page
   * @param size
   * @param stopStatus
   * @param startFinishTime
   * @param endFinishTime
   * @return
   */
  Page<Application> findApplicationByAdvanceConditions(String user, Long projectId, Integer status, Integer commentType,
                                                       String startSubmitDate, String endSubmitDate, Long ruleGroupId, String executeUser, List<Integer> stopStatus,
                                                       String startFinishTime, String endFinishTime, int page, int size);

    /**
   * Find application by status in status list
   * @param statusList
   * @return
   */
  List<Application> findByStatusIn(List<Integer> statusList);

  /**
   * Find application by status not in status list
   * @param statusList
   * @return
   */
  List<Application> findByStatusNotIn(List<Integer> statusList);

  /**
   * Count application by user and submit time between and status not in status list
   * @param user
   * @param startSubmitDate
   * @param endSubmitDate
   * @param appStatus
   * @return
   */
  long countApplicationByUserAndSubmitTimeBetweenAndStatusIn(String user, String startSubmitDate, String endSubmitDate, Integer[] appStatus);

  /**
   * Find application by creator and application id, to be optimized
   * @param createUser
   * @param applicationId
   * @param page
   * @param size
   * @return
   */
  List<Application> findByCreateUserAndId(String createUser, String applicationId, Integer page, Integer size);

  /**
   * Count
   * @param userName
   * @param applicationId
   * @return
   */
  long countByCreateUserAndId(String userName, String applicationId);

  /**
   * With datasource advance
   * @param userName
   * @param clusterName
   * @param databaseName
   * @param tableName
   * @param projectId
   * @param status
   * @param commentType
   * @param startTime
   * @param endTime
   * @param ruleGroupId
   * @param executeUser
   * @param page
   * @param size
   * @return
   */
  List<Application> findApplicationByAdvanceConditionsWithDatasource(String userName, String clusterName, String databaseName, String tableName, Long projectId, Integer status, Integer commentType, String startTime, String endTime, Long ruleGroupId,String executeUser, Integer page,
        Integer size);

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
   * @param ruleGroupId
   * @param executeUser
   * @return
   */
  long countApplicationByAdvanceConditionsWithDatasource(String userName, String clusterName, String databaseName, String tableName, Long projectId, Integer status, Integer commentType, String startTime, String endTime, Long ruleGroupId,String executeUser);

  /**
   * Get service with application num
   * @return
   */
    List<Map<String, Object>> getServiceWithApplicationNum();

  /**
   * Get service with application num in list
   * @param ipList
   * @return
   */
    List<Map<String, Object>>  getServiceWithApplicationNumIn(List<String> ipList);

  /**
   * Count not finish application
   * @param currentIp
   * @return
   */
  int countNotFinishApplicationNum(String currentIp);

  /**
   * get All ExecuteUser
   * @param userName
   * @return
   */
  List<String> getAllExecuteUser(String userName);

}
