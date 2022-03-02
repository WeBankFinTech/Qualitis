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

package com.webank.wedatasphere.qualitis.project.controller;

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.AuthorizeProjectUserRequest;
import com.webank.wedatasphere.qualitis.project.response.ProjectUserResponse;
import com.webank.wedatasphere.qualitis.project.service.ProjectUserService;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author allenzhou
 */
@Path("api/v1/projector/project_user")
public class ProjectUserController {

    @Autowired
    private ProjectUserService projectUserService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectUserController.class);

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<ProjectUserResponse> addProjectUser(AuthorizeProjectUserRequest request, @Context HttpServletRequest httpServletRequest)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            Long userId = HttpUtils.getUserId(httpServletRequest);
            return projectUserService.authorizePermission(request, userId, false);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to authorize project user, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_ADD_PROJECT_USER}", null);
        }
    }

    @POST
    @Path("all/{projectId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<ProjectUserResponse>> getAllProjectUser(@PathParam("projectId") Long projectId)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return projectUserService.getAllProjectUser(projectId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get authorized user, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_PROJECT_USER}", null);
        }
    }

    @POST
    @Path("modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<ProjectUserResponse> modifyProjectUser(AuthorizeProjectUserRequest request, @Context HttpServletRequest httpServletRequest)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            Long userId = HttpUtils.getUserId(httpServletRequest);
            return projectUserService.authorizePermission(request, userId, true);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to modify authorize project, project id: {}, caused by system error: {}", request.getProjectId(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_ADD_PROJECT_USER}", null);
        }
    }

    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> deleteProjectUser(AuthorizeProjectUserRequest request, @Context HttpServletRequest httpServletRequest)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            Long userId = HttpUtils.getUserId(httpServletRequest);
            return projectUserService.deletePermission(request, userId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to delete project user, project id: {}, caused by system error: {}", request.getProjectId(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_USER}", null);
        }
    }

    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> getUser(@Context HttpServletRequest httpServletRequest)
        throws UnExpectedRequestException {
        try {
            Long userId = HttpUtils.getUserId(httpServletRequest);
            PageRequest pageRequest = new PageRequest(0, Integer.MAX_VALUE);
            return projectUserService.getAllUsers(pageRequest, userId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to find users, caused by system error: {}", e.getMessage());
            return new GeneralResponse<>("500", "{&FAILED_TO_FIND_ALL_USERS}", null);
        }
    }
}
