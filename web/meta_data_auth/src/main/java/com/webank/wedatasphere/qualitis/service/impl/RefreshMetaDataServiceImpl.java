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

package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.dao.ClusterInfoDao;
import com.webank.wedatasphere.qualitis.dao.MetaDataClusterDao;
import com.webank.wedatasphere.qualitis.dao.MetaDataDbDao;
import com.webank.wedatasphere.qualitis.entity.ClusterInfo;
import com.webank.wedatasphere.qualitis.entity.MetaDataCluster;
import com.webank.wedatasphere.qualitis.entity.MetaDataDb;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.hive.cron.RefreshMetaData;
import com.webank.wedatasphere.qualitis.hive.dao.HiveMetaDataDao;
import com.webank.wedatasphere.qualitis.hive.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.hive.exception.ConnectionAcquireFailedException;
import com.webank.wedatasphere.qualitis.request.RefreshDbByClusterRequest;
import com.webank.wedatasphere.qualitis.request.RefreshTableByClusterAndDbRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.RefreshResponse;
import com.webank.wedatasphere.qualitis.service.RefreshMetaDataService;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.hive.cron.RefreshMetaData;
import com.webank.wedatasphere.qualitis.request.RefreshDbByClusterRequest;
import com.webank.wedatasphere.qualitis.request.RefreshTableByClusterAndDbRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.RefreshMetaDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Service
public class RefreshMetaDataServiceImpl implements RefreshMetaDataService {

    @Autowired
    private ClusterInfoDao clusterInfoDao;

    @Autowired
    private HiveMetaDataDao hiveMetaDataDao;

    @Autowired
    private MetaDataClusterDao metaDataClusterDao;

    @Autowired
    private MetaDataDbDao metaDataDbDao;

    @Autowired
    private RefreshMetaData refreshMetaData;

    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshMetaDataServiceImpl.class);

    @Override
    public GeneralResponse<?> refreshCluster() throws UnExpectedRequestException {
        List<ClusterInfo> clusterInfos = clusterInfoDao.findAllClusterInfo(0, Integer.MAX_VALUE);
        List<MetaDataCluster> metaDataClusters = metaDataClusterDao.findAll();
        List<String> savedClusterNames = metaDataClusters.stream().map(MetaDataCluster::getClusterName).collect(Collectors.toList());
        List<String> refreshClusterNames = clusterInfos.stream().map(ClusterInfo::getClusterName).collect(Collectors.toList());

        LOGGER.info("Start to refresh cluster, all cluster: {}", refreshClusterNames);
        List<String> addClusters = refreshMetaData.notContains(savedClusterNames, refreshClusterNames);
        LOGGER.info("Succeed to find cluster that should be added: {}", addClusters);
        for (String addCluster : addClusters) {
            metaDataClusterDao.saveMetaDataCluster(new MetaDataCluster(addCluster));
        }

        List<String> removeClusters = refreshMetaData.deleteLeftCluster(refreshClusterNames);
        LOGGER.info("Succeed to find cluster that should be removed: {}", removeClusters);
        RefreshResponse refreshResponse = new RefreshResponse(addClusters, removeClusters);

        return new GeneralResponse<>("200", "Succeed to refresh cluster", refreshResponse);
    }

    @Override
    public GeneralResponse<?> refreshDbByCluster(RefreshDbByClusterRequest request) throws UnExpectedRequestException, ConnectionAcquireFailedException, SQLException, ClusterInfoNotConfigException {
        // Check Arguments
        RefreshDbByClusterRequest.checkRequest(request);

        // Check existence of cluster
        MetaDataCluster metaDataClusterInDb = metaDataClusterDao.findByClusterName(request.getClusterName());
        if (metaDataClusterInDb == null) {
            throw new UnExpectedRequestException("Cluster : " + request.getClusterName() + " does not exist");
        }

        // Find database by cluster
        LOGGER.info("Start to refresh db");
        List<String> refreshDbNames = hiveMetaDataDao.getHiveDbByCluster(request.getClusterName());
        LOGGER.info("Succeed to get all db by cluster. db: {}", refreshDbNames);
        List<String> savedDbs = metaDataDbDao.findAllByCluster(metaDataClusterInDb).stream().map(MetaDataDb::getDbName).collect(Collectors.toList());

        List<String> addDbs = refreshMetaData.notContains(savedDbs, refreshDbNames);
        LOGGER.info("Succeed to find dbs that should be added. db: {}", addDbs);
        for (String addDb : addDbs) {
            metaDataDbDao.saveMetaDataDb(new MetaDataDb(metaDataClusterInDb, addDb));
        }

        List<String> removeDbs = refreshMetaData.deleteLeftDb(refreshDbNames, metaDataClusterInDb);
        LOGGER.info("Succeed to find dbs that should be removed. db: {}", removeDbs);
        RefreshResponse refreshResponse = new RefreshResponse(addDbs, removeDbs);
        return new GeneralResponse<>("200", "Succeed to refresh cluster", refreshResponse);
    }

    @Override
    public GeneralResponse<?> refreshTableByClusterAndDb(RefreshTableByClusterAndDbRequest request) throws UnExpectedRequestException, ConnectionAcquireFailedException, SQLException, ClusterInfoNotConfigException {
        // Check Arguments
        RefreshTableByClusterAndDbRequest.checkRequest(request);

        // Check existence of cluster
        MetaDataCluster metaDataClusterInDb = metaDataClusterDao.findByClusterName(request.getClusterName());
        if (metaDataClusterInDb == null) {
            throw new UnExpectedRequestException("Cluster : " + request.getClusterName() + " does not exist");
        }

        // Check existence of database
        MetaDataDb metaDataDbInDb = metaDataDbDao.findByDbNameAndCluster(request.getDbName(), metaDataClusterInDb);
        if (metaDataDbInDb == null) {
            throw new UnExpectedRequestException("Cluster : " + request.getClusterName() + "Db: " + request.getDbName() + " does not exist");
        }

        // Refresh table and column
        LOGGER.info("Start to refresh table and column");
        refreshMetaData.getAndSaveTables(metaDataDbInDb);
        LOGGER.info("Finish to refresh table and column");
        return new GeneralResponse<>("200", "Succeed to refresh table and column", null);
    }
}
