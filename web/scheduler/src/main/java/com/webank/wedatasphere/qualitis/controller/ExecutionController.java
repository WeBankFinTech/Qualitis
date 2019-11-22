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
import com.webank.wedatasphere.qualitis.request.ProjectExecutionRequest;
import com.webank.wedatasphere.qualitis.request.RuleListExecutionRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.ExecutionService;
import com.webank.wedatasphere.qualitis.request.ProjectExecutionRequest;
import com.webank.wedatasphere.qualitis.request.RuleListExecutionRequest;
import com.webank.wedatasphere.qualitis.service.ExecutionService;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionController.class);

    @POST
    @Path("project")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> projectExecution(ProjectExecutionRequest request) throws UnExpectedRequestException {
        try {
            return executionService.projectExecution(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to execute project, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_EXECUTE_PROJECT}, caused by: " + e.getMessage(), null);
        }
    }

    @POST
    @Path("rule")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> ruleListExecution(RuleListExecutionRequest request) throws UnExpectedRequestException {
        try {
            return executionService.ruleListExecution(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to execute rule list, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_EXECUTE_RULE_LIST}, caused by: " + e.getMessage(), null);
        }
    }

}
