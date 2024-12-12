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
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.exception.RuleLockException;
import com.webank.wedatasphere.qualitis.rule.request.AddBatchRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddGroupRulesRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleGroupRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyGroupRulesRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleGroupRequest;
import com.webank.wedatasphere.qualitis.rule.request.QueryGroupRulesRequest;
import com.webank.wedatasphere.qualitis.rule.request.RuleGroupPageRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleGroupResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author allenzhou
 */
public interface RuleGroupService {

    /**
     * Add
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    RuleGroupResponse addRuleGroup(AddRuleGroupRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Modify
     * @param modifyRuleGroupRequest
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    RuleGroupResponse modifyRuleGroup(ModifyRuleGroupRequest modifyRuleGroupRequest)
        throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Modify
     * @param modifyRuleGroupRequest
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws RuleLockException
     */
    RuleGroupResponse modifyRuleGroupWithLock(ModifyRuleGroupRequest modifyRuleGroupRequest)
            throws UnExpectedRequestException, PermissionDeniedRequestException, RuleLockException;

    /**
     * Add rules
     * @param request
     * @param ruleGroupInDb
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    RuleGroupResponse addRules(AddGroupRulesRequest request, RuleGroup ruleGroupInDb)
        throws ExecutionException, InterruptedException;

    /**
     * Modify rules
     * @param request
     * @param ruleGroupInDb
     * @param ruleTotal
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws UnExpectedRequestException
     */
    RuleGroupResponse modifyRules(ModifyGroupRulesRequest request, RuleGroup ruleGroupInDb, int ruleTotal)
            throws ExecutionException, InterruptedException, UnExpectedRequestException;

    /**
     * Modify rules
     * @param request
     * @param ruleGroupInDb
     * @param ruleTotal
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws UnExpectedRequestException
     * @throws RuleLockException
     */
    RuleGroupResponse modifyRulesWithLock(ModifyGroupRulesRequest request, RuleGroup ruleGroupInDb, int ruleTotal)
            throws ExecutionException, InterruptedException, UnExpectedRequestException, RuleLockException;

    /**
     * Query rules
     * @param request
     * @param ruleGroupInDb
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GetAllResponse<RuleDetailResponse> queryRules(QueryGroupRulesRequest request, RuleGroup ruleGroupInDb)
        throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Query option list of template
     * @param request
     * @return
     */
    List<Map<String, Object>> getOptionList(RuleGroupPageRequest request);

    /**
     * Creating batch rule by template
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     */
    GeneralResponse addBatchRule(AddBatchRuleRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException, InterruptedException, ExecutionException, IOException;

    /**
     * Generate
     * @param ruleGroupId
     * @param ruleGroupName
     * @param projectInDb
     * @param groupType
     * @return
     * @throws UnExpectedRequestException
     */
    RuleGroup generate(Long ruleGroupId, String ruleGroupName, Project projectInDb, int groupType) throws UnExpectedRequestException;
}
