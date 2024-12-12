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

package com.webank.wedatasphere.qualitis.service.impl;

import com.webank.wedatasphere.qualitis.constant.InvokeTypeEnum;
import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.JobKillException;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import com.webank.wedatasphere.qualitis.request.GroupExecutionRequest;
import com.webank.wedatasphere.qualitis.request.GroupListExecutionRequest;
import com.webank.wedatasphere.qualitis.request.KillApplicationsRequest;
import com.webank.wedatasphere.qualitis.request.ProjectExecutionRequest;
import com.webank.wedatasphere.qualitis.request.RuleListExecutionRequest;
import com.webank.wedatasphere.qualitis.response.ApplicationProjectResponse;
import com.webank.wedatasphere.qualitis.response.ApplicationTaskSimpleResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.ExecutionService;
import com.webank.wedatasphere.qualitis.service.OuterExecutionService;
import com.webank.wedatasphere.qualitis.timer.RuleGroupSumbitCallable;
import com.webank.wedatasphere.qualitis.timer.RuleKindSumbitCallable;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.concurrent.*;

/**
 * @author howeye
 */
@Service
public class ExecutionServiceImpl implements ExecutionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionServiceImpl.class);

    @Autowired
    private OuterExecutionService outerExecutionService;

    @Autowired
    private ExecutionService executionService;

    @Autowired
    private OuterAsyncExecutionService outerAsyncExecutionService;

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private LocaleParser localeParser;

    @Context
    private HttpServletRequest httpRequest;

    @Value("${execution.schedule.maxNum:400}")
    private Integer maxScheduleNum;

    @Value("${execution.controller.async: false}")
    private Boolean enableAsyncRequest;

    public ExecutionServiceImpl(@Context HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Autowired
    @Qualifier("ruleExecutionThreadPool")
    private ThreadPoolExecutor ruleExecutionThreadPool;

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse projectExecution(ProjectExecutionRequest request, Integer code, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        if (request.getExecutionUser() == null) {
            throw new UnExpectedRequestException("Execution user {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (request.getDyNamicPartitionPrefix() == null) {
            request.setDyNamicPartitionPrefix("");
        }
        try {
            return outerExecutionService.projectExecution(request, code, loginUser);
        } catch (UnExpectedRequestException | PermissionDeniedRequestException e) {
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse ruleListExecution(RuleListExecutionRequest request, Integer invokeCode, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        if (request.getExecutionUser() == null) {
            throw new UnExpectedRequestException("Execution user {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (request.getDyNamicPartitionPrefix() == null) {
            request.setDyNamicPartitionPrefix("");
        }
        try {
            return outerExecutionService.ruleListExecution(request, invokeCode, loginUser);
        } catch (UnExpectedRequestException e) {
            throw e;
        } catch (PermissionDeniedRequestException e) {
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse ruleGroupListExecution(GroupListExecutionRequest request, Integer invokeCode, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        if (request == null || CollectionUtils.isEmpty(request.getGroupExecutionRequests())) {
            throw new UnExpectedRequestException("{&NO_RULE_CAN_BE_EXECUTED}");
        }
        for (GroupExecutionRequest groupExecutionRequest : request.getGroupExecutionRequests()) {
            try {
                if (StringUtils.isNotBlank(request.getClusterName())) {
                    groupExecutionRequest.setClusterName(request.getClusterName());
                }
                if (StringUtils.isNotBlank(request.getExecutionParam())) {
                    groupExecutionRequest.setExecutionParam(request.getExecutionParam());
                }

                if (StringUtils.isNotBlank(request.getStartupParamName().replace("engine_reuse=false", "").replace("engine_reuse=true", ""))) {
                    groupExecutionRequest.setStartupParamName(request.getStartupParamName());
                }
                if (StringUtils.isNotBlank(request.getSetFlag())) {
                    groupExecutionRequest.setSetFlag(request.getSetFlag());
                }
                if (StringUtils.isNotBlank(request.getFpsFileId())) {
                    groupExecutionRequest.setFpsFileId(request.getFpsFileId());
                }
                if (StringUtils.isNotBlank(request.getFpsHashValue())) {
                    groupExecutionRequest.setFpsHashValue(request.getFpsHashValue());
                }
                if (StringUtils.isNotBlank(request.getExecutionUser())) {
                    groupExecutionRequest.setExecutionUser(request.getExecutionUser());
                }
                if (groupExecutionRequest.getDyNamicPartitionPrefix() == null) {
                    groupExecutionRequest.setDyNamicPartitionPrefix("");
                }
                outerExecutionService.groupExecution(groupExecutionRequest, invokeCode, loginUser);
            } catch (UnExpectedRequestException e) {
                throw e;
            } catch (PermissionDeniedRequestException e) {
                throw e;
            } catch (Exception e) {
                LOGGER.error("One group execution[id={}] of the rule group list execution start failed!", groupExecutionRequest.getGroupId());
                LOGGER.error("Exception: ", e);
            }
        }

        return new GeneralResponse<>("200", "{&SUCCEED_TO_DISPATCH_TASK}", null);
    }

    @Override
    public GeneralResponse killApplication(String applicationId)
            throws JobKillException, ClusterInfoNotConfigException, UnExpectedRequestException, PermissionDeniedRequestException {
        String userName = HttpUtils.getUserName(httpRequest);
        LOGGER.info("Qualitis user {} to kill.", userName);
        return outerExecutionService.killApplication(applicationId, userName);
    }

    @Override
    public void killApplications(KillApplicationsRequest request) {
        String userName = HttpUtils.getUserName(httpRequest);
        LOGGER.info("Qualitis user {} to kill.", userName);
        request.setLoginUser(userName);
        try {
            outerAsyncExecutionService.killApplications(request);
        } catch (Exception e) {
            LOGGER.error("Batch kill error.", e);
        }
    }

    @Override
    public Integer getMaxExecutionNum() {
        return maxScheduleNum;
    }

    @Override
    public GeneralResponse commonHandleRuleOrProjectMethod(ProjectExecutionRequest projectExecutionRequest, RuleListExecutionRequest ruleListExecutionRequest, Integer code, String loginUser) throws ExecutionException, InterruptedException {
        Future<ApplicationProjectResponse> applicationProjectResponse = null;

        if (projectExecutionRequest != null) {
            applicationProjectResponse = ruleExecutionThreadPool.submit(new RuleKindSumbitCallable(projectExecutionRequest, null, InvokeTypeEnum.UI_INVOKE.getCode(), executionService, loginUser));
        } else if (ruleListExecutionRequest != null) {
            applicationProjectResponse = ruleExecutionThreadPool.submit(new RuleKindSumbitCallable(null, ruleListExecutionRequest, InvokeTypeEnum.UI_INVOKE.getCode(), executionService, loginUser));
        }
        if (enableAsyncRequest) {
            LOGGER.info("The task has been submitted asynchronously");
            return new GeneralResponse<>("200", "{&SUCCEED_TO_DISPATCH_TASK}", null);
        }
        if (applicationProjectResponse != null && StringUtils.isBlank(applicationProjectResponse.get().getExceptionMessage())) {
            return new GeneralResponse<>("200", "{&SUCCEED_TO_DISPATCH_TASK}", applicationProjectResponse.get());
        }

        if (applicationProjectResponse != null) {
            return new GeneralResponse<>("500", "{&FAILED_TO_EXECUTE_PROJECT}, caused by: " + applicationProjectResponse.get().getExceptionMessage(), null);
        } else {
            return new GeneralResponse<>("500", "{&FAILED_TO_EXECUTE_PROJECT}, caused by: " + null, null);
        }

    }

    @Override
    public GeneralResponse handleRuleGroupListMethod(GroupListExecutionRequest groupListExecutionRequest, Integer code, String loginUser) throws ExecutionException, InterruptedException {
        Future<ApplicationTaskSimpleResponse> applicationTaskSimpleResponse = ruleExecutionThreadPool.submit(new RuleGroupSumbitCallable(groupListExecutionRequest, InvokeTypeEnum.UI_INVOKE.getCode(), executionService, loginUser));
        if (enableAsyncRequest) {
            LOGGER.info("The task has been submitted asynchronously");
            return new GeneralResponse<>("200", "{&SUCCEED_TO_DISPATCH_TASK}", null);
        }
        if (applicationTaskSimpleResponse != null && StringUtils.isBlank(applicationTaskSimpleResponse.get().getExceptionMessage())) {
            return new GeneralResponse<>("200", "{&SUCCEED_TO_DISPATCH_TASK}", null);
        }

        if (applicationTaskSimpleResponse != null) {
            return new GeneralResponse<>("500", "{&FAILED_TO_EXECUTE_PROJECT}, caused by: " + applicationTaskSimpleResponse.get().getExceptionMessage(), null);
        } else {
            return new GeneralResponse<>("500", "{&FAILED_TO_EXECUTE_PROJECT}, caused by: " + null, null);
        }

    }

}
