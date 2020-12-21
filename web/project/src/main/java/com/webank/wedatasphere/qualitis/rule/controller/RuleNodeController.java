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
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.request.DeleteRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.RuleNodeRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleNodeResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.rule.service.RuleNodeService;
import com.webank.wedatasphere.qualitis.rule.service.RuleService;
import java.io.IOException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author allenzhou
 */
@Path("outer/api/v1/projector/rule")
public class RuleNodeController {

    @Autowired
    private RuleService ruleService;
    @Autowired
    private RuleNodeService ruleNodeService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleNodeController.class);

    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> deleteRule(DeleteRuleRequest request) throws UnExpectedRequestException {
        try {
            return ruleNodeService.deleteRule(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to delete rule. rule_id: {}, caused by: {}", request.getRuleGroupId(), e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_RULE}", null);
        }
    }

    @GET
    @Path("/export/{ruleGroupId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleNodeResponse> exportRuleByGroupId(@PathParam("ruleGroupId")Long ruleGroupId) throws UnExpectedRequestException {
        try {
            return ruleNodeService.exportRuleByGroupId(ruleGroupId);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to get rule detail. ruleGroupId: {}, caused by: {}", ruleGroupId.toString().replace("\r", "").replace("\n", ""),
                e.getMessage().replace("\r", "").replace("\n", ""), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_EXPORT_RULE}", null);
        }
    }

    @PUT
    @Path("/import")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleResponse> importRule(RuleNodeRequest ruleNodeRequest) throws UnExpectedRequestException{
        CommonChecker.checkObject(ruleNodeRequest, "ruleNodeRequest");
        try {
            return ruleNodeService.importRule(ruleNodeRequest);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to import rule. ruleNodeRequest: {}, caused by: {}", ruleNodeRequest, e.getMessage());
            return new GeneralResponse<>("500", "{&FAILED_TO_IMPORT_RULE}", null);
        }
    }

    @POST
    @Path("/modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleResponse> modifyRuleCsById(ModifyRuleRequest request) throws UnExpectedRequestException {
        try {
            return ruleNodeService.modifyRuleWithContextService(request);
        } catch (UnExpectedRequestException e) {
            throw new UnExpectedRequestException(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Failed to modify rule detail, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_RULE_DETAIL}", null);
        }
    }
}
