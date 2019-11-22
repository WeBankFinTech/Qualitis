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

package com.webank.wedatasphere.qualitis.hive;

import com.webank.wedatasphere.qualitis.config.ZkConfig;
import com.webank.wedatasphere.qualitis.exception.*;
import com.webank.wedatasphere.qualitis.zk.ZookeeperCuratorManager;
import com.webank.wedatasphere.qualitis.exception.HiveMetaStoreConnectException;
import com.webank.wedatasphere.qualitis.exception.ZooKeeperWaitTimeOutException;
import com.webank.wedatasphere.qualitis.zk.ZookeeperCuratorManager;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

/**
 * @author howeye
 */
@Component
public class DatabaseChecker {

    @Autowired
    private HiveMetaStoreManager hiveMetaStoreManager;

    @Autowired
    private HiveServer2Manager hiveServer2Manager;

    @Autowired
    private ZookeeperCuratorManager zookeeperCuratorManager;

    @Autowired
    private ZkConfig zkConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseChecker.class);

    public boolean createDataBaseIfNotExist(String databaseName, String hiveMetaAddress, String hiveServer2Address, String hiveSuperUsername, String hiveSuperPassword, String username)
            throws HiveServer2ConnectException, HiveServer2StatementException, ZookeeperLockException, ZooKeeperWaitTimeOutException, HiveMetaStoreConnectException {
        // If database not exist
        LOGGER.info("Start to check if database: {} is exist. url: {}", databaseName, hiveMetaAddress);
        if (!dbExist(hiveMetaAddress, hiveSuperUsername, databaseName)) {
            LOGGER.info("Failed to find database: {} in hive, url: {}. Trying to create database", databaseName, hiveMetaAddress);
            // Try to acquire user lock and create database
            lockAndCreateDatabase(databaseName, hiveMetaAddress, hiveServer2Address, hiveSuperUsername, hiveSuperPassword, username);
            return true;
        }
        return false;
    }

    private boolean dbExist(String hiveMetaAddress, String username, String dbName) throws HiveMetaStoreConnectException {
        // Check existence of database from hive meta store
        HiveMetaStoreClient client = null;
        try {
            client = hiveMetaStoreManager.createHiveMetaClient(hiveMetaAddress, username);

            client.getDatabase(dbName);
        } catch (NoSuchObjectException e) {
            LOGGER.info("Database: {} in [{}] does not exist.", dbName, hiveMetaAddress);
            return false;
        } catch (TException e) {
            throw new HiveMetaStoreConnectException("Failed to connect to hive meta store, address: " + hiveMetaAddress);
        } finally {
            hiveMetaStoreManager.closeClient(client);
        }

        LOGGER.info("Database: {} in [{}] exist, there is no need to create database.", dbName, hiveMetaAddress);
        return true;
    }

    private void lockAndCreateDatabase(String databaseName, String hiveMetaAddress, String hiveServer2Address, String hiveUsername, String hivePassword, String username)
            throws HiveMetaStoreConnectException, HiveServer2ConnectException, HiveServer2StatementException, ZookeeperLockException, ZooKeeperWaitTimeOutException {
        CuratorFramework client = null;
        InterProcessLock lock = null;
        String lockPath = null;
        try {
            client = zookeeperCuratorManager.createClient();
            lockPath = getLockPath(username);
            // Try to acquire lock
            lock = new InterProcessMutex(client, lockPath);

            Boolean lockFlag;
            LOGGER.info("Trying to acquire lock of zk, lock_path: {}", lockPath);
            lockFlag = lock.acquire(zkConfig.getLockWaitTimeMs(), TimeUnit.MILLISECONDS);
            // Failed to acquire lock
            if (!lockFlag) {
                throw new ZooKeeperWaitTimeOutException("Failed to get lock of zk, caused by time out. user: " + username);
            }
            // Succeed to acquire lock, create database if database not exist
            LOGGER.info("Succeed to acquire lock, lock_path: {}", lockPath);
            if (!dbExist(hiveMetaAddress, hiveUsername, databaseName)) {
                LOGGER.info("Failed to find database: {} in hive, url: {}. Trying to create database", databaseName, hiveMetaAddress);
                createDatabase(databaseName, hiveServer2Address, hiveUsername, hivePassword, username);
            } else {
                LOGGER.info("Succeed to find database: {} in hive, url: {}. No need to create database", databaseName, hiveMetaAddress);
            }
        } catch (HiveMetaStoreConnectException e) {
            throw new HiveMetaStoreConnectException("Failed to connect to hive meta store, address: " + hiveMetaAddress);
        } catch (HiveServer2ConnectException e) {
            throw new HiveServer2ConnectException("Failed to connect to hive server2, address: " + hiveServer2Address);
        } catch (HiveServer2StatementException e) {
            throw new HiveServer2StatementException("Failed to create hive server2 statement, address:" + hiveServer2Address);
        }  catch (ZooKeeperWaitTimeOutException e) {
            throw new ZooKeeperWaitTimeOutException("Failed to get lock of zk, cause by time out. user: " + username);
        } catch (Exception e) {
            throw new ZookeeperLockException("Failed to get lock of zk, lock_path: " + lockPath +", caused by: " + e.getMessage());
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                    LOGGER.info("Succeed to release lock. lock_path: {}", lockPath);
                } catch (Exception e) {
                    LOGGER.error("Failed to release lock, caused by {}", e.getMessage(), e);
                }
            }
            zookeeperCuratorManager.closeClient(client);
        }
    }

    private void createDatabase(String dbName, String hiveServer2Address, String hiveUsername, String hivePassword, String username) throws HiveServer2ConnectException, HiveServer2StatementException {
        LOGGER.info("Starting to create database: {} in url: {}", dbName, hiveServer2Address);
        Connection connection = null;
        Statement statement = null;
        try {
            connection = hiveServer2Manager.createConnection(hiveServer2Address, hiveUsername, hivePassword);

            statement = connection.createStatement();
            String createDatabaseStatement = getCreateDatabase(dbName, username);
            LOGGER.info("Start to create database, hql : {}, address: {}", createDatabaseStatement, hiveServer2Address);
            statement.execute(createDatabaseStatement);
            LOGGER.info("Succeed to create database. database: {}, user: {}", dbName, username);

            String grantStatement = getGrantStatement(dbName, username);
            LOGGER.info("Start to grant database to user: {}, hql : {}, address: {}", username, grantStatement, hiveServer2Address);
            statement.execute(getGrantStatement(dbName, username));
            LOGGER.info("Succeed to grant database: {} to user: {}", dbName, username);

            String changeOwnerStatement = getChangeOwner(dbName, username);
            LOGGER.info("Start to change the owner of database., hql : {}, address: {}", changeOwnerStatement, hiveServer2Address);
            statement.execute(getChangeOwner(dbName, username));
            LOGGER.info("Succeed to change the owner of database: {}, owner: {}", dbName, username);
        } catch (SQLException e) {
            throw new HiveServer2StatementException("Failed to create hive server2 statement, address:" + hiveServer2Address);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    LOGGER.error("Failed to shutdown statement. url: {}", hiveServer2Address);
                }
            }
            hiveServer2Manager.releaseConnection(connection);
        }
    }

    private String getCreateDatabase(String dbName, String username) {
        return "create database " + dbName + " location \"/user/hive/warehouse/" + username + "/" + dbName + ".db\"";
    }

    private String getGrantStatement(String dbName, String username) {
        return "grant all on database " + dbName + " to user " + username;
    }

    private String getChangeOwner(String dbName, String username) {
        return "dfs -chown " + username + ":" + username + " /user/hive/warehouse/" + username + "/" + dbName + ".db";
    }

    private String getLockPath(String username) {
        return "/qualitis/tmp/create_db/qualitis" + username + "_tmp_safe";
    }



}
