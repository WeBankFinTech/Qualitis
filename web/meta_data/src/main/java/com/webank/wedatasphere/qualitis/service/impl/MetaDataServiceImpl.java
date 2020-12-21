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

import com.webank.wedatasphere.qualitis.metadata.client.MetaDataClient;
import com.webank.wedatasphere.qualitis.metadata.response.table.CsTableInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.request.*;
import com.webank.wedatasphere.qualitis.response.*;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.cluster.ClusterInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.db.DbInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableInfoDetail;
import com.webank.wedatasphere.qualitis.rule.service.RuleLimitationService;
import com.webank.wedatasphere.qualitis.service.MetaDataService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.request.GetUserClusterRequest;
import com.webank.wedatasphere.qualitis.request.GetUserColumnByTableIdRequest;
import com.webank.wedatasphere.qualitis.request.GetUserDbByClusterRequest;
import com.webank.wedatasphere.qualitis.request.GetUserTableByDbIdRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

/**
 * @author howeye
 */
@Service
public class MetaDataServiceImpl implements MetaDataService {

    @Autowired
    private MetaDataClient metaDataClient;
    @Autowired
    private RuleLimitationService ruleLimitationService;

    private HttpServletRequest httpServletRequest;
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataServiceImpl.class);

    public MetaDataServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public GeneralResponse<GetAllResponse<DbInfoDetail>> getUserDbByCluster(GetUserDbByClusterRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check Arguments
        checkRequest(request);

        // Get login user
        String username = HttpUtils.getUserName(httpServletRequest);

        GetDbByUserAndClusterRequest getDbByUserAndClusterRequest = new GetDbByUserAndClusterRequest(username, request.getStartIndex(),
                request.getPageSize(), request.getClusterName());
        DataInfo<DbInfoDetail> response = metaDataClient.getDbByUserAndCluster(getDbByUserAndClusterRequest);

        GetAllResponse<DbInfoDetail> result = new GetAllResponse<>();
        result.setTotal(response.getTotalCount());
        result.setData(response.getContent());
        LOGGER.info("Succeed to get database by cluster, cluster: {}", request.getClusterName());
        return new GeneralResponse<>("200", "{&GET_DB_SUCCESSFULLY}", result);
    }

    @Override
    public GeneralResponse<GetAllResponse<TableInfoDetail>> getUserTableByDbId(GetUserTableByDbIdRequest request) throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check Arguments
        checkRequest(request);
        // Get login user
        String username = HttpUtils.getUserName(httpServletRequest);

        GetTableByUserAndDbRequest getTableByUserAndDbRequest = new GetTableByUserAndDbRequest(username, request.getStartIndex(),
                request.getPageSize(), request.getClusterName(), request.getDbName());
        DataInfo<TableInfoDetail> response = metaDataClient.getTableByUserAndDb(getTableByUserAndDbRequest);

        GetAllResponse<TableInfoDetail> result = new GetAllResponse<>();
        result.setTotal(response.getTotalCount());
        result.setData(response.getContent());

        LOGGER.info("Succeed to get table by database. database: {}.{}", request.getClusterName(), request.getDbName());
        return new GeneralResponse<>("200", "{&GET_TABLE_SUCCESSFULLY}", result);
    }

    @Override
    public GeneralResponse<GetAllResponse<ColumnInfoDetail>> getUserColumnByTableId(GetUserColumnByTableIdRequest request)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check Arguments
        checkRequest(request);
        // Get login user
        String username = HttpUtils.getUserName(httpServletRequest);

        GetColumnByUserAndTableRequest getColumnByUserAndTableRequest = new GetColumnByUserAndTableRequest(username, request.getStartIndex(),
                request.getPageSize(), request.getClusterName(), request.getDbName(), request.getTableName());
        DataInfo<ColumnInfoDetail> response = metaDataClient.getColumnByUserAndTable(getColumnByUserAndTableRequest);

        GetAllResponse<ColumnInfoDetail> result = new GetAllResponse<>();
        result.setTotal(response.getTotalCount());
        result.setData(response.getContent());

        LOGGER.info("Succeed to get column by table. table: {}.{}.{}", request.getClusterName(), request.getDbName(), request.getTableName());
        return new GeneralResponse<>("200", "{&GET_COLUMN_SUCCESSFULLY}", result);
    }

    @Override
    public GeneralResponse<GetAllClusterResponse<ClusterInfoDetail>> getUserCluster(GetUserClusterRequest request)
        throws UnExpectedRequestException, MetaDataAcquireFailedException {
        // Check Arguments
        checkRequest(request);
        // Get login user
        String username = HttpUtils.getUserName(httpServletRequest);

        GetClusterByUserRequest getClusterByUserRequest = new GetClusterByUserRequest(username, request.getStartIndex(), request.getPageSize());
        DataInfo<ClusterInfoDetail> response = metaDataClient.getClusterByUser(getClusterByUserRequest);

        GetAllClusterResponse<ClusterInfoDetail> result = new GetAllClusterResponse<>();
        result.setOptionalClusters(ruleLimitationService.getLimitClusters());
        result.setTotal(response.getTotalCount());
        result.setData(response.getContent());

        LOGGER.info("Succeed to get cluster. response: {}", result);
        return new GeneralResponse<>("200", "{&GET_CLUSTER_SUCCESSFULLY}", result);
    }

    private void checkRequest(GetUserClusterRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
    }

    private void checkRequest(GetUserColumnByTableIdRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }

        checkString(request.getClusterName(), "cluster name");
        checkString(request.getDbName(), "db name");
        checkString(request.getTableName(), "table name");
    }

    private void checkRequest(GetUserTableByDbIdRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }

        checkString(request.getClusterName(), "cluster name");
        checkString(request.getDbName(), "db name");
    }

    private void checkRequest(GetUserDbByClusterRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }

        checkString(request.getClusterName(), "cluster name");
    }

    private void checkString(String str, String strName) throws UnExpectedRequestException {
        if (StringUtils.isBlank(str)) {
            throw new UnExpectedRequestException(strName + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }
}
