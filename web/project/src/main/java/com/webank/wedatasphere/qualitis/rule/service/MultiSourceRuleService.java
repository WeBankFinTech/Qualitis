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
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.request.multi.AddMultiSourceRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.DeleteMultiSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.ModifyMultiSourceRequest;
import com.webank.wedatasphere.qualitis.rule.response.MultiRuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.request.multi.AddMultiSourceRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.DeleteMultiSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.ModifyMultiSourceRequest;

/**
 * @author howeye
 */
public interface MultiSourceRuleService {

    /**
     * Add multi-table rule
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<RuleResponse> addMultiSourceRule(AddMultiSourceRuleRequest request) throws UnExpectedRequestException;

    /**
     * Delete multi-table rule
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> deleteMultiSourceRule(DeleteMultiSourceRequest request) throws UnExpectedRequestException;

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
     */
    GeneralResponse<RuleResponse> modifyMultiSourceRule(ModifyMultiSourceRequest request) throws UnExpectedRequestException;

    /**
     * Get multi-table rule detail
     * @param ruleId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<MultiRuleDetailResponse> getMultiSourceRule(Long ruleId) throws UnExpectedRequestException;

}
