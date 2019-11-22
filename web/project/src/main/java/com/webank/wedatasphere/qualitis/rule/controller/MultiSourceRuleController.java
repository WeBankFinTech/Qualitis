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
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.request.multi.AddMultiSourceRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.DeleteMultiSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.ModifyMultiSourceRequest;
import com.webank.wedatasphere.qualitis.rule.service.MultiSourceRuleService;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.request.multi.AddMultiSourceRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.DeleteMultiSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.ModifyMultiSourceRequest;
import com.webank.wedatasphere.qualitis.rule.service.MultiSourceRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author howeye
 */
@Path("api/v1/projector/mul_source_rule")
public class MultiSourceRuleController {

    @Autowired
    private MultiSourceRuleService multiSourceRuleService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiSourceRuleController.class);

    /**
     * Add multi-table rule
     * @return
     */
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> addMultiSourceRule(AddMultiSourceRuleRequest request) throws UnExpectedRequestException {
        try {
            return multiSourceRuleService.addMultiSourceRule(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to add multi_source rule. caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_ADD_MULTI_SOURCE_RULE}", null);
        }
    }

    /**
     * Delete multi-table rule
     * @return
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> deleteMultiSourceRule(DeleteMultiSourceRequest request) throws UnExpectedRequestException {
        try {
            return multiSourceRuleService.deleteMultiSourceRule(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to delete multi_source rule. caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_MULTI_SOURCE_RULE}", null);
        }
    }

    /**
     * Modify multi-table rule
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> modifyMultiSourceRule(ModifyMultiSourceRequest request) throws UnExpectedRequestException {
        try {
            return multiSourceRuleService.modifyMultiSourceRule(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to modify multi_source rule. caused by: {}", e.getMessage(), e);
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
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get details of multi_source rule. caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_DETAILS_OF_MULTI_SOURCE_RULE}", null);
        }
    }

}
