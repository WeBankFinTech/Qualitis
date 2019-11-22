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
import com.webank.wedatasphere.qualitis.hive.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.hive.exception.ConnectionAcquireFailedException;
import com.webank.wedatasphere.qualitis.request.RefreshDbByClusterRequest;
import com.webank.wedatasphere.qualitis.request.RefreshTableByClusterAndDbRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.RefreshDbByClusterRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;

import java.sql.SQLException;

/**
 * @author howeye
 */
public interface RefreshMetaDataService {

    /**
     * Refresh cluster
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> refreshCluster() throws UnExpectedRequestException;

    /**
     * Refresh database in cluster
     * @param refreshDbByClusterRequest
     * @return
     * @throws UnExpectedRequestException
     * @throws ConnectionAcquireFailedException
     * @throws SQLException
     * @throws ClusterInfoNotConfigException
     */
    GeneralResponse<?> refreshDbByCluster(RefreshDbByClusterRequest refreshDbByClusterRequest) throws UnExpectedRequestException, ConnectionAcquireFailedException, SQLException, ClusterInfoNotConfigException;

    /**
     * Refresh table in database
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws ConnectionAcquireFailedException
     * @throws SQLException
     * @throws ClusterInfoNotConfigException
     */
    GeneralResponse<?> refreshTableByClusterAndDb(RefreshTableByClusterAndDbRequest request) throws UnExpectedRequestException, ConnectionAcquireFailedException, SQLException, ClusterInfoNotConfigException;
}
