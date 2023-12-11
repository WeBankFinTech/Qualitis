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

package com.webank.wedatasphere.qualitis.rule.service;

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.request.AddCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleTemplateRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleTemplateRequest;
import com.webank.wedatasphere.qualitis.rule.request.TemplatePageRequest;
import com.webank.wedatasphere.qualitis.rule.response.NamingConventionsResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleTemplatePlaceholderResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleTemplateResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateInputDemandResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateMetaResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public interface RuleTemplateService {

    /**
     * Paging get custom template
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<RuleTemplateResponse>> getCustomRuleTemplateByUser(PageRequest request) throws UnExpectedRequestException;

    /**
     * Paging get default template
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<RuleTemplateResponse>> getDefaultRuleTemplate(TemplatePageRequest request) throws UnExpectedRequestException;

    /**
     * Get template meta data information by template id
     *
     * @param ruleTemplateId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<TemplateMetaResponse> getRuleTemplateMeta(Long ruleTemplateId) throws UnExpectedRequestException;

    /**
     * Find input meta data by rule template id
     *
     * @param ruleTemplateId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<TemplateInputDemandResponse> getRuleTemplateInputMeta(Long ruleTemplateId) throws UnExpectedRequestException;

    /**
     * Check existence of rule template
     *
     * @param ruleTemplateId
     * @return
     * @throws UnExpectedRequestException
     */
    Template checkRuleTemplate(Long ruleTemplateId) throws UnExpectedRequestException;

    /**
     * Add custom template
     *
     * @param request
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     * @throws PermissionDeniedRequestException
     */
    Template addCustomTemplate(AddCustomRuleRequest request, String loginUser) throws UnExpectedRequestException, IOException, PermissionDeniedRequestException;

    /**
     * Modify custom template
     *
     * @param addCustomRuleRequest
     * @param template
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     * @throws IOException
     * @throws PermissionDeniedRequestException
     */
    Template modifyCustomTemplate(AddCustomRuleRequest addCustomRuleRequest, Template template, String loginUser) throws UnExpectedRequestException, IOException, PermissionDeniedRequestException;

    /**
     * Get function alias
     *
     * @param code
     * @return
     */
    String getFunctionAlias(Integer code);

    /**
     * 删除自定义规则模版
     *
     * @param template
     * @throws UnExpectedRequestException
     */
    void deleteCustomTemplate(Template template) throws UnExpectedRequestException;

    /**
     * Get meta data of multi-table rule template
     *
     * @param templateId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<TemplateInputDemandResponse> getMultiSourceTemplateMeta(Long templateId) throws UnExpectedRequestException;

    /**
     * Paging get multi-table rule template
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<RuleTemplateResponse>> getMultiRuleTemplate(TemplatePageRequest request) throws UnExpectedRequestException;

    /**
     * Get meta data from multi-table rule template
     *
     * @param ruleTemplateId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<TemplateMetaResponse> getRuleMultiTemplateMeta(Long ruleTemplateId)
            throws UnExpectedRequestException;

    /**
     * Add default template.
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    RuleTemplateResponse addRuleTemplate(AddRuleTemplateRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Modify rule template.
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws PermissionDeniedRequestException
     */
    RuleTemplateResponse modifyRuleTemplate(ModifyRuleTemplateRequest request)
            throws UnExpectedRequestException, InvocationTargetException, IllegalAccessException, PermissionDeniedRequestException;

    /**
     * Delete default template.
     *
     * @param templateId
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    void deleteRuleTemplate(Long templateId) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Delete file template.
     *
     * @param templateId
     * @throws UnExpectedRequestException
     */
    void deleteFileRuleTemplate(Long templateId) throws UnExpectedRequestException;

    /**
     * Get rule template detail.
     *
     * @param templateId
     * @return
     * @throws UnExpectedRequestException
     */
    RuleTemplateResponse getModifyRuleTemplateDetail(Long templateId) throws UnExpectedRequestException;

    /**
     * Add file template.
     *
     * @param request
     * @return
     */
    Template addFileTemplate(AddFileRuleRequest request);

    /**
     * getTemplateOptionList
     *
     * @param templateType
     * @return
     * @throws UnExpectedRequestException
     */
    List<Map<String, Object>> getTemplateOptionList(Integer templateType) throws UnExpectedRequestException;

    /**
     * get Option List
     *
     * @return
     * @throws UnExpectedRequestException
     */
    List<Map<String, Object>> getAllTemplateInRule();

    /**
     * get Template Check Level List
     *
     * @return
     */
    List<Map<String, Object>> getTemplateCheckLevelList();

    /**
     * get Template Check Type List
     *
     * @return
     */
    List<Map<String, Object>> getTemplateCheckTypeList();


    /**
     * get Template File Type List
     *
     * @return
     */
    List<Map<String, Object>> getTemplateFileTypeList();


    /**
     * get Statistical Function List
     *
     * @return
     */
    List<Map<String, Object>> getStatisticalFunctionList();

    /**
     * Get placeholder data
     *
     * @param templateType
     * @return
     * @throws UnExpectedRequestException
     */
    RuleTemplatePlaceholderResponse getPlaceholderData(Integer templateType) throws UnExpectedRequestException;

    /**
     * Checking if the template is editable
     *
     * @param template
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    void checkAccessiblePermission(Template template) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * get Naming Method List
     *
     * @return
     */
    List<Map<String, Object>> getNamingMethodList();

    /**
     * get Conventions Naming Query
     *
     * @return
     */
    List<NamingConventionsResponse> getConventionsNamingQuery();
}
