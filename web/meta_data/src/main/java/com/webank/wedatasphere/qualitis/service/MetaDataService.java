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
import com.webank.wedatasphere.qualitis.constant.DepartmentSourceTypeEnum;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.TaskNotExistException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserColumnByCsRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserTableByCsIdRequest;
import com.webank.wedatasphere.qualitis.metadata.response.CmdbDepartmentResponse;
import com.webank.wedatasphere.qualitis.metadata.response.DepartmentSubResponse;
import com.webank.wedatasphere.qualitis.metadata.response.cluster.ClusterInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.db.DbInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.CsTableInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableInfoDetail;
import com.webank.wedatasphere.qualitis.request.DataSourceConnectRequest;
import com.webank.wedatasphere.qualitis.request.DataSourceModifyRequest;
import com.webank.wedatasphere.qualitis.request.DataSourceParamModifyRequest;
import com.webank.wedatasphere.qualitis.request.GetUserClusterRequest;
import com.webank.wedatasphere.qualitis.request.GetUserColumnByTableIdRequest;
import com.webank.wedatasphere.qualitis.request.GetUserDbByClusterRequest;
import com.webank.wedatasphere.qualitis.request.GetUserTableByDbIdRequest;
import com.webank.wedatasphere.qualitis.request.MulDbRequest;
import com.webank.wedatasphere.qualitis.request.QueryDataSourceRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllClusterResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;
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
     * @throws Exception
     */
    GeneralResponse<GetAllResponse<CsTableInfoDetail>> getUserTableByCsId(GetUserTableByCsIdRequest request)
        throws Exception;

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
     * @throws Exception
     */
    GeneralResponse<GetAllResponse<ColumnInfoDetail>> getUserColumnByCsId(GetUserColumnByCsRequest request)
            throws Exception;

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
     * Get db from datamap
     * @param searchKey
     * @param clusterName
     * @param proxyUser
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    String getDbFromDatamap(String searchKey, String clusterName, String proxyUser) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get dataset from datamap
     * @param dbId
     * @param datasetName
     * @param clusterName
     * @param proxyUser
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    Integer getDatasetFromDatamap(String dbId, String datasetName, String clusterName, String proxyUser) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get column from datamap
     * @param datasetId
     * @param fieldName
     * @param proxyUser
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    Map<String, Object> getColumnFromDatamap(Long datasetId, String fieldName, String proxyUser) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get standard from datamap
     * @param stdCode
     * @param source
     * @param proxyUser
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    Map<String, Object> getDataStandardDetailFromDatamap(String stdCode, String source, String proxyUser) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Add multi-db rules in batch.
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws ClusterInfoNotConfigException
     * @throws TaskNotExistException
     * @throws IOException
     * @throws Exception
     */
    String addMultiDbRules(MulDbRequest request)
            throws Exception;

    /**
     * Get all data source types.
     * @param clusterName
     * @param proxyUser
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map<String, Object>> getAllDataSourceTypes(String clusterName, String proxyUser)
        throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get data source env.
     * @param clusterName
     * @param proxyUser
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map<String, Object>> getDataSourceEnv(String clusterName, String proxyUser) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get data source info pageable by advance condition
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws IOException
     */
    GeneralResponse<Map<String, Object>> getDataSourceInfoWithAdvance(QueryDataSourceRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException, IOException;

    /**
     * Get data source versions.
     * @param clusterName
     * @param proxyUser
     * @param dataSourceId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map<String, Object>> getDataSourceVersions(String clusterName, String proxyUser, Long dataSourceId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get data source info detail.
     * @param clusterName
     * @param proxyUser
     * @param dataSourceId
     * @param versionId
     * @return
     * @throws Exception
     */
    GeneralResponse<Map<String, Object>> getDataSourceInfoDetail(String clusterName, String proxyUser, Long dataSourceId, Long versionId) throws Exception;

    /**
     * Get data source key define.
     * @param clusterName
     * @param proxyUser
     * @param keyId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map<String, Object>> getDataSourceKeyDefine(String clusterName, String proxyUser, Long keyId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Connect to data source.
     * @param clusterName
     * @param proxyUser
     * @param request
     * @return
     * @throws IOException
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws JSONException
     */
    GeneralResponse<Map<String, Object>> connectDataSource(String clusterName, String proxyUser, DataSourceConnectRequest request)
            throws UnExpectedRequestException, MetaDataAcquireFailedException, IOException, JSONException;

    /**
     * Publish data source.
     * @param clusterName
     * @param proxyUser
     * @param dataSourceId
     * @param versionId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws JSONException
     */
    GeneralResponse<Map<String, Object>> publishDataSource(String clusterName, String proxyUser, Long dataSourceId, Long versionId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Expire data source.
     * @param clusterName
     * @param proxyUser
     * @param dataSourceId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map<String, Object>> expireDataSource(String clusterName, String proxyUser, Long dataSourceId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Modify data source.
     * @param clusterName
     * @param dataSourceId
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws JsonProcessingException
     * @throws JSONException
     * @throws IOException
     * @throws Exception
     */
    GeneralResponse modifyDataSource(String clusterName, Long dataSourceId, DataSourceModifyRequest request)
            throws Exception;

    /**
     * Modify data source param.
     * @param clusterName
     * @param dataSourceId
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws IOException
     * @throws JSONException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse modifyDataSourceParam(String clusterName, Long dataSourceId, DataSourceParamModifyRequest request)
            throws UnExpectedRequestException, MetaDataAcquireFailedException, IOException, JSONException, PermissionDeniedRequestException;

    /**
     * Create data source param.
     * @param clusterName
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws JsonProcessingException
     * @throws JSONException
     * @throws IOException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse createDataSource(String clusterName, DataSourceModifyRequest request)
            throws UnExpectedRequestException, MetaDataAcquireFailedException, IOException, JSONException, PermissionDeniedRequestException;

    /**
     * Get db by data source.
     * @param clusterName
     * @param proxyUser
     * @param dataSourceId
     * @param envId
     * @return
     * @throws Exception
     */
    Map<String, Object> getDbsByDataSource(String clusterName, String proxyUser, Long dataSourceId, Long envId) throws Exception;

    /**
     * Get table by data source.
     * @param clusterName
     * @param proxyUser
     * @param dataSourceId
     * @param dbName
     * @param envId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws Exception
     */
    Map<String, Object> getTablesByDataSource(String clusterName, String proxyUser, Long dataSourceId, String dbName, Long envId) throws Exception;

    /**
     * Get column by data source.
     * @param clusterName
     * @param proxyUser
     * @param dataSourceId
     * @param dbName
     * @param tableName
     * @param envId
     * @return
     * @throws Exception
     */
    GeneralResponse<GetAllResponse<ColumnInfoDetail>> getColumnsByDataSource(String clusterName, String proxyUser, Long dataSourceId, String dbName, String tableName, Long envId) throws Exception;

    /**
     * getEnvList
     * @param clusterName
     * @param proxyUser
     * @param dataSourceId
     * @param versionId
     * @return
     * @throws Exception
     */
    GeneralResponse<List<Map<String, Object>>> getEnvList(String clusterName, String proxyUser, Long dataSourceId, Long versionId) throws Exception;

    /**
     * find All Department By Source Type
     * @param departmentSourceTypeEnum
     * @return
     * @throws UnExpectedRequestException
     */
    List<CmdbDepartmentResponse> findAllDepartment(DepartmentSourceTypeEnum departmentSourceTypeEnum) throws UnExpectedRequestException;

    /**
     * find Sub Department By Dept Code
     * @param departmentSourceType
     * @param deptCode
     * @return
     * @throws UnExpectedRequestException
     */
    List<DepartmentSubResponse> getSubDepartmentByDeptCode(DepartmentSourceTypeEnum departmentSourceType, Integer deptCode) throws UnExpectedRequestException;

    /**
     * find department by role type
     * @param departmentSourceType
     * @return
     * @throws UnExpectedRequestException
     */
    List<CmdbDepartmentResponse> getDepartmentInfoListByRoleType(DepartmentSourceTypeEnum departmentSourceType) throws UnExpectedRequestException;

    /**
     *  find sub department by role type
     * @param departmentSourceTypeEnum
     * @param deptCode
     * @return
     * @throws UnExpectedRequestException
     */
    List<DepartmentSubResponse> getDevAndOpsInfoListByRoleType(DepartmentSourceTypeEnum departmentSourceTypeEnum, Integer deptCode) throws UnExpectedRequestException;

    /**
     * get Data Source Name List
     * @return
     */
    List<String> getDataSourceNameList();

}
