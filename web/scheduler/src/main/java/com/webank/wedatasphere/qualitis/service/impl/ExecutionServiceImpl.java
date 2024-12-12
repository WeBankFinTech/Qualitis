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

import com.webank.wedatasphere.qualitis.dao.ApplicationDao;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.JobKillException;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.parser.LocaleParser;
import com.webank.wedatasphere.qualitis.request.GroupExecutionRequest;
import com.webank.wedatasphere.qualitis.request.GroupListExecutionRequest;
import com.webank.wedatasphere.qualitis.request.KillApplicationsRequest;
import com.webank.wedatasphere.qualitis.request.ProjectExecutionRequest;
import com.webank.wedatasphere.qualitis.request.RuleListExecutionRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.ExecutionService;
import com.webank.wedatasphere.qualitis.service.OuterExecutionService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author howeye
 */
@Service
public class ExecutionServiceImpl implements ExecutionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionServiceImpl.class);

    @Autowired
    private OuterExecutionService outerExecutionService;

    @Autowired
    private OuterAsyncExecutionService asyncOuterExecutionUtil;

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private LocaleParser localeParser;

    @Context
    private HttpServletRequest httpRequest;

    public ExecutionServiceImpl(@Context HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> projectExecution(ProjectExecutionRequest request, Integer code)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        if (request.getExecutionUser() == null) {
            throw new UnExpectedRequestException("Execution user {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (request.getDyNamicPartitionPrefix() == null) {
            request.setDyNamicPartitionPrefix("");
        }
        try {
            return outerExecutionService.projectExecution(request, code);
        } catch (UnExpectedRequestException e) {
            throw e;
        } catch (PermissionDeniedRequestException e) {
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> ruleListExecution(RuleListExecutionRequest request, Integer invokeCode)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        if (request.getExecutionUser() == null) {
            throw new UnExpectedRequestException("Execution user {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        if (request.getDyNamicPartitionPrefix() == null) {
            request.setDyNamicPartitionPrefix("");
        }
        try {
            return outerExecutionService.ruleListExecution(request, invokeCode);
        } catch (UnExpectedRequestException e) {
            throw e;
        } catch (PermissionDeniedRequestException e) {
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> ruleGroupListExecution(GroupListExecutionRequest request, Integer invokeCode)
        throws UnExpectedRequestException {
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
                if (StringUtils.isNotBlank(request.getExecutionUser())) {
                    groupExecutionRequest.setExecutionUser(request.getExecutionUser());
                }
                if (groupExecutionRequest.getDyNamicPartitionPrefix() == null) {
                    groupExecutionRequest.setDyNamicPartitionPrefix("");
                }
                outerExecutionService.groupExecution(groupExecutionRequest, invokeCode);
            } catch (Exception e) {
                LOGGER.error("One group execution[id={}] of the rule group list execution start failed!", groupExecutionRequest.getGroupId());
                LOGGER.error("Exception: ", e);
            }
        }

        return new GeneralResponse<>("200", "{&SUCCEED_TO_DISPATCH_TASK}", null);
    }

    @Override
    public GeneralResponse<?> killApplication(String applicationId, String executionUser)
        throws JobKillException, ClusterInfoNotConfigException, UnExpectedRequestException, PermissionDeniedRequestException {
        LOGGER.info("Qualitis execution user: {}", executionUser);
        return outerExecutionService.killApplication(applicationId, executionUser);
    }

    @Override
    public void killApplications(KillApplicationsRequest request) {
        String userName = HttpUtils.getUserName(httpRequest);
        LOGGER.info("Qualitis execution user: {}", userName);
        request.setLoginUser(userName);
        try {
            asyncOuterExecutionUtil.killApplications(request);
        } catch (Exception e) {
            LOGGER.error("Batch kill error.", e);
        }
    }
}
