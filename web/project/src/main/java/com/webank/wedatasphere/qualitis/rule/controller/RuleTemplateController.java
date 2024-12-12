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

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleTemplateRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteRuleTemplateRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleTemplateRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplatePageRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplatePullDownRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleTemplateResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateInputDemandResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateMetaResponse;
import com.webank.wedatasphere.qualitis.rule.service.RuleTemplateService;
import com.webank.wedatasphere.qualitis.util.RequestParametersUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

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
            RequestParametersUtils.transcoding(request);
            return ruleTemplateService.getMultiRuleTemplate(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to find multi rule_template, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_FIND_MULTI_RULE_TEMPLATE}", null);
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
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_FIND_CUSTOM_RULE_TEMPLATE}", null);
        }
    }

    @POST
    @Path("default/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<RuleTemplateResponse>> getDefaultRuleTemplate(TemplatePageRequest request) throws UnExpectedRequestException {
        request.checkRequest();
        try {
            RequestParametersUtils.transcoding(request);
            return ruleTemplateService.getDefaultRuleTemplate(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to find default rule_template, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_FIND_DEFAULT_RULE_TEMPLATE}", null);
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
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_RULE_TEMPLATE}", null);
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
            LOGGER.error("Failed to find the input of rule_template. rule_template_id: {}, caused by: {}", ruleTemplateId, e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_FIND_THE_INPUT_OF_RULE_TEMPLATE}", null);
        }
    }

    @POST
    @Path("default/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleTemplateResponse> addDefaultRuleTemplate(AddRuleTemplateRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            RequestParametersUtils.transcoding(request);
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&ADD_RULE_TEMPLATE_SUCCESSFULLY}", ruleTemplateService.addRuleTemplate(request));
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to find multi rule_template, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_ADD_RULE_TEMPLATE}", null);
        }
    }

    @POST
    @Path("default/modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleTemplateResponse> modifyDefaultRuleTemplate(ModifyRuleTemplateRequest request)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            RequestParametersUtils.transcoding(request);
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&MODIFY_RULE_TEMPLATE_SUCCESSFULLY}", ruleTemplateService.modifyRuleTemplate(request));
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to modify rule_template, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_MODIFY_RULE_TEMPLATE}", null);
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
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&DELETE_RULE_TEMPLATE_SUCCESSFULLY}", null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to delete rule templates, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_DELETE_RULE_TEMPLATE}", null);
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
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&DELETE_RULE_TEMPLATE_SUCCESSFULLY}", null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to find multi rule_template, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_DELETE_RULE_TEMPLATE}", null);
        }
    }

    @GET
    @Path("modify/detail/{template_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<RuleTemplateResponse> getModifyRuleTemplateDetail(@PathParam("template_id") Long templateId) throws UnExpectedRequestException {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_RULE_TEMPLATE_SUCCESSFULLY}", ruleTemplateService.getModifyRuleTemplateDetail(templateId));
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to modify rule_template, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_RULE_TEMPLATE}", null);
        }
    }

    @POST
    @Path("option/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<Map<String, Object>>> getAllOptionListByProject(TemplatePullDownRequest request) {
        try {
            List<Map<String, Object>> ruleTemplateResponses = ruleTemplateService.getAllTemplateInRule(request);
            return new GeneralResponse(ResponseStatusConstants.OK, "{&GET_RULE_TEMPLATE_SUCCESSFULLY}"
                    , ruleTemplateResponses);
        } catch (Exception e) {
            LOGGER.error("Failed to query option list of template by project, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_RULE_TEMPLATE}", null);
        }
    }

    @GET
    @Path("user/option/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<Map<String, Object>>> getOptionListByUser(@QueryParam("template_type") Integer templateType) {
        try {
            List<Map<String, Object>> ruleTemplateResponses = ruleTemplateService.getTemplateOptionList(templateType);
            return new GeneralResponse(ResponseStatusConstants.OK, "{&GET_RULE_TEMPLATE_SUCCESSFULLY}"
                    , ruleTemplateResponses);
        } catch (Exception e) {
            LOGGER.error("Failed to query option list of template, caused by: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_RULE_TEMPLATE}", null);
        }
    }

    @POST
    @Path("check/level/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getCheckLevelEnumn() {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_CHECK_LEVEL_ENUMN_SUCCESSFULLY}", ruleTemplateService.getTemplateCheckLevelList());
        } catch (Exception e) {
            LOGGER.error("Failed to get check level enumn, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_CHECK_LEVEL_ENUMN}", null);
        }
    }

    @POST
    @Path("check/type/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getCheckTypeEnumn() {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_CHECK_TYPE_ENUMN_SUCCESSFULLY}", ruleTemplateService.getTemplateCheckTypeList());
        } catch (Exception e) {
            LOGGER.error("Failed to get check type enumn, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_CHECK_TYPE_ENUMN}", null);
        }
    }

    @POST
    @Path("file/type/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getFileTypeEnumn() {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_FILE_TYPE_ENUMN_SUCCESSFULLY}", ruleTemplateService.getTemplateFileTypeList());
        } catch (Exception e) {
            LOGGER.error("Failed to get file type enumn, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_FILE_TYPE_ENUMN}", null);
        }
    }

    @POST
    @Path("statistical/function/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getStatisticalFunctionEnumn() {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_STATISTICAL_FUNCTION_ENUMN_SUCCESSFULLY}", ruleTemplateService.getStatisticalFunctionList());
        } catch (Exception e) {
            LOGGER.error("Failed to get statistical function list enumn, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_STATISTICAL_FUNCTION_ENUMN}", null);
        }
    }

    @POST
    @Path("placeholder/list/{template_type}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getPlaceholderList(@PathParam("template_type") Integer templateType) {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_PLACEHOLDER_LIST_SUCCESSFULLY}", ruleTemplateService.getPlaceholderData(templateType));
        } catch (Exception e) {
            LOGGER.error("Failed to get placeholder list , caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_PLACEHOLDER_LIST}", null);
        }
    }

    @POST
    @Path("naming/method/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getNamingMethodEnumn() {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_NAMING_METHOD_ENUMN_SUCCESSFULLY}", ruleTemplateService.getNamingMethodList());
        } catch (Exception e) {
            LOGGER.error("Failed to get naming method enumn, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_NAMING_METHOD_ENUMN}", null);
        }
    }

    @POST
    @Path("naming/conventions/config")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getNamingConventionsConfig() {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_NAMING_CONVENTIONS_CONFIG_SUCCESSFULLY}", ruleTemplateService.getConventionsNamingQuery());
        } catch (Exception e) {
            LOGGER.error("Failed to get naming conventions config, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_NAMING_CONVENTIONS_CONFIG}", null);
        }
    }


}
