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

import com.webank.wedatasphere.qualitis.request.permission.AddPermissionRequest;
import com.webank.wedatasphere.qualitis.request.permission.DeletePermissionRequest;
import com.webank.wedatasphere.qualitis.request.permission.ModifyPermissionRequest;
import com.webank.wedatasphere.qualitis.response.AddUserTenantUserResponse;
import com.webank.wedatasphere.qualitis.response.PermissionResponse;
import com.webank.wedatasphere.qualitis.service.PermissionService;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.permission.AddPermissionRequest;
import com.webank.wedatasphere.qualitis.request.permission.DeletePermissionRequest;
import com.webank.wedatasphere.qualitis.request.permission.ModifyPermissionRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.PermissionResponse;
import com.webank.wedatasphere.qualitis.service.PermissionService;
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
@Path("/api/v1/admin/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionController.class);

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<PermissionResponse> addPermission(AddPermissionRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return permissionService.addPermission(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to add permission, method: {}, url: {}, caused by: {}, current_user: {}", request.getMethod(), request.getUrl(), e.getMessage(), username, e);
            return new GeneralResponse<>("500", "{&FAILED_TO_ADD_PERMISSION}", null);
        }
    }

    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse deletePermission(DeletePermissionRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return permissionService.deletePermission(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to delete permission, permissionId: {}, caused by: {}, current_user: {}", request.getPermissionId(), e.getMessage(), username, e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_PERMISSION}", null);
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse modifyPermission(ModifyPermissionRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return permissionService.modifyPermission(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to modify permission, permissionId: {}, caused by: {}, current_user: {}", request.getPermissionId(), e.getMessage(), username, e);
            return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_PERMISSION}", null);
        }
    }

    @POST
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<PermissionResponse>> getAllPermission(PageRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return permissionService.getAllPermission(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get all permission, page: {}, size: {}, caused by: {}, current_user: {}", request.getPage(), request.getSize(), e.getMessage(), username, e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_PERMISSION}", null);
        }
    }

}
