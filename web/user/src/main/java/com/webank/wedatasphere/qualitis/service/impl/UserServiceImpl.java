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

import com.webank.wedatasphere.qualitis.dao.DepartmentDao;
import com.webank.wedatasphere.qualitis.dao.RoleDao;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.dao.UserSpecPermissionDao;
import com.webank.wedatasphere.qualitis.entity.Department;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.request.user.ModifyDepartmentRequest;
import com.webank.wedatasphere.qualitis.request.user.ModifyPasswordRequest;
import com.webank.wedatasphere.qualitis.request.user.UserAddRequest;
import com.webank.wedatasphere.qualitis.request.user.UserRequest;
import com.webank.wedatasphere.qualitis.service.UserService;
import com.webank.wedatasphere.qualitis.encoder.Sha256Encoder;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserRole;
import com.webank.wedatasphere.qualitis.entity.UserSpecPermission;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.password.RandomPasswordGenerator;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.response.user.AddUserResponse;
import com.webank.wedatasphere.qualitis.response.user.UserResponse;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import com.webank.wedatasphere.qualitis.util.UuidGenerator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.List;

/**
 * @author howeye
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserSpecPermissionDao userSpecPermissionDao;

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private DepartmentDao departmentDao;

    private HttpServletRequest httpServletRequest;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(@Context HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<AddUserResponse> addUser(UserAddRequest request) throws UnExpectedRequestException {
        // Check Arguments
        checkRequest(request);

        // Check existence of user by username
        String username = request.getUsername();
        User userInDb = userDao.findByUsername(username);
        if (userInDb != null) {
            throw new UnExpectedRequestException("username: " + username + " {&ALREADY_EXIST}");
        }

        // Generate random password and save user
        User newUser = new User();
        String password = RandomPasswordGenerator.generate(16);
        String passwordEncoded = Sha256Encoder.encode(password);
        newUser.setUserName(username);
        newUser.setPassword(passwordEncoded);
        newUser.setChineseName(request.getChineseName());
        // Find department by department name
        Department departmentInDb = departmentDao.findById(request.getDepartmentId());
        if (null == departmentInDb) {
            throw new UnExpectedRequestException("Department ID of " + request.getDepartmentId() + " {&DOES_NOT_EXIST}");
        }
        newUser.setDepartment(departmentInDb);

        User savedUser = userDao.saveUser(newUser);
        AddUserResponse addUserResponse = new AddUserResponse(savedUser, password);
        LOGGER.info("Succeed to create user, response: {}, current_user: {}", addUserResponse, HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&CREATE_USER_SUCCESSFULLY}", addUserResponse);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<?> deleteUser(UserRequest request) throws UnExpectedRequestException {
        // Check Arguments
        checkRequest(request);

        // Check existence of user by id
        Long userId = request.getUserId();
        User userInDb = userDao.findById(userId);
        if (userInDb == null) {
            throw new UnExpectedRequestException("user id {&DOES_NOT_EXIST}, request: " + request);
        }
        // Check personal template.
        checkTemplate(userInDb);
        List<UserRole> userRolesInDb = userRoleDao.findByUser(userInDb);
        if (null != userRolesInDb && !userRolesInDb.isEmpty()) {
            throw new UnExpectedRequestException("{&DELETE_ERROR_USER_ROLE_HAS_FOREIGN_KEY}");
        }
        List<UserSpecPermission> userSpecPermissionsInDb = userSpecPermissionDao.findByUser(userInDb);
        if (null != userSpecPermissionsInDb && !userSpecPermissionsInDb.isEmpty()) {
            throw new UnExpectedRequestException("{&DELETE_ERROR_USER_SPEC_PERMISSION_HAS_FOREIGN_KEY}");
        }

        // Delete user
        userDao.deleteUser(userInDb);
        LOGGER.info("Succeed to delete user, userId: {}, username: {}, current_user: {}", userInDb.getId(), userInDb.getUserName(), HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&DELETE_USER_SUCCESSFULLY}", null);
    }

    private void checkTemplate(User userInDb) throws UnExpectedRequestException {
        if (! userDao.checkTemplate(userInDb)) {
            throw new UnExpectedRequestException("{&USER_HAS_TEMPLATES}");
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, UnExpectedRequestException.class})
    public GeneralResponse<String> initPassword(UserRequest request) throws UnExpectedRequestException {
        // Check Arguments
        checkRequest(request);

        // Check existence of user by id
        Long id = request.getUserId();
        User userInDb = userDao.findById(id);
        if (userInDb == null) {
            throw new UnExpectedRequestException("user id {&DOES_NOT_EXIST}, request: " + request);
        }

        // Generate random password and save user
        String password = RandomPasswordGenerator.generate(16);
        String passwordEncoded = Sha256Encoder.encode(password);
        userInDb.setPassword(passwordEncoded);
        userDao.saveUser(userInDb);

        LOGGER.info("Succeed to init password, user_id: {}, current_user: {}", id, HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&INIT_PASSWORD_SUCCESSFULLY}", password);
    }

    @Override
    public GeneralResponse<GetAllResponse<UserResponse>> findAllUser(PageRequest request) throws UnExpectedRequestException {
        // Check Arguments
        PageRequest.checkRequest(request);

        int page = request.getPage();
        int size = request.getSize();
        List<User> users = userDao.findAllUser(page, size);
        long total = userDao.countAll();
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users) {
            UserResponse tmp = new UserResponse(user);
            userResponses.add(tmp);
        }
        GetAllResponse<UserResponse> response = new GetAllResponse<>();
        response.setTotal(total);
        response.setData(userResponses);

        LOGGER.info("Succeed to find all users, response: {}, current_user: {}", response, HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&FIND_ALL_USERS_SUCCESSFULLY}", response);
    }

    @Override
    public GeneralResponse<?> modifyDepartment(ModifyDepartmentRequest request) throws UnExpectedRequestException {
        // Check Arguments
        checkRequest(request);
        User userInDb = userDao.findById(request.getUserId());
        if (null == userInDb) {
            throw new UnExpectedRequestException("userId {&DOES_NOT_EXIST}");
        }
        // Find department by name.
        Department departmentInDb = departmentDao.findByName(request.getDepartmentName());
        if (null == departmentInDb) {
            throw new UnExpectedRequestException("Department of " + request.getDepartmentName() + " {&DOES_NOT_EXIST}");
        }
        userInDb.setDepartment(departmentInDb);
        // Save user
        userDao.saveUser(userInDb);
        LOGGER.info("Succeed to modify department, userId: {}, current_user: {}", userInDb.getId(), userInDb.getUserName());
        return new GeneralResponse<>("200", "{&MODIFY_DEPARTMENT_SUCCESSFULLY}", null);
    }

    @Override
    public GeneralResponse<?> modifyPassword(ModifyPasswordRequest request) throws UnExpectedRequestException {
        // Check Arguments
        checkRequest(request);

        // Modify if old password is correct
        Long userId = HttpUtils.getUserId(httpServletRequest);
        User userInDb = userDao.findById(userId);
        if (null == userInDb) {
            throw new UnExpectedRequestException("userId {&DOES_NOT_EXIST}");
        }
        String passwordInDb = userInDb.getPassword();
        if (!passwordInDb.equals(request.getOldPassword())) {
            throw new UnExpectedRequestException("{&OLD_PASSWORD_NOT_CORRECT}");
        }
        userInDb.setPassword(request.getNewPassword());

        // Save user
        userDao.saveUser(userInDb);
        LOGGER.info("Succeed to modify password, userId: {}, current_user: {}", userId, HttpUtils.getUserName(httpServletRequest));
        return new GeneralResponse<>("200", "{&MODIFY_PASSWORD_SUCCESSFULLY}", null);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void autoAddUser(String username) throws RoleNotFoundException {
        User newUser = new User();
        String password = username;
        String passwordEncoded = Sha256Encoder.encode(password);
        newUser.setUserName(username);
        newUser.setPassword(passwordEncoded);
        User savedUser = userDao.saveUser(newUser);

        Role role = roleDao.findByRoleName("PROJECTOR");
        if (role == null) {
            throw new RoleNotFoundException();
        }
        UserRole userRole = new UserRole();
        userRole.setId(UuidGenerator.generate());
        userRole.setRole(role);
        userRole.setUser(savedUser);
        userRoleDao.saveUserRole(userRole);
        LOGGER.info("Succeed to save user_role. uuid: {}, user_id: {}, role_id: {}", userRole.getId(), savedUser.getId(), role.getId());
    }

    private void checkRequest(ModifyPasswordRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkString(request.getOldPassword(), "old password");
        checkString(request.getNewPassword(), "new password");
    }

    private void checkRequest(ModifyDepartmentRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        if (request.getUserId() == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkString(request.getUserId().toString(), "user ID");
        checkString(request.getDepartmentName(), "department role id");
    }

    private void checkId(Long id, String idName) throws UnExpectedRequestException {
        if (null == id) {
            throw new UnExpectedRequestException(idName + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }

    private void checkString(String str, String strName) throws UnExpectedRequestException {
        if (StringUtils.isBlank(str)) {
            throw new UnExpectedRequestException(strName + " {&CAN_NOT_BE_NULL_OR_EMPTY}");
        }
    }


    private void checkRequest(UserRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkId(request.getUserId(), "id");
    }

    private void checkRequest(UserAddRequest request) throws UnExpectedRequestException {
        if (request == null) {
            throw new UnExpectedRequestException("{&REQUEST_CAN_NOT_BE_NULL}");
        }
        checkString(request.getUsername(), "username");
        checkString(request.getChineseName(), "ChineseName");
        checkString(request.getDepartmentId().toString(), "Department");
    }
}
