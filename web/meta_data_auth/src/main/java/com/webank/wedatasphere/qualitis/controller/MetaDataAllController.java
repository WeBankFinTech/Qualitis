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

import com.webank.wedatasphere.qualitis.entity.MetaDataCluster;
import com.webank.wedatasphere.qualitis.entity.MetaDataColumn;
import com.webank.wedatasphere.qualitis.entity.MetaDataDb;
import com.webank.wedatasphere.qualitis.entity.MetaDataTable;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.request.QueryMetaColumnRequest;
import com.webank.wedatasphere.qualitis.request.QueryMetaDbRequest;
import com.webank.wedatasphere.qualitis.request.QueryMetaTableRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.QueryMetaClusterResponse;
import com.webank.wedatasphere.qualitis.response.QueryMetaColumnResponse;
import com.webank.wedatasphere.qualitis.response.QueryMetaDbResponse;
import com.webank.wedatasphere.qualitis.response.QueryMetaTableResponse;
import com.webank.wedatasphere.qualitis.service.MetaDataQueryService;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.QueryMetaColumnRequest;
import com.webank.wedatasphere.qualitis.request.QueryMetaDbRequest;
import com.webank.wedatasphere.qualitis.request.QueryMetaTableRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.QueryMetaClusterResponse;
import com.webank.wedatasphere.qualitis.service.MetaDataQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Providing query api in meta data management
 *
 * @author v_wblwyan
 * @date 2019-04-18
 */
@Path("api/v1/admin/meta_data_all")
public class MetaDataAllController {

  private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataAllController.class);
  @Autowired
  private MetaDataQueryService metaDataQueryService;

  /**
   * Paging get cluster information
   * @param request
   * @return
   */
  @POST
  @Path("cluster")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<QueryMetaClusterResponse> queryCluster(PageRequest request) {
    try {
      if (request == null) {
        request = new PageRequest();
      }
      List<MetaDataCluster> data = metaDataQueryService.queryCluster(request);
      long total = metaDataQueryService.countClusterAll();
      return new GeneralResponse<>("200", String.format("get cluster successfully for request:%s",
                                                        request.toString()),
                                   new QueryMetaClusterResponse(data, total));
    } catch (Exception e) {
      LOGGER.error("Failed to get cluster for request:{}, caused by: {}", request.toString(),
                   e.getMessage(), e);
      return new GeneralResponse<>("500", String.format(
          "Failed to get cluster for request:%s, caused by: %s", request.toString(),
          e.getMessage()), null);
    }
  }

  /**
   * Paging get database information
   * @param request
   * @return
   * @throws UnExpectedRequestException
   */
  @POST
  @Path("db")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<QueryMetaDbResponse> queryDb(QueryMetaDbRequest request)
      throws UnExpectedRequestException {
    request.checkRequest();
    try {
      List<MetaDataDb> data = metaDataQueryService.queryDb(request);
      long total = metaDataQueryService.countDbByCluster(request);
      return new GeneralResponse<>("200", String.format("get db successfully for request:%s",
              request.toString()),
                                   new QueryMetaDbResponse(data, total));
    } catch (UnExpectedRequestException e) {
      throw new UnExpectedRequestException(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Failed to get db for request:{}, caused by: {}", request.toString(),
                   e.getMessage(), e);
      return new GeneralResponse<>("500",
                                   String.format("Failed to get db for request:%s, caused by: %s",
                                           request.toString(), e.getMessage()), null);
    }
  }

  /**
   * Paging get table information
   * @param request
   * @return
   * @throws UnExpectedRequestException
   */
  @POST
  @Path("table")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<QueryMetaTableResponse> queryTable(QueryMetaTableRequest request)
      throws UnExpectedRequestException {
    request.checkRequest();
    try {
      List<MetaDataTable> data = metaDataQueryService.queryTable(request);
      long total = metaDataQueryService.countTableByDb(request);
      return new GeneralResponse<>("200", String.format("get table successfully for request:%s",
              request.toString()),
                                   new QueryMetaTableResponse(data, total));
    } catch (UnExpectedRequestException e) {
      throw new UnExpectedRequestException(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Failed to get table for request:{}, caused by: {}", request.toString(),
                   e.getMessage(), e);
      return new GeneralResponse<>("500", String.format(
          "Failed to get table for request:%s, caused by: %s", request.toString(),
          e.getMessage()), null);
    }
  }

  /**
   * Paging get field information
   * @param request
   * @return
   * @throws UnExpectedRequestException
   */
  @POST
  @Path("column")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public GeneralResponse<QueryMetaColumnResponse> queryColumn(QueryMetaColumnRequest request)
      throws UnExpectedRequestException {
    request.checkRequest();
    try {
      List<MetaDataColumn> data = metaDataQueryService.queryColumn(request);
      long total = metaDataQueryService.countColumnByTable(request);
      return new GeneralResponse<>("200", String.format("get column successfully for request:%s",
              request.toString()),
                                   new QueryMetaColumnResponse(data, total));
    } catch (UnExpectedRequestException e) {
      throw new UnExpectedRequestException(e.getMessage());
    } catch (Exception e) {
      LOGGER.error("Failed to get column for request:{}, caused by: {}", request.toString(),
                   e.getMessage(), e);
      return new GeneralResponse<>("500", String.format(
          "Failed to get column for request:%s, caused by: %s", request.toString(),
          e.getMessage()), null);
    }
  }

}
