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

package com.webank.wedatasphere.qualitis.checkalert.controller;

import com.webank.wedatasphere.qualitis.checkalert.request.CheckAlertRequest;
import com.webank.wedatasphere.qualitis.checkalert.request.QueryCheckAlertRequest;
import com.webank.wedatasphere.qualitis.checkalert.request.QueryWorkFlowRequest;
import com.webank.wedatasphere.qualitis.checkalert.response.CheckAlertResponse;
import com.webank.wedatasphere.qualitis.checkalert.service.CheckAlertService;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 * @author allenzhou
 */
@Path("api/v1/projector/checkAlert")
public class CheckAlertController {

    @Autowired
    private CheckAlertService checkAlertService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckAlertController.class);

    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<CheckAlertResponse> add(CheckAlertRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return checkAlertService.add(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }  catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to add check alert node rule, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_ADD_RULE}", null);
        }
    }

    @POST
    @Path("delete/{checkAlertId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<CheckAlertResponse> delete(@PathParam("checkAlertId") Long checkAlertId) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return checkAlertService.delete(checkAlertId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to delete check alert node rule, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_RULE}", null);
        }
    }

    @POST
    @Path("modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<CheckAlertResponse> modify(CheckAlertRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return checkAlertService.modify(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to modify check alert node rule, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_RULE_DETAIL}", null);
        }
    }

    @GET
    @Path("get/{checkAlertId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<CheckAlertResponse> getDetail(@PathParam("checkAlertId") Long checkAlertId) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return checkAlertService.get(checkAlertId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get check alert node rule, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_RULE_DETAIL}", null);
        }
    }

    @POST
    @Path("check")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> checkDatasource(CheckAlertRequest request) throws UnExpectedRequestException {
        try {
            return checkAlertService.checkDatasource(request.getAlertTable(), request.getAlertCol(), request.getMajorAlertCol(), request.getContentCols());
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("400", e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", "Failed to check datasource", null);
        }
    }


    /**
     * query projectDetail and ruleList by condition
     * @param request
     * @return
     */
    @POST
    @Path("query")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<CheckAlertResponse>> getCheckAlertQuery(QueryCheckAlertRequest request)throws UnExpectedRequestException, PermissionDeniedRequestException{
        request.convertParameter();
        try {
            return checkAlertService.getCheckAlertQuery(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get check alert, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_CHECK_ALERT_QUERY}", null);
        }
    }

    @POST
    @Path("condition/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> getConditionList(QueryWorkFlowRequest request) throws UnExpectedRequestException {
        try {
            Map<String, Object> deduplicationField = checkAlertService.getDeduplicationField(request);
            return new GeneralResponse("200", "{&GET_CHECK_ALERT_CONDITION_LIST_SUCCESSFULLY}"
                    , deduplicationField);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get check alert condition list, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_CHECK_ALERT_CONDITION_LIST}", null);
        }
    }

}
