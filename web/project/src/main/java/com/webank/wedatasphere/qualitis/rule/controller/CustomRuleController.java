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

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.request.AddCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.service.CustomRuleService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author howeye
 */
@Path("api/v1/projector/rule/custom")
public class CustomRuleController {

    @Autowired
    private CustomRuleService customRuleService;

    @Autowired
    private ProjectEventService projectEventService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleController.class);

    private HttpServletRequest httpServletRequest;
    public CustomRuleController(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> addCustomRule(AddCustomRuleRequest request) throws UnExpectedRequestException {
        try {
            // Record project event.
//            String loginUser = HttpUtils.getUserName(httpServletRequest);
//            projectEventService.record(request.getProjectId(), loginUser, "add", "custom rule[name= " + request.getRuleName() + "].", EventTypeEnum.MODIFY_PROJECT.getCode());
            return customRuleService.addCustomRule(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
	        throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to add custom rule, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_ADD_CUSTOM_RULE}", null);
        }
    }

    /**
     * Delete custom rule
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> deleteCustomRule(DeleteCustomRuleRequest request) throws UnExpectedRequestException {
        try {
            String loginUser = HttpUtils.getUserName(httpServletRequest);
            return customRuleService.deleteCustomRule(request, loginUser);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
	        throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to delete custom rule, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_CUSTOM_RULE}", null);
        }
    }

    /**
     * Get custom rule detail
     * @param ruleId
     * @return
     * @throws UnExpectedRequestException
     */
    @GET
    @Path("{rule_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> getCustomRuleDetail(@PathParam("rule_id")Long ruleId) throws UnExpectedRequestException {
        try {
            return customRuleService.getCustomRuleDetail(ruleId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
	        throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get custom rule detail, rule_id: {}, caused by system error: {}", ruleId, e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_CUSTOM_RULE_DETAIL}", null);
        }
    }

    /**
     * Modify custom rule
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    @POST
    @Path("modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> modifyCustomRule(ModifyCustomRuleRequest request) throws UnExpectedRequestException {
        try {
            return customRuleService.modifyCustomRule(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
	        throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to modify custom rule, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_CUSTOM_RULE}", null);
        }
    }

}
