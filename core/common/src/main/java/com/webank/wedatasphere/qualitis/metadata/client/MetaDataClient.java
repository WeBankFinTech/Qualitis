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


import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.request.GetClusterByUserRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetColumnByUserAndTableRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetDbByUserAndClusterRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetTableByUserAndDbRequest;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.cluster.ClusterInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.db.DbInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableInfoDetail;
import java.util.List;

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
    String getTableComment(String clusterName, String dbName, String tableName, String userName)
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

}