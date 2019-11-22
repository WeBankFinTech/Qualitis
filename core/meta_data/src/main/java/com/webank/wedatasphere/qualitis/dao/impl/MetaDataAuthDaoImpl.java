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

import com.webank.wedatasphere.qualitis.dao.MetaDataAuthDao;
import com.webank.wedatasphere.qualitis.dao.repository.AuthMetaDataRepository;
import com.webank.wedatasphere.qualitis.entity.AuthMetaData;

import com.webank.wedatasphere.qualitis.dao.MetaDataAuthDao;
import com.webank.wedatasphere.qualitis.entity.AuthMetaData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author howeye
 */
@Repository
public class MetaDataAuthDaoImpl implements MetaDataAuthDao {

  @Autowired
  private AuthMetaDataRepository authMetaDataRepository;

  @Override
  public AuthMetaData findByAuthTypeAndClusterNameAndDbNameAndTableNameAndColumnNameAndUsernameAndIsOrg(
      Integer authType, String clusterName, String dbName, String tableName, String columnName,
      String username, Boolean isOrg) {
    return authMetaDataRepository.findByAuthTypeAndClusterNameAndDbNameAndTableNameAndColumnNameAndUsernameAndIsOrg(
        authType, clusterName, dbName, tableName, columnName, username, isOrg);
  }

  @Override
  public AuthMetaData saveAuthMetaData(AuthMetaData authMetaData) {
    return authMetaDataRepository.save(authMetaData);
  }

  @Override
  public void deleteAuthMetaData(AuthMetaData authMetaData) {
    authMetaDataRepository.delete(authMetaData);
  }

  @Override
  public AuthMetaData findById(Long id) {
    return authMetaDataRepository.findById(id).get();
  }

  @Override
  public List<AuthMetaData> findByUsername(String username, Boolean isOrg, Integer page,
      Integer size) {
    Sort sort = new Sort(Sort.Direction.ASC, "id");
    Pageable pageable = PageRequest.of(page, size, sort);
    return authMetaDataRepository.findByUsernameAndIsOrg(username, isOrg, pageable).getContent();
  }

  /**
   * Find meta data authorization by cluster and user and is org
   * @param loginUser
   * @param isOrg
   * @param pageIndex
   * @param pageSize
   * @return
   */
  @Override
  public List<String> findDistinctClusterPageByUsernameAndIsOrg(String loginUser, boolean isOrg,
      Integer pageIndex, Integer pageSize) {
    Sort sort = new Sort(Sort.Direction.ASC, "id");
    Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
    return authMetaDataRepository.findDistinctByUsernameAndIsOrg(loginUser, isOrg, pageable);
  }

  /**
   * Count meta data authorization by user and is org
   * @param loginUser
   * @param isOrg
   * @return
   */
  @Override
  public long countDistinctClusterByUsernameAndIsOrg(String loginUser, boolean isOrg) {
    return authMetaDataRepository.countDistinctByUsernameAndIsOrg(loginUser, isOrg);
  }

  /**
   * Check if user authorized to cluster
   * @param clusterName
   * @param loginUser
   * @param isOrg
   * @return
   */
  @Override
  public boolean existsClusterAuth(Integer authType, String clusterName, String loginUser,
      boolean isOrg) {
    return authMetaDataRepository.existsByAuthTypeAndClusterNameAndUsernameAndIsOrg(authType,
                                                                                    clusterName,
                                                                                    loginUser,
                                                                                    isOrg);
  }

  /**
   * Find authorization by cluster and user and is org
   * @param clusterName
   * @param loginUser
   * @param isOrg
   * @param pageIndex
   * @param pageSize
   * @return
   */
  @Override
  public List<String> findDistinctDbPageByClusterNameAndUsernameAndIsOrg(String clusterName,
      String loginUser, boolean isOrg, Integer pageIndex, Integer pageSize) {
    Sort sort = new Sort(Sort.Direction.ASC, "id");
    Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
    return authMetaDataRepository.findDistinctDbByClusterNameAndUsernameAndIsOrg(clusterName,
                                                                                 loginUser, isOrg,
                                                                                 pageable);
  }

  /**
   * Count authorization by cluster and user and is org
   * @param clusterName
   * @param loginUser
   * @param isOrg
   * @return
   */
  @Override
  public long countDistinctDbByClusterNameAndUsernameAndIsOrg(String clusterName, String loginUser,
      boolean isOrg) {
    return authMetaDataRepository.countDistinctDbByClusterNameAndUsernameAndIsOrg(clusterName,
                                                                                  loginUser, isOrg);
  }

  /**
   * Check if user authorized to database
   * @param authType
   * @param clusterName
   * @param dbName
   * @param loginUser
   * @param isOrg
   * @return boolean
   */
  @Override
  public boolean existsDbAuth(Integer authType, String clusterName, String dbName, String loginUser,
      boolean isOrg) {
    return authMetaDataRepository.existsByAuthTypeAndClusterNameAndDbNameAndUsernameAndIsOrg(
        authType, clusterName, dbName, loginUser, isOrg);
  }

  /**
   * Find authorization by table and cluster and username and is org
   * @param clusterName
   * @param dbName
   * @param loginUser
   * @param isOrg
   * @param pageIndex
   * @param pageSize
   * @return
   */
  @Override
  public List<String> findDistinctTablePageByClusterNameAndDbNameAndUsernameAndIsOrg(
      String clusterName, String dbName, String loginUser, boolean isOrg, Integer pageIndex,
      Integer pageSize) {
    Sort sort = new Sort(Sort.Direction.ASC, "id");
    Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
    return authMetaDataRepository.findDistinctTableByClusterNameAndDbNameAndUsernameAndIsOrg(
        clusterName, dbName, loginUser, isOrg, pageable);
  }

  /**
   * Count authorization by table and cluster and username and is org
   * @param clusterName
   * @param dbName
   * @param loginUser
   * @param isOrg
   * @return
   */
  @Override
  public long countDistinctTableByClusterNameAndDbNameAndUsernameAndIsOrg(String clusterName,
      String dbName, String loginUser, boolean isOrg) {
    return authMetaDataRepository.countDistinctTableByClusterNameAndDbNameAndUsernameAndIsOrg(
        clusterName, dbName, loginUser, isOrg);
  }

  /**
   * Check if table authorized to user
   * @param authType
   * @param clusterName
   * @param dbName
   * @param tableName
   * @param loginUser
   * @param isOrg
   * @return boolean
   */
  @Override
  public boolean existsTableAuth(Integer authType, String clusterName, String dbName,
      String tableName, String loginUser, boolean isOrg) {
    return authMetaDataRepository.existsByAuthTypeAndClusterNameAndDbNameAndTableNameAndUsernameAndIsOrg(
        authType, clusterName, dbName, tableName, loginUser, isOrg);
  }

  /**
   * Find authorization by auth type and cluster name and db name and table name and username and is org
   * @param authType
   * @param clusterName
   * @param dbName
   * @param tableName
   * @param loginUser
   * @param isOrg
   * @param pageIndex
   * @param pageSize
   * @return
   */
  @Override
  public List<AuthMetaData> findColumnPageByAuthTypeAndClusterNameAndDbNameAndTableNameAndUsernameAndIsOrg(
      Integer authType, String clusterName, String dbName, String tableName, String loginUser,
      boolean isOrg, Integer pageIndex, Integer pageSize) {
    Sort sort = new Sort(Sort.Direction.ASC, "id");
    Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
    return authMetaDataRepository.findByAuthTypeAndClusterNameAndDbNameAndTableNameAndUsernameAndIsOrg(
        authType, clusterName, dbName, tableName, loginUser, isOrg, pageable);
  }

  /**
   *
   * Count authorization by auth type and cluster name and db name and table name and username and is org
   * @param authType
   * @param clusterName
   * @param dbName
   * @param tableName
   * @param loginUser
   * @param isOrg
   * @return
   */
  @Override
  public long countColumnByAuthTypeAndClusterNameAndDbNameAndTableNameAndUsernameAndIsOrg(
      Integer authType, String clusterName, String dbName, String tableName, String loginUser,
      boolean isOrg) {
    return authMetaDataRepository.countByAuthTypeAndClusterNameAndDbNameAndTableNameAndUsernameAndIsOrg(
        authType, clusterName, dbName, tableName, loginUser, isOrg);
  }

  /**
   * Query authorization by auth type, cluster, db, table and so on
   * @param authTypes
   * @param clusterName
   * @param dbName
   * @param tableName
   * @param username
   * @param page
   * @param size
   * @return
   */
  @Override
  public List<AuthMetaData> query(List<Integer> authTypes, String clusterName, String dbName, String tableName,
      String username, Integer page, Integer size) {
    Sort sort = new Sort(Sort.Direction.ASC, "id");
    Pageable pageable = PageRequest.of(page, size, sort);
    return authMetaDataRepository.findAll((root, query, cb) -> getSpec(root, query, cb, authTypes, clusterName, dbName, tableName, username)
    , pageable).getContent();
  }

  /**
   * Count all authorizations
   * @param authTypes
   * @param clusterName
   * @param dbName
   * @param tableName
   * @param username
   * @return
   */
  @Override
  public long count(List<Integer> authTypes, String clusterName, String dbName, String tableName, String username) {
    return authMetaDataRepository.count((root, query, cb) -> getSpec(root, query, cb, authTypes, clusterName, dbName, tableName, username));
  }

  private Predicate getSpec(Root<AuthMetaData> root, CriteriaQuery<?> query, CriteriaBuilder cb,
                            List<Integer> authTypes, String clusterName, String dbName, String tableName, String username) {
    List<Predicate> predicates = new ArrayList<>();
    Predicate predicates1 = null;
    if (CollectionUtils.isNotEmpty(authTypes)) {
      predicates.add(root.get("authType").in(authTypes));
    }
    if (StringUtils.isNotBlank(username)) {
      predicates.add(cb.equal(root.get("username"), username));
    }

    if (StringUtils.isNotBlank(clusterName)) {
      predicates1 = cb.equal(root.get("clusterName"), clusterName);
      if (StringUtils.isNotBlank(dbName)) {
        predicates1 = cb.or(cb.and(cb.equal(root.get("clusterName"), clusterName), cb.equal(root.get("dbName"), dbName)),
                cb.and(cb.equal(root.get("clusterName"), clusterName), cb.equal(root.get("dbName"), "")));

        if (StringUtils.isNotBlank(tableName)) {
          predicates1 = cb.or(cb.and(cb.equal(root.get("clusterName"), clusterName), cb.equal(root.get("dbName"), dbName), cb.equal(root.get("tableName"), tableName)),
                  cb.and(cb.equal(root.get("clusterName"), clusterName), cb.equal(root.get("dbName"), dbName), cb.equal(root.get("tableName"), "")),
                  cb.and(cb.equal(root.get("clusterName"), clusterName), cb.equal(root.get("dbName"), ""), cb.equal(root.get("tableName"), ""))
          );
        }
      }
    }

    if (predicates1 != null) {
      predicates.add(predicates1);
    }
    Predicate[] p = new Predicate[predicates.size()];

    query.where(cb.and(predicates.toArray(p)));
    return query.getRestriction();
  }
}
