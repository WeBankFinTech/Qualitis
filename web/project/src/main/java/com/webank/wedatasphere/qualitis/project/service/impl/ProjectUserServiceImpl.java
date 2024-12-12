package com.webank.wedatasphere.qualitis.project.service.impl;

import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.OperateTypeEnum;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.constant.SwitchTypeEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.dao.ProjectUserDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectUser;
import com.webank.wedatasphere.qualitis.project.request.AuthorizeProjectUserRequest;
import com.webank.wedatasphere.qualitis.project.response.ProjectUserResponse;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.project.service.ProjectUserService;
import com.webank.wedatasphere.qualitis.report.dao.SubscribeOperateReportDao;
import com.webank.wedatasphere.qualitis.report.dao.SubscribeOperateReportProjectsDao;
import com.webank.wedatasphere.qualitis.report.entity.SubscribeOperateReport;
import com.webank.wedatasphere.qualitis.report.entity.SubscribeOperateReportProjects;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.UserService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author allenzhou
 */
@Service
public class ProjectUserServiceImpl implements ProjectUserService {
    @Autowired
    private ProjectEventService projectEventService;
    @Autowired
    private ProjectUserDao projectUserDao;
    @Autowired
    private UserRoleDao userRoleDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;
    @Autowired
    private SubscribeOperateReportProjectsDao subscribeOperateReportProjectsDao;
    @Autowired
    private SubscribeOperateReportDao subscribeOperateReportDao;

    @Context
    private HttpServletRequest httpRequest;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectUserServiceImpl.class);

    public ProjectUserServiceImpl(@Context HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<ProjectUserResponse> authorizePermission(AuthorizeProjectUserRequest authorizeProjectUserRequest, Long loginUserId, boolean modify)
            throws UnExpectedRequestException, PermissionDeniedRequestException, RoleNotFoundException {
        List<ProjectUser> projectUsers = new ArrayList<>();
        AuthorizeProjectUserRequest.checkRequest(authorizeProjectUserRequest);
        Project projectInDb = projectDao.findById(authorizeProjectUserRequest.getProjectId());
        String projectUser = authorizeProjectUserRequest.getProjectUser();
        LOGGER.info("User[id={}] start to authorize user[name={}]", loginUserId, projectUser);
        if (projectInDb == null) {
            throw new UnExpectedRequestException("{&PROJECT}: [ID=" + authorizeProjectUserRequest.getProjectId() + "] {&DOES_NOT_EXIST}");
        }
        User projectUserInDb = userDao.findByUsername(projectUser);
        if (projectUserInDb == null) {
            LOGGER.warn("Project user is from outside, qualitis will auto add user. Name: " + projectUser);
            userService.autoAddUser(projectUser);
        }
        User loginUser = userDao.findById(loginUserId);
        if (!checkPermission(projectInDb, loginUser.getUsername(), ProjectUserPermissionEnum.CREATOR.getCode())) {
            throw new PermissionDeniedRequestException("{&NO_PERMISSION_MODIFYING_PROJECT}", 403);
        }
        if (loginUser.getUsername().equals(projectUser)) {
            return null;
        }
        List<Integer> permissions = new ArrayList<>();
        if (modify) {
            projectUserDao.deleteByProjectAndUserName(projectInDb, projectUser);
            LOGGER.info("Success to delete original project user permissions.");
        }
        for (Integer permission : authorizeProjectUserRequest.getProjectPermissions()) {
            ProjectUser tmp = new ProjectUser(permission, projectInDb, projectUser, SwitchTypeEnum.HAND_MOVEMENT.getCode());
            LOGGER.info("User[name={}] get permission[ID={}].", projectUser, permission);
            projectUsers.add(tmp);
            permissions.add(permission);
        }
        projectUserDao.saveAll(projectUsers);
        String authorizedUsernames = projectUsers.stream().map(currProjectUser -> currProjectUser.getUserName()).distinct().collect(Collectors.joining(SpecCharEnum.COMMA.getValue()));
        projectEventService.record(projectInDb, loginUser.getUsername(), "授权 " + authorizedUsernames, OperateTypeEnum.AUTHORIZE_PROJECT);
        ProjectUserResponse projectUserResponse = new ProjectUserResponse(projectInDb.getName(), loginUser.getUsername(), projectUser);
        projectUserResponse.setPermissions(permissions);

        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&SUCCESS_TO_ADD_PROJECT_USER}", projectUserResponse);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse deletePermission(AuthorizeProjectUserRequest request, Long loginUserId)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        Long projectId = request.getProjectId();
        Project projectInDb = projectDao.findById(projectId);
        if (projectInDb == null) {
            throw new UnExpectedRequestException("{&PROJECT}: [ID=" + projectId + "] {&DOES_NOT_EXIST}");
        }
        User loginUser = userDao.findById(loginUserId);
        if (!checkPermission(projectInDb, loginUser.getUsername(), ProjectUserPermissionEnum.CREATOR.getCode())) {
            throw new PermissionDeniedRequestException("{&NO_PERMISSION_MODIFYING_PROJECT}", 403);
        }
        projectUserDao.deleteByProjectAndUserName(projectInDb, request.getProjectUser());
        projectEventService.record(projectInDb, loginUser.getUsername(), "回收授权 " + request.getProjectUser(), OperateTypeEnum.UNAUTHORIZE_PROJECT);

        //remove subscription report recipients
        List<SubscribeOperateReportProjects> subscribeOperateReportProjects = subscribeOperateReportProjectsDao.findByProjectId(projectInDb.getId());
        List<SubscribeOperateReport> subscribeOperateReports = subscribeOperateReportProjects.stream().map(item -> item.getSubscribeOperateReport()).collect(Collectors.toList());
        for (SubscribeOperateReport subscribeOperateReport : subscribeOperateReports) {
            String[] receivers = subscribeOperateReport.getReceiver().split(SpecCharEnum.COMMA.getValue());
            StringBuilder result = new StringBuilder();
            for (String receiver : receivers) {
                if (request.getProjectUser().equals(receiver)) {
                    continue;
                } else {
                    result.append(receiver).append(SpecCharEnum.COMMA.getValue());
                }
            }
            if (result.length() > 0) {
                subscribeOperateReport.setReceiver(result.deleteCharAt(result.length() - 1).toString());
                subscribeOperateReport.setModifyTime(QualitisConstants.PRINT_TIME_FORMAT.format(new Date()));
                subscribeOperateReport.setModifyUser(loginUser.getUsername());
                subscribeOperateReportDao.save(subscribeOperateReport);
            } else {
                //recipient is empty
                subscribeOperateReportDao.delete(subscribeOperateReport);
            }
        }
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&DELETE_USER_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<List<ProjectUserResponse>> getAllProjectUser(Long projectId)
            throws UnExpectedRequestException, PermissionDeniedRequestException {
        List<ProjectUserResponse> projectUserResponses = new ArrayList<>();
        Project projectInDb = projectDao.findById(projectId);
        if (projectInDb == null) {
            throw new UnExpectedRequestException("{&PROJECT}: [ID=" + projectId + "] {&DOES_NOT_EXIST}");
        }
        String userName = HttpUtils.getUserName(httpRequest);
        List<ProjectUser> projectUsers = projectUserDao.findByProject(projectInDb);
        List<String> projectUserNames = projectUsers.stream().map(ProjectUser::getUserName).collect(Collectors.toList());
        if (!projectUserNames.contains(userName)) {
            throw new PermissionDeniedRequestException("{&HAS_NO_PERMISSION_TO_ACCESS}", 403);
        }
        for (ProjectUser currentProjectUser : projectUsers) {
            List<String> userNames = projectUserResponses.stream().map(ProjectUserResponse::getAuthorizedUser).collect(Collectors.toList());
            String projectCreator = projectInDb.getCreateUser();
            String currentUser = currentProjectUser.getUserName();
            if (userNames.contains(currentUser)) {
                continue;
            }
            ProjectUserResponse projectUserResponse = new ProjectUserResponse(projectInDb.getName(), projectCreator, currentUser);
            projectUserResponse.setHidden(currentProjectUser.getAutomaticSwitch() != null ? currentProjectUser.getAutomaticSwitch() : Boolean.FALSE);
            List<Integer> permissions = getPermissionList(projectUsers, currentUser);
            projectUserResponse.setPermissions(permissions);
            projectUserResponses.add(projectUserResponse);
        }

        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&SUCCESS_TO_GET_PROJECT_USER}", projectUserResponses);
    }

    @Override
    public List<Integer> getPermissionList(List<ProjectUser> projectUsers, String userName) {
        List<Integer> permissions = projectUsers.stream().filter(projectUser -> projectUser.getUserName().equals(userName)).map(ProjectUser::getPermission).distinct().collect(Collectors.toList());

        return permissions;
    }

    @Override
    public boolean checkPermission(Project project, String userName, Integer permission) {
        return getPermissionList(projectUserDao.findByProject(project), userName).contains(permission);
    }

    @Override
    public GeneralResponse<List<String>> getAllUsers(PageRequest request, Long projectCreatorId) throws UnExpectedRequestException {
        PageRequest.checkRequest(request);
        User projectCreator = userDao.findById(projectCreatorId);
        if (projectCreator == null) {
            throw new UnExpectedRequestException("User {&DOES_NOT_EXIST}, projectCreatorId: " + projectCreatorId);
        }
        List<Map<String, Object>> userList = userDao.findAllUserIdAndName();
        List<String> usernameList = userList.stream().filter(Objects::nonNull).map(user -> (String) user.get("username")).collect(Collectors.toList());
        LOGGER.info("Get all users by: " + projectCreator.getUsername());
        return new GeneralResponse<>(ResponseStatusConstants.OK, "{&FIND_ALL_USERS_SUCCESSFULLY}", usernameList);
    }

}
