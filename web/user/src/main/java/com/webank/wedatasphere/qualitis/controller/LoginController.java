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

import com.webank.wedatasphere.qualitis.request.LocalLoginRequest;
import com.webank.wedatasphere.qualitis.service.LoginService;
import com.webank.wedatasphere.qualitis.service.RoleService;
import com.webank.wedatasphere.qualitis.exception.LoginFailedException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.LocalLoginRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.LoginService;
import com.webank.wedatasphere.qualitis.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * @author howeye
 */
@Path("api/v1")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private RoleService roleService;

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @POST
    @Path("login/local")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> localLogin(LocalLoginRequest request, @Context HttpServletRequest httpServletRequest) throws LoginFailedException, UnExpectedRequestException {
        try {
            return loginService.localLogin(request);
        } catch (LoginFailedException e) {
            LOGGER.error("Failed to login. user: {}, caused by: {}", HttpUtils.getUserName(httpServletRequest), e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error("Failed to login. user: {}, caused by: {}", HttpUtils.getUserName(httpServletRequest), e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error("Failed to login. user: {}, caused by: {}", HttpUtils.getUserName(httpServletRequest), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_LOGIN}.", null);
        }
    }

    @GET
    @Path("projector/role")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> getRole(@Context HttpServletRequest httpServletRequest) {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return roleService.getRoleByUser();
        } catch (Exception e) {
            LOGGER.error("Failed to get role of user: {}, caused by: {}, current_user: {}", username, e.getMessage(), username, e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_ROLE_OF_USER} :" + username + ", caused by :" + e.getMessage(), null);
        }
    }

    @GET
    @Path("projector/proxy_user")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> getUserProxyUser(@Context HttpServletRequest httpServletRequest) {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return roleService.getProxyUserByUser();
        } catch (Exception e) {
            LOGGER.error("Failed to get proxy user of user: {}, caused by: {}, current_user: {}", username, e.getMessage(), username, e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_PROXY_USER_OF_USER}" + username + ", caused by " + e.getMessage(), null);
        }
    }

    @GET
    @Path("logout")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> logout(@Context HttpServletRequest httpServletRequest, @Context HttpServletResponse httpServletResponse) {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return loginService.logout(httpServletRequest, httpServletResponse);
        } catch (Exception e) {
            LOGGER.error("Failed to logout, user: {}, current_user: {}", username, username, e);
            return new GeneralResponse<>("500", "{&FAILED_TO_LOGOUT}", null);
        }
    }

}
