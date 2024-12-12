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
import com.webank.wedatasphere.qualitis.rule.exception.RuleLockException;
import com.webank.wedatasphere.qualitis.rule.request.AbstractCommonRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;

import java.io.IOException;

/**
 * @author allenzhou
 */
public interface FileRuleService {
    /**
     * Add file rule
     * @param request
     * @param loginUser
     * @param groupRules
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    GeneralResponse<RuleResponse> addRule(AddFileRuleRequest request, String loginUser, boolean groupRules) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException;

    /**
     * Add file rule for bdp-client.
     * @param request
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    GeneralResponse<RuleResponse> addRuleForOuter(AbstractCommonRequest request, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException;

    /**
     * Delete rule
     * @param request
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse deleteRule(DeleteFileRuleRequest request, String loginUser) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Delete rule real
     * @param rule
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse deleteRuleReal(Rule rule, String loginUser) throws UnExpectedRequestException;

    /**
     * Modify rule detail
     * @param request
     * @param loginUser
     * @param groupRules
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    GeneralResponse<RuleResponse> modifyRuleDetail(ModifyFileRuleRequest request, String loginUser, boolean groupRules) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException;

    /**
     * Modify rule detail
     * @param request
     * @param loginUser
     * @param groupRules
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     * @throws RuleLockException
     */
    GeneralResponse<RuleResponse> modifyRuleDetailWithLock(ModifyFileRuleRequest request, String loginUser, boolean groupRules) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException, RuleLockException;

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
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    GeneralResponse<RuleResponse> addRuleForUpload(AddFileRuleRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException;

    /**
     * Modify rule in one transaction for upload.
     * @param modifyRuleRequest
     * @param userName
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    GeneralResponse<RuleResponse> modifyRuleDetailForOuter(ModifyFileRuleRequest modifyRuleRequest, String userName)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException;
}
