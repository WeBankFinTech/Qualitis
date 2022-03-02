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

import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author howeye
 */
@Path("outer/api/v1/user")
public class OuterUserController {

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(OuterUserController.class);

    @POST
    @Path("{username}")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> generalExecution(@PathParam("username")String username) {
        try {
            userService.autoAddUser(username);
            return new GeneralResponse<>("200", "{&CREATE_USER_SUCCESSFULLY}", null);
        } catch (Exception e) {
            LOGGER.error("Failed to add user, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_CREATE_USER}, caused by: " + e.getMessage(), null);
        }
    }

}
