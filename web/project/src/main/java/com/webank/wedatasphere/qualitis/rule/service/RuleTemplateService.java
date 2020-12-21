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

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.request.AddCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleTemplateRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleTemplateRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleTemplateResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateInputDemandResponse;
import com.webank.wedatasphere.qualitis.rule.response.TemplateMetaResponse;
import java.lang.reflect.InvocationTargetException;

/**
 * @author howeye
 */
public interface RuleTemplateService {

    /**
     * Paging get custom template
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<RuleTemplateResponse>> getCustomRuleTemplateByUser(PageRequest request) throws UnExpectedRequestException;

    /**
     * Paging get default template
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<RuleTemplateResponse>> getDefaultRuleTemplate(PageRequest request) throws UnExpectedRequestException;

    /**
     * Get template meta data information by template id
     * @param ruleTemplateId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<TemplateMetaResponse> getRuleTemplateMeta(Long ruleTemplateId) throws UnExpectedRequestException;

    /**
     * Find input meta data by rule template id
     * @param ruleTemplateId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<TemplateInputDemandResponse> getRuleTemplateInputMeta(Long ruleTemplateId) throws UnExpectedRequestException;

    /**
     * Check existence of rule template
     * @param ruleTemplateId
     * @return
     * @throws UnExpectedRequestException
     */
    Template checkRuleTemplate(Long ruleTemplateId) throws UnExpectedRequestException;

    /**
     * Add custom template
     * @param request
     * @return
     */
    Template addCustomTemplate(AddCustomRuleRequest request);

    /**
     * Get function alias
     * @param code
     * @return
     */
    String getFunctionAlias(Integer code);

    /**
     * 删除自定义规则模版
     * @param template
     * @throws UnExpectedRequestException
     */
    void deleteCustomTemplate(Template template) throws UnExpectedRequestException;

    /**
     * Get meta data of multi-table rule template
     * @param templateId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> getMultiSourceTemplateMeta(Long templateId) throws UnExpectedRequestException;

    /**
     * Paging get multi-table rule template
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<RuleTemplateResponse>> getMultiRuleTemplate(PageRequest request)
        throws UnExpectedRequestException;

    /**
     * Get meta data from multi-table rule template
     * @param ruleTemplateId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<TemplateMetaResponse> getRuleMultiTemplateMeta(Long ruleTemplateId)
        throws UnExpectedRequestException;

    /**
     * Add default template.
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    RuleTemplateResponse addRuleTemplate(AddRuleTemplateRequest request) throws UnExpectedRequestException;

    /**
     * Modify default template.
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    RuleTemplateResponse modifyRuleTemplate(ModifyRuleTemplateRequest request)
        throws UnExpectedRequestException, InvocationTargetException, IllegalAccessException;

    /**
     * Delete default template.
     * @param templateId
     * @throws UnExpectedRequestException
     */
    void deleteRuleTemplate(Long templateId) throws UnExpectedRequestException;

    /**
     * get rule template detail
     * @param templateId
     * @throws UnExpectedRequestException
     * @return
     */
    RuleTemplateResponse getModifyRuleTemplateDetail(Long templateId) throws UnExpectedRequestException;
}
