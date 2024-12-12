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

package com.webank.wedatasphere.qualitis.service.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.qualitis.constant.ProjectUserPermissionEnum;
import com.webank.wedatasphere.qualitis.constant.RoleTypeEnum;
import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.dao.RoleDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserRole;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.project.constant.SwitchTypeEnum;
import com.webank.wedatasphere.qualitis.project.dao.ProjectDao;
import com.webank.wedatasphere.qualitis.project.dao.ProjectUserDao;
import com.webank.wedatasphere.qualitis.project.entity.Project;
import com.webank.wedatasphere.qualitis.project.entity.ProjectUser;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.request.QueryUserRequest;
import com.webank.wedatasphere.qualitis.request.userrole.AddUserRoleRequest;
import com.webank.wedatasphere.qualitis.request.userrole.DeleteUserRoleRequest;
import com.webank.wedatasphere.qualitis.request.userrole.ModifyUserRoleRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.UserRoleResponse;
import com.webank.wedatasphere.qualitis.rule.constant.RoleSystemTypeEnum;
import com.webank.wedatasphere.qualitis.service.UserRoleService;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.SpringContextHolder;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * @author howeye
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private ProjectUserDao projectUserDao;

    public static final FastDateFormat PRINT_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleServiceImpl.class);
    private HttpServletRequest httpServletRequest;

    public UserRoleServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<UserRoleResponse> addUserRole(AddUserRoleRequest request) throws UnExpectedRequestException, ExecutionException, InterruptedException {
        // Check Arguments
        checkRequest(request);

        // Check existence of user, role and user role
        Long userId = request.getUserId();
        Long roleId = request.getRoleId();
        User userInDb = userDao.findById(userId);
        if (userInDb == null) {
            throw new UnExpectedRequestException("userId {&DOES_NOT_EXIST}, request: " + request);
        }
        Role roleInDb = roleDao.findById(roleId);
        if (roleInDb == null) {
            throw new UnExpectedRequestException("roleId {&DOES_NOT_EXIST}, request: " + request);
        }
        UserRole userRoleInDb = userRoleDao.findByUserAndRole(userInDb, roleInDb);
        if (userRoleInDb != null) {
            throw new UnExpectedRequestException("userId and roleId {&ALREADY_EXIST}, request: " + request);
        }

        //一个用户只能配置一个职位角色
        long total = userRoleDao.countPositionRole(userId, RoleTypeEnum.POSITION_ROLE.getCode());
        if ((total == 1 || total > 1) && roleInDb.getRoleType().equals(RoleTypeEnum.POSITION_ROLE.getCode())) {
            throw new UnExpectedRequestException("{&ONLY_ONE_POSITION_ROLE_CAN_BE_CONFIGURED_FOR_ONE_USER}");
        }

        addProjectPermission(roleId, userInDb);

        // Save user role
        UserRole newUserRole = new UserRole();
        newUserRole.setRole(roleInDb);
        newUserRole.setUser(userInDb);
        newUserRole.setId(UuidGenerator.generate());
        newUserRole.setCreateUser(HttpUtils.getUserName(httpServletRequest));
        newUserRole.setCreateTime(UserRoleServiceImpl.PRINT_TIME_FORMAT.format(new Date()));

        UserRole savedUserRole = userRoleDao.saveUserRole(newUserRole);
        UserRoleResponse response = new UserRoleResponse(savedUserRole);

        LOGGER.info("Succeed to add user_role: response: {}, current_user: {}", response, HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&ADD_USER_ROLE_SUCCESSFULLY}", response);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse deleteUserRole(DeleteUserRoleRequest request) throws UnExpectedRequestException, InterruptedException {
        // Check Arguments
        checkRequest(request);

        // Check existence of user role
        String uuid = request.getUuid();
        UserRole userRoleInDb = userRoleDao.findByUuid(uuid);
        if (userRoleInDb == null) {
            throw new UnExpectedRequestException("user role id {&DOES_NOT_EXIST}, request: " + request);
        }

        // Delete user role
        userRoleDao.deleteUserRole(userRoleInDb);

        //查询用户角色是否为部门管理员或系统管理员
        checkExistRoles(userRoleInDb);
        LOGGER.info("Succeed to delete user_role. uuid: {}, current_user: {}", request.getUuid(), HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&DELETE_USER_ROLE_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse modifyUserRole(ModifyUserRoleRequest request) throws UnExpectedRequestException, ExecutionException, InterruptedException {
        // Check Arguments
        checkRequest(request);

        // Find user role by id
        String uuid = request.getUuid();
        UserRole userRoleInDb = userRoleDao.findByUuid(uuid);
        if (userRoleInDb == null) {
            throw new UnExpectedRequestException("user role id {&DOES_NOT_EXIST}, request: " + request);
        }
        LOGGER.info("Succeed to find user_role, uuid: {}, user_id: {}, role_id: {}, current_user: {}", uuid, userRoleInDb.getUser().getId(),
                userRoleInDb.getRole().getId(), HttpUtils.getUserName(httpServletRequest));

        Long userId = request.getUserId();
        Long roleId = request.getRoleId();
        User userInDb = userDao.findById(userId);
        if (userInDb == null) {
            throw new UnExpectedRequestException("userId {&DOES_NOT_EXIST}, request: " + request);
        }
        Role roleInDb = roleDao.findById(roleId);
        if (roleInDb == null) {
            throw new UnExpectedRequestException("roleId {&DOES_NOT_EXIST}, request: " + request);
        }
        UserRole userIdAndRoleIdInDb = userRoleDao.findByUserAndRole(userInDb, roleInDb);
        if (userIdAndRoleIdInDb != null) {
            throw new UnExpectedRequestException("userId and roleId {&ALREADY_EXIST}, request: " + request);
        }

        userRoleInDb.setUser(userInDb);
        userRoleInDb.setRole(roleInDb);
        userRoleInDb.setModifyUser(HttpUtils.getUserName(httpServletRequest));
        userRoleInDb.setModifyTime(UserRoleServiceImpl.PRINT_TIME_FORMAT.format(new Date()));
        UserRole savedUserRole = userRoleDao.saveUserRole(userRoleInDb);

        addProjectPermission(roleId, userInDb);
        LOGGER.info("Succeed to modify user_role, uuid: {}, user_id: {}, role_id: {}, current_user: {}", uuid, savedUserRole.getUser().getId(),
                savedUserRole.getRole().getId(), HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&MODIFY_USER_ROLE_SUCCESSFULLY}", null);
    }

    @Override
    public GeneralResponse<GetAllResponse<UserRoleResponse>> findAllUserRole(QueryUserRequest request) throws UnExpectedRequestException {
        // Check Arguments
        PageRequest.checkRequest(request);

        int page = request.getPage();
        int size = request.getSize();
        String userName = null;
        if (StringUtils.isNotBlank(request.getUserName())) {
            userName = SpecCharEnum.PERCENT.getValue() + request.getUserName() + SpecCharEnum.PERCENT.getValue();
        }
        List<UserRole> userRoles = userRoleDao.findAllUserRole(userName, page, size);
        long total = userRoleDao.countAllUserRole(userName);
        List<UserRoleResponse> userRoleResponses = new ArrayList<>();
        for (UserRole userRole : userRoles) {
            UserRoleResponse response = new UserRoleResponse(userRole);
            userRoleResponses.add(response);
        }
        GetAllResponse<UserRoleResponse> responses = new GetAllResponse<>();
        responses.setTotal(total);
        responses.setData(userRoleResponses);

        LOGGER.info("Succeed to find all user_roles, response: {}, current_user: {}", responses, HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&FIND_ALL_USER_ROLES_SUCCESSFULLY}", responses);
    }

    private void checkRequest(ModifyUserRoleRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkUuid(request.getUuid());
        checkId(request.getUserId(), "userId");
        checkId(request.getRoleId(), "roleId");
    }

    private void checkRequest(DeleteUserRoleRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkUuid(request.getUuid());
    }

    private void checkUuid(String uuid) throws UnExpectedRequestException {
        if (StringUtils.isBlank(uuid)) {
            throw new UnExpectedRequestException("uuid {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }

    private void checkRequest(AddUserRoleRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}l");
        }
        checkId(request.getUserId(), "userId");
        checkId(request.getRoleId(), "roleId");
    }

    private void checkId(Long id, String idName) throws UnExpectedRequestException {
        if (id == null) {
            throw new UnExpectedRequestException(idName + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }

    /**
     * 授权管理员时->所有项目授权
     *
     * @param userRoleInDb 用户角色(改动的)
     */
    public void checkExistRoles(UserRole userRoleInDb) throws InterruptedException {
        //当系统管理员+部门管理的人员发生变动时，需移除所有依赖了该人员的项目权限，移除权限依赖(用户创建项目时自动赋权的用户)
        //用户角色管理  页面表单-> a.用户名;  b.角色名;
        //管理员、部门管理员角色授权和取消授权，对应有项目权限的变动

        //1.判断当前用户角色是否部门管理员
        boolean deptAdmin = userRoleInDb.getRole().getName().endsWith(RoleSystemTypeEnum.DEPARTMENT_ADMIN.getMessage());

        //2.获取系统管理员角色
        Role role = roleDao.findByRoleName(QualitisConstants.ADMIN);
        //3.修改或删除操作(条件判断): 不是部门管理员或系统管理员
        if (deptAdmin || (userRoleInDb.getRole().getId() + "").equals(role.getId() + "")) {
            //4.删除该用户项目权限----->日志输出 log.info()    批量删除   ------>先批量删除 查看删除效率，再考虑用异步实现
            //4->a.查询ProjectUser表, 匹配的用户名、标识switch为自动的List<ProjectUser>
            // ->b.批量删除项目权限
            List<ProjectUser> projectUserList = SpringContextHolder.getBean(ProjectUserDao.class).findUserNameAndAutomatic(userRoleInDb.getUser().getUsername(), SwitchTypeEnum.AUTO_MATIC.getCode());
            if (CollectionUtils.isNotEmpty(projectUserList)) {
                LOGGER.info(" >>>>>>>>>>find matching data to ProjectUser: <<<<<<<<<<" + projectUserList);
                handleBatchObject(projectUserList, false);
            }

        }

    }

    /**
     * 授权管理员时->所有项目授权
     *
     * @param roleId 角色id
     * @param user   用户对象
     */
    public void addProjectPermission(Long roleId, User user) throws InterruptedException {
        //管理员角色
        Role role = roleDao.findByRoleName(QualitisConstants.ADMIN);
        //判断角色id和用户对象id是否相等
        if (!roleId.toString().equals(role.getId() + "")) {
            return;
        }

        List<Project> projectList = SpringContextHolder.getBean(ProjectDao.class).findAll();
        if (CollectionUtils.isEmpty(projectList)) {
            return;
        }

        handleStockProjectUser(user.getUsername());

        List<ProjectUser> list = Lists.newArrayList();
        for (Project project : projectList) {
            ProjectUser creatorProject = new ProjectUser(ProjectUserPermissionEnum.CREATOR.getCode(), project, user.getUsername(), user.getChineseName(), SwitchTypeEnum.AUTO_MATIC.getCode());
            list.add(creatorProject);
        }

        handleBatchObject(list, true);

    }

    /**
     * 过滤掉自己ProjectUser
     *
     * @param userName 用户对象
     */
    public void handleStockProjectUser(String userName) {
        List<ProjectUser> projects = projectUserDao.findByUserName(userName);
        batchDeleteObject(projects);
    }

    private void handleBatchObject(List<ProjectUser> list, Boolean flag) {

        if (flag && CollectionUtils.isNotEmpty(list)) {
            //每100条数据插入开一个线程
            List<List<ProjectUser>> lists = Lists.partition(list, 100);
            CountDownLatch countDownLatch = new CountDownLatch(lists.size());
            for (List<ProjectUser> listSub : lists) {
                projectUserDao.batchInsert(listSub, countDownLatch);
            }

            try {
                countDownLatch.await(); //保证之前的所有的线程都执行完成，才会走下面的；
                // 这样就可以在下面拿到所有线程执行完的集合结果
            } catch (Exception e) {
                LOGGER.error("阻塞异常:" + e.getMessage());
            }
        } else {
            batchDeleteObject(list);
        }

    }

    private void batchDeleteObject(List<ProjectUser> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            //每100条数据插入开一个线程
            List<List<ProjectUser>> lists = Lists.partition(list, 100);
            CountDownLatch countDownLatch = new CountDownLatch(lists.size());
            for (List<ProjectUser> listSub : lists) {
                projectUserDao.deleteInBatch(listSub, countDownLatch);
            }

            try {
                countDownLatch.await(); //保证之前的所有的线程都执行完成，才会走下面的；
                // 这样就可以在下面拿到所有线程执行完的集合结果
            } catch (Exception e) {
                LOGGER.error("阻塞异常:" + e.getMessage());
            }
        }


    }


}
