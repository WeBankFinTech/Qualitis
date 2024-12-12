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
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.request.AbstractAddRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.response.CustomRuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;

/**
 * @author howeye
 */
public interface CustomRuleService {


    /**
     * Add custom rule
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<RuleResponse> addCustomRule(AddCustomRuleRequest request)
        throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Add custom rule for bdp-client
     * @param request
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<RuleResponse> addRuleForOuter(AbstractAddRequest request, String loginUser)
        throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * delete custom rule
     * @param request
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<?> deleteCustomRule(DeleteCustomRuleRequest request, String loginUser) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * delete custom rule real
     * @param rule
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> deleteCustomRuleReal(Rule rule) throws UnExpectedRequestException;

    /**
     * Get custom rule detail
     * @param ruleId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<CustomRuleDetailResponse> getCustomRuleDetail(Long ruleId) throws UnExpectedRequestException;

    /**
     * Modifty custom rule
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws ClusterInfoNotConfigException
     * @throws TaskNotExistException
     * @throws MetaDataAcquireFailedException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<RuleResponse> modifyCustomRule(ModifyCustomRuleRequest request)
        throws UnExpectedRequestException, ClusterInfoNotConfigException, TaskNotExistException, MetaDataAcquireFailedException, PermissionDeniedRequestException;

    /**
     * Add rule in one transaction for upload.
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<RuleResponse> addCustomRuleForUpload(AddCustomRuleRequest request)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, PermissionDeniedRequestException;

    /**
     * Modify rule in one transaction for upload.
     * @param modifyRuleRequest
     * @param userName
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<RuleResponse> modifyRuleDetailForOuter(ModifyCustomRuleRequest modifyRuleRequest, String userName)
        throws UnExpectedRequestException, MetaDataAcquireFailedException, PermissionDeniedRequestException;
}
