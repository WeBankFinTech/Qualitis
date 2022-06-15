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

package com.webank.wedatasphere.qualitis.rule.controller;

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.request.multi.AddMultiSourceRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.DeleteMultiSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.ModifyMultiSourceRequest;
import com.webank.wedatasphere.qualitis.rule.service.MultiSourceRuleService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author howeye
 */
@Path("api/v1/projector/mul_source_rule")
public class MultiSourceRuleController {
    @Autowired
    private ProjectEventService projectEventService;

    @Autowired
    private MultiSourceRuleService multiSourceRuleService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiSourceRuleController.class);

    private HttpServletRequest httpServletRequest;
    public MultiSourceRuleController(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * Add multi-table rule
     * @return
     */
    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> addMultiSourceRule(AddMultiSourceRuleRequest request) throws UnExpectedRequestException {
        try {
            // Record project event.
//            String loginUser = HttpUtils.getUserName(httpServletRequest);
//            projectEventService.record(request.getProjectId(), loginUser, "add", "multi source rule[name= " + request.getRuleName() + "].", EventTypeEnum.MODIFY_PROJECT.getCode());
            return multiSourceRuleService.addMultiSourceRule(request, true);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to add multi_source rule, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_ADD_MULTI_SOURCE_RULE}", null);
        }
    }

    /**
     * Delete multi-table rule
     * @return
     */
    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> deleteMultiSourceRule(DeleteMultiSourceRequest request)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            String loginUser = HttpUtils.getUserName(httpServletRequest);
            return multiSourceRuleService.deleteMultiSourceRule(request, loginUser);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to delete multi_source rule, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_MULTI_SOURCE_RULE}", null);
        }
    }

    /**
     * Modify multi-table rule
     * @return
     */
    @POST
    @Path("modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> modifyMultiSourceRule(ModifyMultiSourceRequest request)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return multiSourceRuleService.modifyMultiSourceRule(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to modify multi_source rule, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_MULTI_SOURCE_RULE}", null);
        }
    }

    /**
     * Get multi-table rule detail
     * @return
     */
    @GET
    @Path("/{rule_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> getMultiSourceRule(@PathParam("rule_id")Long ruleId) throws UnExpectedRequestException {
        try {
            return multiSourceRuleService.getMultiSourceRule(ruleId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get details of multi_source rule, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_DETAILS_OF_MULTI_SOURCE_RULE}", null);
        }
    }

}
