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
import com.webank.wedatasphere.qualitis.metadata.request.GetUserColumnByCsRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserTableByCsIdRequest;
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
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllClusterResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.MulDbResponse;
import com.webank.wedatasphere.qualitis.service.MetaDataService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.ResourceAccessException;

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
    @Path("cs_table")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<CsTableInfoDetail>> getContextServiceTableByCsId(GetUserTableByCsIdRequest request) throws UnExpectedRequestException {
        try {
            return metaDataService.getUserTableByCsId(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error("Failed to get table. Context Service api response error, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error("Failed to get table by context service ID: {}, caused by: {}",  request.getCsId(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_TABLE_BY_CSID}", null);
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

    @POST
    @Path("cs_column")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<ColumnInfoDetail>> getUserColumnByContextService(GetUserColumnByCsRequest request) throws UnExpectedRequestException {
        try {
            return metaDataService.getUserColumnByCsId(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error("Failed to get column. Context Service api response error, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error("Failed to get column by table's context key: {}, caused by: {}", request.getContextKey(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_COLUMN_BY_TABLE}", null);
        }
    }

    @POST
    @Path("mul_db")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<MulDbResponse> addMultiDbRules(MulDbRequest request) throws UnExpectedRequestException {
        try {
            String dbs = metaDataService.addMultiDbRules(request);
            return new GeneralResponse<>("200", "{&SUCCESS_MUL_DBS_COMPARE}", new MulDbResponse(dbs));
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to create multi-db rules, caused by: {}", e);
            return new GeneralResponse<>("500", "{&FAILED_TO_ADD_MULTI_SOURCE_RULE}", null);
        }
    }

    @GET
    @Path("data_source/types/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map> getAllDataSourceTypes(@QueryParam("clusterName") String clusterName, @QueryParam("proxyUser") String proxyUser) {
        try {
            return metaDataService.getAllDataSourceTypes(clusterName, proxyUser);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "Failed", null);
        }
    }

    @GET
    @Path("data_source/env")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map> getDataSourceEnv(@QueryParam("clusterName") String clusterName, @QueryParam("proxyUser") String proxyUser) {
        try {
            return metaDataService.getDataSourceEnv(clusterName, proxyUser);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "Failed", null);
        }
    }

    @GET
    @Path("data_source/info")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map> getDataSourceInfoPage(@QueryParam("clusterName") String clusterName, @QueryParam("proxyUser") String proxyUser
        , @QueryParam("currentPage") Integer currentPage, @QueryParam("pageSize") Integer pageSize, @QueryParam("name") String searchName
        , @QueryParam("typeId") Long typeId) {
        try {
            if (currentPage == null) {
                currentPage = 1;
            }
            if (pageSize == null) {
                pageSize = 10;
            }
            return metaDataService.getDataSourceInfoPage(clusterName, proxyUser, currentPage.intValue(), pageSize.intValue(), searchName, typeId);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "Failed", null);
        }
    }

    @GET
    @Path("data_source/versions")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map> getDataSourceVersions(@QueryParam("clusterName") String clusterName, @QueryParam("proxyUser") String proxyUser
    , @QueryParam("dataSourceId") Long dataSourceId) {
        try {
            if (dataSourceId == null) {
                throw new UnExpectedRequestException("Data source ID" + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            return metaDataService.getDataSourceVersions(clusterName, proxyUser, dataSourceId);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "Failed", null);
        }
    }

    @GET
    @Path("data_source/info/detail")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map> getDataSourceInfoDetail(@QueryParam("clusterName") String clusterName, @QueryParam("proxyUser") String proxyUser
    , @QueryParam("dataSourceId") Long dataSourceId, @QueryParam("versionId") Long versionId) {
        try {
            if (dataSourceId == null) {
                throw new UnExpectedRequestException("Data source ID or version ID " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            return metaDataService.getDataSourceInfoDetail(clusterName, proxyUser, dataSourceId, versionId);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "Failed", null);
        }
    }

    @GET
    @Path("data_source/key_define/type")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map> getDataSourceKeyDefine(@QueryParam("clusterName") String clusterName, @QueryParam("proxyUser") String proxyUser, @QueryParam("keyId") Long keyId) {
        try {
            if (keyId == null) {
                throw new UnExpectedRequestException("Key ID " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            return metaDataService.getDataSourceKeyDefine(clusterName, proxyUser, keyId);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "Failed", null);
        }
    }

    @POST
    @Path("data_source/connect")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map> connectDataSource(@QueryParam("clusterName") String clusterName, @QueryParam("proxyUser") String proxyUser, DataSourceConnectRequest request) {
        try {
            if (request == null || StringUtils.isBlank(clusterName)) {
                throw new UnExpectedRequestException("Request " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            return metaDataService.connectDataSource(clusterName, proxyUser, request);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "{&PARAMS_ERROR_FOR_THIRD_PART_SERVICE}", null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "{&CONNECT_FAILED}", null);
        }
    }

    @POST
    @Path("data_source/publish")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map> publishDataSource(@QueryParam("clusterName") String clusterName, @QueryParam("proxyUser") String proxyUser
        , @QueryParam("dataSourceId") Long dataSourceId, @QueryParam("versionId") Long versionId) {
        try {
            if (dataSourceId == null || versionId == null || StringUtils.isBlank(clusterName)) {
                throw new UnExpectedRequestException("Request " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            return metaDataService.publishDataSource(clusterName, proxyUser, dataSourceId, versionId);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "{&PARAMS_ERROR_FOR_THIRD_PART_SERVICE}", null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "Failed", null);
        }
    }

    @POST
    @Path("data_source/expire")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map> expireDataSource(@QueryParam("clusterName") String clusterName, @QueryParam("proxyUser") String proxyUser
        , @QueryParam("dataSourceId") Long dataSourceId) {
        try {
            if (dataSourceId == null || StringUtils.isBlank(clusterName)) {
                throw new UnExpectedRequestException("Request " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            return metaDataService.expireDataSource(clusterName, proxyUser, dataSourceId);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "{&PARAMS_ERROR_FOR_THIRD_PART_SERVICE}", null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "Failed", null);
        }
    }

    @POST
    @Path("data_source/modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map> modifyDataSource(@QueryParam("clusterName") String clusterName, @QueryParam("proxyUser") String proxyUser
        , @QueryParam("dataSourceId") Long dataSourceId, DataSourceModifyRequest request) {
        try {
            if (request == null || dataSourceId == null || StringUtils.isBlank(clusterName)) {
                throw new UnExpectedRequestException("Request " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            return metaDataService.modifyDataSource(clusterName, proxyUser, dataSourceId, request);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "{&PARAMS_ERROR_FOR_THIRD_PART_SERVICE}", null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "Failed", null);
        }
    }

    @POST
    @Path("data_source/param/modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map> modifyDataSourceParam(@QueryParam("clusterName") String clusterName, @QueryParam("proxyUser") String proxyUser
        , @QueryParam("dataSourceId") Long dataSourceId, DataSourceParamModifyRequest request) {
        try {
            if (request == null || dataSourceId == null || StringUtils.isBlank(clusterName)) {
                throw new UnExpectedRequestException("Request " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }

            return metaDataService.modifyDataSourceParam(clusterName, proxyUser, dataSourceId, request);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "{&PARAMS_ERROR_FOR_THIRD_PART_SERVICE}", null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "Failed", null);
        }
    }

    @POST
    @Path("data_source/create")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map> createDataSource(@QueryParam("clusterName") String clusterName, @QueryParam("proxyUser") String proxyUser
        , DataSourceModifyRequest request) {
        try {
            if (request == null || StringUtils.isBlank(clusterName)) {
                throw new UnExpectedRequestException("Request " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            return metaDataService.createDataSource(clusterName, proxyUser, request);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "{&PARAMS_ERROR_FOR_THIRD_PART_SERVICE}", null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        }
    }

    @GET
    @Path("data_source/dbs")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<DbInfoDetail>> getDbsByDataSource(@QueryParam("clusterName") String clusterName, @QueryParam("proxyUser") String proxyUser
        , @QueryParam("dataSourceId") Long dataSourceId) {
        try {
            if (dataSourceId == null || StringUtils.isBlank(clusterName)) {
                throw new UnExpectedRequestException("Request " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            Map response = metaDataService.getDbsByDataSource(clusterName, proxyUser, dataSourceId);
            GetAllResponse allResponse = new GetAllResponse();
            List<String> dbs = (List<String>) response.get("dbs");
            List<DbInfoDetail> dbInfoDetails = new ArrayList<>(CollectionUtils.isEmpty(dbs) ? 0 : dbs.size());
            for (String db : dbs) {
                DbInfoDetail dbInfoDetail = new DbInfoDetail(db);
                dbInfoDetails.add(dbInfoDetail);
            }
            allResponse.setTotal(CollectionUtils.isEmpty(dbInfoDetails) ? 0 : dbInfoDetails.size());
            allResponse.setData(dbInfoDetails);
            return new GeneralResponse<>("200", "{&GET_DB_SUCCESSFULLY}", allResponse);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_DATABASE_BY_CLUSTER}", null);
        }
    }

    @GET
    @Path("data_source/tables")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<TableInfoDetail>> getTablesByDataSource(@QueryParam("clusterName") String clusterName, @QueryParam("proxyUser") String proxyUser
        , @QueryParam("dataSourceId") Long dataSourceId, @QueryParam("dbName") String dbName) {
        try {
            if (dataSourceId == null || StringUtils.isBlank(dbName) || StringUtils.isBlank(clusterName)) {
                throw new UnExpectedRequestException("Request " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            Map response = metaDataService.getTablesByDataSource(clusterName, proxyUser, dataSourceId, dbName);
            GetAllResponse allResponse = new GetAllResponse();
            List<String> tables = (List<String>) response.get("tables");
            List<TableInfoDetail> tableInfoDetails = new ArrayList<>(CollectionUtils.isEmpty(tables) ? 0 : tables.size());
            for (String table : tables) {
                TableInfoDetail tableInfoDetail = new TableInfoDetail(table);
                tableInfoDetails.add(tableInfoDetail);
            }
            allResponse.setTotal(CollectionUtils.isEmpty(tableInfoDetails) ? 0 : tableInfoDetails.size());
            allResponse.setData(tableInfoDetails);
            return new GeneralResponse<>("200", "{&GET_TABLE_SUCCESSFULLY}", allResponse);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_TABLE_BY_DATABASE}", null);
        }
    }

    @GET
    @Path("data_source/columns")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<ColumnInfoDetail>> getColumnsByDataSource(@QueryParam("clusterName") String clusterName, @QueryParam("proxyUser") String proxyUser
        , @QueryParam("dataSourceId") Long dataSourceId, @QueryParam("dbName") String dbName, @QueryParam("tableName") String tableName) {
        try {
            if (dataSourceId == null || StringUtils.isBlank(dbName) || StringUtils.isBlank(tableName) || StringUtils.isBlank(clusterName)) {
                throw new UnExpectedRequestException("Request " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            return metaDataService.getColumnsByDataSource(clusterName, proxyUser, dataSourceId, dbName, tableName);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_COLUMN_BY_TABLE}", null);
        }
    }
}
