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
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectUser;
import com.webank.wedatasphere.qualitis.project.request.AuthorizeProjectUserRequest;
import com.webank.wedatasphere.qualitis.project.response.ProjectUserResponse;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import java.util.List;
import javax.management.relation.RoleNotFoundException;

/**
 * @author allenzhou
 */
public interface ProjectUserService {

    /**
     * Authorize user permission of project.
     * @param addProjectUserRequest
     * @param userId
     * @param modify
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<ProjectUserResponse> authorizePermission(AuthorizeProjectUserRequest addProjectUserRequest, Long userId, boolean modify)
        throws UnExpectedRequestException, PermissionDeniedRequestException, RoleNotFoundException;

    /**
     * Delete user permissions.
     * @param request
     * @param userId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse deletePermission(AuthorizeProjectUserRequest request, Long userId)
        throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Get all project user.
     * @param projectId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<List<ProjectUserResponse>> getAllProjectUser(Long projectId) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Get user permission of project.
     * @param projectUsers
     * @param userName
     * @return
     */
    List<Integer> getPermissionList(List<ProjectUser> projectUsers, String userName);

    /**
     * Check user permission of project.
     * @param project
     * @param userName
     * @param permission
     * @return
     */
    boolean checkPermission(Project project, String userName, Integer permission);

    /**
     * Find all qualitis user.
     * @param request
     * @param userId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<List<String>> getAllUsers(PageRequest request, Long userId) throws UnExpectedRequestException;
}
