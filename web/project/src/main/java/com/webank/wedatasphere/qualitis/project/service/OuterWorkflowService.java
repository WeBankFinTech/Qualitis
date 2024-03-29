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
import com.webank.wedatasphere.qualitis.project.request.AddProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.DeleteProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.GetProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.ModifyProjectDetailRequest;
import com.webank.wedatasphere.qualitis.project.response.ProjectDetailResponse;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import javax.management.relation.RoleNotFoundException;

/**
 * @author howeye
 */
public interface OuterWorkflowService {

    /**
     * Add workflow project
     * @param request
     * @param username
     * @return
     * @throws UnExpectedRequestException
     * @throws RoleNotFoundException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<ProjectDetailResponse> addWorkflowProject(AddProjectRequest request, String username)
        throws UnExpectedRequestException, RoleNotFoundException, PermissionDeniedRequestException;

    /**
     * Modify workflow project
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws RoleNotFoundException
     */
    GeneralResponse<ProjectDetailResponse> modifyWorkflowProjectDetail(ModifyProjectDetailRequest request)
        throws UnExpectedRequestException, PermissionDeniedRequestException, RoleNotFoundException;

    /**
     * Delete workflow project
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse deleteWorkflowProject(DeleteProjectRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Get workflow project
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<ProjectDetailResponse> getWorkflowProject(GetProjectRequest request) throws UnExpectedRequestException;
}
