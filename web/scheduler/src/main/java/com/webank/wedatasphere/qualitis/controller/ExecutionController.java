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
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.GroupListExecutionRequest;
import com.webank.wedatasphere.qualitis.request.KillApplicationsRequest;
import com.webank.wedatasphere.qualitis.request.ProjectExecutionRequest;
import com.webank.wedatasphere.qualitis.request.RuleListExecutionRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.ClusterInfoService;
import com.webank.wedatasphere.qualitis.service.ExecutionService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.ExecutionException;

/**
 * @author howeye
 */
@Path("/api/v1/projector/execution")
public class ExecutionController {

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private ClusterInfoService clusterInfoService;

    private HttpServletRequest httpServletRequest;

    public ExecutionController(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionController.class);

    @POST
    @Path("project")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse projectExecution(ProjectExecutionRequest request) throws UnExpectedRequestException, InterruptedException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        try {
            GeneralResponse generalResponse = executionService.commonHandleRuleOrProjectMethod(request, null, InvokeTypeEnum.UI_INVOKE.getCode(), loginUser);
            return new GeneralResponse<>(generalResponse.getCode(), generalResponse.getMessage(), generalResponse.getData());
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted!", e);
            Thread.currentThread().interrupt();
            throw new InterruptedException(e.getMessage());
        } catch (ExecutionException | PermissionDeniedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        }
    }

    @POST
    @Path("group")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse groupListExecution(GroupListExecutionRequest request) throws UnExpectedRequestException, InterruptedException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        try {
            GeneralResponse generalResponse = executionService.handleRuleGroupListMethod(request, InvokeTypeEnum.UI_INVOKE.getCode(), loginUser);
            return new GeneralResponse<>(generalResponse.getCode(), generalResponse.getMessage(), generalResponse.getData());
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted!", e);
            Thread.currentThread().interrupt();
            throw new InterruptedException(e.getMessage());
        } catch (ExecutionException | PermissionDeniedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        }
    }

    @POST
    @Path("rule")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse ruleListExecution(RuleListExecutionRequest request) throws UnExpectedRequestException, InterruptedException {
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        try {
            GeneralResponse<?> generalResponse = executionService.commonHandleRuleOrProjectMethod(null, request, InvokeTypeEnum.UI_INVOKE.getCode(), loginUser);
            return new GeneralResponse<>(generalResponse.getCode(), generalResponse.getMessage(), generalResponse.getData());
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted!", e);
            Thread.currentThread().interrupt();
            throw new InterruptedException(e.getMessage());
        } catch (ExecutionException | PermissionDeniedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        }
    }

    @GET
    @Path("application/kill/{applicationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse killApplication(@PathParam("applicationId") String applicationId)
            throws UnExpectedRequestException {
        try {
            return executionService.killApplication(applicationId);
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
    public GeneralResponse killBatchApplication(KillApplicationsRequest request) {
        executionService.killApplications(request);
        return new GeneralResponse<>("200", "{&SUCCESS_ASYNC_KILL_TASK}", null);
    }

    @GET
    @Path("schedule/max_num")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getMaxExecutionNum() {

        return new GeneralResponse<>("200", "success", executionService.getMaxExecutionNum());
    }
}
