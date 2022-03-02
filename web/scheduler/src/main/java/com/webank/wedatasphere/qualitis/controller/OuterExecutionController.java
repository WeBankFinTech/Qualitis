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
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.request.ApplicationQueryRequest;
import com.webank.wedatasphere.qualitis.request.GeneralExecutionRequest;
import com.webank.wedatasphere.qualitis.request.GetTaskLogRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.OuterExecutionService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
            if (request.getAsync()) {
                LOGGER.info("Start to axync run submit application.");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            outerExecutionService.generalExecution(request);
                        } catch (UnExpectedRequestException e) {
                            LOGGER.error(e.getMessage(), e);;
                        } catch (Exception e) {
                            LOGGER.error("Async failed exception.", e);
                        }
                    }
                }).start();

                return new GeneralResponse<>("200", "{&SUCCESS_ASYNC_SUBMIT_TASK}", null);
            } else {
                return outerExecutionService.generalExecution(request);
            }
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to dispatch application, caused by: {}. Exception: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DISPATCH_APPLICATION}", e);
        }
    }

    @GET
    @Path("execution/application/kill/{applicationId}/{executionUser}")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> killApplication(@PathParam("applicationId") String applicationId, @PathParam("executionUser") String executionUser) throws UnExpectedRequestException {
        try {
            return outerExecutionService.killApplication(applicationId, executionUser);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getResponse().getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to kill application: {}", applicationId, e);
            return new GeneralResponse<>("500", "{&FAILED_TO_KILL_TASK}: " + applicationId, e);
        }
    }

    @GET
    @Path("application/{applicationId}/status")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> getApplicationStatus(@PathParam("applicationId") String applicationId) throws UnExpectedRequestException{
        try {
            return outerExecutionService.getApplicationStatus(applicationId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get application status. application_id: {}, caused by: {}", applicationId, e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_APPLICATION_STATUS}， caused by: " + e.getMessage(), e);
        }
    }

    @GET
    @Path("application/dynamic/{applicationId}/status")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> getApplicationDynamicStatus(@PathParam("applicationId") String applicationId) throws UnExpectedRequestException{
        try {
            return outerExecutionService.getApplicationDynamicStatus(applicationId);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getResponse().getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get application status. application_id: {}, caused by: {}", applicationId, e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_APPLICATION_STATUS}， caused by: " + e.getMessage(), e);
        }
    }

    @GET
    @Path("application/{applicationId}/result")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> getApplicationResult(@PathParam("applicationId") String applicationId) throws UnExpectedRequestException {
        try {
            return outerExecutionService.getApplicationResult(applicationId);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getResponse().getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get result of application: {}", applicationId, e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_RESULT_OF_APPLICATION}: " + applicationId, e);
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
            throw new UnExpectedRequestException(e.getResponse().getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get log of the task: {}, cluster_id: {}", request.getTaskId(), request.getClusterId(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_LOG_OF_THE_TASK}", e);
        }
    }
}
