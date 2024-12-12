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

import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.TaskNotExistException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.request.AbstractAddRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.AddMultiSourceRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.DeleteMultiSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.ModifyMultiSourceRequest;
import com.webank.wedatasphere.qualitis.rule.response.MultiRuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author howeye
 */
public interface MultiSourceRuleService {

    /**
     * Add multi-table rule
     * @param request
     * @param check
     * @return
     * @throws UnExpectedRequestException
     * @throws TaskNotExistException
     * @throws ClusterInfoNotConfigException
     */
    GeneralResponse<RuleResponse> addMultiSourceRule(AddMultiSourceRuleRequest request, boolean check)
        throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Add multi-table rule for bdp-client
     * @param request
     * @param check
     * @return
     * @throws UnExpectedRequestException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    GeneralResponse<RuleResponse> addRuleForOuter(AbstractAddRequest request, boolean check)
        throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Delete multi-table rule
     * @param request
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> deleteMultiSourceRule(DeleteMultiSourceRequest request, String loginUser) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Delete multi-table rule real
     * @param rule
     * @return
     */
    GeneralResponse<?> deleteMultiRuleReal(Rule rule);

    /**
     * Modify multi-table rule
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws TaskNotExistException
     * @throws ClusterInfoNotConfigException
     */
    GeneralResponse<RuleResponse> modifyMultiSourceRule(ModifyMultiSourceRequest request)
        throws UnExpectedRequestException, TaskNotExistException, ClusterInfoNotConfigException, PermissionDeniedRequestException;

    /**
     * Get multi-table rule detail
     * @param ruleId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<MultiRuleDetailResponse> getMultiSourceRule(Long ruleId) throws UnExpectedRequestException;

    /**
     * Add rule in one transaction for upload.
     * @param request
     * @param check
     * @return
     * @throws UnExpectedRequestException
     * @throws TaskNotExistException
     * @throws ClusterInfoNotConfigException
     */
    GeneralResponse<RuleResponse> addMultiSourceRuleForUpload(AddMultiSourceRuleRequest request,
        boolean check) throws UnExpectedRequestException, TaskNotExistException, ClusterInfoNotConfigException, PermissionDeniedRequestException;
    /**
     * Modify rule in one transaction for upload.
     * @param modifyRuleRequest
     * @param userName
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<RuleResponse> modifyRuleDetailForOuter(ModifyMultiSourceRequest modifyRuleRequest, String userName)
        throws UnExpectedRequestException, PermissionDeniedRequestException;
}
