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
import com.webank.wedatasphere.qualitis.service.TransferUserService;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.TransferUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author howeye
 */
@Path("api/v1/admin/transfer_user")
public class TransferUserController {

    @Autowired
    private TransferUserService transferUserService;

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferUserController.class);

    @GET
    @Path("{proxy_username}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> transferUser(@PathParam("proxy_username")String proxyUsername) throws UnExpectedRequestException {
        try {
            return transferUserService.transferUser(proxyUsername);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to transfer to user: {}, caused by: {}", proxyUsername, e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_TRANSFER_TO_USER}: " + proxyUsername +", caused by: " + e.getMessage(), null);
        }
    }

    @GET
    @Path("exit")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> exitUser() {
        try {
            return transferUserService.exitUser();
        } catch (Exception e) {
            LOGGER.error("Failed to exit user: {}, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_EXIT_USER}, caused by: " + e.getMessage(), null);
        }
    }

}
