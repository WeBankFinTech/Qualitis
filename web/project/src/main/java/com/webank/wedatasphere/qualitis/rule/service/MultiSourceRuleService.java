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
import com.webank.wedatasphere.qualitis.rule.exception.RuleLockException;
import com.webank.wedatasphere.qualitis.rule.request.AbstractCommonRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.AddMultiSourceRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.DeleteMultiSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.ModifyMultiSourceRequest;
import com.webank.wedatasphere.qualitis.rule.response.MultiRuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public interface MultiSourceRuleService {

    /**
     * Add multi-table rule.
     * @param request
     * @param check
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    GeneralResponse<RuleResponse> addMultiSourceRule(AddMultiSourceRuleRequest request, boolean check)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException;

    /**
     * Add multi-table rule for bdp-client.
     * @param request
     * @param check
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    GeneralResponse<RuleResponse> addRuleForOuter(AbstractCommonRequest request, boolean check)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException;

    /**
     * Delete multi-table rule.
     * @param request
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<Object> deleteMultiSourceRule(DeleteMultiSourceRequest request, String loginUser) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Delete multi-table rule real.
     * @param rule
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<Object> deleteMultiRuleReal(Rule rule) throws UnExpectedRequestException;

    /**
     * Modify multi-table rule.
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws TaskNotExistException
     * @throws ClusterInfoNotConfigException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    GeneralResponse<RuleResponse> modifyMultiSourceRule(ModifyMultiSourceRequest request)
            throws UnExpectedRequestException, TaskNotExistException, ClusterInfoNotConfigException, PermissionDeniedRequestException, IOException;


    /**
     * Modify multi-table rule.
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws TaskNotExistException
     * @throws ClusterInfoNotConfigException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     * @throws RuleLockException
     */
    GeneralResponse<RuleResponse> modifyMultiSourceRuleWithLock(ModifyMultiSourceRequest request)
            throws UnExpectedRequestException, TaskNotExistException, ClusterInfoNotConfigException, PermissionDeniedRequestException, IOException, RuleLockException;

    /**
     * Get multi-table rule detail.
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
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    GeneralResponse<RuleResponse> addMultiSourceRuleForUpload(AddMultiSourceRuleRequest request,
        boolean check) throws UnExpectedRequestException, TaskNotExistException, ClusterInfoNotConfigException, PermissionDeniedRequestException, IOException;
    /**
     * Modify rule in one transaction for upload.
     * @param modifyRuleRequest
     * @param userName
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    GeneralResponse<RuleResponse> modifyRuleDetailForOuter(ModifyMultiSourceRequest modifyRuleRequest, String userName)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException;

    /**
     * get constrast enum
     * @return
     */
    List<Map<String, Object>> getAllConstrastEnum();
}
