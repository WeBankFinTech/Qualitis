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
import com.webank.wedatasphere.qualitis.request.FilterAdvanceRequest;
import com.webank.wedatasphere.qualitis.request.FilterApplicationIdRequest;
import com.webank.wedatasphere.qualitis.request.FilterDataSourceRequest;
import com.webank.wedatasphere.qualitis.request.FilterProjectRequest;
import com.webank.wedatasphere.qualitis.request.FilterStatusRequest;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.request.UploadResultRequest;
import com.webank.wedatasphere.qualitis.response.ApplicationClusterResponse;
import com.webank.wedatasphere.qualitis.response.ApplicationResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.service.ApplicationService;
import org.datanucleus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author howeye
 */
@Path("/api/v1/projector/application")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationController.class);

    @POST
    @Path("filter/status")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<ApplicationResponse>> filterStatusApplication(FilterStatusRequest request) throws UnExpectedRequestException {
        try {
            return applicationService.filterStatusApplication(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getResponse().getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to find applications. page: {}, size: {}, status: {}, caused by: {}", request.getPage(), request.getSize(),
                    request.getStatus(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_FIND_APPLICATIONS}.", null);
        }
    }

    @POST
    @Path("filter/project")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<ApplicationResponse>> filterProjectApplication(FilterProjectRequest request) throws UnExpectedRequestException {
        try {
            return applicationService.filterProjectApplication(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getResponse().getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to find applications. page: {}, size: {}, application_id: {}, caused by: {}", request.getPage(),
                    request.getSize(), request.getProjectId(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_FIND_APPLICATIONS}.", null);
        }
    }

    @POST
    @Path("filter/datasource")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<ApplicationResponse>> filterDataSourceApplication(FilterDataSourceRequest request) throws UnExpectedRequestException {
        try {
            return applicationService.filterDataSourceApplication(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getResponse().getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to find applications. page: {}, size: {}, cluster: {}, database: {}, table: {}, caused by: {}", request.getPage(),
                    request.getSize(), request.getClusterName(), request.getDatabaseName(), request.getTableName(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_FIND_APPLICATIONS}.", null);
        }
    }


    @POST
    @Path("datasource")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<ApplicationClusterResponse>> getDataSource(PageRequest pageRequest) throws UnExpectedRequestException {
        try {
            return applicationService.getDataSource(pageRequest);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getResponse().getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to find dataSources. page: {}, size: {}, caused by: {}", pageRequest.getPage(),
                    pageRequest.getSize(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_FIND_APPLICATIONS}", null);
        }
    }

    @POST
    @Path("upload")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Integer> uploadDataSourceAnalysisResult(UploadResultRequest request) throws UnExpectedRequestException {
        try {
            return applicationService.uploadDataSourceAnalysisResult(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        } catch (Exception e) {
            LOGGER.error("Failed to upload dataSources.", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_UPLOAD_ANALYSIS_EXCEL}", null);
        }
    }

    @POST
    @Path("filter/application_id")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<ApplicationResponse>> filterApplicationId(FilterApplicationIdRequest request)
        throws UnExpectedRequestException {
        FilterApplicationIdRequest.checkRequest(request);
        try {
            return applicationService.filterApplicationId(request.getApplicationId(), request.getFilterStatus(), request.getPage(), request.getSize(), request.getTaskPage(), request.getTaskSize());
        } catch (Exception e) {
            LOGGER.error("Failed to find application by application_id[{}],system exception.", request.getApplicationId(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_FIND_APPLICATIONS}", null);
        }
    }

    @POST
    @Path("filter/advance")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<ApplicationResponse>> filterAdvanceApplication(FilterAdvanceRequest request) throws UnExpectedRequestException {
        FilterAdvanceRequest.checkRequest(request);

        if (StringUtils.isEmpty(request.getStartTime())) {
            request.setStartTime("2019-01-01 00:00:00");
        }
        if (StringUtils.isEmpty(request.getEndTime())) {
            request.setEndTime("2099-01-01 23:59:59");
        }
        if (!StringUtils.isEmpty(request.getApplicationId())) {
            request.setApplicationId("%" + request.getApplicationId() + "%");
        }
        try {
            return applicationService.filterAdvanceApplication(request);
        } catch (Exception e) {
            LOGGER.error("Failed to find application.", e);
            return new GeneralResponse<>("500", "{&FAILED_TO_FIND_APPLICATIONS}", null);
        }
    }

    @POST
    @Path("get/All/execute/user")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<String>> getAllExecuteUser() {
        try {
            return applicationService.getAllExecuteUser();
        } catch (Exception e) {
            LOGGER.error("Failed to get execute user . caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_ALL_EXECUTE_USER}", null);
        }
    }

}
