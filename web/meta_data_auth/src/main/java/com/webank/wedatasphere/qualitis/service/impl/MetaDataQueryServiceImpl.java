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

import com.webank.wedatasphere.qualitis.dao.MetaDataClusterDao;
import com.webank.wedatasphere.qualitis.dao.MetaDataColumnDao;
import com.webank.wedatasphere.qualitis.dao.MetaDataDbDao;
import com.webank.wedatasphere.qualitis.dao.MetaDataTableDao;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.entity.MetaDataCluster;
import com.webank.wedatasphere.qualitis.entity.MetaDataColumn;
import com.webank.wedatasphere.qualitis.entity.MetaDataDb;
import com.webank.wedatasphere.qualitis.entity.MetaDataTable;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.QueryMetaColumnRequest;
import com.webank.wedatasphere.qualitis.request.QueryMetaDbRequest;
import com.webank.wedatasphere.qualitis.request.QueryMetaTableRequest;
import com.webank.wedatasphere.qualitis.service.MetaDataQueryService;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.QueryMetaColumnRequest;
import com.webank.wedatasphere.qualitis.request.QueryMetaDbRequest;
import com.webank.wedatasphere.qualitis.request.QueryMetaTableRequest;
import com.webank.wedatasphere.qualitis.service.MetaDataQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author v_wblwyan
 * @date 2019-4-18
 */
@Service
public class MetaDataQueryServiceImpl implements MetaDataQueryService {

    @Autowired
    private MetaDataClusterDao metaDataClusterDao;
    @Autowired
    private MetaDataDbDao metaDataDbDao;
    @Autowired
    private MetaDataTableDao metaDataTableDao;
    @Autowired
    private MetaDataColumnDao metaDataColumnDao;

    /**
     * Paging get cluster information
     * @param request
     * @return
     */
    @Override
    public List<MetaDataCluster> queryCluster(PageRequest request) {
        return metaDataClusterDao.queryByPage(request.getPage(), request.getSize());
    }

    /**
     * Paging get database information
     * @param request
     * @return
     */
    @Override
    public List<MetaDataDb> queryDb(QueryMetaDbRequest request) throws UnExpectedRequestException {
        MetaDataCluster metaDataCluster = checkClusterNameExists(request.getClusterName());
        return metaDataDbDao.queryPageByCluster(metaDataCluster, request.getPage(), request.getSize());
    }

    /**
     * Paging get table information
     * @param request
     * @return
     */
    @Override
    public List<MetaDataTable> queryTable(QueryMetaTableRequest request)
        throws UnExpectedRequestException {
        MetaDataDb metaDataDb = checkClusterAndDbNameExists(request.getClusterName(), request.getDbName());
        return metaDataTableDao.queryPageByDb(metaDataDb, request.getPage(), request.getSize());
    }

    /**
     * Paging get field information
     * @param request
     * @return
     */
    @Override
    public List<MetaDataColumn> queryColumn(QueryMetaColumnRequest request)
        throws UnExpectedRequestException {
        MetaDataTable metaDataTable = checkClusterAndDbAndTableNameExists(request.getClusterName(), request.getDbName(), request.getTableName());
        return metaDataColumnDao.queryPageByTable(metaDataTable, request.getPage(), request.getSize());
    }

    /**
     * Get total num of cluster
     * @return
     */
    @Override
    public long countClusterAll() {
        return metaDataClusterDao.countAll();
    }

    /**
     * Get total num of database in cluster
     *
     * @param request
     * @return
     */
    @Override
    public long countDbByCluster(QueryMetaDbRequest request) throws UnExpectedRequestException {
        MetaDataCluster metaDataCluster = checkClusterNameExists(request.getClusterName());
        return metaDataDbDao.countByCluster(metaDataCluster);
    }

    /**
     * Get total num of table in database
     * @param request
     * @return
     */
    @Override
    public long countTableByDb(QueryMetaTableRequest request) throws UnExpectedRequestException {
        MetaDataDb metaDataDb = checkClusterAndDbNameExists(request.getClusterName(), request.getDbName());
        return metaDataTableDao.countByDb(metaDataDb);
    }

    /**
     * Get total num of field in table
     * @param request
     * @return
     */
    @Override
    public long countColumnByTable(QueryMetaColumnRequest request)
        throws UnExpectedRequestException {
        MetaDataTable metaDataTable = checkClusterAndDbAndTableNameExists(request.getClusterName(), request.getDbName(), request.getTableName());
        return metaDataColumnDao.countByTable(metaDataTable);
    }


    private MetaDataCluster checkClusterNameExists(String clusterName) throws
        UnExpectedRequestException {
        MetaDataCluster metaDataCluster = metaDataClusterDao.findByClusterName(clusterName);
        if (metaDataCluster == null) {
            throw new UnExpectedRequestException(String.format("%s 集群名称不存在", clusterName));
        }
        return metaDataCluster;
    }

    private MetaDataDb checkClusterAndDbNameExists(String clusterName, String dbName) throws
        UnExpectedRequestException {
        MetaDataCluster metaDataCluster = checkClusterNameExists(clusterName);
        MetaDataDb metaDataDb = metaDataDbDao.findByDbNameAndCluster(dbName, metaDataCluster);
        if (metaDataDb == null) {
            throw new UnExpectedRequestException(String.format("%s.%s 集群-库关系不存在", clusterName, dbName));
        }
        return metaDataDb;
    }

    private MetaDataTable checkClusterAndDbAndTableNameExists(String clusterName, String dbName,
        String tableName) throws
        UnExpectedRequestException {
        MetaDataDb metaDataDb = checkClusterAndDbNameExists(clusterName, dbName);
        MetaDataTable metaDataTable = metaDataTableDao.findByTableNameAndDb(tableName, metaDataDb);
        if (metaDataTable == null) {
            throw new UnExpectedRequestException(String.format("%s.%s.%s 集群-库-表关系不存在", clusterName, dbName, tableName));
        }
        return metaDataTable;
    }
}
