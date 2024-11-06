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
import com.webank.wedatasphere.qualitis.request.userpermission.AddUserSpecPermissionRequest;
import com.webank.wedatasphere.qualitis.request.userpermission.DeleteUserSpecPermissionRequest;
import com.webank.wedatasphere.qualitis.request.userpermission.ModifyUserSpecPermissionRequest;
import com.webank.wedatasphere.qualitis.response.UserSpecPermissionResponse;
import com.webank.wedatasphere.qualitis.service.UserSpecPermissionService;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.userpermission.AddUserSpecPermissionRequest;
import com.webank.wedatasphere.qualitis.request.userpermission.DeleteUserSpecPermissionRequest;
import com.webank.wedatasphere.qualitis.request.userpermission.ModifyUserSpecPermissionRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.UserSpecPermissionResponse;
import com.webank.wedatasphere.qualitis.service.UserSpecPermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * @author howeye
 */
@Path("api/v1/admin/user_spec_permission")
public class UserSpecPermissionController {

    @Autowired
    private UserSpecPermissionService userSpecPermissionService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSpecPermissionController.class);

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<UserSpecPermissionResponse> addUserSpecPermission(AddUserSpecPermissionRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return userSpecPermissionService.addUserSpecPermission(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to add user_permission. user_id: {}, permission_id: {}, caused by: {}, current_user: {}", request.getUserId(),
                    request.getPermissionId(), e.getMessage(), username, e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_ADD_USER_PERMISSION}", null);
        }
    }

    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse deleteUserSpecPermission(DeleteUserSpecPermissionRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return userSpecPermissionService.deleteUserSpecPermission(request);
        } catch (UnExpectedRequestException e) {
            throw  new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to delete user_permission, uuid: {}, caused by: {}, current_user: {}", request.getUuid(), e.getMessage(), username, e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_DELETE_USER_PERMISSION}", null);
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse modifyUserSpecPermission(ModifyUserSpecPermissionRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return userSpecPermissionService.modifyUserSpecPermission(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to modify user permission. uuid: {}, caused by: {}, current_user: {}", request.getUuid(), e.getMessage(), username, e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_MODIFY_USER_PERMISSION}", null);
        }
    }

    @POST
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<UserSpecPermissionResponse>> findAllUserSpecPermission(PageRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return userSpecPermissionService.findAllUserSpecPermission(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to find user_permission. page: {}, size: {}, caused by: {}, current_user: {}", request.getPage(), request.getSize(),
                    e.getMessage(), username, e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_FIND_USER_PERMISSION}", null);
        }
    }

}
