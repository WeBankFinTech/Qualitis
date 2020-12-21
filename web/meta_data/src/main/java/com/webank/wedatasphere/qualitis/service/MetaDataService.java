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

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.response.cluster.ClusterInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.db.DbInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableInfoDetail;
import com.webank.wedatasphere.qualitis.request.GetUserClusterRequest;
import com.webank.wedatasphere.qualitis.request.GetUserColumnByTableIdRequest;
import com.webank.wedatasphere.qualitis.request.GetUserDbByClusterRequest;
import com.webank.wedatasphere.qualitis.request.GetUserTableByDbIdRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllClusterResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;

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
     Get Column by user and table
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<GetAllResponse<ColumnInfoDetail>> getUserColumnByTableId(GetUserColumnByTableIdRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException;

    /**
     * Get Cluster by user
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     */
    GeneralResponse<GetAllClusterResponse<ClusterInfoDetail>> getUserCluster(GetUserClusterRequest request)
        throws UnExpectedRequestException, MetaDataAcquireFailedException;


}
