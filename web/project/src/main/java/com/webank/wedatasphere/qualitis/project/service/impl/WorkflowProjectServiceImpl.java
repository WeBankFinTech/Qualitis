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

import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.ProjectTypeEnum;
import com.webank.wedatasphere.qualitis.project.response.ProjectDetailResponse;
import com.webank.wedatasphere.qualitis.project.response.ProjectResponse;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.project.service.WorkflowProjectService;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author howeye
 */
@Service
public class WorkflowProjectServiceImpl implements WorkflowProjectService {

    @Autowired
    private ProjectService projectService;

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowProjectServiceImpl.class);

    @Override
    public GeneralResponse<GetAllResponse<ProjectResponse>> getWorkflowProjectByUser(PageRequest request) throws UnExpectedRequestException {
        GetAllResponse<ProjectResponse> response = projectService.getAllProjectByUserReal(request, ProjectTypeEnum.WORKFLOW_PROJECT.getCode());

        LOGGER.info("Succeed to get all workflow project, response: {}", response);
        return new GeneralResponse<>("200", "{&GET_ALL_PROJECT_SUCCESSFULLY}", response);
    }

    @Override
    public GeneralResponse<ProjectDetailResponse> getWorkflowProjectDetail(Long projectId, PageRequest pageRequest)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        return projectService.getProjectDetail(projectId, pageRequest);
    }
}
