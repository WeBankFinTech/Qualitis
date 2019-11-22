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
import com.webank.wedatasphere.qualitis.entity.Application;

import java.util.List;

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
   * @param page
   * @param size
   * @param status
   * @return
   */
  List<Application> findByCreateUserAndStatus(String createUser, Integer status, Integer page, Integer size);

  /**
   * Count application by creator and status
   * @param createUser
   * @param status
   * @return
   */
  Long countByCreateUserAndStatus(String createUser, Integer status);

  /**
   * Find application by user and time between
   * @param createUser
   * @param startSubmitDate
   * @param endSubmitDate
   * @return
   */
  List<Application> findApplicationByUserAndSubmitTimeBetween(String createUser, String startSubmitDate, String endSubmitDate);

  /**
   * Paging find application by user and time between
   * @param user
   * @param startSubmitDate
   * @param endSubmitDate
   * @param page
   * @param size
   * @return
   */
  List<Application> findApplicationByUserAndSubmitTimeBetweenPage(String user, String startSubmitDate, String endSubmitDate, int page, int size);

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
  long countApplicationByUserAndSubmitTimeBetweenAndStatusIn(String user,
      String startSubmitDate, String endSubmitDate, Integer[] appStatus);

  /**
   * Find application by creator and application id
   * @param createUser
   * @param applicationId
   * @return
   */
  List<Application> findByCreateUserAndIdLike(String createUser, String applicationId);
}
