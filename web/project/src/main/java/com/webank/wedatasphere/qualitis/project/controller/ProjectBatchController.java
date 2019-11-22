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

import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.DownloadProjectRequest;
import com.webank.wedatasphere.qualitis.project.service.ProjectBatchService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.exception.WriteExcelException;
import com.webank.wedatasphere.qualitis.project.request.DownloadProjectRequest;
import com.webank.wedatasphere.qualitis.project.service.ProjectBatchService;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

/**
 * @author howeye
 */
@Path("api/v1/projector/project/batch")
public class ProjectBatchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectBatchController.class);

    @Autowired
    private ProjectBatchService projectBatchService;

    @POST
    @Path("upload")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public GeneralResponse<?> uploadProjects(@FormDataParam("file") InputStream fileInputStream, @FormDataParam("file") FormDataContentDisposition fileDisposition)
            throws UnExpectedRequestException {
        try {
            return projectBatchService.uploadProjects(fileInputStream, fileDisposition);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error("Failed to get cluster mapping. DataMap api response error, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        }  catch (SemanticException e) {
            LOGGER.error("Failed to get db and table from sql. Database and table must be written as follow: [db.table]. caused by: {}, ", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_DB_AND_TABLE_FROM_SQL}", null);
        } catch (ParseException e) {
            LOGGER.error("Failed to parse sql. please check your sql. caused by: {}, ", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_PARSE_SQL}", null);
        } catch (Exception e) {
            LOGGER.error("Failed to upload projects, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_UPLOAD_PROJECTS}, caused by: " + e.getMessage(), null);
        }
    }

    @POST
    @Path("download")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> downloadProjects(DownloadProjectRequest downloadProjectRequest, @Context HttpServletResponse response) throws UnExpectedRequestException {
        try {
            return projectBatchService.downloadProjects(downloadProjectRequest, response);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (WriteExcelException e) {
            LOGGER.error("Failed to write projects and rules, caused by : {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_WRITE_PROJECTS_AND_RULES}", null);
        } catch (Exception e) {
            LOGGER.error("Failed to download projects and rules, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DOWNLOAD_PROJECTS_AND_RULES}", null);
        }
    }
}
