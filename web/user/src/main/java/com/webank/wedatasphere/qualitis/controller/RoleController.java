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
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.request.role.RoleAddRequest;
import com.webank.wedatasphere.qualitis.request.role.RoleModifyRequest;
import com.webank.wedatasphere.qualitis.request.role.RoleRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.RoleResponse;
import com.webank.wedatasphere.qualitis.service.RoleService;
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
@Path("api/v1/admin/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleController.class);

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RoleResponse> addRole(RoleAddRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return roleService.addRole(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to add role, role: {}, caused by: {}, current_user: {}", request.getRoleName(), e.getMessage(), username, e);
            return new GeneralResponse<>("500", "{&FAILED_TO_ADD_ROLE}", null);
        }
    }

    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse deleteRole(RoleRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return roleService.deleteRole(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to delete role, role_id : {}, cause by: {}, current_user: {}", request.getRoleId(), e.getMessage(), username, e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_ROLE}", null);
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse modifyRole(RoleModifyRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return roleService.modifyRole(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to modify role, role_id: {}, caused by: {}, current_user: {}", request.getRoleId(), e.getMessage(), username,  e);
            return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_ROLE}", null);
        }
    }


    @POST
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<RoleResponse>> getAllRole(PageRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        String username = null;
        try {
            username = HttpUtils.getUserName(httpServletRequest);
            return roleService.getAllRole(request);
        } catch (UnExpectedRequestException e) {
          throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to find all roles, page: {}, size: {}, caused by: {}, current_user: {}", request.getPage(), request.getSize(), e.getMessage(), username, e);
            return new GeneralResponse<>("500", "{&FAILED_TO_FIND_ALL_ROLES}", null);
        }
    }

    @POST
    @Path("type/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getRoleTypeEnumn() {
        try {
            return new GeneralResponse<>("200", "{&GET_ROLE_TYPE_ENUMN_SUCCESSFULLY}", roleService.getAllRoleTypeEnum());
        } catch (Exception e) {
            LOGGER.error("Failed to get Scheduled System enumn, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_ROLE_TYPE_ENUMN}", e.getMessage());
        }
    }


}
