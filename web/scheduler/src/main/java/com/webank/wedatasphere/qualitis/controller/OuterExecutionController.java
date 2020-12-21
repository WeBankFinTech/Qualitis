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

import com.webank.wedatasphere.qualitis.request.GeneralExecutionRequest;
import com.webank.wedatasphere.qualitis.exception.*;
import com.webank.wedatasphere.qualitis.request.GetTaskLogRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.OuterExecutionService;
import com.webank.wedatasphere.qualitis.request.GeneralExecutionRequest;
import com.webank.wedatasphere.qualitis.request.GetTaskLogRequest;
import com.webank.wedatasphere.qualitis.service.OuterExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author howeye
 */
@Path("outer/api/v1")
public class OuterExecutionController {

    @Autowired
    private OuterExecutionService outerExecutionService;

    private static final Logger LOGGER = LoggerFactory.getLogger(OuterExecutionController.class);

    @POST
    @Path("execution")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> generalExecution(GeneralExecutionRequest request) throws UnExpectedRequestException {
        try {
            return outerExecutionService.generalExecution(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to dispatch application, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DISPATCH_APPLICATION}, caused by: " + e.getMessage(), e);
        }
    }

    @GET
    @Path("application/{applicationId}/status/")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> getApplicationStatus(@PathParam("applicationId") String applicationId) throws UnExpectedRequestException{
        try {
            return outerExecutionService.getApplicationStatus(applicationId);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get application status. application_id: {}, caused by: {}", applicationId.replace("\r", "").replace("\n", ""),
                e.getMessage().replace("\r", "").replace("\n", ""));
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_APPLICATION_STATUS}， caused by: " + e.getMessage(), e);
        }
    }

    @POST
    @Path("log")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> getTaskLog(GetTaskLogRequest request) throws UnExpectedRequestException {
        try {
            return outerExecutionService.getTaskLog(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get log of the task: {}, cluster_id: {}", request.getTaskId(), request.getClusterId(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_LOG_OF_THE_TASK}", e);
        }
    }

    @GET
    @Path("application/{applicationId}/result")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> getApplicationResult(@PathParam("applicationId") String applicationId) throws UnExpectedRequestException {
        try {
            return outerExecutionService.getApplicationResult(applicationId);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get result of application: {}", applicationId, e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_RESULT_OF_APPLICATION}: " + applicationId, e);
        }
    }
}
