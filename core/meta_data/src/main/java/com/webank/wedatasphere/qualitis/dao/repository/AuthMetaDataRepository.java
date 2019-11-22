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

import com.webank.wedatasphere.qualitis.entity.AuthMetaData;

import com.webank.wedatasphere.qualitis.entity.AuthMetaData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author howeye
 */
public interface AuthMetaDataRepository extends JpaRepository<AuthMetaData, Long> ,
    JpaSpecificationExecutor<AuthMetaData> {

    /**
     * Find authorization by auth type, cluster name, db name, table name, column name, username and is org
     * @param authType
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param columnName
     * @param username
     * @param isOrg
     * @return
     */
    AuthMetaData findByAuthTypeAndClusterNameAndDbNameAndTableNameAndColumnNameAndUsernameAndIsOrg(Integer authType, String clusterName, String dbName,
                                                                                           String tableName, String columnName, String username, Boolean isOrg);

    /**
     * Paging find auth meta data by username
     * @param username
     * @param pageable
     * @param isOrg
     * @return
     */
    Page<AuthMetaData> findByUsernameAndIsOrg(String username, Boolean isOrg, Pageable pageable);

    /**
     * Find auth meta data by auth type, cluster name, db name, table name and column name
     * @param authType
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param columnName
     * @param pageable
     * @return
     */
    Page<AuthMetaData> findByAuthTypeAndClusterNameAndDbNameAndTableNameAndColumnName(Integer authType, String clusterName,
                                                                                      String dbName, String tableName, String columnName, Pageable pageable);

    /**
     * Find auth meta data by auth type, cluster name, db name, table name and column name
     * @param authType
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param columnName
     * @return
     */
    Long countByAuthTypeAndClusterNameAndDbNameAndTableNameAndColumnName(Integer authType, String clusterName, String dbName, String tableName, String columnName);

    /**
     * Count by username and is org
     * @param username
     * @param isOrg
     * @return
     */
    Long countByUsernameAndIsOrg(String username, Boolean isOrg);
    /**
     * Find authorized cluster by username and is org
     * @param username
     * @param isOrg
     * @param pageable
     * @return
     */
    @Query(value = "select distinct j.clusterName from AuthMetaData j  where j.username = :username and j.isOrg = :isOrg and j.clusterName is not null")
    List<String> findDistinctByUsernameAndIsOrg(@Param("username")String username, @Param("isOrg")boolean isOrg, Pageable pageable);

    /**
     * Count authorized cluster by username and is org
     * @param username 登录用户名
     * @param isOrg 是否是用户组
     * @return  总数
     */
    @Query(value = "select count(distinct j.clusterName) from AuthMetaData j  where j.username = :username and j.isOrg = :isOrg and j.clusterName is not null")
    long countDistinctByUsernameAndIsOrg(@Param("username")String username, @Param("isOrg")boolean isOrg);

    /**
     * Check existence of authorization by auth type, cluster name, username and is org
     * @param authType
     * @param clusterName
     * @param loginUser
     * @param isOrg
     * @return boolean
     */
    boolean existsByAuthTypeAndClusterNameAndUsernameAndIsOrg(Integer authType, String clusterName, String loginUser, boolean isOrg);

    /**
     * Find db name by cluster name and user
     * @param clusterName
     * @param username
     * @param isOrg
     * @param pageable
     * @return
     */
    @Query(value = "select distinct j.dbName from AuthMetaData j  where j.username = :username and j.isOrg = :isOrg and j.clusterName = :clusterName and j.dbName is not null")
    List<String> findDistinctDbByClusterNameAndUsernameAndIsOrg(@Param("clusterName")String clusterName, @Param("username")String username, @Param("isOrg")boolean isOrg, Pageable pageable);

    /**
     * Count db name by cluster name and user
     * @param clusterName
     * @param username
     * @param isOrg
     * @return
     */
    @Query(value = "select count(distinct j.dbName) from AuthMetaData j  where j.username = :username and j.isOrg = :isOrg and j.clusterName = :clusterName and j.dbName is not null")
    long countDistinctDbByClusterNameAndUsernameAndIsOrg(@Param("clusterName")String clusterName, @Param("username")String username,
        @Param("isOrg")boolean isOrg);

    /**
     * 查找用户是否已授权某个数据库
     * Check existence of authorization by auth type, cluster name, db name and username
     * @param authType
     * @param clusterName
     * @param dbName
     * @param loginUser
     * @param isOrg
     * @return boolean
     */
    boolean existsByAuthTypeAndClusterNameAndDbNameAndUsernameAndIsOrg(Integer authType,
        String clusterName, String dbName, String loginUser, boolean isOrg);

    /**
     * Find table by cluster, db name and username
     * @param clusterName
     * @param dbName
     * @param username
     * @param isOrg
     * @param pageable
     * @return
     */
    @Query(value = "select distinct j.tableName from AuthMetaData j  where j.username = :username and j.isOrg = :isOrg and j.clusterName = :clusterName and j.dbName = :dbName and j.tableName is not null")
    List<String> findDistinctTableByClusterNameAndDbNameAndUsernameAndIsOrg(@Param("clusterName") String clusterName,
        @Param("dbName") String dbName, @Param("username") String username, @Param("isOrg") boolean isOrg, Pageable pageable);

    /**
     * Count table by cluster, db name and username
     * @param clusterName
     * @param dbName
     * @param username
     * @param isOrg
     * @return
     */
    @Query(value = "select count(distinct j.tableName) from AuthMetaData j  where j.username = :username and j.isOrg = :isOrg and j.clusterName = :clusterName and j.dbName = :dbName and j.tableName is not null")
    long countDistinctTableByClusterNameAndDbNameAndUsernameAndIsOrg(@Param("clusterName") String clusterName,
        @Param("dbName") String dbName, @Param("username") String username, @Param("isOrg") boolean isOrg);

    /**
     * Check existence of authorization by auth type, cluster, db name, table name and user
     * @param authType
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param loginUser
     * @param isOrg
     * @return boolean
     */
    boolean existsByAuthTypeAndClusterNameAndDbNameAndTableNameAndUsernameAndIsOrg(Integer authType,
        String clusterName, String dbName, String tableName, String loginUser, boolean isOrg);

    /**
     * Count column by auth type, cluster name, db name, table name and user
     * @param authType
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param loginUser
     * @param isOrg
     * @return
     */
    long countByAuthTypeAndClusterNameAndDbNameAndTableNameAndUsernameAndIsOrg(Integer authType,
        String clusterName, String dbName, String tableName, String loginUser, boolean isOrg);

    /**
     * Find column by auth type, cluster name, db name, table name and user
     * @param authType
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param loginUser
     * @param isOrg
     * @param pageable
     * @return
     */
    List<AuthMetaData> findByAuthTypeAndClusterNameAndDbNameAndTableNameAndUsernameAndIsOrg(Integer authType,
        String clusterName, String dbName, String tableName, String loginUser, boolean isOrg, Pageable pageable);
}
