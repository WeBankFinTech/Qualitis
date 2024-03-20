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

package com.webank.wedatasphere.qualitis.project.service;

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.request.QueryProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.QueryRuleRequest;
import com.webank.wedatasphere.qualitis.project.response.ProjectDetailResponse;
import com.webank.wedatasphere.qualitis.project.response.ProjectResponse;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;

/**
 * @author howeye
 */
public interface WorkflowProjectService {

    /**
     * Get workflow project by user
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<ProjectResponse>> getWorkflowProjectByUser(PageRequest request) throws UnExpectedRequestException;

    /**
     * get Work flow Project By User
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<ProjectResponse>> getWorkflowProjectByUser(QueryProjectRequest request) throws UnExpectedRequestException;

    /**
     * Get workflow project detail
     * @param projectId
     * @param pageRequest
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<ProjectDetailResponse> getWorkflowProjectDetail(Long projectId, PageRequest pageRequest)
        throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Get workflow project detail by condition
     * @param projectId
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<ProjectDetailResponse> getWorkflowRuleByCondition(Long projectId, QueryRuleRequest request)
            throws UnExpectedRequestException, PermissionDeniedRequestException;

}
