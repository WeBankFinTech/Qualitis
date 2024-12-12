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
import com.webank.wedatasphere.qualitis.rule.request.AbstractAddRequest;
import com.webank.wedatasphere.qualitis.rule.request.AddRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.DataSourceRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyRuleRequest;
import com.webank.wedatasphere.qualitis.rule.response.RuleDetailResponse;
import com.webank.wedatasphere.qualitis.rule.response.RuleResponse;
import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author howeye
 */
public interface RuleService {

    /**
     * Add rule
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws ClusterInfoNotConfigException
     * @throws TaskNotExistException
     */
    GeneralResponse<RuleResponse> addRule(AddRuleRequest request)
        throws UnExpectedRequestException, ClusterInfoNotConfigException, TaskNotExistException, PermissionDeniedRequestException;

    /**
     * Add rule for bdp-client
     * @param request
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    GeneralResponse<RuleResponse> addRuleForOuter(AbstractAddRequest request, String loginUser)
        throws UnExpectedRequestException, PermissionDeniedRequestException;

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
     */
    GeneralResponse<?> deleteRule(DeleteRuleRequest request, String loginUser) throws UnExpectedRequestException, PermissionDeniedRequestException;

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
     * @throws ClusterInfoNotConfigException
     * @throws TaskNotExistException
     */
    GeneralResponse<RuleResponse> modifyRuleDetail(ModifyRuleRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

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
     */
    GeneralResponse<RuleResponse> addRuleForUpload(AddRuleRequest request)
        throws UnExpectedRequestException, ClusterInfoNotConfigException, TaskNotExistException, PermissionDeniedRequestException;

    /**
     * Modify rule in one transaction for upload.
     * @param modifyRuleRequest
     * @param userName
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<RuleResponse> modifyRuleDetailForOuter(ModifyRuleRequest modifyRuleRequest, String userName)
        throws UnExpectedRequestException, PermissionDeniedRequestException;
}
