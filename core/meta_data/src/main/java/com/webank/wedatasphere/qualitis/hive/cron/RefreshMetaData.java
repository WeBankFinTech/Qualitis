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

package com.webank.wedatasphere.qualitis.hive.cron;

import com.webank.wedatasphere.qualitis.dao.*;
import com.webank.wedatasphere.qualitis.entity.*;
import com.webank.wedatasphere.qualitis.hive.dao.HiveMetaDataDao;
import com.webank.wedatasphere.qualitis.hive.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.hive.exception.ConnectionAcquireFailedException;
import com.webank.wedatasphere.qualitis.hive.model.HiveColumn;
import com.webank.wedatasphere.qualitis.zk.ZookeeperCuratorManager;
import com.webank.wedatasphere.qualitis.entity.MetaDataCluster;
import com.webank.wedatasphere.qualitis.entity.MetaDataColumn;
import com.webank.wedatasphere.qualitis.entity.MetaDataDb;
import com.webank.wedatasphere.qualitis.entity.MetaDataTable;
import com.webank.wedatasphere.qualitis.hive.model.HiveColumn;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Component
@Configuration
public class RefreshMetaData implements Job {

    @Value("${meta-data.refresh-cron}")
    private String refreshCron;

    @Value("${meta-data.lock-path}")
    private String lockPath;

    @Value("${meta-data.lock-time}")
    private Long lockTime;

    @Autowired
    private ClusterInfoDao clusterInfoDao;
    @Autowired
    private HiveMetaDataDao hiveMetaDataDao;
    @Autowired
    private MetaDataClusterDao metaDataClusterDao;
    @Autowired
    private MetaDataDbDao metaDataDbDao;
    @Autowired
    private MetaDataTableDao metaDataTableDao;
    @Autowired
    private MetaDataColumnDao metaDataColumnDao;
    @Autowired
    private ZookeeperCuratorManager zookeeperCuratorManager;

    private InterProcessLock lock;
    private Boolean lockFlag = false;

    private static final String TRIGGER_NAME = "Meta data refresh trigger";
    private static final String TRIGGER_GROUP = "Meta data refresh trigger group";
    private static final String JOB_NAME = "Meta data refresh job";
    private static final String JOB_GROUP = "Meta data refresh job group";

    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshMetaData.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // Find all config
        receiveParam(context);

        try {
            // Get zookeeper lock
            if (acquireLock()) {
                LOGGER.info("Start to refresh meta data");
                List<String> allClusters = clusterInfoDao.findAllClusterInfo(0, Integer.MAX_VALUE).
                        stream().map(ClusterInfo::getClusterName).collect(Collectors.toList());
                deleteLeftCluster(allClusters);
                try {
                    for (String clusterName : allClusters) {
                        LOGGER.info("Start to refresh meta data of cluster: {}", clusterName);
                        MetaDataCluster metaDataCluster = metaDataClusterDao.saveMetaDataCluster(new MetaDataCluster(clusterName));
                        getAndSaveDbs(metaDataCluster);
                        LOGGER.info("Finish to refresh meta data of cluster: {}", clusterName);
                    }
                } catch (ConnectionAcquireFailedException | ClusterInfoNotConfigException e) {
                    LOGGER.error("Failed to execute sql", e);
                    throw new JobExecutionException(e.getMessage(), e);
                } catch (SQLException e) {
                    LOGGER.error("Failed to execute sql", e);
                    throw new JobExecutionException("Failed to execute sql", e);
                }
                LOGGER.info("Finish to refresh meta data");
            }
        } finally {
            if (lockFlag) {
                try {
                    lock.release();
                } catch (Exception e) {
                    LOGGER.error("Failed to release lock of zookeeper", e);
                }
            }
        }
    }

    private Boolean acquireLock() {
        try {
            LOGGER.info("Trying to acquire lock of zk, lock_path: {}, lock: {}", lockPath, lock.hashCode());
            lockFlag = lock.acquire(lockTime, TimeUnit.SECONDS);
            if (lockFlag) {
                LOGGER.info("Succeed to acquire lock of zk, lock_path: {}, lock: {}", lockPath, lock.hashCode());
            } else {
                LOGGER.warn("Failed to acquire lock of zk, caused by timeout. lock_path: {}, lock: {}", lockPath, lock.hashCode());
            }
            return lockFlag;
        } catch (Exception e) {
            LOGGER.error("Failed to get lock of zk, lock_path: {}, caused by: {}", lockPath, e.getMessage(), e);
        }
        return false;
    }

    private void receiveParam(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        clusterInfoDao = (ClusterInfoDao) jobDataMap.get("clusterInfoDao");
        hiveMetaDataDao = (HiveMetaDataDao) jobDataMap.get("hiveMetaDataDao");
        metaDataClusterDao = (MetaDataClusterDao) jobDataMap.get("metaDataClusterDao");
        metaDataDbDao = (MetaDataDbDao) jobDataMap.get("metaDataDbDao");
        metaDataTableDao = (MetaDataTableDao) jobDataMap.get("metaDataTableDao");
        metaDataColumnDao = (MetaDataColumnDao) jobDataMap.get("metaDataColumnDao");
        lock = (InterProcessLock) jobDataMap.get("lock");
        lockTime = (Long) jobDataMap.get("lockTime");
    }

    private void getAndSaveDbs(MetaDataCluster metaDataCluster) throws ConnectionAcquireFailedException, SQLException, ClusterInfoNotConfigException {
        List<String> dbs = hiveMetaDataDao.getHiveDbByCluster(metaDataCluster.getClusterName());
        deleteLeftDb(dbs, metaDataCluster);
        for (String db : dbs) {
            LOGGER.info("Start to refresh meta data of cluster : [{}] db: [{}]", metaDataCluster.getClusterName(), db);
            MetaDataDb metaDataDbInDb = metaDataDbDao.findByDbNameAndCluster(db, metaDataCluster);
            if (metaDataDbInDb == null) {
                metaDataDbInDb = metaDataDbDao.saveMetaDataDb(new MetaDataDb(metaDataCluster, db));
            }
            getAndSaveTables(metaDataDbInDb);
            LOGGER.info("Finish to refresh meta data of cluster: [{}], db: [{}]", metaDataCluster.getClusterName(), db);
        }
    }

    public void getAndSaveTables(MetaDataDb metaDataDb) throws ConnectionAcquireFailedException, SQLException, ClusterInfoNotConfigException {
        List<String> tables = hiveMetaDataDao.getHiveTableByClusterAndDb(metaDataDb.getMetaDataCluster().getClusterName(), metaDataDb.getDbName());
        deleteLeftTable(tables, metaDataDb);
        for (String table : tables) {
            MetaDataTable metaDataTableInDb = metaDataTableDao.findByTableNameAndDb(table, metaDataDb);
            if (metaDataTableInDb == null) {
                metaDataTableInDb = metaDataTableDao.saveMeteDataTable(new MetaDataTable(metaDataDb, table));
            }
            getAndSaveColumns(metaDataTableInDb);
        }
    }

    private void getAndSaveColumns(MetaDataTable metaDataTable) throws ConnectionAcquireFailedException, SQLException, ClusterInfoNotConfigException {
        List<HiveColumn> columns = hiveMetaDataDao.getHiveColumnByClusterAndDbAndTable(
                metaDataTable.getMetaDataDb().getMetaDataCluster().getClusterName(),
                metaDataTable.getMetaDataDb().getDbName(), metaDataTable.getTableName());
        List<String> columnNames = columns.stream().map(HiveColumn::getColumnName).collect(Collectors.toList());
        deleteLeftColumn(columnNames, metaDataTable);
        for (HiveColumn column : columns) {
            MetaDataColumn metaDataColumnInDb = metaDataColumnDao.findByColumnNameAndTable(column.getColumnName(), metaDataTable);
            if (metaDataColumnInDb == null) {
                metaDataColumnDao.saveMetaDataColumn(new MetaDataColumn(metaDataTable, column.getColumnName(), column.getColumnType()));
            }
        }
    }

    @PostConstruct
    public void schedule() {
        CuratorFramework client = zookeeperCuratorManager.createClient();
        lock = new InterProcessMutex(client, lockPath);

        JobDetail jobDetail = JobBuilder
                .newJob(RefreshMetaData.class)
                .withIdentity(JOB_NAME, JOB_GROUP)
                .build();

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(TRIGGER_NAME, TRIGGER_GROUP)
                .withSchedule(CronScheduleBuilder
                        .cronSchedule(refreshCron)
                ).build();

        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        jobDataMap.put("clusterInfoDao", clusterInfoDao);
        jobDataMap.put("hiveMetaDataDao", hiveMetaDataDao);
        jobDataMap.put("metaDataClusterDao", metaDataClusterDao);
        jobDataMap.put("metaDataDbDao", metaDataDbDao);
        jobDataMap.put("metaDataTableDao", metaDataTableDao);
        jobDataMap.put("metaDataColumnDao", metaDataColumnDao);
        jobDataMap.put("lock", lock);
        jobDataMap.put("lockTime", lockTime);

        try {
            SchedulerFactory factory = new StdSchedulerFactory();
            Scheduler scheduler = factory.getScheduler();
            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
        } catch (SchedulerException e) {
            LOGGER.error("Stopping server! Failed to scheduler meta data refresher", e);
            System.exit(-1);
        }
    }

    /**
     * Return data in list2 but not in list1
     * @param list1
     * @param list2
     * @return
     */
    public List<String> notContains(List<String> list1, List<String> list2) {
        List<String> notContainsList = new ArrayList<>();
        for (String str : list2) {
            if (!list1.contains(str)) {
                notContainsList.add(str);
            }
        }
        return notContainsList;
    }

    public List<String> deleteLeftCluster(List<String> allClusters) {
        List<String> allSavedClusters = metaDataClusterDao.findAll().stream().map(MetaDataCluster::getClusterName).collect(Collectors.toList());
        List<String> shouldDeleteClusters = notContains(allClusters, allSavedClusters);
        for (String shouldDeleteCluster : shouldDeleteClusters) {
            MetaDataCluster metaDataCluster = metaDataClusterDao.findByClusterName(shouldDeleteCluster);
            metaDataClusterDao.deleteMetaDataCluster(metaDataCluster);
        }
        return shouldDeleteClusters;
    }

    public List<String> deleteLeftDb(List<String> allDbs, MetaDataCluster metaDataCluster) {
        List<String> allSavedDb = metaDataDbDao.findAllByCluster(metaDataCluster).stream()
                .map(MetaDataDb::getDbName).collect(Collectors.toList());
        List<String> shouldDeleteDbs = notContains(allDbs, allSavedDb);
        for (String shouldDeleteDb : shouldDeleteDbs) {
            MetaDataDb metaDataDbInDb = metaDataDbDao.findByDbNameAndCluster(shouldDeleteDb, metaDataCluster);
            metaDataDbDao.deleteMetaDataDb(metaDataDbInDb);
        }
        return shouldDeleteDbs;
    }

    public List<String> deleteLeftTable(List<String> allTables, MetaDataDb metaDataDb) {
        List<String> allSavedTable = metaDataTableDao.findAllByMetaDataDb(metaDataDb).stream()
                .map(MetaDataTable::getTableName).collect(Collectors.toList());
        List<String> shouldDeleteTables = notContains(allTables, allSavedTable);
        for (String shouldDeleteTable : shouldDeleteTables) {
            MetaDataTable metaDataTableInDb = metaDataTableDao.findByTableNameAndDb(shouldDeleteTable, metaDataDb);
            metaDataTableDao.deleteMetaDataTable(metaDataTableInDb);
        }
        return shouldDeleteTables;
    }

    public List<String> deleteLeftColumn(List<String> allColumns, MetaDataTable metaDataTable) {
        List<String> allSavedColumn = metaDataColumnDao.findAllByMetaDataTable(metaDataTable).stream()
                .map(MetaDataColumn::getColumnName).collect(Collectors.toList());
        List<String> shouldDeleteColumns = notContains(allColumns, allSavedColumn);
        for (String shouldDeleteColumn : shouldDeleteColumns) {
            MetaDataColumn metaDataColumn = metaDataColumnDao.findByColumnNameAndTable(shouldDeleteColumn, metaDataTable);
            metaDataColumnDao.deleteMetaDataColumn(metaDataColumn);
        }
        return shouldDeleteColumns;
    }
}
