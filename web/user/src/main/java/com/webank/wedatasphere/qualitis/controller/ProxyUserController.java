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
import com.webank.wedatasphere.qualitis.request.AddProxyUserRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.request.ModifyProxyUserRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.DeleteProxyUserRequest;
import com.webank.wedatasphere.qualitis.service.ProxyUserService;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.AddProxyUserRequest;
import com.webank.wedatasphere.qualitis.request.ModifyProxyUserRequest;
import com.webank.wedatasphere.qualitis.response.DeleteProxyUserRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.ProxyUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author howeye
 */
@Path("/api/v1/admin/proxy_user")
public class ProxyUserController {

    @Autowired
    private ProxyUserService proxyUserService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyUserController.class);

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> addProxyUser(AddProxyUserRequest request) throws UnExpectedRequestException {
        try {
            return proxyUserService.addProxyUser(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to create proxy user, proxy user: {}, caused by: {}", request.getProxyUserName(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_CREATE_PROXY_USER}", null);
        }
    }

    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> deleteProxyUser(DeleteProxyUserRequest request) throws UnExpectedRequestException {
        try {
            return proxyUserService.deleteProxyUser(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to delete proxy user, proxy_user_id: {}, caused by: {}", request.getProxyUserId(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_PROXY_USER}", null);
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> modifyProxyUser(ModifyProxyUserRequest request) throws UnExpectedRequestException {
        try {
            return proxyUserService.modifyProxyUser(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to modify proxy user name to {}, proxy_user_id: {}, caused by: {}", request.getProxyUserName(), request.getProxyUserId(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_PROXY_USER}", null);
        }
    }

    @POST
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> getAllProxyUser(PageRequest request) throws UnExpectedRequestException {
        try {
            return proxyUserService.getAllProxyUser(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get all proxy users, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_ALL_PROXY_USERS}", null);
        }
    }

}
