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

import com.webank.wedatasphere.qualitis.constant.InvokeTypeEnum;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.GroupListExecutionRequest;
import com.webank.wedatasphere.qualitis.request.KillApplicationsRequest;
import com.webank.wedatasphere.qualitis.request.ProjectExecutionRequest;
import com.webank.wedatasphere.qualitis.request.RuleListExecutionRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.ClusterInfoService;
import com.webank.wedatasphere.qualitis.service.ExecutionService;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author howeye
 */
@Path("/api/v1/projector/execution")
public class ExecutionController {

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private ClusterInfoService clusterInfoService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionController.class);

    @POST
    @Path("project")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> projectExecution(ProjectExecutionRequest request) throws UnExpectedRequestException {
        try {
            return executionService.projectExecution(request, InvokeTypeEnum.UI_INVOKE.getCode());
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getResponse().getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to execute project, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_EXECUTE_PROJECT}, caused by: " + e.getMessage(), null);
        }
    }

    @POST
    @Path("group")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> groupListExecution(GroupListExecutionRequest request) throws UnExpectedRequestException {
        try {
            return executionService.ruleGroupListExecution(request, InvokeTypeEnum.UI_INVOKE.getCode());
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getResponse().getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to execute rule list, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_EXECUTE_RULE_LIST}, caused by: " + e.getMessage(), null);
        }
    }

    @POST
    @Path("rule")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> ruleListExecution(RuleListExecutionRequest request) throws UnExpectedRequestException {
        try {
            return executionService.ruleListExecution(request, InvokeTypeEnum.UI_INVOKE.getCode());
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getResponse().getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to execute rule list, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_EXECUTE_RULE_LIST}, caused by: " + e.getMessage(), null);
        }
    }

    @GET
    @Path("application/kill/{applicationId}/{executionUser}")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> killApplication(@PathParam("applicationId") String applicationId, @PathParam("executionUser") String executionUser)
        throws UnExpectedRequestException {
        try {
            return executionService.killApplication(applicationId, executionUser);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getResponse().getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to kill application: {}", applicationId, e);
            return new GeneralResponse<>("500", "{&FAILED_TO_KILL_TASK}: " + applicationId, e);
        }
    }

    @POST
    @Path("application/batch/kill")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> killBatchApplication(KillApplicationsRequest request) {
        executionService.killApplications(request);
        return new GeneralResponse<>("200", "{&SUCCESS_ASYNC_KILL_TASK}", null);
    }
}
