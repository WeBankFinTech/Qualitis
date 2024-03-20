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

package com.webank.wedatasphere.qualitis.service;

import com.webank.wedatasphere.qualitis.dto.SubmitRuleBaseInfo;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.JobKillException;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.request.BatchDeleteRuleRequest;
import com.webank.wedatasphere.qualitis.request.DataSourceExecutionRequest;
import com.webank.wedatasphere.qualitis.request.GeneralExecutionRequest;
import com.webank.wedatasphere.qualitis.request.GetTaskLogRequest;
import com.webank.wedatasphere.qualitis.request.GroupExecutionRequest;
import com.webank.wedatasphere.qualitis.request.ProjectExecutionRequest;
import com.webank.wedatasphere.qualitis.request.RuleListExecutionRequest;
import com.webank.wedatasphere.qualitis.response.ApplicationProjectResponse;
import com.webank.wedatasphere.qualitis.response.*;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author howeye
 */
public interface OuterExecutionService {

    /**
     * 通用执行接口
     *
     * @param request
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse generalExecution(GeneralExecutionRequest request, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Datasource execution.
     *
     * @param request
     * @param landUser
     * @return
     * @throws UnExpectedRequestException
     * @throws MetaDataAcquireFailedException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse dataSourceExecution(DataSourceExecutionRequest request, String landUser)
            throws UnExpectedRequestException, MetaDataAcquireFailedException, PermissionDeniedRequestException;

    /**
     * 获取任务状态信息
     *
     * @param applicationId 任务id
     * @return 任务信息
     * @throws UnExpectedRequestException 参数异常
     */
    GeneralResponse<ApplicationTaskResponse> getApplicationStatus(String applicationId) throws UnExpectedRequestException;

    /**
     * 获取顶层任务状态信息
     *
     * @param applicationId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<ApplicationTaskResponse> getApplicationDynamicStatus(String applicationId) throws UnExpectedRequestException;

    /**
     * 获取任务日志
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<String> getTaskLog(GetTaskLogRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Submit rules, and update ruleDataSource
     *
     * @param jobId
     * @param ruleIds
     * @param partition
     * @param createUser
     * @param executionUser
     * @param nodeName
     * @param projectId
     * @param ruleGroupId
     * @param fpsFileId
     * @param fpsHashValue
     * @param startupParam
     * @param clusterName
     * @param setFlag
     * @param execParams
     * @param execParamStr
     * @param runDate
     * @param splitBy
     * @param invokeCode
     * @param pendingApplication
     * @param subSystemId
     * @param partitionFullSize
     * @param engineReuse
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<ApplicationTaskSimpleResponse> submitRulesAndUpdateRule(String jobId, List<Long> ruleIds, StringBuilder partition, String createUser, String executionUser
            , String nodeName, Long projectId, Long ruleGroupId, String fpsFileId, String fpsHashValue, String startupParam, String clusterName
            , String setFlag, Map<String, String> execParams, String execParamStr, StringBuilder runDate, StringBuilder splitBy, Integer invokeCode
            , Application pendingApplication, Long subSystemId, String partitionFullSize, Boolean engineReuse);

    /**
     * Just submit rules
     * (If you also want to update ruleDataSource like submitRulesAndUpdateRule(), please call updateDataSourceByExecutionParameters() or other methods individually)
     *
     * @param rules
     * @param submitRuleBaseInfo
     * @return
     */
    GeneralResponse<ApplicationTaskSimpleResponse> submitRules(List<Rule> rules, SubmitRuleBaseInfo submitRuleBaseInfo);

    /**
     * 生成applicationInfo
     *
     * @param createUser
     * @param executionUser
     * @param date
     * @param code
     * @param jobId
     * @return
     * @throws UnExpectedRequestException
     */
    Application generateApplicationInfo(String createUser, String executionUser, Date date, Integer code, String jobId)
            throws UnExpectedRequestException;

    /**
     * Group execution.
     *
     * @param request
     * @param invokeCode
     * @param landUser
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse groupExecution(GroupExecutionRequest request, Integer invokeCode, String landUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * 项目调度
     *
     * @param request
     * @param code
     * @param landUser
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse projectExecution(ProjectExecutionRequest request, Integer code, String landUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Handle check alert
     *
     * @param loginUser
     * @param ruleGroupInDb
     * @param projectInDb
     * @param executionParam
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    GeneralResponse<ApplicationTaskSimpleResponse> handleCheckAlert(String loginUser, RuleGroup ruleGroupInDb, Project projectInDb, String executionParam) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * 规则列表调度
     *
     * @param request
     * @param invokeCode
     * @param landUser
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse ruleListExecution(RuleListExecutionRequest request, Integer invokeCode, String landUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * 获取application的结果状态
     *
     * @param applicationId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<ApplicationResultResponse> getApplicationSummary(String applicationId) throws UnExpectedRequestException;

    /**
     * Get application result value
     *
     * @param applicationId
     * @param executionUser
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<List<ApplicationResultValueResponse>> getApplicationResultValue(String applicationId, String executionUser) throws UnExpectedRequestException;

    /**
     * 获取application的总日志
     *
     * @param applicationId
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<String> getApplicationLog(String applicationId) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * 杀死执行中的application
     *
     * @param applicationId
     * @param executionUser
     * @return
     * @throws JobKillException
     * @throws UnExpectedRequestException
     * @throws ClusterInfoNotConfigException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<Integer> killApplication(String applicationId, String executionUser)
            throws JobKillException, UnExpectedRequestException, ClusterInfoNotConfigException, PermissionDeniedRequestException;

    /**
     * Common execution for submit rules.
     *
     * @param rules
     * @param partition
     * @param executionUser
     * @param nodeName
     * @param fpsFileId
     * @param fpsHashValue
     * @param startupParam
     * @param clusterName
     * @param setFlag
     * @param execParams
     * @param newApplication
     * @param date
     * @param runDate
     * @param splitBy
     * @param partitionFullSize
     * @param engineReuse
     * @param createUser
     * @return
     * @throws Exception
     */
    ApplicationTaskSimpleResponse commonExecution(List<Rule> rules, StringBuilder partition, String executionUser, String nodeName, String fpsFileId
            , String fpsHashValue, String startupParam, String clusterName, String setFlag, Map<String, String> execParams, Application newApplication,
                                                  Date date
            , StringBuilder runDate, StringBuilder splitBy, String partitionFullSize, Boolean engineReuse, String createUser) throws Exception;

    /**
     * Save gateway job info
     *
     * @param jobId
     * @param size
     */
    void saveGatewayJobInfo(String jobId, int size);

    /**
     * Query job info
     *
     * @param jobId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GatewayJobInfoResponse> queryJobInfo(String jobId) throws UnExpectedRequestException;

    /**
     * Query application reponses
     *
     * @param jobId
     * @return
     * @throws UnExpectedRequestException
     * @throws ParseException
     */
    GeneralResponse<ApplicationProjectResponse> queryApplications(String jobId) throws UnExpectedRequestException, ParseException;

    /**
     * 第三方批量删除规则 delete Rule
     *
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    void deleteRule(BatchDeleteRuleRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Resubmit check alert
     * @param executeUser
     * @param ruleGroupId
     * @param projectId
     * @param executionParam
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    void reSubmitCheckAlertGroup(String executeUser, Long ruleGroupId, Long projectId, String executionParam) throws UnExpectedRequestException, PermissionDeniedRequestException;
}
