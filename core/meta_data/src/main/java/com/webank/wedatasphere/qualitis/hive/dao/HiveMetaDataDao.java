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

package com.webank.wedatasphere.qualitis.hive.dao;

import com.webank.wedatasphere.qualitis.hive.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.hive.exception.ConnectionAcquireFailedException;
import com.webank.wedatasphere.qualitis.hive.model.HiveColumn;
import com.webank.wedatasphere.qualitis.hive.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.hive.exception.ConnectionAcquireFailedException;
import com.webank.wedatasphere.qualitis.hive.model.HiveColumn;

import java.sql.SQLException;
import java.util.List;

/**
 * @author howeye
 */
public interface HiveMetaDataDao {

    /**
     * Get database by cluster
     * @param clusterName
     * @throws ConnectionAcquireFailedException
     * @throws SQLException
     * @throws ClusterInfoNotConfigException
     * @return
     */
    List<String> getHiveDbByCluster(String clusterName) throws ConnectionAcquireFailedException, SQLException, ClusterInfoNotConfigException;

    /**
     * Get table by cluster and database
     * @param cluster
     * @param dbName
     * @throws ConnectionAcquireFailedException
     * @throws SQLException
     * @throws ClusterInfoNotConfigException
     * @return
     */
    List<String> getHiveTableByClusterAndDb(String cluster, String dbName) throws ConnectionAcquireFailedException, SQLException, ClusterInfoNotConfigException;

    /**
     * Get column by cluster and database and table
     * @param cluster
     * @param dbName
     * @param tableName
     * @throws ConnectionAcquireFailedException
     * @throws SQLException
     * @throws ClusterInfoNotConfigException
     * @return
     */
    List<HiveColumn> getHiveColumnByClusterAndDbAndTable(String cluster, String dbName, String tableName) throws ConnectionAcquireFailedException, SQLException, ClusterInfoNotConfigException;

}
