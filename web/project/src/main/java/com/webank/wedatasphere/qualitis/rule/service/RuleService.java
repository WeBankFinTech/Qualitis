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
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.Template;
import com.webank.wedatasphere.qualitis.rule.exception.RuleLockException;
import com.webank.wedatasphere.qualitis.rule.request.AbstractCommonRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.EnableRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleEnableResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * @author howeye
 */
public interface RuleService {

    /**
     * Add rule
     * @param request
     * @param loginUser
     * @param groupRules
     * @return
     * @throws UnExpectedRequestException
     * @throws ClusterInfoNotConfigException
     * @throws TaskNotExistException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    GeneralResponse<RuleResponse> addRule(AddRuleRequest request, String loginUser, boolean groupRules)
            throws UnExpectedRequestException, ClusterInfoNotConfigException, TaskNotExistException, PermissionDeniedRequestException, IOException;

    /**
     * Add rule for bdp-client
     * @param request
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    GeneralResponse<RuleResponse> addRuleForOuter(AbstractCommonRequest request, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException;

    /**
     * Add uuid with table name
     * @param datasources
     */
    void addUuid(List<DataSourceRequest> datasources);

    /**
     * Delete rule
     * @param request
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<Object> deleteRule(DeleteRuleRequest request, String loginUser) throws UnExpectedRequestException, PermissionDeniedRequestException;

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
    GeneralResponse<RuleResponse> modifyRuleDetail(ModifyRuleRequest request, String loginUser, boolean groupRules) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException;

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
    GeneralResponse<RuleResponse> modifyRuleDetailWithLock(ModifyRuleRequest request, String loginUser, boolean groupRules) throws UnExpectedRequestException, PermissionDeniedRequestException, IOException, RuleLockException;

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
     * @param workFlowName
     * @param workFlowVersion
     * @param project
     * @param ruleId
     * @throws UnExpectedRequestException
     */
    void checkRuleName(String ruleName, String workFlowName, String workFlowVersion, Project project, Long ruleId) throws UnExpectedRequestException;

    /**
     * Check rule name Number by ruleName and project
     * @param ruleName
     * @param project
     * @throws UnExpectedRequestException
     */
    void checkRuleNameNumber(String ruleName, Project project) throws UnExpectedRequestException;

    /**
     * check rule existence using template
     * @param templateInDb
     * @throws UnExpectedRequestException
     */
    void checkRuleOfTemplate(Template templateInDb)  throws UnExpectedRequestException;

    /**
     * Add rule in one transaction for upload.
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws ClusterInfoNotConfigException
     * @throws TaskNotExistException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    GeneralResponse<RuleResponse> addRuleForUpload(AddRuleRequest request)
            throws UnExpectedRequestException, ClusterInfoNotConfigException, TaskNotExistException, PermissionDeniedRequestException, IOException;

    /**
     * Modify rule in one transaction for upload.
     * @param modifyRuleRequest
     * @param userName
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws IOException
     */
    GeneralResponse<RuleResponse> modifyRuleDetailForOuter(ModifyRuleRequest modifyRuleRequest, String userName)
            throws UnExpectedRequestException, PermissionDeniedRequestException, IOException;

    /**
     * get rule by standardVersionId
     * @param standardVersionId
     * @return
     */
    List<Rule> getDeployStandardVersionId(Long standardVersionId);

    /**
     * enable Rule
     * @param request
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<RuleEnableResponse> enableRule(EnableRuleRequest request, String loginUser) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * set ExecutionParameters Info
     * @param rule
     * @param ruleEnable
     * @param id
     */
    void setExecutionParametersInfo(Rule rule, Boolean ruleEnable, Long id);
}
