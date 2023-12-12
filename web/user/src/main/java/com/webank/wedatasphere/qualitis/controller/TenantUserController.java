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
import com.webank.wedatasphere.qualitis.request.AddTenantUserRequest;
import com.webank.wedatasphere.qualitis.request.DeleteTenantUserRequest;
import com.webank.wedatasphere.qualitis.request.ModifyTenantUserRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.request.QueryTenantUserRequest;
import com.webank.wedatasphere.qualitis.response.AddTenantUserResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.service.TenantUserService;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author allenzhou
 */
@Path("/api/v1/admin/tenant_user")
public class TenantUserController {

    @Autowired
    private TenantUserService tenantUserService;

    private static final Logger LOGGER = LoggerFactory.getLogger(TenantUserController.class);

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<AddTenantUserResponse> addTenantUser(AddTenantUserRequest request) throws UnExpectedRequestException {
        try {
            return tenantUserService.addTenantUser(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to create tenant user, tenant user: {}, caused by: {}", request.getTenantUserName(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_CREATE_TENANT_USER}", null);
        }
    }

    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse deleteTenantUser(DeleteTenantUserRequest request) throws UnExpectedRequestException {
        try {
            return tenantUserService.deleteTenantUser(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to delete tenant user, tenant user id: {}, caused by: {}", request.getTenantUserId(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_TENANT_USER}", null);
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse modifyTenantUser(ModifyTenantUserRequest request) throws UnExpectedRequestException {
        try {
            return tenantUserService.modifyTenantUser(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to modify tenant user name to {}, tenant user id: {}, caused by: {}", request.getTenantUserName(), request.getTenantUserId(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_TENANT_USER}", null);
        }
    }

    @POST
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<AddTenantUserResponse>> getAllTenantUser(QueryTenantUserRequest request) throws UnExpectedRequestException {
        try {
            return tenantUserService.getAllTenantUser(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get all tenant users, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_ALL_TENANT_USERS}", null);
        }
    }

}
