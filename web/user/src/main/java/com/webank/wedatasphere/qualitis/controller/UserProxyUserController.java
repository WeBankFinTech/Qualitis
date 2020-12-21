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
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.request.AddUserProxyUserRequest;
import com.webank.wedatasphere.qualitis.request.DeleteUserProxyUserRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.service.UserProxyUserService;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.AddUserProxyUserRequest;
import com.webank.wedatasphere.qualitis.request.DeleteUserProxyUserRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.UserProxyUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author howeye
 */
@Path("api/v1/admin/user_proxy_user")
public class UserProxyUserController {

    @Autowired
    private UserProxyUserService userProxyUserService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProxyUserController.class);

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> addUserProxyUser(AddUserProxyUserRequest request) throws UnExpectedRequestException {
        try {
            return userProxyUserService.addUserProxyUser(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to create user_proxy_user, username: {}, proxy_user_name: {}, caused by: {}", request.getUsername(), request.getProxyUserName(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_CREATE_USER_PROXY_USER}", null);
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> deleteUserProxyUser(DeleteUserProxyUserRequest request) throws UnExpectedRequestException {
        try {
            return userProxyUserService.deleteUserProxyUser(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to delete user_proxy_user, user_proxy_user_id: {}, caused by: {}", request.getUserProxyUserId(), e.getMessage());
            return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_USER_PROXY_USER}", null);
        }
    }

    @POST
    @Path("{proxyUserName}/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> getAllUserProxyUserByProxyUserName(@PathParam("proxyUserName")String proxyUserName, PageRequest request) throws UnExpectedRequestException {
        try {
            return userProxyUserService.getAllUserProxyUserByProxyUserName(proxyUserName, request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get all user_proxy_user by proxy_user name, proxy_user_name: {}, caused by: {}", proxyUserName.replace("\r", "").replace("\n", ""),
                e.getMessage().replace("\r", "").replace("\n", ""));
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_ALL_USER_PROXY_USER_BY_PROXY_USER_NAME}", null);
        }
    }



}
