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


import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.request.GetClusterByUserRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetColumnByUserAndTableRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetDbByUserAndClusterRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetTableByUserAndDbRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserColumnByCsRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserTableByCsIdRequest;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.cluster.ClusterInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.db.DbInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.CsTableInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.PartitionStatisticsInfo;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableInfoDetail;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableStatisticsInfo;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import org.springframework.web.client.RestClientException;

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
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     */
    DataInfo<CsTableInfoDetail> getTableByCsId(GetUserTableByCsIdRequest request)
        throws MetaDataAcquireFailedException, UnExpectedRequestException;

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
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     */
    List<ColumnInfoDetail> getColumnInfo(String clusterName, String dbName, String tableName, String userName)
        throws MetaDataAcquireFailedException, UnExpectedRequestException;

    /**
     * Get column by context service ID and table context key
     * @param request
     * @return
     * @throws MetaDataAcquireFailedException
     * @throws UnExpectedRequestException
     */
    DataInfo<ColumnInfoDetail> getColumnByCsId(GetUserColumnByCsRequest request)
        throws MetaDataAcquireFailedException, UnExpectedRequestException;

    /**
     * Get table statistics info.
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param user
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws RestClientException
     */
    TableStatisticsInfo getTableStatisticsInfo(String clusterName, String dbName, String tableName, String user)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, RestClientException;

    /**
     * Get partition statistics info.
     * @param clusterName
     * @param dbName
     * @param tableName
     * @param partitionPath
     * @param user
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws RestClientException
     */
    PartitionStatisticsInfo getPartitionStatisticsInfo(String clusterName, String dbName, String tableName, String partitionPath, String user)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, RestClientException;

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
    GeneralResponse<Map> getAllDataSourceTypes(String clusterName, String userName)
        throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get data source env.
     * @param clusterName
     * @param userName
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map> getDataSourceEnv(String clusterName, String userName) throws UnExpectedRequestException, MetaDataAcquireFailedException;

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
    GeneralResponse<Map> getDataSourceInfoPage(String clusterName, String userName, int page, int size, String searchName, Long typeId)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, UnsupportedEncodingException;

    /**
     * Get data source versions.
     * @param clusterName
     * @param userName
     * @param dataSourceId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map> getDataSourceVersions(String clusterName, String userName, Long dataSourceId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get data source info detail.
     * @param clusterName
     * @param userName
     * @param dataSourceId
     * @param versionId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map> getDataSourceInfoDetail(String clusterName, String userName, Long dataSourceId, Long versionId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get data source info detail by name.
     * @param clusterName
     * @param authUser
     * @param dataSourceName
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map> getDataSourceInfoDetailByName(String clusterName, String authUser,
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
    GeneralResponse<Map> getDataSourceKeyDefine(String clusterName, String userName, Long keyId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Connect to data source.
     * @param clusterName
     * @param userName
     * @param jsonRequest
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map> connectDataSource(String clusterName, String userName, String jsonRequest) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get connect params.
     * @param clusterName
     * @param authUser
     * @param dataSourceId
     * @param versionId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map> getDataSourceConnectParams(String clusterName, String authUser, Long dataSourceId,
        Long versionId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

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
    GeneralResponse<Map> publishDataSource(String clusterName, String userName, Long dataSourceId, Long versionId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Expire data source.
     * @param clusterName
     * @param userName
     * @param dataSourceId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map> expireDataSource(String clusterName, String userName, Long dataSourceId)throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Modify data source.
     * @param clusterName
     * @param userName
     * @param dataSourceId
     * @param jsonRequest
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map> modifyDataSource(String clusterName, String userName, Long dataSourceId, String jsonRequest) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Modify data source param.
     * @param clusterName
     * @param userName
     * @param dataSourceId
     * @param jsonRequest
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map> modifyDataSourceParam(String clusterName, String userName, Long dataSourceId, String jsonRequest) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Create data source param.
     * @param clusterName
     * @param userName
     * @param jsonRequest
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<Map> createDataSource(String clusterName, String userName, String jsonRequest) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get db by data source.
     * @param clusterName
     * @param userName
     * @param dataSourceId
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    Map getDbsByDataSource(String clusterName, String userName, Long dataSourceId) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get table by data source.
     * @param clusterName
     * @param userName
     * @param dataSourceId
     * @param dbName
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    Map getTablesByDataSource(String clusterName, String userName, Long dataSourceId, String dbName) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get column by data source.
     * @param clusterName
     * @param userName
     * @param dataSourceId
     * @param dbName
     * @param tableName
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    DataInfo<ColumnInfoDetail> getColumnsByDataSource(String clusterName, String userName, Long dataSourceId, String dbName, String tableName) throws UnExpectedRequestException, MetaDataAcquireFailedException;
}