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
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleTemplateRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteRuleTemplateRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleTemplateRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplatePageRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleTemplateResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateInputDemandResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateMetaResponse;
import com.webank.wedatasphere.qualitis.rule.service.RuleTemplateService;
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
@Path("api/v1/projector/rule_template")
public class RuleTemplateController {

    @Autowired
    private RuleTemplateService ruleTemplateService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleTemplateController.class);

    @POST
    @Path("multi/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<RuleTemplateResponse>> getMultiRuleTemplate(TemplatePageRequest request) throws UnExpectedRequestException {
        try {
            return ruleTemplateService.getMultiRuleTemplate(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to find multi rule_template, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_FIND_MULTI_RULE_TEMPLATE}", null);
        }
    }

    @POST
    @Path("custom/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<RuleTemplateResponse>> getCustomRuleTemplateByUser(PageRequest request) throws UnExpectedRequestException {
        try {
            return ruleTemplateService.getCustomRuleTemplateByUser(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to find custom rule_template, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_FIND_CUSTOM_RULE_TEMPLATE}", null);
        }
    }

    @POST
    @Path("default/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<RuleTemplateResponse>> getDefaultRuleTemplate(TemplatePageRequest request) throws UnExpectedRequestException {
        try {
            return ruleTemplateService.getDefaultRuleTemplate(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to find default rule_template, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_FIND_DEFAULT_RULE_TEMPLATE}", null);
        }
    }

    @GET
    @Path("meta/{rule_template_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<TemplateMetaResponse> getRuleTemplateMeta(@PathParam("rule_template_id") Long ruleTemplateId) throws UnExpectedRequestException {
        try {
            return ruleTemplateService.getRuleTemplateMeta(ruleTemplateId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get rule_template. rule_template_id: {}, caused by: {}", ruleTemplateId, e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_RULE_TEMPLATE}", null);
        }
    }

    @GET
    @Path("meta_input/{rule_template_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<TemplateInputDemandResponse> getRuleTemplateInputMeta(@PathParam("rule_template_id") Long ruleTemplateId) throws UnExpectedRequestException {
        try {
            return ruleTemplateService.getRuleTemplateInputMeta(ruleTemplateId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to find the input of rule_template. rule_template_id: {}, caused by: {}", ruleTemplateId, e.getMessage() ,e);
            return new GeneralResponse<>("500", "{&FAILED_TO_FIND_THE_INPUT_OF_RULE_TEMPLATE}", null);
        }
    }

    @GET
    @Path("meta_input/multi_source/{rule_template_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> getMultiSourceTemplateMeta(@PathParam("rule_template_id") Long ruleTemplateId) throws UnExpectedRequestException {
        try {
            return ruleTemplateService.getMultiSourceTemplateMeta(ruleTemplateId);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to find the meta of multi_rule_template. rule_template_id: {}, caused by: {}", ruleTemplateId, e.getMessage() ,e);
            return new GeneralResponse<>("500", "{&FAILED_TO_FIND_THE_META_OF_MULTI_RULE_TEMPLATE}", null);
        }
    }

    /**
     * The following is the advanced interface of the operation rule template, which is now open to all users
     */
    @POST
    @Path("multi/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleTemplateResponse> addMultiRuleTemplate(AddRuleTemplateRequest request) throws UnExpectedRequestException {
        try {
            return new GeneralResponse<>("200", "{&ADD_RULE_TEMPLATE_SUCCESSFULLY}", ruleTemplateService.addRuleTemplate(request));
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to find multi rule_template, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_ADD_RULE_TEMPLATE}", null);
        }
    }

    @POST
    @Path("multi/modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleTemplateResponse> modifyMultiRuleTemplate(ModifyRuleTemplateRequest request)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return new GeneralResponse<>("200", "{&MODIFY_RULE_TEMPLATE_SUCCESSFULLY}", ruleTemplateService.modifyRuleTemplate(request));
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to modify rule_template, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_RULE_TEMPLATE}", null);
        }
    }

    @POST
    @Path("multi/delete/{template_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<RuleTemplateResponse>> deleteMultiRuleTemplate(@PathParam("template_id") Long templateId)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            ruleTemplateService.deleteRuleTemplate(templateId);
            return new GeneralResponse<>("200", "{&DELETE_RULE_TEMPLATE_SUCCESSFULLY}", null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to find delete rule templates, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_RULE_TEMPLATE}", null);
        }
    }

    @POST
    @Path("default/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleTemplateResponse> addDefaultRuleTemplate(AddRuleTemplateRequest request) throws UnExpectedRequestException {
        try {
            return new GeneralResponse<>("200", "{&ADD_RULE_TEMPLATE_SUCCESSFULLY}", ruleTemplateService.addRuleTemplate(request));
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to find multi rule_template, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_ADD_RULE_TEMPLATE}", null);
        }
    }

    @POST
    @Path("default/modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleTemplateResponse> modifyDefaultRuleTemplate(ModifyRuleTemplateRequest request)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            return new GeneralResponse<>("200", "{&MODIFY_RULE_TEMPLATE_SUCCESSFULLY}", ruleTemplateService.modifyRuleTemplate(request));
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to modify rule_template, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_MODIFY_RULE_TEMPLATE}", null);
        }
    }

    @POST
    @Path("default/delete/{template_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<RuleTemplateResponse>> deleteDefaultRuleTemplate(@PathParam("template_id") Long templateId)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            ruleTemplateService.deleteRuleTemplate(templateId);
            return new GeneralResponse<>("200", "{&DELETE_RULE_TEMPLATE_SUCCESSFULLY}", null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to delete rule templates, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_RULE_TEMPLATE}", null);
        }
    }

    @POST
    @Path("delete/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<RuleTemplateResponse>> deleteAllRuleTemplate(DeleteRuleTemplateRequest request)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            DeleteRuleTemplateRequest.checkRequest(request);
            for (Long templateId : request.getRuleTemplateIdList()) {
                ruleTemplateService.deleteRuleTemplate(templateId);
            }
            return new GeneralResponse<>("200", "{&DELETE_RULE_TEMPLATE_SUCCESSFULLY}", null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to find multi rule_template, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_DELETE_RULE_TEMPLATE}", null);
        }
    }

    @GET
    @Path("modify/detail/{template_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleTemplateResponse> getModifyRuleTemplateDetail(@PathParam("template_id") Long templateId) throws UnExpectedRequestException {
        try {
            return new GeneralResponse<>("200", "{&GET_RULE_TEMPLATE_SUCCESSFULLY}", ruleTemplateService.getModifyRuleTemplateDetail(templateId));
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to modify rule_template, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>("500", "{&FAILED_TO_GET_RULE_TEMPLATE}", null);
        }
    }
}
