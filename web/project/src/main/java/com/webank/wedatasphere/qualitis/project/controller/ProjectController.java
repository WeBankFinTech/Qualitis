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

import com.webank.wedatasphere.qualitis.project.request.AddProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.DeleteProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.ModifyProjectDetailRequest;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.response.ProjectDetailResponse;
import com.webank.wedatasphere.qualitis.project.response.ProjectResponse;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.project.request.AddProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.DeleteProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.ModifyProjectDetailRequest;
import com.webank.wedatasphere.qualitis.project.response.ProjectDetailResponse;
import com.webank.wedatasphere.qualitis.project.response.ProjectResponse;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
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
@Path("api/v1/projector/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<ProjectDetailResponse> addProject(AddProjectRequest request, @Context HttpServletRequest httpServletRequest) throws UnExpectedRequestException {
        try {
            Long userId = HttpUtils.getUserId(httpServletRequest);
            return projectService.addProject(request, userId);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to add project. caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_ADD_PROJECT}", null);
        }
    }

    @POST
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<ProjectResponse>> getAllProjectByUser(PageRequest request) throws UnExpectedRequestException {
        try {
            return projectService.getAllProjectByUser(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get project, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_ALL_PROJECT}", null);
        }
    }

    @GET
    @Path("detail/{projectId}")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<ProjectDetailResponse> getProjectDetail(@PathParam("projectId") Long projectId) throws UnExpectedRequestException {
        try {
            return projectService.getProjectDetail(projectId);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get project detail, project_id: {}, caused by: {}", projectId.toString().replace("\r", "")
                .replace("\n", ""), e.getMessage().replace("\r", "").replace("\n", ""), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_PROJECT_DETAIL}", null);
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> modifyProjectDetail(ModifyProjectDetailRequest request) throws UnExpectedRequestException {
        try {
            return projectService.modifyProjectDetail(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to modify project. project_id: {}, caused by: {}", request.getProjectId(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_PROJECT}", null);
        }
    }

    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> deleteProject(DeleteProjectRequest request) throws UnExpectedRequestException {
        try {
            return projectService.deleteProject(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to delete project. project_id: {}, caused by: {}", request.getProjectId(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_PROJECT}", null);
        }
    }

}
