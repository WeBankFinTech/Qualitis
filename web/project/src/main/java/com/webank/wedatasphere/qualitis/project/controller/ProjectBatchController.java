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

import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.request.DownloadProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.UploadProjectRequest;
import com.webank.wedatasphere.qualitis.project.service.ProjectBatchService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.FileService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author howeye
 */
@Path("api/v1/projector/project/batch")
public class ProjectBatchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectBatchController.class);

    @Autowired
    private ProjectBatchService projectBatchService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private FileService fileService;

    @POST
    @Path("upload_zip_from_local")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public GeneralResponse uploadZip(@FormDataParam("file") InputStream fileInputStream, @FormDataParam("file") FormDataContentDisposition fileDisposition)
        throws UnExpectedRequestException {
        try {
            int fileSize = fileInputStream.available();
            LOGGER.info("Upload zip file size: {} MB", fileSize / (1024 * 1024));
            int maxFileSize = 10 * 1024 * 1024;
            if(fileSize > maxFileSize) {
                throw new UnExpectedRequestException("File size exceeds limit: 10 MB");
            }
            return fileService.uploadFile(fileInputStream, fileDisposition, "");
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to upload project's zip, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        }
    }

    @POST
    @Path("upload")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse uploadProjects(UploadProjectRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        List<Project> projectLists= Lists.newArrayList();
        try {
            if (null != request.getProjectId()) {
                projectLists = projectBatchService.checkProjects(Arrays.asList(request.getProjectId()));
            }
            return projectBatchService.uploadProjectFromLocalOrGit(request, false);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            projectService.batchSaveAndFlushProject(projectLists);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            projectService.batchSaveAndFlushProject(projectLists);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to upload projects, caused by system error: {}", e.getMessage(), e);
            projectService.batchSaveAndFlushProject(projectLists);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        }
    }

    @POST
    @Path("download")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse downloadProject(DownloadProjectRequest downloadProjectRequest, @Context HttpServletResponse response)
        throws UnExpectedRequestException {
        List<Project> projectLists= Lists.newArrayList();
        try {
            projectLists = projectBatchService.checkProjects(downloadProjectRequest.getProjectId());
            return projectBatchService.downloadProjectsToLocalOrGit(downloadProjectRequest, response);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            projectService.batchSaveAndFlushProject(projectLists);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to download projects and rules, caused by system error: {}", e.getMessage(), e);
            projectService.batchSaveAndFlushProject(projectLists);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_DOWNLOAD_PROJECTS_AND_RULES}", null);
        }
    }

    @POST
    @Path("download_files_to_git")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse downloadProjectToGit(DownloadProjectRequest downloadProjectRequest, @Context HttpServletResponse response)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        List<Project> projectLists= Lists.newArrayList();
        try {
            projectLists = projectBatchService.checkProjects(downloadProjectRequest.getProjectId());
            return projectBatchService.downloadProjectsToLocalOrGit(downloadProjectRequest, response);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            projectService.batchSaveAndFlushProject(projectLists);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            projectService.batchSaveAndFlushProject(projectLists);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to download projects and rules, caused by system error: {}", e.getMessage(), e);
            projectService.batchSaveAndFlushProject(projectLists);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_DOWNLOAD_PROJECTS_AND_RULES}", null);
        }
    }

    @GET
    @Path("diff_variables")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse diffVariables() {
        try {
            return projectBatchService.diffVariables();
        } catch (Exception e) {
            LOGGER.error("Failed to list diff variables, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "Failed to list diff variables", null);
        }
    }
}
