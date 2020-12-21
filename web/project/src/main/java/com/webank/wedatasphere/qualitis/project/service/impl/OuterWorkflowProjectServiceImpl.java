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

package com.webank.wedatasphere.qualitis.project.service.impl;

import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.ProjectTypeEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.request.AddProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.DeleteProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.ModifyProjectDetailRequest;
import com.webank.wedatasphere.qualitis.project.response.ProjectDetailResponse;
import com.webank.wedatasphere.qualitis.project.service.OuterWorkflowService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.request.AddProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.ModifyProjectDetailRequest;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author howeye
 */
@Service
public class OuterWorkflowProjectServiceImpl implements OuterWorkflowService {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private UserDao userDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(OuterWorkflowProjectServiceImpl.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse<ProjectDetailResponse> addWorkflowProject(AddProjectRequest request, String username) throws UnExpectedRequestException {
        // Check Arguments
        AddProjectRequest.checkRequest(request);
        if (request.getUsername() == null) {
            throw new UnExpectedRequestException("Username: {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }

        // Check existence of user
        User userInDb = userDao.findByUsername(username);
        if (userInDb == null) {
            throw new UnExpectedRequestException(String.format("{&FAILED_TO_FIND_USER} %s", username));
        }

        Project newProject = projectService.addProjectReal(userInDb.getId(), request.getProjectName(), request.getDescription());
        newProject.setProjectType(ProjectTypeEnum.WORKFLOW_PROJECT.getCode());
        Set<String> labels = request.getProjectLabels();
        Project savedProject = projectDao.saveProject(newProject);
        projectService.addProjectLabels(labels, savedProject);
        ProjectDetailResponse response = new ProjectDetailResponse(savedProject, null);
        LOGGER.info("Succeed to add workflow project, response: {}", response);
        return new GeneralResponse<>("200", "{&ADD_PROJECT_SUCCESSFULLY}", response);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse<?> modifyWorkflowProjectDetail(ModifyProjectDetailRequest request) throws UnExpectedRequestException {
        if (request.getUsername() == null) {
            throw new UnExpectedRequestException("Username: {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        return projectService.modifyProjectDetail(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GeneralResponse<?> deleteWorkflowProject(DeleteProjectRequest request) throws UnExpectedRequestException {
        if (request.getUsername() == null) {
            throw new UnExpectedRequestException("Username: {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
        return projectService.deleteProject(request);
    }
}
