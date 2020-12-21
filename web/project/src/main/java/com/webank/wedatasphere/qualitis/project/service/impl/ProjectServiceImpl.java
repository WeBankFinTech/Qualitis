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
import com.webank.wedatasphere.qualitis.project.constant.ProjectTypeEnum;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.dao.ProjectLabelDao;
import com.webank.wedatasphere.qualitis.project.dao.ProjectUserDao;
import com.webank.wedatasphere.qualitis.project.entity.ProjectLabel;
import com.webank.wedatasphere.qualitis.project.request.AddProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.DeleteProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.ModifyProjectDetailRequest;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectUser;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.project.response.ProjectDetailResponse;
import com.webank.wedatasphere.qualitis.project.response.ProjectResponse;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.service.CustomRuleService;
import com.webank.wedatasphere.qualitis.rule.service.MultiSourceRuleService;
import com.webank.wedatasphere.qualitis.rule.service.RuleService;
import com.webank.wedatasphere.qualitis.submitter.impl.ExecutionManagerImpl;
import com.webank.wedatasphere.qualitis.util.HttpUtils;

import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.dao.ProjectUserDao;
import com.webank.wedatasphere.qualitis.project.request.AddProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.DeleteProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.ModifyProjectDetailRequest;
import com.webank.wedatasphere.qualitis.project.response.ProjectDetailResponse;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.*;

/**
 * @author howeye
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private RuleDao ruleDao;

    @Autowired
    private ProjectUserDao projectUserDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private CustomRuleService customRuleService;

    @Autowired
    private MultiSourceRuleService multiSourceRuleService;

    @Autowired
    private ProjectLabelDao projectLabelDao;

    private HttpServletRequest httpServletRequest;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);

    public ProjectServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<ProjectDetailResponse> addProject(AddProjectRequest request, Long userId) throws UnExpectedRequestException {
        // Check Arguments
        AddProjectRequest.checkRequest(request);

        Project newProject = addProjectReal(userId, request.getProjectName(), request.getDescription());
        newProject.setProjectType(ProjectTypeEnum.NORMAL_PROJECT.getCode());

        Project savedProject = projectDao.saveProject(newProject);
        Set<String> labels = request.getProjectLabels();
        addProjectLabels(labels, savedProject);
        ProjectDetailResponse response = new ProjectDetailResponse(savedProject, null);
        LOGGER.info("Succeed to add project, response: {}", response);
        return new GeneralResponse<>("200", "{&ADD_PROJECT_SUCCESSFULLY}", response);
    }

    @Override
    public GeneralResponse<GetAllResponse<ProjectResponse>> getAllProjectByUser(PageRequest request) throws UnExpectedRequestException {
        GetAllResponse<ProjectResponse> response = getAllProjectByUserReal(request, ProjectTypeEnum.NORMAL_PROJECT.getCode());

        LOGGER.info("Succeed to get all project, response: {}", response);
        return new GeneralResponse<>("200", "{&GET_ALL_PROJECT_SUCCESSFULLY}", response);
    }

    @Override
    public GeneralResponse<ProjectDetailResponse> getProjectDetail(Long projectId) throws UnExpectedRequestException {
        // Check existence of project
        Project projectInDb = projectDao.findById(projectId);
        if (projectInDb == null) {
            throw new UnExpectedRequestException("Project id {&DOES_NOT_EXIST}");
        }

        String username =  HttpUtils.getUserName(httpServletRequest);
        // Check if user has permission modifying project
        checkProjectPermission(projectInDb, username);

        // Find rules by project
        List<Rule> rules = ruleDao.findByProject(projectInDb);

        ProjectDetailResponse projectDetailResponse = new ProjectDetailResponse(projectInDb, rules);
        LOGGER.info("Succeed to get get project detail. project_id: {}, response: {}", projectId, projectDetailResponse);
        return new GeneralResponse<>("200", "{&GET_PROJECT_DETAIL_SUCCESSFULLY}", projectDetailResponse);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> modifyProjectDetail(ModifyProjectDetailRequest request) throws UnExpectedRequestException {
        // Check Arguments
        ModifyProjectDetailRequest.checkRequest(request);

        // Check existence of project
        Project projectInDb = projectDao.findById(request.getProjectId());
        if (projectInDb == null) {
            throw new UnExpectedRequestException("project id {&DOES_NOT_EXIST}");
        }
        LOGGER.info("Succeed to get project. project: {}", projectInDb);

        // Get userId
        Long userId =  HttpUtils.getUserId(httpServletRequest);
        User user = null;
        if (userId == null) {
            user = userDao.findByUsername(request.getUsername());
            if (user == null) {
                throw new UnExpectedRequestException(String.format("{&FAILED_TO_FIND_USER} %s", request.getUsername()));
            }
        } else {
            user = userDao.findById(userId);
        }
        // Check if user has permission modifying project
        checkProjectPermission(projectInDb, user.getUsername());

        // Check project name
        Project otherProject = projectDao.findByNameAndCreateUser(request.getProjectName(), user.getUsername());
        if (otherProject != null && !otherProject.getId().equals(projectInDb.getId())) {
            throw new UnExpectedRequestException(String.format("Project name: %s already exist", request.getProjectName()));
        }

        // Save project
        projectInDb.setName(request.getProjectName());
        projectInDb.setDescription(request.getDescription());
        // Delete old projectUser
        projectUserDao.deleteByProject(projectInDb);
        LOGGER.info("Succeed to delete all project_user, project_id: {}", request.getProjectId());
        // Delete old projectLabel
        projectLabelDao.deleteByProject(projectInDb);
        LOGGER.info("Succeed to delete all project_label, project_id: {}", request.getProjectId());
        // Rebuild projectUser
        setProjectUser(projectInDb, user);
        // Rebuild project labels.
        addProjectLabels(request.getProjectLabelStrs(), projectInDb);
        // Record modify user
        projectInDb.setModifyUser(user.getUsername());
        projectInDb.setModifyTime(ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date()));
        Project savedProject = projectDao.saveProject(projectInDb);
        LOGGER.info("Succeed to modify project. project: {}", savedProject);
        return new GeneralResponse<>("200", "{&MODIFY_PROJECT_DETAIL_SUCCESSFULLY}", null);
    }

    private void setProjectUser(Project project, User user) {
        Map<String, ProjectUser> projectUserMap = new HashMap<>(4);

        ProjectUser creatorProject = new ProjectUser(ProjectUserPermissionEnum.CREATOR.getCode(), project, user.getUsername(), user.getChineseName());
        projectUserMap.put(user.getUsername(), creatorProject);
        
        projectUserDao.saveAll(projectUserMap.values());
        LOGGER.info("Succeed to save project_user, project_user: {}", projectUserMap.values());
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> deleteProject(DeleteProjectRequest request) throws UnExpectedRequestException {
        // Check Arguments
        DeleteProjectRequest.checkRequest(request);

        // Check existence of project
        Project projectInDb = projectDao.findById(request.getProjectId());
        if (projectInDb == null) {
            throw new UnExpectedRequestException("project_id {&DOES_NOT_EXIST}");
        }

        Long userId =  HttpUtils.getUserId(httpServletRequest);
        User user = null;
        if (userId == null) {
            user = userDao.findByUsername(request.getUsername());
            if (user == null) {
                throw new UnExpectedRequestException(String.format("{&FAILED_TO_FIND_USER} %s", request.getUsername()));
            }
        } else {
            user = userDao.findById(userId);
        }
        // Check project permission
        checkProjectPermission(projectInDb, user.getUsername());

        // Delete project
        projectDao.deleteProject(projectInDb);
        LOGGER.info("Succeed to delete project. project_id: {}", request.getProjectId());
        return new GeneralResponse<>("200", "{&DELETE_PROJECT_SUCCESSFULLY}", null);
    }

    @Override
    public Project checkProjectExistence(Long projectId) throws UnExpectedRequestException {
        Project projectInDb = projectDao.findById(projectId);
        if (projectInDb == null) {
            throw new UnExpectedRequestException("project_id :["+ projectId + "] {&DOES_NOT_EXIST}");
        }
        String loginUser = HttpUtils.getUserName(httpServletRequest);

        String createUser = projectInDb.getCreateUser();
        if (StringUtils.isBlank(loginUser) || !loginUser.equals(createUser)) {
            throw new UnExpectedRequestException("{&NO_PERMISSION_MODIFYING_PROJECT}");
        }

        return projectInDb;
    }

    @Override
    public Project addProjectReal(Long userId, String projectName, String projectDescription) throws UnExpectedRequestException {
        User user = userDao.findById(userId);
        // Check existence of project by project name
        Project projectInDb = projectDao.findByNameAndCreateUser(projectName, user.getUsername());
        if (projectInDb != null) {
            throw new UnExpectedRequestException(String.format("{&PROJECT}:%s {&ALREADY_EXIST}.", projectName));
        }

        // Save project
        Project newProject = new Project(projectName, projectDescription, user.getUsername(), user.getChineseName(), user.getDepartment(),
            ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date()));

        // Create project user
        setProjectUser(newProject, user);

        return newProject;
    }

    @Override
    public GetAllResponse<ProjectResponse> getAllProjectByUserReal(PageRequest request, Integer projectType) throws UnExpectedRequestException {
        // Check Arguments
        PageRequest.checkRequest(request);

        // Get username
        String username = HttpUtils.getUserName(httpServletRequest);

        // Paging get project
        int page = request.getPage();
        int size = request.getSize();
        List<ProjectUser> projectUsers = projectUserDao.findByUsernameAndPermissionAndProjectType(username,
                ProjectUserPermissionEnum.CREATOR.getCode(), projectType, page, size);
        long total = projectUserDao.countByUsernameAndPermissionAndProjectType(username,
                ProjectUserPermissionEnum.CREATOR.getCode(), projectType);
        List<ProjectResponse> projectResponses = new ArrayList<>();
        for (ProjectUser projectUser : projectUsers) {
            projectResponses.add(new ProjectResponse(projectUser.getProject()));
        }

        GetAllResponse<ProjectResponse> response = new GetAllResponse<>();
        response.setTotal(total);
        response.setData(projectResponses);

        return response;
    }

    private void checkProjectPermission(Project project, String username) throws UnExpectedRequestException {
        List<ProjectUser> projectUsers = projectUserDao.findByProject(project);
        for (ProjectUser projectUser : projectUsers) {
            if (projectUser.getUserName().equals(username)) {
                return;
            }
        }
        throw new UnExpectedRequestException("{&NO_PERMISSION_OPERATING_PROJECT}");
    }

    @Override
    public void addProjectLabels(Set<String> labels, Project project) {
        Map<String, ProjectLabel> map = new HashMap<>(2);
        if (project.getProjectLabels() != null) {
            for (ProjectLabel projectLabel : project.getProjectLabels()) {
                map.put(projectLabel.getLabelName(), projectLabel);
            }
        } else {
            project.setProjectLabels(new HashSet<>());
        }
        if (labels != null && ! labels.isEmpty()) {
            Set<ProjectLabel> projectLabels = new HashSet<>();
            for (String labelName : labels) {
                if (map.keySet().contains(labelName)) {
                    continue;
                }
                ProjectLabel projectLabel = new ProjectLabel();
                projectLabel.setProject(project);
                projectLabel.setLabelName(labelName);
                projectLabels.add(projectLabel);
            }
            LOGGER.info("Start to save project labels. Labels: {}", Arrays.toString(projectLabels.toArray()));
            projectLabelDao.saveAll(projectLabels);
            for (String temp : map.keySet()) {
                if (! labels.contains(temp)) {
                    map.remove(temp);
                }
            }
            project.setProjectLabels(new HashSet<>(map.values()));
            LOGGER.info("Succeed to save project labels.");
        } else {
            project.setProjectLabels(null);
        }
    }

    private void deleteAllRules(Iterable<Rule> ruleList) throws UnExpectedRequestException {
        for (Rule rule : ruleList) {
            if (rule.getRuleType().equals(RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode())) {
                ruleService.deleteRuleReal(rule);
            } else if (rule.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
                customRuleService.deleteCustomRuleReal(rule);
            } else if (rule.getRuleType().equals(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode())) {
                multiSourceRuleService.deleteMultiRuleReal(rule);
            }
        }
    }


}
