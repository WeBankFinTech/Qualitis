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

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.AddServiceInfoRequest;
import com.webank.wedatasphere.qualitis.request.DeleteServiceInfoRequest;
import com.webank.wedatasphere.qualitis.request.FindServiceInfoRequest;
import com.webank.wedatasphere.qualitis.request.ModifyServiceInfoRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.ServiceInfoResponse;
import com.webank.wedatasphere.qualitis.service.ServiceInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author allenzhou
 */
@Path("api/v1/admin/service_info")
public class ServiceInfoController {

    @Autowired
    private ServiceInfoService serviceInfoService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceInfoController.class);

    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<ServiceInfoResponse> addServiceInfo(AddServiceInfoRequest request) throws UnExpectedRequestException {
        try {
            return serviceInfoService.addServiceInfo(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to add service info. request: {}, caused by: {}", request, e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_ADD_SERVICE_INFO}", null);
        }
    }

    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> deleteServiceInfo(DeleteServiceInfoRequest request) throws UnExpectedRequestException {
        try {
            return serviceInfoService.deleteServiceInfo(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw  e;
        } catch (Exception e) {
            LOGGER.error("Failed to delete service info. service info ID: {}, caused by: {}", request.getId(), e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_DELETE_SERVICE_INFO}", null);
        }
    }

    @POST
    @Path("modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<ServiceInfoResponse> modifyServiceInfo(ModifyServiceInfoRequest request) throws UnExpectedRequestException {
        try {
            return serviceInfoService.modifyServiceInfo(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to modify service info, service info ID: {}, caused by: {}", request.getId(), e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_MODIFY_SERVICE_INFO}", null);
        }
    }

    @POST
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<ServiceInfoResponse>> findAllServiceInfo(FindServiceInfoRequest request) throws UnExpectedRequestException {
        try {
            return serviceInfoService.findAllServiceInfo(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to find service infos. page: {}, size: {}, caused by: {}", request.getPage(), request.getSize(), e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_FIND_SERVICE_INFOS}", null);
        }
    }

    @GET
    @Path("tenant_user/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<ServiceInfoResponse>> findFromTenantUser() throws UnExpectedRequestException {
        try {
            return serviceInfoService.findFromTenantUser();
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_FIND_SERVICE_INFOS}", null);
        }
    }

}
