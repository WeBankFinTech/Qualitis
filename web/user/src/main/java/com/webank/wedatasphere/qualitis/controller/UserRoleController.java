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
import com.webank.wedatasphere.qualitis.request.QueryUserRequest;
import com.webank.wedatasphere.qualitis.request.userrole.AddUserRoleRequest;
import com.webank.wedatasphere.qualitis.request.userrole.DeleteUserRoleRequest;
import com.webank.wedatasphere.qualitis.request.userrole.ModifyUserRoleRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.UserRoleResponse;
import com.webank.wedatasphere.qualitis.service.UserRoleService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * @author howeye
 */
@Path("/api/v1/admin/user_role")
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleController.class);

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<UserRoleResponse> addUserRole(AddUserRoleRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return userRoleService.addUserRole(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to add user_role, user_id: {}, role_id: {}, caused by: {}, current_user: {}", request.getUserId(), request.getRoleId(),
                    e.getMessage(), username, e);
            return new GeneralResponse<>("500", "{&FAILED_TO_ADD_USE_ROLE}", null);
        }
    }

    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse deleteUserRole(DeleteUserRoleRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return userRoleService.deleteUserRole(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to delete user_role, uuid: {}, caused by: {}, current_user: {}", request.getUuid(), e.getMessage(), username, e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_USE_ROLE}", null);
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse modifyUserRole(ModifyUserRoleRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return userRoleService.modifyUserRole(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to modify user_role, uuid: {}, caused by: {}, current_user: {}", request.getUuid(), e.getMessage(), username, e);
            return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_USE_ROLE}", null);
        }
    }

    @POST
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<UserRoleResponse>> findAllUserRole(QueryUserRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return userRoleService.findAllUserRole(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to find user_role, page: {}, size: {}, caused by: {}, current_user: {}", request.getPage(), request.getSize(),
                    e.getMessage(), username, e);
            return new GeneralResponse<>("500", "{&FAILED_TO_FIND_USE_ROLE}", null);
        }
    }

}
