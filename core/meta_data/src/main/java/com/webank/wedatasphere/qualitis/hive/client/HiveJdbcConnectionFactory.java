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

package com.webank.wedatasphere.qualitis.hive.client;

import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.hive.config.HiveJdbcConfig;
import com.webank.wedatasphere.qualitis.hive.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.hive.exception.ConnectionAcquireFailedException;
import com.webank.wedatasphere.qualitis.hive.config.HiveJdbcConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author howeye
 */
@Component
public class HiveJdbcConnectionFactory {

    @Autowired
    private ClusterInfoDao clusterInfoDao;

    @Autowired
    private HiveJdbcConfig hiveJdbcConfig;

    private volatile Map<String, HikariDataSource> dataSourceMap = new HashMap<>();

    private void initDataSource(ClusterInfo clusterInfo){
        synchronized (HiveJdbcConnectionFactory.class){
            HikariDataSource dataSource = dataSourceMap.get(clusterInfo.getClusterName());
            if (dataSource == null) {
                dataSource = new HikariDataSource();
                // Set Driver
                dataSource.setDriverClassName(hiveJdbcConfig.getDriverClassName());
                // Set database url
                dataSource.setJdbcUrl(clusterInfo.getHiveDatabaseAddress());
                // Set username
                dataSource.setUsername(clusterInfo.getHiveDatabaseUsername());
                // Set password
                dataSource.setPassword(clusterInfo.getHiveDatabasePassword());
                // Set max connection
                dataSource.setMaximumPoolSize(hiveJdbcConfig.getMaxPoolSize());
                // Set min connection
                dataSource.setMinimumIdle(hiveJdbcConfig.getMinPoolSize());

                dataSourceMap.put(clusterInfo.getClusterName(), dataSource);
            }
        }
    }

    public void refreshDataSource(ClusterInfo clusterInfo) {
        synchronized (HiveJdbcConnectionFactory.class){
            HikariDataSource dataSource = new HikariDataSource();
            // Set Driver
            dataSource.setDriverClassName(hiveJdbcConfig.getDriverClassName());
            // Set database url
            dataSource.setJdbcUrl(clusterInfo.getHiveDatabaseAddress());
            // Set username
            dataSource.setUsername(clusterInfo.getHiveDatabaseUsername());
            // Set password
            dataSource.setPassword(clusterInfo.getHiveDatabasePassword());
            // Set max connection
            dataSource.setMaximumPoolSize(hiveJdbcConfig.getMaxPoolSize());
            // Set min connection
            dataSource.setMinimumIdle(hiveJdbcConfig.getMinPoolSize());

            dataSourceMap.put(clusterInfo.getClusterName(), dataSource);
        }
    }

    public Connection getConnection(String clusterName) throws SQLException, ClusterInfoNotConfigException, ConnectionAcquireFailedException {
        ClusterInfo clusterInfo = clusterInfoDao.findByClusterName(clusterName);
        if (clusterInfo == null) {
            throw new ClusterInfoNotConfigException("Can not find cluster config of cluster: " + clusterName);
        }

        if (dataSourceMap.get(clusterInfo.getClusterName()) == null) {
            initDataSource(clusterInfo);
        }

        HikariDataSource dataSource = dataSourceMap.get(clusterInfo.getClusterName());
        if (dataSource != null) {
            return dataSource.getConnection();
        }

        throw new ConnectionAcquireFailedException("Failed to acquire connection");
    }


}
