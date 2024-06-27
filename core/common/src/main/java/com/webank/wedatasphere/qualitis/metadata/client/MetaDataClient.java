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

package com.webank.wedatasphere.qualitis.metadata.client;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.request.GetClusterByUserRequest;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.response.datasource.LinkisDataSourceInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.request.GetColumnByUserAndTableRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetDbByUserAndClusterRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetTableByUserAndDbRequest;
import com.webank.wedatasphere.qualitis.metadata.response.cluster.ClusterInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserTableByCsIdRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserColumnByCsRequest;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.db.DbInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.CsTableInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.PartitionStatisticsInfo;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableStatisticsInfo;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableInfoDetail;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import org.json.JSONException;
import java.io.File;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public interface MetaDataClient {
    /**
     * Get all cluster by user
     * @param request
     * @return
     * @throws MetaDataAcquireFailedException
     */
    DataInfo<ClusterInfoDetail> getClusterByUser(GetClusterByUserRequest request)
        throws MetaDataAcquireFailedException;

    /**
     * Get db by user and cluster
     * @param request
     * @return
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     */
    DataInfo<DbInfoDetail> getDbByUserAndCluster(GetDbByUserAndClusterRequest request)
        throws MetaDataAcquireFailedException, UnExpectedRequestException;

    /**
     * Get table by user and db
     * @param request
     * @return
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     */
    DataInfo<TableInfoDetail> getTableByUserAndDb(GetTableByUserAndDbRequest request)
        throws MetaDataAcquireFailedException, UnExpectedRequestException;

    /**
     * Get table commit from table basic info. More table details can be obtained in the future
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param userName
     * @return
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     */
    String getTableBasicInfo(String clusterName, String dbName, String tableName, String userName)
        throws MetaDataAcquireFailedException, UnExpectedRequestException;

    /**
     * Get table by context service ID and DSS node name
     * @param request
     * @return
     * @throws Exception
     */
    DataInfo<CsTableInfoDetail> getTableByCsId(GetUserTableByCsIdRequest request)
        throws Exception;

    /**
     * Get column by user and table
     * @param request
     * @return
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     */
    DataInfo<ColumnInfoDetail> getColumnByUserAndTable(GetColumnByUserAndTableRequest request)
        throws MetaDataAcquireFailedException, UnExpectedRequestException;

    /**
     * Get the columns' information of the table
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param userName
     * @return
     * @throws Exception
     */
    List<ColumnInfoDetail> getColumnInfo(String clusterName, String dbName, String tableName, String userName)
        throws Exception;

    /**
     * Get column by context service ID and table context key
     * @param request
     * @return
     * @throws Exception
     */
    DataInfo<ColumnInfoDetail> getColumnByCsId(GetUserColumnByCsRequest request)
            throws Exception;

    /**
     * Get table statistics info.
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param user
     * @return
     * @throws Exception
     */
    TableStatisticsInfo getTableStatisticsInfo(String clusterName, String dbName, String tableName, String user)
            throws Exception;

    /**
     * Get partition statistics info.
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param partitionPath
     * @param user
     * @return
     * @throws Exception
     */
    PartitionStatisticsInfo getPartitionStatisticsInfo(String clusterName, String dbName, String tableName, String partitionPath, String user)
            throws Exception;

    /**
     * Check field.
     * @param col
     * @param cols
     * @param mappingCols
     * @return
     */
    boolean fieldExist(String col, List<ColumnInfoDetail> cols, Map<String, String> mappingCols);

    /**
     * Get all data source types.
     * @param clusterName
     * @param userName
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map<String, Object>> getAllDataSourceTypes(String clusterName, String userName)
        throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get data source env.
     * @param clusterName
     * @param userName
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map<String, Object>> getDataSourceEnv(String clusterName, String userName) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Create data source env.
     * @param clusterName
     * @param authUser
     * @param createSystem
     * @param datasourceEnvs
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws JSONException
     */
    GeneralResponse<Map<String, Object>> createDataSourceEnvBatch(String clusterName, String authUser, String createSystem, String datasourceEnvs) throws UnExpectedRequestException, MetaDataAcquireFailedException, JSONException;

    /**
     * Modify data source env.
     * @param clusterName
     * @param authUser
     * @param createSystem
     * @param datasourceEnvs
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws JSONException
     */
    GeneralResponse<Map<String, Object>> modifyDataSourceEnvBatch(String clusterName, String authUser, String createSystem, String datasourceEnvs) throws UnExpectedRequestException, MetaDataAcquireFailedException, JSONException;

    /**
     * Get datasource env by id.
     * @param clusterName
     * @param authUser
     * @param envId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws JsonProcessingException
     */
    GeneralResponse<Map<String, Object>> getDatasourceEnvById(String clusterName, String authUser, Long envId) throws UnExpectedRequestException, MetaDataAcquireFailedException, JsonProcessingException;

    /**
     * Get data source info pageable.
     * @param clusterName
     * @param userName
     * @param page
     * @param size
     * @param searchName
     * @param typeId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws UnsupportedEncodingException
     */
    GeneralResponse<Map<String, Object>> getDataSourceInfoPage(String clusterName, String userName, int page, int size, String searchName, Long typeId)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, UnsupportedEncodingException;

    /**
     * Get data source info by ids
     * @param clusterName
     * @param userName
     * @param dataSourceIds
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    GeneralResponse<Map<String, Object>> getDataSourceInfoByIds(String clusterName, String userName, List<Long> dataSourceIds)
            throws UnExpectedRequestException, MetaDataAcquireFailedException, IOException;

    /**
     * Get data source versions.
     * @param clusterName
     * @param userName
     * @param dataSourceId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map<String, Object>> getDataSourceVersions(String clusterName, String userName, Long dataSourceId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get data source info detail.
     * @param clusterName
     * @param userName
     * @param dataSourceId
     * @param versionId
     * @return
     * @throws Exception
     */
    GeneralResponse<Map<String, Object>> getDataSourceInfoDetail(String clusterName, String userName, Long dataSourceId, Long versionId) throws Exception;

    /**
     * Get data source info detail by name.
     * @param clusterName
     * @param authUser
     * @param dataSourceName
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map<String, Object>> getDataSourceInfoDetailByName(String clusterName, String authUser,
        String dataSourceName) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get data source key define.
     * @param clusterName
     * @param userName
     * @param keyId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map<String, Object>> getDataSourceKeyDefine(String clusterName, String userName, Long keyId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Connect to data source.
     * @param clusterName
     * @param userName
     * @param jsonRequest
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws JSONException
     */
    GeneralResponse<Map<String, Object>> connectDataSource(String clusterName, String userName, String jsonRequest) throws UnExpectedRequestException, MetaDataAcquireFailedException, JSONException;

    /**
     * Get connect params.
     * @param clusterName
     * @param authUser
     * @param dataSourceId
     * @param versionId
     * @return
     * @throws Exception
     */
    GeneralResponse<Map<String, Object>> getDataSourceConnectParams(String clusterName, String authUser, Long dataSourceId,
        Long versionId) throws Exception;

    /**
     * Publish data source.
     * @param clusterName
     * @param userName
     * @param dataSourceId
     * @param versionId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map<String, Object>> publishDataSource(String clusterName, String userName, Long dataSourceId, Long versionId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Expire data source.
     * @param clusterName
     * @param userName
     * @param dataSourceId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map<String, Object>> expireDataSource(String clusterName, String userName, Long dataSourceId)throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Modify data source.
     * @param clusterName
     * @param userName
     * @param dataSourceId
     * @param jsonRequest
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws JSONException
     */
    GeneralResponse<Map<String, Object>> modifyDataSource(String clusterName, String userName, Long dataSourceId, String jsonRequest) throws UnExpectedRequestException, MetaDataAcquireFailedException, JSONException;

    /**
     * Modify data source param.
     * @param clusterName
     * @param userName
     * @param dataSourceId
     * @param jsonRequest
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws JSONException
     */
    GeneralResponse<Map<String, Object>> modifyDataSourceParam(String clusterName, String userName, Long dataSourceId, String jsonRequest) throws UnExpectedRequestException, MetaDataAcquireFailedException, JSONException;

    /**
     * Create data source param.
     * @param clusterName
     * @param userName
     * @param jsonRequest
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws JSONException
     */
    GeneralResponse<Map<String, Object>> createDataSource(String clusterName, String userName, String jsonRequest) throws UnExpectedRequestException, MetaDataAcquireFailedException, JSONException;

    /**
     * delete data source
     * @param clusterName
     * @param userName
     * @param dataSourceId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map<String, Object>> deleteDataSource(String clusterName, String userName, Long dataSourceId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * delete data source
     * @param clusterName
     * @param userName
     * @param envId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map<String, Object>> deleteEnv(String clusterName, String userName, Long envId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get db by data source.
     * @param clusterName
     * @param userName
     * @param dataSourceName
     * @param envId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    Map<String, Object> getDbsByDataSourceName(String clusterName, String userName, String dataSourceName, Long envId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get table by data source.
     * @param clusterName
     * @param userName
     * @param dataSourceName
     * @param dbName
     * @param envId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    Map<String, Object> getTablesByDataSourceName(String clusterName, String userName, String dataSourceName, String dbName, Long envId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get column by data source.
     * @param clusterName
     * @param userName
     * @param dataSourceId
     * @param dbName
     * @param tableName
     * @return
     * @throws Exception
     */
    @Deprecated
    DataInfo<ColumnInfoDetail> getColumnsByDataSource(String clusterName, String userName, Long dataSourceId, String dbName, String tableName) throws Exception;

    /**
     * Get column by data source.
     * @param clusterName
     * @param userName
     * @param dataSourceName
     * @param dbName
     * @param tableName
     * @param envId
     * @return
     * @throws Exception
     */
    DataInfo<ColumnInfoDetail> getColumnsByDataSourceName(String clusterName, String userName, String dataSourceName, String dbName, String tableName, Long envId) throws Exception;

    /**
     * Get undone task total.
     * @param clusterName
     * @param executionUser
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    int getUndoneTaskTotal(String clusterName, String executionUser) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get data source name by id
     * @param clusterName
     * @param userName
     * @param dataSourceId
     * @return
     * @throws Exception
     */
    LinkisDataSourceInfoDetail getDataSourceInfoById(String clusterName, String userName, Long dataSourceId) throws Exception;

    /**
     * Get data source name by id
     * @param clusterName
     * @param userName
     * @param dataSourceId
     * @param versionId
     * @return
     * @throws Exception
     */
    LinkisDataSourceInfoDetail getDataSourceInfoById(String clusterName, String userName, Long dataSourceId, Long versionId) throws Exception;

    /**
     * Add udf
     * @param currentCluster
     * @param username
     * @param requestBody
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     * @throws JSONException
     * @throws MetaDataAcquireFailedException
     */
    Long addUdf(String currentCluster, String username, Map<String, Object> requestBody)
        throws UnExpectedRequestException, IOException, JSONException, MetaDataAcquireFailedException;

    /**
     * Modify udf
     * @param currentCluster
     * @param linkisUdfAdminUser
     * @param requestBody
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     * @throws JSONException
     * @throws MetaDataAcquireFailedException
     */
    void modifyUdf(String currentCluster, String linkisUdfAdminUser, Map<String, Object> requestBody) throws UnExpectedRequestException, IOException, JSONException, MetaDataAcquireFailedException;

    /**
     * Check file path exists
     * @param currentCluster
     * @param userNmae
     * @param uploadFile
     * @param needUpload
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws IOException
     * @throws JSONException
     * @return
     */
    String checkFilePathExistsAndUploadToWorkspace(String currentCluster, String userNmae, File uploadFile, Boolean needUpload) throws UnExpectedRequestException, MetaDataAcquireFailedException, IOException, JSONException;

    /**
     * Client add
     * @param currentCluster
     * @param targetFilePath
     * @param uploadFile
     * @param fileName
     * @param udfDesc
     * @param udfName
     * @param returnType
     * @param enter
     * @param registerName
     * @param status
     * @param dir
     * @return
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     * @throws JSONException
     * @throws IOException
     */
    Long clientAdd(String currentCluster, String targetFilePath, File uploadFile, String fileName, String udfDesc, String udfName, String returnType
        , String enter, String registerName, Boolean status,
        String dir) throws MetaDataAcquireFailedException, UnExpectedRequestException, JSONException, IOException;

    /**
     * Client modify
     * @param targetFilePath
     * @param uploadFile
     * @param currentCluster
     * @param clusterIdMaps
     * @param fileName
     * @param udfDesc
     * @param udfName
     * @param returnType
     * @param enter
     * @param registerName
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     * @throws JSONException
     * @throws IOException
     */
    void clientModify(String targetFilePath, File uploadFile, String currentCluster, Map<String, Long> clusterIdMaps, String fileName, String udfDesc
        , String udfName, String returnType, String enter,
        String registerName) throws MetaDataAcquireFailedException, UnExpectedRequestException, JSONException, IOException;

    /**
     * Share and deploy
     * @param udfId
     * @param currentCluster
     * @param proxyUserNames
     * @param udfName
     */
    void shareAndDeploy(Long udfId, String currentCluster, List<String> proxyUserNames, String udfName);

    /**
     * Get udf detail
     * @param clusterName
     * @param linkisUdfAdminUser
     * @param linkisUdfId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    Map<String, Object> getUdfDetail(String clusterName, String linkisUdfAdminUser, Long linkisUdfId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get directory
     * @param category
     * @param clusterName
     * @param linkisUdfAdminUser
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    List<String> getDirectory(String category, String clusterName, String linkisUdfAdminUser) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Share udf to proxy users
     * @param currentCluster
     * @param linkisUdfAdminUser
     * @param proxyUserNames
     * @param udfId
     * @throws IOException
     * @throws JSONException
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    void shareUdfToProxyUsers(String currentCluster, String linkisUdfAdminUser, List<String> proxyUserNames, Long udfId) throws IOException, JSONException, UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Delete udf
     * @param enableClusterName
     * @param linkisUdfId
     * @param linkisUdfAdminUser
     * @param fileName
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    void deleteUdf(String enableClusterName, Long linkisUdfId, String linkisUdfAdminUser, String fileName)
        throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Switch udf status
     * @param enableClusterName
     * @param linkisUdfId
     * @param linkisUdfAdminUser
     * @param isLoad
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    void switchUdfStatus(String enableClusterName, Long linkisUdfId, String linkisUdfAdminUser, Boolean isLoad)
        throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get new version
     * @param currentCluster
     * @param linkisUdfAdminUser
     * @param name
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     * @throws JSONException
     * @throws MetaDataAcquireFailedException
     */
    String getUdfNewVersion(String currentCluster, String linkisUdfAdminUser, String name)
        throws UnExpectedRequestException, IOException, JSONException, MetaDataAcquireFailedException;

    /**
     * Deploy new version
     * @param currentCluster
     * @param linkisUdfAdminUser
     * @param udfId
     * @param version
     * @throws UnExpectedRequestException
     * @throws IOException
     * @throws JSONException
     * @throws MetaDataAcquireFailedException
     */
    void deployUdfNewVersion(String currentCluster, String linkisUdfAdminUser, Long udfId, String version)
        throws UnExpectedRequestException, IOException, JSONException, MetaDataAcquireFailedException;
}