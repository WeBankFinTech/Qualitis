package com.webank.wedatasphere.qualitis.project.service.impl;

import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.EventTypeEnum;
import com.webank.wedatasphere.qualitis.project.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.dao.ProjectUserDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectUser;
import com.webank.wedatasphere.qualitis.project.request.AuthorizeProjectUserRequest;
import com.webank.wedatasphere.qualitis.project.response.ProjectUserResponse;
import com.webank.wedatasphere.qualitis.project.service.ProjectEventService;
import com.webank.wedatasphere.qualitis.project.service.ProjectUserService;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.service.UserService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.management.relation.RoleNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    private ProjectDao projectDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;
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
        if (! checkPermission(projectInDb, loginUser.getUserName(), ProjectUserPermissionEnum.CREATOR.getCode())) {
            throw new PermissionDeniedRequestException("{&NO_PERMISSION_MODIFYING_PROJECT}", 403);
        }
        if (loginUser.getUserName().equals(projectUser)) {
            return null;
        }
        List<Integer> permissions = new ArrayList<>();
        if (modify) {
            projectUserDao.deleteByProjectAndUserName(projectInDb, projectUser);
            LOGGER.info("Success to delete original project user permissions.");
        }
        for (Integer permission : authorizeProjectUserRequest.getProjectPermissions()) {
            ProjectUser tmp = new ProjectUser(permission, projectInDb, projectUser);
            LOGGER.info("User[name={}] get permission[ID={}].", projectUser, permission);
            projectUsers.add(tmp);
            permissions.add(permission);
        }
        projectUserDao.saveAll(projectUsers);
//        projectEventService.record(projectInDb.getId(), loginUser.getUserName(), "authorized", projectUser, EventTypeEnum.MODIFY_PROJECT.getCode());
        ProjectUserResponse projectUserResponse = new ProjectUserResponse(projectInDb.getName(), loginUser.getUserName(), projectUser);
        projectUserResponse.setPermissions(permissions);

        return new GeneralResponse<>("200", "{&SUCCESS_TO_ADD_PROJECT_USER}", projectUserResponse);
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
        if (! checkPermission(projectInDb, loginUser.getUserName(), ProjectUserPermissionEnum.CREATOR.getCode())) {
            throw new PermissionDeniedRequestException("{&NO_PERMISSION_MODIFYING_PROJECT}", 403);
        }
        projectUserDao.deleteByProjectAndUserName(projectInDb, request.getProjectUser());
//        projectEventService.record(projectInDb.getId(), loginUser.getUserName(), "authorized", request.getProjectUser(), EventTypeEnum.MODIFY_PROJECT.getCode());
        return new GeneralResponse<>("200", "{&DELETE_USER_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly=true, rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
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
        if (! projectUserNames.contains(userName)) {
            throw new PermissionDeniedRequestException("{&HAS_NO_PERMISSION_TO_ACCESS}", 403);
        }
        for (ProjectUser currentProjectUser : projectUsers) {
            List<String> userNames = projectUserResponses.stream().map(ProjectUserResponse::getAuthorizedUser).collect(Collectors.toList());
            String currentUser = currentProjectUser.getUserName();
            if (userNames.contains(currentUser)) {
                continue;
            }
            String projectCreator = projectInDb.getCreateUser();
            ProjectUserResponse projectUserResponse = new ProjectUserResponse(projectInDb.getName(), projectCreator, currentUser);
            List<Integer> permissions = getPermissionList(projectUsers, currentUser);
            projectUserResponse.setPermissions(permissions);
            projectUserResponses.add(projectUserResponse);
        }

        return new GeneralResponse<>("200", "{&SUCCESS_TO_GET_PROJECT_USER}", projectUserResponses);
    }

    @Override
    public List<Integer> getPermissionList(List<ProjectUser> projectUsers, String userName) {
        List<Integer> permissions = projectUsers.stream()
            .filter(projectUser -> projectUser.getUserName().equals(userName)).map(ProjectUser::getPermission).distinct().collect(Collectors.toList());

        return permissions;
    }

    @Override
    public boolean checkPermission(Project project, String userName, Integer permission) {
        return getPermissionList(projectUserDao.findByProject(project), userName).contains(permission);
    }

    @Override
    public GeneralResponse<List<String>> getAllUsers(PageRequest request, Long projectCreatorId) throws UnExpectedRequestException {
        PageRequest.checkRequest(request);
        int page = request.getPage();
        int size = request.getSize();
        User projectCreator = userDao.findById(projectCreatorId);
        if (projectCreator == null) {
            throw new UnExpectedRequestException("User {&DOES_NOT_EXIST}, name: " + projectCreator.getUserName());
        }
        LOGGER.info("Get all users by: " + projectCreator.getUserName());
        List<User> users = userDao.findAllUser(page, size);
        return new GeneralResponse<>("200", "{&FIND_ALL_USERS_SUCCESSFULLY}", users.stream().map(User::getUserName).collect(Collectors.toList()));
    }

}
