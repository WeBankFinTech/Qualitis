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

import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.rule.service.RuleService;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;

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
@Path("api/v1/projector/rule")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    @Autowired
    private ProjectEventService projectEventService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleController.class);
    private HttpServletRequest httpServletRequest;
    public RuleController(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleResponse> addRule(AddRuleRequest request) throws UnExpectedRequestException {
        try {
            // Record project event.
//            String loginUser = HttpUtils.getUserName(httpServletRequest);
//            projectEventService.record(request.getProjectId(), loginUser, "add", "rule[name= " + request.getRuleName() + "].", EventTypeEnum.MODIFY_PROJECT.getCode());
            return ruleService.addRule(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to add rule. caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_ADD_RULE}", null);
        }
    }

    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> deleteRule(DeleteRuleRequest request) throws UnExpectedRequestException {
        try {
            String loginUser = HttpUtils.getUserName(httpServletRequest);
            return ruleService.deleteRule(request, loginUser);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
	        throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to delete rule. rule_id: {}, caused by system error: {}", request.getRuleGroupId(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_RULE}", null);
        }
    }

    @POST
    @Path("modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleResponse> modifyRuleDetail(ModifyRuleRequest request) throws UnExpectedRequestException {
        try {
            return ruleService.modifyRuleDetail(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
	        throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to modify rule detail. rule_id: {}, caused by system error: {}", request.getRuleId(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_RULE_DETAIL}", null);
        }
    }

    @GET
    @Path("/{rule_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleDetailResponse> getRuleDetail(@PathParam("rule_id")Long ruleId) throws UnExpectedRequestException {
        try {
            return ruleService.getRuleDetail(ruleId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
	        throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get rule detail. rule_id: {}, caused by system error: {}", ruleId, e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_RULE_DETAIL}", null);
        }
    }

}
