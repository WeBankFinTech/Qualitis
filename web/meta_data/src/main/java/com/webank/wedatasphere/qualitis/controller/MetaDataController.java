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

package com.webank.wedatasphere.qualitis.controller;

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
import com.webank.wedatasphere.qualitis.service.MetaDataService;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author howeye
 */
@Path("api/v1/projector/meta_data")
public class MetaDataController {

    @Autowired
    private MetaDataService metaDataService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataController.class);

    @POST
    @Path("cluster")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllClusterResponse<ClusterInfoDetail>> getUserCluster(GetUserClusterRequest request) throws UnExpectedRequestException {
        try {
            return metaDataService.getUserCluster(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error("Failed to get cluster. DataMap api response error, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error("Failed to get cluster, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_CLUSTER}", null);
        }
    }

    @POST
    @Path("db")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<DbInfoDetail>> getUserDbByCluster(GetUserDbByClusterRequest request) throws UnExpectedRequestException {
        try {
            return metaDataService.getUserDbByCluster(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error("Failed to get database. DataMap api response error, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error("Failed to get database by cluster: {}, caused by: {}", request.getClusterName(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_DATABASE_BY_CLUSTER}", null);
        }
    }

    @POST
    @Path("table")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<TableInfoDetail>> getUserTableByDbId(GetUserTableByDbIdRequest request) throws UnExpectedRequestException {
        try {
            return metaDataService.getUserTableByDbId(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error("Failed to get table. DataMap api response error, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error("Failed to get table by database: {}.{}, caused by: {}", request.getClusterName(), request.getDbName(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_TABLE_BY_DATABASE}", null);
        }
    }

    @POST
    @Path("column")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<ColumnInfoDetail>> getUserColumnByTableId(GetUserColumnByTableIdRequest request) throws UnExpectedRequestException {
        try {
            return metaDataService.getUserColumnByTableId(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error("Failed to get column. DataMap api response error, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error("Failed to get column by table: {}.{}.{}, caused by: {}", request.getClusterName(), request.getDbName(), request.getTableName(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_COLUMN_BY_TABLE}", null);
        }
    }

}
