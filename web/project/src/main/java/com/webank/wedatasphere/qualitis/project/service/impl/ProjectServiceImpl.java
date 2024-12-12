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

import com.webank.wedatasphere.qualitis.dao.RoleDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.UserRole;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.EventTypeEnum;
import com.webank.wedatasphere.qualitis.project.constant.ProjectTypeEnum;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.dao.ProjectEventDao;
import com.webank.wedatasphere.qualitis.project.dao.ProjectLabelDao;
import com.webank.wedatasphere.qualitis.project.dao.ProjectUserDao;
import com.webank.wedatasphere.qualitis.project.entity.ProjectEvent;
import com.webank.wedatasphere.qualitis.project.entity.ProjectLabel;
import com.webank.wedatasphere.qualitis.project.request.AddProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.AuthorizeProjectUserRequest;
import com.webank.wedatasphere.qualitis.project.request.DeleteProjectRequest;
import com.webank.wedatasphere.qualitis.project.request.ModifyProjectDetailRequest;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectUser;
import com.webank.wedatasphere.qualitis.project.response.ProjectEventResponse;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.project.service.ProjectUserService;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.project.response.ProjectDetailResponse;
import com.webank.wedatasphere.qualitis.project.response.ProjectResponse;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.request.DeleteCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.DeleteMultiSourceRequest;
import com.webank.wedatasphere.qualitis.rule.service.CustomRuleService;
import com.webank.wedatasphere.qualitis.rule.service.FileRuleService;
import com.webank.wedatasphere.qualitis.rule.service.MultiSourceRuleService;
import com.webank.wedatasphere.qualitis.rule.service.RuleService;
import com.webank.wedatasphere.qualitis.submitter.impl.ExecutionManagerImpl;
import com.webank.wedatasphere.qualitis.util.HttpUtils;

import java.util.stream.Collectors;
import javax.management.relation.RoleNotFoundException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private RuleDao ruleDao;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProjectUserDao projectUserDao;

    @Autowired
    private ProjectLabelDao projectLabelDao;

    @Autowired
    private ProjectEventDao projectEventDao;

    @Autowired
    private ProjectUserService projectUserService;
    @Autowired
    private ProjectEventService projectEventService;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private CustomRuleService customRuleService;

    @Autowired
    private MultiSourceRuleService multiSourceRuleService;

    @Autowired
    private FileRuleService fileRuleService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);
    private static final String ADMIN = "ADMIN";

    private HttpServletRequest httpServletRequest;
    public ProjectServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<ProjectDetailResponse> addProject(AddProjectRequest request, Long userId) throws UnExpectedRequestException {
        // Check Arguments.
        AddProjectRequest.checkRequest(request);

        Project newProject = addProjectReal(userId, request.getProjectName(), request.getCnName(), request.getDescription());
        newProject.setProjectType(ProjectTypeEnum.NORMAL_PROJECT.getCode());

        Project savedProject = projectDao.saveProject(newProject);
        // Labels of project.
        Set<String> labels = request.getProjectLabels();
        addProjectLabels(labels, savedProject);

        ProjectDetailResponse response = new ProjectDetailResponse(savedProject, null);
        LOGGER.info("Succeed to add project, response: {}", response);

        return new GeneralResponse<>("200", "{&ADD_PROJECT_SUCCESSFULLY}", response);
    }

    @Override
    public Project addProjectReal(Long userId, String projectName, String cnName, String projectDescription) throws UnExpectedRequestException {
        User user = userDao.findById(userId);
        // Automatically grant the highest authority to the system administrator.
        Role role = roleDao.findByRoleName(ADMIN);
        List<User> admins = userRoleDao.findByRole(role).stream().map(UserRole::getUser).collect(Collectors.toList());
        // Check existence of project by project name.
        Project projectInDb = projectDao.findByNameAndCreateUser(projectName, user.getUserName());
        if (projectInDb != null) {
            throw new UnExpectedRequestException(String.format("{&PROJECT}:%s {&ALREADY_EXIST}", projectName));
        }

        // Save project.
        Project newProject = new Project(projectName, cnName, projectDescription, user.getUserName(), user.getChineseName(),
            user.getDepartment() != null ? user.getDepartment().getName() : "", ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date()));

        // Create project user.
        if (admins.contains(user)) {
            createProjectUser(newProject, user);
        } else {
            createProjectUser(newProject, user);

            for (User currentAdmin : admins) {
                createProjectUser(newProject, currentAdmin);
            }
        }

        return newProject;
    }

    @Override
    public GeneralResponse<GetAllResponse<ProjectResponse>> getAllProjectByUser(PageRequest request) throws UnExpectedRequestException {
        GetAllResponse<ProjectResponse> response = getAllProjectByUserReal(request, ProjectTypeEnum.NORMAL_PROJECT.getCode());

        LOGGER.info("Succeed to get all project, response: {}", response);
        return new GeneralResponse<>("200", "{&GET_ALL_PROJECT_SUCCESSFULLY}", response);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<ProjectDetailResponse> getProjectDetail(Long projectId, PageRequest pageRequest)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check existence of project
        Project projectInDb = projectDao.findById(projectId);
        if (projectInDb == null) {
            throw new UnExpectedRequestException("Project id {&DOES_NOT_EXIST}");
        }

        String username =  HttpUtils.getUserName(httpServletRequest);
        // Check if user has permission get project.
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.BUSSMAN.getCode());
        checkProjectPermission(projectInDb, username, permissions);

        // Find rules by project
        List<Rule> rules = ruleDao.findByProjectWithPage(projectInDb, pageRequest.getPage(), pageRequest.getSize());
        int total = ruleDao.countByProject(projectInDb);
        ProjectDetailResponse projectDetailResponse = new ProjectDetailResponse(projectInDb, rules);
        projectDetailResponse.setTotal(total);
        LOGGER.info("Succeed to get get project detail. project_id: {}, response: {}", projectId, projectDetailResponse);
        return new GeneralResponse<>("200", "{&GET_PROJECT_DETAIL_SUCCESSFULLY}", projectDetailResponse);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> modifyProjectDetail(ModifyProjectDetailRequest request, boolean workflow)
        throws UnExpectedRequestException, PermissionDeniedRequestException, RoleNotFoundException {
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
        User user;
        if (userId == null) {
            user = userDao.findByUsername(request.getUsername());
            if (user == null) {
                throw new UnExpectedRequestException(String.format("{&FAILED_TO_FIND_USER} %s", request.getUsername()));
            }
        } else {
            user = userDao.findById(userId);
            if (user == null) {
                throw new UnExpectedRequestException(String.format("{&FAILED_TO_FIND_USER} %s", userId));
            }
        }
        // Check if user has permission modifying project
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.BUSSMAN.getCode());
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        checkProjectPermission(projectInDb, user.getUserName(), permissions);

        // Check project name
        Project otherProject = projectDao.findByNameAndCreateUser(request.getProjectName(), user.getUserName());
        if (otherProject != null && !otherProject.getId().equals(projectInDb.getId())) {
            throw new UnExpectedRequestException(String.format("Project name: %s already exist", request.getProjectName()));
        }

        // Save project.
        String oldLabels = "";
        String newLabels = "";
        if (CollectionUtils.isNotEmpty(projectInDb.getProjectLabels())) {
            oldLabels = projectInDb.getProjectLabels().stream().map(ProjectLabel::getLabelName).collect(Collectors.joining());
        }
        if (CollectionUtils.isNotEmpty(request.getProjectLabelStrs())) {
            newLabels = request.getProjectLabelStrs().stream().collect(Collectors.joining());
        }
        if (! oldLabels.equals(newLabels)) {
            projectEventService.recordModifyProject(projectInDb, user.getUserName(), "Project labels", oldLabels, newLabels, EventTypeEnum.MODIFY_PROJECT.getCode());
        }
        if (StringUtils.isNotEmpty(projectInDb.getCnName()) && ! projectInDb.getCnName().equals(request.getCnName())) {
            projectEventService.recordModifyProject(projectInDb, user.getUserName(), "Chinese Name", projectInDb.getCnName(), request.getCnName(), EventTypeEnum.MODIFY_PROJECT.getCode());
        }
        if (! projectInDb.getName().equals(request.getProjectName())) {
            projectEventService.recordModifyProject(projectInDb, user.getUserName(), "English Name", projectInDb.getName(), request.getProjectName(), EventTypeEnum.MODIFY_PROJECT.getCode());
        }
        if (! projectInDb.getDescription().equals(request.getDescription())) {
            projectEventService.recordModifyProject(projectInDb, user.getUserName(), "Describe", projectInDb.getDescription(), request.getDescription(), EventTypeEnum.MODIFY_PROJECT.getCode());
        }
        projectInDb.setCnName(request.getCnName());
        projectInDb.setName(request.getProjectName());
        projectInDb.setDescription(request.getDescription());
        // Delete old projectLabel.
        projectLabelDao.deleteByProject(projectInDb);
        LOGGER.info("Succeed to delete all project_label, project_id: {}", request.getProjectId());
        // Create new project labels.
        addProjectLabels(request.getProjectLabelStrs(), projectInDb);
        // Record modify user
        projectInDb.setModifyUser(user.getUserName());
        projectInDb.setModifyTime(ExecutionManagerImpl.PRINT_TIME_FORMAT.format(new Date()));
        Project savedProject = projectDao.saveProject(projectInDb);
        LOGGER.info("Succeed to modify project. project: {}", savedProject);
        if (workflow) {
            // Clear old project user.
            List<ProjectUser> projectUsers = projectUserDao.findByProject(projectInDb);
            Role role = roleDao.findByRoleName(ADMIN);
            List<String> admins = userRoleDao.findByRole(role).stream().map(UserRole::getUser).map(User::getUserName).collect(Collectors.toList());
            projectUsers = projectUsers.stream()
                .filter(projectUser -> ! admins.contains(projectUser.getUserName()))
                .filter(projectUser -> ! projectUser.getUserName().equals(request.getUsername()))
                .collect(Collectors.toList());
            for (ProjectUser projectUser : projectUsers) {
                projectUserDao.deleteByProjectAndUserName(projectInDb, projectUser.getUserName());
            }
        }
        authorizeUsers(projectInDb, user, request.getAuthorizeProjectUserRequests(), true);

        return new GeneralResponse<>("200", "{&MODIFY_PROJECT_DETAIL_SUCCESSFULLY}", new ProjectDetailResponse(savedProject, null));
    }

    @Override
    public void createProjectUser(Project project, User user) {
        Map<String, ProjectUser> projectUserMap = new HashMap<>(4);

        ProjectUser creatorProject = new ProjectUser(ProjectUserPermissionEnum.CREATOR.getCode(), project, user.getUserName(), user.getChineseName());
        projectUserMap.put(user.getUserName(), creatorProject);
        
        projectUserDao.saveAll(projectUserMap.values());
        LOGGER.info("Succeed to save project_user, project_user: {}", projectUserMap.values());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> deleteProject(DeleteProjectRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
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
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.CREATOR.getCode());
        checkProjectPermission(projectInDb, user.getUserName(), permissions);
        List<Rule> rules = ruleDao.findByProject(projectInDb);
        deleteAllRules(rules, request.getUsername());
        // Delete project
        projectDao.deleteProject(projectInDb);
        LOGGER.info("Succeed to delete project. project_id: {}", request.getProjectId());
        return new GeneralResponse<>("200", "{&DELETE_PROJECT_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly=true, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public Project checkProjectExistence(Long projectId, String loginUser) throws UnExpectedRequestException {
        Project projectInDb = projectDao.findById(projectId);
        if (projectInDb == null) {
            throw new UnExpectedRequestException("project_id :["+ projectId + "] {&DOES_NOT_EXIST}");
        }

        return projectInDb;
    }

    @Override
    public GetAllResponse<ProjectResponse> getAllProjectByUserReal(PageRequest request, Integer projectType) throws UnExpectedRequestException {
        // Check Arguments
        PageRequest.checkRequest(request);
        // Get user name
        String userName = HttpUtils.getUserName(httpServletRequest);

        // Paging get project
        int page = request.getPage();
        int size = request.getSize();
        List<ProjectUser> projectUsers = projectUserDao.findByUsernameAndPermissionAndProjectType(userName, projectType, page, size);
        long total = projectUserDao.countByUsernameAndPermissionAndProjectType(userName, projectType);
        List<ProjectResponse> projectResponses = new ArrayList<>();
        for (ProjectUser projectUser : projectUsers) {
            projectResponses.add(new ProjectResponse(projectUser.getProject()));
        }

        GetAllResponse<ProjectResponse> response = new GetAllResponse<>();
        response.setData(projectResponses);
        response.setTotal(total);
        return response;
    }

    @Override
    public GeneralResponse<GetAllResponse<ProjectEventResponse>> getProjectEvents(Long projectId, Integer typeId, PageRequest pageRequest)
        throws UnExpectedRequestException, PermissionDeniedRequestException {
        PageRequest.checkRequest(pageRequest);
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        // Check project existence.
        Project projectInDb = checkProjectExistence(projectId, loginUser);
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.BUSSMAN.getCode());
        checkProjectPermission(projectInDb, loginUser, permissions);
        List<ProjectEvent> projectEvents = projectEventDao.find(pageRequest.getPage(), pageRequest.getSize(), projectInDb, typeId);
        long total = projectEventDao.count(projectInDb, typeId);
        List<ProjectEventResponse> projectEventResponses = new ArrayList<>(projectEvents.size());
        for (ProjectEvent projectEvent : projectEvents) {
            ProjectEventResponse projectEventResponse = new ProjectEventResponse(projectEvent);
            projectEventResponses.add(projectEventResponse);
        }
        GetAllResponse<ProjectEventResponse> response = new GetAllResponse<>(total, projectEventResponses);
        return new GeneralResponse<>("200", "{&SUCCESS_TO_GET_PROJECT_EVENT}", response);
    }

    @Override
    public void authorizeUsers(Project savedProject, User userInDb, List<AuthorizeProjectUserRequest> authorizeProjectUserRequests, boolean modify)
        throws UnExpectedRequestException, PermissionDeniedRequestException, RoleNotFoundException {
        if (CollectionUtils.isNotEmpty(authorizeProjectUserRequests)) {
            for (AuthorizeProjectUserRequest authorizeProjectUserRequest : authorizeProjectUserRequests) {
                authorizeProjectUserRequest.setProjectId(savedProject.getId());
                projectUserService.authorizePermission(authorizeProjectUserRequest, userInDb.getId(), modify);
            }
        }
    }

    @Override
    public GetAllResponse<ProjectResponse> getAllProjectByUserReal() {
        // Get username
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        // Find project user in different permissions.
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.CREATOR.getCode());
        permissions.add(ProjectUserPermissionEnum.DEVELOPER.getCode());
        permissions.add(ProjectUserPermissionEnum.OPERATOR.getCode());
        permissions.add(ProjectUserPermissionEnum.BUSSMAN.getCode());

        List<Project> projects = projectUserDao.findByUsernameAndPermission(loginUser, permissions);
        long total = projectUserDao.countByUsernameAndPermission(loginUser, permissions);
        List<ProjectResponse> projectResponses = new ArrayList<>();
        for (Project project : projects) {
            projectResponses.add(new ProjectResponse(project));
        }

        GetAllResponse<ProjectResponse> response = new GetAllResponse<>();
        response.setTotal(total);
        response.setData(projectResponses);

        return response;
    }

    @Override
    public void checkProjectPermission(Project project, String userName, List<Integer> permissions) throws PermissionDeniedRequestException {
        List<ProjectUser> projectUsers = projectUserDao.findByProject(project);
        for (ProjectUser projectUser : projectUsers) {
            if (projectUser.getUserName().equals(userName)) {
                // If creator, return.
                if (projectUser.getPermission().intValue() == ProjectUserPermissionEnum.CREATOR.getCode().intValue()
                    || projectUser.getUserName().equals(project.getCreateUser())) {
                    return;
                }
                // Check permissions one by one.
                if (permissions.contains(projectUser.getPermission())) {
                    permissions.remove(projectUser.getPermission());
                }
            }
        }
        if (permissions.isEmpty()) {
            return;
        } else {
            throw new PermissionDeniedRequestException("{&NO_PERMISSION_OPERATING_PROJECT}", 403);
        }
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
            for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
                if (! labels.contains(iterator.next())) {
                    iterator.remove();
                }
            }
            project.setProjectLabels(new HashSet<>(map.values()));
            LOGGER.info("Succeed to save project labels.");
        } else {
            project.setProjectLabels(null);
        }
    }

    private void deleteAllRules(Iterable<Rule> ruleList, String username) throws UnExpectedRequestException, PermissionDeniedRequestException {
        for (Rule rule : ruleList) {
            if (rule.getRuleType().equals(RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode())) {
                ruleService.deleteRule(new DeleteRuleRequest(rule.getId()), username);
            } else if (rule.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
                customRuleService.deleteCustomRule(new DeleteCustomRuleRequest(rule.getId()), username);
            } else if (rule.getRuleType().equals(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode())) {
                multiSourceRuleService.deleteMultiSourceRule(new DeleteMultiSourceRequest(rule.getId()), username);
            } else if (rule.getRuleType().equals(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode())) {
                fileRuleService.deleteRule(new DeleteFileRuleRequest(rule.getId()), username);
            }
        }

    }
}
