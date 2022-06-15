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
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.request.AbstractAddRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;

/**
 * @author allenzhou
 */
public interface FileRuleService {
    /**
     * Add file rule
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<RuleResponse> addRule(AddFileRuleRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Add file rule for bdp-client.
     * @param request
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<RuleResponse> addRuleForOuter(AbstractAddRequest request, String loginUser)
        throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Delete rule
     * @param request
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> deleteRule(DeleteFileRuleRequest request, String loginUser) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Delete rule real
     * @param rule
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> deleteRuleReal(Rule rule) throws UnExpectedRequestException;

    /**
     * Modify rule detail
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<RuleResponse> modifyRuleDetail(ModifyFileRuleRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * 根据ruleId获取rule详情
     * @param ruleId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<RuleDetailResponse> getRuleDetail(Long ruleId) throws UnExpectedRequestException;

    /**
     * For upload in one transaction.
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<RuleResponse> addRuleForUpload(AddFileRuleRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Modify rule in one transaction for upload.
     * @param modifyRuleRequest
     * @param userName
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<RuleResponse> modifyRuleDetailForOuter(ModifyFileRuleRequest modifyRuleRequest, String userName)
        throws UnExpectedRequestException, PermissionDeniedRequestException;
}
