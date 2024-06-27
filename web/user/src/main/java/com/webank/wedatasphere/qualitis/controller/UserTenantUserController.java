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
import com.webank.wedatasphere.qualitis.request.AddUserTenantUserRequest;
import com.webank.wedatasphere.qualitis.request.DeleteUserTenantUserRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.AddUserTenantUserResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.service.UserTenantUserService;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author howeye
 */
@Path("api/v1/admin/user_tenant_user")
public class UserTenantUserController {

    @Autowired
    private UserTenantUserService userTenantUserService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTenantUserController.class);

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<AddUserTenantUserResponse> addUserTenantUser(AddUserTenantUserRequest request) throws UnExpectedRequestException {
        try {
            return userTenantUserService.addUserTenantUser(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to create user tenant user, user name: {}, tenant user name: {}, caused by: {}", request.getUsername(), request.getTenantUserName(), e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_CREATE_USER_TENANT_USER}", null);
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse deleteUserTenantUser(DeleteUserTenantUserRequest request) throws UnExpectedRequestException {
        try {
            return userTenantUserService.deleteUserTenantUser(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to delete user tenant user, user tenant user_id: {}, caused by: {}", request.getUserTenantUserId(), e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_DELETE_USER_TENANT_USER}", null);
        }
    }

    @POST
    @Path("{tenantUserName}/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<AddUserTenantUserResponse>> getAllUserTenantUserByTenantUserName(@PathParam("tenantUserName")String tenantUserName, PageRequest request) throws UnExpectedRequestException {
        try {
            return userTenantUserService.getAllUserTenantUserByTenantUserName(tenantUserName, request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get all user tenant user by tenant_user name, tenant user name: {}, caused by: {}", tenantUserName, e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_ALL_USER_TENANT_USERS}", null);
        }
    }



}
