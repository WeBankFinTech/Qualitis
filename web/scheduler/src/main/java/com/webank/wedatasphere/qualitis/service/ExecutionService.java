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
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.request.GroupListExecutionRequest;
import com.webank.wedatasphere.qualitis.request.KillApplicationsRequest;
import com.webank.wedatasphere.qualitis.request.ProjectExecutionRequest;
import com.webank.wedatasphere.qualitis.request.RuleListExecutionRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;

/**
 * @author howeye
 */
public interface ExecutionService {

    /**
     * Execute project job
     * @param request
     * @param code
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> projectExecution(ProjectExecutionRequest request, Integer code)
        throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Execute rule job
     * @param request
     * @param code
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<?> ruleListExecution(RuleListExecutionRequest request, Integer code)
        throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Kill job
     * @param applicationId
     * @param executionUser
     * @return
     * @throws JobKillException
     * @throws ClusterInfoNotConfigException
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> killApplication(String applicationId, String executionUser)
        throws JobKillException, ClusterInfoNotConfigException, UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Execute rule group job
     * @param request
     * @param code
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> ruleGroupListExecution(GroupListExecutionRequest request, Integer code)
        throws UnExpectedRequestException;

    /**
     * Batch kill.
     * @param request
     * @throws JobKillException
     * @throws ClusterInfoNotConfigException
     * @throws UnExpectedRequestException
     */
    void killApplications(KillApplicationsRequest request);
}
