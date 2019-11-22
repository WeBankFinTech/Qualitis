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

import com.webank.wedatasphere.qualitis.dao.RoleDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.request.userrole.AddUserRoleRequest;
import com.webank.wedatasphere.qualitis.request.userrole.DeleteUserRoleRequest;
import com.webank.wedatasphere.qualitis.request.userrole.ModifyUserRoleRequest;
import com.webank.wedatasphere.qualitis.service.UserRoleService;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserRole;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.UserRoleResponse;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import com.webank.wedatasphere.qualitis.dao.RoleDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserRole;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.request.userrole.AddUserRoleRequest;
import com.webank.wedatasphere.qualitis.request.userrole.DeleteUserRoleRequest;
import com.webank.wedatasphere.qualitis.request.userrole.ModifyUserRoleRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.UserRoleResponse;
import com.webank.wedatasphere.qualitis.service.UserRoleService;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.List;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleServiceImpl.class);
    private HttpServletRequest httpServletRequest;

    public UserRoleServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<UserRoleResponse> addUserRole(AddUserRoleRequest request) throws UnExpectedRequestException {
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

        // Save user role
        UserRole newUserRole = new UserRole();
        newUserRole.setRole(roleInDb);
        newUserRole.setUser(userInDb);
        newUserRole.setId(UuidGenerator.generate());
        UserRole savedUserRole = userRoleDao.saveUserRole(newUserRole);
        UserRoleResponse response = new UserRoleResponse(savedUserRole);

        LOGGER.info("Succeed to add user_role: response: {}, current_user: {}", response, HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&ADD_USER_ROLE_SUCCESSFULLY}", response);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> deleteUserRole(DeleteUserRoleRequest request) throws UnExpectedRequestException {
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
        LOGGER.info("Succeed to delete user_role. uuid: {}, current_user: {}", request.getUuid(), HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&DELETE_USER_ROLE_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> modifyUserRole(ModifyUserRoleRequest request) throws UnExpectedRequestException {
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
        UserRole savedUserRole = userRoleDao.saveUserRole(userRoleInDb);

        LOGGER.info("Succeed to modify user_role, uuid: {}, user_id: {}, role_id: {}, current_user: {}", uuid, savedUserRole.getUser().getId(),
                savedUserRole.getRole().getId(), HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&MODIFY_USER_ROLE_SUCCESSFULLY}", null);
    }

    @Override
    public GeneralResponse<GetAllResponse<UserRoleResponse>> findAllUserRole(PageRequest request) throws UnExpectedRequestException {
        // Check Arguments
        PageRequest.checkRequest(request);

        int page = request.getPage();
        int size = request.getSize();
        List<UserRole> userRoles = userRoleDao.findAllUserRole(page, size);
        long total = userRoleDao.countAll();
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


}
