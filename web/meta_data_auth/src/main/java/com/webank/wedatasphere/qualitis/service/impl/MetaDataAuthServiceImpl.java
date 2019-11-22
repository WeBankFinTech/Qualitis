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

import com.webank.wedatasphere.qualitis.dao.*;
import com.webank.wedatasphere.qualitis.entity.*;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.AddMetaDataAuthRequest;
import com.webank.wedatasphere.qualitis.request.DeleteMetaDataAuthRequest;
import com.webank.wedatasphere.qualitis.request.QueryUserOfMetaDataAuthRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.MetaDataAuthService;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.AddMetaDataAuthRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.MetaDataAuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author howeye
 */
@Service
public class MetaDataAuthServiceImpl implements MetaDataAuthService {

    @Autowired
    private MetaDataAuthDao metaDataAuthDao;

    @Autowired
    private MetaDataClusterDao metaDataClusterDao;
    @Autowired
    private MetaDataDbDao metaDataDbDao;
    @Autowired
    private MetaDataTableDao metaDataTableDao;
    @Autowired
    private MetaDataColumnDao metaDataColumnDao;
    @Autowired
    private UserDao userDao;

    @Override
    public GeneralResponse<?> addMetaDataAuth(AddMetaDataAuthRequest request) throws UnExpectedRequestException {
        // Check Arguments
        AddMetaDataAuthRequest.checkRequest(request);

        // Check existence of user
        User user = userDao.findByUsername(request.getUsername());
        if (user == null) {
            throw new UnExpectedRequestException("User: " + request.getUsername() + " does not exist");
        }

        // Check existence of authorization
        AuthMetaData authMetaDataInDb = metaDataAuthDao.findByAuthTypeAndClusterNameAndDbNameAndTableNameAndColumnNameAndUsernameAndIsOrg(
                request.getAuthType(), request.getClusterName(), request.getDbName(), request.getTableName(),
                request.getColumnName(), request.getUsername(), request.getIsOrg()
        );
        if (authMetaDataInDb != null) {
            throw new UnExpectedRequestException("Meta data auth of user: " + request.getUsername() + " is already exist");
        }

        // Check existence of cluster, database and table
        MetaDataColumn metaDataColumn = checkMetaDataExistence(request);

        // Save auth meta
        AuthMetaData authMetaData = new AuthMetaData(request.getAuthType(), request.getClusterName(),
                request.getDbName(), request.getTableName(), metaDataColumn.getColumnName(), metaDataColumn.getColumnType(), request.getUsername(), request.getIsOrg());
        metaDataAuthDao.saveAuthMetaData(authMetaData);
        return new GeneralResponse<>("200", "Succeed to save auth meta data", authMetaData.getId());
    }

    @Override
    public GeneralResponse<?> deleteMetaDataAuth(DeleteMetaDataAuthRequest request) throws UnExpectedRequestException {
        // Check Arguments
        DeleteMetaDataAuthRequest.checkRequest(request);

        // Check existence of authorization
        AuthMetaData authMetaDataInDb = metaDataAuthDao.findById(request.getAuthId());
        if (authMetaDataInDb == null) {
            throw new UnExpectedRequestException("Auth meta data does not exist, id: " + request.getAuthId());
        }

        metaDataAuthDao.deleteAuthMetaData(authMetaDataInDb);
        return new GeneralResponse<>("200", "Succeed to delete auth meta data", null);
    }

    /**
     * Paging get authorization meta data list
     *
     * @param request
     * @return
     */
    @Override
    public List<AuthMetaData> query(QueryUserOfMetaDataAuthRequest request) {
        return metaDataAuthDao.query(request.getAuthTypes(), request.getClusterName(), request.getDbName(), request.getTableName(), request.getUsername(), request.getPage(), request.getSize());
    }

    /**
     * Paging query total num of authorization
     * @param request
     * @return
     */
    @Override
    public long count(QueryUserOfMetaDataAuthRequest request) {
        return metaDataAuthDao.count(request.getAuthTypes(), request.getClusterName(), request.getDbName(), request.getTableName(), request.getUsername());
    }

    private MetaDataColumn checkMetaDataExistence(AddMetaDataAuthRequest request) throws UnExpectedRequestException {
        MetaDataCluster metaDataCluster;
        MetaDataDb metaDataDb;
        MetaDataTable metaDataTable;
        MetaDataColumn metaDataColumn;
        if (StringUtils.isNotBlank(request.getClusterName())) {
            metaDataCluster = checkClusterAndReturnIfExist(request.getClusterName());

            if (StringUtils.isNotBlank(request.getDbName())) {
                metaDataDb = checkDbAndReturnIfExist(request.getDbName(), metaDataCluster);

                if (StringUtils.isNotBlank(request.getTableName())) {
                    metaDataTable = checkTableAndReturnIfExist(request.getTableName(), metaDataDb);

                    if (StringUtils.isNotBlank(request.getColumnName())) {
                        return metaDataColumn = checkColumnAndReturnIfExist(request.getColumnName(), metaDataTable);
                    }
                }
            }
        }
        MetaDataColumn tmp = new MetaDataColumn();
        tmp.setColumnName("");
        tmp.setColumnType("");
        return tmp;
    }

    private MetaDataColumn checkColumnAndReturnIfExist(String columnName, MetaDataTable metaDataTable) throws UnExpectedRequestException {
        MetaDataColumn metaDataColumn = metaDataColumnDao.findByColumnNameAndTable(columnName, metaDataTable);

        if (metaDataColumn == null) {
            throw new UnExpectedRequestException("Cluster :" + metaDataTable.getMetaDataDb().getMetaDataCluster().getClusterName() +
                    " Db name: " + metaDataTable.getMetaDataDb().getDbName() +
                    " Table name: " + columnName + " does not exist");
        }
        return metaDataColumn;
    }

    private MetaDataTable checkTableAndReturnIfExist(String tableName, MetaDataDb metaDataDb) throws UnExpectedRequestException {
        MetaDataTable metaDataTable = metaDataTableDao.findByTableNameAndDb(tableName, metaDataDb);

        if (metaDataTable == null) {
            throw new UnExpectedRequestException("Cluster :" + metaDataDb.getMetaDataCluster().getClusterName() +
                    " Db name: " + metaDataDb.getDbName() +
                    " Table name: " + tableName + " does not exist");
        }
        return metaDataTable;
    }

    private MetaDataDb checkDbAndReturnIfExist(String dbName, MetaDataCluster metaDataCluster) throws UnExpectedRequestException {
        MetaDataDb metaDataDb = metaDataDbDao.findByDbNameAndCluster(dbName, metaDataCluster);

        if (metaDataDb == null) {
            throw new UnExpectedRequestException("Cluster :" + metaDataCluster.getClusterName() +
                    " Db name: " + dbName + " does not exist");
        }
        return metaDataDb;
    }

    private MetaDataCluster checkClusterAndReturnIfExist(String clusterName) throws UnExpectedRequestException {
        MetaDataCluster metaDataCluster = metaDataClusterDao.findByClusterName(clusterName);
        if (metaDataCluster == null) {
            throw new UnExpectedRequestException("Cluster :" + clusterName + " does not exist");
        }

        return metaDataCluster;
    }
}
