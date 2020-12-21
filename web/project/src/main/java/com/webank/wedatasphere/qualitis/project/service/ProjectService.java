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

import com.webank.wedatasphere.qualitis.project.request.AddProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.DeleteProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.ModifyProjectDetailRequest;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.response.ProjectDetailResponse;
import com.webank.wedatasphere.qualitis.project.response.ProjectResponse;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.project.request.AddProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.DeleteProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.ModifyProjectDetailRequest;
import com.webank.wedatasphere.qualitis.project.response.ProjectDetailResponse;
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
     * Paging get all normal project by user
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<GetAllResponse<ProjectResponse>> getAllProjectByUser(PageRequest request) throws UnExpectedRequestException;

    /**
     * Get project detail
     * @param projectId
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<ProjectDetailResponse> getProjectDetail(Long projectId) throws UnExpectedRequestException;

    /**
     * Modify project
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> modifyProjectDetail(ModifyProjectDetailRequest request) throws UnExpectedRequestException;

    /**
     * Delete project by id
     * @param request
     * @return
     * @throws UnExpectedRequestException
     */
    GeneralResponse<?> deleteProject(DeleteProjectRequest request) throws UnExpectedRequestException;

    /**
     * Check existence of project
     * @param projectId
     * @return
     * @throws UnExpectedRequestException
     */
    Project checkProjectExistence(Long projectId) throws UnExpectedRequestException;


    /**
     * Construct and add project
     * @param userId
     * @param projectName
     * @param projectDescription
     * @return
     * @throws UnExpectedRequestException
     */
    Project addProjectReal(Long userId, String projectName, String projectDescription) throws UnExpectedRequestException;

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

}
