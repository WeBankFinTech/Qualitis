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

import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.JobKillException;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.GroupListExecutionRequest;
import com.webank.wedatasphere.qualitis.request.KillApplicationsRequest;
import com.webank.wedatasphere.qualitis.request.ProjectExecutionRequest;
import com.webank.wedatasphere.qualitis.request.RuleListExecutionRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;

import java.util.concurrent.ExecutionException;

/**
 * @author howeye
 */
public interface ExecutionService {

    /**
     * Execute project job
     *
     * @param request
     * @param code
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse projectExecution(ProjectExecutionRequest request, Integer code, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Execute rule job
     *
     * @param request
     * @param code
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse ruleListExecution(RuleListExecutionRequest request, Integer code, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Kill job
     *
     * @param applicationId
     * @return
     * @throws JobKillException
     * @throws ClusterInfoNotConfigException
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse killApplication(String applicationId)
            throws JobKillException, ClusterInfoNotConfigException, UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Execute rule group job
     *
     * @param request
     * @param code
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse ruleGroupListExecution(GroupListExecutionRequest request, Integer code, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Batch kill.
     *
     * @param request
     * @throws JobKillException
     * @throws ClusterInfoNotConfigException
     * @throws UnExpectedRequestException
     */
    void killApplications(KillApplicationsRequest request);

    /**
     * Get max execution num with login user
     *
     * @return
     */
    Integer getMaxExecutionNum();

    /**
     * common Handle Rule Or Project Method
     *
     * @param projectExecutionRequest
     * @param ruleListExecutionRequest
     * @param code
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    GeneralResponse commonHandleRuleOrProjectMethod(ProjectExecutionRequest projectExecutionRequest, RuleListExecutionRequest ruleListExecutionRequest, Integer code, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException, ExecutionException, InterruptedException;

    /**
     * handle Rule Group List Method
     *
     * @param groupListExecutionRequest
     * @param code
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    GeneralResponse handleRuleGroupListMethod(GroupListExecutionRequest groupListExecutionRequest, Integer code, String loginUser)
            throws UnExpectedRequestException, PermissionDeniedRequestException, ExecutionException, InterruptedException;

}
