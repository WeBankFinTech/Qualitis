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

import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleRequest;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleRequest;

/**
 * @author howeye
 */
public interface RuleService {


    /**
     * Add rule
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<RuleResponse> addRule(AddRuleRequest request) throws UnExpectedRequestException;

    /**
     * Delete rule
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> deleteRule(DeleteRuleRequest request) throws UnExpectedRequestException;

    /**
     * Delete rule real
     * @param rule
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> deleteRuleReal(Rule rule);

    /**
     * Modify rule detail
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<RuleResponse> modifyRuleDetail(ModifyRuleRequest request) throws UnExpectedRequestException;

    /**
     * 根据ruleId获取rule详情
     * @param ruleId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<RuleDetailResponse> getRuleDetail(Long ruleId) throws UnExpectedRequestException;

    /**
     * Check rule name unique exclude ruleId
     * @param ruleName
     * @param project
     * @param ruleId
     * @throws UnExpectedRequestException
     */
    void checkRuleName(String ruleName, Project project, Long ruleId) throws UnExpectedRequestException;

}
