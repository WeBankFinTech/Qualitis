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

import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.constant.RoleTypeEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.RoleDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.dao.repository.ProxyUserRepository;
import com.webank.wedatasphere.qualitis.dao.repository.UserProxyUserRepository;
import com.webank.wedatasphere.qualitis.entity.*;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.OperateTypeEnum;
import com.webank.wedatasphere.qualitis.project.constant.ProjectTypeEnum;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.constant.SwitchTypeEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.dao.ProjectEventDao;
import com.webank.wedatasphere.qualitis.project.dao.ProjectLabelDao;
import com.webank.wedatasphere.qualitis.project.dao.ProjectUserDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectEvent;
import com.webank.wedatasphere.qualitis.project.entity.ProjectLabel;
import com.webank.wedatasphere.qualitis.project.entity.ProjectUser;
import com.webank.wedatasphere.qualitis.project.request.*;
import com.webank.wedatasphere.qualitis.project.response.ProjectDetail;
import com.webank.wedatasphere.qualitis.project.response.ProjectDetailResponse;
import com.webank.wedatasphere.qualitis.project.response.ProjectEventResponse;
import com.webank.wedatasphere.qualitis.project.response.ProjectResponse;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.project.service.ProjectService;
import com.webank.wedatasphere.qualitis.project.service.ProjectUserService;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.dao.ExecutionParametersDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleDao;
import com.webank.wedatasphere.qualitis.rule.dao.RuleGroupDao;
import com.webank.wedatasphere.qualitis.rule.entity.ExecutionParameters;
import com.webank.wedatasphere.qualitis.rule.entity.Rule;
import com.webank.wedatasphere.qualitis.rule.entity.RuleGroup;
import com.webank.wedatasphere.qualitis.rule.request.DeleteCustomRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteFileRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteRuleRequest;
import com.webank.wedatasphere.qualitis.rule.request.multi.DeleteMultiSourceRequest;
import com.webank.wedatasphere.qualitis.rule.service.CustomRuleService;
import com.webank.wedatasphere.qualitis.rule.service.FileRuleService;
import com.webank.wedatasphere.qualitis.rule.service.MultiSourceRuleService;
import com.webank.wedatasphere.qualitis.rule.service.RuleService;
import com.webank.wedatasphere.qualitis.scheduled.constant.RuleTypeEnum;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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
    private RuleGroupDao ruleGroupDao;

    @Autowired
    private ProjectUserDao projectUserDao;

    @Autowired
    private ProjectLabelDao projectLabelDao;

    @Autowired
    private ProjectEventDao projectEventDao;

    @Autowired
    private ProxyUserRepository proxyUserRepository;

    @Autowired
    private UserProxyUserRepository userProxyUserRepository;

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

    @Autowired
    private ExecutionParametersDao executionParametersDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);

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
        newProject.setSubSystemId(request.getSubSystemId());
        newProject.setSubSystemName(request.getSubSystemName());

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

        // Check existence of project by project name.
        Project projectInDb = projectDao.findByNameAndCreateUser(projectName, user.getUsername());
        if (projectInDb != null) {
            throw new UnExpectedRequestException(String.format("{&PROJECT}:%s {&ALREADY_EXIST}", projectName));
        }

        // Save project.
        Project newProject = new Project(projectName, cnName, projectDescription, user.getUsername(), user.getChineseName()
            , user.getDepartment() != null ? user.getDepartment().getName() : "", QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));
        Project savedProject = projectDao.saveProject(newProject);
        autoAuthAdminAndProxy(user, savedProject);

        projectEventService.record(newProject, user.getUsername(), Strings.EMPTY, OperateTypeEnum.CREATE_PROJECT);

        return savedProject;
    }

    @Override
    public void autoAuthAdminAndProxy(User user, Project savedProject) {
        List<Role> roles = Lists.newArrayList();
        // Automatically grant the highest authority to the system administrator.
        Role role = roleDao.findByRoleName(QualitisConstants.ADMIN);
        roles.add(role);
        if (user.getDepartment() != null) {
            Role departmentAdmin = roleDao.findByDepartmentAndRoleType(user.getDepartment(), RoleTypeEnum.SYSTEM_ROLE.getCode());
            roles.add(departmentAdmin);
        }
        List<User> admins = userRoleDao.findByRoleList(roles).stream().map(UserRole::getUser).collect(Collectors.toList());

        // Create project user.
        if (!admins.contains(user)) {
            createProjectUser(savedProject, user);
        }
        for (User currentAdmin : admins) {
            createProjectUser(savedProject, currentAdmin);
        }

        // Give user who proxy this user read permission
        ProxyUser proxyUserInDb = proxyUserRepository.findByProxyUserName(user.getUsername());
        if (proxyUserInDb != null) {
            Sort sort = Sort.by(Sort.Direction.ASC, "id");
            Pageable pageable = org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE, sort);
            List<UserProxyUser> userProxyUsers = userProxyUserRepository.findByProxyUser(proxyUserInDb, pageable);
            if (CollectionUtils.isNotEmpty(userProxyUsers)) {
                List<ProjectUser> projectUsers = new ArrayList<>(userProxyUsers.size());
                for (UserProxyUser userProxyUser : userProxyUsers) {
                    User realUser = userProxyUser.getUser();
                    if (admins.contains(realUser)) {
                        continue;
                    }
                    ProjectUser bussmanProject = new ProjectUser(ProjectUserPermissionEnum.BUSSMAN.getCode(), savedProject, realUser.getUsername(),
                        realUser.getChineseName(), SwitchTypeEnum.AUTO_MATIC.getCode());
                    projectUsers.add(bussmanProject);
                }
                projectUserDao.saveAll(projectUsers);
                LOGGER.info("Succeed to save project users as proxy user, project users: {}", projectUsers);
            }
        }
    }

    @Override
    public GeneralResponse<GetAllResponse<ProjectResponse>> getAllProjectByUser(PageRequest request) throws UnExpectedRequestException {
        GetAllResponse<ProjectResponse> response = getAllProjectByUserReal(request, ProjectTypeEnum.NORMAL_PROJECT.getCode());

        LOGGER.info("Succeed to get all project, response: {}", response);
        return new GeneralResponse<>("200", "{&GET_ALL_PROJECT_SUCCESSFULLY}", response);
    }

    @Override
    public GeneralResponse<GetAllResponse<ProjectResponse>> getProjectsByCondition(QueryProjectRequest request, Integer projectType) {
        // Get user name
        String userName = HttpUtils.getUserName(httpServletRequest);
        // Paging get project
        Page<ProjectUser> projectUsers = projectUserDao.findByAdvanceConditions(userName, projectType
                , request.getProjectName(), request.getSubsystemName(), request.getCreateUser(), request.getDb()
                , request.getTable(), request.getStartTime(), request.getEndTime(), request.getPage(), request.getSize());

        List<ProjectResponse> projectResponses = new ArrayList<>(projectUsers.getSize());
        for (ProjectUser projectUser : projectUsers) {
            projectResponses.add(new ProjectResponse(projectUser.getProject()));
        }

        GetAllResponse<ProjectResponse> response = new GetAllResponse<>();
        response.setData(projectResponses);
        response.setTotal(projectUsers.getTotalElements());

        LOGGER.info("Succeed to get projects by advance conditions, response: {}", response);
        return new GeneralResponse<>("200", "{&GET_ALL_PROJECT_SUCCESSFULLY}", response);
    }

    @Override
    public GeneralResponse<ProjectDetailResponse> getRulesByCondition(Long projectId, QueryRuleRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check existence of project
        Project projectInDb = projectDao.findById(projectId);
        if (projectInDb == null) {
            throw new UnExpectedRequestException("Project id {&DOES_NOT_EXIST}");
        }
        String username = HttpUtils.getUserName(httpServletRequest);
        // Check if user has permission get project.
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.BUSSMAN.getCode());
        checkProjectPermission(projectInDb, username, permissions);
        // Count by project
        // Find rules by project
        Page<Rule> rulePage = ruleDao.findByConditionWithPage(projectInDb, request.getRuleName(), request.getRuleCnName(), request.getRuleTemplateId()
                , request.getDb(), request.getTable(), request.getRuleEnable()
                , request.getCreateUser(), request.getModifyUser(), request.getStartCreateTime()
                , request.getEndCreateTime(), request.getStartModifyTime(), request.getEndModifyTime(), request.getRuleGroupName(), request.getWorkFlowSpace(), request.getWorkFlowProject()
                , request.getWorkFlowName(), request.getNodeName(), request.getPage(), request.getSize());
        if (rulePage.getSize() <= 0) {
            ProjectDetailResponse projectDetailResponse = new ProjectDetailResponse();
            projectDetailResponse.setProjectDetail(new ProjectDetail());
            projectDetailResponse.setRuleDetails(Collections.emptyList());
            return new GeneralResponse<>("200", "{&GET_PROJECT_DETAIL_SUCCESSFULLY}", projectDetailResponse);
        }

        ProjectDetailResponse projectDetailResponse = new ProjectDetailResponse(projectInDb, rulePage.getContent());
        projectDetailResponse.setTotal(Long.valueOf(rulePage.getTotalElements()).intValue());
        LOGGER.info("Succeed to get project rules. project_id: {}, response: {}", projectId, projectDetailResponse);
        return new GeneralResponse<>("200", "{&GET_PROJECT_DETAIL_SUCCESSFULLY}", projectDetailResponse);
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

        String username = HttpUtils.getUserName(httpServletRequest);
        // Check if user has permission get project.
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.BUSSMAN.getCode());
        checkProjectPermission(projectInDb, username, permissions);

        // Find rules by project
        List<Rule> rules = ruleDao.findByProjectWithPage(projectInDb, pageRequest.getPage(), pageRequest.getSize());
        int total = ruleDao.countByProject(projectInDb);
        ProjectDetailResponse projectDetailResponse = new ProjectDetailResponse(projectInDb, rules);
        projectDetailResponse.setTotal(total);
        LOGGER.info("Succeed to get project detail. project_id: {}, response: {}", projectId, projectDetailResponse);
        return new GeneralResponse<>("200", "{&GET_PROJECT_DETAIL_SUCCESSFULLY}", projectDetailResponse);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<ProjectDetailResponse> modifyProjectDetail(ModifyProjectDetailRequest request, boolean workflow)
            throws UnExpectedRequestException, PermissionDeniedRequestException, RoleNotFoundException {
        // Check Arguments
        ModifyProjectDetailRequest.checkRequest(request);

        Project projectInDb = projectDao.findByNameAndCreateUser(request.getProjectName(), HttpUtils.getUserName(httpServletRequest));
        if (null != projectInDb && !projectInDb.getId().equals(request.getProjectId())) {
            throw new UnExpectedRequestException(String.format("{&PROJECT}:%s {&ALREADY_EXIST}", request.getProjectName()));
        }
        // Check existence of project
        projectInDb = projectDao.findById(request.getProjectId());
        if (projectInDb == null) {
            throw new UnExpectedRequestException("project ID {&DOES_NOT_EXIST}");
        }
        LOGGER.info("Succeed to get project. project: {}", projectInDb);

        // Get userId
        Long userId = HttpUtils.getUserId(httpServletRequest);
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
        checkProjectPermission(projectInDb, user.getUsername(), permissions);

        // Check project name
        Project otherProject = projectDao.findByNameAndCreateUser(request.getProjectName(), user.getUsername());
        if (otherProject != null && ! otherProject.getId().equals(projectInDb.getId())) {
            throw new UnExpectedRequestException(String.format("Project name: %s already exist", request.getProjectName()));
        }
        // Record modify field detail.
        projectEventService.record(projectInDb, user.getUsername(), Strings.EMPTY, OperateTypeEnum.MODIFY_PROJECT);

        projectInDb.setCnName(request.getCnName());
        projectInDb.setName(request.getProjectName());
        projectInDb.setDescription(request.getDescription());
        projectInDb.setSubSystemId(request.getSubSystemId());
        projectInDb.setSubSystemName(request.getSubSystemName());
        // Delete old projectLabel.
        projectLabelDao.deleteByProject(projectInDb);
        LOGGER.info("Succeed to delete all project_label, project_id: {}", request.getProjectId());
        // Create new project labels.
        addProjectLabels(request.getProjectLabelStrs(), projectInDb);
        // Record modify user
        projectInDb.setModifyUser(user.getUsername());
        projectInDb.setModifyTime(QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));
        Project savedProject = projectDao.saveProject(projectInDb);
        LOGGER.info("Succeed to modify project. project: {}", savedProject);
        if (workflow) {
            // Clear old project user.
            List<ProjectUser> projectUsers = projectUserDao.findByProject(projectInDb);
            Role role = roleDao.findByRoleName(QualitisConstants.ADMIN);
            List<String> admins = userRoleDao.findByRole(role).stream().map(UserRole::getUser).map(User::getUsername).collect(Collectors.toList());
            projectUsers = projectUsers.stream()
                    .filter(projectUser -> !admins.contains(projectUser.getUserName()))
                    .filter(projectUser -> !projectUser.getUserName().equals(request.getUsername()))
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

        ProjectUser creatorProject = new ProjectUser(ProjectUserPermissionEnum.CREATOR.getCode(), project, user.getUsername(), user.getChineseName(), SwitchTypeEnum.AUTO_MATIC.getCode());
        projectUserMap.put(user.getUsername(), creatorProject);

        projectUserDao.saveAll(projectUserMap.values());
        LOGGER.info("Succeed to save project_user, project_user: {}", projectUserMap.values());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse deleteProject(DeleteProjectRequest request, boolean workflow) throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check Arguments
        DeleteProjectRequest.checkRequest(request);

        // Check existence of project
        Project projectInDb = projectDao.findById(request.getProjectId());
        if (projectInDb == null) {
            throw new UnExpectedRequestException("project_id {&DOES_NOT_EXIST}");
        }
        User user;
        if (!workflow) {
            user = userDao.findById(HttpUtils.getUserId(httpServletRequest));
        } else {
            user = userDao.findByUsername(request.getUsername());
        }

        if (user == null) {
            throw new UnExpectedRequestException(String.format("{&FAILED_TO_FIND_USER} %s", request.getUsername()));
        }
        // Check project permission
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.CREATOR.getCode());
        checkProjectPermission(projectInDb, user.getUsername(), permissions);

        //Delete executionParameters
        List<ExecutionParameters> executionParametersList = executionParametersDao.getAllExecutionParameters(projectInDb.getId());
        if (CollectionUtils.isNotEmpty(executionParametersList)) {
            for (ExecutionParameters executionParameters : executionParametersList) {
                executionParametersDao.deleteExecutionParameters(executionParameters);
                LOGGER.info("Succeed to delete ExecutionParameters. executionParameters_id: {}", executionParameters.getId());
            }
        }
        List<Rule> rules = ruleDao.findByProject(projectInDb);
        deleteAllRules(rules, user.getUsername());
        // Delete project
        projectDao.deleteProject(projectInDb);

        projectEventService.record(projectInDb, user.getUsername(), Strings.EMPTY, OperateTypeEnum.DELETE_PROJECT);

        List<RuleGroup> ruleGroupList = rules.stream().map(Rule::getRuleGroup).filter(Objects::nonNull).collect(Collectors.toList());
        ruleGroupDao.deleteAll(ruleGroupList);

        LOGGER.info("Succeed to delete project. project_id: {}", request.getProjectId());
        return new GeneralResponse<>("200", "{&DELETE_PROJECT_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public Project checkProjectExistence(Long projectId, String loginUser) throws UnExpectedRequestException {
        Project projectInDb = projectDao.findById(projectId);
        if (projectInDb == null) {
            throw new UnExpectedRequestException("Sorry ! " + loginUser + "'s project ID :[" + projectId + "] {&DOES_NOT_EXIST}");
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
        List<ProjectUser> projectUsers = projectUserDao.findByUserNameAndProjectType(userName, projectType, page, size);
        long total = projectUserDao.countByUserNameAndProjectType(userName, projectType);
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
    public GeneralResponse<GetAllResponse<ProjectEventResponse>> getProjectEvents(Long projectId, PageRequest pageRequest)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        PageRequest.checkRequest(pageRequest);
        String loginUser = HttpUtils.getUserName(httpServletRequest);
        // Check project existence.
        Project projectInDb = checkProjectExistence(projectId, loginUser);
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.BUSSMAN.getCode());
        checkProjectPermission(projectInDb, loginUser, permissions);
        Page<ProjectEvent> projectEventPage = projectEventDao.findWithPage(pageRequest.getPage(), pageRequest.getSize(), projectInDb.getId());
        List<ProjectEventResponse> projectEventResponses = projectEventPage.getContent().stream().map(ProjectEventResponse::new).collect(Collectors.toList());
        GetAllResponse<ProjectEventResponse> response = new GetAllResponse<>(projectEventPage.getTotalElements(), projectEventResponses);
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

        List<Map<String, Object>> projects = projectUserDao.findProjectByUserName(loginUser);
        long total = projectUserDao.countProjectByUserName(loginUser);
        List<ProjectResponse> projectResponses = new ArrayList<>();
        for (Map<String, Object> project : projects) {
            projectResponses.add(new ProjectResponse(project));
        }

        GetAllResponse<ProjectResponse> response = new GetAllResponse<>();
        response.setData(projectResponses);
        response.setTotal(total);

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
        if (labels != null && !labels.isEmpty()) {
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
            for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext(); ) {
                if (!labels.contains(iterator.next())) {
                    iterator.remove();
                }
            }
            project.setProjectLabels(new HashSet<>(map.values()));
            LOGGER.info("Succeed to save project labels.");
        } else {
            project.setProjectLabels(null);
        }
    }

    @Override
    public void deleteAllRules(Iterable<Rule> ruleList, String loginUser) throws UnExpectedRequestException, PermissionDeniedRequestException {
        for (Rule rule : ruleList) {
            if (rule.getRuleType().equals(RuleTypeEnum.SINGLE_TEMPLATE_RULE.getCode())) {
                ruleService.deleteRule(new DeleteRuleRequest(rule.getId()), loginUser);
            } else if (rule.getRuleType().equals(RuleTypeEnum.CUSTOM_RULE.getCode())) {
                customRuleService.deleteCustomRule(new DeleteCustomRuleRequest(rule.getId()), loginUser);
            } else if (rule.getRuleType().equals(RuleTypeEnum.MULTI_TEMPLATE_RULE.getCode())) {
                multiSourceRuleService.deleteMultiSourceRule(new DeleteMultiSourceRequest(rule.getId()), loginUser);
            } else if (rule.getRuleType().equals(RuleTypeEnum.FILE_TEMPLATE_RULE.getCode())) {
                fileRuleService.deleteRule(new DeleteFileRuleRequest(rule.getId()), loginUser);
            }
        }
    }

    @Override
    public GeneralResponse<ProjectDetailResponse> getAllRules(Long projectId) throws UnExpectedRequestException, PermissionDeniedRequestException {
        // Check existence of project
        Project projectInDb = projectDao.findById(projectId);
        if (projectInDb == null) {
            throw new UnExpectedRequestException("Project id {&DOES_NOT_EXIST}");
        }

        String username = HttpUtils.getUserName(httpServletRequest);
        // Check if user has permission get project.
        List<Integer> permissions = new ArrayList<>();
        permissions.add(ProjectUserPermissionEnum.BUSSMAN.getCode());
        checkProjectPermission(projectInDb, username, permissions);

        // Find rules by project
        List<Map<String, Object>> rules = ruleDao.findSpecialInfoByProject(projectInDb);
        List<RuleGroup> notExistsRuleGroups = ruleGroupDao.findByProjectIdAndNotExistRule(projectId);
        if (CollectionUtils.isNotEmpty(notExistsRuleGroups)) {
            LOGGER.info("Delete groups which has no rules. Rule group names: {}", Strings.join(notExistsRuleGroups.stream().map(ruleGroup -> ruleGroup.getRuleGroupName()).collect(Collectors.toList()), ','));
            checkAndDeleteEmptyRuleGroups(notExistsRuleGroups);
        }
        ProjectDetailResponse projectDetailResponse = new ProjectDetailResponse(rules);
        projectDetailResponse.setTotal(rules != null ? rules.size() : 0);
        LOGGER.info("Succeed to get project rules. project_id: {}, response: {}", projectId, projectDetailResponse);
        return new GeneralResponse<>("200", "{&GET_PROJECT_DETAIL_SUCCESSFULLY}", projectDetailResponse);
    }

    private void checkAndDeleteEmptyRuleGroups(List<RuleGroup> ruleGroups) {
        for (RuleGroup ruleGroup: ruleGroups) {
            ruleGroupDao.delete(ruleGroup);
        }
    }

    @Override
    public List<Project> checkProjectsExistence(List<Long> projectIds, String loginUser) throws UnExpectedRequestException {
        List<Project> projectInDb = projectDao.findAllById(projectIds);
        List<Long> collect = projectInDb.stream().map(Project::getId).collect(Collectors.toList());
        List<Long> nonexistentProject = projectIds.stream().filter(o -> !collect.contains(o)).collect(Collectors.toList());

        if (!nonexistentProject.isEmpty()) {
            throw new UnExpectedRequestException("project_id :" + nonexistentProject + " {&DOES_NOT_EXIST}");
        }

        return projectInDb;
    }

}
