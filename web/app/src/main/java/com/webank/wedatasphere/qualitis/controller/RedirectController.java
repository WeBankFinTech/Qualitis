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
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.LoginService;
import com.webank.wedatasphere.qualitis.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.management.relation.RoleNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * @author howeye
 */
@Component
@Path("api/v1/redirect")
public class RedirectController {

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;
    @Autowired
    private LoginService loginService;

    @Value("${workflow.enable}")
    private Boolean workflowEnable;

    private static final Logger LOGGER = LoggerFactory.getLogger(RedirectController.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse redirectToCoordinatePage(@Context HttpServletRequest httpServletRequest, @Context HttpServletResponse httpServletResponse) {
        if (workflowEnable) {
            LOGGER.info("Succeed to get redirect url");
//            try {
//                // Invoke SDK to login
//                AppJointAuth appJointAuth = AppJointAuth.getAppJointAuth();
//                if (appJointAuth.isDssRequest(httpServletRequest)) {
//                    RedirectMsg redirectMsg = appJointAuth.getRedirectMsg(httpServletRequest);
//
//                    String redirectUrl = redirectMsg.getRedirectUrl();
//                    String username = redirectMsg.getUser();
//                    LOGGER.info("Succeed to get redirect url: {}, and username: {}", redirectUrl, username);
//
//                    // Login in my system
//                    loginByUser(username, httpServletRequest);
//
//                    httpServletResponse.sendRedirect(redirectUrl);
//                    return null;
//                } else {
//                    return new GeneralResponse(ResponseStatusConstants.BAD_REQUEST, "{&NOT_A_DSS_REQUEST}", null);
//                }
//            } catch (Exception e) {
//                LOGGER.error("Failed to redirect to other page, caused by: {}", e.getMessage(), e);
//                return new GeneralResponse(ResponseStatusConstants.BAD_REQUEST, "Failed to redirect to other page.", null);
//            }
            return new GeneralResponse<>(ResponseStatusConstants.OK, "Succeed to get redirect url", null);
        } else {
            return new GeneralResponse<>(ResponseStatusConstants.BAD_REQUEST, "Workflow is not enabled", null);
        }
    }

//    private void loginByUser(String username, HttpServletRequest request) {
//        // 查询数据库，看用户是否存在
//        User userInDb = userDao.findByUsername(username);
//        if (userInDb != null) {
//            // 放入session
//            LOGGER.info("User: {} succeed to login", username);
//            loginService.addToSession(username, request);
//        } else {
//            // 自动创建用户
//            LOGGER.warn("user: {}, do not exist, trying to create user", username);
//            try {
//                userService.autoAddUser(username);
//                loginService.addToSession(username, request);
//            } catch (RoleNotFoundException e) {
//                LOGGER.error("Failed to auto add user, cause by: Failed to get role [PROJECTOR]", e);
//            }
//        }
//
//    }

}
