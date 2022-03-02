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

package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.dao.repository.ApplicationRepository;
import com.webank.wedatasphere.qualitis.entity.Application;

import com.webank.wedatasphere.qualitis.project.entity.Project;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

/**
 * @author howeye
 */
@Repository
public class ApplicationDaoImpl implements ApplicationDao {

  @Autowired
  private ApplicationRepository repository;

  @Override
  public Application saveApplication(Application application) {
    return repository.save(application);
  }

  @Override
  public void saveAndFlush(Application application) {
    repository.saveAndFlush(application);
  }

  @Override
  public void flush() {
    repository.flush();
  }


  @Override
  public Application findById(String applicationId) {
    return repository.findById(applicationId).orElse(null);
  }

  @Override
  public List<Application> findByCreateUser(String createUser, Integer page, Integer size) {
    Sort sort = new Sort(Sort.Direction.DESC, "submitTime");
    Pageable pageable = PageRequest.of(page, size, sort);
    return repository.findByCreateUser(createUser, pageable).getContent();
  }

  @Override
  public Long countByCreateUser(String createUser) {
    return repository.countByCreateUser(createUser);
  }

  @Override
  public List<Application> findByCreateUserAndStatus(String createUser, Integer status, Integer commentType, Integer page, Integer size) {
    Sort sort = new Sort(Sort.Direction.DESC, "submitTime");
    Pageable pageable = PageRequest.of(page, size, sort);
    return repository.findByCreateUserAndStatus(createUser, status, commentType, pageable).getContent();
  }

  @Override
  public Long countByCreateUserAndStatus(String createUser, Integer status, Integer commentType) {
    return repository.countByCreateUserAndStatus(createUser, status, commentType);
  }

  @Override
  public List<Application> findByCreateUserAndProject(String createUser, Long projectId, Integer page,
      Integer size) {
    Sort sort = new Sort(Sort.Direction.DESC, "submitTime");
    Pageable pageable = PageRequest.of(page, size, sort);
    return repository.findByCreateUserAndProject(createUser, projectId, pageable).getContent();
  }

  @Override
  public Long countByCreateUserAndProject(String createUser, Long projectId) {
    return repository.countByCreateUserAndProject(createUser, projectId);
  }

  @Override
  public List<Application> findApplicationByUserAndSubmitTimeBetween(String createUser, String startSubmitDate, String endSubmitDate) {
    return repository.findApplicationByUserAndSubmitTimeBetween(createUser, startSubmitDate, endSubmitDate);
  }

  @Override
  public List<Application> findApplicationByUserAndSubmitTimeBetweenPage(String user, String startSubmitDate, String endSubmitDate, int page, int size) {
    Sort sort = new Sort(Sort.Direction.DESC, "submitTime");
    Pageable pageable = PageRequest.of(page, size, sort);
    return repository.findApplicationByUserAndSubmitTimeBetweenPage(user, startSubmitDate, endSubmitDate, pageable).getContent();
  }

  @Override
  public List<Application> findApplicationByAdavnceConditions(String user, Long projectId, Integer status, Integer commentType, String startSubmitDate,
      String endSubmitDate, int page, int size) {
    Sort sort = new Sort(Sort.Direction.DESC, "submitTime");
    Pageable pageable = PageRequest.of(page, size, sort);
    return repository.findApplicationByAdavnceConditions(user, projectId, status, startSubmitDate, endSubmitDate, commentType, pageable).getContent();
  }

  @Override
  public long countApplicationByAdavnceConditions(String user, Long projectId, Integer status, Integer commentType, String startSubmitDate, String endSubmitDate) {
    return repository.countApplicationByAdavnceConditions(user, projectId, status, startSubmitDate, endSubmitDate, commentType);
  }

  @Override
  public List<Application> findByStatusIn(List<Integer> statusList) {

    return repository.findByStatusIn(statusList);
  }
  @Override
  public List<Application> findByStatusNotIn(List<Integer> statusList) {
    return repository.findByStatusNotIn(statusList);
  }

  @Override
  public long countApplicationByUserAndSubmitTimeBetweenAndStatusIn(String createUser, String startSubmitDate,
                                                                    String endSubmitDate, Integer[] appStatus) {
    return repository.count((root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      predicates.add(cb.equal(root.get("createUser"), createUser));

      if (StringUtils.isNotBlank(startSubmitDate) && StringUtils.isNotBlank(endSubmitDate)) {
        predicates.add(cb.between(root.get("submitTime"), startSubmitDate, endSubmitDate));
      }
      if (appStatus != null && appStatus.length > 0) {
        predicates.add(root.get("status").in((Object[]) appStatus));
      }
      Predicate[] p = new Predicate[predicates.size()];
      query.where(cb.and(predicates.toArray(p)));

      return query.getRestriction();
    });
  }

  @Override
  public List<Application> findByCreateUserAndId(String createUser, String applicationId) {
    return repository.findByCreateUserAndId(createUser, applicationId);
  }

    @Override
    public List<Application> findApplicationByAdavnceConditionsWithDatasource(String userName, String clusterName, String databaseName,
        String tableName, Long projectId, Integer status, Integer commentType, String startTime, String endTime, Integer page, Integer size) {
      Sort sort = new Sort(Sort.Direction.DESC, "id");
      Pageable pageable = PageRequest.of(page, size, sort);
      return repository.findApplicationByAdavnceConditionsWithDatasource(userName, clusterName, databaseName, tableName, projectId
      , status, commentType, startTime, endTime, pageable).getContent();
    }

  @Override
  public long countApplicationByAdavnceConditionsWithDatasource(String userName, String clusterName, String databaseName, String tableName,
      Long projectId, Integer status, Integer commentType, String startTime, String endTime) {
    return repository.countApplicationByAdavnceConditionsWithDatasource(userName, clusterName, databaseName, tableName, projectId
        , status, commentType, startTime, endTime);
  }

}
