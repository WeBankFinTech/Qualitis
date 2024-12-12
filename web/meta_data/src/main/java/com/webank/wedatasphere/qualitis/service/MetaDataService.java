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

package com.webank.wedatasphere.qualitis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.TaskNotExistException;
import com.webank.wedatasphere.qualitis.metadata.response.table.CsTableInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserColumnByCsRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserTableByCsIdRequest;
import com.webank.wedatasphere.qualitis.request.DataSourceConnectRequest;
import com.webank.wedatasphere.qualitis.request.DataSourceModifyRequest;
import com.webank.wedatasphere.qualitis.request.DataSourceParamModifyRequest;
import com.webank.wedatasphere.qualitis.request.GetUserTableByDbIdRequest;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.GetUserClusterRequest;
import com.webank.wedatasphere.qualitis.request.GetUserColumnByTableIdRequest;
import com.webank.wedatasphere.qualitis.request.GetUserDbByClusterRequest;
import com.webank.wedatasphere.qualitis.request.MulDbRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllClusterResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.metadata.response.cluster.ClusterInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.db.DbInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableInfoDetail;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * @author howeye
 */
public interface MetaDataService {

    /**
     * Get Database by user and cluster
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<GetAllResponse<DbInfoDetail>> getUserDbByCluster(GetUserDbByClusterRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get Table by user and db
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<GetAllResponse<TableInfoDetail>> getUserTableByDbId(GetUserTableByDbIdRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get Table by context service ID
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<GetAllResponse<CsTableInfoDetail>> getUserTableByCsId(GetUserTableByCsIdRequest request)
        throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     Get Column by user and table
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<GetAllResponse<ColumnInfoDetail>> getUserColumnByTableId(GetUserColumnByTableIdRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     Get Column by context service ID
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<GetAllResponse<ColumnInfoDetail>> getUserColumnByCsId(GetUserColumnByCsRequest request)
        throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get Cluster by user
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<GetAllClusterResponse<ClusterInfoDetail>> getUserCluster(GetUserClusterRequest request)
        throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Add multi-db rules in batch.
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws ClusterInfoNotConfigException
     * @throws TaskNotExistException
     * @throws IOException
     */
    String addMultiDbRules(MulDbRequest request)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, ClusterInfoNotConfigException, TaskNotExistException, IOException;

    /**
     * Get all data source types.
     * @param clusterName
     * @param proxyUser
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map> getAllDataSourceTypes(String clusterName, String proxyUser)
        throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get data source env.
     * @param clusterName
     * @param proxyUser
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map> getDataSourceEnv(String clusterName, String proxyUser) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get data source info pageable.
     * @param clusterName
     * @param proxyUser
     * @param page
     * @param size
     * @param searchName
     * @param typeId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws UnsupportedEncodingException
     */
    GeneralResponse<Map> getDataSourceInfoPage(String clusterName, String proxyUser, int page, int size, String searchName,
        Long typeId) throws UnExpectedRequestException, MetaDataAcquireFailedException, UnsupportedEncodingException;

    /**
     * Get data source versions.
     * @param clusterName
     * @param proxyUser
     * @param dataSourceId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map> getDataSourceVersions(String clusterName, String proxyUser, Long dataSourceId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get data source info detail.
     * @param clusterName
     * @param proxyUser
     * @param dataSourceId
     * @param versionId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map> getDataSourceInfoDetail(String clusterName, String proxyUser, Long dataSourceId, Long versionId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get data source key define.
     * @param clusterName
     * @param proxyUser
     * @param keyId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map> getDataSourceKeyDefine(String clusterName, String proxyUser, Long keyId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Connect to data source.
     * @param clusterName
     * @param proxyUser
     * @param request
     * @return
     * @throws IOException
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map> connectDataSource(String clusterName, String proxyUser, DataSourceConnectRequest request)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, IOException;

    /**
     * Publish data source.
     * @param clusterName
     * @param proxyUser
     * @param dataSourceId
     * @param versionId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map> publishDataSource(String clusterName, String proxyUser, Long dataSourceId, Long versionId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Expire data source.
     * @param clusterName
     * @param proxyUser
     * @param dataSourceId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map> expireDataSource(String clusterName, String proxyUser, Long dataSourceId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Modify data source.
     * @param clusterName
     * @param proxyUser
     * @param dataSourceId
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws JsonProcessingException
     */
    GeneralResponse<Map> modifyDataSource(String clusterName, String proxyUser, Long dataSourceId, DataSourceModifyRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException, JsonProcessingException;

    /**
     * Modify data source param.
     * @param clusterName
     * @param proxyUser
     * @param dataSourceId
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws IOException
     */
    GeneralResponse<Map> modifyDataSourceParam(String clusterName, String proxyUser, Long dataSourceId, DataSourceParamModifyRequest request)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, IOException;

    /**
     * Create data source param.
     * @param clusterName
     * @param proxyUser
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws JsonProcessingException
     */
    GeneralResponse<Map> createDataSource(String clusterName, String proxyUser, DataSourceModifyRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException, JsonProcessingException;

    /**
     * Get db by data source.
     * @param clusterName
     * @param proxyUser
     * @param dataSourceId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    Map getDbsByDataSource(String clusterName, String proxyUser, Long dataSourceId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get table by data source.
     * @param clusterName
     * @param proxyUser
     * @param dataSourceId
     * @param dbName
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    Map getTablesByDataSource(String clusterName, String proxyUser, Long dataSourceId, String dbName) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get column by data source.
     * @param clusterName
     * @param proxyUser
     * @param dataSourceId
     * @param dbName
     * @param tableName
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<GetAllResponse<ColumnInfoDetail>> getColumnsByDataSource(String clusterName, String proxyUser, Long dataSourceId, String dbName, String tableName) throws UnExpectedRequestException, MetaDataAcquireFailedException;
}
