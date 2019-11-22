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

import com.webank.wedatasphere.qualitis.entity.AuthMetaData;
import com.webank.wedatasphere.qualitis.entity.AuthMetaData;
import com.webank.wedatasphere.qualitis.entity.AuthMetaData;

import java.util.List;

/**
 * @author howeye
 */
public interface MetaDataAuthDao {

  /**
   * Find auth by cluster name, db name, table name and column and user
   * @param authType
   * @param clusterName
   * @param dbName
   * @param tableName
   * @param columnName
   * @param username
   * @param isOrg
   * @return
   */
  AuthMetaData findByAuthTypeAndClusterNameAndDbNameAndTableNameAndColumnNameAndUsernameAndIsOrg(
      Integer authType, String clusterName, String dbName, String tableName, String columnName,
      String username, Boolean isOrg);

  /**
   * Save authMetaData
   * @param authMetaData
   * @return
   */
  AuthMetaData saveAuthMetaData(AuthMetaData authMetaData);

  /**
   * Delete authMetaData
   * @param authMetaData
   */
  void deleteAuthMetaData(AuthMetaData authMetaData);

  /**
   * Find AuthMetaData by id
   * @param id
   * @return
   */
  AuthMetaData findById(Long id);

  /**
   * Find AuthMetaData by user
   * @param username
   * @param isOrg
   * @param page
   * @param size
   * @return
   */
  List<AuthMetaData> findByUsername(String username, Boolean isOrg, Integer page, Integer size);

  /**
   * Count authorized cluster by user
   * @param loginUser
   * @param isOrg
   * @return
   */
  long countDistinctClusterByUsernameAndIsOrg(String loginUser, boolean isOrg);

  /**
   * Check cluster if authorized to user
   * @param authType
   * @param loginUser
   * @param clusterName
   * @param isOrg
   * @return boolean
   */
  boolean existsClusterAuth(Integer authType, String clusterName, String loginUser, boolean isOrg);

  /**
   * Find Meta data auth by cluster, username and is org
   * @param clusterName
   * @param loginUser
   * @param isOrg
   * @param pageIndex
   * @param pageSize
   * @return
   */
  List<String> findDistinctDbPageByClusterNameAndUsernameAndIsOrg(String clusterName,
      String loginUser, boolean isOrg, Integer pageIndex, Integer pageSize);

  /**
   * Count Meta data auth by cluster, username and is org
   * @param clusterName
   * @param loginUser
   * @param isOrg
   * @return
   */
  long countDistinctDbByClusterNameAndUsernameAndIsOrg(String clusterName, String loginUser,
      boolean isOrg);

  /**
   * Check existence of db auth
   * @param authType
   * @param loginUser
   * @param clusterName
   * @param dbName
   * @param isOrg
   * @return boolean
   */
  boolean existsDbAuth(Integer authType, String clusterName, String dbName, String loginUser,
      boolean isOrg);

  /**
   * Find authorized table by user
   * @param clusterName
   * @param dbName
   * @param loginUser
   * @param isOrg
   * @param pageIndex
   * @param pageSize
   * @return
   */
  List<String> findDistinctTablePageByClusterNameAndDbNameAndUsernameAndIsOrg(String clusterName,
      String dbName, String loginUser, boolean isOrg, Integer pageIndex, Integer pageSize);

  /**
   * Count authorized table by user
   * @param clusterName
   * @param dbName
   * @param loginUser
   * @param isOrg
   * @return
   */
  long countDistinctTableByClusterNameAndDbNameAndUsernameAndIsOrg(String clusterName,
      String dbName, String loginUser, boolean isOrg);

  /**
   * Check table auth existence
   * @param authType
   * @param loginUser
   * @param clusterName
   * @param dbName
   * @param tableName
   * @param isOrg
   * @return boolean
   */
  boolean existsTableAuth(Integer authType, String clusterName, String dbName, String tableName,
      String loginUser, boolean isOrg);

  /**
   * Find authorized field list
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
  List<AuthMetaData> findColumnPageByAuthTypeAndClusterNameAndDbNameAndTableNameAndUsernameAndIsOrg(
      Integer authType, String clusterName, String dbName, String tableName, String loginUser,
      boolean isOrg, Integer pageIndex, Integer pageSize);

  /**
   * Count authorized field list
   * @param authType
   * @param clusterName
   * @param dbName
   * @param tableName
   * @param loginUser
   * @param isOrg
   * @return
   */
  long countColumnByAuthTypeAndClusterNameAndDbNameAndTableNameAndUsernameAndIsOrg(Integer authType,
      String clusterName, String dbName, String tableName, String loginUser, boolean isOrg);

  /**
   * Query auth meta data
   * @param authType
   * @param clusterName
   * @param dbName
   * @param tableName
   * @param username
   * @param page
   * @param size
   * @return
   */
  List<AuthMetaData> query(List<Integer> authType, String clusterName, String dbName, String tableName, String username, Integer page, Integer size);

  /**
   * Count all authorization
   * @param authType
   * @param clusterName
   * @param dbName
   * @param tableName
   * @param username
   * @return
   */
  long count(List<Integer> authType, String clusterName, String dbName, String tableName, String username);

  /**
   * Find meta data authorization by cluster and user and is org
   * @param loginUser
   * @param isOrg
   * @param pageIndex
   * @param pageSize
   * @return
   */
   List<String> findDistinctClusterPageByUsernameAndIsOrg(String loginUser, boolean isOrg, Integer pageIndex, Integer pageSize);
}
