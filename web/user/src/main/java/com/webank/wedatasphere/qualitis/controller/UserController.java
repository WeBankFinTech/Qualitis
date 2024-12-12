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
import com.webank.wedatasphere.qualitis.request.QueryUserRequest;
import com.webank.wedatasphere.qualitis.request.user.ModifyDepartmentRequest;
import com.webank.wedatasphere.qualitis.request.user.ModifyPasswordRequest;
import com.webank.wedatasphere.qualitis.request.user.UserAddRequest;
import com.webank.wedatasphere.qualitis.request.user.UserRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.user.AddUserResponse;
import com.webank.wedatasphere.qualitis.response.user.UserResponse;
import com.webank.wedatasphere.qualitis.service.RoleService;
import com.webank.wedatasphere.qualitis.service.UserService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
@Path("/api/v1")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @PUT
    @Path("admin/user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<AddUserResponse> addUser(UserAddRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return userService.addUser(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to create user, username: {}, caused by: {}, current_user: {}", request.getUsername(), e.getMessage(), username, e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_CREATE_USER}", null);
        }
    }

    @POST
    @Path("admin/user/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> deleteUser(UserRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return userService.deleteUser(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to delete user, userId: {}, caused by: {}, current_user: {}", request.getUserId(), e.getMessage(), username, e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_DELETE_USER}", null);
        }
    }

    @POST
    @Path("admin/user/init_password")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<String> initPassword(UserRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return userService.initPassword(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to init password of user, user_id: {}, caused by: {}, current_user: {}", request.getUserId(), e.getMessage(), username, e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_INIT_PASSWORD}", null);
        }
    }

    @POST
    @Path("admin/user/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<UserResponse>> findAllUser(QueryUserRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return userService.findAllUser(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to find all users, page: {}, size: {}, caused by: {}, current_user: {}", request.getPage(), request.getSize(), e.getMessage(), username, e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_FIND_ALL_USERS}", null);
        }
    }

    @GET
    @Path("admin/user/name/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<String>> findAllUserName(@Context HttpServletRequest httpServletRequest) {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&FIND_ALL_USERS_SUCCESSFULLY}", userService.findAllUserName());
        } catch (Exception e) {
            LOGGER.error("Failed to find all user names, caused by: {}, current_user: {}", e.getMessage(), username, e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_FIND_ALL_USERS}", null);
        }
    }

    @POST
    @Path("admin/user/modify_department")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> modifyDepartment(ModifyDepartmentRequest request) throws UnExpectedRequestException {
        try {
            return userService.modifyDepartment(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to modify department, request: {}, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_MODIFY_DEPARTMENT}", null);
        }
    }

    @POST
    @Path("projector/user/modify_password")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Object> modifyPassword(ModifyPasswordRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return userService.modifyPassword(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to modify password, userId: {}, caused by: {}", HttpUtils.getUserId(httpServletRequest), e.getMessage(), username, e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_MODIFY_PASSWORD}", null);
        }
    }

    @POST
    @Path("position/role/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<Map<String, Object>>> getPositionRoleConstant() {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_POSITION_ROLE_ENUMN_SUCCESSFULLY}", userService.getPositionRoleEnum());
        } catch (Exception e) {
            LOGGER.error("Failed to get Scheduled System enumn, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_POSITION_ROLE_ENUMN}", null);
        }
    }

    @GET
    @Path("projector/proxy_user")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<String>> getUserProxyUser(@Context HttpServletRequest httpServletRequest) {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return roleService.getProxyUserByUser();
        } catch (Exception e) {
            LOGGER.error("Failed to get proxy user of user: {}, caused by: {}, current_user: {}", username, e.getMessage(), username, e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_PROXY_USER_OF_USER}" + username + ", caused by " + e.getMessage(), null);
        }
    }

}
