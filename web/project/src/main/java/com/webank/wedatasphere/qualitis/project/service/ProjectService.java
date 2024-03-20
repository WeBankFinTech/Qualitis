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

import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.request.*;
import com.webank.wedatasphere.qualitis.project.response.ProjectDetailResponse;
import com.webank.wedatasphere.qualitis.project.response.ProjectEventResponse;
import com.webank.wedatasphere.qualitis.project.response.ProjectResponse;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Set;

/**
 * @author howeye
 */
public interface ProjectService {

    /**
     * Add project
     * @param request
     * @param userId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<ProjectDetailResponse> addProject(AddProjectRequest request, Long userId) throws UnExpectedRequestException;

    /**
     * Auth admin and proxy
     * @param user
     * @param savedProject
     */
    void autoAuthAdminAndProxy(User user, Project savedProject);

    /**
     * Paging get all normal project by user
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<ProjectResponse>> getAllProjectByUser(PageRequest request) throws UnExpectedRequestException;

    /**
     * paging get normal project by some conditions
     * @param request
     * @param projectType
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<ProjectResponse>> getProjectsByCondition(QueryProjectRequest request, Integer projectType) throws UnExpectedRequestException;

    /**
     * query projectDetail and ruleList by condition
     * this is an extension to getProjectDetail()
     * @param projectId
     * @param request
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<ProjectDetailResponse> getRulesByCondition(Long projectId, QueryRuleRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Get project detail.
     * @param projectId
     * @param pageRequest
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<ProjectDetailResponse> getProjectDetail(Long projectId, PageRequest pageRequest)
        throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Modify project
     * @param request
     * @param workflow
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws RoleNotFoundException
     */
    GeneralResponse<ProjectDetailResponse> modifyProjectDetail(ModifyProjectDetailRequest request, boolean workflow) throws UnExpectedRequestException, PermissionDeniedRequestException, RoleNotFoundException;

    /**
     * Create project user.
     * @param project
     * @param user
     */
    void createProjectUser(Project project, User user);

    /**
     * Delete project by id
     * @param request
     * @param workflow
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse deleteProject(DeleteProjectRequest request, boolean workflow) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Check existence of project
     * @param projectId
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     */
    Project checkProjectExistence(Long projectId, String loginUser) throws UnExpectedRequestException;


    /**
     * Construct and add project
     * @param userId
     * @param projectName
     * @param cnName
     * @param projectDescription
     * @return
     * @throws UnExpectedRequestException
     */
    Project addProjectReal(Long userId, String projectName, String cnName, String projectDescription) throws UnExpectedRequestException;

    /**
     * No pagine, no project type
     * @return
     */
    GetAllResponse<ProjectResponse> getAllProjectByUserReal();

    /**
     * Check permissions of project
     * @param project
     * @param userName
     * @param permissions
     * @throws PermissionDeniedRequestException
     */
    void checkProjectPermission(Project project, String userName, List<Integer> permissions) throws PermissionDeniedRequestException;

    /**
     * Add project labels
     * @param labels
     * @param project
     */
    void addProjectLabels(Set<String> labels, Project project);

    /**
     * Paging get all project by user and type
     * @param request
     * @param projectType
     * @return
     * @throws UnExpectedRequestException
     */
    GetAllResponse<ProjectResponse> getAllProjectByUserReal(PageRequest request, Integer projectType) throws UnExpectedRequestException;

    /**
     * Get project events.
     * @param projectId
     * @param pageRequest
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<GetAllResponse<ProjectEventResponse>> getProjectEvents(Long projectId, PageRequest pageRequest)
        throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Authorize users from work flow.
     * @param savedProject
     * @param userInDb
     * @param authorizeProjectUserRequests
     * @param modify
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     * @throws RoleNotFoundException
     */
    void authorizeUsers(Project savedProject, User userInDb, List<AuthorizeProjectUserRequest> authorizeProjectUserRequests, boolean modify)
        throws UnExpectedRequestException, PermissionDeniedRequestException, RoleNotFoundException;

    /**
     * Delete all rules.
     * @param ruleList
     * @param userName
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    void deleteAllRules(Iterable<Rule> ruleList, String userName) throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * Get all rules, groups info for submit, execution param config
     * @param projectId
     * @return
     * @throws UnExpectedRequestException
     * @throws PermissionDeniedRequestException
     */
    GeneralResponse<ProjectDetailResponse> getAllRules(Long projectId)
        throws UnExpectedRequestException, PermissionDeniedRequestException;

    /**
     * check Projects Existence
     * @param projectIds
     * @param loginUser
     * @return
     * @throws UnExpectedRequestException
     */
    List<Project> checkProjectsExistence(List<Long> projectIds, String loginUser) throws UnExpectedRequestException;

}
