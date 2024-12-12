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
import com.webank.wedatasphere.qualitis.project.request.AddProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.DeleteProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.GetProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.ModifyProjectDetailRequest;
import com.webank.wedatasphere.qualitis.project.response.ProjectDetailResponse;
import com.webank.wedatasphere.qualitis.project.service.OuterWorkflowService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.project.request.AddProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.DeleteProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.ModifyProjectDetailRequest;
import com.webank.wedatasphere.qualitis.project.response.ProjectDetailResponse;
import com.webank.wedatasphere.qualitis.project.service.OuterWorkflowService;
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
@Path("outer/api/v1/project/workflow")
public class OuterWorkflowController {


    @Autowired
    private OuterWorkflowService outerWorkflowService;

    private static final Logger LOGGER = LoggerFactory.getLogger(OuterWorkflowController.class);

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<ProjectDetailResponse> addWorkflowProject(AddProjectRequest request, @Context HttpServletRequest httpServletRequest)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return outerWorkflowService.addWorkflowProject(request, request.getUsername());
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to add workflow project. caused by : {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_ADD_PROJECT}", null);
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> modifyWorkflowProjectDetail(ModifyProjectDetailRequest request)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return outerWorkflowService.modifyWorkflowProjectDetail(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to modify workflow project. project_id: {}, caused by: {}", request.getProjectId(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_PROJECT}", null);
        }
    }

    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> deleteWorkflowProject(DeleteProjectRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return outerWorkflowService.deleteWorkflowProject(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to delete workflow project. project_id: {}, caused by: {}", request.getProjectId(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_PROJECT}", null);
        }
    }

    @POST
    @Path("get")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<ProjectDetailResponse> getWorkflowProject(GetProjectRequest request) throws UnExpectedRequestException {
        try {
            GetProjectRequest.checkRequest(request);
            LOGGER.info("Get project request: ", request.toString());
            return outerWorkflowService.getWorkflowProject(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get workflow project. Project name: {}, caused by: {}", request.getName(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_PROJECT}", null);
        }
    }

}
