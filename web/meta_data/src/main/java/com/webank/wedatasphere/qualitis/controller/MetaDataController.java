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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.webank.wedatasphere.qualitis.config.LinkisConfig;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.client.OperateCiService;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserColumnByCsRequest;
import com.webank.wedatasphere.qualitis.metadata.request.GetUserTableByCsIdRequest;
import com.webank.wedatasphere.qualitis.metadata.response.*;
import com.webank.wedatasphere.qualitis.metadata.response.cluster.ClusterInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.db.DbInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.CsTableInfoDetail;
import com.webank.wedatasphere.qualitis.metadata.response.table.TableInfoDetail;
import com.webank.wedatasphere.qualitis.request.*;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllClusterResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.MulDbResponse;
import com.webank.wedatasphere.qualitis.rule.service.FpsService;
import com.webank.wedatasphere.qualitis.service.FileService;
import com.webank.wedatasphere.qualitis.service.MetaDataService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.ResourceAccessException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Path("api/v1/projector/meta_data")
public class MetaDataController {

    @Autowired
    private OperateCiService operateCiService;
    @Autowired
    private FileService fileService;
    @Autowired
    private FpsService fpsService;
    @Autowired
    private LinkisConfig linkisConfig;
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
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error("Failed to get cluster, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_CLUSTER}", null);
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
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error("Failed to get database by cluster: {}, caused by: {}", request.getClusterName(), e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_DATABASE_BY_CLUSTER}", null);
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
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error("Failed to get table by database: {}.{}, caused by: {}", request.getClusterName(), request.getDbName(), e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_TABLE_BY_DATABASE}", null);
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
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error("Failed to get table by context service ID: {}, caused by: {}",  request.getCsId(), e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_TABLE_BY_CSID}", null);
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
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error("Failed to get column by table: {}.{}.{}, caused by: {}", request.getClusterName(), request.getDbName(), request.getTableName(), e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_COLUMN_BY_TABLE}", null);
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
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error("Failed to get column by table's context key: {}, caused by: {}", request.getContextKey(), e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_COLUMN_BY_TABLE}", null);
        }
    }

    @POST
    @Path("mul_db")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<MulDbResponse> addMultiDbRules(MulDbRequest request) throws UnExpectedRequestException {
        try {
            String dbs = metaDataService.addMultiDbRules(request);
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&SUCCESS_MUL_DBS_COMPARE}", new MulDbResponse(dbs));
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to create multi-db rules, caused by: {}", e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_ADD_MULTI_SOURCE_RULE}", null);
        }
    }

    @POST
    @Path("subSystemInfo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<SubSystemResponse>> getSubSystemInfo() throws UnExpectedRequestException {
        try {
            List<SubSystemResponse> subSystemResponses = operateCiService.getAllSubSystemInfo();
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_SUB_SYSTEM_INFO_SUCCESS}", subSystemResponses);
        } catch (Exception e) {
            LOGGER.error("Failed to get sub_system info, caused by: {}", e.getMessage());
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_SUB_SYSTEM_INFO_CMDB}", null);
        }
    }

    @POST
    @Path("productInfo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<ProductResponse>> getProductInfo() throws UnExpectedRequestException {
        try {
            List<ProductResponse> productResponses = operateCiService.getAllProductInfo();
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_PRODUCT_INFO_SUCCESS}", productResponses);
        } catch (Exception e) {
            LOGGER.error("Failed to get product info, caused by: {}", e.getMessage());
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_PRODUCT_INFO_CMDB}", null);
        }
    }

    @POST
    @Path("system/departmentInfo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<CmdbDepartmentResponse>> getAllDepartmentBySourceType() throws UnExpectedRequestException {
        return new GeneralResponse<>(ResponseStatusConstants.OK, "success", metaDataService.findAllDepartment(null));
    }

    @GET
    @Path("system/devAndOpsInfo/{deptCode}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<DepartmentSubResponse>> getSubDepartmentInfoBySourceType(@PathParam("deptCode") Integer deptCode) throws UnExpectedRequestException {
        return new GeneralResponse<>(ResponseStatusConstants.OK, "success", metaDataService.getSubDepartmentByDeptCode(null, deptCode));
    }

    @POST
    @Path("system/departmentInfoWithRole")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<CmdbDepartmentResponse>> getDepartmentInfoListByRoleType() throws UnExpectedRequestException {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_DEPARTMENT_INFO_SUCCESS}", metaDataService.getDepartmentInfoListByRoleType(null));
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get department info, caused by: {}", e.getMessage());
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_DEPARTMENT_INFO}", null);
        }
    }

    @GET
    @Path("system/devAndOpsInfoWithRole/{deptCode}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<DepartmentSubResponse>> getDevAndOpsInfoByRoleTypeAndSourceType(@PathParam("deptCode") Integer deptCode) throws UnExpectedRequestException {
        try {
            if (deptCode == null) {
                throw new UnExpectedRequestException("Dept code {&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_DEPARTMENT_INFO_SUCCESS}", metaDataService.getDevAndOpsInfoListByRoleType(null, deptCode));
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get department info, caused by: {}", e.getMessage());
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_DEPARTMENT_INFO}", null);
        }
    }

    @GET
    @Path("datamap/database")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> getDbFromDatamap(@QueryParam("search_key") String searchKey, @QueryParam("cluster_name") String clusterName
        , @QueryParam("proxy_user") String proxyUser) {
        try {
            String id = metaDataService.getDbFromDatamap(searchKey, clusterName, proxyUser);
            Map<String, Object> map = Maps.newHashMapWithExpectedSize(1);
            map.put("db_id", id);
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&SUCCESS_TO_GET_DATABASE_FROM_DATAMAP}", map);
        } catch (Exception e) {
            LOGGER.error("Failed to get database info from DataMap.", e);
            Map<String, Object> map = Maps.newHashMapWithExpectedSize(1);
            map.put("db_id", "");
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&FAILED_TO_GET_DATABASE_FROM_DATAMAP}", map);
        }
    }

    @GET
    @Path("datamap/table")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> getTableFromDatamap(@QueryParam("db_id") String dbId, @QueryParam("dataset_name") String datasetName
        , @QueryParam("cluster_name") String clusterName, @QueryParam("proxy_user") String proxyUser) {
        try {
            Integer id  = metaDataService.getDatasetFromDatamap(dbId, datasetName, clusterName, proxyUser);
            Map<String, Object> map = Maps.newHashMapWithExpectedSize(1);
            map.put("dataset_id", id);
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&SUCCESS_TO_GET_TABLE_FROM_DATAMAP}", map);
        } catch (Exception e) {
            LOGGER.error("Failed to get table info from DataMap.", e);
            Map<String, Object> map = Maps.newHashMapWithExpectedSize(1);
            map.put("dataset_id", "");
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&FAILED_TO_GET_TABLE__FROM_DATAMAP}", map);
        }
    }

    @GET
    @Path("datamap/column")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> getColumnFromDatamap(@QueryParam("dataset_id") Long datasetId, @QueryParam("field_name") String fieldName
        , @QueryParam("proxy_user") String proxyUser) {
        try {
            Map<String, Object> response = metaDataService.getColumnFromDatamap(datasetId,fieldName, proxyUser);
            String id = (String) ((List<Map<String, Object>>) response.get("content")).iterator().next().get("stdCode");
            Map<String, Object> map = Maps.newHashMapWithExpectedSize(1);
            map.put("std_code", id);
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&SUCCESS_TO_GET_COLUMN_FROM_DATAMAP}", map);
        } catch (Exception e) {
            LOGGER.error("Failed to get column info from DataMap.", e);
            Map<String, Object> map = Maps.newHashMapWithExpectedSize(1);
            map.put("std_code", "");
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&FAILED_TO_GET_COLUMN_FROM_DATAMAP}", map);
        }
    }

    @GET
    @Path("datamap/standard")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> getStandardFromDatamap(@QueryParam("std_code") String stdCode, @QueryParam("source") String source
        , @QueryParam("proxy_user") String proxyUser) {
        try {
            Map<String, Object> response = metaDataService.getDataStandardDetailFromDatamap(stdCode, source, proxyUser);
            String checkRule = (String) response.get("checkRule");
            Map<String, Object> map = Maps.newHashMapWithExpectedSize(1);
            map.put("check_rule", checkRule);
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&SUCCESS_TO_GET_STANDARD_FROM_DATAMAP}", map);
        } catch (Exception e) {
            LOGGER.error("Failed to get standard info from DataMap.", e);
            Map<String, Object> map = Maps.newHashMapWithExpectedSize(1);
            map.put("check_rule", "");
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&FAILED_TO_GET_STANDARD_FROM_DATAMAP}", map);
        }
    }

    @GET
    @Path("data_source/types/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> getAllDataSourceTypes(@QueryParam("proxyUser") String proxyUser) {
        try {
            String clusterName = linkisConfig.getDatasourceCluster();
            return metaDataService.getAllDataSourceTypes(clusterName, proxyUser);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "Failed", null);
        }
    }

    @GET
    @Path("data_source/types/custom")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> getDataSourceTypesAdapter(@QueryParam("proxyUser") String proxyUser) {
        try {
            String clusterName = linkisConfig.getDatasourceCluster();
            return metaDataService.getAllDataSourceTypes(clusterName, proxyUser);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "Failed", null);
        }
    }

    @GET
    @Path("data_source/env")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> getDataSourceEnv(@QueryParam("proxyUser") String proxyUser) {
        try {
            String clusterName = linkisConfig.getDatasourceCluster();
            return metaDataService.getDataSourceEnv(clusterName, proxyUser);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "Failed", null);
        }
    }

    @GET
    @Path("data_source/env/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<Map<String, Object>>> envList(@QueryParam("proxyUser") String proxyUser, @QueryParam("dataSourceId") Long dataSourceId, @QueryParam("versionId") Long versionId) {
        try {
            String clusterName = linkisConfig.getDatasourceCluster();
            return metaDataService.getEnvList(clusterName, proxyUser, dataSourceId, versionId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "Failed", null);
        }
    }

    @POST
    @Path("data_source/info/advance")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getDataSourceAdvanceInfo(QueryDataSourceRequest request) {
        try {
            return metaDataService.getDataSourceInfoWithAdvance(request);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "Failed", null);
        }
    }

    @GET
    @Path("data_source/versions")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> getDataSourceVersions(@QueryParam("proxyUser") String proxyUser
    , @QueryParam("dataSourceId") Long dataSourceId) {
        try {
            if (dataSourceId == null) {
                throw new UnExpectedRequestException("Data source ID" + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            String clusterName = linkisConfig.getDatasourceCluster();
            return metaDataService.getDataSourceVersions(clusterName, proxyUser, dataSourceId);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "Failed", null);
        }
    }

    @GET
    @Path("data_source/info")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> getDataSourceInfoPage(@QueryParam("currentPage") Integer currentPage, @QueryParam("pageSize") Integer pageSize, @QueryParam("name") String searchName
            , @QueryParam("typeId") Long typeId) {
        try {
            QueryDataSourceRequest request = new QueryDataSourceRequest();
            request.setName(searchName);
            request.setDataSourceTypeId(typeId);
            request.setPage(currentPage);
            request.setSize(pageSize);
            return metaDataService.getDataSourceInfoWithAdvance(request);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "Failed", null);
        }
    }

    @GET
    @Path("data_source/info/detail")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> getDataSourceInfoDetail(@QueryParam("proxyUser") String proxyUser
    , @QueryParam("dataSourceId") Long dataSourceId, @QueryParam("versionId") Long versionId) {
        try {
            if (dataSourceId == null) {
                throw new UnExpectedRequestException("Data source ID or version ID " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            String clusterName = linkisConfig.getDatasourceCluster();
            return metaDataService.getDataSourceInfoDetail(clusterName, proxyUser, dataSourceId, versionId);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "Failed", null);
        }
    }

    @GET
    @Path("data_source/key_define/type")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> getDataSourceKeyDefine(@QueryParam("proxyUser") String proxyUser, @QueryParam("keyId") Long keyId) {
        try {
            if (keyId == null) {
                throw new UnExpectedRequestException("Key ID " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            String clusterName = linkisConfig.getDatasourceCluster();
            return metaDataService.getDataSourceKeyDefine(clusterName, proxyUser, keyId);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "Failed", null);
        }
    }

    @POST
    @Path("data_source/connect")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> connectDataSource(@QueryParam("proxyUser") String proxyUser, @RequestBody DataSourceConnectRequest request) {
        try {
            if (request == null) {
                throw new UnExpectedRequestException("Request " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            String clusterName = linkisConfig.getDatasourceCluster();
            return metaDataService.connectDataSource(clusterName, proxyUser, request);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&PARAMS_ERROR_FOR_THIRD_PART_SERVICE}", null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&CONNECT_FAILED}", null);
        }
    }

    @POST
    @Path("data_source/publish")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> publishDataSource(@QueryParam("proxyUser") String proxyUser
        , @QueryParam("dataSourceId") Long dataSourceId, @QueryParam("versionId") Long versionId) {
        try {
            if (dataSourceId == null || versionId == null) {
                throw new UnExpectedRequestException("Request " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            String clusterName = linkisConfig.getDatasourceCluster();
            return metaDataService.publishDataSource(clusterName, proxyUser, dataSourceId, versionId);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&PARAMS_ERROR_FOR_THIRD_PART_SERVICE}", null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "Failed", null);
        }
    }

    @POST
    @Path("data_source/expire")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> expireDataSource(@QueryParam("proxyUser") String proxyUser
        , @QueryParam("dataSourceId") Long dataSourceId) {
        try {
            if (dataSourceId == null) {
                throw new UnExpectedRequestException("Request " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            String clusterName = linkisConfig.getDatasourceCluster();
            return metaDataService.expireDataSource(clusterName, proxyUser, dataSourceId);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&PARAMS_ERROR_FOR_THIRD_PART_SERVICE}", null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "Failed", null);
        }
    }

    @POST
    @Path("data_source/modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse modifyDataSource(@QueryParam("dataSourceId") Long dataSourceId, DataSourceModifyRequest request) throws PermissionDeniedRequestException {
        try {
            if (request == null || dataSourceId == null) {
                throw new UnExpectedRequestException("Request " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            String clusterName = linkisConfig.getDatasourceCluster();
            return metaDataService.modifyDataSource(clusterName, dataSourceId, request);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&PARAMS_ERROR_FOR_THIRD_PART_SERVICE}", null);
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }  catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "Failed", null);
        }
    }

    @POST
    @Path("data_source/param/modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse modifyDataSourceParam(@QueryParam("dataSourceId") Long dataSourceId, @RequestBody DataSourceParamModifyRequest request) {
        try {
            if (request == null || dataSourceId == null) {
                throw new UnExpectedRequestException("Request " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            String clusterName = linkisConfig.getDatasourceCluster();
            return metaDataService.modifyDataSourceParam(clusterName, dataSourceId, request);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&PARAMS_ERROR_FOR_THIRD_PART_SERVICE}", null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "Failed", null);
        }
    }

    @POST
    @Path("data_source/create")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse createDataSource(@RequestBody DataSourceModifyRequest request) throws PermissionDeniedRequestException {
        try {
            if (request == null) {
                throw new UnExpectedRequestException("Request " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            String clusterName = linkisConfig.getDatasourceCluster();
            return metaDataService.createDataSource(clusterName, request);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&PARAMS_ERROR_FOR_THIRD_PART_SERVICE}", null);
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }  catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        }
    }

    @GET
    @Path("data_source/dbs")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<DbInfoDetail>> getDbsByDataSource(@QueryParam("proxyUser") String proxyUser
        , @QueryParam("dataSourceId") Long dataSourceId, @QueryParam("envId") Long envId) {
        try {
            if (dataSourceId == null) {
                throw new UnExpectedRequestException("Request " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            String clusterName = linkisConfig.getDatasourceCluster();
            Map<String, Object> response = metaDataService.getDbsByDataSource(clusterName, proxyUser, dataSourceId, envId);
            GetAllResponse allResponse = new GetAllResponse();
            List<String> dbs = (List<String>) response.get("dbs");
            List<DbInfoDetail> dbInfoDetails = new ArrayList<>(CollectionUtils.isEmpty(dbs) ? 0 : dbs.size());
            dbs = dbs.stream().distinct().collect(Collectors.toList());
            for (String db : dbs) {
                DbInfoDetail dbInfoDetail = new DbInfoDetail(db);
                dbInfoDetails.add(dbInfoDetail);
            }
            allResponse.setTotal(CollectionUtils.isEmpty(dbInfoDetails) ? 0 : dbInfoDetails.size());
            allResponse.setData(dbInfoDetails);
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_DB_SUCCESSFULLY}", allResponse);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_DATABASE_BY_CLUSTER}", null);
        }
    }

    @GET
    @Path("data_source/tables")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<TableInfoDetail>> getTablesByDataSource(@QueryParam("proxyUser") String proxyUser
        , @QueryParam("dataSourceId") Long dataSourceId, @QueryParam("envId") Long envId, @QueryParam("dbName") String dbName) {
        try {
            if (dataSourceId == null || StringUtils.isBlank(dbName)) {
                throw new UnExpectedRequestException("Request " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            String clusterName = linkisConfig.getDatasourceCluster();
            Map<String, Object> response = metaDataService.getTablesByDataSource(clusterName, proxyUser, dataSourceId, dbName, envId);
            GetAllResponse allResponse = new GetAllResponse();
            List<String> tables = (List<String>) response.get("tables");
            List<TableInfoDetail> tableInfoDetails = new ArrayList<>(CollectionUtils.isEmpty(tables) ? 0 : tables.size());
            for (String table : tables) {
                TableInfoDetail tableInfoDetail = new TableInfoDetail(table);
                tableInfoDetails.add(tableInfoDetail);
            }
            allResponse.setTotal(CollectionUtils.isEmpty(tableInfoDetails) ? 0 : tableInfoDetails.size());
            allResponse.setData(tableInfoDetails);
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_TABLE_SUCCESSFULLY}", allResponse);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_TABLE_BY_DATABASE}", null);
        }
    }

    @GET
    @Path("data_source/columns")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<ColumnInfoDetail>> getColumnsByDataSource(@QueryParam("proxyUser") String proxyUser
        , @QueryParam("dataSourceId") Long dataSourceId, @QueryParam("envId") Long envId, @QueryParam("dbName") String dbName, @QueryParam("tableName") String tableName) {
        try {
            if (dataSourceId == null || StringUtils.isBlank(dbName) || StringUtils.isBlank(tableName)) {
                throw new UnExpectedRequestException("Request " + "{&CAN_NOT_BE_NULL_OR_EMPTY}");
            }
            String clusterName = linkisConfig.getDatasourceCluster();
            return metaDataService.getColumnsByDataSource(clusterName, proxyUser, dataSourceId, dbName, tableName, envId);
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_COLUMN_BY_TABLE}", null);
        }
    }

    @POST
    @Path("dcn")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<DcnResponse> getDcn(@QueryParam("sub_system_id") Long subSystemId) throws UnExpectedRequestException {
//        try {
//            return operateCiService.getDcn(subSystemId);
//        } catch (UnExpectedRequestException e) {
//            LOGGER.error(e.getMessage(), e);
//            throw e;
//        } catch (Exception e) {
//            LOGGER.error(e.getMessage(), e);
//            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "Failed to get dcn tdsql info.", null);
//        }
        DcnResponse dcnResponse = new DcnResponse();
        dcnResponse.setRes(Collections.emptyMap());
        return new GeneralResponse<>(ResponseStatusConstants.OK, "", dcnResponse);
    }

    @GET
    @Path("all/dataSourceName")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getDataSourceNameList() {
        return new GeneralResponse(ResponseStatusConstants.OK, "success", metaDataService.getDataSourceNameList());
    }
}
